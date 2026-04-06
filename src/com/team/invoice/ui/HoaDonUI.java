package com.team.invoice.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.team.invoice.components.AppColors;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.dialog.CreateInvoiceDialog;
import com.team.invoice.dialog.InvoiceDetailDialog;
import com.team.invoice.entity.Invoice;
import com.team.invoice.entity.Room;
import com.team.invoice.service.HoaDonService;
import com.team.invoice.table.InvoiceTableModel;
import com.team.invoice.util.CurrencyUtils;

public class HoaDonUI extends JPanel {
    private final HoaDonService hoaDonService = new HoaDonService();
    private final InvoiceTableModel tableModel = new InvoiceTableModel();
    private final JTable table = new JTable(tableModel);
    private final StatCard totalCard = new StatCard("Tổng số hóa đơn");
    private final StatCard revenueCard = new StatCard("Tổng doanh thu");
    private final StatCard pendingCard = new StatCard("Chờ thanh toán");
    private final JLabel selectedPeriodLabel = new JLabel("Kỳ thanh toán: --");
    private final JLabel selectedDueLabel = new JLabel("Hạn thanh toán: --");
    private final JTextField txtFilterPeriod = new JTextField();
    private final JTextField txtFilterRoom = new JTextField();
    private final JComboBox<String> cboFilterStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Bản nháp", "Chờ thanh toán", "Đã thu", "Quá hạn"});
    private List<Invoice> allInvoices = new ArrayList<>();

