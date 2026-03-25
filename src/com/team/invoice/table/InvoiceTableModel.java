package com.team.invoice.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.team.invoice.entity.Invoice;
import com.team.invoice.util.CurrencyUtils;
import com.team.invoice.util.DateUtils;

public class InvoiceTableModel extends AbstractTableModel {
    private final String[] columns = {"Mã HD", "Thông tin", "Kỳ thu", "Tổng tiền (VNĐ)", "Hạn thu", "Trạng thái", "Thao tác"};
    private List<Invoice> data = new ArrayList<Invoice>();

    public void setData(List<Invoice> data) { this.data = data; fireTableDataChanged(); }
    public Invoice getInvoiceAt(int row) { return row < 0 || row >= data.size() ? null : data.get(row); }
    public int getRowCount() { return data.size(); }
    public int getColumnCount() { return columns.length; }
    public String getColumnName(int column) { return columns[column]; }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Invoice invoice = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return invoice.getCode();
            case 1: return invoice.getRoom().getCode() + "\n" + invoice.getRoom().getTenantName();
            case 2: return invoice.getPeriod();
            case 3: return CurrencyUtils.formatMoney(invoice.getTotal());
            case 4: return DateUtils.formatDate(invoice.getDueDate());
            case 5: return invoice.getStatus().getDisplayName();
            case 6: return "Chi tiết | Thu tiền | Xóa";
            default: return "";
        }
    }
}
