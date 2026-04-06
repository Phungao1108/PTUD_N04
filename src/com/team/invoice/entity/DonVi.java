package com.team.invoice.entity;

public class DonVi {
    private String id;
    private String ten;

    public DonVi(String id, String ten) {
        this.id = id;
        this.ten = ten;
    }

    public String getId() { return id; }
    public String getTen() { return ten; }

    @Override
    public String toString() { return ten; } // combobox tự hiển thị tên
}