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
import com.team.invoice.dialog.DichVuDialog;
import com.team.invoice.entity.DichVu;
import com.team.invoice.service.DichVuService;

public class DichvuUI extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private DichVuService dichVuService;
    private List<DichVu> currentList;

    public DichvuUI() {
        dichVuService = new DichVuService();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        UITheme.stylePage(this);

        // Header
        add(UITheme.createHeader(
            "Quản lý Dịch Vụ", 
            "Danh sách các dịch vụ tính phí trong chung cư (Điện, Nước, Rác...)",
            UITheme.actionBar(createAddButton())
        ));
        
        add(UITheme.vspace(20));
        add(buildTableCard());
        loadData();
    }

    private RoundedButton createAddButton() {
        RoundedButton btnAdd = UITheme.primaryButton("Thêm Dịch Vụ");
        btnAdd.addActionListener(e -> showDichVuDialog(null)); // Truyền null để báo là thêm mới
        return btnAdd;
    }

    private RoundedPanel buildTableCard() {
        RoundedPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 16));

        String[] cols = {"Mã Dịch Vụ", "Tên Dịch Vụ", "Đơn Vị", "Loại", "Hành Động"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Chỉ cho phép click vào cột Hành Động
            }
        };

        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setRowHeight(40); // Tăng chiều cao dòng để chứa vừa nút bấm

        // Gắn Renderer và Editor (nút Sửa/Xóa) vào cột 4 (cột cuối)
        table.getColumnModel().getColumn(4).setCellRenderer(new TableActionCell.Renderer());
        table.getColumnModel().getColumn(4).setCellEditor(new TableActionCell(new TableActionCell.TableActionEvent() {

            @Override
            public void onDelete(int row) {
                DichVu selectedDV = currentList.get(row);
                int confirm = JOptionPane.showConfirmDialog(DichvuUI.this, 
                        "Bạn có chắc muốn xóa dịch vụ " + selectedDV.getTenDV() + "?", 
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dichVuService.deleteDichVu(selectedDV.getMaDV());
                    loadData(); // Tải lại bảng sau khi xóa
                }
            }

            @Override
            public void onEdit(int row) {
                // ĐÃ BỔ SUNG: Mở form sửa dữ liệu
                DichVu selectedDV = currentList.get(row);
                showDichVuDialog(selectedDV);
            }
        }));

        card.add(UITheme.wrapTable(table), BorderLayout.CENTER);
        return card;
    }

    private void loadData() {
        tableModel.setRowCount(0); // Xóa data cũ
        currentList = dichVuService.getAllDichVu(); // Gọi DB
        if (currentList != null) {
            for (DichVu dv : currentList) {
                tableModel.addRow(new Object[]{
                    dv.getMaDV(), 
                    dv.getTenDV(), 
                    dv.getDonVi(), 
                    dv.getLoaiDichVu(), 
                    "" // Ô cuối cùng để trống cho Renderer vẽ nút
                });
            }
        }
    }

    // ĐÃ BỔ SUNG: Hàm hiển thị Dialog Thêm/Sửa
    private void showDichVuDialog(DichVu dv) {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        
        // Mở dialog và truyền callback để refresh bảng khi lưu thành công
        DichVuDialog dialog = new DichVuDialog(parentFrame, dichVuService, dv, () -> {
            loadData(); 
        });
        dialog.setVisible(true);
    }
}