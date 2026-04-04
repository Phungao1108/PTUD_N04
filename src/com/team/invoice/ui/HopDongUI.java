package com.team.invoice.ui;

import com.team.invoice.components.HintTextField;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.components.TableActionCell;
import com.team.invoice.components.UITheme;
import com.team.invoice.dao.HopDongDAO;
import com.team.invoice.entity.HopDong;
import com.team.invoice.entity.KhachThue;
import com.team.invoice.service.HopDongService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HopDongUI extends JPanel {
    private final HopDongDAO hopDongDAO = new HopDongDAO();
    private final HopDongService hopDongService = new HopDongService();

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
        btn.addActionListener(e -> openContractForm(null));
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
                openContractForm(currentList.get(modelRow));
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
                model.addRow(new Object[]{row[0], row[1], row[2], row[3], row[4], row[5], row[6], ""});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải hợp đồng: " + ex.getMessage());
        }
    }

    private void addFilterEvents() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
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

    private void openContractForm(Object[] rowData) {
        boolean isEdit = rowData != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setTitle(isEdit ? "Cập nhật Hợp đồng" : "Lập Hợp đồng");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(860, 720);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.add(new HopDongFormPanel(dialog, rowData), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private class HopDongFormPanel extends JPanel {
        private final JDialog dialog;
        private final Object[] rowData;
        private final boolean isEdit;
        private final JTextField txtMaHopDong = new JTextField();
        private final JFormattedTextField txtNgayBatDau = HopDongUI.createDateField();
        private final JFormattedTextField txtNgayKetThuc = HopDongUI.createDateField();
        private final JTextField txtTienDatCoc = new JTextField();
        private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{"HIEU_LUC", "HET_HAN", "DA_HUY", "DA_THANH_LY"});
        private final SuggestionField fieldPhong;
        private final SuggestionField fieldKhachChinh;
        private final JPanel guestContainer = new JPanel();
        private final List<SuggestionField> guestFields = new ArrayList<>();

        private HopDongFormPanel(JDialog dialog, Object[] rowData) {
            this.dialog = dialog;
            this.rowData = rowData;
            this.isEdit = rowData != null;
            this.fieldPhong = new SuggestionField(this::searchRooms);
            this.fieldKhachChinh = new SuggestionField(this::searchCustomers);

            setLayout(new BorderLayout());
            setBackground(UITheme.BG);
            setBorder(new EmptyBorder(24, 24, 24, 24));

            RoundedPanel content = UITheme.createCard();
            content.setLayout(new BorderLayout(0, 18));

            JPanel header = buildHeader();
            JPanel form = buildForm();
            JPanel actions = buildActions();

            content.add(header, BorderLayout.NORTH);
            content.add(new JScrollPane(form), BorderLayout.CENTER);
            content.add(actions, BorderLayout.SOUTH);
            add(content, BorderLayout.CENTER);

            initData();
        }

        private JPanel buildHeader() {
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            JLabel title = new JLabel(isEdit ? "Cập nhật hợp đồng" : "Lập hợp đồng mới");
            title.setFont(new Font("Segoe UI", Font.BOLD, 24));
            JLabel desc = new JLabel("Nhập ngày theo số, dấu gạch sẽ tự chèn. Phòng và khách chọn bằng ô tìm kiếm gợi ý bên dưới.");
            desc.setForeground(new Color(110, 120, 135));
            panel.add(title);
            panel.add(Box.createVerticalStrut(6));
            panel.add(desc);
            return panel;
        }

        private JPanel buildForm() {
            JPanel form = new JPanel(new GridBagLayout());
            form.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(0, 0, 12, 0);

            UITheme.styleTextField(txtMaHopDong);
            UITheme.styleTextField(txtNgayBatDau);
            UITheme.styleTextField(txtNgayKetThuc);
            UITheme.styleTextField(txtTienDatCoc);
            UITheme.styleCombo(cboTrangThai, 260);
            txtMaHopDong.setEditable(false);
            txtMaHopDong.setBackground(new Color(245, 247, 250));

            addField(form, gbc, "Mã hợp đồng", txtMaHopDong, "Mã hợp đồng tự sinh");
            addField(form, gbc, "Phòng", fieldPhong, "Gõ mã phòng hoặc tên phòng để hiện gợi ý");
            addField(form, gbc, "Khách chính", fieldKhachChinh, "Gõ tên, mã khách, CCCD hoặc số điện thoại");
            addField(form, gbc, "Ngày bắt đầu", txtNgayBatDau, "Nhập 8 số, ví dụ 01042026 sẽ thành 01-04-2026");
            addField(form, gbc, "Ngày kết thúc", txtNgayKetThuc, "Nhập 8 số, ví dụ 31032027 sẽ thành 31-03-2027");
            addField(form, gbc, "Tiền đặt cọc", txtTienDatCoc, "Cho phép số nguyên hoặc số thập phân");
            addField(form, gbc, "Trạng thái", cboTrangThai, "HIEU_LUC, HET_HAN, DA_HUY hoặc DA_THANH_LY");

            guestContainer.setOpaque(false);
            guestContainer.setLayout(new BoxLayout(guestContainer, BoxLayout.Y_AXIS));
            JButton btnAddGuest = new JButton("+ Khách phụ");
            btnAddGuest.setFocusPainted(false);
            btnAddGuest.addActionListener(e -> addGuestField(null));
            JPanel guestHeader = new JPanel(new BorderLayout());
            guestHeader.setOpaque(false);
            guestHeader.add(new JLabel("Khách phụ"), BorderLayout.WEST);
            guestHeader.add(btnAddGuest, BorderLayout.EAST);

            JPanel guestWrap = new JPanel(new BorderLayout(0, 8));
            guestWrap.setOpaque(false);
            guestWrap.add(guestHeader, BorderLayout.NORTH);
            guestWrap.add(guestContainer, BorderLayout.CENTER);
            JLabel guestHelper = new JLabel("Không bắt buộc. Có thể thêm nhiều người ở cùng phòng bằng nút dấu cộng.");
            guestHelper.setForeground(new Color(120, 130, 145));
            guestHelper.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            guestWrap.add(guestHelper, BorderLayout.SOUTH);
            gbc.gridy++;
            form.add(guestWrap, gbc);

            return form;
        }

        private JPanel buildActions() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            panel.setOpaque(false);
            RoundedButton btnCancel = UITheme.secondaryButton("Hủy");
            RoundedButton btnSave = UITheme.primaryButton("Lưu hợp đồng");
            btnCancel.addActionListener(e -> dialog.dispose());
            btnSave.addActionListener(e -> saveContract());
            panel.add(btnCancel);
            panel.add(btnSave);
            return panel;
        }

        private void initData() {
            if (isEdit) {
                txtMaHopDong.setText(String.valueOf(rowData[0]));
                fieldPhong.setSelectedValue(String.valueOf(rowData[1]));
                fieldKhachChinh.setSelectedValue(String.valueOf(rowData[2]));
                txtNgayBatDau.setText(formatDisplayDate(String.valueOf(rowData[3])));
                txtNgayKetThuc.setText(formatDisplayDate(String.valueOf(rowData[4])));
                txtTienDatCoc.setText(String.valueOf(rowData[5]));
                cboTrangThai.setSelectedItem(String.valueOf(rowData[6]));
                try {
                    List<KhachThue> guestPhu = hopDongDAO.findGuestPhuByContract(String.valueOf(rowData[0]));
                    if (guestPhu.isEmpty()) {
                        addGuestField(null);
                    } else {
                        for (KhachThue kh : guestPhu) {
                            addGuestField(kh.getMaKhach() + " - " + kh.getHoTen());
                        }
                    }
                } catch (SQLException ex) {
                    addGuestField(null);
                }
            } else {
                txtMaHopDong.setText("HD" + System.currentTimeMillis());
                addGuestField(null);
            }
        }

        private void saveContract() {
            try {
                String maHopDong = txtMaHopDong.getText().trim();
                String phongDisplay = fieldPhong.getSelectedValue();
                String khachDisplay = fieldKhachChinh.getSelectedValue();
                if (phongDisplay == null || phongDisplay.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn phòng từ danh sách gợi ý");
                    return;
                }
                if (khachDisplay == null || khachDisplay.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn khách chính từ danh sách gợi ý");
                    return;
                }

                String maPhong = extractKey(phongDisplay);
                String maKhach = extractKey(khachDisplay);
                Date ngayBatDau = parseDisplayDate(txtNgayBatDau.getText().trim());
                Date ngayKetThuc = parseDisplayDate(txtNgayKetThuc.getText().trim());
                if (ngayBatDau == null || ngayKetThuc == null) {
                    JOptionPane.showMessageDialog(dialog, "Ngày hợp đồng phải đúng đủ 8 số theo dạng dd-MM-yyyy");
                    return;
                }
                if (ngayKetThuc.before(ngayBatDau)) {
                    JOptionPane.showMessageDialog(dialog, "Ngày kết thúc không được nhỏ hơn ngày bắt đầu");
                    return;
                }

                String trangThai = String.valueOf(cboTrangThai.getSelectedItem());
                if ("HIEU_LUC".equals(trangThai) && hopDongDAO.isRoomOccupiedByOtherActiveContract(maPhong, isEdit ? maHopDong : null)) {
                    JOptionPane.showMessageDialog(dialog, "Phòng này đã có hợp đồng hiệu lực khác");
                    return;
                }
                if ("HIEU_LUC".equals(trangThai) && hopDongDAO.isPrimaryCustomerInOtherActiveContract(maKhach, isEdit ? maHopDong : null)) {
                    JOptionPane.showMessageDialog(dialog, "Khách chính đang đứng tên một hợp đồng hiệu lực khác");
                    return;
                }

                BigDecimal tienDatCoc;
                try {
                    tienDatCoc = txtTienDatCoc.getText().trim().isEmpty() ? BigDecimal.ZERO : new BigDecimal(txtTienDatCoc.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Tiền đặt cọc không hợp lệ");
                    return;
                }
                if (tienDatCoc.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(dialog, "Tiền đặt cọc không được âm");
                    return;
                }

                List<String> guestPhuIds = collectGuestPhuIds(maKhach);
                if (guestPhuIds == null) {
                    return;
                }

                HopDong hd = new HopDong();
                hd.setMaHopDong(maHopDong);
                hd.setMaPhong(maPhong);
                hd.setMaKhachChinh(maKhach);
                hd.setNgayBatDau(ngayBatDau);
                hd.setNgayKetThuc(ngayKetThuc);
                hd.setTienDatCoc(tienDatCoc);
                hd.setTrangThai(trangThai);

                boolean ok = isEdit ? hopDongService.capNhatHopDong(hd) : hopDongService.taoHopDong(hd);
                if (ok) {
                    hopDongDAO.saveGuestPhu(maHopDong, guestPhuIds);
                }
                JOptionPane.showMessageDialog(dialog, ok ? "Thành công" : "Không thể lưu hợp đồng");
                if (ok) {
                    dialog.dispose();
                    reloadData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi lưu hợp đồng: " + ex.getMessage());
            }
        }

        private List<String> collectGuestPhuIds(String maKhachChinh) {
            Set<String> ids = new LinkedHashSet<>();
            for (SuggestionField field : guestFields) {
                String value = field.getSelectedValue();
                if (value == null || value.trim().isEmpty()) {
                    continue;
                }
                String guestId = extractKey(value);
                if (guestId.equals(maKhachChinh)) {
                    JOptionPane.showMessageDialog(dialog, "Khách phụ không được trùng khách chính");
                    return null;
                }
                if (!ids.add(guestId)) {
                    JOptionPane.showMessageDialog(dialog, "Danh sách khách phụ đang bị trùng");
                    return null;
                }
            }
            return new ArrayList<>(ids);
        }

        private void addGuestField(String presetValue) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            SuggestionField field = new SuggestionField(this::searchCustomers);
            if (presetValue != null) {
                field.setSelectedValue(presetValue);
            }
            JButton remove = new JButton("X");
            remove.setMargin(new Insets(4, 10, 4, 10));
            remove.addActionListener(e -> {
                guestFields.remove(field);
                guestContainer.remove(row);
                guestContainer.revalidate();
                guestContainer.repaint();
            });
            row.add(field, BorderLayout.CENTER);
            row.add(remove, BorderLayout.EAST);
            row.setBorder(new EmptyBorder(0, 0, 8, 0));
            guestFields.add(field);
            guestContainer.add(row);
            guestContainer.revalidate();
            guestContainer.repaint();
        }

        private List<String> searchRooms(String keyword) {
            try {
                String excludeId = isEdit ? String.valueOf(rowData[0]) : null;
                return hopDongDAO.searchRoomSuggestions(keyword, excludeId);
            } catch (SQLException ex) {
                return new ArrayList<>();
            }
        }

        private List<String> searchCustomers(String keyword) {
            try {
                List<KhachThue> list = hopDongDAO.searchCustomerSuggestions(keyword);
                List<String> display = new ArrayList<>();
                for (KhachThue kh : list) {
                    display.add(kh.getMaKhach() + " - " + kh.getHoTen());
                }
                return display;
            } catch (SQLException ex) {
                return new ArrayList<>();
            }
        }

        private void addField(JPanel form, GridBagConstraints gbc, String labelText, JComponent field, String helperText) {
            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            form.add(label, gbc);
            gbc.gridy++;
            JPanel wrap = new JPanel(new BorderLayout(0, 4));
            wrap.setOpaque(false);
            wrap.add(field, BorderLayout.CENTER);
            JLabel helper = new JLabel(helperText);
            helper.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            helper.setForeground(new Color(120, 130, 145));
            wrap.add(helper, BorderLayout.SOUTH);
            form.add(wrap, gbc);
            gbc.gridy++;
        }
    }

    private static JFormattedTextField createDateField() {
        try {
            javax.swing.text.MaskFormatter formatter = new javax.swing.text.MaskFormatter("##-##-####");
            formatter.setPlaceholderCharacter('_');
            formatter.setAllowsInvalid(false);
            formatter.setCommitsOnValidEdit(true);
            JFormattedTextField field = new JFormattedTextField(formatter);
            field.setFocusLostBehavior(JFormattedTextField.PERSIST);
            field.setColumns(10);
            return field;
        } catch (java.text.ParseException ex) {
            return new JFormattedTextField();
        }
    }

    private static Date parseDisplayDate(String display) {
        try {
            display = display.replace("_", "").trim();
            if (!display.matches("\\d{2}-\\d{2}-\\d{4}")) {
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            java.util.Date parsed = sdf.parse(display);
            return new Date(parsed.getTime());
        } catch (Exception ex) {
            return null;
        }
    }

    private static String formatDisplayDate(String isoDate) {
        if (isoDate == null || isoDate.trim().isEmpty()) {
            return "";
        }
        String[] parts = isoDate.split("-");
        if (parts.length == 3) {
            return parts[2] + "-" + parts[1] + "-" + parts[0];
        }
        return isoDate;
    }

    private static String extractKey(String value) {
        int idx = value.indexOf(" - ");
        return idx > 0 ? value.substring(0, idx).trim() : value.trim();
    }

    @FunctionalInterface
    private interface SuggestionProvider {
        List<String> find(String keyword);
    }

    private static class SuggestionField extends JPanel {
        private final JTextField textField = new JTextField();
        private final DefaultListModel<String> listModel = new DefaultListModel<>();
        private final JList<String> list = new JList<>(listModel);
        private final JScrollPane scrollPane = new JScrollPane(list);
        private final SuggestionProvider provider;
        private String selectedValue;
        private boolean updating;

        SuggestionField(SuggestionProvider provider) {
            this.provider = provider;
            setLayout(new BorderLayout(0, 4));
            setOpaque(false);
            UITheme.styleTextField(textField);
            add(textField, BorderLayout.NORTH);

            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setVisibleRowCount(4);
            list.setFixedCellHeight(24);
            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    applySelection();
                }
            });

            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(215, 220, 228)));
            scrollPane.setPreferredSize(new Dimension(260, 96));
            scrollPane.setVisible(false);
            add(scrollPane, BorderLayout.CENTER);

            textField.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    refreshSuggestions();
                }
            });

            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (!scrollPane.isVisible()) return;
                    int size = listModel.getSize();
                    if (size == 0) return;
                    int idx = Math.max(list.getSelectedIndex(), 0);
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        list.setSelectedIndex(Math.min(idx + 1, size - 1));
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        list.setSelectedIndex(Math.max(idx - 1, 0));
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        applySelection();
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        hideSuggestions();
                    }
                }
            });

            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { refreshSuggestions(); }
                @Override
                public void removeUpdate(DocumentEvent e) { refreshSuggestions(); }
                @Override
                public void changedUpdate(DocumentEvent e) { refreshSuggestions(); }
            });
        }

        void setSelectedValue(String value) {
            this.selectedValue = value;
            updating = true;
            textField.setText(value == null ? "" : value);
            updating = false;
            hideSuggestions();
        }

        String getSelectedValue() {
            String current = textField.getText().trim();
            if (selectedValue != null && selectedValue.equals(current)) {
                return selectedValue;
            }
            return current.isEmpty() ? null : current;
        }

        private void refreshSuggestions() {
            if (updating) return;
            selectedValue = null;
            String keyword = textField.getText().trim();
            List<String> suggestions = provider.find(keyword);
            listModel.clear();
            if (suggestions != null) {
                for (String item : suggestions) {
                    listModel.addElement(item);
                }
            }
            if (listModel.isEmpty()) {
                hideSuggestions();
                return;
            }
            list.setSelectedIndex(0);
            scrollPane.setVisible(true);
            revalidate();
            repaint();
        }

        private void hideSuggestions() {
            scrollPane.setVisible(false);
            revalidate();
            repaint();
        }

        private void applySelection() {
            String value = list.getSelectedValue();
            if (value == null) return;
            setSelectedValue(value);
        }
    }
}
