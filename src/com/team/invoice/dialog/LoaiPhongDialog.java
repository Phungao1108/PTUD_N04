package com.team.invoice.dialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.team.invoice.components.AppColors;
import com.team.invoice.components.HintTextField;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.entity.LoaiPhong;
import com.team.invoice.service.LoaiPhongService;

public class LoaiPhongDialog extends JDialog {
    private JTextField txtMaLoaiPhong;
    private JTextField txtTenLoaiPhong;
    private JTextField txtDienTich;
    private JTextField txtMoTa;
    
    private LoaiPhongService loaiPhongService;
    private LoaiPhong currentLoaiPhong;
    private boolean isEditMode;
    private Runnable onSuccess;

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
        }
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

        mainPanel.add(new JLabel("Mã Loại Phòng (*):")); mainPanel.add(txtMaLoaiPhong);
        mainPanel.add(new JLabel("Tên Loại Phòng (*):")); mainPanel.add(txtTenLoaiPhong);
        mainPanel.add(new JLabel("Diện Tích Chuẩn (m2):")); mainPanel.add(txtDienTich);
        mainPanel.add(new JLabel("Mô Tả:")); mainPanel.add(txtMoTa);

        add(mainPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(AppColors.BG);

        RoundedButton btnCancel = new RoundedButton("Hủy", Color.WHITE, AppColors.TEXT, AppColors.BORDER);
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.addActionListener(e -> dispose());

        RoundedButton btnSave = new RoundedButton("Lưu", AppColors.PRIMARY, Color.WHITE, AppColors.PRIMARY);
        btnSave.setPreferredSize(new Dimension(100, 35));
        btnSave.addActionListener(e -> saveLoaiPhong());

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnSave);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void fillData() {
        txtMaLoaiPhong.setText(currentLoaiPhong.getMaLoaiPhong());
        txtTenLoaiPhong.setText(currentLoaiPhong.getTenLoaiPhong());
        txtDienTich.setText(String.valueOf(currentLoaiPhong.getDienTichChuan()));
        txtMoTa.setText(currentLoaiPhong.getMoTa());
    }

    private void saveLoaiPhong() {
        if (currentLoaiPhong == null) {
            currentLoaiPhong = new LoaiPhong();
        }
        
        currentLoaiPhong.setMaLoaiPhong(txtMaLoaiPhong.getText().trim());
        currentLoaiPhong.setTenLoaiPhong(txtTenLoaiPhong.getText().trim());
        currentLoaiPhong.setMoTa(txtMoTa.getText().trim());
        
        // Validate dữ liệu số cho diện tích
        try {
            double dienTich = Double.parseDouble(txtDienTich.getText().trim());
            if(dienTich <= 0) {
                JOptionPane.showMessageDialog(this, "Diện tích phải lớn hơn 0!");
                return;
            }
            currentLoaiPhong.setDienTichChuan(dienTich);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Diện tích phải là một số hợp lệ!");
            return;
        }

        String message;
        if (isEditMode) {
            message = loaiPhongService.updateLoaiPhong(currentLoaiPhong);
        } else {
            message = loaiPhongService.createLoaiPhong(currentLoaiPhong);
        }

        JOptionPane.showMessageDialog(this, message);
        
        if (message.equals("Thành công")) {
            if (onSuccess != null) {
                onSuccess.run(); 
            }
            dispose(); 
        }
    }
}