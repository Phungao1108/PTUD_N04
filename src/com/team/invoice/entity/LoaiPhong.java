package com.team.invoice.entity;

public class LoaiPhong {
    private String maLoaiPhong;
    private String tenLoaiPhong;
    private double dienTichChuan;
    private String moTa;
    private boolean isDeleted;

    public LoaiPhong() {}

    public LoaiPhong(String maLoaiPhong, String tenLoaiPhong, double dienTichChuan, String moTa, boolean isDeleted) {
        this.maLoaiPhong = maLoaiPhong;
        this.tenLoaiPhong = tenLoaiPhong;
        this.dienTichChuan = dienTichChuan;
        this.moTa = moTa;
        this.isDeleted = isDeleted;
    }

    public String getMaLoaiPhong() { return maLoaiPhong; }
    public void setMaLoaiPhong(String maLoaiPhong) { this.maLoaiPhong = maLoaiPhong; }
    public String getTenLoaiPhong() { return tenLoaiPhong; }
    public void setTenLoaiPhong(String tenLoaiPhong) { this.tenLoaiPhong = tenLoaiPhong; }
    public double getDienTichChuan() { return dienTichChuan; }
    public void setDienTichChuan(double dienTichChuan) { this.dienTichChuan = dienTichChuan; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { this.isDeleted = deleted; }
}