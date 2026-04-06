package com.team.invoice.service;

import java.sql.SQLException;
import java.util.List;

import com.team.invoice.dao.PhongDAO;
import com.team.invoice.entity.Phong;

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

    public List<String> getAllTangIds() {
        try {
            return dao.findAllTangIds();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createPhong(Phong p) {
        try {
			if (p == null) {
				return "Dữ liệu phòng không hợp lệ!";
			}
			if (p.getMaPhong() == null || p.getMaPhong().trim().isEmpty()) {
				return "Mã phòng không được để trống!";
			}
            if (dao.existsByMaPhong(p.getMaPhong())) {
                return "Mã phòng đã tồn tại, vui lòng nhập mã khác!";
            }

            if (dao.existsByTenPhong(p.getTenPhong())) {
                return "Tên phòng đã tồn tại, vui lòng nhập tên khác!";
            }

            boolean ok = dao.insert(p);
            return ok ? "Thành công" : "Không thể thêm phòng!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi SQL: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String updatePhong(Phong p) {
        try {
            if (dao.existsByTenPhongExceptMaPhong(p.getTenPhong(), p.getMaPhong())) {
                return "Tên phòng đã tồn tại, vui lòng nhập tên khác!";
            }

            boolean ok = dao.update(p);
            return ok ? "Thành công" : "Không thể cập nhật phòng!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi SQL: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }
 // Gọi DAO để lấy danh sách mã Tòa
    public List<String> getAllToaIds() {
        try {
            return dao.findAllToaIds();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Xử lý logic và gọi DAO để thêm mới Cơ sở vật chất
    public String createCSVC(Phong p, String loaiCsvc) {
        try {
            // Kiểm tra trùng ID trên toàn bộ bảng
            if (dao.existsById(p.getMaPhong())) {
                return "Mã cơ sở vật chất đã tồn tại trên hệ thống, vui lòng nhập mã khác!";
            }
            
            // Thực hiện thêm mới
            boolean ok = dao.insertCSVC(p, loaiCsvc);
            return ok ? "Thành công" : "Không thể thêm dữ liệu!";
            
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi SQL: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }
    public List<Object[]> getCSVCTreeData() {
        try {
            return dao.findAllCSVCForTree();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String deletePhong(String maPhong) {
        try {
            boolean ok = dao.delete(maPhong);
            return ok ? "Thành công" : "Không thể xóa phòng!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi SQL: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }
}