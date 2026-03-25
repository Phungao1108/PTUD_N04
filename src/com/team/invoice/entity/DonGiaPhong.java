package com.team.invoice.entity;

public class DonGiaPhong {
    private String maBG;
    private String maLoaiPhong;
    private double giaTheoThang;
    
    // Thuộc tính phụ trợ hiển thị UI (không có trong DB gốc)
    private String tenLoaiPhong; 

    public DonGiaPhong() {}

    public DonGiaPhong(String maBG, String maLoaiPhong, double giaTheoThang) {
        this.maBG = maBG;
        this.maLoaiPhong = maLoaiPhong;
        this.giaTheoThang = giaTheoThang;
    }

    // Getters and Setters
    public String getMaBG() { return maBG; }
    public void setMaBG(String maBG) { this.maBG = maBG; }
    public String getMaLoaiPhong() { return maLoaiPhong; }
    public void setMaLoaiPhong(String maLoaiPhong) { this.maLoaiPhong = maLoaiPhong; }
    public double getGiaTheoThang() { return giaTheoThang; }
    public void setGiaTheoThang(double giaTheoThang) { this.giaTheoThang = giaTheoThang; }
    public String getTenLoaiPhong() { return tenLoaiPhong; }
    public void setTenLoaiPhong(String tenLoaiPhong) { this.tenLoaiPhong = tenLoaiPhong; }
}