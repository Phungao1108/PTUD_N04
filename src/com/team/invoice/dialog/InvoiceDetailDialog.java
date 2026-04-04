package com.team.invoice.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.team.invoice.components.AppColors;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.entity.Invoice;

public class InvoiceDetailDialog extends JDialog {
    private final JTextField txtCode = new JTextField();
    private final JTextField txtRoom = new JTextField();
    private final JTextField txtTenant = new JTextField();
    private final JTextField txtPeriod = new JTextField();
    private final JFormattedTextField txtDueDate = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
    private final JTextArea txtNote = new JTextArea(4, 20);
    private final JTextArea txtDetails = new JTextArea(10, 20);
    private final RoundedButton btnSave = new RoundedButton("Lưu thay đổi", AppColors.PRIMARY, Color.WHITE, AppColors.PRIMARY);
    private final RoundedButton btnClose = new RoundedButton("Đóng", AppColors.WHITE, AppColors.TEXT, AppColors.BORDER);
    private boolean saved;

    public InvoiceDetailDialog(java.awt.Component owner, Invoice invoice, List<String> details) {
        super(SwingUtilities.getWindowAncestor(owner), "Chi tiết hóa đơn", ModalityType.APPLICATION_MODAL);
        setSize(new Dimension(700, 620));
        setLocationRelativeTo(owner);
        getContentPane().setBackground(AppColors.BG);
        setLayout(new BorderLayout(0, 16));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(invoice, details), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);
        fill(invoice, details);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Chi tiết hóa đơn");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(AppColors.TEXT);
        JLabel sub = new JLabel("Xem nội dung chi tiết và cập nhật kỳ hóa đơn, hạn thanh toán, ghi chú.");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(AppColors.MUTED);
        panel.add(title);
        panel.add(sub);
        return panel;
    }

    private JPanel buildBody(Invoice invoice, List<String> details) {
        JPanel wrap = new JPanel(new BorderLayout(16, 0));
        wrap.setOpaque(false);

        RoundedPanel infoCard = new RoundedPanel(24, AppColors.WHITE, AppColors.BORDER);
        infoCard.setLayout(new BorderLayout());
        infoCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        styleField(txtCode, true);
        styleField(txtRoom, true);
        styleField(txtTenant, true);
        styleField(txtPeriod, false);
        styleField(txtDueDate, false);

        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        txtNote.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtNote.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        addField(form, gbc, 0, "Mã hóa đơn", txtCode);
        addField(form, gbc, 1, "Phòng", txtRoom);
        addField(form, gbc, 2, "Khách", txtTenant);
        addField(form, gbc, 3, "Kỳ hóa đơn (MM/yyyy)", txtPeriod);
        addField(form, gbc, 4, "Hạn thanh toán", txtDueDate);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JLabel note = new JLabel("Ghi chú");
        note.setFont(new Font("SansSerif", Font.BOLD, 13));
        form.add(note, gbc);

        gbc.gridy = 6;
        JScrollPane notePane = new JScrollPane(txtNote);
        notePane.setPreferredSize(new Dimension(260, 100));
        notePane.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        form.add(notePane, gbc);

        infoCard.add(form, BorderLayout.CENTER);

        RoundedPanel detailCard = new RoundedPanel(24, AppColors.WHITE, AppColors.BORDER);
        detailCard.setLayout(new BorderLayout(0, 10));
        detailCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel detailTitle = new JLabel("Các dòng chi tiết");
        detailTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        detailTitle.setForeground(AppColors.TEXT);
        txtDetails.setEditable(false);
        txtDetails.setLineWrap(true);
        txtDetails.setWrapStyleWord(true);
        txtDetails.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtDetails.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JScrollPane detailPane = new JScrollPane(txtDetails);
        detailPane.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        detailCard.add(detailTitle, BorderLayout.NORTH);
        detailCard.add(detailPane, BorderLayout.CENTER);

        wrap.add(infoCard, BorderLayout.WEST);
        wrap.add(detailCard, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);
        btnClose.setPreferredSize(new Dimension(110, 38));
        btnSave.setPreferredSize(new Dimension(140, 38));
        btnClose.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> {
            saved = true;
            dispose();
        });
        panel.add(btnClose);
        panel.add(btnSave);
        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component comp) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(AppColors.TEXT);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(comp, gbc);
    }

    private void styleField(JTextField field, boolean readOnly) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        field.setPreferredSize(new Dimension(250, 40));
        if (readOnly) {
            field.setEditable(false);
            field.setBackground(new Color(245, 247, 250));
            field.setForeground(AppColors.MUTED);
        } else {
            field.setBackground(Color.WHITE);
            field.setForeground(AppColors.TEXT);
        }
    }

    private void fill(Invoice invoice, List<String> details) {
        txtCode.setText(invoice.getCode());
        txtRoom.setText(invoice.getRoom() != null ? invoice.getRoom().getCode() : "");
        txtTenant.setText(invoice.getRoom() != null ? invoice.getRoom().getTenantName() : "");
        txtPeriod.setText(invoice.getPeriod() == null ? "" : invoice.getPeriod());
        txtDueDate.setText(invoice.getDueDate() == null ? "" : new SimpleDateFormat("dd/MM/yyyy").format(invoice.getDueDate()));
        txtNote.setText(extractNote(details));
        StringBuilder sb = new StringBuilder();
        for (String line : details) {
            sb.append("- ").append(line).append("\n");
        }
        txtDetails.setText(sb.toString());
        txtDetails.setCaretPosition(0);
    }

    private String extractNote(List<String> details) {
        for (String line : details) {
            if (line != null && line.startsWith("Ghi chú:")) {
                return line.substring("Ghi chú:".length()).trim();
            }
        }
        return "";
    }

    public boolean isSaved() {
        return saved;
    }

    public String getUpdatedPeriod() {
        String value = txtPeriod.getText() == null ? "" : txtPeriod.getText().trim();
        value = value.replace('-', '/').replace('.', '/').replaceAll("\\s+", "");
        if (!value.matches("\\d{1,2}/\\d{4}")) {
            throw new IllegalArgumentException("Kỳ hóa đơn phải có dạng MM/yyyy.");
        }
        String[] parts = value.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Tháng của kỳ hóa đơn phải từ 01 đến 12.");
        }
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Năm của kỳ hóa đơn không hợp lệ.");
        }
        return String.format("%02d/%04d", month, year);
    }

    public Date getUpdatedDueDate() {
        String value = txtDueDate.getText() == null ? "" : txtDueDate.getText().trim();
        if (value.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            return sdf.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Hạn thanh toán phải có dạng dd/MM/yyyy.");
        }
    }

    public String getUpdatedNote() {
        return txtNote.getText() == null ? "" : txtNote.getText().trim();
    }
}
