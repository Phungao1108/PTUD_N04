package com.team.invoice.dao;

import com.team.invoice.entity.Phong;
import com.team.invoice.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhongDAO {

    public List<Phong> findAll() throws SQLException {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT * FROM Phong WHERE isDeleted = 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Phong(
                    rs.getString("maPhong"),
                    rs.getString("tenPhong"),
                    rs.getString("maLoaiPhong"),
                    rs.getString("trangThai"),
                    rs.getString("khachHienTai"),
                    rs.getDouble("giaThang"),
                    rs.getDouble("dien"),
                    rs.getDouble("nuoc"),
                    rs.getDouble("dichVu"),
                    rs.getString("kyChiSo"),
                    rs.getBoolean("isDeleted")
                ));
            }
        }

        return list;
    }

    public boolean existsByMaPhong(String maPhong) throws SQLException {
        String sql = "SELECT 1 FROM Phong WHERE maPhong = ? AND isDeleted = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maPhong);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public boolean existsByTenPhong(String tenPhong) throws SQLException {
        String sql = "SELECT 1 FROM Phong WHERE tenPhong = ? AND isDeleted = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenPhong);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public boolean existsByTenPhongExceptMaPhong(String tenPhong, String maPhong) throws SQLException {
        String sql = "SELECT 1 FROM Phong WHERE tenPhong = ? AND maPhong <> ? AND isDeleted = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenPhong);
            stmt.setString(2, maPhong);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public boolean insert(Phong p) throws SQLException {
        String sql = "INSERT INTO Phong (maPhong, tenPhong, maLoaiPhong, trangThai, khachHienTai, giaThang, dien, nuoc, dichVu, kyChiSo, isDeleted) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getMaPhong());
            stmt.setString(2, p.getTenPhong());
            stmt.setString(3, p.getMaLoaiPhong());
            stmt.setString(4, p.getTrangThai());
            stmt.setString(5, p.getKhachHienTai());
            stmt.setDouble(6, p.getGiaThang());
            stmt.setDouble(7, p.getDien());
            stmt.setDouble(8, p.getNuoc());
            stmt.setDouble(9, p.getDichVu());
            stmt.setString(10, p.getKyChiSo());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean update(Phong p) throws SQLException {
        String sql = "UPDATE Phong SET tenPhong = ?, maLoaiPhong = ?, trangThai = ?, khachHienTai = ?, "
                   + "giaThang = ?, dien = ?, nuoc = ?, dichVu = ?, kyChiSo = ? "
                   + "WHERE maPhong = ? AND isDeleted = 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getTenPhong());
            stmt.setString(2, p.getMaLoaiPhong());
            stmt.setString(3, p.getTrangThai());
            stmt.setString(4, p.getKhachHienTai());
            stmt.setDouble(5, p.getGiaThang());
            stmt.setDouble(6, p.getDien());
            stmt.setDouble(7, p.getNuoc());
            stmt.setDouble(8, p.getDichVu());
            stmt.setString(9, p.getKyChiSo());
            stmt.setString(10, p.getMaPhong());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(String maPhong) throws SQLException {
        String sql = "UPDATE Phong SET isDeleted = 1 WHERE maPhong = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maPhong);
            return stmt.executeUpdate() > 0;
        }
    }
}