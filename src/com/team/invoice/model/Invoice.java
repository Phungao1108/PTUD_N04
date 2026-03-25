package com.team.invoice.model;

import java.util.Date;

public class Invoice {
    private String code;
    private String period;
    private Room room;
    private int newElectric;
    private int newWater;
    private int electricUsage;
    private int waterUsage;
    private double roomFee;
    private double electricFee;
    private double waterFee;
    private double serviceFee;
    private double extraFee;
    private double total;
    private Date dueDate;
    private InvoiceStatus status;
    private Date createdAt;
    private String note;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public int getNewElectric() { return newElectric; }
    public void setNewElectric(int newElectric) { this.newElectric = newElectric; }
    public int getNewWater() { return newWater; }
    public void setNewWater(int newWater) { this.newWater = newWater; }
    public int getElectricUsage() { return electricUsage; }
    public void setElectricUsage(int electricUsage) { this.electricUsage = electricUsage; }
    public int getWaterUsage() { return waterUsage; }
    public void setWaterUsage(int waterUsage) { this.waterUsage = waterUsage; }
    public double getRoomFee() { return roomFee; }
    public void setRoomFee(double roomFee) { this.roomFee = roomFee; }
    public double getElectricFee() { return electricFee; }
    public void setElectricFee(double electricFee) { this.electricFee = electricFee; }
    public double getWaterFee() { return waterFee; }
    public void setWaterFee(double waterFee) { this.waterFee = waterFee; }
    public double getServiceFee() { return serviceFee; }
    public void setServiceFee(double serviceFee) { this.serviceFee = serviceFee; }
    public double getExtraFee() { return extraFee; }
    public void setExtraFee(double extraFee) { this.extraFee = extraFee; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
