package com.team.invoice.entity;

public class DichVu {
    private String maDV;
    private String tenDV;
    private String donVi;
    private String loaiDichVu; // Có 2 giá trị: 'CO_DINH' hoặc 'CHI_SO'
    private boolean isDeleted;

    // Constructor mặc định
    public DichVu() {}

    // Constructor đầy đủ
    public DichVu(String maDV, String tenDV, String donVi, String loaiDichVu, boolean isDeleted) {
        this.maDV = maDV;
        this.tenDV = tenDV;
        this.donVi = donVi;
        this.loaiDichVu = loaiDichVu;
        this.isDeleted = isDeleted;
    }

    // Getters và Setters
    public String getMaDV() { return maDV; }
    public void setMaDV(String maDV) { this.maDV = maDV; }

    public String getTenDV() { return tenDV; }
    public void setTenDV(String tenDV) { this.tenDV = tenDV; }

    public String getDonVi() { return donVi; }
    public void setDonVi(String donVi) { this.donVi = donVi; }

    public String getLoaiDichVu() { return loaiDichVu; }
    public void setLoaiDichVu(String loaiDichVu) { this.loaiDichVu = loaiDichVu; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { this.isDeleted = deleted; }
}