package com.team.invoice.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.components.TableActionCell;
import com.team.invoice.components.UITheme;
import com.team.invoice.entity.BangGia;
import com.team.invoice.entity.DonGiaDichVu;
import com.team.invoice.entity.DonGiaPhong;
import com.team.invoice.service.BangGiaService;
import com.team.invoice.util.CurrencyUtils;

public class DonGiaUI extends JPanel {

    private JTable table;
    private DefaultTableModel roomPriceModel;
    private DefaultTableModel servicePriceModel;
    
    private JComboBox<String> cboLoaiGia;
    private JComboBox<String> cboBangGia;
    
    private BangGiaService bangGiaService;

    public DonGiaUI() {
        bangGiaService = new BangGiaService();
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        UITheme.stylePage(this);
        
        add(UITheme.createHeader(
                "Quản lý Chi Tiết Đơn Giá",
                "Xem, sửa và cập nhật mức giá chi tiết của từng loại phòng và dịch vụ theo các đợt bảng giá.",
                UITheme.actionBar(createSyncButton())));
        
        add(UITheme.vspace(20));
        add(buildTableCard());
        
        loadComboBoxBangGia();
    }

    private RoundedButton createSyncButton() {
        RoundedButton button = UITheme.primaryButton("Đồng bộ Bổ sung");
        button.addActionListener(e -> {
            String selectedMaBG = (String) cboBangGia.getSelectedItem();
            if (selectedMaBG == null) return;
            
            String msg = bangGiaService.boSungDonGiaThieu(selectedMaBG);
            JOptionPane.showMessageDialog(this, msg);
            refreshTable();
        });
        return button;
    }

