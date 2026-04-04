package com.team.invoice.service;

import java.util.Date;
import java.util.List;

import com.team.invoice.dao.HoaDonDAO;
import com.team.invoice.entity.Invoice;
import com.team.invoice.entity.InvoiceFormData;
import com.team.invoice.entity.InvoiceStatus;
import com.team.invoice.entity.Room;

public class HoaDonService {
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();

    public List<Room> getRoomsForInvoice() {
        return hoaDonDAO.findRoomsForInvoice();
    }

    public List<Invoice> getAllInvoices() {
        return hoaDonDAO.findAllInvoices();
    }

    public List<String> getInvoiceDetails(String maHoaDon) {
        return hoaDonDAO.findInvoiceDetails(maHoaDon);
    }

    public Invoice calculate(InvoiceFormData data) {
        validate(data);
        Room room = data.getRoom();
        Invoice invoice = new Invoice();
        invoice.setPeriod(data.getPeriod());
        invoice.setRoom(room);
        invoice.setNewElectric(data.getNewElectric());
        invoice.setNewWater(data.getNewWater());
        invoice.setElectricUsage(data.getNewElectric() - room.getOldElectric());
        invoice.setWaterUsage(data.getNewWater() - room.getOldWater());
        invoice.setRoomFee(room.getRoomPrice());
        invoice.setElectricFee(invoice.getElectricUsage() * room.getElectricUnitPrice());
        invoice.setWaterFee(invoice.getWaterUsage() * room.getWaterUnitPrice());
        invoice.setServiceFee(room.getServiceFee());
        invoice.setExtraFee(data.getExtraFee());
        invoice.setDueDate(data.getDueDate() == null ? new Date() : data.getDueDate());
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setCreatedAt(new Date());
        invoice.setNote(data.getNote());
        invoice.setTotal(invoice.getRoomFee() + invoice.getElectricFee() + invoice.getWaterFee() + invoice.getServiceFee() + invoice.getExtraFee());
        return invoice;
    }

    public boolean saveInvoice(Invoice invoice, boolean issueNow) {
        return hoaDonDAO.saveInvoice(invoice, issueNow);
    }

    public boolean markPaid(String maHoaDon) {
        return hoaDonDAO.markPaid(maHoaDon);
    }

    public boolean deleteInvoice(String maHoaDon) {
        return hoaDonDAO.softDelete(maHoaDon);
    }
    public boolean updateInvoiceMeta(String maHoaDon, String period, Date dueDate, String note) {
        if (period == null || period.trim().isEmpty()) {
            throw new IllegalArgumentException("Kỳ hóa đơn không được để trống.");
        }
        String normalized = period.trim().replace('-', '/').replace('.', '/').replaceAll("\s+", "");
        if (!normalized.matches("\\d{1,2}/\\d{4}")) {
            throw new IllegalArgumentException("Kỳ hóa đơn phải có dạng MM/yyyy.");
        }
        String[] parts = normalized.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Tháng trong kỳ hóa đơn phải từ 01 đến 12.");
        }
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Năm trong kỳ hóa đơn không hợp lệ.");
        }
        return hoaDonDAO.updateInvoiceMeta(maHoaDon, String.format("%02d/%04d", month, year), dueDate, note);
    }

    private void validate(InvoiceFormData data) {
        if (data.getRoom() == null) {
            throw new IllegalArgumentException("Vui lòng chọn phòng đang thuê.");
        }
        if (data.getPeriod() == null || data.getPeriod().trim().isEmpty()) {
            throw new IllegalArgumentException("Kỳ hóa đơn không được để trống.");
        }
        String period = data.getPeriod().trim().replace('-', '/').replace('.', '/').replaceAll("\s+", "");
        if (!period.matches("\\d{1,2}/\\d{4}")) {
            throw new IllegalArgumentException("Kỳ hóa đơn phải có dạng MM/yyyy.");
        }
        String[] parts = period.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Tháng trong kỳ hóa đơn phải từ 01 đến 12.");
        }
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Năm trong kỳ hóa đơn không hợp lệ.");
        }
        data.setPeriod(String.format("%02d/%04d", month, year));
        if (data.getNewElectric() < data.getRoom().getOldElectric()) {
            throw new IllegalArgumentException("Chỉ số điện mới không được nhỏ hơn chỉ số cũ.");
        }
        if (data.getNewWater() < data.getRoom().getOldWater()) {
            throw new IllegalArgumentException("Chỉ số nước mới không được nhỏ hơn chỉ số cũ.");
        }
        if (data.getExtraFee() < 0) {
            throw new IllegalArgumentException("Phụ phí không được âm.");
        }
    }
}
