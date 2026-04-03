package com.team.invoice.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.team.invoice.components.AppColors;
import com.team.invoice.components.HintTextField;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.entity.DonVi;
import com.team.invoice.entity.LoaiPhong;
import com.team.invoice.entity.Phong;
import com.team.invoice.service.LoaiPhongService;
import com.team.invoice.service.PhongService;
import com.team.invoice.util.FocusUtils;

public class PhongDialog extends JDialog {
    private HintTextField txtMaPhong;
    private HintTextField txtTenPhong;

    private JComboBox<DonVi> cboKhuVuc;
    private JComboBox<DonVi> cboTang;
    private JComboBox<String> cboLoaiPhong;
    private JComboBox<String> cboTrangThai;

    private RoundedButton btnSave;

    private final PhongService phongService;
    private final Runnable onSuccess;
    private final Phong currentPhong;
    private final boolean isEditMode;

    private String originalTenPhong = "";
    private String originalTang = "";
    private String originalLoaiPhong = "";
    private String originalTrangThai = "";

    // prefix hiện tại cho mã phòng, ví dụ "a1."
    private String maPhongPrefix = "";
    // cờ để bypass DocumentFilter khi set prefix từ code
    private boolean isSettingPrefix = false;

    public PhongDialog(Frame owner, PhongService service, Phong phong, Runnable onSuccess) {
        super(owner, true);
        this.phongService = service;
        this.onSuccess = onSuccess;
        this.currentPhong = phong;
        this.isEditMode = (phong != null && phong.getMaPhong() != null && !phong.getMaPhong().trim().isEmpty());

        setTitle(isEditMode ? "Cập Nhật Phòng" : "Thêm Phòng Mới");
        setSize(520, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColors.BG);

        initComponents();
        loadKhuVucToComboBox();
        loadLoaiPhongToComboBox();

        if (isEditMode) {
            fillData();
            saveOriginalData();
            updateSaveButtonState();
        } else {
            btnSave.setEnabled(true);
        }

        addChangeListeners();
        FocusUtils.enableClearFocusOnClick(this.getContentPane());
    }

    private void initComponents() {
        RoundedPanel mainPanel = new RoundedPanel(15, Color.WHITE, AppColors.BORDER);
        mainPanel.setLayout(new GridLayout(6, 2, 10, 16));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        txtMaPhong = new HintTextField("Chọn khu vực và tầng trước");
        txtTenPhong = new HintTextField("VD: Phòng 101");

        cboKhuVuc = new JComboBox<>();
        cboTang = new JComboBox<>();
        cboLoaiPhong = new JComboBox<>();
        cboTrangThai = new JComboBox<>(new String[] { "Trống", "Đang thuê", "Bảo trì" });

        if (isEditMode) {
            txtMaPhong.setEditable(false);
            txtMaPhong.setBackground(AppColors.BG);
            txtMaPhong.setToolTipText("Không thể thay đổi mã phòng");
            cboKhuVuc.setEnabled(false);
            cboTang.setEnabled(false);
        } else {
            // Gắn DocumentFilter để bảo vệ prefix không bị xóa
            ((AbstractDocument) txtMaPhong.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                    if (isSettingPrefix) { super.remove(fb, offset, length); return; }
                    if (offset < maPhongPrefix.length()) return;
                    super.remove(fb, offset, length);
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                        throws BadLocationException {
                    if (isSettingPrefix) { super.replace(fb, offset, length, text, attrs); return; }
                    if (offset < maPhongPrefix.length()) return;
                    super.replace(fb, offset, length, text, attrs);
                }

                @Override
                public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                        throws BadLocationException {
                    if (isSettingPrefix) { super.insertString(fb, offset, string, attr); return; }
                    if (offset < maPhongPrefix.length()) return;
                    super.insertString(fb, offset, string, attr);
                }
            });

            // Chưa chọn tầng -> không cho nhập
            txtMaPhong.setEditable(false);
        }

        mainPanel.add(new JLabel("Khu Vực (*):"));
        mainPanel.add(cboKhuVuc);

        mainPanel.add(new JLabel("Tầng (*):"));
        mainPanel.add(cboTang);

        mainPanel.add(new JLabel("Mã Phòng (*):"));
        mainPanel.add(txtMaPhong);

        mainPanel.add(new JLabel("Tên Phòng (*):"));
        mainPanel.add(txtTenPhong);

        mainPanel.add(new JLabel("Loại Phòng (*):"));
        mainPanel.add(cboLoaiPhong);

