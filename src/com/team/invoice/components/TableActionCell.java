package com.team.invoice.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class TableActionCell extends AbstractCellEditor implements TableCellEditor {
    private JPanel panel;
    private JButton btnEdit;
    private JButton btnDelete;
    private int currentRow;

    // Interface để truyền hành động ra ngoài UI chính
    public interface TableActionEvent {
        void onEdit(int row);
        void onDelete(int row);
    }

    public TableActionCell(TableActionEvent event) {
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setBackground(Color.WHITE);

        btnEdit = new RoundedButton("Sửa", AppColors.WARNING, Color.WHITE, AppColors.WARNING);
        btnDelete = new RoundedButton("Xóa", AppColors.DANGER, Color.WHITE, AppColors.DANGER);

        btnEdit.setPreferredSize(new Dimension(60, 25));
        btnDelete.setPreferredSize(new Dimension(60, 25));

        btnEdit.addActionListener(e -> {
            fireEditingStopped();
            event.onEdit(currentRow);
        });

        btnDelete.addActionListener(e -> {
            fireEditingStopped();
            event.onDelete(currentRow);
        });

        panel.add(btnEdit);
        panel.add(btnDelete);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.currentRow = row;
        panel.setBackground(table.getSelectionBackground());
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }

    // --- RENDERER (Dùng chung class để vẽ lên UI khi không click) ---
    public static class Renderer extends DefaultTableCellRenderer {
        private JPanel panel;
        private JButton btnEdit;
        private JButton btnDelete;

        public Renderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            btnEdit = new RoundedButton("Sửa", AppColors.WARNING, Color.WHITE, AppColors.WARNING);
            btnDelete = new RoundedButton("Xóa", AppColors.DANGER, Color.WHITE, AppColors.DANGER);
            btnEdit.setPreferredSize(new Dimension(60, 25));
            btnDelete.setPreferredSize(new Dimension(60, 25));
            panel.add(btnEdit);
            panel.add(btnDelete);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return panel;
        }
    }
}