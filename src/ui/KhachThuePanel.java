
package ui;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class KhachThuePanel extends JPanel {

    public KhachThuePanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        initHeaderAndActions();
        initTableArea();
    }

    private void initHeaderAndActions() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 246, 250));

        // 1. Tiêu đề
        JLabel title = new JLabel("<html><h1 style='margin:0; font-family:sans-serif;'>Quản lý Khách thuê</h1><span style='color:gray; font-family:sans-serif;'>Danh sách người thuê và hợp đồng</span></html>");
        
        // 2. Khu vực các nút bấm (Nhập Excel, Thêm mới)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(new Color(245, 246, 250));

        JButton btnImportExcel = new JButton("Nhập Excel");
        btnImportExcel.setBackground(Color.WHITE);
        btnImportExcel.setFocusPainted(false);
        btnImportExcel.setPreferredSize(new Dimension(120, 38));

        JButton btnExportExcel = new JButton("Xuất Excel");
        btnExportExcel.setBackground(Color.WHITE);
        btnExportExcel.setFocusPainted(false);
        btnExportExcel.setPreferredSize(new Dimension(120, 38));

        JButton btnAddContract = new JButton("+ Lập hợp đồng mới");
        btnAddContract.setBackground(new Color(88, 86, 214));
        btnAddContract.setForeground(Color.WHITE);
        btnAddContract.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAddContract.setFocusPainted(false);
        btnAddContract.setPreferredSize(new Dimension(180, 38));

        // Sự kiện: Khi bấm "Lập hợp đồng mới" -> Sẽ mở ra một form nhập liệu (JDialog)
        btnAddContract.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Sẽ mở Form: Thêm thông tin Khách Hàng + Thông tin Hợp Đồng ở đây!");
            // Sau này bạn sẽ gọi: new HopDongDialog(MainFrame.this).setVisible(true);
        });

        actionPanel.add(btnImportExcel);
        actionPanel.add(btnExportExcel);
        actionPanel.add(btnAddContract);

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initTableArea() {
        // Khung viền trắng bọc lấy bộ lọc và bảng
        JPanel contentBg = new JPanel(new BorderLayout(0, 15));
        contentBg.setBackground(Color.WHITE);
        contentBg.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // 1. Khu vực Bộ lọc & Tìm kiếm
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBackground(Color.WHITE);

        JTextField txtSearch = new JTextField(30);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 10, 5, 10)
        ));
        txtSearch.setPreferredSize(new Dimension(300, 35));
        
        // Placeholder giả cho JTextField
        txtSearch.setText("Tìm tên, số điện thoại, CMND...");
        txtSearch.setForeground(Color.GRAY);

        // Nút lọc theo trạng thái
        String[] statusOptions = {"Tất cả trạng thái", "Đang thuê", "Sắp hết hạn", "Đã rời đi"};
        JComboBox<String> cbStatus = new JComboBox<>(statusOptions);
        cbStatus.setBackground(Color.WHITE);
        cbStatus.setPreferredSize(new Dimension(150, 35));

        JPanel rightFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightFilter.setBackground(Color.WHITE);
        rightFilter.add(new JLabel("Lọc theo:  "));
        rightFilter.add(cbStatus);

        filterPanel.add(txtSearch, BorderLayout.WEST);
        filterPanel.add(rightFilter, BorderLayout.EAST);

        // 2. Bảng dữ liệu (JTable)
        String[] columnNames = {"Mã Khách", "Khách thuê", "Phòng", "Liên hệ", "Ngày bắt đầu", "Trạng thái"};
        
        // Dữ liệu mẫu (Dummy data)
        Object[][] data = {
            {"KH001", "Nguyễn Văn A", "P101", "0901234567", "15/05/2023", "Đang thuê"},
            {"KH002", "Trần Thị B", "P201", "0912345678", "20/06/2023", "Đang thuê"},
            {"KH003", "Lê Văn C", "P301", "0987654321", "10/08/2023", "Đang thuê"},
            {"KH004", "Phạm Thị D", "P302", "0934567890", "05/09/2023", "Sắp hết hạn"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho chỉnh sửa trực tiếp trên ô
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(40); // Tăng chiều cao dòng cho dễ nhìn
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(245, 246, 250));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.setSelectionBackground(new Color(230, 230, 250)); // Màu xanh nhạt khi chọn dòng

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        contentBg.add(filterPanel, BorderLayout.NORTH);
        contentBg.add(scrollPane, BorderLayout.CENTER);

        add(contentBg, BorderLayout.CENTER);
    }
}