        mainPanel.add(new JLabel("Trạng Thái (*):"));
        mainPanel.add(cboTrangThai);

        add(mainPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(AppColors.BG);

        RoundedButton btnCancel = new RoundedButton("Hủy", Color.WHITE, AppColors.TEXT, AppColors.BORDER);
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.addActionListener(e -> dispose());

        btnSave = new RoundedButton("Lưu", AppColors.PRIMARY, Color.WHITE, AppColors.PRIMARY);
        btnSave.setPreferredSize(new Dimension(100, 35));
        btnSave.addActionListener(e -> savePhong());

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnSave);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadKhuVucToComboBox() {
        cboKhuVuc.removeAllItems();
        cboKhuVuc.addItem(new DonVi(null, "-- Chọn khu vực --"));

        List<DonVi> dsKhuVuc = phongService.getAllKhuVuc();
        if (dsKhuVuc != null) {
            for (DonVi kv : dsKhuVuc) {
                cboKhuVuc.addItem(kv);
            }
        }

        if (cboKhuVuc.getItemCount() <= 1) {
            JOptionPane.showMessageDialog(this, "Không có khu vực nào trong hệ thống!");
        }

        // Reset tầng về placeholder, không auto-chọn
        cboTang.removeAllItems();
        cboTang.addItem(new DonVi(null, "-- Chọn tầng --"));

        // Reset mã phòng
        maPhongPrefix = "";
        txtMaPhong.setActualText("");
    }

    private void loadTangByKhuVuc() {
        cboTang.removeAllItems();
        cboTang.addItem(new DonVi(null, "-- Chọn tầng --"));

        DonVi selectedKhuVuc = (DonVi) cboKhuVuc.getSelectedItem();
        if (selectedKhuVuc == null || selectedKhuVuc.getId() == null) {
            // Khu vực chưa chọn -> reset mã phòng
            maPhongPrefix = "";
            txtMaPhong.setActualText("");
            return;
        }

        List<DonVi> dsTang = phongService.getTangByKhuVuc(selectedKhuVuc.getId());
        if (dsTang != null) {
            for (DonVi tang : dsTang) {
                cboTang.addItem(tang);
            }
        }

        // Cập nhật prefix mã phòng sau khi load tầng
        updateMaPhongPrefix();
    }

    private void updateMaPhongPrefix() {
        if (isEditMode) return;

        DonVi selectedTang = (DonVi) cboTang.getSelectedItem();
        if (selectedTang == null || selectedTang.getId() == null) {
            maPhongPrefix = "";
            isSettingPrefix = true;
            txtMaPhong.setActualText("");
            isSettingPrefix = false;
            txtMaPhong.setEditable(false);
            return;
        }

        maPhongPrefix = selectedTang.getId() + ".";

        isSettingPrefix = true;
        txtMaPhong.setActualText(maPhongPrefix);
        isSettingPrefix = false;

        // Cho phép nhập sau khi đã có prefix
        txtMaPhong.setEditable(true);

        // Đặt con trỏ về cuối
        txtMaPhong.setCaretPosition(txtMaPhong.getText().length());
    }

    private void loadLoaiPhongToComboBox() {
        cboLoaiPhong.removeAllItems();

        LoaiPhongService loaiPhongService = new LoaiPhongService();
        List<LoaiPhong> dsLoaiPhong = loaiPhongService.getAllLoaiPhong();

        if (dsLoaiPhong != null) {
            for (LoaiPhong lp : dsLoaiPhong) {
                cboLoaiPhong.addItem(lp.getMaLoaiPhong());
            }
        }

        if (cboLoaiPhong.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có loại phòng nào trong hệ thống!");
        }
    }

    private void fillData() {
        // Chọn đúng khu vực và tầng khi edit
        String idCha = currentPhong.getIdCha(); // id tầng, ví dụ "a1"

        // Load khu vực rồi tìm đúng tầng
        for (int i = 0; i < cboKhuVuc.getItemCount(); i++) {
            DonVi kv = cboKhuVuc.getItemAt(i);
            if (idCha != null && idCha.startsWith(kv.getId())) {
                cboKhuVuc.setSelectedIndex(i);
                loadTangByKhuVuc();
                break;
            }
        }

        for (int i = 0; i < cboTang.getItemCount(); i++) {
            DonVi tang = cboTang.getItemAt(i);
            if (tang.getId().equals(idCha)) {
                cboTang.setSelectedIndex(i);
                break;
            }
        }

        txtMaPhong.setActualText(currentPhong.getMaPhong());
        txtTenPhong.setActualText(currentPhong.getTenPhong());
        cboLoaiPhong.setSelectedItem(currentPhong.getMaLoaiPhong());
        cboTrangThai.setSelectedItem(mapTrangThaiToLabel(currentPhong.getTrangThai()));
    }

