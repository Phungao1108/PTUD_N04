package com.team.invoice.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.team.invoice.ui.components.HintTextField;
import com.team.invoice.ui.components.RoundedButton;
import com.team.invoice.ui.components.RoundedPanel;

import store.AppStore;
import store.AppStore.Contract;
import store.AppStore.DataListener;
import store.AppStore.Room;

public class PhongUI extends JPanel implements DataListener {
    private final AppStore store = AppStore.getInstance();
    private DefaultTableModel model;
    private JTable table;
    private HintTextField txtSearch;
    private JComboBox<String> cboStatus;
    private TableRowSorter<DefaultTableModel> sorter;

    public PhongUI() {
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        UITheme.stylePage(this);
        add(UITheme.createHeader(
                "Quản lý phòng & đơn giá",
                "Theo dõi trạng thái phòng, cập nhật giá dịch vụ và ghi chỉ số điện nước theo cùng giao diện chuẩn.",
                UITheme.actionBar(createPriceButton(), createMeterButton(), createDetailButton())));
        add(UITheme.vspace(20));
        add(buildStats());
        add(UITheme.vspace(20));
        add(buildTableCard());
        store.addListener(this);
        refreshTable();
    }

    private RoundedButton createPriceButton() {
        RoundedButton button = UITheme.secondaryButton("Đơn giá");
        button.addActionListener(e -> updatePricing());
        return button;
    }

    private RoundedButton createMeterButton() {
        RoundedButton button = UITheme.primaryButton("Ghi chỉ số");
        button.setPreferredSize(new Dimension(130, 42));
        button.addActionListener(e -> recordMeter());
        return button;
    }

    private RoundedButton createDetailButton() {
        RoundedButton button = UITheme.secondaryButton("Chi tiết");
        button.addActionListener(e -> showDetail());
        return button;
    }

    private JPanel buildStats() {
        int total = store.getRooms().size();
        int occupied = 0;
        for (int i = 0; i < store.getRooms().size(); i++) if ("DANG_O".equals(store.getRooms().get(i).getTrangThaiPhong())) occupied++;
        int empty = total - occupied;
        JPanel panel = new JPanel(new GridLayout(1, 3, 16, 0));
        panel.setOpaque(false);
        panel.add(UITheme.createStatCard("Tổng số phòng", String.valueOf(total), "Quản lý toàn bộ phòng trong tòa nhà", UITheme.TEXT));
        panel.add(UITheme.createStatCard("Đang ở", String.valueOf(occupied), "Có hợp đồng hiệu lực", UITheme.SUCCESS));
        panel.add(UITheme.createStatCard("Còn trống", String.valueOf(empty), "Sẵn sàng lập hợp đồng mới", UITheme.WARNING));
        return panel;
    }

