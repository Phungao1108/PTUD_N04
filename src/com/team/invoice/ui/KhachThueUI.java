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
        dialog.setSize(620, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        RoundedPanel wrapper = UITheme.createCard();
        wrapper.setLayout(new BorderLayout(0, 18));
        wrapper.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(data == null ? "Thêm khách thuê" : "Cập nhật khách thuê");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel desc = new JLabel("Kiểm tra CCCD, số điện thoại và chuẩn hóa họ tên trước khi lưu.");
        desc.setForeground(new Color(105, 117, 134));
        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(desc);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JTextField txtMaKhach = new JTextField();
        JTextField txtHoTen = new JTextField();
        JTextField txtCCCD = new JTextField();
        JTextField txtSDT = new JTextField();
        JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});

        UITheme.styleTextField(txtMaKhach);
        UITheme.styleTextField(txtHoTen);
        UITheme.styleTextField(txtCCCD);
        UITheme.styleTextField(txtSDT);
        UITheme.styleCombo(cboTrangThai, 280);
        txtMaKhach.setEditable(false);
        txtMaKhach.setBackground(new Color(245, 247, 250));

        if (data != null) {
            txtMaKhach.setText(data.getMaKhach());
            txtHoTen.setText(data.getHoTen());
            txtCCCD.setText(data.getSoCCCD());
            txtSDT.setText(data.getSdt());
            cboTrangThai.setSelectedItem(data.getTrangThai());
        } else {
            txtMaKhach.setText("KT" + System.currentTimeMillis());
        }

        txtHoTen.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                txtHoTen.setText(Validate.normalizePersonName(txtHoTen.getText()));
            }
        });

        addField(form, gbc, 0, "Họ tên", txtHoTen, "Ví dụ: Nguyễn Văn An");
        addField(form, gbc, 1, "Mã khách", txtMaKhach, "Mã tự sinh và chỉ đọc");
        addField(form, gbc, 2, "CCCD", txtCCCD, "Gồm đúng 12 chữ số");
        addField(form, gbc, 3, "Số điện thoại", txtSDT, "Gồm 10 chữ số, bắt đầu bằng 0");
        addField(form, gbc, 4, "Trạng thái", cboTrangThai, "ACTIVE hoặc INACTIVE");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        RoundedButton btnCancel = UITheme.secondaryButton("Hủy");
        RoundedButton btnSave = UITheme.primaryButton("Lưu");

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                txtHoTen.setText(Validate.normalizePersonName(txtHoTen.getText()));
                String error = Validate.validateKhachThue(
                        khachThueDAO,
                        data == null ? null : data.getMaKhach(),
                        txtHoTen.getText(),
                        txtCCCD.getText().trim(),
                        txtSDT.getText().trim()
                );
                if (error != null) {
                    JOptionPane.showMessageDialog(dialog, error);
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

        wrapper.add(heading, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        wrapper.add(actions, BorderLayout.SOUTH);
        dialog.add(wrapper, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, String labelText, JComponent field, String helper) {
        gbc.gridy = row * 2;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        form.add(label, gbc);

        gbc.gridy = row * 2 + 1;
        JPanel wrap = new JPanel(new BorderLayout(0, 4));
        wrap.setOpaque(false);
        wrap.add(field, BorderLayout.NORTH);
        JLabel helperText = new JLabel(helper);
        helperText.setForeground(new Color(120, 130, 145));
        helperText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        wrap.add(helperText, BorderLayout.SOUTH);
        form.add(wrap, gbc);
    }
}
