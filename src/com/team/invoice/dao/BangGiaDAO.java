package com.team.invoice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.team.invoice.entity.BangGia;
import com.team.invoice.entity.DonGiaDichVu;
import com.team.invoice.entity.DonGiaPhong;
import com.team.invoice.util.DBConnection;

public class BangGiaDAO {

    public List<BangGia> findAllActive() {
        List<BangGia> list = new ArrayList<>();
        String sql = "SELECT * FROM BangGia WHERE isDeleted = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new BangGia(
                        rs.getString("maBG"), rs.getDate("ngayHieuLuc"),
                        rs.getDate("ngayKetThuc"), rs.getString("trangThai"),
                        rs.getBoolean("isDeleted")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy chi tiết đơn giá phòng của một bảng giá cụ thể, kèm theo tên loại phòng để hiển thị UI
    public List<DonGiaPhong> findDonGiaPhongByMaBG(String maBG) {
        List<DonGiaPhong> list = new ArrayList<>();
        String sql = "SELECT dp.*, lp.tenLoaiPhong FROM DonGiaPhong dp " +
                     "JOIN LoaiPhong lp ON dp.maLoaiPhong = lp.maLoaiPhong " +
                     "WHERE dp.maBG = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maBG);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DonGiaPhong dp = new DonGiaPhong(
                        rs.getString("maBG"), rs.getString("maLoaiPhong"), rs.getDouble("giaTheoThang")
                );
                dp.setTenLoaiPhong(rs.getString("tenLoaiPhong"));
                list.add(dp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy chi tiết đơn giá dịch vụ
    public List<DonGiaDichVu> findDonGiaDichVuByMaBG(String maBG) {
        List<DonGiaDichVu> list = new ArrayList<>();
        String sql = "SELECT dd.*, dv.tenDV, dv.donVi FROM DonGiaDichVu dd " +
                     "JOIN DichVu dv ON dd.maDV = dv.maDV " +
                     "WHERE dd.maBG = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maBG);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DonGiaDichVu dd = new DonGiaDichVu(
                        rs.getString("maBG"), rs.getString("maDV"), rs.getDouble("gia")
                );
                dd.setTenDV(rs.getString("tenDV"));
                dd.setDonVi(rs.getString("donVi"));
                list.add(dd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
 // Cập nhật giá 1 loại phòng
    public boolean updateGiaPhong(String maBG, String maLoaiPhong, double giaMoi) {
        String sql = "UPDATE DonGiaPhong SET giaTheoThang = ? WHERE maBG = ? AND maLoaiPhong = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, giaMoi);
            stmt.setString(2, maBG);
            stmt.setString(3, maLoaiPhong);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật giá 1 dịch vụ
    public boolean updateGiaDichVu(String maBG, String maDV, double giaMoi) {
        String sql = "UPDATE DonGiaDichVu SET gia = ? WHERE maBG = ? AND maDV = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, giaMoi);
            stmt.setString(2, maBG);
            stmt.setString(3, maDV);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Thêm lẻ 1 giá phòng (dùng khi đồng bộ bổ sung)
    public boolean insertGiaPhong(String maBG, String maLoaiPhong, double gia) {
        String sql = "INSERT INTO DonGiaPhong(maBG, maLoaiPhong, giaTheoThang) VALUES(?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maBG);
            stmt.setString(2, maLoaiPhong);
            stmt.setDouble(3, gia);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // Thêm lẻ 1 giá dịch vụ (dùng khi đồng bộ bổ sung)
    public boolean insertGiaDichVu(String maBG, String maDV, double gia) {
        String sql = "INSERT INTO DonGiaDichVu(maBG, maDV, gia) VALUES(?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maBG);
            stmt.setString(2, maDV);
            stmt.setDouble(3, gia);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // --- TRANSACTION LOGIC: LƯU TẤT CẢ CÙNG LÚC ---
    public boolean insertBangGiaMoi(BangGia bg, List<DonGiaPhong> dgpList, List<DonGiaDichVu> dgdList) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Insert Bảng Giá
            String sqlBG = "INSERT INTO BangGia (maBG, ngayHieuLuc, trangThai, isDeleted) VALUES (?, ?, 'DANG_AP_DUNG', 0)";
            try (PreparedStatement stmtBG = conn.prepareStatement(sqlBG)) {
                stmtBG.setString(1, bg.getMaBG());
                stmtBG.setDate(2, new java.sql.Date(bg.getNgayHieuLuc().getTime()));
                stmtBG.executeUpdate();
            }

            // 2. Insert Đơn Giá Phòng
            String sqlDP = "INSERT INTO DonGiaPhong (maBG, maLoaiPhong, giaTheoThang) VALUES (?, ?, ?)";
            try (PreparedStatement stmtDP = conn.prepareStatement(sqlDP)) {
                for (DonGiaPhong dp : dgpList) {
                    stmtDP.setString(1, bg.getMaBG());
                    stmtDP.setString(2, dp.getMaLoaiPhong());
                    stmtDP.setDouble(3, dp.getGiaTheoThang());
                    stmtDP.addBatch(); // Sử dụng Batch cho hiệu năng cao
                }
                stmtDP.executeBatch();
            }

            // 3. Insert Đơn Giá Dịch Vụ
            String sqlDD = "INSERT INTO DonGiaDichVu (maBG, maDV, gia) VALUES (?, ?, ?)";
            try (PreparedStatement stmtDD = conn.prepareStatement(sqlDD)) {
                for (DonGiaDichVu dd : dgdList) {
                    stmtDD.setString(1, bg.getMaBG());
                    stmtDD.setString(2, dd.getMaDV());
                    stmtDD.setDouble(3, dd.getGia());
                    stmtDD.addBatch();
                }
                stmtDD.executeBatch();
            }

            conn.commit(); // Hoàn thành Transaction
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Nếu có lỗi, rollback lại toàn bộ
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public boolean chotDotGiaCu(String maBG, java.util.Date ngayKetThuc) {
        String sql = "UPDATE BangGia SET ngayKetThuc = ?, trangThai = 'HET_HIEU_LUC' WHERE maBG = ?";
        try (java.sql.Connection conn = com.team.invoice.util.DBConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(ngayKetThuc.getTime()));
            stmt.setString(2, maBG);
            return stmt.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
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