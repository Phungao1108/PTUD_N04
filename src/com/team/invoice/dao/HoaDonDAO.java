package com.team.invoice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.team.invoice.entity.Invoice;
import com.team.invoice.entity.InvoiceStatus;
import com.team.invoice.entity.Room;
import com.team.invoice.util.DBConnection;

public class HoaDonDAO {
    private static final SimpleDateFormat PERIOD_DATE = new SimpleDateFormat("MM/yyyy");

    public List<Room> findRoomsForInvoice() {
        List<Room> rooms = new ArrayList<>();
        String sql = """
            SELECT p.id AS maPhong,
                   k.hoTen AS tenKhach,
                   lp.maLoaiPhong,
                   ISNULL(dp.giaTheoThang, 0) AS giaPhong,
                   ISNULL(MAX(CASE WHEN dv.tenDV = N'Điện' THEN dgd.gia END), 0) AS giaDien,
                   ISNULL(MAX(CASE WHEN dv.tenDV = N'Nước' THEN dgd.gia END), 0) AS giaNuoc,
                   ISNULL(SUM(CASE WHEN dv.loaiDichVu = 'CO_DINH' THEN dgd.gia ELSE 0 END), 0) AS phiDichVuCoDinh,
                   ISNULL(cp.dien_moi, 0) AS dienCu,
                   ISNULL(cp.nuoc_moi, 0) AS nuocCu
            FROM CoSoVatChat p
            JOIN HopDong hd ON hd.maPhong = p.id AND hd.trangThai = 'HIEU_LUC' AND hd.isDeleted = 0
            JOIN KhachThue k ON k.maKhach = hd.maKhachChinh AND k.isDeleted = 0
            LEFT JOIN LoaiPhong lp ON lp.maLoaiPhong = p.maLoaiPhong
            OUTER APPLY (
                SELECT TOP 1 bg.maBG
                FROM BangGia bg
                WHERE bg.isDeleted = 0
                  AND bg.trangThai = 'DANG_AP_DUNG'
                ORDER BY bg.ngayHieuLuc DESC
            ) bgAct
            LEFT JOIN DonGiaPhong dp ON dp.maBG = bgAct.maBG AND dp.maLoaiPhong = p.maLoaiPhong
            LEFT JOIN DonGiaDichVu dgd ON dgd.maBG = bgAct.maBG
            LEFT JOIN DichVu dv ON dv.maDV = dgd.maDV AND dv.isDeleted = 0
            OUTER APPLY (
                SELECT TOP 1 c.dien_moi, c.nuoc_moi
                FROM ChiPhi c
                WHERE c.maPhong = p.id
                ORDER BY c.ngayGhi DESC, c.maCS DESC
            ) cp
            WHERE p.loai = 'PHONG' AND p.isDeleted = 0 AND p.trangThaiPhong = 'DANG_THUE'
            GROUP BY p.id, k.hoTen, lp.maLoaiPhong, dp.giaTheoThang, cp.dien_moi, cp.nuoc_moi
            ORDER BY p.id
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rooms.add(new Room(
                        rs.getString("maPhong"),
                        rs.getString("tenKhach"),
                        true,
                        rs.getDouble("giaPhong"),
                        rs.getDouble("giaDien"),
                        rs.getDouble("giaNuoc"),
                        rs.getDouble("phiDichVuCoDinh"),
                        rs.getInt("dienCu"),
                        rs.getInt("nuocCu")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public List<Invoice> findAllInvoices() {
        List<Invoice> list = new ArrayList<>();
        String sql = """
            SELECT h.maHoaDon, h.ky, h.ngayLap, h.trangThai,
                   v.TongTienThanhToan,
                   p.id AS maPhong,
                   k.hoTen AS tenKhach,
                   due.tenNoiDung AS hanThanhToanText
            FROM HoaDon h
            JOIN v_HoaDonTong v ON v.maHoaDon = h.maHoaDon
            JOIN CoSoVatChat p ON p.id = h.maPhong
            LEFT JOIN HopDong hd ON hd.maPhong = h.maPhong AND hd.trangThai = 'HIEU_LUC' AND hd.isDeleted = 0
            LEFT JOIN KhachThue k ON k.maKhach = hd.maKhachChinh
            OUTER APPLY (
                SELECT TOP 1 ct.tenNoiDung
                FROM ChiTietHoaDon ct
                WHERE ct.maHoaDon = h.maHoaDon
                  AND ct.tenNoiDung LIKE N'Hạn thanh toán:%'
                ORDER BY ct.maCT
            ) due
            WHERE h.isDeleted = 0
            ORDER BY h.ngayLap DESC, h.maHoaDon DESC
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setCode(rs.getString("maHoaDon"));
                invoice.setPeriod(rs.getString("ky"));
                java.sql.Timestamp created = rs.getTimestamp("ngayLap");
                invoice.setCreatedAt(created);
                invoice.setStatus(mapStatus(rs.getString("trangThai")));
                invoice.setTotal(rs.getDouble("TongTienThanhToan"));
                invoice.setDueDate(parseSavedDueDate(rs.getString("hanThanhToanText"), created));
                Room room = new Room(rs.getString("maPhong"), rs.getString("tenKhach"), true, 0, 0, 0, 0, 0, 0);
                invoice.setRoom(room);
                list.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> findInvoiceDetails(String maHoaDon) {
        List<String> details = new ArrayList<>();
        String sql = "SELECT tenNoiDung, soLuong, donGiaSnapshot, thanhTien FROM ChiTietHoaDon WHERE maHoaDon = ? ORDER BY maCT";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String line = String.format("%s: %s x %,.0f = %,.0f VNĐ",
                        rs.getString("tenNoiDung"),
                        stripTrailingZero(rs.getDouble("soLuong")),
                        rs.getDouble("donGiaSnapshot"),
                        rs.getDouble("thanhTien"));
                details.add(line);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    public boolean existsInvoiceForRoomPeriod(String maPhong, String ky) {
        try (Connection conn = DBConnection.getConnection()) {
            return existsInvoiceForRoomPeriod(conn, maPhong, ky, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean existsInvoiceForRoomPeriod(Connection conn, String maPhong, String ky, String excludeMaHoaDon) throws SQLException {
        String normalizedKy = normalizePeriod(ky);
        StringBuilder sql = new StringBuilder("SELECT 1 FROM HoaDon WHERE maPhong = ? AND REPLACE(ky, ' ', '') = ? AND isDeleted = 0");
        if (excludeMaHoaDon != null && !excludeMaHoaDon.trim().isEmpty()) {
            sql.append(" AND maHoaDon <> ?");
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setString(1, maPhong);
            stmt.setString(2, normalizedKy.replace(" ", ""));
            if (excludeMaHoaDon != null && !excludeMaHoaDon.trim().isEmpty()) {
                stmt.setString(3, excludeMaHoaDon);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean saveInvoice(Invoice invoice, boolean issueNow) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            if (existsInvoiceForRoomPeriod(conn, invoice.getRoom().getCode(), invoice.getPeriod(), null)) {
                throw new SQLException("Phòng này đã có hóa đơn ở kỳ " + invoice.getPeriod());
            }

            String maHoaDon = nextCode(conn, "HoaDon", "maHoaDon", "INV");
            String maChiPhi = nextCode(conn, "ChiPhi", "maCS", "CS");

            String insertChiPhi = "INSERT INTO ChiPhi(maCS, maPhong, ky, dien_cu, dien_moi, nuoc_cu, nuoc_moi, ngayGhi) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertChiPhi)) {
                stmt.setString(1, maChiPhi);
                stmt.setString(2, invoice.getRoom().getCode());
                stmt.setString(3, normalizePeriod(invoice.getPeriod()));
                stmt.setInt(4, invoice.getRoom().getOldElectric());
                stmt.setInt(5, invoice.getNewElectric());
                stmt.setInt(6, invoice.getRoom().getOldWater());
                stmt.setInt(7, invoice.getNewWater());
                stmt.setDate(8, new java.sql.Date(System.currentTimeMillis()));
                stmt.executeUpdate();
            }

            String insertHoaDon = "INSERT INTO HoaDon(maHoaDon, maPhong, maTK_NguoiLap, ky, ngayLap, trangThai, isDeleted) VALUES(?, ?, ?, ?, ?, ?, 0)";
            try (PreparedStatement stmt = conn.prepareStatement(insertHoaDon)) {
                stmt.setString(1, maHoaDon);
                stmt.setString(2, invoice.getRoom().getCode());
                stmt.setString(3, "TK01");
                stmt.setString(4, normalizePeriod(invoice.getPeriod()));
                stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                stmt.setString(6, mapStatus(issueNow ? InvoiceStatus.PENDING : InvoiceStatus.DRAFT));
                stmt.executeUpdate();
            }

            int seq = 1;
            insertDetail(conn, maHoaDon, maHoaDon + "-CT" + String.format("%02d", seq++), "Tiền phòng", 1, invoice.getRoomFee());
            insertDetail(conn, maHoaDon, maHoaDon + "-CT" + String.format("%02d", seq++), "Tiền điện", invoice.getElectricUsage(), invoice.getRoom().getElectricUnitPrice());
            insertDetail(conn, maHoaDon, maHoaDon + "-CT" + String.format("%02d", seq++), "Tiền nước", invoice.getWaterUsage(), invoice.getRoom().getWaterUnitPrice());
            if (invoice.getServiceFee() > 0) {
                insertDetail(conn, maHoaDon, maHoaDon + "-CT" + String.format("%02d", seq++), "Phí dịch vụ cố định", 1, invoice.getServiceFee());
            }
            if (invoice.getExtraFee() > 0) {
                insertDetail(conn, maHoaDon, maHoaDon + "-CT" + String.format("%02d", seq++), "Phụ phí khác", 1, invoice.getExtraFee());
            }

            if (invoice.getDueDate() != null) {
                insertDetail(conn, maHoaDon, maHoaDon + "-CT" + String.format("%02d", seq++), "Hạn thanh toán: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(invoice.getDueDate()), 1, 0);
            }
            if (invoice.getNote() != null && !invoice.getNote().trim().isEmpty()) {
                insertDetail(conn, maHoaDon, maHoaDon + "-CT" + String.format("%02d", seq++), "Ghi chú: " + invoice.getNote().trim(), 1, 0);
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public boolean markPaid(String maHoaDon) {
        return updateStatus(maHoaDon, "DA_THANH_TOAN");
    }

    public boolean softDelete(String maHoaDon) {
        String sql = "UPDATE HoaDon SET isDeleted = 1 WHERE maHoaDon = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateStatus(String maHoaDon, String status) {
        String sql = "UPDATE HoaDon SET trangThai = ? WHERE maHoaDon = ? AND isDeleted = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, maHoaDon);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateInvoiceMeta(String maHoaDon, String period, java.util.Date dueDate, String note) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String sql = "UPDATE HoaDon SET ky = ? WHERE maHoaDon = ? AND isDeleted = 0";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, normalizePeriod(period));
                stmt.setString(2, maHoaDon);
                if (stmt.executeUpdate() <= 0) {
                    throw new SQLException("Không tìm thấy hóa đơn để cập nhật.");
                }
            }

            String deleteMeta = "DELETE FROM ChiTietHoaDon WHERE maHoaDon = ? AND (tenNoiDung LIKE N'Hạn thanh toán:%' OR tenNoiDung LIKE N'Ghi chú:%')";
            try (PreparedStatement stmt = conn.prepareStatement(deleteMeta)) {
                stmt.setString(1, maHoaDon);
                stmt.executeUpdate();
            }

            int seq = findNextDetailSeq(conn, maHoaDon);
            if (dueDate != null) {
                insertDetail(conn, maHoaDon, maHoaDon + "-CT" + String.format("%02d", seq++),
                        "Hạn thanh toán: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(dueDate), 1, 0);
            }
            if (note != null && !note.trim().isEmpty()) {
                insertDetail(conn, maHoaDon, maHoaDon + "-CT" + String.format("%02d", seq++),
                        "Ghi chú: " + note.trim(), 1, 0);
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    private int findNextDetailSeq(Connection conn, String maHoaDon) throws SQLException {
        String sql = "SELECT maCT FROM ChiTietHoaDon WHERE maHoaDon = ?";
        int max = 0;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String maCT = rs.getString(1);
                    if (maCT != null) {
                        String digits = maCT.replaceAll(".*-CT", "").replaceAll("[^0-9]", "");
                        if (!digits.isEmpty()) {
                            max = Math.max(max, Integer.parseInt(digits));
                        }
                    }
                }
            }
        }
        return max + 1;
    }

    private void insertDetail(Connection conn, String maHoaDon, String maCT, String ten, double soLuong, double donGia) throws SQLException {
        String sql = "INSERT INTO ChiTietHoaDon(maCT, maHoaDon, tenNoiDung, soLuong, donGiaSnapshot) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maCT);
            stmt.setString(2, maHoaDon);
            stmt.setString(3, ten);
            stmt.setDouble(4, soLuong);
            stmt.setDouble(5, donGia);
            stmt.executeUpdate();
        }
    }

    private String nextCode(Connection conn, String tableName, String idCol, String prefix) throws SQLException {
        String sql = "SELECT MAX(" + idCol + ") FROM " + tableName + " WHERE " + idCol + " LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String last = rs.getString(1);
                if (last == null || last.length() <= prefix.length()) {
                    return prefix + "01";
                }
                String num = last.substring(prefix.length()).replaceAll("[^0-9]", "");
                int next = num.isEmpty() ? 1 : Integer.parseInt(num) + 1;
                return prefix + String.format("%02d", next);
            }
            return prefix + "01";
        }
    }


    private String normalizePeriod(String period) {
        if (period == null) {
            return "";
        }
        String value = period.trim().replace('-', '/').replace('.', '/');
        value = value.replaceAll("\s+", "");
        if (value.matches("\\d{1,2}/\\d{4}")) {
            String[] parts = value.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);
            if (month >= 1 && month <= 12) {
                return String.format("%02d/%04d", month, year);
            }
        }
        return value;
    }

    private java.util.Date parseSavedDueDate(String dueText, java.util.Date createdAt) {
        try {
            if (dueText != null) {
                int idx = dueText.indexOf(":");
                String value = idx >= 0 ? dueText.substring(idx + 1).trim() : dueText.trim();
                if (value.matches("\\d{2}/\\d{2}/\\d{4}")) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    sdf.setLenient(false);
                    return sdf.parse(value);
                }
            }
        } catch (Exception ignored) {}
        if (createdAt == null) return null;
        return new java.util.Date(createdAt.getTime() + 7L * 24 * 60 * 60 * 1000);
    }

    private String stripTrailingZero(double value) {
        if (Math.floor(value) == value) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    private InvoiceStatus mapStatus(String dbStatus) {
        if ("DA_THANH_TOAN".equalsIgnoreCase(dbStatus)) {
            return InvoiceStatus.PAID;
        }
        if ("QUA_HAN".equalsIgnoreCase(dbStatus)) {
            return InvoiceStatus.OVERDUE;
        }
        if ("CHO_THANH_TOAN".equalsIgnoreCase(dbStatus)) {
            return InvoiceStatus.PENDING;
        }
        return InvoiceStatus.DRAFT;
    }

    private String mapStatus(InvoiceStatus status) {
        switch (status) {
            case PAID: return "DA_THANH_TOAN";
            case OVERDUE: return "QUA_HAN";
            case PENDING: return "CHO_THANH_TOAN";
            default: return "BAN_NHAP";
        }
    }
}
