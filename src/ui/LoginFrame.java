package ui;

import javax.swing.*;
import java.awt.*;
import ui.MainFrame;
public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Đăng nhập - MiniApart");
        setSize(1440, 1024);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Đăng nhập MiniApart", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(new Color(88, 86, 214)); // Màu tím chủ đạo

        JLabel lblUsername = new JLabel("Tên đăng nhập / Email:");
        lblUsername.setFont(new Font("SansSerif", Font.BOLD, 14));
        JTextField txtUsername = new JTextField(25);
        txtUsername.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtUsername.setPreferredSize(new Dimension(0, 40));
        
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("SansSerif", Font.BOLD, 14));
        JPasswordField txtPassword = new JPasswordField(25);
        txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtPassword.setPreferredSize(new Dimension(0, 40));

        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setBackground(new Color(88, 86, 214));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnLogin.setPreferredSize(new Dimension(0, 45));
        btnLogin.setFocusPainted(false); // Bỏ viền chấm chấm khi click
        
        // Sự kiện click
        btnLogin.addActionListener(e -> {
            new MainFrame().setVisible(true);
            this.dispose();
        });

        gbc.gridx = 0; 
        gbc.gridy = 0; gbc.insets = new Insets(15, 15, 40, 15); panel.add(titleLabel, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(5, 15, 5, 15); panel.add(lblUsername, gbc);
        gbc.gridy = 2; panel.add(txtUsername, gbc);
        gbc.gridy = 3; gbc.insets = new Insets(15, 15, 5, 15); panel.add(lblPassword, gbc);
        gbc.gridy = 4; gbc.insets = new Insets(5, 15, 5, 15); panel.add(txtPassword, gbc);
        gbc.gridy = 5; gbc.insets = new Insets(30, 15, 15, 15); panel.add(btnLogin, gbc);

        add(panel);
    }
}