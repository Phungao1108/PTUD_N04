package com.team.invoice.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Thay đổi thông tin kết nối phù hợp với máy của bạn
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=master;user=sa;password=sapassword;encrypt=true;trustServerCertificate=true;";
    
    // Nạp driver JDBC (chỉ cần chạy 1 lần)
    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy JDBC Driver: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}