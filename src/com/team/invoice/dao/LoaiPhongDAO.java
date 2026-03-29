package com.team.invoice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.team.invoice.entity.LoaiPhong;
import com.team.invoice.util.DBConnection;

public class LoaiPhongDAO {

    public List<LoaiPhong> findAll() throws SQLException {
        List<LoaiPhong> list = new ArrayList<>();
        String sql = "SELECT * FROM LoaiPhong WHERE isDeleted = 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new LoaiPhong(
                    rs.getString("maLoaiPhong"),
                    rs.getString("tenLoaiPhong"),
                    rs.getDouble("dienTichChuan"),
                    rs.getString("mota"),
                    rs.getBoolean("isDeleted")
                ));
            }
        }

        return list;
    }

    public boolean insert(LoaiPhong lp) throws SQLException {
        String checkSql = "SELECT isDeleted FROM LoaiPhong WHERE maLoaiPhong = ?";
        String insertSql = "INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, dienTichChuan, mota, isDeleted) VALUES (?, ?, ?, ?, 0)";
        String restoreSql = "UPDATE LoaiPhong SET tenLoaiPhong = ?, dienTichChuan = ?, mota = ?, isDeleted = 0 WHERE maLoaiPhong = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, lp.getMaLoaiPhong());

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    boolean isDeleted = rs.getBoolean("isDeleted");

                    // Nếu mã đã tồn tại nhưng đang bị xoá mềm -> khôi phục lại
                    if (isDeleted) {
                        try (PreparedStatement restoreStmt = conn.prepareStatement(restoreSql)) {
                            restoreStmt.setString(1, lp.getTenLoaiPhong());
                            restoreStmt.setDouble(2, lp.getDienTichChuan());
                            restoreStmt.setString(3, lp.getMoTa());
                            restoreStmt.setString(4, lp.getMaLoaiPhong());
                            return restoreStmt.executeUpdate() > 0;
                        }
                    }

                    // Nếu mã đã tồn tại và chưa xoá -> không cho thêm
                    return false;
                }
            }

            // Nếu mã chưa tồn tại -> thêm mới
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, lp.getMaLoaiPhong());
                insertStmt.setString(2, lp.getTenLoaiPhong());
                insertStmt.setDouble(3, lp.getDienTichChuan());
                insertStmt.setString(4, lp.getMoTa());
                return insertStmt.executeUpdate() > 0;
            }
        }
    }

    public boolean update(LoaiPhong lp) throws SQLException {
        String sql = "UPDATE LoaiPhong SET tenLoaiPhong = ?, dienTichChuan = ?, mota = ? WHERE maLoaiPhong = ? AND isDeleted = 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, lp.getTenLoaiPhong());
            stmt.setDouble(2, lp.getDienTichChuan());
            stmt.setString(3, lp.getMoTa());
            stmt.setString(4, lp.getMaLoaiPhong());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(String maLoaiPhong) throws SQLException {
        String sql = "UPDATE LoaiPhong SET isDeleted = 1 WHERE maLoaiPhong = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maLoaiPhong);
            return stmt.executeUpdate() > 0;
        }
    }
}