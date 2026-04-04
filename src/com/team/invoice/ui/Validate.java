package com.team.invoice.ui;

import com.team.invoice.dao.KhachThueDAO;
import com.team.invoice.entity.KhachThue;

import java.sql.SQLException;
import java.util.List;

public class Validate {
    private Validate() {}

    public static String normalizePersonName(String input) {
        if (input == null) return "";
        String cleaned = input.trim().replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) return "";

        StringBuilder builder = new StringBuilder();
        for (String part : cleaned.split(" ")) {
            if (part.isEmpty()) continue;
            String lower = part.toLowerCase();
            builder.append(Character.toUpperCase(lower.charAt(0)));
            if (lower.length() > 1) {
                builder.append(lower.substring(1));
            }
            builder.append(' ');
        }
        return builder.toString().trim();
    }

    public static String validateKhachThue(KhachThueDAO dao, String maKhach, String hoTen, String cccd, String sdt) throws SQLException {
        String normalizedName = normalizePersonName(hoTen);
        if (normalizedName.isEmpty()) {
            return "Họ tên không được để trống";
        }
        if (!normalizedName.matches("[\\p{L}]+(?: [\\p{L}]+)+")) {
            return "Họ tên phải có ít nhất 2 từ và chỉ gồm chữ cái";
        }
        if (!normalizedName.equals(hoTen.trim().replaceAll("\\s+", " "))) {
            return "Họ tên phải viết hoa chữ cái đầu, các chữ còn lại viết thường";
        }

        if (cccd == null || !cccd.matches("\\d{12}")) {
            return "CCCD phải gồm đúng 12 chữ số";
        }

        if (sdt == null || !sdt.matches("0\\d{9}")) {
            return "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0";
        }

        List<KhachThue> danhSach = dao.findAll(null);
        for (KhachThue item : danhSach) {
            if (maKhach != null && maKhach.equals(item.getMaKhach())) {
                continue;
            }
            if (cccd.equals(item.getSoCCCD())) {
                return "CCCD đã tồn tại trong hệ thống";
            }
            if (sdt.equals(item.getSdt())) {
                return "Số điện thoại đã tồn tại trong hệ thống";
            }
        }
        return null;
    }
}
