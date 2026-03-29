package com.team.invoice.ui;

import com.team.invoice.dao.KhachThueDAO;
import com.team.invoice.entity.KhachThue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class KhachThueUI extends JPanel {
    private final KhachThueDAO khachThueDAO = new KhachThueDAO();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Mã khách", "Họ tên", "CCCD", "SĐT", "Trạng thái"}, 0);
    private final JTable table = new JTable(model);

    public KhachThueUI() {
        setLayout(new BorderLayout());
        add(new JLabel("Màn hình Khách thuê", SwingConstants.CENTER), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        reloadData();
    }

    public void reloadData() {
        try {
            List<KhachThue> list = khachThueDAO.findAll(null);
            model.setRowCount(0);
            for (KhachThue kh : list) {
                model.addRow(new Object[]{kh.getMaKhach(), kh.getHoTen(), kh.getSoCCCD(), kh.getSdt(), kh.getTrangThai()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
