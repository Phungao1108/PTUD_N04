package com.team.invoice.ui;

import com.team.invoice.components.HintTextField;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.components.TableActionCell;
import com.team.invoice.components.UITheme;
import com.team.invoice.dao.HopDongDAO;
import com.team.invoice.dao.KhachThueDAO;
import com.team.invoice.entity.HopDong;
import com.team.invoice.entity.KhachThue;
import com.team.invoice.service.HopDongService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class HopDongUI extends JPanel {
    private final HopDongDAO hopDongDAO = new HopDongDAO();
    private final HopDongService hopDongService = new HopDongService();
    private final KhachThueDAO khachThueDAO = new KhachThueDAO();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã HĐ", "Phòng", "Khách chính", "Ngày bắt đầu", "Ngày kết thúc", "Tiền cọc", "Trạng thái", "Hành động"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 7;
        }
    };

    private final JTable table = new JTable(model);
    private final HintTextField txtSearch = new HintTextField("Tìm theo mã hợp đồng / phòng / khách chính...");
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    private List<Object[]> currentList;

    public HopDongUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        UITheme.stylePage(this);

        add(UITheme.createHeader(
                "Quản lý Hợp đồng",
                "Thêm, sửa, xóa mềm và tra cứu hợp đồng thuê phòng.",
                UITheme.actionBar(createAddButton())
        ));
        add(UITheme.vspace(20));
        add(buildTableCard());

        addFilterEvents();
        reloadData();
    }

    private RoundedButton createAddButton() {
        RoundedButton btn = UITheme.primaryButton("Thêm Hợp đồng");
        btn.addActionListener(e -> showHopDongDialog(null));
        return btn;
    }

    private RoundedPanel buildTableCard() {
        RoundedPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 16));

        JPanel filterBar = new JPanel(new BorderLayout());
        filterBar.setOpaque(false);
        filterBar.add(UITheme.wrapField(txtSearch), BorderLayout.CENTER);

        UITheme.styleTable(table);
        table.setRowHeight(38);
        table.setRowSorter(sorter);

        table.getColumnModel().getColumn(7).setPreferredWidth(140);
        table.getColumnModel().getColumn(7).setMinWidth(140);
        table.getColumnModel().getColumn(7).setMaxWidth(160);

        table.getColumnModel().getColumn(7).setCellRenderer(new TableActionCell.Renderer());
        table.getColumnModel().getColumn(7).setCellEditor(new TableActionCell(new TableActionCell.TableActionEvent() {
            @Override
            public void onEdit(int row) {
                int modelRow = table.convertRowIndexToModel(row);
                Object[] data = currentList.get(modelRow);
                showHopDongDialog(data);
            }

            @Override
            public void onDelete(int row) {
                int modelRow = table.convertRowIndexToModel(row);
                Object[] data = currentList.get(modelRow);
                String maHopDong = String.valueOf(data[0]);

                int confirm = JOptionPane.showConfirmDialog(
                        HopDongUI.this,
                        "Bạn có chắc muốn xóa hợp đồng " + maHopDong + "?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean ok = hopDongService.xoaHopDong(maHopDong);
                        JOptionPane.showMessageDialog(HopDongUI.this, ok ? "Thành công" : "Không thể xóa hợp đồng");
                        reloadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(HopDongUI.this, "Lỗi xóa hợp đồng: " + ex.getMessage());
                    }
                }
            }
        }));

        card.add(filterBar, BorderLayout.NORTH);
        card.add(UITheme.wrapTable(table), BorderLayout.CENTER);
        return card;
    }

    public void reloadData() {
        try {
            currentList = hopDongDAO.findAll();
            model.setRowCount(0);
            for (Object[] row : currentList) {
                model.addRow(new Object[]{
                        row[0], row[1], row[2], row[3], row[4], row[5], row[6], ""
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải hợp đồng: " + ex.getMessage());
        }
    }

    private void addFilterEvents() {
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
    }

    private void applyFilter() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                for (int i = 0; i < 3; i++) {
                    String value = entry.getStringValue(i);
                    if (value != null && value.toLowerCase().contains(keyword)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void showHopDongDialog(Object[] rowData) {
        boolean isEdit = rowData != null;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setTitle(isEdit ? "Cập nhật Hợp đồng" : "Thêm Hợp đồng");
        dialog.setSize(560, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        form.setBackground(Color.WHITE);

        JTextField txtMaHopDong = new JTextField();
        JComboBox<String> cboPhong = new JComboBox<>();
        JComboBox<String> cboKhach = new JComboBox<>();
        JTextField txtNgayBatDau = new JTextField();
        JTextField txtNgayKetThuc = new JTextField();
        JTextField txtTienDatCoc = new JTextField();
        JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{"HIEU_LUC", "HET_HAN", "HUY"});

        UITheme.styleTextField(txtMaHopDong);
        UITheme.styleTextField(txtNgayBatDau);
        UITheme.styleTextField(txtNgayKetThuc);
        UITheme.styleTextField(txtTienDatCoc);
        UITheme.styleCombo(cboPhong, 250);
        UITheme.styleCombo(cboKhach, 250);
        UITheme.styleCombo(cboTrangThai, 250);

        loadPhongToComboBox(cboPhong, isEdit ? String.valueOf(rowData[1]) : null, isEdit ? String.valueOf(rowData[0]) : null);
        loadKhachToComboBox(cboKhach);

        if (isEdit) {
            txtMaHopDong.setText(String.valueOf(rowData[0]));
            txtMaHopDong.setEditable(false);
            txtNgayBatDau.setText(String.valueOf(rowData[3]));
            txtNgayKetThuc.setText(String.valueOf(rowData[4]));
            txtTienDatCoc.setText(String.valueOf(rowData[5]));
            cboTrangThai.setSelectedItem(String.valueOf(rowData[6]));
            selectKhachByName(cboKhach, String.valueOf(rowData[2]));
        } else {
            txtMaHopDong.setText("HD" + System.currentTimeMillis());
        }

        form.add(new JLabel("Mã hợp đồng:"));
        form.add(txtMaHopDong);
        form.add(new JLabel("Phòng:"));
        form.add(cboPhong);
        form.add(new JLabel("Khách chính:"));
        form.add(cboKhach);
        form.add(new JLabel("Ngày bắt đầu (yyyy-mm-dd):"));
        form.add(txtNgayBatDau);
        form.add(new JLabel("Ngày kết thúc (yyyy-mm-dd):"));
        form.add(txtNgayKetThuc);
        form.add(new JLabel("Tiền đặt cọc:"));
        form.add(txtTienDatCoc);
        form.add(new JLabel("Trạng thái:"));
        form.add(cboTrangThai);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBackground(Color.WHITE);

        RoundedButton btnCancel = UITheme.secondaryButton("Hủy");
        RoundedButton btnSave = UITheme.primaryButton("Lưu");

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                if (cboPhong.getSelectedItem() == null || cboKhach.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(dialog, "Phòng và khách chính không được để trống");
                    return;
                }

                String selectedPhong = extractKey(String.valueOf(cboPhong.getSelectedItem()));
                String selectedKhach = extractKey(String.valueOf(cboKhach.getSelectedItem()));
                String maHopDong = txtMaHopDong.getText().trim();

                if (hopDongDAO.isRoomOccupiedByOtherActiveContract(selectedPhong, isEdit ? maHopDong : null)
                        && "HIEU_LUC".equals(String.valueOf(cboTrangThai.getSelectedItem()))) {
                    JOptionPane.showMessageDialog(dialog, "Phòng này đã có hợp đồng hiệu lực khác");
                    return;
                }

                HopDong hd = new HopDong();
                hd.setMaHopDong(maHopDong);
                hd.setMaPhong(selectedPhong);
                hd.setMaKhachChinh(selectedKhach);
                hd.setNgayBatDau(Date.valueOf(txtNgayBatDau.getText().trim()));
                hd.setNgayKetThuc(Date.valueOf(txtNgayKetThuc.getText().trim()));
                String tien = txtTienDatCoc.getText().trim();
                hd.setTienDatCoc(tien.isEmpty() ? BigDecimal.ZERO : new BigDecimal(tien));
                hd.setTrangThai(String.valueOf(cboTrangThai.getSelectedItem()));

                boolean ok = isEdit ? hopDongService.capNhatHopDong(hd) : hopDongService.taoHopDong(hd);
                JOptionPane.showMessageDialog(dialog, ok ? "Thành công" : "Không thể lưu hợp đồng");
                if (ok) {
                    dialog.dispose();
                    reloadData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi lưu hợp đồng: " + ex.getMessage());
            }
        });

        actions.add(btnCancel);
        actions.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadPhongToComboBox(JComboBox<String> cboPhong, String selectedRoom, String excludeContractId) {
        try {
            cboPhong.removeAllItems();
            List<String> rooms = excludeContractId == null ? hopDongDAO.findAvailableRoomIds() : hopDongDAO.findAvailableRoomIdsExcept(excludeContractId);
            if (selectedRoom != null && !selectedRoom.isEmpty() && !rooms.contains(selectedRoom)) {
                rooms.add(0, selectedRoom);
            }
            for (String room : rooms) {
                cboPhong.addItem(room);
            }
            if (selectedRoom != null) {
                cboPhong.setSelectedItem(selectedRoom);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách phòng: " + ex.getMessage());
        }
    }

    private void loadKhachToComboBox(JComboBox<String> cboKhach) {
        try {
            cboKhach.removeAllItems();
            List<KhachThue> dsKhach = khachThueDAO.findAll(null);
            for (KhachThue kh : dsKhach) {
                cboKhach.addItem(kh.getMaKhach() + " - " + kh.getHoTen());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách khách thuê: " + ex.getMessage());
        }
    }

    private void selectKhachByName(JComboBox<String> cboKhach, String hoTen) {
        for (int i = 0; i < cboKhach.getItemCount(); i++) {
            String item = String.valueOf(cboKhach.getItemAt(i));
            if (item.endsWith(" - " + hoTen)) {
                cboKhach.setSelectedIndex(i);
                return;
            }
        }
    }

    private String extractKey(String comboValue) {
        int idx = comboValue.indexOf(" - ");
        return idx > 0 ? comboValue.substring(0, idx) : comboValue;
    }
}
