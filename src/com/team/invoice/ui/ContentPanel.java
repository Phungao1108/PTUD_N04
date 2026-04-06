package com.team.invoice.ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ContentPanel extends JPanel {
    private final CardLayout cardLayout;
    private final Map<String, JComponent> panelMap = new HashMap<>();

    public ContentPanel() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setBackground(Color.WHITE);

        addPanel("Tổng quan", new JLabel("Màn hình Dashboard", SwingConstants.CENTER));
        addPanel("Loại phòng", new LoaiPhongUI());
        addPanel("Phòng", new PhongUI());
        addPanel("Dịch vụ", new DichvuUI());
        addPanel("Bảng giá", new BangGiaUI());
        addPanel("Hóa đơn", new HoaDonUI());
        addPanel("Hợp đồng", new HopDongUI());
        addPanel("Khách thuê", new KhachThueUI());
        addPanel("Cài đặt", new JLabel("Màn hình Cài đặt", SwingConstants.CENTER));
    }

    public void addPanel(String name, JComponent panel) {
        add(panel, name);
        panelMap.put(name, panel);
    }

    public void showPanel(ActionEvent e) {
        String name = e.getActionCommand();
        cardLayout.show(this, name);
    }

    public void showPanelByName(String name) {
        cardLayout.show(this, name);
    }

    public JComponent getPanel(String name) {
        return panelMap.get(name);
    }
}
