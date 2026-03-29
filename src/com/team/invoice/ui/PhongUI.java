package com.team.invoice.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.team.invoice.components.HintTextField;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.components.TableActionCell;
import com.team.invoice.components.UITheme;
import com.team.invoice.dialog.PhongDialog;
import com.team.invoice.entity.LoaiPhong;
import com.team.invoice.entity.Phong;
import com.team.invoice.service.LoaiPhongService;
import com.team.invoice.service.PhongService;
import com.team.invoice.util.FocusUtils;

public class PhongUI extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private HintTextField txtSearch;
    private JComboBox<String> cboLoai;
    private JComboBox<String> cboStatus;
    private TableRowSorter<DefaultTableModel> sorter;

    private final PhongService phongService;
    private List<Phong> currentList;

    public PhongUI() {
        phongService = new PhongService();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        UITheme.stylePage(this);

        txtSearch = new HintTextField("Tìm theo tên phòng...");
        cboLoai = new JComboBox<>();
        cboStatus = new JComboBox<>(new String[] {
            "Tất cả trạng thái",
            "TRONG",
            "ĐÃ THUÊ",
            "BẢO TRÌ"
        });

        loadLoaiPhongToComboBox();

        add(UITheme.createHeader(
            "Quản lý Phòng",
            "Quản lý danh sách phòng trong chung cư",
            UITheme.actionBar(createAddButton())
        ));

        add(UITheme.vspace(20));
        add(buildTableCard());

        addFilterEvents();
        loadData();

        FocusUtils.enableClearFocusOnClick(this);
    }

    private RoundedButton createAddButton() {
        RoundedButton btnAdd = UITheme.primaryButton("Thêm Phòng");
        btnAdd.addActionListener(e -> showPhongDialog(null));
        return btnAdd;
    }

    private void loadLoaiPhongToComboBox() {
        cboLoai.removeAllItems();
        cboLoai.addItem("Tất cả loại");

        LoaiPhongService loaiPhongService = new LoaiPhongService();
        List<LoaiPhong> dsLoaiPhong = loaiPhongService.getAllLoaiPhong();

        if (dsLoaiPhong != null) {
            for (LoaiPhong lp : dsLoaiPhong) {
                cboLoai.addItem(lp.getMaLoaiPhong());
            }
        }
    }

    private RoundedPanel buildTableCard() {
        RoundedPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 16));

        JPanel filterBar = new JPanel(new BorderLayout(12, 0));
        filterBar.setOpaque(false);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        right.add(new JLabel("Loại:"));
        right.add(cboLoai);

        right.add(new JLabel("Trạng thái:"));
        right.add(cboStatus);

        filterBar.add(UITheme.wrapField(txtSearch), BorderLayout.CENTER);
        filterBar.add(right, BorderLayout.EAST);

        model = new DefaultTableModel(
            new String[] {
                "Mã phòng", "Tên phòng", "Loại", "Trạng thái", "Tầng", "Hành động"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        table = new JTable(model);
        UITheme.styleTable(table);
        table.setRowHeight(38);

        table.getColumnModel().getColumn(5).setPreferredWidth(140);
        table.getColumnModel().getColumn(5).setMinWidth(140);
        table.getColumnModel().getColumn(5).setMaxWidth(160);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        table.getColumnModel().getColumn(5).setCellRenderer(new TableActionCell.Renderer());
        table.getColumnModel().getColumn(5).setCellEditor(new TableActionCell(new TableActionCell.TableActionEvent() {
            @Override
            public void onDelete(int row) {
                int modelRow = table.convertRowIndexToModel(row);
                Phong selectedPhong = currentList.get(modelRow);

                int confirm = JOptionPane.showConfirmDialog(
                    PhongUI.this,
                    "Bạn có chắc muốn xóa phòng " + selectedPhong.getTenPhong() + "?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    String message = phongService.deletePhong(selectedPhong.getMaPhong());
                    JOptionPane.showMessageDialog(PhongUI.this, message);
                    loadData();
                }
            }

            @Override
            public void onEdit(int row) {
                int modelRow = table.convertRowIndexToModel(row);
                Phong selectedPhong = currentList.get(modelRow);
                showPhongDialog(selectedPhong);
            }
        }));

        card.add(filterBar, BorderLayout.NORTH);
        card.add(UITheme.wrapTable(table), BorderLayout.CENTER);

        return card;
    }

    private void loadData() {
        model.setRowCount(0);
        currentList = phongService.getAllPhong();

        if (currentList != null) {
            for (Phong p : currentList) {
                model.addRow(new Object[] {
                    p.getMaPhong(),
                    p.getTenPhong(),
                    p.getMaLoaiPhong(),
                    p.getTrangThai(),
                    p.getIdCha(),
                    ""
                });
            }
        }
    }

    private void showPhongDialog(Phong phong) {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        PhongDialog dialog = new PhongDialog(parentFrame, phongService, phong, this::loadData);
        dialog.setVisible(true);
    }

    private void addFilterEvents() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilters();
            }
        });

        cboLoai.addActionListener(e -> applyFilters());
        cboStatus.addActionListener(e -> applyFilters());
    }

    private void applyFilters() {
        String keyword = txtSearch.getText().trim();
        String selectedLoai = cboLoai.getSelectedItem() == null ? "Tất cả loại" : cboLoai.getSelectedItem().toString();
        String selectedStatus = cboStatus.getSelectedItem() == null ? "Tất cả trạng thái" : cboStatus.getSelectedItem().toString();

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String tenPhongRow = entry.getStringValue(1);
                String loaiRow = entry.getStringValue(2);
                String trangThaiRow = entry.getStringValue(3);

                if (tenPhongRow == null) {
                    tenPhongRow = "";
                }

                boolean matchTenPhong = keyword.isEmpty()
                        || tenPhongRow.toLowerCase().contains(keyword.toLowerCase());

                boolean matchLoai = selectedLoai.equals("Tất cả loại")
                        || loaiRow.equals(selectedLoai);

                boolean matchTrangThai = selectedStatus.equals("Tất cả trạng thái")
                        || trangThaiRow.equalsIgnoreCase(selectedStatus);

                return matchTenPhong && matchLoai && matchTrangThai;
            }
        });
    }
}