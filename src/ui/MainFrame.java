package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame() {
        setTitle("MiniApart - Quản lý chung cư");
        setSize(1440, 1024);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initHeader();
        initSidebar();
        initContentArea();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 100)); 
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 30));
        leftHeader.setBackground(Color.WHITE);
        
        JButton btnToggle = new JButton("☰");
        btnToggle.setFont(new Font("SansSerif", Font.BOLD, 20));
        btnToggle.setFocusPainted(false);
        btnToggle.setBackground(Color.WHITE);
        btnToggle.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        btnToggle.setPreferredSize(new Dimension(45, 40));
        btnToggle.addActionListener(e -> {
            sidebarPanel.setVisible(!sidebarPanel.isVisible());
        });

        // Thay ô tìm kiếm bằng Label + Textfield
        JPanel searchBox = new JPanel(new BorderLayout());
        searchBox.setBackground(Color.WHITE);
        searchBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JTextField txtSearch = new JTextField(30);
        txtSearch.setBorder(new EmptyBorder(5, 10, 5, 10)); // Giả làm padding
        txtSearch.setPreferredSize(new Dimension(300, 38));
        searchBox.add(txtSearch, BorderLayout.CENTER);

        leftHeader.add(btnToggle);
        leftHeader.add(searchBox);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 30));
        rightHeader.setBackground(Color.WHITE);
        JLabel userInfo = new JLabel("<html><div style='text-align: right;'><b>Admin</b><br><span style='color:gray;'>Quản lý</span></div></html>");
        rightHeader.add(userInfo);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void initSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(300, 0));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));

        JLabel logo = new JLabel("MiniApart");
        logo.setFont(new Font("SansSerif", Font.BOLD, 28));
        logo.setForeground(new Color(88, 86, 214));
        logo.setBorder(new EmptyBorder(30, 30, 40, 30));
        sidebarPanel.add(logo);

        String[] menuItems = {"Tổng quan", "Phòng", "Khách thuê", "Hóa đơn", "Bảo trì", "Cài đặt"};
        
        for (String item : menuItems) {
            JButton menuBtn = new JButton("  " + item);
            menuBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            menuBtn.setMaximumSize(new Dimension(300, 50));
            menuBtn.setBackground(Color.WHITE);
            menuBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
            menuBtn.setForeground(new Color(80, 80, 80));
            menuBtn.setBorder(new EmptyBorder(0, 30, 0, 0));
            menuBtn.setFocusPainted(false);
            menuBtn.setHorizontalAlignment(SwingConstants.LEFT);
            
            
            menuBtn.addActionListener(e -> {
                cardLayout.show(contentPanel, item);
            });
            
            sidebarPanel.add(menuBtn);
        }

        add(sidebarPanel, BorderLayout.WEST);
    }

    private void initContentArea() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Đăng ký các màn hình tương ứng với tên Menu
        contentPanel.add(new DashboardPanel(), "Tổng quan");
        // Thêm màn hình Khách thuê (Code ở bên dưới)
        contentPanel.add(new PhongPanel(), "Phòng");
        contentPanel.add(new KhachThuePanel(),"Khách thuê");
        
        // Tạo sẵn label tạm cho các menu chưa code
        contentPanel.add(new JLabel("Màn hình Phòng", SwingConstants.CENTER), "Phòng");
        contentPanel.add(new JLabel("			 hình Hóa đơn", SwingConstants.CENTER), "Hóa đơn");
        
        add(contentPanel, BorderLayout.CENTER);
    }
}