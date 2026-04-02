package com.team.invoice.dialog;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.team.invoice.service.AuthService;

public class ChangePasswordDialog extends JDialog {
    private JPasswordField txtOldPass;
    private JPasswordField txtNewPass;
    private JPasswordField txtConfirmPass;
    private AuthService authService;
    private String currentUsername;

    public ChangePasswordDialog(Frame owner, String username) {
        super(owner, "Đổi mật khẩu", true);
        this.currentUsername = username;
        this.authService = new AuthService();

        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        initComponents();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Mật khẩu hiện tại (*):"));
        txtOldPass = new JPasswordField();
        formPanel.add(txtOldPass);

        formPanel.add(new JLabel("Mật khẩu mới (*):"));
        txtNewPass = new JPasswordField();
        formPanel.add(txtNewPass);

        formPanel.add(new JLabel("Xác nhận mật khẩu (*):"));
        txtConfirmPass = new JPasswordField();
        formPanel.add(txtConfirmPass);

        add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(0, 20, 15, 20));

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = new JButton("Lưu thay đổi");
        btnSave.setBackground(new Color(41, 128, 185));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> changePassword());

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnSave);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void changePassword() {
        String oldPass = new String(txtOldPass.getPassword());
        String newPass = new String(txtNewPass.getPassword());
        String confirmPass = new String(txtConfirmPass.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra mật khẩu cũ có đúng không (tái sử dụng hàm login)
        if (!authService.login(currentUsername, oldPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không chính xác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cập nhật mật khẩu mới
        if (authService.resetPassword(currentUsername, newPass)) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!\nVui lòng ghi nhớ mật khẩu mới.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}