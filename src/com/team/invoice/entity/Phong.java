package com.team.invoice.entity;

public class Phong {
    private String maPhong;      // map với CoSoVatChat.id
    private String tenPhong;     // map với CoSoVatChat.ten
    private String maLoaiPhong;
    private String trangThai;    // map với CoSoVatChat.trangThaiPhong
    private String idCha;        // tầng cha
    private boolean isDeleted;

    public Phong() {
    }

    public Phong(String maPhong, String tenPhong, String maLoaiPhong, String trangThai, String idCha, boolean isDeleted) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.maLoaiPhong = maLoaiPhong;
        this.trangThai = trangThai;
        this.idCha = idCha;
        this.isDeleted = isDeleted;
    }

    public String getToa() {
        if (this.maPhong == null || this.maPhong.isEmpty()) {
            return "Khác";
        }
        // Dùng Regex giữ lại toàn bộ chữ cái, loại bỏ số (VD: "A101" -> "A", "b205" -> "B")
        String toa = this.maPhong.replaceAll("[^a-zA-Z]", "").trim();
        return toa.isEmpty() ? "Khác" : toa.toUpperCase();
    }

    public String getTang() {
        if (this.maPhong == null || this.maPhong.isEmpty()) {
            return "Khác";
        }
        // Dùng Regex giữ lại toàn bộ số, loại bỏ chữ (VD: "A101" -> "101", "B1205" -> "1205")
        String numbers = this.maPhong.replaceAll("[^0-9]", "");
        
        // Quy tắc: 2 số cuối là số phòng, phần đứng trước là tầng
        // VD: 101 -> Tầng 1 | 205 -> Tầng 2 | 1205 -> Tầng 12
        if (numbers.length() >= 3) {
            return numbers.substring(0, numbers.length() - 2);
        } else if (!numbers.isEmpty()) {
            return numbers; // Dự phòng cho trường hợp người nhập mã phòng chỉ có 1-2 chữ số
        }
        
        return "Khác";
    }
    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public String getMaLoaiPhong() {
        return maLoaiPhong;
    }

    public void setMaLoaiPhong(String maLoaiPhong) {
        this.maLoaiPhong = maLoaiPhong;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getIdCha() {
        return idCha;
    }

    public void setIdCha(String idCha) {
        this.idCha = idCha;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
