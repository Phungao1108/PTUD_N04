package com.team.invoice.service;

import com.team.invoice.dao.PhongDAO;
import com.team.invoice.entity.Phong;

import java.sql.SQLException;
import java.util.List;

public class PhongService {
    private final PhongDAO dao = new PhongDAO();

    public List<Phong> getAllPhong() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createPhong(Phong p) {
        try {
            if (dao.existsByMaPhong(p.getMaPhong())) {
                return "Mã phòng đã tồn tại, vui lòng nhập mã khác!";
            }

            if (dao.existsByTenPhong(p.getTenPhong())) {
                return "Tên phòng đã tồn tại, vui lòng nhập tên khác!";
            }

            boolean ok = dao.insert(p);
            return ok ? "Thành công" : "Không thể thêm phòng";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi hệ thống khi thêm phòng!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi hệ thống khi thêm phòng!";
        }
    }

    public String updatePhong(Phong p) {
        try {
            if (dao.existsByTenPhongExceptMaPhong(p.getTenPhong(), p.getMaPhong())) {
                return "Tên phòng đã tồn tại, vui lòng nhập tên khác!";
            }

            boolean ok = dao.update(p);
            return ok ? "Thành công" : "Không thể cập nhật phòng";
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi hệ thống khi cập nhật phòng!";
        }
    }

    public String deletePhong(String maPhong) {
        try {
            boolean ok = dao.delete(maPhong);
            return ok ? "Thành công" : "Không thể xóa phòng";
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi hệ thống khi xóa phòng!";
        }
    }
}