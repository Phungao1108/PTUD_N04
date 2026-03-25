package com.team.invoice.service;

import java.util.List;

import com.team.invoice.dao.DichVuDAO;
import com.team.invoice.entity.DichVu;

public class DichVuService {
    private DichVuDAO dichVuDAO;

    public DichVuService() {
        this.dichVuDAO = new DichVuDAO();
    }

    // Lấy danh sách tất cả dịch vụ đang hoạt động (isDeleted = 0)
    public List<DichVu> getAllDichVu() {
        try {
            return dichVuDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Trong thực tế có thể trả về list rỗng (new ArrayList<>())
        }
    }

    // Thêm mới dịch vụ
    public String createDichVu(DichVu dv) {
        // 1. Validate dữ liệu đầu vào
        if (dv.getMaDV() == null || dv.getMaDV().trim().isEmpty()) {
            return "Mã dịch vụ không được để trống!";
        }
        if (dv.getTenDV() == null || dv.getTenDV().trim().isEmpty()) {
            return "Tên dịch vụ không được để trống!";
        }
        if (dv.getDonVi() == null || dv.getDonVi().trim().isEmpty()) {
            return "Đơn vị tính không được để trống!";
        }
        if (dv.getLoaiDichVu() == null || dv.getLoaiDichVu().trim().isEmpty()) {
            return "Vui lòng chọn loại dịch vụ (CO_DINH hoặc CHI_SO)!";
        }

        // 2. Gọi DAO để thêm vào DB
        try {
            boolean success = dichVuDAO.insert(dv);
            return success ? "Thành công" : "Thêm mới thất bại. Mã dịch vụ có thể đã tồn tại!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    // Cập nhật thông tin dịch vụ
    public String updateDichVu(DichVu dv) {
        // Validate dữ liệu
        if (dv.getMaDV() == null || dv.getMaDV().trim().isEmpty()) {
            return "Mã dịch vụ không hợp lệ!";
        }
        if (dv.getTenDV() == null || dv.getTenDV().trim().isEmpty()) {
            return "Tên dịch vụ không được để trống!";
        }
        if (dv.getDonVi() == null || dv.getDonVi().trim().isEmpty()) {
            return "Đơn vị tính không được để trống!";
        }

        try {
            boolean success = dichVuDAO.update(dv);
            return success ? "Thành công" : "Cập nhật thất bại. Không tìm thấy dịch vụ.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    // Xóa (mềm) dịch vụ
    public String deleteDichVu(String maDV) {
        if (maDV == null || maDV.trim().isEmpty()) {
            return "Mã dịch vụ không hợp lệ!";
        }

        try {
            // Lưu ý: Sau này có thể thêm logic kiểm tra xem dịch vụ 
            // đã có trong Hóa Đơn nào chưa trước khi cho phép xóa.
            boolean success = dichVuDAO.delete(maDV);
            return success ? "Thành công" : "Xóa thất bại. Không tìm thấy dịch vụ.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }
}