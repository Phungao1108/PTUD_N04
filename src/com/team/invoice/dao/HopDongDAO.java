package com.team.invoice.dao;

import com.team.invoice.entity.HopDong;
import com.team.invoice.entity.KhachThue;
import com.team.invoice.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HopDongDAO {

    public List<Object[]> findAll() throws SQLException {
        String sql = "SELECT h.maHopDong, c.id + ' - ' + c.ten AS thongTinPhong, " +
                "k.maKhach + ' - ' + k.hoTen AS thongTinKhach, h.ngayBatDau, h.ngayKetThuc, h.tienDatCoc, h.trangThai " +
                "FROM HopDong h " +
                "LEFT JOIN KhachThue k ON h.maKhachChinh = k.maKhach " +
                "LEFT JOIN CoSoVatChat c ON h.maPhong = c.id " +
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

    public List<String> searchRoomSuggestions(String keyword, String excludeContractId) throws SQLException {
        String normalized = keyword == null ? "" : keyword.trim();
        String sql = "SELECT TOP 8 c.id, c.ten FROM CoSoVatChat c " +
                "WHERE c.loai = 'PHONG' AND c.isDeleted = 0 " +
                "AND (c.id LIKE ? OR c.ten LIKE ?) " +
                "AND NOT EXISTS (SELECT 1 FROM HopDong h WHERE h.maPhong = c.id AND h.trangThai = 'HIEU_LUC' AND h.isDeleted = 0" +
                (excludeContractId != null ? " AND h.maHopDong <> ?" : "") +
                ") ORDER BY c.id";
        List<String> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + normalized + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            if (excludeContractId != null) {
                ps.setString(3, excludeContractId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("id") + " - " + rs.getString("ten"));
                }
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

    public boolean update(HopDong hd) throws SQLException {
        String sql = "UPDATE HopDong SET maPhong = ?, maKhachChinh = ?, ngayBatDau = ?, ngayKetThuc = ?, tienDatCoc = ?, trangThai = ? " +
                "WHERE maHopDong = ? AND isDeleted = 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hd.getMaPhong());
            ps.setString(2, hd.getMaKhachChinh());
            ps.setDate(3, hd.getNgayBatDau());
            ps.setDate(4, hd.getNgayKetThuc());
            ps.setBigDecimal(5, hd.getTienDatCoc());
            ps.setString(6, hd.getTrangThai());
            ps.setString(7, hd.getMaHopDong());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean softDelete(String maHopDong) throws SQLException {
        deleteGuestLinks(maHopDong);
        String sql = "UPDATE HopDong SET isDeleted = 1, trangThai = 'DA_HUY' WHERE maHopDong = ? AND isDeleted = 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHopDong);
            return ps.executeUpdate() > 0;
        }
    }

    public String findRoomByContractId(String maHopDong) throws SQLException {
        String sql = "SELECT maPhong FROM HopDong WHERE maHopDong = ? AND isDeleted = 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHopDong);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    public String findStatusByContractId(String maHopDong) throws SQLException {
        String sql = "SELECT trangThai FROM HopDong WHERE maHopDong = ? AND isDeleted = 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHopDong);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    public boolean isRoomOccupiedByOtherActiveContract(String maPhong, String excludeContractId) throws SQLException {
        String sql = "SELECT 1 FROM HopDong WHERE maPhong = ? AND trangThai = 'HIEU_LUC' AND isDeleted = 0 " +
                (excludeContractId != null ? "AND maHopDong <> ?" : "");
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            if (excludeContractId != null) {
                ps.setString(2, excludeContractId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isPrimaryCustomerInOtherActiveContract(String maKhach, String excludeContractId) throws SQLException {
        String sql = "SELECT 1 FROM HopDong WHERE maKhachChinh = ? AND trangThai = 'HIEU_LUC' AND isDeleted = 0 " +
                (excludeContractId != null ? "AND maHopDong <> ?" : "");
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhach);
            if (excludeContractId != null) {
                ps.setString(2, excludeContractId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<KhachThue> searchCustomerSuggestions(String keyword) throws SQLException {
        String normalized = keyword == null ? "" : keyword.trim();
        String sql = "SELECT TOP 8 maKhach, hoTen, soCCCD, sdt, trangThai FROM KhachThue " +
                "WHERE isDeleted = 0 AND trangThai = 'ACTIVE' AND (maKhach LIKE ? OR hoTen LIKE ? OR soCCCD LIKE ? OR sdt LIKE ?) " +
                "ORDER BY hoTen";
        List<KhachThue> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + normalized + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
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

    public void saveGuestPhu(String maHopDong, List<String> guestIds) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement delete = con.prepareStatement("DELETE FROM HopDongKhachThue WHERE maHopDong = ?")) {
                delete.setString(1, maHopDong);
                delete.executeUpdate();
            }
            if (guestIds == null || guestIds.isEmpty()) {
                return;
            }
            try (PreparedStatement insert = con.prepareStatement(
                    "INSERT INTO HopDongKhachThue(maHopDong, maKhach, vaiTro) VALUES (?, ?, 'PHU')")) {
                for (String guestId : guestIds) {
                    insert.setString(1, maHopDong);
                    insert.setString(2, guestId);
                    insert.addBatch();
                }
                insert.executeBatch();
            }
        }
    }

    public List<KhachThue> findGuestPhuByContract(String maHopDong) throws SQLException {
        String sql = "SELECT k.maKhach, k.hoTen, k.soCCCD, k.sdt, k.trangThai " +
                "FROM HopDongKhachThue p JOIN KhachThue k ON p.maKhach = k.maKhach " +
                "WHERE p.maHopDong = ? AND k.isDeleted = 0 ORDER BY k.hoTen";
        List<KhachThue> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHopDong);
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

    public void deleteGuestLinks(String maHopDong) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM HopDongKhachThue WHERE maHopDong = ?")) {
            ps.setString(1, maHopDong);
            ps.executeUpdate();
        }
    }
}
