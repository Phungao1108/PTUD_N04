package com.team.invoice.service;

import java.util.List;

import com.team.invoice.dao.BangGiaDAO;
import com.team.invoice.entity.BangGia;
import com.team.invoice.entity.DonGiaDichVu;
import com.team.invoice.entity.DonGiaPhong;

public class BangGiaService {
	private BangGiaDAO bangGiaDAO = new BangGiaDAO();

	public List<BangGia> getAllBangGia() {
		return bangGiaDAO.findAllActive();
	}

	public List<DonGiaPhong> getDonGiaPhongByBG(String maBG) {
		return bangGiaDAO.findDonGiaPhongByMaBG(maBG);
	}

	public List<DonGiaDichVu> getDonGiaDichVuByBG(String maBG) {
		return bangGiaDAO.findDonGiaDichVuByMaBG(maBG);
	}

	// Logic xử lý nghiệp vụ khi tạo bảng giá mới
	public String createBangGia(BangGia bg, List<DonGiaPhong> phongs, List<DonGiaDichVu> dichVus) {
		if (bg.getMaBG() == null || bg.getMaBG().trim().isEmpty()) {
			return "Mã Bảng Giá không được để trống!";
		}
		if (bg.getNgayHieuLuc() == null) {
			return "Ngày hiệu lực không hợp lệ!";
		}

		boolean success = bangGiaDAO.insertBangGiaMoi(bg, phongs, dichVus);
		return success ? "Thành công" : "Có lỗi xảy ra trong quá trình lưu Bảng Giá. Vui lòng thử lại.";
	}

	public String updateGiaPhong(String maBG, String maLoaiPhong, double giaMoi) {
		if (giaMoi < 0)
			return "Mức giá không được âm!";
		return bangGiaDAO.updateGiaPhong(maBG, maLoaiPhong, giaMoi) ? "Thành công" : "Lỗi cập nhật CSDL!";
	}

	public String updateGiaDichVu(String maBG, String maDV, double giaMoi) {
		if (giaMoi < 0)
			return "Mức giá không được âm!";
		return bangGiaDAO.updateGiaDichVu(maBG, maDV, giaMoi) ? "Thành công" : "Lỗi cập nhật CSDL!";
	}

