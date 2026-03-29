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
            dongBoTrangThaiPhong(hd.getMaPhong(), hd.getTrangThai());
        }
        return ok;
    }

    public boolean capNhatHopDong(HopDong hd) throws SQLException {
        String oldRoom = hopDongDAO.findRoomByContractId(hd.getMaHopDong());
        String oldStatus = hopDongDAO.findStatusByContractId(hd.getMaHopDong());

        boolean ok = hopDongDAO.update(hd);
        if (ok) {
            if (oldRoom != null && !oldRoom.equals(hd.getMaPhong()) && "HIEU_LUC".equals(oldStatus)) {
                capNhatTrangThaiPhong(oldRoom, "TRONG");
            }
            dongBoTrangThaiPhong(hd.getMaPhong(), hd.getTrangThai());
        }
        return ok;
    }

    public boolean xoaHopDong(String maHopDong) throws SQLException {
        String oldRoom = hopDongDAO.findRoomByContractId(maHopDong);
        String oldStatus = hopDongDAO.findStatusByContractId(maHopDong);

        boolean ok = hopDongDAO.softDelete(maHopDong);
        if (ok && oldRoom != null && "HIEU_LUC".equals(oldStatus)) {
            capNhatTrangThaiPhong(oldRoom, "TRONG");
        }
        return ok;
    }

    private void dongBoTrangThaiPhong(String maPhong, String trangThaiHopDong) throws SQLException {
        if ("HIEU_LUC".equals(trangThaiHopDong)) {
            capNhatTrangThaiPhong(maPhong, "DA_THUE");
        } else {
            capNhatTrangThaiPhong(maPhong, "TRONG");
        }
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
