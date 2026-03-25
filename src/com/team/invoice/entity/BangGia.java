package com.team.invoice.entity;

import java.util.Date;

public class BangGia {
    private String maBG;
    private Date ngayHieuLuc;
    private Date ngayKetThuc;
    private String trangThai; // DANG_AP_DUNG, HET_HIEU_LUC
    private boolean isDeleted;

    public BangGia() {}

    public BangGia(String maBG, Date ngayHieuLuc, Date ngayKetThuc, String trangThai, boolean isDeleted) {
        this.maBG = maBG;
        this.ngayHieuLuc = ngayHieuLuc;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters
    public String getMaBG() { return maBG; }
    public void setMaBG(String maBG) { this.maBG = maBG; }
    public Date getNgayHieuLuc() { return ngayHieuLuc; }
    public void setNgayHieuLuc(Date ngayHieuLuc) { this.ngayHieuLuc = ngayHieuLuc; }
    public Date getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(Date ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
}