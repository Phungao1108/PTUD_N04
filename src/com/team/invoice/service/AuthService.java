package com.team.invoice.service;

import java.sql.*;

public class AuthService {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=master;user=sa;password=sapassword;encrypt=true;trustServerCertificate=true;";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "sapassword";

    // Step 1: Check if username exists
    public boolean requestPasswordReset(String username) {
        String sql = "SELECT * FROM TaiKhoan WHERE tenDangNhap = ? AND isDeleted = 0";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Step 2: Reset password (after verification)
    public boolean resetPassword(String username, String newPassword) {
        String sql = "UPDATE TaiKhoan SET matKhauHash = ? WHERE tenDangNhap = ? AND isDeleted = 0";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword); // Sử dụng mật khẩu chuỗi bình thường
            stmt.setString(2, username);
            int updated = stmt.executeUpdate();
            return updated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username, String password) {
        String sql = "SELECT * FROM TaiKhoan WHERE tenDangNhap = ? AND matKhauHash = ? AND isDeleted = 0";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // Sử dụng mật khẩu chuỗi bình thường
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String username, String password) {
        String checkSql = "SELECT * FROM TaiKhoan WHERE tenDangNhap = ?";
        String insertSql = "INSERT INTO TaiKhoan (maTK, tenDangNhap, matKhauHash, vaiTro, trangThai, isDeleted) VALUES (?, ?, ?, 'MANAGER', 'ACTIVE', 0)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // Username exists
            }
            String maTK = "TK" + System.currentTimeMillis();
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, maTK);
                insertStmt.setString(2, username);
                insertStmt.setString(3, password); // Sử dụng mật khẩu chuỗi bình thường
                insertStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT 1 FROM TaiKhoan WHERE tenDangNhap = ? AND isDeleted = 0";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}