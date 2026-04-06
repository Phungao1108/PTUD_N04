package com.team.invoice.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.team.invoice.service.AuthService;

public class LoginFrame extends JFrame {
    private AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("Đăng nhập Hệ thống - MiniApart");
        setSize(700, 450); // Mở rộng size để làm bố cục chia đôi
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // Khóa size để giữ form luôn đẹp

        // Set Icon cho ứng dụng (Góc trên cùng bên trái của window)
        // Thay "icon_app.png" bằng đường dẫn file icon của bạn
        try {
            ImageIcon appIcon = new ImageIcon(getClass().getResource("/icons/icon_app.png"));
            setIconImage(appIcon.getImage());
        } catch (Exception e) {
            System.out.println("Chưa tìm thấy icon ứng dụng.");
        }

        // Main Panel chia 2 nửa
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Nửa trái: Hình nền và Logo
        mainPanel.add(createLeftPanel(), BorderLayout.WEST);
        
        // Nửa phải: Form Đăng nhập
        mainPanel.add(createRightPanel(), BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createLeftPanel() {
        // Panel bên trái với màu nền chủ đạo (Brand Color)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, 450));
        leftPanel.setBackground(new Color(41, 128, 185)); // Xanh dương đậm chuyên nghiệp

        // Tiêu đề phần mềm
        JLabel lblTitle = new JLabel("<html><center>PHẦN MỀM QUẢN LÝ<br>CHUNG CƯ MINI</center></html>", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(new EmptyBorder(30, 0, 0, 0));
        leftPanel.add(lblTitle, BorderLayout.NORTH);

        // Hình nền minh họa tòa nhà
        // Bạn cần chuẩn bị 1 ảnh PNG trong suốt (kích thước khoảng 200x200) đặt vào thư mục resources
        try {
            ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/building_bg.png"));
            JLabel lblImage = new JLabel(bgIcon);
            leftPanel.add(lblImage, BorderLayout.CENTER);
        } catch (Exception e) {
            // Fallback nếu chưa có ảnh
            JLabel lblPlaceholder = new JLabel("<html><center>[Hình ảnh minh họa<br>Tòa nhà]</center></html>", SwingConstants.CENTER);
            lblPlaceholder.setForeground(new Color(255, 255, 255, 150));
            leftPanel.add(lblPlaceholder, BorderLayout.CENTER);
        }

        // Footer phiên bản
        JLabel lblVersion = new JLabel("Phiên bản 1.0", SwingConstants.CENTER);
        lblVersion.setForeground(new Color(255, 255, 255, 200));
        lblVersion.setBorder(new EmptyBorder(0, 0, 15, 0));
        leftPanel.add(lblVersion, BorderLayout.SOUTH);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20); // Tạo khoảng cách thở cho các component
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề form
        JLabel lblLoginTitle = new JLabel("ĐĂNG NHẬP");
        lblLoginTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblLoginTitle.setForeground(new Color(51, 51, 51));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 20, 30, 20); // Cách top nhiều hơn
        rightPanel.add(lblLoginTitle, gbc);

        // Reset insets cho các field
        gbc.insets = new Insets(5, 20, 5, 20);

        // Tài khoản
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(250, 35)); // Field to, dễ nhìn
        
        gbc.gridy = 1; rightPanel.add(lblUsername, gbc);
        gbc.gridy = 2; rightPanel.add(txtUsername, gbc);

        // Mật khẩu
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPasswordField txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(250, 35));
        
        gbc.gridy = 3; rightPanel.add(lblPassword, gbc);
        gbc.gridy = 4; rightPanel.add(txtPassword, gbc);

        // Nút Đăng nhập
        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(41, 128, 185)); // Trùng màu left panel
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(250, 40));

        // Label báo lỗi
        JLabel lblMessage = new JLabel(" ", SwingConstants.CENTER);
        lblMessage.setForeground(Color.RED);
        lblMessage.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        // Xử lý sự kiện click
        btnLogin.addActionListener(e -> {
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword());
            
            if(user.isEmpty() || pass.isEmpty()) {
                lblMessage.setText("Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            // Gọi logic Auth
            if (authService.login(user, pass)) {
                
                // ---- ĐOẠN CẦN SỬA ĐÂY ----
                // Truyền tên đăng nhập thật (user) và quyền ("OWNER" hoặc "MANAGER") vào MainFrame
                new MainFrame(user, "OWNER").setVisible(true); 
                
                this.dispose();
            } else {
                lblMessage.setText("Tài khoản hoặc mật khẩu không chính xác!");
            }
        });

        // Hỗ trợ nhấn Enter để đăng nhập
        getRootPane().setDefaultButton(btnLogin);

        gbc.gridy = 5;
        gbc.insets = new Insets(20, 20, 5, 20); // Cách xa field mật khẩu một chút
        rightPanel.add(btnLogin, gbc);
        
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 20, 5, 20);
        rightPanel.add(lblMessage, gbc);

        // Thêm một dòng ghi chú thay thế cho chức năng đăng ký
        JLabel lblHelp = new JLabel("Liên hệ Admin nếu quên mật khẩu", SwingConstants.CENTER);
        lblHelp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHelp.setForeground(Color.GRAY);
        gbc.gridy = 7;
        gbc.insets = new Insets(30, 20, 10, 20);
        rightPanel.add(lblHelp, gbc);

        return rightPanel;
    }
}