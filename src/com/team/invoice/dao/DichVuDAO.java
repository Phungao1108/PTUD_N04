package com.team.invoice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.team.invoice.entity.DichVu;
import com.team.invoice.util.DBConnection;

public class DichVuDAO {

    // Lấy toàn bộ danh sách dịch vụ chưa bị xóa (isDeleted = 0)
    public List<DichVu> findAll() throws SQLException {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM DichVu WHERE isDeleted = 0";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(new DichVu(
                    rs.getString("maDV"), 
                    rs.getString("tenDV"),
                    rs.getString("donVi"), 
                    rs.getString("loaiDichVu"),
                    rs.getBoolean("isDeleted")
                ));
            }
        }
        return list;
    }

    // Thêm mới một dịch vụ
    public boolean insert(DichVu dv) throws SQLException {
        String sql = "INSERT INTO DichVu (maDV, tenDV, donVi, loaiDichVu, isDeleted) VALUES (?, ?, ?, ?, 0)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, dv.getMaDV());
            stmt.setString(2, dv.getTenDV());
            stmt.setString(3, dv.getDonVi());
            stmt.setString(4, dv.getLoaiDichVu());
            
            return stmt.executeUpdate() > 0;
        }
    }

    // Cập nhật thông tin dịch vụ hiện có
    public boolean update(DichVu dv) throws SQLException {
        String sql = "UPDATE DichVu SET tenDV = ?, donVi = ?, loaiDichVu = ? WHERE maDV = ? AND isDeleted = 0";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, dv.getTenDV());
            stmt.setString(2, dv.getDonVi());
            stmt.setString(3, dv.getLoaiDichVu());
            stmt.setString(4, dv.getMaDV());
            
            return stmt.executeUpdate() > 0;
        }
    }

    // Xóa mềm dịch vụ (chuyển isDeleted thành 1)
    public boolean delete(String maDV) throws SQLException {
        String sql = "UPDATE DichVu SET isDeleted = 1 WHERE maDV = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maDV);
            
            return stmt.executeUpdate() > 0;
        }
    }
}
