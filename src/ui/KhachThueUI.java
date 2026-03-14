package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class KhachThueUI extends JPanel {

    private DefaultTableModel model;
    private int nextCustomerNumber = 1;

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
        mainContent.setFocusable(true);
        mainContent.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mainContent.requestFocusInWindow();
            }
        });

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
        content.setFocusable(true);
        content.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                content.requestFocusInWindow();
            }
        });

        RoundedBoxPanel box = new RoundedBoxPanel();
        box.setLayout(new BorderLayout());
        box.setFocusable(true);
        box.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                box.requestFocusInWindow();
            }
        });

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
        table.setRowHeight(60);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBorder(BorderFactory.createEmptyBorder());
        table.setSelectionBackground(new Color(245, 245, 255));
        table.setSelectionForeground(Color.BLACK);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setVerticalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer leftPaddingRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

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
                        table, value, isSelected, hasFocus, row, column);

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

        JLabel lblInfo = new JLabel("Hiển thị 0 khách");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfo.setForeground(Color.GRAY);

        JPanel pagingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pagingPanel.setOpaque(false);

        RoundedWhiteButton btnPrev = new RoundedWhiteButton("Trước");
        RoundedWhiteButton btnNext = new RoundedWhiteButton("Sau");
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
        btnAdd.addActionListener(e -> showAddTenantDialog(lblInfo));
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

    private void showAddTenantDialog(JLabel lblInfo) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setUndecorated(true);
        dialog.setSize(500, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(0, 0, 0, 0));

        RoundedBoxPanel container = new RoundedBoxPanel();
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        dialog.setContentPane(container);

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

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
        buttonPanel.setBackground(Color.WHITE);
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

            model.addRow(new Object[]{
                    tenHienThi,
                    phong,
                    lienHeHienThi,
                    ngayVao,
                    "Đang thuê",
                    thaoTacHienThi
            });

            lblInfo.setText("Hiển thị " + model.getRowCount() + " khách");
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

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.dispose();
            super.paintComponent(g);
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
            setBorder(new EmptyBorder(0, 12, 0, 12));

            setUI(new BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    JButton button = new JButton("▼");
                    button.setBorder(BorderFactory.createEmptyBorder());
                    button.setContentAreaFilled(false);
                    button.setFocusPainted(false);
                    button.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    return button;
                }
            });

            setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(
                            list, value, index, isSelected, cellHasFocus);
                    label.setBorder(new EmptyBorder(6, 10, 6, 10));
                    if (isSelected) {
                        label.setBackground(new Color(240, 240, 240));
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

            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.dispose();
            super.paintComponent(g);
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