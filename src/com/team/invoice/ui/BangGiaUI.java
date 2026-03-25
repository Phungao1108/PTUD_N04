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
import com.team.invoice.dialog.BangGiaDialog;
import com.team.invoice.entity.BangGia;
import com.team.invoice.service.BangGiaService;
import com.team.invoice.util.DateUtils;

public class BangGiaUI extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private BangGiaService bangGiaService;
    private List<BangGia> currentList;

 // Trong file BangGiaUI.java
 // Cập nhật lại thanh công cụ Header để thêm nút Generate

     public BangGiaUI() {
         bangGiaService = new BangGiaService();
         setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
         UITheme.stylePage(this);

         add(UITheme.createHeader(
             "Danh sách Đợt Giá", 
             "Quản lý các đợt áp dụng giá. Chọn một đợt và bấm Generate để sinh lưới đơn giá.",
             UITheme.actionBar(createAddButton(), createGenerateButton()) // Thêm nút Generate vào đây
         ));
         
         add(UITheme.vspace(20));
         add(buildTableCard());
         loadData();
     }

     private RoundedButton createGenerateButton() {
         RoundedButton btnGen = UITheme.secondaryButton("Generate Đơn Giá");
         btnGen.addActionListener(e -> {
             int row = table.getSelectedRow();
             if (row < 0) {
                 JOptionPane.showMessageDialog(this, "Vui lòng click chọn một đợt giá trong bảng trước khi Generate!");
                 return;
             }
             String maBG = table.getValueAt(row, 0).toString();
             // Hàm boSungDonGiaThieu chúng ta đã viết ở Service sẽ làm nhiệm vụ Generate này
             String msg = bangGiaService.boSungDonGiaThieu(maBG);
             JOptionPane.showMessageDialog(this, msg);
         });
         return btnGen;
     }

    private RoundedButton createAddButton() {
        RoundedButton btnAdd = UITheme.primaryButton("Thêm Đợt Giá Mới");
        btnAdd.addActionListener(e -> showBangGiaDialog()); 
        return btnAdd;
    }

    private RoundedPanel buildTableCard() {
        RoundedPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 16));

        String[] cols = {"Mã Bảng Giá", "Ngày Hiệu Lực", "Ngày Kết Thúc", "Trạng Thái", "Hành Động"};
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
                BangGia selectedBG = currentList.get(row);
                
                // Hiển thị cảnh báo mạnh mẽ vì hành động này xóa nhiều dữ liệu liên quan
                int confirm = JOptionPane.showConfirmDialog(
                    BangGiaUI.this, 
                    "CẢNH BÁO: Xóa đợt giá [" + selectedBG.getMaBG() + "] sẽ xóa TẤT CẢ đơn giá phòng và dịch vụ đi kèm!\n" +
                    "Hành động này không thể hoàn tác. Bạn có chắc chắn muốn tiếp tục?", 
                    "Xác nhận xóa vĩnh viễn", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.ERROR_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    String result = bangGiaService.deleteBangGia(selectedBG.getMaBG());
                    JOptionPane.showMessageDialog(BangGiaUI.this, result);
                    if ("Thành công".equals(result)) {
                        loadData(); // Cập nhật lại bảng UI ngay lập tức
                    }
                }
            }

            @Override
            public void onEdit(int row) {
                JOptionPane.showMessageDialog(BangGiaUI.this, "Chức năng sửa thông tin Bảng Giá đang hoàn thiện!");
            }
        }));

        card.add(UITheme.wrapTable(table), BorderLayout.CENTER);
        return card;
    }

    private void loadData() {
        tableModel.setRowCount(0); 
        currentList = bangGiaService.getAllBangGia(); 
        if (currentList != null) {
            for (BangGia bg : currentList) {
                tableModel.addRow(new Object[]{
                    bg.getMaBG(), 
                    DateUtils.formatDate(bg.getNgayHieuLuc()), 
                    bg.getNgayKetThuc() != null ? DateUtils.formatDate(bg.getNgayKetThuc()) : "-", 
                    bg.getTrangThai(), 
                    "" 
                });
            }
        }
    }

    private void showBangGiaDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        BangGiaDialog dialog = new BangGiaDialog(parentFrame, bangGiaService, () -> {
            loadData(); 
        });
        dialog.setVisible(true);
    }
}