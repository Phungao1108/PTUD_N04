package com.team.invoice.ui;

import com.team.invoice.components.HintTextField;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.components.TableActionCell;
import com.team.invoice.components.UITheme;
import com.team.invoice.dao.KhachThueDAO;
import com.team.invoice.entity.KhachThue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class KhachThueUI extends JPanel {
    private final KhachThueDAO khachThueDAO = new KhachThueDAO();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã khách", "Họ tên", "CCCD", "SĐT", "Trạng thái", "Hành động"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 5;
        }
    };

    private final JTable table = new JTable(model);
    private final HintTextField txtSearch = new HintTextField("Tìm theo họ tên / CCCD / SĐT...");
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    private List<KhachThue> currentList;

    public KhachThueUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        UITheme.stylePage(this);

        add(UITheme.createHeader(
                "Quản lý Khách thuê",
                "Thêm, sửa, xóa mềm và tra cứu khách thuê trong hệ thống.",
                UITheme.actionBar(createAddButton())
        ));
        add(UITheme.vspace(20));
        add(buildTableCard());

        addFilterEvents();
        reloadData();
    }

    private RoundedButton createAddButton() {
        RoundedButton btn = UITheme.primaryButton("Thêm Khách thuê");
        btn.addActionListener(e -> showKhachDialog(null));
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

        table.getColumnModel().getColumn(5).setPreferredWidth(140);
        table.getColumnModel().getColumn(5).setMinWidth(140);
        table.getColumnModel().getColumn(5).setMaxWidth(160);

        table.getColumnModel().getColumn(5).setCellRenderer(new TableActionCell.Renderer());
        table.getColumnModel().getColumn(5).setCellEditor(new TableActionCell(new TableActionCell.TableActionEvent() {
            @Override
            public void onEdit(int row) {
                int modelRow = table.convertRowIndexToModel(row);
                showKhachDialog(currentList.get(modelRow));
            }

            @Override
            public void onDelete(int row) {
                int modelRow = table.convertRowIndexToModel(row);
                KhachThue kh = currentList.get(modelRow);

                int confirm = JOptionPane.showConfirmDialog(
                        KhachThueUI.this,
                        "Bạn có chắc muốn xóa khách thuê " + kh.getHoTen() + "?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean ok = khachThueDAO.softDelete(kh.getMaKhach());
                        JOptionPane.showMessageDialog(KhachThueUI.this, ok ? "Thành công" : "Không thể xóa khách thuê");
                        reloadData();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(KhachThueUI.this, "Lỗi xóa khách thuê: " + ex.getMessage());
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
            currentList = khachThueDAO.findAll(null);
            model.setRowCount(0);
            for (KhachThue kh : currentList) {
                model.addRow(new Object[]{
                        kh.getMaKhach(),
                        kh.getHoTen(),
                        kh.getSoCCCD(),
                        kh.getSdt(),
                        kh.getTrangThai(),
                        ""
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu khách thuê: " + ex.getMessage());
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
                for (int i = 0; i < 4; i++) {
                    String value = entry.getStringValue(i);
                    if (value != null && value.toLowerCase().contains(keyword)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void showKhachDialog(KhachThue data) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setTitle(data == null ? "Thêm Khách thuê" : "Cập nhật Khách thuê");
        dialog.setSize(520, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        form.setBackground(Color.WHITE);

        JTextField txtMaKhach = new JTextField();
        JTextField txtHoTen = new JTextField();
        JTextField txtCCCD = new JTextField();
        JTextField txtSDT = new JTextField();
        JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});

        UITheme.styleTextField(txtMaKhach);
        UITheme.styleTextField(txtHoTen);
        UITheme.styleTextField(txtCCCD);
        UITheme.styleTextField(txtSDT);
        UITheme.styleCombo(cboTrangThai, 250);

        if (data != null) {
            txtMaKhach.setText(data.getMaKhach());
            txtMaKhach.setEditable(false);
            txtHoTen.setText(data.getHoTen());
            txtCCCD.setText(data.getSoCCCD());
            txtSDT.setText(data.getSdt());
            cboTrangThai.setSelectedItem(data.getTrangThai());
        } else {
            txtMaKhach.setText("KT" + System.currentTimeMillis());
        }

        form.add(new JLabel("Mã khách:"));
        form.add(txtMaKhach);
        form.add(new JLabel("Họ tên:"));
        form.add(txtHoTen);
        form.add(new JLabel("CCCD:"));
        form.add(txtCCCD);
        form.add(new JLabel("SĐT:"));
        form.add(txtSDT);
        form.add(new JLabel("Trạng thái:"));
        form.add(cboTrangThai);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBackground(Color.WHITE);

        RoundedButton btnCancel = UITheme.secondaryButton("Hủy");
        RoundedButton btnSave = UITheme.primaryButton("Lưu");

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                if (txtHoTen.getText().trim().isEmpty() || txtCCCD.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Họ tên và CCCD không được để trống");
                    return;
                }

                KhachThue kh = new KhachThue(
                        txtMaKhach.getText().trim(),
                        txtHoTen.getText().trim(),
                        txtCCCD.getText().trim(),
                        txtSDT.getText().trim(),
                        String.valueOf(cboTrangThai.getSelectedItem())
                );

                boolean ok = (data == null) ? khachThueDAO.insert(kh) : khachThueDAO.update(kh);
                JOptionPane.showMessageDialog(dialog, ok ? "Thành công" : "Không thể lưu khách thuê");
                if (ok) {
                    dialog.dispose();
                    reloadData();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi lưu khách thuê: " + ex.getMessage());
            }
        });

        actions.add(btnCancel);
        actions.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
