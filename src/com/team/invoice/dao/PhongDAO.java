package com.team.invoice.dao;

import com.team.invoice.entity.Phong;
import com.team.invoice.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhongDAO {

    public List<Phong> findAll() throws SQLException {
        List<Phong> list = new ArrayList<>();

        String sql = """
            SELECT id, ten, maLoaiPhong, trangThaiPhong, idCha, isDeleted
            FROM CoSoVatChat
            WHERE loai = 'PHONG' AND isDeleted = 0
            ORDER BY id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Phong(
                    rs.getString("id"),
                    rs.getString("ten"),
                    rs.getString("maLoaiPhong"),
                    rs.getString("trangThaiPhong"),
                    rs.getString("idCha"),
                    rs.getBoolean("isDeleted")
                ));
            }
        }

        return list;
    }

    public boolean existsByMaPhong(String maPhong) throws SQLException {
        String sql = """
            SELECT 1
            FROM CoSoVatChat
            WHERE id = ? AND loai = 'PHONG' AND isDeleted = 0
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maPhong);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public boolean existsByTenPhong(String tenPhong) throws SQLException {
        String sql = """
            SELECT 1
            FROM CoSoVatChat
            WHERE ten = ? AND loai = 'PHONG' AND isDeleted = 0
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenPhong);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public boolean existsByTenPhongExceptMaPhong(String tenPhong, String maPhong) throws SQLException {
        String sql = """
            SELECT 1
            FROM CoSoVatChat
            WHERE ten = ? AND id <> ? AND loai = 'PHONG' AND isDeleted = 0
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenPhong);
            stmt.setString(2, maPhong);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public boolean insert(Phong p) throws SQLException {
        String sql = """
            INSERT INTO CoSoVatChat
            (id, ten, loai, idCha, trangThaiPhong, maLoaiPhong, isDeleted)
            VALUES (?, ?, 'PHONG', ?, ?, ?, 0)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getMaPhong());
            stmt.setString(2, p.getTenPhong());
            stmt.setString(3, p.getIdCha());
            stmt.setString(4, p.getTrangThai());
            stmt.setString(5, p.getMaLoaiPhong());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean update(Phong p) throws SQLException {
        String sql = """
            UPDATE CoSoVatChat
            SET ten = ?, trangThaiPhong = ?, maLoaiPhong = ?, idCha = ?
            WHERE id = ? AND loai = 'PHONG' AND isDeleted = 0
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getTenPhong());
            stmt.setString(2, p.getTrangThai());
            stmt.setString(3, p.getMaLoaiPhong());
            stmt.setString(4, p.getIdCha());
            stmt.setString(5, p.getMaPhong());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(String maPhong) throws SQLException {
        String sql = """
            UPDATE CoSoVatChat
            SET isDeleted = 1
            WHERE id = ? AND loai = 'PHONG'
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maPhong);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<String> findAllTangIds() throws SQLException {
        List<String> list = new ArrayList<>();

        String sql = """
            SELECT id
            FROM CoSoVatChat
            WHERE loai = 'TANG' AND isDeleted = 0
            ORDER BY id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("id"));
            }
        }

        return list;
    }
}
