package com.team.invoice.entity;

public class DonGiaDichVu {
    private String maBG;
    private String maDV;
    private double gia;

    // Thuộc tính phụ trợ hiển thị UI
    private String tenDV;
    private String donVi;

    public DonGiaDichVu() {}

    public DonGiaDichVu(String maBG, String maDV, double gia) {
        this.maBG = maBG;
        this.maDV = maDV;
        this.gia = gia;
    }

    // Getters and Setters
    public String getMaBG() { return maBG; }
    public void setMaBG(String maBG) { this.maBG = maBG; }
    public String getMaDV() { return maDV; }
    public void setMaDV(String maDV) { this.maDV = maDV; }
    public double getGia() { return gia; }
    public void setGia(double gia) { this.gia = gia; }
    public String getTenDV() { return tenDV; }
    public void setTenDV(String tenDV) { this.tenDV = tenDV; }
    public String getDonVi() { return donVi; }
    public void setDonVi(String donVi) { this.donVi = donVi; }
}