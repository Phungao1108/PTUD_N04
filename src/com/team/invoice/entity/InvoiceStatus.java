package com.team.invoice.entity;

public enum InvoiceStatus {
    DRAFT("Bản nháp"),
    PENDING("Chờ thanh toán"),
    PAID("Đã thu"),
    OVERDUE("Quá hạn");

    private final String displayName;

    InvoiceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
