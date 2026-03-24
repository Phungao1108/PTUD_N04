package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

public class KhachThuePanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private int nextCustomerNumber = 1;

    private final int ITEMS_PER_PAGE = 9;
    private int currentPage = 1;
    private final List<TenantInfo> allTenants = new ArrayList<>();

    private JLabel lblInfo;
    private RoundedWhiteButton btnPrev;
    private RoundedWhiteButton btnNext;

    private int hoverRow = -1;
    private int hoverActionPart = 0; // 0: none, 1: edit, 2: delete
    private int pressedRow = -1;
    private int pressedActionPart = 0;

    public KhachThuePanel() {
        setLayout(new BorderLayout());

        Color bgMain = new Color(240, 240, 240);

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(bgMain);

        // ===== HEADER / TOOLBAR =====
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(bgMain);
        toolbar.setBorder(new EmptyBorder(15, 20, 10, 20));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Khách thuê");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 34));
        lblTitle.setForeground(new Color(30, 30, 30));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Quản lý danh sách người thuê nhà");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(120, 120, 120));
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(lblSubtitle);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        buttonPanel.setOpaque(false);

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

        buttonPanel.add(btnExport);
        buttonPanel.add(btnAdd);

        toolbar.add(titlePanel, BorderLayout.WEST);
        toolbar.add(buttonPanel, BorderLayout.EAST);

        mainContent.add(toolbar, BorderLayout.NORTH);

        // ===== CONTENT =====
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(bgMain);
        content.setBorder(new EmptyBorder(0, 20, 0, 20));

        RoundedBoxPanel box = new RoundedBoxPanel();
        box.setLayout(new BorderLayout());

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

        table = new JTable(model);
        table.setRowHeight(75);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBorder(BorderFactory.createEmptyBorder());
        table.setSelectionBackground(new Color(245, 245, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);

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
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

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

        box.add(topSection, BorderLayout.NORTH);
        box.add(tableSection, BorderLayout.CENTER);
        box.add(bottomSection, BorderLayout.SOUTH);

        content.add(box, BorderLayout.CENTER);
        mainContent.add(content, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> showTenantDialog(null));

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

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                int newHoverRow = -1;
                int newHoverPart = 0;

                if (row != -1 && col == 5) {
                    Rectangle editBounds = getEditTextBounds(row, col);
                    Rectangle deleteBounds = getDeleteTextBounds(row, col);
                    Point p = e.getPoint();

                    if (editBounds.contains(p)) {
                        newHoverRow = (currentPage - 1) * ITEMS_PER_PAGE + row;
                        newHoverPart = 1;
                        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    } else if (deleteBounds.contains(p)) {
                        newHoverRow = (currentPage - 1) * ITEMS_PER_PAGE + row;
                        newHoverPart = 2;
                        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    } else {
                        table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                } else {
                    table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                if (hoverRow != newHoverRow || hoverActionPart != newHoverPart) {
                    hoverRow = newHoverRow;
                    hoverActionPart = newHoverPart;
                    refreshTable();
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoverRow = -1;
                hoverActionPart = 0;
                pressedRow = -1;
                pressedActionPart = 0;
                table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                refreshTable();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                pressedRow = -1;
                pressedActionPart = 0;

                if (row != -1 && col == 5) {
                    Rectangle editBounds = getEditTextBounds(row, col);
                    Rectangle deleteBounds = getDeleteTextBounds(row, col);
                    Point p = e.getPoint();

                    if (editBounds.contains(p)) {
                        pressedRow = (currentPage - 1) * ITEMS_PER_PAGE + row;
                        pressedActionPart = 1;
                        refreshTable();
                    } else if (deleteBounds.contains(p)) {
                        pressedRow = (currentPage - 1) * ITEMS_PER_PAGE + row;
                        pressedActionPart = 2;
                        refreshTable();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                int releasedRow = -1;
                int releasedPart = 0;

                if (row != -1 && col == 5) {
                    Rectangle editBounds = getEditTextBounds(row, col);
                    Rectangle deleteBounds = getDeleteTextBounds(row, col);
                    Point p = e.getPoint();

                    if (editBounds.contains(p)) {
                        releasedRow = (currentPage - 1) * ITEMS_PER_PAGE + row;
                        releasedPart = 1;
                    } else if (deleteBounds.contains(p)) {
                        releasedRow = (currentPage - 1) * ITEMS_PER_PAGE + row;
                        releasedPart = 2;
                    }
                }

                if (pressedRow == releasedRow && pressedActionPart == releasedPart && releasedRow != -1) {
                    if (releasedPart == 1) {
                        showTenantDialog(allTenants.get(releasedRow));
                    } else {
                        int confirm = JOptionPane.showConfirmDialog(
                                KhachThuePanel.this,
                                "Bạn có chắc muốn xóa khách này?",
                                "Xác nhận xóa",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (confirm == JOptionPane.YES_OPTION) {
                            allTenants.remove(releasedRow);
                            if (currentPage > getTotalPages()) currentPage = getTotalPages();
                        }
                    }
                }

                pressedRow = -1;
                pressedActionPart = 0;
                refreshTable();
            }
        });

        addTenantToList("Nguyễn Văn A", "0901234567", "a@gmail.com", "P101", "03/14/2026");
        addTenantToList("Trần Thị B", "0912345678", "b@gmail.com", "P102", "03/14/2026");
        addTenantToList("Lê Văn C", "0987654321", "c@gmail.com", "P103", "03/14/2026");
        addTenantToList("Phạm Thị D", "0934567890", "d@gmail.com", "P104", "03/14/2026");
        addTenantToList("Hoàng Văn E", "0945678901", "e@gmail.com", "P105", "03/14/2026");
        addTenantToList("Vũ Thị F", "0956789012", "f@gmail.com", "P106", "03/14/2026");
        addTenantToList("Dương Văn G", "0967890123", "g@gmail.com", "P107", "03/14/2026");
        addTenantToList("Bùi Thị H", "0978901234", "h@gmail.com", "P108", "03/14/2026");
        addTenantToList("Tô Văn I", "0989012345", "i@gmail.com", "P109", "03/14/2026");

        currentPage = 1;
        refreshTable();
    }

    private Rectangle getEditTextBounds(int viewRow, int viewCol) {
        Rectangle cellRect = table.getCellRect(viewRow, viewCol, false);

        Font font = new Font("Segoe UI", Font.PLAIN, 13);
        FontMetrics fm = table.getFontMetrics(font);

        String text = "Chỉnh sửa";
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        int centerX = cellRect.x + cellRect.width / 2;
        int textX = centerX - textWidth / 2;

        int firstLineBaseline = cellRect.y + (table.getRowHeight() / 2) - 6;
        int textY = firstLineBaseline - fm.getAscent();

        return new Rectangle(textX, textY, textWidth, textHeight);
    }

    private Rectangle getDeleteTextBounds(int viewRow, int viewCol) {
        Rectangle cellRect = table.getCellRect(viewRow, viewCol, false);

        Font font = new Font("Segoe UI", Font.PLAIN, 13);
        FontMetrics fm = table.getFontMetrics(font);

        String text = "Xóa";
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        int centerX = cellRect.x + cellRect.width / 2;
        int textX = centerX - textWidth / 2;

        int secondLineBaseline = cellRect.y + (table.getRowHeight() / 2) + 14;
        int textY = secondLineBaseline - fm.getAscent();

        return new Rectangle(textX, textY, textWidth, textHeight);
    }

    private int getTotalPages() {
        if (allTenants.isEmpty()) return 1;
        return (int) Math.ceil((double) allTenants.size() / ITEMS_PER_PAGE);
    }

    private void refreshTable() {
        model.setRowCount(0);

        int totalPages = getTotalPages();
        if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;

        int start = (currentPage - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allTenants.size());

        for (int i = start; i < end; i++) {
            model.addRow(toRow(allTenants.get(i), i));
        }

        lblInfo.setText("Đang có " + allTenants.size() + " khách - Trang " + currentPage + "/" + totalPages);
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);

        revalidate();
        repaint();
    }

    private Object[] toRow(TenantInfo tenant, int rowIndex) {
        String tenHienThi =
                "<html><div style='padding-left:10px;'>" +
                        "<b>" + tenant.hoTen + "</b><br>" +
                        "<span style='color:#6b7280;'>Mã: " + tenant.maKhach + "</span>" +
                        "</div></html>";

        String lienHeHienThi =
                "<html><div style='padding-left:10px;'>" +
                        tenant.soDienThoai + "<br>" +
                        "<span style='color:#6b7280;'>" + tenant.email + "</span>" +
                        "</div></html>";

        String editColor = "#111111";
        String deleteColor = "#ef4444";

        if (rowIndex == hoverRow && hoverActionPart == 1) editColor = "#2563eb";
        if (rowIndex == hoverRow && hoverActionPart == 2) deleteColor = "#dc2626";

        if (rowIndex == pressedRow && pressedActionPart == 1) editColor = "#1d4ed8";
        if (rowIndex == pressedRow && pressedActionPart == 2) deleteColor = "#b91c1c";

        String thaoTacHienThi =
                "<html><div style='text-align:center;'>" +
                        "<span style='color:" + editColor + ";'><u>Chỉnh sửa</u></span><br>" +
                        "<span style='color:" + deleteColor + ";'><u>Xóa</u></span>" +
                        "</div></html>";

        return new Object[]{
                tenHienThi,
                tenant.phong,
                lienHeHienThi,
                tenant.ngayVao,
                "Đang thuê",
                thaoTacHienThi
        };
    }

    private void addTenantToList(String hoTen, String soDienThoai, String email, String phong, String ngayVao) {
        String maKhach = String.format("KH%03d", nextCustomerNumber);
        nextCustomerNumber++;

        allTenants.add(new TenantInfo(maKhach, hoTen, soDienThoai, email, phong, ngayVao));
    }

    private void showTenantDialog(TenantInfo editingTenant) {
        boolean isEdit = editingTenant != null;

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

        JLabel title = new JLabel(isEdit ? "Sửa thông tin khách thuê" : "Thêm khách thuê mới");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(title);
        formPanel.add(Box.createVerticalStrut(20));

        RoundedTextField txtHoTen = createInputField(formPanel, "Họ tên");
        RoundedTextField txtSoDienThoai = createInputField(formPanel, "Số điện thoại");
        RoundedTextField txtEmail = createInputField(formPanel, "Email");

        JLabel lblPhong = new JLabel("Phòng");
        lblPhong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPhong.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lblPhong);
        formPanel.add(Box.createVerticalStrut(8));

        String[] dsPhong = {"Chưa có phòng", "P101", "P201", "P301", "P302", "P102", "P103", "P104", "P105", "P106", "P107", "P108", "P109"};
        RoundedComboBox<String> cbPhong = new RoundedComboBox<>(dsPhong);
        cbPhong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        cbPhong.setPreferredSize(new Dimension(400, 42));
        cbPhong.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(cbPhong);
        formPanel.add(Box.createVerticalStrut(18));

        RoundedTextField txtNgayVao = createInputField(formPanel, "Ngày vào");
        RoundedTextField txtTienCoc = createInputField(formPanel, "Tiền cọc (VNĐ)");

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedWhiteButton btnHuy = new RoundedWhiteButton("Hủy");
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));

        RoundedButton btnSave = new RoundedButton(isEdit ? "Sửa" : "Thêm mới");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(new Color(37, 99, 235));
        btnSave.setForeground(Color.WHITE);
        btnSave.setEnabled(!isEdit);

        buttonPanel.add(btnHuy);
        buttonPanel.add(btnSave);

        formPanel.add(Box.createVerticalStrut(18));
        formPanel.add(buttonPanel);

        if (isEdit) {
            txtHoTen.setRealText(editingTenant.hoTen);
            txtSoDienThoai.setRealText(editingTenant.soDienThoai);
            txtEmail.setRealText(editingTenant.email);
            cbPhong.setSelectedItem(editingTenant.phong);
            txtNgayVao.setRealText(editingTenant.ngayVao);
            txtTienCoc.setRealText("0");
        } else {
            txtNgayVao.setRealText("03/14/2026");
            txtTienCoc.setRealText("0");
        }

        Runnable updateSaveButtonState = () -> {
            if (!isEdit) return;

            boolean changed =
                    !txtHoTen.getRealText().equals(editingTenant.hoTen) ||
                    !txtSoDienThoai.getRealText().equals(editingTenant.soDienThoai) ||
                    !txtEmail.getRealText().equals(editingTenant.email) ||
                    !String.valueOf(cbPhong.getSelectedItem()).equals(editingTenant.phong) ||
                    !txtNgayVao.getRealText().equals(editingTenant.ngayVao);

            btnSave.setEnabled(changed);
        };

        DocumentListener dl = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSaveButtonState.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSaveButtonState.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSaveButtonState.run();
            }
        };

        txtHoTen.getDocument().addDocumentListener(dl);
        txtSoDienThoai.getDocument().addDocumentListener(dl);
        txtEmail.getDocument().addDocumentListener(dl);
        txtNgayVao.getDocument().addDocumentListener(dl);

        cbPhong.addActionListener(e -> updateSaveButtonState.run());

        btnHuy.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String hoTen = txtHoTen.getRealText();
            String soDienThoai = txtSoDienThoai.getRealText();
            String email = txtEmail.getRealText();
            String phong = String.valueOf(cbPhong.getSelectedItem());
            String ngayVao = txtNgayVao.getRealText();

            if (hoTen.isEmpty() || soDienThoai.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập ít nhất Họ tên và Số điện thoại.");
                return;
            }

            if (isEdit) {
                editingTenant.hoTen = hoTen;
                editingTenant.soDienThoai = soDienThoai;
                editingTenant.email = email;
                editingTenant.phong = phong;
                editingTenant.ngayVao = ngayVao;
            } else {
                addTenantToList(hoTen, soDienThoai, email, phong, ngayVao);
                currentPage = getTotalPages();
            }

            refreshTable();
            dialog.dispose();
        });

        container.add(formPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private RoundedTextField createInputField(JPanel parent, String labelText) {
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

    private static class TenantInfo {
        String maKhach;
        String hoTen;
        String soDienThoai;
        String email;
        String phong;
        String ngayVao;

        public TenantInfo(String maKhach, String hoTen, String soDienThoai, String email, String phong, String ngayVao) {
            this.maKhach = maKhach;
            this.hoTen = hoTen;
            this.soDienThoai = soDienThoai;
            this.email = email;
            this.phong = phong;
            this.ngayVao = ngayVao;
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

            Color fillColor;
            Color textColor;

            if (isEnabled()) {
                fillColor = getBackground();
                textColor = getForeground();
            } else {
                fillColor = new Color(220, 220, 220);
                textColor = new Color(150, 150, 150);
            }

            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

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

            if (placeholder != null && !placeholder.isEmpty()) {
                setText(placeholder);
                setForeground(Color.GRAY);
                showingPlaceholder = true;
            } else {
                setText("");
                setForeground(Color.BLACK);
                showingPlaceholder = false;
            }

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
                    if (getText().trim().isEmpty() && placeholder != null && !placeholder.isEmpty()) {
                        setText(placeholder);
                        setForeground(Color.GRAY);
                        setCaretColor(new Color(0, 0, 0, 0));
                        showingPlaceholder = true;
                    }
                }
            });
        }

        public void setRealText(String value) {
            if (value == null || value.trim().isEmpty()) {
                if (placeholder != null && !placeholder.isEmpty()) {
                    setText(placeholder);
                    setForeground(Color.GRAY);
                    setCaretColor(new Color(0, 0, 0, 0));
                    showingPlaceholder = true;
                } else {
                    setText("");
                    setForeground(Color.BLACK);
                    setCaretColor(Color.BLACK);
                    showingPlaceholder = false;
                }
            } else {
                setText(value);
                setForeground(Color.BLACK);
                setCaretColor(Color.BLACK);
                showingPlaceholder = false;
            }
        }

        public String getRealText() {
            return showingPlaceholder ? "" : getText().trim();
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
}