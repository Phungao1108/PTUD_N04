package com.team.invoice.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.team.invoice.components.HintTextField;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.components.UITheme;


public class PhongUI extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private HintTextField txtSearch;
    private JComboBox<String> cboStatus;
    private TableRowSorter<DefaultTableModel> sorter;

   
    private RoundedPanel buildTableCard() {
        RoundedPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 16));

        JPanel filterBar = new JPanel(new BorderLayout(12, 0));
        filterBar.setOpaque(false);


        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(cboStatus);


        filterBar.add(UITheme.wrapField(txtSearch), BorderLayout.CENTER);
        filterBar.add(right, BorderLayout.EAST);

        model = new DefaultTableModel(new String[]{"Mã phòng","Tên phòng","Loại","Trạng thái","Khách hiện tại","Giá tháng","Điện","Nước","Dịch vụ","Kỳ chỉ số"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);


        card.add(filterBar, BorderLayout.NORTH);
        card.add(UITheme.wrapTable(table), BorderLayout.CENTER);
        return card;
    }
}

   