package com.team.invoice.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.team.invoice.dialog.ChangePasswordDialog;
import com.team.invoice.dialog.CreateAccountDialog;

public class MainFrame extends JFrame {
    private SidebarPanel sidebarPanel;
    private ContentPanel contentPanel;
    private HeaderPanel headerPanel;

    public MainFrame(String username, String role) { 
        setTitle("MiniApart - Quản lý chung cư");
        setSize(1440, 1024);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. KHỞI TẠO CÁC PANEL
        headerPanel = new HeaderPanel();
        sidebarPanel = new SidebarPanel();
        contentPanel = new ContentPanel();

        // 2. CẤU HÌNH DỮ LIỆU CHO HEADER
        // Gán trực tiếp 2 biến vừa nhận được từ tham số
        String currentUsername = username; 
        String currentRole = role; 
        headerPanel.setCurrentUser(currentUsername, currentRole);
        

        // 3. GẮN CÁC SỰ KIỆN (ACTION LISTENERS)
        // Sự kiện thu phóng Sidebar
        headerPanel.setToggleAction(e -> sidebarPanel.setVisible(!sidebarPanel.isVisible()));
        
        // Sự kiện chuyển tab menu Sidebar
        sidebarPanel.setMenuAction(contentPanel::showPanel);

        // Sự kiện Đăng xuất
        headerPanel.setLogoutAction(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                MainFrame.this, 
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?", 
                "Xác nhận đăng xuất", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                MainFrame.this.dispose(); // Đóng MainFrame
                
                // Mở lại màn hình Login
                java.awt.EventQueue.invokeLater(() -> {
                    new LoginFrame().setVisible(true); 
                });
            }
        });

        headerPanel.setChangePasswordAction(e -> {
            ChangePasswordDialog dialog = new ChangePasswordDialog(MainFrame.this, currentUsername);
            dialog.setVisible(true);
        });

        // Sự kiện Tạo tài khoản
        headerPanel.setCreateAccountAction(e -> {
            CreateAccountDialog dialog = new CreateAccountDialog(MainFrame.this);
            dialog.setVisible(true);
        });

        // 4. THÊM CÁC PANEL VÀO BỐ CỤC CHÍNH (LAYOUT)
        add(headerPanel, BorderLayout.NORTH);
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    public SidebarPanel getSidebarPanel() {
        return sidebarPanel;
    }

    public ContentPanel getContentPanel() {
        return contentPanel;
    }

    public HeaderPanel getHeaderPanel() {
        return headerPanel;
    }
}