    private RoundedPanel buildTableCard() {
        RoundedPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 16));

        // Đã bổ sung cột "Mã" để làm key update vào Database
        roomPriceModel = new DefaultTableModel(new String[]{"Mã Đợt", "Mã LP", "Tên Loại Phòng", "Giá Theo Tháng (VNĐ)", "Hành Động"}, 0) {
            public boolean isCellEditable(int row, int column) { return column == 4; }
        };
        
        servicePriceModel = new DefaultTableModel(new String[]{"Mã Đợt", "Mã DV", "Tên Dịch Vụ", "Đơn Vị", "Đơn Giá (VNĐ)", "Hành Động"}, 0) {
            public boolean isCellEditable(int row, int column) { return column == 5; }
        };

        table = new JTable();
        UITheme.styleTable(table);
        table.setRowHeight(40);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterBar.setOpaque(false);
        
        cboBangGia = new JComboBox<>();
        UITheme.styleCombo(cboBangGia, 250);
        cboBangGia.addActionListener(e -> switchViewModel());

        cboLoaiGia = new JComboBox<>(new String[] {"Giá Phòng", "Giá Dịch Vụ"});
        UITheme.styleCombo(cboLoaiGia, 150);
        cboLoaiGia.addActionListener(e -> switchViewModel());

        filterBar.add(new JLabel("Chọn Đợt Áp Dụng: "));
        filterBar.add(cboBangGia);
        filterBar.add(new JLabel("   Xem lưới: "));
        filterBar.add(cboLoaiGia);

        card.add(filterBar, BorderLayout.NORTH);
        card.add(UITheme.wrapTable(table), BorderLayout.CENTER);
        return card;
    }

    private void loadComboBoxBangGia() {
        cboBangGia.removeAllItems();
        List<BangGia> listBangGia = bangGiaService.getAllBangGia();
        if (listBangGia != null && !listBangGia.isEmpty()) {
            for (BangGia bg : listBangGia) {
                cboBangGia.addItem(bg.getMaBG());
            }
            switchViewModel(); 
        }
    }

    private void switchViewModel() {
        if (cboBangGia.getSelectedItem() == null) return;
        
        String selectedType = (String) cboLoaiGia.getSelectedItem();
        
        if ("Giá Phòng".equals(selectedType)) {
            table.setModel(roomPriceModel);
            table.getColumnModel().getColumn(4).setCellRenderer(new TableActionCell.Renderer());
            table.getColumnModel().getColumn(4).setCellEditor(new TableActionCell(createTableActionEvents("PHONG")));
        } else {
            table.setModel(servicePriceModel);
            table.getColumnModel().getColumn(5).setCellRenderer(new TableActionCell.Renderer());
            table.getColumnModel().getColumn(5).setCellEditor(new TableActionCell(createTableActionEvents("DICH_VU")));
        }
        
        refreshTable();
    }

    private TableActionCell.TableActionEvent createTableActionEvents(String type) {
        return new TableActionCell.TableActionEvent() {
            @Override
            public void onEdit(int row) {
                String maBG = table.getValueAt(row, 0).toString();
                String maID = table.getValueAt(row, 1).toString(); // Mã LP hoặc Mã DV
                String ten = table.getValueAt(row, 2).toString();
                String oldPriceStr = table.getValueAt(row, type.equals("PHONG") ? 3 : 4).toString();
                
                // Mở InputDialog để nhập giá mới
                String newPriceStr = JOptionPane.showInputDialog(DonGiaUI.this, 
                        "Nhập số tiền mới cho: " + ten, oldPriceStr.replace(",", ""));
                
                if (newPriceStr != null && !newPriceStr.trim().isEmpty()) {
                    try {
                        double giaMoi = Double.parseDouble(newPriceStr.trim());
                        String msg;
                        if (type.equals("PHONG")) {
                            msg = bangGiaService.updateGiaPhong(maBG, maID, giaMoi);
                        } else {
                            msg = bangGiaService.updateGiaDichVu(maBG, maID, giaMoi);
                        }
                        
                        JOptionPane.showMessageDialog(DonGiaUI.this, msg);
                        if (msg.equals("Thành công")) refreshTable();
                        
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(DonGiaUI.this, "Vui lòng nhập định dạng số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            @Override
            public void onDelete(int row) {
                 JOptionPane.showMessageDialog(DonGiaUI.this, "Đơn giá liên kết cứng với hệ thống.\nVui lòng sét giá = 0 nếu không sử dụng!");
            }
        };
    }

    private void refreshTable() {
        roomPriceModel.setRowCount(0);
        servicePriceModel.setRowCount(0);

        String selectedMaBG = (String) cboBangGia.getSelectedItem();
        if (selectedMaBG == null) return;

        List<DonGiaPhong> roomPrices = bangGiaService.getDonGiaPhongByBG(selectedMaBG);
        if (roomPrices != null) {
            for (DonGiaPhong r : roomPrices) {
                // Mã Đợt, Mã LP, Tên LP, Giá
                roomPriceModel.addRow(new Object[]{r.getMaBG(), r.getMaLoaiPhong(), r.getTenLoaiPhong(), CurrencyUtils.formatMoney(r.getGiaTheoThang()), ""});
            }
        }

        List<DonGiaDichVu> servicePrices = bangGiaService.getDonGiaDichVuByBG(selectedMaBG);
        if (servicePrices != null) {
            for (DonGiaDichVu s : servicePrices) {
                // Mã Đợt, Mã DV, Tên DV, Đơn vị, Giá
                servicePriceModel.addRow(new Object[]{s.getMaBG(), s.getMaDV(), s.getTenDV(), s.getDonVi(), CurrencyUtils.formatMoney(s.getGia()), ""});
            }
        }
    }
    private RoundedButton createAddButton() {
        RoundedButton button = UITheme.primaryButton("Thêm Đơn Giá");
        button.addActionListener(e -> {
            String selectedMaBG = (String) cboBangGia.getSelectedItem();
            if (selectedMaBG == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một đợt giá trước!");
                return;
            }
            
            java.awt.Frame parentFrame = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
            com.team.invoice.dialog.ThemDonGiaDialog dialog = 
                new com.team.invoice.dialog.ThemDonGiaDialog(parentFrame, selectedMaBG, bangGiaService, () -> {
                    refreshTable(); // Load lại lưới khi thêm xong
            });
            dialog.setVisible(true);
        });
        return button;
    }
}