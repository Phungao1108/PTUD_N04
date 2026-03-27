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

    public interface TableActionEvent {
        void onEdit(int row);
        void onDelete(int row);
    }

    public TableActionCell(TableActionEvent event) {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);

        btnEdit = new RoundedButton("Sửa", AppColors.WARNING, Color.WHITE, AppColors.WARNING);
        btnDelete = new RoundedButton("Xóa", AppColors.DANGER, Color.WHITE, AppColors.DANGER);

        btnEdit.setPreferredSize(new Dimension(55, 25));
        btnDelete.setPreferredSize(new Dimension(55, 25));

        btnEdit.setMaximumSize(new Dimension(55, 25));
        btnDelete.setMaximumSize(new Dimension(55, 25));

        btnEdit.setMinimumSize(new Dimension(55, 25));
        btnDelete.setMinimumSize(new Dimension(55, 25));

        btnEdit.setFocusable(false);
        btnDelete.setFocusable(false);

        btnEdit.addActionListener(e -> {
            fireEditingStopped();
            event.onEdit(currentRow);
        });

        btnDelete.addActionListener(e -> {
            fireEditingStopped();
            event.onDelete(currentRow);
        });

        panel.add(Box.createHorizontalGlue());
        panel.add(btnEdit);
        panel.add(Box.createHorizontalStrut(6));
        panel.add(btnDelete);
        panel.add(Box.createHorizontalGlue());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.currentRow = row;
        panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }

    public static class Renderer extends DefaultTableCellRenderer {
        private final JPanel panel;
        private final JButton btnEdit;
        private final JButton btnDelete;

        public Renderer() {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.setOpaque(true);

            btnEdit = new RoundedButton("Sửa", AppColors.WARNING, Color.WHITE, AppColors.WARNING);
            btnDelete = new RoundedButton("Xóa", AppColors.DANGER, Color.WHITE, AppColors.DANGER);

            btnEdit.setPreferredSize(new Dimension(55, 25));
            btnDelete.setPreferredSize(new Dimension(55, 25));

            btnEdit.setMaximumSize(new Dimension(55, 25));
            btnDelete.setMaximumSize(new Dimension(55, 25));

            btnEdit.setMinimumSize(new Dimension(55, 25));
            btnDelete.setMinimumSize(new Dimension(55, 25));

            btnEdit.setFocusable(false);
            btnDelete.setFocusable(false);

            panel.add(Box.createHorizontalGlue());
            panel.add(btnEdit);
            panel.add(Box.createHorizontalStrut(6));
            panel.add(btnDelete);
            panel.add(Box.createHorizontalGlue());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return panel;
        }
    }
}