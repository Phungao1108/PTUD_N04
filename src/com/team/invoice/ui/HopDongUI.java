package com.team.invoice.ui;

import com.team.invoice.dao.HopDongDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HopDongUI extends JPanel {
    private final HopDongDAO hopDongDAO = new HopDongDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã HĐ", "Phòng", "Khách chính", "Ngày bắt đầu", "Ngày kết thúc", "Tiền cọc", "Trạng thái"}, 0);
    private final JTable table = new JTable(model);

    public HopDongUI() {
        setLayout(new BorderLayout());
        add(new JLabel("Màn hình Hợp đồng", SwingConstants.CENTER), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        reloadData();
    }

    public void reloadData() {
        try {
            model.setRowCount(0);
            for (Object[] row : hopDongDAO.findAll()) {
                model.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
