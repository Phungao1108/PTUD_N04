package com.team.invoice.service;

import com.team.invoice.dao.HopDongDAO;
import com.team.invoice.entity.HopDong;
import com.team.invoice.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HopDongService {
    private final HopDongDAO hopDongDAO = new HopDongDAO();

    public boolean taoHopDong(HopDong hd) throws SQLException {
        boolean ok = hopDongDAO.insert(hd);
        if (ok) {
            capNhatTrangThaiPhong(hd.getMaPhong(), "DA_THUE");
        }
        return ok;
    }

    private void capNhatTrangThaiPhong(String maPhong, String trangThai) throws SQLException {
        String sql = "UPDATE CoSoVatChat SET trangThaiPhong = ? WHERE id = ? AND loai = 'PHONG'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, maPhong);
            ps.executeUpdate();
        }
    }
}
