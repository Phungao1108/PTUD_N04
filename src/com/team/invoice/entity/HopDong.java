package com.team.invoice.entity;

import java.math.BigDecimal;
import java.sql.Date;

public class HopDong {
    private String maHopDong;
    private String maPhong;
    private String maKhachChinh;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private BigDecimal tienDatCoc;
    private String trangThai;

    public String getMaHopDong() { return maHopDong; }
    public void setMaHopDong(String maHopDong) { this.maHopDong = maHopDong; }
    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }
    public String getMaKhachChinh() { return maKhachChinh; }
    public void setMaKhachChinh(String maKhachChinh) { this.maKhachChinh = maKhachChinh; }
    public Date getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(Date ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public Date getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(Date ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
    public BigDecimal getTienDatCoc() { return tienDatCoc; }
    public void setTienDatCoc(BigDecimal tienDatCoc) { this.tienDatCoc = tienDatCoc; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
