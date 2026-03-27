package com.team.invoice.entity;

public class Phong {
    private String maPhong;
    private String tenPhong;
    private String maLoaiPhong;
    private String trangThai;
    private String khachHienTai;
    private double giaThang;
    private double dien;
    private double nuoc;
    private double dichVu;
    private String kyChiSo;
    private boolean isDeleted;

    public Phong() {}

    public Phong(String maPhong, String tenPhong, String maLoaiPhong, String trangThai,
                 String khachHienTai, double giaThang, double dien, double nuoc,
                 double dichVu, String kyChiSo, boolean isDeleted) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.maLoaiPhong = maLoaiPhong;
        this.trangThai = trangThai;
        this.khachHienTai = khachHienTai;
        this.giaThang = giaThang;
        this.dien = dien;
        this.nuoc = nuoc;
        this.dichVu = dichVu;
        this.kyChiSo = kyChiSo;
        this.isDeleted = isDeleted;
    }

    // Getter / Setter
    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    public String getMaLoaiPhong() { return maLoaiPhong; }
    public void setMaLoaiPhong(String maLoaiPhong) { this.maLoaiPhong = maLoaiPhong; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getKhachHienTai() { return khachHienTai; }
    public void setKhachHienTai(String khachHienTai) { this.khachHienTai = khachHienTai; }

    public double getGiaThang() { return giaThang; }
    public void setGiaThang(double giaThang) { this.giaThang = giaThang; }

    public double getDien() { return dien; }
    public void setDien(double dien) { this.dien = dien; }

    public double getNuoc() { return nuoc; }
    public void setNuoc(double nuoc) { this.nuoc = nuoc; }

    public double getDichVu() { return dichVu; }
    public void setDichVu(double dichVu) { this.dichVu = dichVu; }

    public String getKyChiSo() { return kyChiSo; }
    public void setKyChiSo(String kyChiSo) { this.kyChiSo = kyChiSo; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
}