package com.team.invoice.model;

import java.util.Date;

public class InvoiceFormData {
    private String period;
    private Room room;
    private int newElectric;
    private int newWater;
    private double extraFee;
    private Date dueDate;
    private String note;

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public int getNewElectric() { return newElectric; }
    public void setNewElectric(int newElectric) { this.newElectric = newElectric; }
    public int getNewWater() { return newWater; }
    public void setNewWater(int newWater) { this.newWater = newWater; }
    public double getExtraFee() { return extraFee; }
    public void setExtraFee(double extraFee) { this.extraFee = extraFee; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
