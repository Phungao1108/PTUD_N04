package com.team.invoice.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HeaderPanel extends JPanel {
    private JButton btnToggle;
    private JLabel userInfo;
    
    // Khai báo các thành phần của Popup Menu
    private JPopupMenu profileMenu;
    private JMenuItem menuChangePass;
    private JMenuItem menuCreateAccount;
    private JMenuItem menuLogout;

    public HeaderPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 100));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // --- LEFT HEADER ---
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 30));
        leftHeader.setBackground(Color.WHITE);

        btnToggle = new JButton("☰");
        btnToggle.setFont(new Font("SansSerif", Font.BOLD, 20));
        btnToggle.setFocusPainted(false);
        btnToggle.setBackground(Color.WHITE);
        btnToggle.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        btnToggle.setPreferredSize(new Dimension(45, 40));
        btnToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel searchBox = new JPanel(new BorderLayout());
        searchBox.setBackground(Color.WHITE);
        searchBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JTextField txtSearch = new JTextField(30);
        txtSearch.setBorder(new EmptyBorder(5, 10, 5, 10));
        txtSearch.setPreferredSize(new Dimension(300, 38));
        searchBox.add(txtSearch, BorderLayout.CENTER);

        leftHeader.add(btnToggle);
        leftHeader.add(searchBox);

        // --- RIGHT HEADER (User Profile Area) ---
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 30));
        rightHeader.setBackground(Color.WHITE);
        
        // Tạo nhãn hiển thị thông tin, biến nó thành có thể click được
        userInfo = new JLabel("<html><div style='text-align: right;'><b>Đang tải...</b><br><span style='color:gray;'>...</span></div></html>");
        userInfo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userInfo.setToolTipText("Nhấn để xem tùy chọn tài khoản");

        // Khởi tạo Popup Menu và các Items
        setupProfileMenu();

        // Thêm sự kiện click chuột vào tên user để xổ menu xuống
        userInfo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Hiển thị menu ngay bên dưới nhãn userInfo
                profileMenu.show(userInfo, userInfo.getWidth() - profileMenu.getPreferredSize().width, userInfo.getHeight());
            }
        });

        rightHeader.add(userInfo);

        add(leftHeader, BorderLayout.WEST);
        add(rightHeader, BorderLayout.EAST);
    }

    private void setupProfileMenu() {
        profileMenu = new JPopupMenu();
        
        // Font cho menu nhìn hiện đại hơn
        Font menuFont = new Font("Segoe UI", Font.PLAIN, 14);

        menuChangePass = new JMenuItem("Đổi mật khẩu");
        menuChangePass.setFont(menuFont);
        
        menuCreateAccount = new JMenuItem("Tạo tài khoản quản lý");
        menuCreateAccount.setFont(menuFont);
        
        menuLogout = new JMenuItem("Đăng xuất");
        menuLogout.setFont(menuFont);
        menuLogout.setForeground(Color.RED); // Làm nổi bật nút đăng xuất

        // Thêm vào menu
        profileMenu.add(menuChangePass);
        profileMenu.add(menuCreateAccount);
        profileMenu.addSeparator(); // Dòng kẻ ngang phân cách
        profileMenu.add(menuLogout);
    }

    // --- CÁC HÀM PUBLIC ĐỂ MAIN FRAME GỌI ---

    /**
     * Hàm này dùng để cập nhật tên và vai trò hiển thị trên Header.
     * Cần gọi hàm này ngay sau khi đăng nhập thành công.
     */
    public void setCurrentUser(String username, String role) {
        // Cập nhật text hiển thị
        String displayRole = role.equalsIgnoreCase("OWNER") ? "Chủ sở hữu" : "Quản lý";
        userInfo.setText("<html><div style='text-align: right;'><b>" + username + "</b><br><span style='color:gray;'>" + displayRole + "</span></div></html>");
        
        // Nếu không phải OWNER (Chủ) thì ẩn nút Tạo tài khoản đi
        menuCreateAccount.setVisible(role.equalsIgnoreCase("OWNER"));
    }

    // Hàm thiết lập sự kiện cho nút Toggle Menu bên trái
    public void setToggleAction(ActionListener listener) {
        btnToggle.addActionListener(listener);
    }

    // Thiết lập sự kiện khi nhấn Đăng xuất
    public void setLogoutAction(ActionListener listener) {
        menuLogout.addActionListener(listener);
    }

    // Thiết lập sự kiện khi nhấn Đổi mật khẩu
    public void setChangePasswordAction(ActionListener listener) {
        menuChangePass.addActionListener(listener);
    }

    // Thiết lập sự kiện khi nhấn Tạo tài khoản
    public void setCreateAccountAction(ActionListener listener) {
        menuCreateAccount.addActionListener(listener);
    }
}