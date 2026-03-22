package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250)); // Màu nền xám cực nhạt
        setBorder(new EmptyBorder(30, 30, 30, 30));

        initHeader();
        initContent();
    }

    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 246, 250));
        
        JLabel title = new JLabel("<html><h1 style='margin:0; font-family:sans-serif;'>Tổng quan hệ thống</h1><span style='color:gray; font-family:sans-serif;'>Cập nhật nhanh tình trạng chung cư</span></html>");
        
        JButton btnNewInvoice = new JButton("Lập hóa đơn mới");
        btnNewInvoice.setBackground(new Color(88, 86, 214));
        btnNewInvoice.setForeground(Color.WHITE);
        btnNewInvoice.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnNewInvoice.setPreferredSize(new Dimension(160, 40));
        btnNewInvoice.setFocusPainted(false);
        
        header.add(title, BorderLayout.WEST);
        header.add(btnNewInvoice, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void initContent() {
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setBackground(new Color(245, 246, 250));
        mainContent.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setBackground(new Color(245, 246, 250));
        cardsPanel.add(createStatCard("Tổng số phòng", "42/45", "Đang cho thuê"));
        cardsPanel.add(createStatCard("Khách đang ở", "86", "+2 khách tháng này"));
        cardsPanel.add(createStatCard("Doanh thu dự kiến", "145.5M", "Tháng 10, 2023"));
        cardsPanel.add(createStatCard("Sự cố chờ xử lý", "5", "2 sự cố nghiêm trọng"));

        JPanel bottomArea = new JPanel(new BorderLayout(20, 0));
        bottomArea.setBackground(new Color(245, 246, 250));
        
        JPanel chartDummy = new JPanel(new BorderLayout());
        chartDummy.setBackground(Color.WHITE);
        chartDummy.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        chartDummy.add(new JLabel("<html><h3 style='font-family:sans-serif;'>Biểu đồ doanh thu</h3></html>"), BorderLayout.NORTH);
        
        JPanel issueDummy = new JPanel(new BorderLayout());
        issueDummy.setBackground(Color.WHITE);
        issueDummy.setPreferredSize(new Dimension(350, 0));
        issueDummy.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        issueDummy.add(new JLabel("<html><h3 style='font-family:sans-serif;'>Sự cố chờ xử lý</h3></html>"), BorderLayout.NORTH);

        bottomArea.add(chartDummy, BorderLayout.CENTER);
        bottomArea.add(issueDummy, BorderLayout.EAST);

        mainContent.add(cardsPanel, BorderLayout.NORTH);
        mainContent.add(bottomArea, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value, String subText) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 28));
        
        JLabel lblSub = new JLabel(subText);
        lblSub.setForeground(Color.GRAY);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));

        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblValue);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(lblSub);

        return card;
    }
}