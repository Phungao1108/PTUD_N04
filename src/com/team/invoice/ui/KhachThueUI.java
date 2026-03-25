package com.team.invoice.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.team.invoice.ui.components.HintTextField;
import com.team.invoice.ui.components.RoundedButton;
import com.team.invoice.ui.components.RoundedPanel;

import store.AppStore;
import store.AppStore.Contract;
import store.AppStore.DataListener;
import store.AppStore.Tenant;

public class KhachThueUI extends JPanel implements DataListener {
    private final AppStore store = AppStore.getInstance();
    private DefaultTableModel model;
    private JTable table;
    private HintTextField txtSearch;
    private JComboBox<String> cboStatus;
    private TableRowSorter<DefaultTableModel> sorter;

    public KhachThueUI() {
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        UITheme.stylePage(this);
        add(UITheme.createHeader(
                "Quản lý khách thuê",
                "Khách thuê được tạo tự động từ màn lập hợp đồng. Tại đây bạn chỉ chỉnh sửa, xóa và tra cứu hồ sơ.",
                UITheme.actionBar(createEditButton(), createDeleteButton())));
        add(UITheme.vspace(20));
        add(buildStats());
        add(UITheme.vspace(20));
        add(buildTableCard());
        store.addListener(this);
        refreshTable();
    }

    private RoundedButton createEditButton() {
        RoundedButton button = UITheme.secondaryButton("Sửa");
        button.addActionListener(e -> editSelected());
        return button;
    }

    private RoundedButton createDeleteButton() {
        RoundedButton button = UITheme.secondaryButton("Xóa");
        button.addActionListener(e -> deleteSelected());
        return button;
    }

    private JPanel buildStats() {
        int total = store.getTenants().size();
        int active = 0;
        int waiting = 0;
        for (int i = 0; i < store.getTenants().size(); i++) {
            if ("DANG_THUE".equals(store.getTenants().get(i).getTrangThai())) active++; else waiting++;
        }
        JPanel panel = new JPanel(new GridLayout(1, 3, 16, 0));
        panel.setOpaque(false);
        panel.add(UITheme.createStatCard("Tổng khách", String.valueOf(total), "Tất cả hồ sơ khách thuê", UITheme.TEXT));
        panel.add(UITheme.createStatCard("Đang thuê", String.valueOf(active), "Đang gắn với hợp đồng hiệu lực", UITheme.SUCCESS));
        panel.add(UITheme.createStatCard("Chờ thuê", String.valueOf(waiting), "Có thể lập hợp đồng mới", UITheme.WARNING));
        return panel;
    }

