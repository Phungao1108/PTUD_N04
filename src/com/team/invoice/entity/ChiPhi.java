package com.team.invoice.entity;

import java.util.Date;

public class ChiPhi {
    private String maCS;
    private String maPhong;
    private String ky; // Định dạng MM/yyyy
    private int dienCu;
    private int dienMoi;
    private int nuocCu;
    private int nuocMoi;
    private Date ngayGhi;

    // Các trường phụ trợ hiển thị UI
    private String tenPhong;
    private int tieuThuDien;
    private int tieuThuNuoc;

    public ChiPhi() {}

    // Constructor, Getters và Setters
    public String getMaCS() { return maCS; }
    public void setMaCS(String maCS) { this.maCS = maCS; }
    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }
    public String getKy() { return ky; }
    public void setKy(String ky) { this.ky = ky; }
    public int getDienCu() { return dienCu; }
    public void setDienCu(int dienCu) { this.dienCu = dienCu; }
    public int getDienMoi() { return dienMoi; }
    public void setDienMoi(int dienMoi) { this.dienMoi = dienMoi; }
    public int getNuocCu() { return nuocCu; }
    public void setNuocCu(int nuocCu) { this.nuocCu = nuocCu; }
    public int getNuocMoi() { return nuocMoi; }
    public void setNuocMoi(int nuocMoi) { this.nuocMoi = nuocMoi; }
    public Date getNgayGhi() { return ngayGhi; }
    public void setNgayGhi(Date ngayGhi) { this.ngayGhi = ngayGhi; }
    
    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }
    public int getTieuThuDien() { return dienMoi > 0 ? dienMoi - dienCu : 0; }
    public int getTieuThuNuoc() { return nuocMoi > 0 ? nuocMoi - nuocCu : 0; }
}
