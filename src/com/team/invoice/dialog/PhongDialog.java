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

import com.team.invoice.components.AppColors;
import com.team.invoice.components.HintTextField;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.entity.LoaiPhong;
import com.team.invoice.entity.Phong;
import com.team.invoice.service.LoaiPhongService;
import com.team.invoice.service.PhongService;
import com.team.invoice.util.FocusUtils;

public class PhongDialog extends JDialog {
    private HintTextField txtMaPhong;
    private HintTextField txtTenPhong;

    private JComboBox<String> cboTang;
    private JComboBox<String> cboLoaiPhong;
    private JComboBox<String> cboTrangThai;

    private RoundedButton btnSave;

    private final PhongService phongService;
    private final Runnable onSuccess;
    private final Phong currentPhong;
    private final boolean isEditMode;

    private String originalMaPhong = "";
    private String originalTenPhong = "";
    private String originalTang = "";
    private String originalLoaiPhong = "";
    private String originalTrangThai = "";

    public PhongDialog(Frame owner, PhongService service, Phong phong, Runnable onSuccess) {
        super(owner, true);
        this.phongService = service;
        this.onSuccess = onSuccess;
        this.currentPhong = phong;
        this.isEditMode = (phong != null && phong.getMaPhong() != null && !phong.getMaPhong().trim().isEmpty());

        setTitle(isEditMode ? "Cập Nhật Phòng" : "Thêm Phòng Mới");
        setSize(520, 360);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColors.BG);

        initComponents();
        loadTangToComboBox();
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
        mainPanel.setLayout(new GridLayout(5, 2, 10, 16));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        txtMaPhong = new HintTextField("VD: a1.01");
        txtTenPhong = new HintTextField("VD: Phòng 101");

        cboTang = new JComboBox<>();
        cboLoaiPhong = new JComboBox<>();
        cboTrangThai = new JComboBox<>(new String[] { "TRONG", "ĐÃ THUÊ", "BẢO TRÌ" });

        if (isEditMode) {
            txtMaPhong.setEditable(false);
            txtMaPhong.setBackground(AppColors.BG);
            txtMaPhong.setToolTipText("Không thể thay đổi mã phòng");
        }

        mainPanel.add(new JLabel("Mã Phòng (*):"));
        mainPanel.add(txtMaPhong);

        mainPanel.add(new JLabel("Tên Phòng (*):"));
        mainPanel.add(txtTenPhong);

        mainPanel.add(new JLabel("Tầng (*):"));
        mainPanel.add(cboTang);

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

    private void loadTangToComboBox() {
        cboTang.removeAllItems();

        List<String> dsTang = phongService.getAllTangIds();
        if (dsTang != null) {
            for (String tangId : dsTang) {
                cboTang.addItem(tangId);
            }
        }

        if (cboTang.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có tầng nào trong hệ thống!");
        }
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
        txtMaPhong.setActualText(currentPhong.getMaPhong());
        txtTenPhong.setActualText(currentPhong.getTenPhong());
        cboTang.setSelectedItem(currentPhong.getIdCha());
        cboLoaiPhong.setSelectedItem(currentPhong.getMaLoaiPhong());
        cboTrangThai.setSelectedItem(currentPhong.getTrangThai());
    }

    private void saveOriginalData() {
        originalMaPhong = txtMaPhong.getText().trim();
        originalTenPhong = txtTenPhong.getText().trim();
        originalTang = Objects.toString(cboTang.getSelectedItem(), "");
        originalLoaiPhong = Objects.toString(cboLoaiPhong.getSelectedItem(), "");
        originalTrangThai = Objects.toString(cboTrangThai.getSelectedItem(), "");
    }

    private void addChangeListeners() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSaveButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSaveButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSaveButtonState();
            }
        };

        txtTenPhong.getDocument().addDocumentListener(listener);

        cboTang.addActionListener(e -> updateSaveButtonState());
        cboLoaiPhong.addActionListener(e -> updateSaveButtonState());
        cboTrangThai.addActionListener(e -> updateSaveButtonState());
    }

    private void updateSaveButtonState() {
        if (!isEditMode) {
            btnSave.setEnabled(true);
            return;
        }

        boolean changed =
            !Objects.equals(txtMaPhong.getText().trim(), originalMaPhong) ||
            !Objects.equals(txtTenPhong.getText().trim(), originalTenPhong) ||
            !Objects.equals(Objects.toString(cboTang.getSelectedItem(), ""), originalTang) ||
            !Objects.equals(Objects.toString(cboLoaiPhong.getSelectedItem(), ""), originalLoaiPhong) ||
            !Objects.equals(Objects.toString(cboTrangThai.getSelectedItem(), ""), originalTrangThai);

        btnSave.setEnabled(changed);
    }

    private void savePhong() {
        String maPhong = txtMaPhong.getText().trim();
        String tenPhong = txtTenPhong.getText().trim();

        if (maPhong.isEmpty()) {
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

        if (cboTrangThai.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn trạng thái!");
            return;
        }

        String idCha = cboTang.getSelectedItem().toString();
        String maLoaiPhong = cboLoaiPhong.getSelectedItem().toString();
        String trangThai = cboTrangThai.getSelectedItem().toString();

        // kiểm tra format mã phòng theo trigger: phải bắt đầu bằng mã tầng + "."
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
            if (onSuccess != null) {
                onSuccess.run();
            }
            dispose();
        }
    }
}