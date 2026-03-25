package com.team.invoice.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class InvoiceDetailDialog extends JDialog {
    public InvoiceDetailDialog(java.awt.Component owner, String detailText) {
        super(SwingUtilities.getWindowAncestor(owner), "Chi tiết hóa đơn", ModalityType.APPLICATION_MODAL);
        setSize(new Dimension(420, 420));
        setLocationRelativeTo(owner);
        JTextArea area = new JTextArea(detailText);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        setLayout(new BorderLayout());
        add(new JScrollPane(area), BorderLayout.CENTER);
    }
}
