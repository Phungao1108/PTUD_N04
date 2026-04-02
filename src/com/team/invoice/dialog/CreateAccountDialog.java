package com.team.invoice.dialog;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.team.invoice.service.AuthService;

public class CreateAccountDialog extends JDialog {
    private JTextField txtUsername;
    private JPasswordField txtPass;
    private JPasswordField txtConfirmPass;
    private AuthService authService;

    public CreateAccountDialog(Frame owner) {
        super(owner, "Tạo Tài Khoản Quản Lý", true);
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

        formPanel.add(new JLabel("Tên đăng nhập mới (*):"));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Mật khẩu (*):"));
        txtPass = new JPasswordField();
        formPanel.add(txtPass);

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

        JButton btnCreate = new JButton("Tạo tài khoản");
        btnCreate.setBackground(new Color(41, 128, 185));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFocusPainted(false);
        btnCreate.addActionListener(e -> createAccount());

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnCreate);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void createAccount() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPass.getPassword());
        String confirmPass = new String(txtConfirmPass.getPassword());

        if (user.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authService.isUsernameExists(user)) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập này đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo tài khoản (Hàm register của bạn mặc định gán quyền 'MANAGER')
        if (authService.register(user, pass)) {
            JOptionPane.showMessageDialog(this, "Đã tạo tài khoản quản lý thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi tạo tài khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}