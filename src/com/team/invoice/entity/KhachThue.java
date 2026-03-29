package com.team.invoice.entity;

public class KhachThue {
    private String maKhach;
    private String hoTen;
    private String soCCCD;
    private String sdt;
    private String trangThai;

    public KhachThue() {
    }

    public KhachThue(String maKhach, String hoTen, String soCCCD, String sdt, String trangThai) {
        this.maKhach = maKhach;
        this.hoTen = hoTen;
        this.soCCCD = soCCCD;
        this.sdt = sdt;
        this.trangThai = trangThai;
    }

    public String getMaKhach() { return maKhach; }
    public void setMaKhach(String maKhach) { this.maKhach = maKhach; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getSoCCCD() { return soCCCD; }
    public void setSoCCCD(String soCCCD) { this.soCCCD = soCCCD; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
