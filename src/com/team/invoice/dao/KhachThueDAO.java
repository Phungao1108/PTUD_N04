package com.team.invoice.dao;

import com.team.invoice.entity.KhachThue;
import com.team.invoice.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhachThueDAO {

    public List<KhachThue> findAll(String keyword) throws SQLException {
        String baseSql = "SELECT maKhach, hoTen, soCCCD, sdt, trangThai FROM KhachThue WHERE isDeleted = 0";
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String sql = hasKeyword
                ? baseSql + " AND (maKhach LIKE ? OR hoTen LIKE ? OR soCCCD LIKE ? OR sdt LIKE ?) ORDER BY maKhach DESC"
                : baseSql + " ORDER BY maKhach DESC";

        List<KhachThue> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (hasKeyword) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(1, kw);
                ps.setString(2, kw);
                ps.setString(3, kw);
                ps.setString(4, kw);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new KhachThue(
                            rs.getString("maKhach"),
                            rs.getString("hoTen"),
                            rs.getString("soCCCD"),
                            rs.getString("sdt"),
                            rs.getString("trangThai")
                    ));
                }
            }
        }
        return list;
    }

    public boolean insert(KhachThue kh) throws SQLException {
        String sql = "INSERT INTO KhachThue(maKhach, hoTen, soCCCD, sdt, trangThai, isDeleted) VALUES (?, ?, ?, ?, ?, 0)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kh.getMaKhach());
            ps.setString(2, kh.getHoTen());
            ps.setString(3, kh.getSoCCCD());
            ps.setString(4, kh.getSdt());
            ps.setString(5, kh.getTrangThai());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(KhachThue kh) throws SQLException {
        String sql = "UPDATE KhachThue SET hoTen = ?, soCCCD = ?, sdt = ?, trangThai = ? WHERE maKhach = ? AND isDeleted = 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getSoCCCD());
            ps.setString(3, kh.getSdt());
            ps.setString(4, kh.getTrangThai());
            ps.setString(5, kh.getMaKhach());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean softDelete(String maKhach) throws SQLException {
        String sql = "UPDATE KhachThue SET isDeleted = 1 WHERE maKhach = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhach);
            return ps.executeUpdate() > 0;
        }
    }
}
