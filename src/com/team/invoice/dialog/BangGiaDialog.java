package com.team.invoice.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.team.invoice.components.AppColors;
import com.team.invoice.components.HintTextField;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.entity.BangGia;
import com.team.invoice.service.BangGiaService;

public class BangGiaDialog extends JDialog {
    private JTextField txtMaBG;
    private JTextField txtNgayHieuLuc;
    private JTextField txtNgayKetThuc; 
    
    private BangGiaService bangGiaService;
    private Runnable onSuccess;

    public BangGiaDialog(Frame owner, BangGiaService service, Runnable onSuccess) {
        super(owner, true);
        this.bangGiaService = service;
        this.onSuccess = onSuccess;

        setTitle("Tạo Đợt Giá Mới");
        setSize(400, 320); 
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(AppColors.BG);

        initComponents();
    }

    private void initComponents() {
        RoundedPanel mainPanel = new RoundedPanel(15, Color.WHITE, AppColors.BORDER);
        mainPanel.setLayout(new GridLayout(3, 2, 10, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        txtMaBG = new HintTextField("Mã Đợt (VD: BG_2026_03)");
        txtNgayHieuLuc = new HintTextField("Ngày Hiệu Lực (dd/MM/yyyy)");
        txtNgayKetThuc = new HintTextField("dd/MM/yyyy (Có thể bỏ trống)");
        
        String autoMaBG = "BG" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        txtMaBG.setText(autoMaBG);
        txtNgayHieuLuc.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        mainPanel.add(new JLabel("Mã Bảng Giá (*):")); mainPanel.add(txtMaBG);
        mainPanel.add(new JLabel("Ngày Áp Dụng (*):")); mainPanel.add(txtNgayHieuLuc);
        mainPanel.add(new JLabel("Ngày Kết Thúc:")); mainPanel.add(txtNgayKetThuc);
        
        add(mainPanel, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlBottom.setBackground(AppColors.BG);

        RoundedButton btnCancel = new RoundedButton("Hủy", Color.WHITE, AppColors.TEXT, AppColors.BORDER);
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.addActionListener(e -> dispose());

        RoundedButton btnSave = new RoundedButton("Lưu Đợt Giá", AppColors.PRIMARY, Color.WHITE, AppColors.PRIMARY);
        btnSave.setPreferredSize(new Dimension(130, 35));
        btnSave.addActionListener(e -> saveBangGia());

        pnlBottom.add(btnCancel);
        pnlBottom.add(btnSave);

        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void saveBangGia() {
        String maBG = txtMaBG.getText().trim();
        if(maBG.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Bảng Giá!");
            return;
        }

        // Xử lý Ngày
        Date ngayHieuLuc;
        Date ngayKetThuc = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            ngayHieuLuc = sdf.parse(txtNgayHieuLuc.getText().trim());
            
            if (!txtNgayKetThuc.getText().trim().isEmpty()) {
                ngayKetThuc = sdf.parse(txtNgayKetThuc.getText().trim());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ngày không đúng định dạng dd/MM/yyyy!");
            return;
        }

        // --- BẮT ĐẦU LOGIC KIỂM TRA TRÙNG LẶP ---
        List<BangGia> activeList = bangGiaService.getAllBangGia();
        BangGia dotGiaBiTrung = null;
        
        if (activeList != null) {
            for (BangGia bgItem : activeList) {
                // Kiểm tra xem có đợt giá nào đang "DANG_AP_DUNG" mà chưa chốt ngày kết thúc 
                // hoặc ngày kết thúc của nó trễ hơn ngày bắt đầu đợt mới không
                if ("DANG_AP_DUNG".equals(bgItem.getTrangThai())) {
                    if (bgItem.getNgayKetThuc() == null || bgItem.getNgayKetThuc().after(ngayHieuLuc)) {
                        dotGiaBiTrung = bgItem;
                        break; 
                    }
                }
            }
        }

        // Nếu phát hiện trùng, bật Dialog hỏi người dùng
        if (dotGiaBiTrung != null) {
            String msg = "Đợt giá mới sẽ ghi đè lên thời gian của đợt giá [" + dotGiaBiTrung.getMaBG() + "].\n" +
                         "Bạn có muốn tự động kết thúc đợt giá cũ vào ngày " + txtNgayHieuLuc.getText().trim() + " không?";
            
            int confirm = JOptionPane.showConfirmDialog(this, msg, "Cảnh báo trùng lặp đợt giá", 
                                                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Cập nhật ngày kết thúc cho đợt cũ
                boolean chotThanhCong = bangGiaService.chotDotGiaCu(dotGiaBiTrung.getMaBG(), ngayHieuLuc);
                if (!chotThanhCong) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi chốt đợt giá cũ. Vui lòng thử lại!");
                    return; // Dừng lại nếu lỗi Database
                }
            } else {
                return; // Nếu chọn Hủy -> Ngừng việc tạo đợt giá mới
            }
        }
        // --- KẾT THÚC LOGIC KIỂM TRA TRÙNG LẶP ---


        // Tiến hành lưu Đợt giá mới
        BangGia bg = new BangGia(maBG, ngayHieuLuc, ngayKetThuc, "DANG_AP_DUNG", false);
        String result = bangGiaService.createBangGia(bg, new ArrayList<>(), new ArrayList<>());
        
        JOptionPane.showMessageDialog(this, result);

        if ("Thành công".equals(result)) {
            if (onSuccess != null) onSuccess.run();
            dispose();
        }
    }
    public boolean deleteBangGiaToanBo(String maBG) {
        Connection conn = null;
        try {
            conn = com.team.invoice.util.DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Xóa chi tiết đơn giá phòng
            String sql1 = "DELETE FROM DonGiaPhong WHERE maBG = ?";
            try (PreparedStatement stmt1 = conn.prepareStatement(sql1)) {
                stmt1.setString(1, maBG);
                stmt1.executeUpdate();
            }

            // 2. Xóa chi tiết đơn giá dịch vụ
            String sql2 = "DELETE FROM DonGiaDichVu WHERE maBG = ?";
            try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
                stmt2.setString(1, maBG);
                stmt2.executeUpdate();
            }

            // 3. Xóa mềm đợt giá (isDeleted = 1) để lưu vết hoặc DELETE hẳn tùy bạn
            // Ở đây mình chọn xóa mềm theo phong cách chung của dự án bạn
            String sql3 = "UPDATE BangGia SET isDeleted = 1, trangThai = 'HET_HIEU_LUC' WHERE maBG = ?";
            try (PreparedStatement stmt3 = conn.prepareStatement(sql3)) {
                stmt3.setString(1, maBG);
                stmt3.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}