package com.team.invoice.service;

import java.util.List;

import com.team.invoice.dao.LoaiPhongDAO;
import com.team.invoice.entity.LoaiPhong;

public class LoaiPhongService {
    private LoaiPhongDAO loaiPhongDAO = new LoaiPhongDAO();

    public List<LoaiPhong> getAllLoaiPhong() {
        try {
            return loaiPhongDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createLoaiPhong(LoaiPhong lp) {
        if (lp.getMaLoaiPhong() == null || lp.getMaLoaiPhong().trim().isEmpty()) return "Mã loại phòng không được để trống!";
        if (lp.getTenLoaiPhong() == null || lp.getTenLoaiPhong().trim().isEmpty()) return "Tên loại phòng không được để trống!";
        
        try {
            return loaiPhongDAO.insert(lp) ? "Thành công" : "Thêm mới thất bại. Mã có thể đã tồn tại!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    public String updateLoaiPhong(LoaiPhong lp) {
        if (lp.getMaLoaiPhong() == null || lp.getMaLoaiPhong().trim().isEmpty()) return "Mã loại phòng không hợp lệ!";
        try {
            return loaiPhongDAO.update(lp) ? "Thành công" : "Cập nhật thất bại.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    public String deleteLoaiPhong(String maLoaiPhong) {
        if (maLoaiPhong == null || maLoaiPhong.trim().isEmpty()) return "Mã loại phòng không hợp lệ!";
        try {
            return loaiPhongDAO.delete(maLoaiPhong) ? "Thành công" : "Xóa thất bại.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }
}