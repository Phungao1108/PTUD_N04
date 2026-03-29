package com.team.invoice.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.Objects;

import javax.swing.BorderFactory;
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
import com.team.invoice.service.LoaiPhongService;
import com.team.invoice.util.FocusUtils;

public class LoaiPhongDialog extends JDialog {
    private HintTextField txtMaLoaiPhong;
    private HintTextField txtTenLoaiPhong;
    private HintTextField txtDienTich;
    private HintTextField txtMoTa;

    private RoundedButton btnSave;

    private final LoaiPhongService loaiPhongService;
    private final LoaiPhong currentLoaiPhong;
    private final boolean isEditMode;
    private final Runnable onSuccess;

    // dữ liệu gốc để kiểm tra thay đổi
    private String originalMaLoaiPhong = "";
    private String originalTenLoaiPhong = "";
    private String originalDienTich = "";
    private String originalMoTa = "";

    public LoaiPhongDialog(Frame owner, LoaiPhongService service, LoaiPhong lp, Runnable onSuccess) {
        super(owner, true);
        this.loaiPhongService = service;
        this.currentLoaiPhong = lp;
        this.onSuccess = onSuccess;
        this.isEditMode = (lp != null && lp.getMaLoaiPhong() != null && !lp.getMaLoaiPhong().isEmpty());

        setTitle(isEditMode ? "Cập nhật Loại Phòng" : "Thêm Loại Phòng Mới");
        setSize(450, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColors.BG);

        initComponents();

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
        mainPanel.setLayout(new GridLayout(4, 2, 10, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        txtMaLoaiPhong = new HintTextField("Mã loại (VD: LP01)...");
        txtTenLoaiPhong = new HintTextField("Tên loại (VD: Phòng VIP)...");
        txtDienTich = new HintTextField("Diện tích (m2)...");
        txtMoTa = new HintTextField("Mô tả ngắn gọn...");

        if (isEditMode) {
            txtMaLoaiPhong.setEditable(false);
            txtMaLoaiPhong.setBackground(AppColors.BG);
            txtMaLoaiPhong.setToolTipText("Không thể thay đổi mã loại phòng");
        }

        mainPanel.add(new JLabel("Mã Loại Phòng (*):"));
        mainPanel.add(txtMaLoaiPhong);
        mainPanel.add(new JLabel("Tên Loại Phòng (*):"));
        mainPanel.add(txtTenLoaiPhong);
        mainPanel.add(new JLabel("Diện Tích Chuẩn (m2):"));
        mainPanel.add(txtDienTich);
        mainPanel.add(new JLabel("Mô Tả:"));
        mainPanel.add(txtMoTa);

        add(mainPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(AppColors.BG);

        RoundedButton btnCancel = new RoundedButton("Hủy", Color.WHITE, AppColors.TEXT, AppColors.BORDER);
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.addActionListener(e -> dispose());

        btnSave = new RoundedButton("Lưu", AppColors.PRIMARY, Color.WHITE, AppColors.PRIMARY);
        btnSave.setPreferredSize(new Dimension(100, 35));
        btnSave.addActionListener(e -> saveLoaiPhong());

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnSave);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void fillData() {
        txtMaLoaiPhong.setActualText(currentLoaiPhong.getMaLoaiPhong());
        txtTenLoaiPhong.setActualText(currentLoaiPhong.getTenLoaiPhong());
        txtDienTich.setActualText(String.valueOf(currentLoaiPhong.getDienTichChuan()));
        txtMoTa.setActualText(currentLoaiPhong.getMoTa() == null ? "" : currentLoaiPhong.getMoTa());
    }

    private void saveOriginalData() {
        originalMaLoaiPhong = txtMaLoaiPhong.getText().trim();
        originalTenLoaiPhong = txtTenLoaiPhong.getText().trim();
        originalDienTich = txtDienTich.getText().trim();
        originalMoTa = txtMoTa.getText().trim();
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

        txtTenLoaiPhong.getDocument().addDocumentListener(listener);
        txtDienTich.getDocument().addDocumentListener(listener);
        txtMoTa.getDocument().addDocumentListener(listener);
    }

    private void updateSaveButtonState() {
        if (!isEditMode) {
            btnSave.setEnabled(true);
            return;
        }

        boolean changed =
            !Objects.equals(txtMaLoaiPhong.getText().trim(), originalMaLoaiPhong) ||
            !Objects.equals(txtTenLoaiPhong.getText().trim(), originalTenLoaiPhong) ||
            !Objects.equals(txtDienTich.getText().trim(), originalDienTich) ||
            !Objects.equals(txtMoTa.getText().trim(), originalMoTa);

        btnSave.setEnabled(changed);
    }

    private void saveLoaiPhong() {
        LoaiPhong lp = (currentLoaiPhong == null) ? new LoaiPhong() : currentLoaiPhong;

        lp.setMaLoaiPhong(txtMaLoaiPhong.getText().trim());
        lp.setTenLoaiPhong(txtTenLoaiPhong.getText().trim());
        lp.setMoTa(txtMoTa.getText().trim());

        if (lp.getMaLoaiPhong().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã loại phòng không được để trống!");
            return;
        }

        if (lp.getTenLoaiPhong().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên loại phòng không được để trống!");
            return;
        }

        try {
            double dienTich = Double.parseDouble(txtDienTich.getText().trim());
            if (dienTich <= 0) {
                JOptionPane.showMessageDialog(this, "Diện tích phải lớn hơn 0!");
                return;
            }
            lp.setDienTichChuan(dienTich);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Diện tích phải là một số hợp lệ!");
            return;
        }

        String message;
        if (isEditMode) {
            message = loaiPhongService.updateLoaiPhong(lp);
        } else {
            message = loaiPhongService.createLoaiPhong(lp);
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