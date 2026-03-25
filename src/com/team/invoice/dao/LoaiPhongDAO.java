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
                    rs.getString("maLoaiPhong"), rs.getString("tenLoaiPhong"),
                    rs.getDouble("dienTichChuan"), rs.getString("mota"),
                    rs.getBoolean("isDeleted")
                ));
            }
        }
        return list;
    }

    public boolean insert(LoaiPhong lp) throws SQLException {
        String sql = "INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, dienTichChuan, mota, isDeleted) VALUES (?, ?, ?, ?, 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lp.getMaLoaiPhong());
            stmt.setString(2, lp.getTenLoaiPhong());
            stmt.setDouble(3, lp.getDienTichChuan());
            stmt.setString(4, lp.getMoTa());
            return stmt.executeUpdate() > 0;
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