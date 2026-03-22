package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KhachThueUI extends JPanel {

    private DefaultTableModel model;
    private int nextCustomerNumber = 1;

    // ===== PAGINATION =====
    private final int ITEMS_PER_PAGE = 4;
    private int currentPage = 1;
    private final List<Object[]> allTenants = new ArrayList<>();

    private JLabel lblInfo;
    private RoundedWhiteButton btnPrev;
    private RoundedWhiteButton btnNext;

    public KhachThueUI() {
        setLayout(new BorderLayout());

        Color bgMain = new Color(240, 240, 240);
        Color bgSidebar = new Color(245, 245, 245);
        Color borderColor = new Color(220, 220, 220);

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));

        JLabel logo = new JLabel("MiniApart");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setPreferredSize(new Dimension(220, 70));
        logo.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, borderColor));
        header.add(logo, BorderLayout.WEST);

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setBackground(Color.WHITE);
        headerText.setBorder(new EmptyBorder(10, 20, 10, 10));

        JLabel title = new JLabel("Khách thuê");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel subtitle = new JLabel("Quản lý danh sách người thuê nhà");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);

        headerText.add(title);
        headerText.add(subtitle);
        header.add(headerText, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // ===== SIDEBAR =====
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(bgSidebar);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(createMenuItem("Tổng quan", false));
        sidebar.add(createMenuItem("Phòng", false));
        sidebar.add(createMenuItem("Khách thuê", true));
        sidebar.add(createMenuItem("Hóa đơn", false));
        sidebar.add(createMenuItem("Bảo trì", false));
        sidebar.add(createMenuItem("Cài đặt", false));

        add(sidebar, BorderLayout.WEST);

        // ===== MAIN CONTENT =====
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(bgMain);

        // ===== TOOLBAR =====
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
        toolbar.setBackground(bgMain);
        toolbar.setBorder(new EmptyBorder(15, 20, 10, 20));

        RoundedButton btnExport = new RoundedButton("Xuất Excel");
        btnExport.setBackground(Color.WHITE);
        btnExport.setForeground(new Color(60, 60, 60));
        btnExport.setPreferredSize(new Dimension(105, 38));
        btnExport.setMaximumSize(new Dimension(105, 38));

        RoundedButton btnAdd = new RoundedButton("+ Thêm khách");
        btnAdd.setBackground(new Color(88, 80, 236));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setPreferredSize(new Dimension(130, 38));
        btnAdd.setMaximumSize(new Dimension(130, 38));

        toolbar.add(btnExport);
        toolbar.add(Box.createHorizontalStrut(15));
        toolbar.add(btnAdd);
        toolbar.add(Box.createHorizontalGlue());

        mainContent.add(toolbar, BorderLayout.NORTH);

        // ===== CONTENT =====
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(bgMain);
        content.setBorder(new EmptyBorder(0, 20, 0, 20));

        RoundedBoxPanel box = new RoundedBoxPanel();
        box.setLayout(new BorderLayout());

        // ===== SEARCH + FILTER =====
        JPanel topSection = new JPanel(new BorderLayout(15, 0));
        topSection.setOpaque(false);
        topSection.setBorder(new EmptyBorder(15, 15, 15, 15));

        RoundedTextField txtSearch = new RoundedTextField("Tìm tên, số điện thoại, CMND...");
        txtSearch.setPreferredSize(new Dimension(500, 42));

        RoundedWhiteButton btnFilter = new RoundedWhiteButton("Lọc theo phòng");
        btnFilter.setPreferredSize(new Dimension(150, 42));
        btnFilter.setMaximumSize(new Dimension(150, 42));

        topSection.add(txtSearch, BorderLayout.CENTER);
        topSection.add(btnFilter, BorderLayout.EAST);

        // ===== TABLE =====
        String[] columns = {
                "KHÁCH THUÊ", "PHÒNG", "LIÊN HỆ",
                "NGÀY BẮT ĐẦU", "TRẠNG THÁI", "THAO TÁC"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(88);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBorder(BorderFactory.createEmptyBorder());
        table.setSelectionBackground(new Color(245, 245, 255));
        table.setSelectionForeground(Color.BLACK);

        // ===== FIX ô xanh / focus cell =====
        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setFocusable(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, false, row, column);

                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setBorder(new EmptyBorder(0, 10, 0, 10));
                return label;
            }
        };

        DefaultTableCellRenderer leftPaddingRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, false, row, column);

                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setBorder(new EmptyBorder(0, 28, 0, 10));
                return label;
            }
        };

        table.getColumnModel().getColumn(0).setCellRenderer(leftPaddingRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(leftPaddingRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableHeader.setForeground(new Color(110, 110, 110));
        tableHeader.setBackground(new Color(245, 245, 245));
        tableHeader.setBorder(BorderFactory.createEmptyBorder());
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(false);
        tableHeader.setPreferredSize(new Dimension(0, 40));

        tableHeader.setDefaultRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(JLabel.CENTER);
            }

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, false, row, column);

                label.setBackground(new Color(245, 245, 245));
                label.setForeground(new Color(110, 110, 110));
                label.setBorder(BorderFactory.createEmptyBorder());
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tableSection = new JPanel(new BorderLayout());
        tableSection.setOpaque(false);

        JPanel tableInner = new JPanel(new BorderLayout());
        tableInner.setOpaque(false);
        tableInner.setBorder(BorderFactory.createMatteBorder(
                1, 1, 1, 1, new Color(220, 220, 220)
        ));

        tableInner.add(tableHeader, BorderLayout.NORTH);
        tableInner.add(scrollPane, BorderLayout.CENTER);

        tableSection.add(tableInner, BorderLayout.CENTER);

        // ===== FOOTER =====
        JPanel bottomSection = new JPanel(new BorderLayout());
        bottomSection.setOpaque(false);
        bottomSection.setBorder(new EmptyBorder(10, 15, 15, 15));

        lblInfo = new JLabel("Hiển thị 0 khách");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfo.setForeground(Color.GRAY);

        JPanel pagingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pagingPanel.setOpaque(false);

        btnPrev = new RoundedWhiteButton("Trước");
        btnNext = new RoundedWhiteButton("Sau");
        btnPrev.setPreferredSize(new Dimension(80, 35));
        btnNext.setPreferredSize(new Dimension(80, 35));

        pagingPanel.add(btnPrev);
        pagingPanel.add(btnNext);

        bottomSection.add(lblInfo, BorderLayout.WEST);
        bottomSection.add(pagingPanel, BorderLayout.EAST);

        // ===== ADD TO BOX =====
        box.add(topSection, BorderLayout.NORTH);
        box.add(tableSection, BorderLayout.CENTER);
        box.add(bottomSection, BorderLayout.SOUTH);

        content.add(box, BorderLayout.CENTER);
        mainContent.add(content, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // ===== BUTTON EVENT =====
        btnAdd.addActionListener(e -> showAddTenantDialog());

        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                refreshTable();
            }
        });

        btnNext.addActionListener(e -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                refreshTable();
            }
        });

        // ===== DATA MẪU =====
        addTenantToList("A", "111", "a@gmail.com", "P101", "03/14/2026");
        addTenantToList("B", "222", "b@gmail.com", "P102", "03/14/2026");
        addTenantToList("C", "333", "c@gmail.com", "P103", "03/14/2026");
        addTenantToList("D", "444", "d@gmail.com", "P104", "03/14/2026");
        addTenantToList("E", "555", "e@gmail.com", "P105", "03/14/2026");
        addTenantToList("F", "666", "f@gmail.com", "P106", "03/14/2026");
        addTenantToList("G", "777", "g@gmail.com", "P107", "03/14/2026");
        addTenantToList("H", "888", "h@gmail.com", "P108", "03/14/2026");
        addTenantToList("I", "999", "i@gmail.com", "P109", "03/14/2026");

        // Mở mặc định ở trang 1
        currentPage = 1;
        refreshTable();
    }

    private int getTotalPages() {
        if (allTenants.isEmpty()) return 1;
        return (int) Math.ceil((double) allTenants.size() / ITEMS_PER_PAGE);
    }

    private void refreshTable() {
        model.setRowCount(0);

        int totalPages = getTotalPages();
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }

        int start = (currentPage - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allTenants.size());

        for (int i = start; i < end; i++) {
            model.addRow(allTenants.get(i));
        }

        lblInfo.setText("Đang có " + allTenants.size() + " khách - Trang " + currentPage + "/" + totalPages);

        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private void addTenantToList(String hoTen, String soDienThoai, String email, String phong, String ngayVao) {
        String maKhach = String.format("KH%03d", nextCustomerNumber);
        nextCustomerNumber++;

        String tenHienThi =
                "<html><div style='padding-left:10px;'>" +
                        "<b>" + hoTen + "</b><br>" +
                        "<span style='color:#6b7280;'>Mã: " + maKhach + "</span>" +
                        "</div></html>";

        String lienHeHienThi =
                "<html><div style='padding-left:10px;'>" +
                        soDienThoai + "<br>" +
                        "<span style='color:#6b7280;'>" + email + "</span>" +
                        "</div></html>";

        String thaoTacHienThi =
                "<html><div style='text-align:center;'>" +
                        "Chỉnh sửa<br>" +
                        "<span style='color:red;'>Xóa</span>" +
                        "</div></html>";

        Object[] row = new Object[]{
                tenHienThi,
                phong,
                lienHeHienThi,
                ngayVao,
                "Đang thuê",
                thaoTacHienThi
        };

        allTenants.add(row);
    }

    private JPanel createMenuItem(String text, boolean selected) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(5, 20, 5, 20));

        RoundedPanel item = new RoundedPanel(selected);
        item.setLayout(new BorderLayout());
        item.setPreferredSize(new Dimension(160, 40));
        item.setMaximumSize(new Dimension(160, 40));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setBorder(new EmptyBorder(0, 15, 0, 0));

        item.add(label, BorderLayout.CENTER);
        wrapper.add(item, BorderLayout.CENTER);

        return wrapper;
    }

    private void showAddTenantDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setSize(520, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        RoundedDialogPanel container = new RoundedDialogPanel();
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(18, 18, 18, 18));
        dialog.setContentPane(container);

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(18, 28, 18, 28));

        JLabel title = new JLabel("Thêm khách thuê mới");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(title);
        formPanel.add(Box.createVerticalStrut(20));

        JTextField txtHoTen = createInputField(formPanel, "Họ tên");
        JTextField txtSoDienThoai = createInputField(formPanel, "Số điện thoại");
        JTextField txtEmail = createInputField(formPanel, "Email");

        JLabel lblPhong = new JLabel("Phòng");
        lblPhong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPhong.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lblPhong);
        formPanel.add(Box.createVerticalStrut(8));

        String[] dsPhong = {"Chưa có phòng", "P101", "P201", "P301", "P302"};
        RoundedComboBox<String> cbPhong = new RoundedComboBox<>(dsPhong);
        cbPhong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        cbPhong.setPreferredSize(new Dimension(400, 42));
        cbPhong.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(cbPhong);
        formPanel.add(Box.createVerticalStrut(18));

        JTextField txtNgayVao = createInputField(formPanel, "Ngày vào");
        txtNgayVao.setText("03/14/2026");

        JTextField txtTienCoc = createInputField(formPanel, "Tiền cọc (VNĐ)");
        txtTienCoc.setText("0");

        formPanel.add(Box.createVerticalStrut(18));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedWhiteButton btnHuy = new RoundedWhiteButton("Hủy");
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));

        RoundedButton btnThemMoi = new RoundedButton("Thêm mới");
        btnThemMoi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThemMoi.setBackground(new Color(37, 99, 235));
        btnThemMoi.setForeground(Color.WHITE);

        buttonPanel.add(btnHuy);
        buttonPanel.add(btnThemMoi);

        formPanel.add(buttonPanel);

        btnHuy.addActionListener(e -> dialog.dispose());

        btnThemMoi.addActionListener(e -> {
            String hoTen = txtHoTen.getText().trim();
            String soDienThoai = txtSoDienThoai.getText().trim();
            String email = txtEmail.getText().trim();
            String phong = (String) cbPhong.getSelectedItem();
            String ngayVao = txtNgayVao.getText().trim();

            if (hoTen.isEmpty() || soDienThoai.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập ít nhất Họ tên và Số điện thoại.");
                return;
            }

            addTenantToList(hoTen, soDienThoai, email, phong, ngayVao);
            currentPage = getTotalPages();
            refreshTable();
            dialog.dispose();
        });

        container.add(formPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JTextField createInputField(JPanel parent, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedTextField textField = new RoundedTextField("");
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        textField.setPreferredSize(new Dimension(400, 42));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(label);
        parent.add(Box.createVerticalStrut(8));
        parent.add(textField);
        parent.add(Box.createVerticalStrut(18));

        return textField;
    }

    class RoundedPanel extends JPanel {
        private final boolean selected;

        public RoundedPanel(boolean selected) {
            this.selected = selected;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (selected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2.setColor(new Color(220, 220, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    class RoundedButton extends JButton {
        private final int radius = 14;

        public RoundedButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(0, 0, 0, 0));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();

            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
        }
    }

    class RoundedBoxPanel extends JPanel {
        private final int radius = 18;

        public RoundedBoxPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            g2.setColor(new Color(225, 225, 225));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    class RoundedDialogPanel extends JPanel {
        private final int radius = 22;

        public RoundedDialogPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(new Color(245, 245, 245));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            g2.setColor(new Color(210, 210, 210));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    class RoundedTextField extends JTextField {
        private final String placeholder;
        private final int radius = 16;
        private boolean showingPlaceholder = true;

        public RoundedTextField(String placeholder) {
            this.placeholder = placeholder;

            setText(placeholder);
            setForeground(Color.GRAY);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setOpaque(false);
            setBorder(new EmptyBorder(0, 15, 0, 15));
            setCaretColor(new Color(0, 0, 0, 0));

            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    if (showingPlaceholder) {
                        setText("");
                        setForeground(Color.BLACK);
                        setCaretColor(Color.BLACK);
                        showingPlaceholder = false;
                    }
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    if (getText().trim().isEmpty()) {
                        setText(placeholder);
                        setForeground(Color.GRAY);
                        setCaretColor(new Color(0, 0, 0, 0));
                        showingPlaceholder = true;
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.dispose();
        }
    }

    class RoundedWhiteButton extends JButton {
        private final int radius = 16;

        public RoundedWhiteButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(new Color(60, 60, 60));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(0, 0, 0, 0));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Color fillColor = isEnabled() ? Color.WHITE : new Color(245, 245, 245);
            Color borderColor = isEnabled() ? new Color(220, 220, 220) : new Color(230, 230, 230);
            Color textColor = isEnabled() ? new Color(60, 60, 60) : new Color(170, 170, 170);

            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            g2.setColor(textColor);
            g2.drawString(getText(), x, y);

            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
        }
    }

    class RoundedComboBox<E> extends JComboBox<E> {
        private final int radius = 14;

        public RoundedComboBox(E[] items) {
            super(items);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBackground(Color.WHITE);
            setForeground(new Color(40, 40, 40));
            setBorder(new EmptyBorder(0, 12, 0, 36));
            setFocusable(false);

            setUI(new BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    JButton button = new JButton() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(
                                    RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON
                            );

                            int w = getWidth();
                            int h = getHeight();

                            int[] x = {w / 2 - 4, w / 2 + 4, w / 2};
                            int[] y = {h / 2 - 2, h / 2 - 2, h / 2 + 3};

                            g2.setColor(new Color(80, 80, 80));
                            g2.fillPolygon(x, y, 3);

                            g2.dispose();
                        }
                    };

                    button.setPreferredSize(new Dimension(30, 30));
                    button.setOpaque(false);
                    button.setContentAreaFilled(false);
                    button.setBorderPainted(false);
                    button.setFocusPainted(false);
                    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    return button;
                }

                @Override
                public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                }
            });

            setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {

                    JLabel label = (JLabel) super.getListCellRendererComponent(
                            list, value, index, isSelected, cellHasFocus);

                    label.setBorder(new EmptyBorder(6, 10, 6, 10));

                    if (index == -1) {
                        label.setOpaque(false);
                        label.setForeground(new Color(40, 40, 40));
                    } else {
                        label.setOpaque(true);
                        if (isSelected) {
                            label.setBackground(new Color(240, 240, 240));
                        } else {
                            label.setBackground(Color.WHITE);
                        }
                        label.setForeground(Color.BLACK);
                    }

                    return label;
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Apartment Management System");
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new KhachThueUI());
        frame.setVisible(true);
    }
}