    public HoaDonUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(AppColors.BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        reloadData();
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Quản lý hóa đơn");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(AppColors.TEXT);

        JLabel sub = new JLabel("Liên kết dữ liệu phòng, bảng giá, điện nước và phát hành hóa đơn từ giao diện chung.");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setForeground(AppColors.MUTED);

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(sub);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        RoundedButton btnCreate = new RoundedButton("Lập hóa đơn", AppColors.PRIMARY, Color.WHITE, AppColors.PRIMARY);
        RoundedButton btnRefresh = new RoundedButton("Làm mới", AppColors.WHITE, AppColors.TEXT, AppColors.BORDER);
        btnCreate.setPreferredSize(new Dimension(140, 38));
        btnRefresh.setPreferredSize(new Dimension(120, 38));
        btnCreate.addActionListener(e -> openCreateDialog());
        btnRefresh.addActionListener(e -> reloadData());
        actions.add(btnRefresh);
        actions.add(btnCreate);

        panel.add(titleBox, BorderLayout.WEST);
        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        stats.setOpaque(false);
        stats.add(totalCard);
        stats.add(revenueCard);
        stats.add(pendingCard);

        RoundedPanel tableCard = new RoundedPanel(24, AppColors.WHITE, AppColors.BORDER);
        tableCard.setLayout(new BorderLayout(0, 12));
        tableCard.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel cardTop = new JPanel(new BorderLayout());
        cardTop.setOpaque(false);
        JLabel title = new JLabel("Danh sách hóa đơn");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(AppColors.TEXT);
        JLabel help = new JLabel("Bấm trực tiếp ở cột thao tác hoặc dùng nút nhanh để xem chi tiết, thu tiền và xóa mềm.");
        help.setForeground(AppColors.MUTED);
        help.setFont(new Font("SansSerif", Font.PLAIN, 13));

        selectedPeriodLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        selectedPeriodLabel.setForeground(AppColors.TEXT);
        selectedDueLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        selectedDueLabel.setForeground(AppColors.TEXT);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(Box.createVerticalStrut(3));
        text.add(help);
        text.add(Box.createVerticalStrut(6));
        text.add(selectedPeriodLabel);
        text.add(Box.createVerticalStrut(2));
        text.add(selectedDueLabel);

        JPanel filterBar = buildFilterBar();

        JPanel rowActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rowActions.setOpaque(false);
        RoundedButton btnDetail = new RoundedButton("Chi tiết", AppColors.WHITE, AppColors.TEXT, AppColors.BORDER);
        RoundedButton btnPaid = new RoundedButton("Đã thanh toán", AppColors.SUCCESS, Color.WHITE, AppColors.SUCCESS);
        RoundedButton btnDelete = new RoundedButton("Xóa", AppColors.WHITE, AppColors.DANGER, AppColors.DANGER);
        btnDetail.setPreferredSize(new Dimension(110, 34));
        btnPaid.setPreferredSize(new Dimension(140, 34));
        btnDelete.setPreferredSize(new Dimension(100, 34));
        btnDetail.addActionListener(e -> showSelectedDetail());
        btnPaid.addActionListener(e -> markSelectedPaid());
        btnDelete.addActionListener(e -> deleteSelected());
        rowActions.add(btnDetail);
        rowActions.add(btnPaid);
        rowActions.add(btnDelete);

        cardTop.add(text, BorderLayout.WEST);
        cardTop.add(rowActions, BorderLayout.EAST);

        configureTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel northWrap = new JPanel(new BorderLayout(0, 10));
        northWrap.setOpaque(false);
        northWrap.add(cardTop, BorderLayout.NORTH);
        northWrap.add(filterBar, BorderLayout.SOUTH);

        tableCard.add(northWrap, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        body.add(stats, BorderLayout.NORTH);
        body.add(tableCard, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildFilterBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);

        txtFilterPeriod.setPreferredSize(new Dimension(120, 34));
        txtFilterRoom.setPreferredSize(new Dimension(140, 34));
        cboFilterStatus.setPreferredSize(new Dimension(150, 34));
        txtFilterPeriod.setToolTipText("Lọc theo kỳ MM/yyyy");
        txtFilterRoom.setToolTipText("Lọc theo phòng hoặc khách");

        RoundedButton btnApply = new RoundedButton("Lọc", AppColors.PRIMARY, Color.WHITE, AppColors.PRIMARY);
        RoundedButton btnClear = new RoundedButton("Xóa lọc", AppColors.WHITE, AppColors.TEXT, AppColors.BORDER);
        btnApply.setPreferredSize(new Dimension(90, 34));
        btnClear.setPreferredSize(new Dimension(100, 34));
        btnApply.addActionListener(e -> applyFilters());
        btnClear.addActionListener(e -> {
            txtFilterPeriod.setText("");
            txtFilterRoom.setText("");
            cboFilterStatus.setSelectedIndex(0);
            applyFilters();
        });

        panel.add(new JLabel("Kỳ:"));
        panel.add(txtFilterPeriod);
        panel.add(new JLabel("Phòng/khách:"));
        panel.add(txtFilterRoom);
        panel.add(new JLabel("Trạng thái:"));
        panel.add(cboFilterStatus);
        panel.add(btnApply);
        panel.add(btnClear);
        return panel;
    }

    private void applyFilters() {
        String period = txtFilterPeriod.getText() == null ? "" : txtFilterPeriod.getText().trim().toLowerCase();
        String room = txtFilterRoom.getText() == null ? "" : txtFilterRoom.getText().trim().toLowerCase();
        String status = cboFilterStatus.getSelectedItem() == null ? "Tất cả trạng thái" : cboFilterStatus.getSelectedItem().toString();

        List<Invoice> filtered = new ArrayList<>();
        for (Invoice invoice : allInvoices) {
            boolean ok = true;
            if (!period.isEmpty()) {
                ok = invoice.getPeriod() != null && invoice.getPeriod().toLowerCase().contains(period);
            }
            if (ok && !room.isEmpty()) {
                String roomText = invoice.getRoom() == null ? "" : ((invoice.getRoom().getCode() == null ? "" : invoice.getRoom().getCode()) + " " + (invoice.getRoom().getTenantName() == null ? "" : invoice.getRoom().getTenantName())).toLowerCase();
                ok = roomText.contains(room);
            }
            if (ok && !"Tất cả trạng thái".equals(status)) {
                ok = invoice.getStatus() != null && status.equalsIgnoreCase(invoice.getStatus().getDisplayName());
            }
            if (ok) {
                filtered.add(invoice);
            }
        }
        tableModel.setData(filtered);
        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
        updateSelectionInfo();
    }

    private void configureTable() {
        table.setRowHeight(34);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(AppColors.BORDER);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(238, 242, 255));
        table.setSelectionForeground(AppColors.TEXT);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(AppColors.TEXT);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(center);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectionInfo();
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col != 6) return;
                java.awt.Rectangle rect = table.getCellRect(row, col, true);
                int relativeX = e.getX() - rect.x;
                int part = Math.max(1, rect.width / 3);
                table.setRowSelectionInterval(row, row);
                if (relativeX < part) {
                    showSelectedDetail();
                } else if (relativeX < part * 2) {
                    markSelectedPaid();
                } else {
                    deleteSelected();
                }
            }
        });
    }

    private void reloadData() {
        allInvoices = hoaDonService.getAllInvoices();
        applyFilters();

        int pending = 0;
        double total = 0;
        for (Invoice invoice : allInvoices) {
            total += invoice.getTotal();
            if (invoice.getStatus() != null && "Chờ thanh toán".equals(invoice.getStatus().getDisplayName())) {
                pending++;
            }
        }
        totalCard.setValue(String.valueOf(allInvoices.size()));
        revenueCard.setValue(CurrencyUtils.formatMoney(total) + " VNĐ");
        pendingCard.setValue(String.valueOf(pending));
        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
        updateSelectionInfo();
    }

    private void updateSelectionInfo() {
        int row = table.getSelectedRow();
        if (row < 0) {
            selectedPeriodLabel.setText("Kỳ thanh toán: --");
            selectedDueLabel.setText("Hạn thanh toán: --");
            return;
        }
        Invoice invoice = tableModel.getInvoiceAt(table.convertRowIndexToModel(row));
        if (invoice == null) {
            selectedPeriodLabel.setText("Kỳ thanh toán: --");
            selectedDueLabel.setText("Hạn thanh toán: --");
            return;
        }
        selectedPeriodLabel.setText("Kỳ thanh toán: " + (invoice.getPeriod() == null ? "--" : invoice.getPeriod()));
        selectedDueLabel.setText("Hạn thanh toán: " + (invoice.getDueDate() == null ? "--" : com.team.invoice.util.DateUtils.formatDate(invoice.getDueDate())));
    }

    private void openCreateDialog() {
        List<Room> rooms = hoaDonService.getRoomsForInvoice();
        if (rooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có phòng đang thuê hoặc chưa thiết lập bảng giá đang áp dụng.");
            return;
        }
        java.awt.Window win = javax.swing.SwingUtilities.getWindowAncestor(this);
        CreateInvoiceDialog dialog = new CreateInvoiceDialog(win instanceof java.awt.Frame ? (java.awt.Frame) win : null, rooms);
        dialog.getBtnCalculate().addActionListener(e -> {
            try {
                dialog.showCalculatedInvoice(hoaDonService.calculate(dialog.getFormData()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.getBtnDraft().addActionListener(e -> saveFromDialog(dialog, false));
        dialog.getBtnIssue().addActionListener(e -> {
            if (dialog.confirmIssue()) {
                saveFromDialog(dialog, true);
            }
        });
        dialog.getBtnClose().addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void saveFromDialog(CreateInvoiceDialog dialog, boolean issueNow) {
        try {
            Invoice invoice = dialog.getCalculatedInvoice();
            if (invoice == null) {
                invoice = hoaDonService.calculate(dialog.getFormData());
                dialog.showCalculatedInvoice(invoice);
            }
            hoaDonService.saveInvoice(invoice, issueNow);
            JOptionPane.showMessageDialog(dialog, issueNow ? "Đã phát hành hóa đơn." : "Đã lưu bản nháp hóa đơn.");
            dialog.dispose();
            reloadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Không thể lưu hóa đơn", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Invoice getSelectedInvoice() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn trong bảng.");
            return null;
        }
        return tableModel.getInvoiceAt(table.convertRowIndexToModel(row));
    }

    private void showSelectedDetail() {
        Invoice invoice = getSelectedInvoice();
        if (invoice == null) return;
        List<String> details = hoaDonService.getInvoiceDetails(invoice.getCode());
        InvoiceDetailDialog dialog = new InvoiceDetailDialog(this, invoice, details);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            try {
                hoaDonService.updateInvoiceMeta(invoice.getCode(), dialog.getUpdatedPeriod(), dialog.getUpdatedDueDate(), dialog.getUpdatedNote());
                JOptionPane.showMessageDialog(this, "Đã cập nhật kỳ hóa đơn và hạn thanh toán.");
                reloadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Không thể cập nhật hóa đơn", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void markSelectedPaid() {
        Invoice invoice = getSelectedInvoice();
        if (invoice == null) return;
        if (hoaDonService.markPaid(invoice.getCode())) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái thanh toán.");
            reloadData();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể cập nhật trạng thái.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        Invoice invoice = getSelectedInvoice();
        if (invoice == null) return;
        int choice = JOptionPane.showConfirmDialog(this, "Xóa mềm hóa đơn " + invoice.getCode() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            if (hoaDonService.deleteInvoice(invoice.getCode())) {
                JOptionPane.showMessageDialog(this, "Đã xóa mềm hóa đơn.");
                reloadData();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa hóa đơn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            return label;
        }
    }

    private static class StatCard extends RoundedPanel {
        private final JLabel valueLabel = new JLabel("0");

        public StatCard(String title) {
            super(24, AppColors.WHITE, AppColors.BORDER);
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(220, 96));
            setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            titleLabel.setForeground(AppColors.MUTED);

            valueLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
            valueLabel.setForeground(AppColors.TEXT);

            add(titleLabel, BorderLayout.NORTH);
            add(valueLabel, BorderLayout.CENTER);
        }

        public void setValue(String value) {
            valueLabel.setText(value);
        }
    }
}
