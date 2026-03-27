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
import javax.swing.JTextField;
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
    private HintTextField txtKhachHienTai;
    private HintTextField txtGiaThang;
    private HintTextField txtDien;
    private HintTextField txtNuoc;
    private HintTextField txtDichVu;
    private HintTextField txtKyChiSo;

    private JComboBox<String> cboLoaiPhong;
    private JComboBox<String> cboTrangThai;

    private RoundedButton btnSave;

    private final PhongService phongService;
    private final Runnable onSuccess;
    private final Phong currentPhong;
    private final boolean isEditMode;

    // dữ liệu gốc để so sánh thay đổi
    private String originalMaPhong = "";
    private String originalTenPhong = "";
    private String originalLoaiPhong = "";
    private String originalTrangThai = "";
    private String originalKhachHienTai = "";
    private String originalGiaThang = "";
    private String originalDien = "";
    private String originalNuoc = "";
    private String originalDichVu = "";
    private String originalKyChiSo = "";

    public PhongDialog(Frame owner, PhongService service, Phong phong, Runnable onSuccess) {
        super(owner, true);
        this.phongService = service;
        this.onSuccess = onSuccess;
        this.currentPhong = phong;
        this.isEditMode = (phong != null && phong.getMaPhong() != null && !phong.getMaPhong().isEmpty());

        setTitle(isEditMode ? "Cập Nhật Phòng" : "Thêm Phòng Mới");
        setSize(520, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColors.BG);

        initComponents();
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
        mainPanel.setLayout(new GridLayout(10, 2, 10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        txtMaPhong = new HintTextField("VD: P03");
        txtTenPhong = new HintTextField("VD: Phòng 103");
        txtKhachHienTai = new HintTextField("Để trống nếu chưa có khách");
        txtGiaThang = new HintTextField("VD: 2500000");
        txtDien = new HintTextField("VD: 0");
        txtNuoc = new HintTextField("VD: 0");
        txtDichVu = new HintTextField("VD: 100000");
        txtKyChiSo = new HintTextField("VD: 2026-03");

        cboLoaiPhong = new JComboBox<>();
        cboTrangThai = new JComboBox<>(new String[] { "TRỐNG", "ĐÃ THUÊ", "BẢO TRÌ" });

        if (isEditMode) {
            txtMaPhong.setEditable(false);
            txtMaPhong.setBackground(AppColors.BG);
            txtMaPhong.setToolTipText("Không thể thay đổi mã phòng");
        }

        mainPanel.add(new JLabel("Mã Phòng (*):"));
        mainPanel.add(txtMaPhong);

        mainPanel.add(new JLabel("Tên Phòng (*):"));
        mainPanel.add(txtTenPhong);

        mainPanel.add(new JLabel("Loại Phòng (*):"));
        mainPanel.add(cboLoaiPhong);

        mainPanel.add(new JLabel("Trạng Thái (*):"));
        mainPanel.add(cboTrangThai);

        mainPanel.add(new JLabel("Khách Hiện Tại:"));
        mainPanel.add(txtKhachHienTai);

        mainPanel.add(new JLabel("Giá Tháng:"));
        mainPanel.add(txtGiaThang);

        mainPanel.add(new JLabel("Điện:"));
        mainPanel.add(txtDien);

        mainPanel.add(new JLabel("Nước:"));
        mainPanel.add(txtNuoc);

        mainPanel.add(new JLabel("Dịch Vụ:"));
        mainPanel.add(txtDichVu);

        mainPanel.add(new JLabel("Kỳ Chỉ Số:"));
        mainPanel.add(txtKyChiSo);

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

    private void loadLoaiPhongToComboBox() {
        cboLoaiPhong.removeAllItems();

        LoaiPhongService loaiPhongService = new LoaiPhongService();
        List<LoaiPhong> danhSachLoaiPhong = loaiPhongService.getAllLoaiPhong();

        if (danhSachLoaiPhong != null) {
            for (LoaiPhong lp : danhSachLoaiPhong) {
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
        cboLoaiPhong.setSelectedItem(currentPhong.getMaLoaiPhong());
        cboTrangThai.setSelectedItem(currentPhong.getTrangThai());
        txtKhachHienTai.setActualText(currentPhong.getKhachHienTai() == null ? "" : currentPhong.getKhachHienTai());
        txtGiaThang.setActualText(String.valueOf(currentPhong.getGiaThang()));
        txtDien.setActualText(String.valueOf(currentPhong.getDien()));
        txtNuoc.setActualText(String.valueOf(currentPhong.getNuoc()));
        txtDichVu.setActualText(String.valueOf(currentPhong.getDichVu()));
        txtKyChiSo.setActualText(currentPhong.getKyChiSo() == null ? "" : currentPhong.getKyChiSo());
    }

    private void saveOriginalData() {
        originalMaPhong = txtMaPhong.getText().trim();
        originalTenPhong = txtTenPhong.getText().trim();
        originalLoaiPhong = Objects.toString(cboLoaiPhong.getSelectedItem(), "");
        originalTrangThai = Objects.toString(cboTrangThai.getSelectedItem(), "");
        originalKhachHienTai = txtKhachHienTai.getText().trim();
        originalGiaThang = txtGiaThang.getText().trim();
        originalDien = txtDien.getText().trim();
        originalNuoc = txtNuoc.getText().trim();
        originalDichVu = txtDichVu.getText().trim();
        originalKyChiSo = txtKyChiSo.getText().trim();
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
        txtKhachHienTai.getDocument().addDocumentListener(listener);
        txtGiaThang.getDocument().addDocumentListener(listener);
        txtDien.getDocument().addDocumentListener(listener);
        txtNuoc.getDocument().addDocumentListener(listener);
        txtDichVu.getDocument().addDocumentListener(listener);
        txtKyChiSo.getDocument().addDocumentListener(listener);

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
            !Objects.equals(Objects.toString(cboLoaiPhong.getSelectedItem(), ""), originalLoaiPhong) ||
            !Objects.equals(Objects.toString(cboTrangThai.getSelectedItem(), ""), originalTrangThai) ||
            !Objects.equals(txtKhachHienTai.getText().trim(), originalKhachHienTai) ||
            !Objects.equals(txtGiaThang.getText().trim(), originalGiaThang) ||
            !Objects.equals(txtDien.getText().trim(), originalDien) ||
            !Objects.equals(txtNuoc.getText().trim(), originalNuoc) ||
            !Objects.equals(txtDichVu.getText().trim(), originalDichVu) ||
            !Objects.equals(txtKyChiSo.getText().trim(), originalKyChiSo);

        btnSave.setEnabled(changed);
    }

    private void savePhong() {
        String maPhong = txtMaPhong.getText().trim();
        String tenPhong = txtTenPhong.getText().trim();

        if (cboLoaiPhong.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm loại phòng trước!");
            return;
        }

        String maLoaiPhong = cboLoaiPhong.getSelectedItem().toString();
        String trangThai = cboTrangThai.getSelectedItem().toString();
        String khachHienTai = txtKhachHienTai.getText().trim();
        String kyChiSo = txtKyChiSo.getText().trim();

        if (maPhong.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã phòng không được để trống!");
            return;
        }

        if (tenPhong.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên phòng không được để trống!");
            return;
        }

        try {
            double giaThang = parseDoubleOrZero(txtGiaThang.getText().trim());
            double dien = parseDoubleOrZero(txtDien.getText().trim());
            double nuoc = parseDoubleOrZero(txtNuoc.getText().trim());
            double dichVu = parseDoubleOrZero(txtDichVu.getText().trim());

            if ("TRỐNG".equals(trangThai)) {
                khachHienTai = null;
            }

            Phong phong = new Phong();
            phong.setMaPhong(maPhong);
            phong.setTenPhong(tenPhong);
            phong.setMaLoaiPhong(maLoaiPhong);
            phong.setTrangThai(trangThai);
            phong.setKhachHienTai(khachHienTai == null || khachHienTai.isBlank() ? null : khachHienTai);
            phong.setGiaThang(giaThang);
            phong.setDien(dien);
            phong.setNuoc(nuoc);
            phong.setDichVu(dichVu);
            phong.setKyChiSo(kyChiSo);

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
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá trị số không hợp lệ!");
        }
    }

    private double parseDoubleOrZero(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        return Double.parseDouble(value);
    }
}