    private RoundedPanel buildTableCard() {
        RoundedPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 16));

        JPanel filterBar = new JPanel(new BorderLayout(12, 0));
        filterBar.setOpaque(false);
        txtSearch = UITheme.searchField("Tìm theo mã phòng, tên phòng, loại phòng hoặc khách hiện tại...", 560);
        txtSearch.addActionListener(e -> applyFilter());
        cboStatus = new JComboBox<String>(new String[]{"Tất cả trạng thái", "TRONG", "DANG_O"});
        UITheme.styleCombo(cboStatus, 180);
        cboStatus.addActionListener(e -> applyFilter());
        RoundedButton btnFilter = UITheme.secondaryButton("Lọc");
        btnFilter.addActionListener(e -> applyFilter());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(cboStatus);
        right.add(btnFilter);

        filterBar.add(UITheme.wrapField(txtSearch), BorderLayout.CENTER);
        filterBar.add(right, BorderLayout.EAST);

        model = new DefaultTableModel(new String[]{"Mã phòng","Tên phòng","Loại","Trạng thái","Khách hiện tại","Giá tháng","Điện","Nước","Dịch vụ","Kỳ chỉ số"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);
        table.getColumnModel().getColumn(3).setCellRenderer(new RoomStatusRenderer());

        card.add(filterBar, BorderLayout.NORTH);
        card.add(UITheme.wrapTable(table), BorderLayout.CENTER);
        return card;
    }

    private Room getSelectedRoom() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        int modelRow = table.convertRowIndexToModel(row);
        return store.getRoom(String.valueOf(model.getValueAt(modelRow, 0)));
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Room> rooms = store.getRooms();
        for (int i = 0; i < rooms.size(); i++) addRow(rooms.get(i));
        applyFilter();
    }

    private void addRow(Room room) {
        model.addRow(new Object[]{room.getId(), room.getTen(), room.getMaLoaiPhong(), room.getTrangThaiPhong(), store.getTenantNameForRoom(room.getId()), AppStore.money(room.getGiaTheoThang()), AppStore.money(room.getGiaDien()), AppStore.money(room.getGiaNuoc()), AppStore.money(room.getPhiDichVu()), room.getKyChiSoGanNhat()});
    }

    private void applyFilter() {
        final String keyword = txtSearch == null ? "" : txtSearch.getText().trim().toLowerCase();
        final String status = cboStatus == null ? "Tất cả trạng thái" : String.valueOf(cboStatus.getSelectedItem());
        sorter.setRowFilter(new RowFilter<DefaultTableModel, Object>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                boolean matchesKeyword = keyword.isEmpty();
                if (!matchesKeyword) {
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        Object value = entry.getValue(i);
                        if (value != null && value.toString().toLowerCase().contains(keyword)) {
                            matchesKeyword = true;
                            break;
                        }
                    }
                }
                boolean matchesStatus = "Tất cả trạng thái".equals(status) || status.equals(String.valueOf(entry.getValue(3)));
                return matchesKeyword && matchesStatus;
            }
        });
    }

    private void updatePricing() {
        Room room = getSelectedRoom();
        if (room == null) { JOptionPane.showMessageDialog(this, "Hãy chọn phòng."); return; }
        JTextField txtRent = new JTextField(String.valueOf((long) room.getGiaTheoThang()));
        JTextField txtElectric = new JTextField(String.valueOf((long) room.getGiaDien()));
        JTextField txtWater = new JTextField(String.valueOf((long) room.getGiaNuoc()));
        JTextField txtService = new JTextField(String.valueOf((long) room.getPhiDichVu()));
        UITheme.styleTextField(txtRent); UITheme.styleTextField(txtElectric); UITheme.styleTextField(txtWater); UITheme.styleTextField(txtService);
        JPanel form = new JPanel(new GridLayout(0,2,10,10));
        form.setOpaque(false);
        form.add(new JLabel("Giá phòng/tháng")); form.add(txtRent);
        form.add(new JLabel("Giá điện")); form.add(txtElectric);
        form.add(new JLabel("Giá nước")); form.add(txtWater);
        form.add(new JLabel("Phí dịch vụ")); form.add(txtService);
        int ok = JOptionPane.showConfirmDialog(this, form, "Cập nhật đơn giá - " + room.getId(), JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                store.updateRoomPricing(room.getId(), Double.parseDouble(txtRent.getText().trim()), Double.parseDouble(txtElectric.getText().trim()), Double.parseDouble(txtWater.getText().trim()), Double.parseDouble(txtService.getText().trim()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void recordMeter() {
        Room room = getSelectedRoom();
        if (room == null) { JOptionPane.showMessageDialog(this, "Hãy chọn phòng."); return; }
        JTextField txtKy = new JTextField(room.getKyChiSoGanNhat());
        JTextField txtDien = new JTextField(String.valueOf(room.getChiSoDienCu()));
        JTextField txtNuoc = new JTextField(String.valueOf(room.getChiSoNuocCu()));
        UITheme.styleTextField(txtKy); UITheme.styleTextField(txtDien); UITheme.styleTextField(txtNuoc);
        JPanel form = new JPanel(new GridLayout(0,2,10,10));
        form.setOpaque(false);
        form.add(new JLabel("Kỳ ghi chỉ số")); form.add(txtKy);
        form.add(new JLabel("Chỉ số điện mới")); form.add(txtDien);
        form.add(new JLabel("Chỉ số nước mới")); form.add(txtNuoc);
        int ok = JOptionPane.showConfirmDialog(this, form, "Ghi chỉ số - " + room.getId(), JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                store.recordMeters(room.getId(), txtKy.getText().trim(), Integer.parseInt(txtDien.getText().trim()), Integer.parseInt(txtNuoc.getText().trim()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDetail() {
        Room room = getSelectedRoom();
        if (room == null) { JOptionPane.showMessageDialog(this, "Hãy chọn phòng."); return; }
        Contract c = store.getActiveContractForRoom(room.getId());
        JTextArea area = new JTextArea();
        UITheme.styleTextArea(area);
        area.setEditable(false);
        area.setText("Mã phòng: " + room.getId() + "\n"
                + "Tên phòng: " + room.getTen() + "\n"
                + "Loại phòng: " + room.getMaLoaiPhong() + "\n"
                + "Trạng thái: " + room.getTrangThaiPhong() + "\n"
                + "Khách hiện tại: " + store.getTenantNameForRoom(room.getId()) + "\n"
                + "Hợp đồng hiệu lực: " + (c == null ? "Không có" : c.getMaHopDong()) + "\n"
                + "Giá phòng: " + AppStore.money(room.getGiaTheoThang()) + "\n"
                + "Giá điện: " + AppStore.money(room.getGiaDien()) + "\n"
                + "Giá nước: " + AppStore.money(room.getGiaNuoc()) + "\n"
                + "Phí dịch vụ: " + AppStore.money(room.getPhiDichVu()) + "\n"
                + "Chỉ số điện cũ: " + room.getChiSoDienCu() + "\n"
                + "Chỉ số nước cũ: " + room.getChiSoNuocCu());
        JOptionPane.showMessageDialog(this, new javax.swing.JScrollPane(area), "Chi tiết phòng", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override public void onDataChanged() { refreshTable(); }

    private static class RoomStatusRenderer extends DefaultTableCellRenderer {
        @Override
        protected void setValue(Object value) {
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(getFont().deriveFont(Font.BOLD, 12f));
            if ("DANG_O".equals(value)) {
                setForeground(UITheme.SUCCESS);
                setText("ĐANG Ở");
            } else {
                setForeground(UITheme.WARNING);
                setText("TRỐNG");
            }
        }
    }
}
