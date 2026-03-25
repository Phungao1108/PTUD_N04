package com.team.invoice.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.components.TableActionCell;
import com.team.invoice.components.UITheme;
import com.team.invoice.dialog.LoaiPhongDialog;
import com.team.invoice.entity.LoaiPhong;
import com.team.invoice.service.LoaiPhongService;

public class LoaiPhongUI extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private LoaiPhongService loaiPhongService;
    private List<LoaiPhong> currentList;

    public LoaiPhongUI() {
        loaiPhongService = new LoaiPhongService();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        UITheme.stylePage(this);

        add(UITheme.createHeader(
            "Quản lý Loại Phòng", 
            "Thiết lập các loại phòng và diện tích chuẩn cho chung cư",
            UITheme.actionBar(createAddButton())
        ));
        
        add(UITheme.vspace(20));
        add(buildTableCard());
        loadData();
    }

    private RoundedButton createAddButton() {
        RoundedButton btnAdd = UITheme.primaryButton("Thêm Loại Phòng");
        btnAdd.addActionListener(e -> showLoaiPhongDialog(null)); 
        return btnAdd;
    }

    private RoundedPanel buildTableCard() {
        RoundedPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 16));

        String[] cols = {"Mã Loại Phòng", "Tên Loại", "Diện Tích (m2)", "Mô Tả", "Hành Động"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; 
            }
        };

        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setRowHeight(40); 

        table.getColumnModel().getColumn(4).setCellRenderer(new TableActionCell.Renderer());
        table.getColumnModel().getColumn(4).setCellEditor(new TableActionCell(new TableActionCell.TableActionEvent() {

            @Override
            public void onDelete(int row) {
                LoaiPhong selectedLP = currentList.get(row);
                int confirm = JOptionPane.showConfirmDialog(LoaiPhongUI.this, 
                        "Bạn có chắc muốn xóa loại phòng " + selectedLP.getTenLoaiPhong() + "?", 
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    loaiPhongService.deleteLoaiPhong(selectedLP.getMaLoaiPhong());
                    loadData(); 
                }
            }

            @Override
            public void onEdit(int row) {
                LoaiPhong selectedLP = currentList.get(row);
                showLoaiPhongDialog(selectedLP);
            }
        }));

        card.add(UITheme.wrapTable(table), BorderLayout.CENTER);
        return card;
    }

    private void loadData() {
        tableModel.setRowCount(0); 
        currentList = loaiPhongService.getAllLoaiPhong(); 
        if (currentList != null) {
            for (LoaiPhong lp : currentList) {
                tableModel.addRow(new Object[]{
                    lp.getMaLoaiPhong(), 
                    lp.getTenLoaiPhong(), 
                    lp.getDienTichChuan(), 
                    lp.getMoTa(), 
                    "" 
                });
            }
        }
    }

    private void showLoaiPhongDialog(LoaiPhong lp) {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        LoaiPhongDialog dialog = new LoaiPhongDialog(parentFrame, loaiPhongService, lp, () -> {
            loadData(); 
        });
        dialog.setVisible(true);
    }
}