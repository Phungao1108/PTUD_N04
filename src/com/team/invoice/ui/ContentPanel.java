package com.team.invoice.ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ContentPanel extends JPanel {
    private CardLayout cardLayout;
    private Map<String, JComponent> panelMap = new HashMap<>();

    public ContentPanel() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setBackground(Color.WHITE);

        // Đăng ký các màn hình vào CardLayout với một "Từ khóa" (Key)
        addPanel("Tổng quan", new JLabel("Màn hình Dashboard", SwingConstants.CENTER));
        addPanel("Dịch vụ", new DichvuUI()); 
        addPanel("Đơn giá", new DonGiaUI());
        addPanel("Loại phòng", new LoaiPhongUI());
        addPanel("Bảng giá", new BangGiaUI()); 
        addPanel("Đơn giá", new DonGiaUI());
        addPanel("Hóa đơn", new JLabel("Màn hình Hóa đơn", SwingConstants.CENTER));
        addPanel("Cài đặt", new JLabel("Màn hình Cài đặt", SwingConstants.CENTER));
    }

    public void addPanel(String name, JComponent panel) {
        add(panel, name);
        panelMap.put(name, panel);
    }

    // Hàm này sẽ được gọi khi bạn bấm nút ở Sidebar
    public void showPanel(java.awt.event.ActionEvent e) {
        String name = e.getActionCommand(); // Lấy từ khóa từ nút bấm
        cardLayout.show(this, name);        // Hiển thị Card tương ứng
    }
}