    private void saveOriginalData() {
        originalTenPhong = txtTenPhong.getText().trim();
        originalTang = currentPhong.getIdCha();
        originalLoaiPhong = Objects.toString(cboLoaiPhong.getSelectedItem(), "");
        originalTrangThai = Objects.toString(cboTrangThai.getSelectedItem(), "");
    }

    private void addChangeListeners() {
        // Khi đổi khu vực -> load lại tầng
        cboKhuVuc.addActionListener(e -> {
            loadTangByKhuVuc();
            updateSaveButtonState();
        });

        // Khi đổi tầng -> cập nhật prefix mã phòng
        cboTang.addActionListener(e -> {
            updateMaPhongPrefix();
            updateSaveButtonState();
        });

        txtTenPhong.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateSaveButtonState(); }
            @Override public void removeUpdate(DocumentEvent e) { updateSaveButtonState(); }
            @Override public void changedUpdate(DocumentEvent e) { updateSaveButtonState(); }
        });

        cboLoaiPhong.addActionListener(e -> updateSaveButtonState());
        cboTrangThai.addActionListener(e -> updateSaveButtonState());
    }

    private void updateSaveButtonState() {
        if (!isEditMode) {
            btnSave.setEnabled(true);
            return;
        }

        DonVi selectedTang = (DonVi) cboTang.getSelectedItem();
        String currentTangId = selectedTang != null ? selectedTang.getId() : "";

        boolean changed =
            !Objects.equals(txtTenPhong.getText().trim(), originalTenPhong) ||
            !Objects.equals(currentTangId, originalTang) ||
            !Objects.equals(Objects.toString(cboLoaiPhong.getSelectedItem(), ""), originalLoaiPhong) ||
            !Objects.equals(Objects.toString(cboTrangThai.getSelectedItem(), ""), originalTrangThai);

        btnSave.setEnabled(changed);
    }

    private void savePhong() {
        String maPhong = txtMaPhong.getText().trim();
        String tenPhong = txtTenPhong.getText().trim();

        if (maPhong.isEmpty() || maPhong.equals(maPhongPrefix)) {
            JOptionPane.showMessageDialog(this, "Mã phòng không được để trống!");
            return;
        }

        if (tenPhong.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên phòng không được để trống!");
            return;
        }

        if (cboTang.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tầng!");
            return;
        }

        if (cboLoaiPhong.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại phòng!");
            return;
        }

        DonVi selectedTang = (DonVi) cboTang.getSelectedItem();
        String idCha = selectedTang.getId();
        String maLoaiPhong = cboLoaiPhong.getSelectedItem().toString();
        String trangThai = mapLabelToTrangThai(cboTrangThai.getSelectedItem().toString());

        if (!maPhong.startsWith(idCha + ".")) {
            JOptionPane.showMessageDialog(
                this,
                "Mã phòng phải đúng định dạng theo tầng.\nVí dụ: tầng " + idCha + " thì mã phòng phải là " + idCha + ".01"
            );
            return;
        }

        Phong phong = new Phong();
        phong.setMaPhong(maPhong);
        phong.setTenPhong(tenPhong);
        phong.setIdCha(idCha);
        phong.setMaLoaiPhong(maLoaiPhong);
        phong.setTrangThai(trangThai);

        String message;
        if (isEditMode) {
            message = phongService.updatePhong(phong);
        } else {
            message = phongService.createPhong(phong);
        }

        JOptionPane.showMessageDialog(this, message);

        if ("Thành công".equals(message)) {
            if (onSuccess != null) onSuccess.run();
            dispose();
        }
    }

    private String mapTrangThaiToLabel(String value) {
        if (value == null) return "Trống";
        return switch (value) {
            case "TRONG" -> "Trống";
            case "DANG_THUE" -> "Đang thuê";
            case "BAO_TRI" -> "Bảo trì";
            default -> "Trống";
        };
    }

    private String mapLabelToTrangThai(String label) {
        return switch (label) {
            case "Đang thuê" -> "DANG_THUE";
            case "Bảo trì" -> "BAO_TRI";
            default -> "TRONG";
        };
    }
}