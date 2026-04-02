package com.team.invoice.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import com.team.invoice.components.AppColors;
import com.team.invoice.components.HintTextField;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.components.UITheme;
import com.team.invoice.entity.ChiPhi;
import com.team.invoice.service.ChiPhiService;

public class ChiPhiUI extends JPanel {
    private JComboBox<String> cboKy;
    private HintTextField txtTaoKyMoi;
    private DefaultTableModel tableModel;
    private JTable table;
    
    private ChiPhiService chiPhiService = new ChiPhiService();
    private List<ChiPhi> currentList = new ArrayList<>();

    public ChiPhiUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(AppColors.BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Header: Tiêu đề và Nút
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Detail: Bảng nhập số liệu
        add(createDetailTable(), BorderLayout.CENTER);

        loadDanhSachKy();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Quản Lý Chỉ Số Điện Nước");
        lblTitle.setFont(lblTitle.getFont().deriveFont(20f));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        rightPanel.add(new JLabel("Kỳ hiện có:"));
        cboKy = new JComboBox<>();
        cboKy.setPreferredSize(new Dimension(100, 35));
        cboKy.addActionListener(e -> loadDataForSelectedKy());
        rightPanel.add(cboKy);

        rightPanel.add(new JLabel("  Hoặc tạo kỳ mới:"));
        txtTaoKyMoi = new HintTextField("MM/yyyy");
        txtTaoKyMoi.setPreferredSize(new Dimension(80, 35));
        rightPanel.add(txtTaoKyMoi);

        RoundedButton btnLoadKyMoi = new RoundedButton("Tạo/Tải Kỳ", Color.WHITE, AppColors.TEXT, AppColors.BORDER);
        btnLoadKyMoi.addActionListener(e -> {
            String newKy = txtTaoKyMoi.getText().trim();
            if (newKy.matches("\\d{2}/\\d{4}")) {
                cboKy.addItem(newKy);
                cboKy.setSelectedItem(newKy);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng MM/yyyy");
            }
        });
        rightPanel.add(btnLoadKyMoi);

        RoundedButton btnSave = UITheme.primaryButton("LƯU TẤT CẢ CHỈ SỐ");
        btnSave.addActionListener(e -> saveTatCaChiSo());
        rightPanel.add(btnSave);

        headerPanel.add(rightPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private RoundedPanel createDetailTable() {
        RoundedPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout());

        String[] cols = {
            "Mã Phòng", "Tên Phòng", 
            "Điện Cũ", "Điện Mới", "Tiêu Thụ Điện", 
            "Nước Cũ", "Nước Mới", "Tiêu Thụ Nước"
        };
        
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // CHỈ CHO PHÉP SỬA CỘT ĐIỆN MỚI (3) VÀ NƯỚC MỚI (6)
                return column == 3 || column == 6; 
            }
        };

        table = new JTable(tableModel);
        UITheme.styleTable(table);
        
        // Bắt sự kiện gõ phím để tính cột Tiêu thụ ngay lập tức
        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col == 3) { // Điện mới thay đổi
                    try {
                        int dienCu = Integer.parseInt(table.getValueAt(row, 2).toString());
                        int dienMoi = Integer.parseInt(table.getValueAt(row, 3).toString());
                        table.setValueAt(dienMoi > 0 ? (dienMoi - dienCu) : 0, row, 4);
                    } catch (Exception ignored) {}
                }
                if (col == 6) { // Nước mới thay đổi
                    try {
                        int nuocCu = Integer.parseInt(table.getValueAt(row, 5).toString());
                        int nuocMoi = Integer.parseInt(table.getValueAt(row, 6).toString());
                        table.setValueAt(nuocMoi > 0 ? (nuocMoi - nuocCu) : 0, row, 7);
                    } catch (Exception ignored) {}
                }
            }
        });

        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private void loadDanhSachKy() {
        cboKy.removeAllItems();
        List<String> kys = chiPhiService.getDanhSachKy();
        if (kys != null) {
            for (String k : kys) cboKy.addItem(k);
        }
    }

    private void loadDataForSelectedKy() {
        if (cboKy.getSelectedItem() == null) return;
        String selectedKy = cboKy.getSelectedItem().toString();
        
        tableModel.setRowCount(0);
        currentList = chiPhiService.getChiSoThang(selectedKy);
        
        if (currentList != null) {
            for (ChiPhi cp : currentList) {
                tableModel.addRow(new Object[]{
                    cp.getMaPhong(), cp.getTenPhong(),
                    cp.getDienCu(), cp.getDienMoi() == 0 ? "" : cp.getDienMoi(), cp.getTieuThuDien(),
                    cp.getNuocCu(), cp.getNuocMoi() == 0 ? "" : cp.getNuocMoi(), cp.getTieuThuNuoc()
                });
            }
        }
    }

    private void saveTatCaChiSo() {
        if (table.isEditing()) table.getCellEditor().stopCellEditing();
        
        for (int i = 0; i < currentList.size(); i++) {
            ChiPhi cp = currentList.get(i);
            try {
                String strDien = table.getValueAt(i, 3).toString();
                String strNuoc = table.getValueAt(i, 6).toString();
                cp.setDienMoi(strDien.isEmpty() ? 0 : Integer.parseInt(strDien));
                cp.setNuocMoi(strNuoc.isEmpty() ? 0 : Integer.parseInt(strNuoc));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Có lỗi định dạng số ở phòng: " + cp.getTenPhong());
                return;
            }
        }

        String msg = chiPhiService.luuDanhSachChiSo(currentList);
        JOptionPane.showMessageDialog(this, msg);
        if ("Thành công".equals(msg)) {
            loadDataForSelectedKy();
        }
    }
}