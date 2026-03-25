package com.team.invoice.entity;

public class Room {
    private String code;
    private String tenantName;
    private boolean active;
    private double roomPrice;
    private double electricUnitPrice;
    private double waterUnitPrice;
    private double serviceFee;
    private int oldElectric;
    private int oldWater;

    public Room(String code, String tenantName, boolean active, double roomPrice,
                double electricUnitPrice, double waterUnitPrice, double serviceFee,
                int oldElectric, int oldWater) {
        this.code = code;
        this.tenantName = tenantName;
        this.active = active;
        this.roomPrice = roomPrice;
        this.electricUnitPrice = electricUnitPrice;
        this.waterUnitPrice = waterUnitPrice;
        this.serviceFee = serviceFee;
        this.oldElectric = oldElectric;
        this.oldWater = oldWater;
    }

    public String getCode() { return code; }
    public String getTenantName() { return tenantName; }
    public boolean isActive() { return active; }
    public double getRoomPrice() { return roomPrice; }
    public double getElectricUnitPrice() { return electricUnitPrice; }
    public double getWaterUnitPrice() { return waterUnitPrice; }
    public double getServiceFee() { return serviceFee; }
    public int getOldElectric() { return oldElectric; }
    public int getOldWater() { return oldWater; }
    public void setOldElectric(int oldElectric) { this.oldElectric = oldElectric; }
    public void setOldWater(int oldWater) { this.oldWater = oldWater; }

    @Override
    public String toString() {
        return code + " - " + tenantName;
    }
}