	// Tính năng Đồng bộ: Tự động tìm các Loại Phòng / Dịch Vụ mới tạo và thêm vào
	// đợt giá hiện tại
	public String boSungDonGiaThieu(String maBG) {
		int countPhong = 0;
		int countDV = 0;

		// Đồng bộ Loại Phòng
		com.team.invoice.service.LoaiPhongService lpService = new com.team.invoice.service.LoaiPhongService();
		List<com.team.invoice.entity.LoaiPhong> allLP = lpService.getAllLoaiPhong();
		List<DonGiaPhong> existingDP = bangGiaDAO.findDonGiaPhongByMaBG(maBG);

		for (com.team.invoice.entity.LoaiPhong lp : allLP) {
			boolean exists = existingDP.stream().anyMatch(d -> d.getMaLoaiPhong().equals(lp.getMaLoaiPhong()));
			if (!exists) {
				if (bangGiaDAO.insertGiaPhong(maBG, lp.getMaLoaiPhong(), 0))
					countPhong++;
			}
		}

		// Đồng bộ Dịch Vụ
		com.team.invoice.service.DichVuService dvService = new com.team.invoice.service.DichVuService();
		List<com.team.invoice.entity.DichVu> allDV = dvService.getAllDichVu();
		List<DonGiaDichVu> existingDV = bangGiaDAO.findDonGiaDichVuByMaBG(maBG);

		for (com.team.invoice.entity.DichVu dv : allDV) {
			boolean exists = existingDV.stream().anyMatch(d -> d.getMaDV().equals(dv.getMaDV()));
			if (!exists) {
				if (bangGiaDAO.insertGiaDichVu(maBG, dv.getMaDV(), 0))
					countDV++;
			}
		}

		if (countPhong == 0 && countDV == 0)
			return "Mọi đơn giá trong đợt này đã được cập nhật đầy đủ!";
		return "Đã bổ sung " + countPhong + " loại phòng và " + countDV
				+ " dịch vụ mới vào đợt giá. Vui lòng cập nhật số tiền!";
	}
	public String createBangGiaToanDien(BangGia bg, List<DonGiaPhong> phongs, List<DonGiaDichVu> dichVus) {
        if (bg.getMaBG() == null || bg.getMaBG().trim().isEmpty()) {
            return "Mã Bảng Giá không được để trống!";
        }
        if (bg.getNgayHieuLuc() == null) {
            return "Vui lòng chọn ngày bắt đầu hiệu lực!";
        }

        long todayMillis = System.currentTimeMillis();
        long startMillis = bg.getNgayHieuLuc().getTime();
        
        // Logic xác định trạng thái
        if (startMillis > todayMillis) {
            bg.setTrangThai("CHUA_AP_DUNG"); // Tương lai
        } else {
            // Nếu ngày bắt đầu <= hiện tại
            if (bg.getNgayKetThuc() != null && bg.getNgayKetThuc().getTime() < todayMillis) {
                bg.setTrangThai("HET_HIEU_LUC"); // Quá khứ
            } else {
                bg.setTrangThai("DANG_AP_DUNG"); // Hiện tại
                
                // --- QUAN TRỌNG: CHỐT BẢNG GIÁ CŨ ---
                // Nếu bảng giá mới là DANG_AP_DUNG, ta phải tìm bảng giá cũ đang áp dụng để chốt nó lại
                String oldActiveId = bangGiaDAO.findActiveBangGiaId();
                if (oldActiveId != null && !oldActiveId.equals(bg.getMaBG())) {
                    // Chốt bảng giá cũ vào ngày hôm nay
                    bangGiaDAO.chotDotGiaCu(oldActiveId, new java.util.Date());
                }
            }
        }

        // Lưu bảng giá mới cùng với toàn bộ chi tiết
        boolean success = bangGiaDAO.insertBangGiaMoi(bg, phongs, dichVus);
        return success ? "Thành công" : "Lỗi lưu cơ sở dữ liệu!";
    }
	// Lấy giá của loại phòng theo bảng giá đang áp dụng (Dành cho hiển thị UI)
		public String getGiaPhongChoUI(String maLoaiPhong) {
			String activeMaBG = bangGiaDAO.findActiveBangGiaId();
			
			// Nếu không có đợt giá nào đang áp dụng
			if (activeMaBG == null) {
				return "Chưa cấu hình";
			}

			List<DonGiaPhong> listGia = bangGiaDAO.findDonGiaPhongByMaBG(activeMaBG);
			for (DonGiaPhong dp : listGia) {
				if (dp.getMaLoaiPhong().equals(maLoaiPhong)) {
					// Nếu giá có trong DB và lớn hơn 0 thì format, ngược lại báo chưa cấu hình
					if (dp.getGiaTheoThang() > 0) {
						return String.format("%,.0f VNĐ", dp.getGiaTheoThang());
					} else {
						return "Chưa cấu hình";
					}
				}
			}
			
			// Nếu loại phòng này chưa từng được thêm vào đợt giá
			return "Chưa cấu hình";
		}
		// =====================================================================
		// CÁC HÀM LẤY SỐ LIỆU TÍNH TOÁN CHO MODULE KHÁC (HÓA ĐƠN, HỢP ĐỒNG)
		// =====================================================================
		
		/**
		 * Lấy giá tiền chính xác của Loại phòng theo bảng giá đang áp dụng.
		 * Dùng để nhân/cộng/trừ khi tính tiền phòng.
		 */
		public double getDonGiaPhongDeTinhToan(String maLoaiPhong) {
			return bangGiaDAO.getGiaLoaiPhongActive(maLoaiPhong);
		}

		/**
		 * Lấy giá tiền chính xác của Dịch vụ (Điện, Nước, Rác...) theo bảng giá đang áp dụng.
		 * Dùng để nhân với chỉ số tiêu thụ.
		 */
		public double getDonGiaDichVuDeTinhToan(String maDV) {
			return bangGiaDAO.getGiaDichVuActive(maDV);
		}
	public boolean chotDotGiaCu(String maBG, java.util.Date ngayKetThuc) {
        return bangGiaDAO.chotDotGiaCu(maBG, ngayKetThuc);
    }
	public String deleteBangGia(String maBG) {
	    if (maBG == null || maBG.isEmpty()) return "Mã bảng giá không hợp lệ!";
	    // Bạn có thể thêm kiểm tra xem bảng giá này có đang được dùng trong hóa đơn nào không trước khi xóa
	    return bangGiaDAO.deleteBangGiaToanBo(maBG) ? "Thành công" : "Lỗi khi xóa bảng giá và các đơn giá liên quan!";
	}
}