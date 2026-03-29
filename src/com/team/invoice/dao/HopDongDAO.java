package com.team.invoice.dao;

import com.team.invoice.entity.HopDong;
import com.team.invoice.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HopDongDAO {

    public List<Object[]> findAll() throws SQLException {
        String sql = "SELECT h.maHopDong, h.maPhong, k.hoTen, h.ngayBatDau, h.ngayKetThuc, h.tienDatCoc, h.trangThai " +
                     "FROM HopDong h " +
                     "LEFT JOIN KhachThue k ON h.maKhachChinh = k.maKhach " +
                     "WHERE h.isDeleted = 0 ORDER BY h.maHopDong DESC";
        List<Object[]> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getDate(4), rs.getDate(5), rs.getBigDecimal(6), rs.getString(7)
                });
            }
        }
        return list;
    }

    public List<String> findAvailableRoomIds() throws SQLException {
        String sql = "SELECT c.id FROM CoSoVatChat c " +
                     "WHERE c.loai = 'PHONG' AND c.isDeleted = 0 " +
                     "AND NOT EXISTS (SELECT 1 FROM HopDong h WHERE h.maPhong = c.id AND h.trangThai = 'HIEU_LUC' AND h.isDeleted = 0) " +
                     "ORDER BY c.id";
        List<String> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        }
        return list;
    }

    public boolean insert(HopDong hd) throws SQLException {
        String sql = "INSERT INTO HopDong(maHopDong, maPhong, maKhachChinh, ngayBatDau, ngayKetThuc, tienDatCoc, trangThai, isDeleted) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, 0)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hd.getMaHopDong());
            ps.setString(2, hd.getMaPhong());
            ps.setString(3, hd.getMaKhachChinh());
            ps.setDate(4, hd.getNgayBatDau());
            ps.setDate(5, hd.getNgayKetThuc());
            ps.setBigDecimal(6, hd.getTienDatCoc());
            ps.setString(7, hd.getTrangThai());
            return ps.executeUpdate() > 0;
        }
    }
}