    private RoundedPanel buildTableCard() {
        RoundedPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 16));

        JPanel filterBar = new JPanel(new BorderLayout(12, 0));
        filterBar.setOpaque(false);
        txtSearch = UITheme.searchField("Tìm theo tên, CCCD, số điện thoại hoặc phòng...", 540);
        txtSearch.addActionListener(e -> applyFilter());
        RoundedPanel searchWrap = UITheme.wrapField(txtSearch);

        cboStatus = new JComboBox<String>(new String[] {"Tất cả trạng thái", "DANG_THUE", "CHO_THUE"});
        UITheme.styleCombo(cboStatus, 180);
        cboStatus.addActionListener(e -> applyFilter());

        RoundedButton btnSearch = UITheme.secondaryButton("Lọc");
        btnSearch.addActionListener(e -> applyFilter());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(cboStatus);
        right.add(btnSearch);

        filterBar.add(searchWrap, BorderLayout.CENTER);
        filterBar.add(right, BorderLayout.EAST);

        model = new DefaultTableModel(new String[]{"Mã khách", "Họ tên", "CCCD", "SĐT", "Phòng", "Trạng thái", "Hợp đồng hiệu lực"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        card.add(filterBar, BorderLayout.NORTH);
        card.add(UITheme.wrapTable(table), BorderLayout.CENTER);
        return card;
    }

    private void applyFilter() {
        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().trim();
        String status = cboStatus == null ? "Tất cả trạng thái" : String.valueOf(cboStatus.getSelectedItem());
        if (sorter == null) return;
        RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                boolean matchesKeyword = keyword.isEmpty();
                if (!matchesKeyword) {
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        Object value = entry.getValue(i);
                        if (value != null && value.toString().toLowerCase().contains(keyword.toLowerCase())) {
                            matchesKeyword = true;
                            break;
                        }
                    }
                }
                boolean matchesStatus = "Tất cả trạng thái".equals(status) || status.equals(String.valueOf(entry.getValue(5)));
                return matchesKeyword && matchesStatus;
            }
        };
        sorter.setRowFilter(filter);
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Tenant> tenants = store.getTenants();
        for (int i = 0; i < tenants.size(); i++) {
            Tenant t = tenants.get(i);
            String contractCode = "-";
            List<Contract> contracts = store.getContracts();
            for (int j = 0; j < contracts.size(); j++) {
                Contract c = contracts.get(j);
                if (c.getMaKhachChinh().equals(t.getMaKhach()) && "HIEU_LUC".equals(c.getTrangThai())) {
                    contractCode = c.getMaHopDong();
                    break;
                }
            }
            model.addRow(new Object[]{t.getMaKhach(), t.getHoTen(), t.getSoCCCD(), t.getSdt(), t.getMaPhongDangO() == null ? "-" : t.getMaPhongDangO(), t.getTrangThai(), contractCode});
        }
        applyFilter();
    }

    private void showTenantDialog(Tenant tenant) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), tenant == null ? "Thêm khách thuê" : "Cập nhật khách thuê", JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(new Dimension(500, 320));
        dialog.setLocationRelativeTo(this);

        RoundedPanel body = UITheme.createCard();
        body.setLayout(new BorderLayout(0, 16));
        body.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setOpaque(false);
        JTextField txtName = new JTextField(tenant == null ? "" : tenant.getHoTen());
        JTextField txtCccd = new JTextField(tenant == null ? "" : tenant.getSoCCCD());
        JTextField txtPhone = new JTextField(tenant == null ? "" : tenant.getSdt());
        UITheme.styleTextField(txtName);
        UITheme.styleTextField(txtCccd);
        UITheme.styleTextField(txtPhone);
        form.add(new JLabel("Họ tên")); form.add(txtName);
        form.add(new JLabel("CCCD")); form.add(txtCccd);
        form.add(new JLabel("Số điện thoại")); form.add(txtPhone);

        JPanel bottom = UITheme.actionBar(UITheme.secondaryButton("Hủy"), UITheme.primaryButton(tenant == null ? "Lưu khách" : "Cập nhật"));
        ((RoundedButton) bottom.getComponent(0)).addActionListener(e -> dialog.dispose());
        ((RoundedButton) bottom.getComponent(1)).addActionListener(e -> {
            try {
                if (tenant == null) store.addTenant(txtName.getText(), txtCccd.getText(), txtPhone.getText());
                else store.updateTenant(tenant.getMaKhach(), txtName.getText(), txtCccd.getText(), txtPhone.getText());
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        body.add(UITheme.createHeader(tenant == null ? "Thông tin khách thuê mới" : "Cập nhật khách thuê", "Dữ liệu khách sẽ được dùng khi lập hợp đồng và hóa đơn.", null), BorderLayout.NORTH);
        body.add(form, BorderLayout.CENTER);
        body.add(bottom, BorderLayout.SOUTH);
        dialog.setContentPane(body);
        dialog.setVisible(true);
    }

    private Tenant getSelectedTenant() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        int modelRow = table.convertRowIndexToModel(row);
        return store.getTenant(String.valueOf(model.getValueAt(modelRow, 0)));
    }

    private void editSelected() {
        Tenant tenant = getSelectedTenant();
        if (tenant == null) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một khách thuê.");
            return;
        }
        showTenantDialog(tenant);
    }

    private void deleteSelected() {
        Tenant tenant = getSelectedTenant();
        if (tenant == null) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một khách thuê.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa khách " + tenant.getHoTen() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                store.removeTenant(tenant.getMaKhach());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void onDataChanged() {
        refreshTable();
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        protected void setValue(Object value) {
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(getFont().deriveFont(Font.BOLD, 12f));
            if ("DANG_THUE".equals(value)) {
                setForeground(UITheme.SUCCESS);
                setText("ĐANG THUÊ");
            } else {
                setForeground(UITheme.WARNING);
                setText("CHỜ THUÊ");
            }
        }
    }
}
