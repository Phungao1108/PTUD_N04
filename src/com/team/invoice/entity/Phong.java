package com.team.invoice.entity;

public class Phong {
    private String maPhong;      // map với CoSoVatChat.id
    private String tenPhong;     // map với CoSoVatChat.ten
    private String maLoaiPhong;
    private String trangThai;    // map với CoSoVatChat.trangThaiPhong
    private String idCha;        // tầng cha
    private boolean isDeleted;
    private String tenTang;
    private String tenKhuVuc;

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

    public String getTenTang() {
        return tenTang != null ? tenTang : "";
    }

    public void setTenTang(String tenTang) {
        this.tenTang = tenTang;
    }

    public String getTenKhuVuc() {
        return tenKhuVuc != null ? tenKhuVuc : "";
    }

    public void setTenKhuVuc(String tenKhuVuc) {
        this.tenKhuVuc = tenKhuVuc;
    }
}