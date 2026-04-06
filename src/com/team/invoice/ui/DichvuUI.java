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
import com.team.invoice.service.BangGiaService;
import com.team.invoice.service.DichVuService;

public class DichvuUI extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private DichVuService dichVuService;
    private BangGiaService bangGiaService; // Khai báo thêm BangGiaService
    private List<DichVu> currentList;

    public DichvuUI() {
        dichVuService = new DichVuService();
        bangGiaService = new BangGiaService(); // Khởi tạo Service lấy giá
        
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
        btnAdd.addActionListener(e -> showDichVuDialog(null)); 
        return btnAdd;
    }

    private RoundedPanel buildTableCard() {
        RoundedPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 16));

        // Đã thêm cột "Đơn Giá Hiện Tại"
        String[] cols = {"Mã Dịch Vụ", "Tên Dịch Vụ", "Đơn Vị", "Loại", "Đơn Giá Hiện Tại", "Hành Động"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Cột Hành Động dịch sang index 5
            }
        };

        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setRowHeight(40); 

        // Gắn Renderer và Editor (nút Sửa/Xóa) vào cột 5
        table.getColumnModel().getColumn(5).setCellRenderer(new TableActionCell.Renderer());
        table.getColumnModel().getColumn(5).setCellEditor(new TableActionCell(new TableActionCell.TableActionEvent() {

            @Override
            public void onDelete(int row) {
                DichVu selectedDV = currentList.get(row);
                int confirm = JOptionPane.showConfirmDialog(DichvuUI.this, 
                        "Bạn có chắc muốn xóa dịch vụ " + selectedDV.getTenDV() + "?", 
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dichVuService.deleteDichVu(selectedDV.getMaDV());
                    loadData(); 
                }
            }

            @Override
            public void onEdit(int row) {
                DichVu selectedDV = currentList.get(row);
                showDichVuDialog(selectedDV);
            }
        }));

        // Chỉnh độ rộng cột cho phù hợp với nội dung hiển thị
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(150); // Cột đơn giá
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Cột hành động

        card.add(UITheme.wrapTable(table), BorderLayout.CENTER);
        return card;
    }

    private void loadData() {
        tableModel.setRowCount(0); 
        currentList = dichVuService.getAllDichVu(); 
        
        if (currentList != null) {
            for (DichVu dv : currentList) {
                // Lấy đơn giá nguyên bản (double) từ đợt giá đang áp dụng
                double gia = bangGiaService.getDonGiaDichVuDeTinhToan(dv.getMaDV());
                
                // Format lại thành chuỗi tiền tệ để hiển thị
                String giaHienThi = (gia > 0) ? String.format("%,.0f VNĐ", gia) : "Chưa cấu hình";

                tableModel.addRow(new Object[]{
                    dv.getMaDV(), 
                    dv.getTenDV(), 
                    dv.getDonVi(), 
                    dv.getLoaiDichVu(), 
                    giaHienThi, // Đưa giá vào cột 4
                    "" 
                });
            }
        }
    }

    private void showDichVuDialog(DichVu dv) {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        
        DichVuDialog dialog = new DichVuDialog(parentFrame, dichVuService, dv, () -> {
            loadData(); 
        });
        dialog.setVisible(true);
    }
}