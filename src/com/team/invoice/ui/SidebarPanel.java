package com.team.invoice.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.team.invoice.components.AppColors;

public class SidebarPanel extends JPanel {
    private ActionListener menuListener;
    private List<JButton> menuButtons = new ArrayList<>();

    public SidebarPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 0));
        setBackground(AppColors.TEXT); // Màu nền tối cho Sidebar
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppColors.BORDER));

        // Logo / Title
        JLabel lblLogo = new JLabel("MINI APART", SwingConstants.CENTER);
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setBorder(new EmptyBorder(30, 0, 30, 0));
        add(lblLogo, BorderLayout.NORTH);

        // Menu items
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(AppColors.TEXT);
        menuContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] menuItems = {"Tổng quan", "Loại phòng", "Phòng", "Dịch vụ", "Bảng giá", "Đơn giá", "Hóa đơn", "Cài đặt"};
        // Tạo các nút bấm (Tên hiển thị phải khớp với Key trong ContentPanel)

        
        for (String item : menuItems) {
            JButton btn = createMenuButton(item);
            menuContainer.add(btn);
            menuContainer.add(Box.createRigidArea(new Dimension(0, 10))); // Khoảng cách giữa các nút
            menuButtons.add(btn);
        }

        add(menuContainer, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setForeground(AppColors.MUTED);
        btn.setBackground(AppColors.TEXT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Set ActionCommand cực kỳ quan trọng để ContentPanel nhận diện
        btn.setActionCommand(text); 

        btn.addActionListener(e -> {
            // Đổi màu nút đang được chọn
            for (JButton b : menuButtons) {
                b.setForeground(AppColors.MUTED);
            }
            btn.setForeground(Color.WHITE);

            // Bắn sự kiện sang cho MainFrame -> ContentPanel
            if (menuListener != null) {
                menuListener.actionPerformed(e);
            }
        });

        return btn;
    }

    // MainFrame sẽ gọi hàm này để kết nối Sidebar với ContentPanel
    public void setMenuAction(ActionListener listener) {
        this.menuListener = listener;
    }
}