package com.team.invoice.service;


import java.util.List;
import com.team.invoice.dao.ChiPhiDAO;
import com.team.invoice.entity.ChiPhi;

public class ChiPhiService {
    private ChiPhiDAO dao = new ChiPhiDAO();

    public List<String> getDanhSachKy() {
        return dao.layDanhSachCacKy();
    }

    public List<ChiPhi> getChiSoThang(String kyHienTai) {
        // Logic tính kỳ trước (Ví dụ: từ 04/2026 -> 03/2026)
        String kyTruoc = "";
        if (kyHienTai != null && kyHienTai.length() == 7) {
            String[] parts = kyHienTai.split("/");
            int thang = Integer.parseInt(parts[0]);
            int nam = Integer.parseInt(parts[1]);
            if (thang == 1) { thang = 12; nam -= 1; } 
            else { thang -= 1; }
            kyTruoc = String.format("%02d/%04d", thang, nam);
        }
        return dao.layChiSoTheoKy(kyHienTai, kyTruoc);
    }

    public String luuDanhSachChiSo(List<ChiPhi> list) {
        // Validate trước khi lưu (Kiểm tra chỉ số mới phải lớn hơn chỉ số cũ)
        for (ChiPhi cp : list) {
            if (cp.getDienMoi() > 0 && cp.getDienMoi() < cp.getDienCu()) {
                return "Lỗi ở phòng " + cp.getTenPhong() + ": Số điện mới nhỏ hơn số cũ!";
            }
            if (cp.getNuocMoi() > 0 && cp.getNuocMoi() < cp.getNuocCu()) {
                return "Lỗi ở phòng " + cp.getTenPhong() + ": Số nước mới nhỏ hơn số cũ!";
            }
        }
        
        boolean success = dao.luuChiPhi(list);
        return success ? "Thành công" : "Lỗi cập nhật dữ liệu vào DB!";
    }
}