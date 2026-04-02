package com.team.invoice.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.team.invoice.entity.ChiPhi;
import com.team.invoice.util.DBConnection;

public class ChiPhiDAO {

    // Lấy danh sách các "Kỳ" (Tháng) đã có dữ liệu để đổ vào ComboBox
    public List<String> layDanhSachCacKy() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT ky FROM ChiPhi ORDER BY ky DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(rs.getString("ky"));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Lấy dữ liệu ghi chỉ số của một kỳ, kết hợp lấy luôn Số cũ từ kỳ trước nếu kỳ này chưa nhập
    public List<ChiPhi> layChiSoTheoKy(String kyHienTai, String kyTruoc) {
        List<ChiPhi> list = new ArrayList<>();
        String sql = """
            SELECT p.id AS maPhong, p.ten AS tenPhong,
                   c.maCS, ISNULL(c.ky, ?) AS ky,
                   ISNULL(c.dien_cu, ISNULL(prev.dien_moi, 0)) AS dien_cu,
                   ISNULL(c.dien_moi, 0) AS dien_moi,
                   ISNULL(c.nuoc_cu, ISNULL(prev.nuoc_moi, 0)) AS nuoc_cu,
                   ISNULL(c.nuoc_moi, 0) AS nuoc_moi
            FROM CoSoVatChat p
            LEFT JOIN ChiPhi c ON p.id = c.maPhong AND c.ky = ?
            LEFT JOIN ChiPhi prev ON p.id = prev.maPhong AND prev.ky = ?
            WHERE p.loai = 'PHONG' AND p.isDeleted = 0
            ORDER BY p.id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kyHienTai);
            stmt.setString(2, kyHienTai);
            stmt.setString(3, kyTruoc);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ChiPhi cp = new ChiPhi();
                cp.setMaPhong(rs.getString("maPhong"));
                cp.setTenPhong(rs.getString("tenPhong"));
                cp.setMaCS(rs.getString("maCS")); // Nếu chưa lưu, mã này sẽ null
                cp.setKy(rs.getString("ky"));
                cp.setDienCu(rs.getInt("dien_cu"));
                cp.setDienMoi(rs.getInt("dien_moi"));
                cp.setNuocCu(rs.getInt("nuoc_cu"));
                cp.setNuocMoi(rs.getInt("nuoc_moi"));
                list.add(cp);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Lưu hàng loạt chỉ số mới nhập
    public boolean luuChiPhi(List<ChiPhi> danhSach) {
        String sqlUpdate = "UPDATE ChiPhi SET dien_moi = ?, nuoc_moi = ?, ngayGhi = GETDATE() WHERE maCS = ?";
        String sqlInsert = "INSERT INTO ChiPhi (maCS, maPhong, ky, dien_cu, dien_moi, nuoc_cu, nuoc_moi, ngayGhi) VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
             PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
            
            conn.setAutoCommit(false);
            
            for (ChiPhi cp : danhSach) {
                if (cp.getMaCS() != null) {
                    // Đã có record -> Update
                    stmtUpdate.setInt(1, cp.getDienMoi());
                    stmtUpdate.setInt(2, cp.getNuocMoi());
                    stmtUpdate.setString(3, cp.getMaCS());
                    stmtUpdate.addBatch();
                } else {
                    // Chưa có record -> Insert
                    String newMaCS = "CS" + System.nanoTime() + (int)(Math.random()*100);
                    stmtInsert.setString(1, newMaCS);
                    stmtInsert.setString(2, cp.getMaPhong());
                    stmtInsert.setString(3, cp.getKy());
                    stmtInsert.setInt(4, cp.getDienCu());
                    stmtInsert.setInt(5, cp.getDienMoi());
                    stmtInsert.setInt(6, cp.getNuocCu());
                    stmtInsert.setInt(7, cp.getNuocMoi());
                    stmtInsert.addBatch();
                }
            }
            stmtUpdate.executeBatch();
            stmtInsert.executeBatch();
            conn.commit();
            return true;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }
}
