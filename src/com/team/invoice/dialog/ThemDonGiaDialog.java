package com.team.invoice.dialog;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.team.invoice.components.AppColors;
import com.team.invoice.components.HintTextField;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.UITheme;
import com.team.invoice.entity.BangGia;
import com.team.invoice.entity.DichVu;
import com.team.invoice.entity.DonGiaDichVu;
import com.team.invoice.entity.DonGiaPhong;
import com.team.invoice.entity.LoaiPhong;
import com.team.invoice.service.BangGiaService;
import com.team.invoice.service.DichVuService;
import com.team.invoice.service.LoaiPhongService;

public class ThemDonGiaDialog extends JDialog {
    private HintTextField txtMaBG, txtMoTa, txtNgayBatDau, txtNgayKetThuc;
    private DefaultTableModel modelPhong, modelDichVu;
    private JTable tablePhong, tableDichVu;
    
    private BangGiaService bangGiaService = new BangGiaService();
    private Runnable onSuccess;

    public ThemDonGiaDialog(Frame owner, Runnable onSuccess) {
        super(owner, "Thêm Bảng Giá Toàn Diện", true);
        this.onSuccess = onSuccess;
        setSize(800, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(AppColors.BG);

        // 1. Panel thông tin chung
        add(createMasterPanel(), BorderLayout.NORTH);

        // 2. TabbedPane chứa 2 bảng nhập giá
        add(createDetailTabbedPane(), BorderLayout.CENTER);

        // 3. Panel Nút bấm
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(AppColors.BG);
        
        RoundedButton btnHuy = new RoundedButton("Hủy", Color.WHITE, AppColors.TEXT, AppColors.BORDER);
        btnHuy.addActionListener(e -> dispose());
        
        RoundedButton btnLuu = UITheme.primaryButton("Lưu Bảng Giá & Đơn Giá");
        btnLuu.addActionListener(e -> saveTatCa());
        
        bottomPanel.add(btnHuy);
        bottomPanel.add(btnLuu);
        add(bottomPanel, BorderLayout.SOUTH);

        // Tự động load các hạng mục vào bảng
        loadHangMucMacDinh();
    }

    private JPanel createMasterPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin Bảng Giá"));

        txtMaBG = new HintTextField("VD: BG_T5_2026");
        txtMoTa = new HintTextField("VD: Bảng giá tháng 5");
        txtNgayBatDau = new HintTextField("dd/MM/yyyy"); // Gợi ý: thay bằng JDateChooser
        txtNgayKetThuc = new HintTextField("dd/MM/yyyy (Có thể để trống)");

        panel.add(new JLabel("Mã Bảng Giá (*):")); panel.add(txtMaBG);
        panel.add(new JLabel("Ngày bắt đầu (*):")); panel.add(txtNgayBatDau);
        panel.add(new JLabel("Mô tả / Tên gọi:")); panel.add(txtMoTa);
        panel.add(new JLabel("Ngày kết thúc:")); panel.add(txtNgayKetThuc);
        
        return panel;
    }

    private JTabbedPane createDetailTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Bảng Loại Phòng
        modelPhong = new DefaultTableModel(new String[]{"Mã Loại Phòng", "Tên Loại", "Đơn Giá (Nhập số)"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 2; }
        };
        tablePhong = new JTable(modelPhong);
        UITheme.styleTable(tablePhong);
        tabbedPane.addTab("Định giá Loại Phòng", new JScrollPane(tablePhong));

        // Bảng Dịch Vụ
        modelDichVu = new DefaultTableModel(new String[]{"Mã DV", "Tên DV", "Đơn Vị", "Đơn Giá (Nhập số)"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 3; }
        };
        tableDichVu = new JTable(modelDichVu);
        UITheme.styleTable(tableDichVu);
        tabbedPane.addTab("Định giá Dịch Vụ", new JScrollPane(tableDichVu));

        return tabbedPane;
    }

    private void loadHangMucMacDinh() {
        // Tải toàn bộ loại phòng lên bảng
        for (LoaiPhong lp : new LoaiPhongService().getAllLoaiPhong()) {
            modelPhong.addRow(new Object[]{lp.getMaLoaiPhong(), lp.getTenLoaiPhong(), "0"});
        }
        // Tải toàn bộ dịch vụ lên bảng
        for (DichVu dv : new DichVuService().getAllDichVu()) {
            modelDichVu.addRow(new Object[]{dv.getMaDV(), dv.getTenDV(), dv.getDonVi(), "0"});
        }
    }

    private void saveTatCa() {
        // Dừng edit trên table nếu người dùng đang gõ
        if (tablePhong.isEditing()) tablePhong.getCellEditor().stopCellEditing();
        if (tableDichVu.isEditing()) tableDichVu.getCellEditor().stopCellEditing();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        BangGia bg = new BangGia();
        bg.setMaBG(txtMaBG.getText().trim());
        
        try {
            bg.setNgayHieuLuc(sdf.parse(txtNgayBatDau.getText().trim()));
            if (!txtNgayKetThuc.getText().trim().isEmpty()) {
                bg.setNgayKetThuc(sdf.parse(txtNgayKetThuc.getText().trim()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày tháng phải đúng định dạng dd/MM/yyyy!");
            return;
        }

        // Đọc dữ liệu từ bảng Phòng
        List<DonGiaPhong> phongs = new ArrayList<>();
        for (int i = 0; i < modelPhong.getRowCount(); i++) {
            double gia = Double.parseDouble(modelPhong.getValueAt(i, 2).toString());
            phongs.add(new DonGiaPhong(bg.getMaBG(), modelPhong.getValueAt(i, 0).toString(), gia));
        }

        // Đọc dữ liệu từ bảng Dịch Vụ
        List<DonGiaDichVu> dichVus = new ArrayList<>();
        for (int i = 0; i < modelDichVu.getRowCount(); i++) {
            double gia = Double.parseDouble(modelDichVu.getValueAt(i, 3).toString());
            dichVus.add(new DonGiaDichVu(bg.getMaBG(), modelDichVu.getValueAt(i, 0).toString(), gia));
        }

        // Gửi xuống Service
        String message = bangGiaService.createBangGiaToanDien(bg, phongs, dichVus);
        JOptionPane.showMessageDialog(this, message);
        if (message.equals("Thành công")) {
            if (onSuccess != null) onSuccess.run();
            dispose();
        }
    }
}