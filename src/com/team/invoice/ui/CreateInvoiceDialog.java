package com.team.invoice.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.team.invoice.model.Invoice;
import com.team.invoice.model.InvoiceFormData;
import com.team.invoice.model.Room;
import com.team.invoice.ui.components.AppColors;
import com.team.invoice.ui.components.RoundedButton;
import com.team.invoice.ui.components.RoundedPanel;
import com.team.invoice.util.CurrencyUtils;

public class CreateInvoiceDialog extends JDialog {
    private JComboBox<String> cboPeriod;
    private JComboBox<Room> cboRoom;
    private JTextField txtOldElectric;
    private JTextField txtNewElectric;
    private JTextField txtOldWater;
    private JTextField txtNewWater;
    private JTextField txtExtraFee;
    private JFormattedTextField txtDueDate;
    private JTextArea txtNote;
    private JLabel lblRoomFee;
    private JLabel lblElectricFee;
    private JLabel lblWaterFee;
    private JLabel lblServiceFee;
    private JLabel lblTotal;

    private RoundedButton btnCalculate;
    private RoundedButton btnIssue;
    private RoundedButton btnDraft;
    private RoundedButton btnClose;

    private Invoice calculatedInvoice;

    public CreateInvoiceDialog(Frame owner, List<Room> rooms) {
        super(owner, "Lập hóa đơn", true);
        setSize(760, 650);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColors.BG);

        RoundedPanel content = new RoundedPanel(24, AppColors.WHITE, AppColors.BORDER);
        content.setLayout(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(content, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        cboPeriod = new JComboBox<String>(new String[]{"10/2023", "11/2023", "12/2023", "01/2024"});
        cboRoom = new JComboBox<Room>(rooms.toArray(new Room[0]));
        txtOldElectric = createReadOnlyField();
        txtNewElectric = new JTextField();
        txtOldWater = createReadOnlyField();
        txtNewWater = new JTextField();
        txtExtraFee = new JTextField("0");
        txtDueDate = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        txtDueDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        txtNote = new JTextArea(4, 20);
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);

        lblRoomFee = createMoneyLabel();
        lblElectricFee = createMoneyLabel();
        lblWaterFee = createMoneyLabel();
        lblServiceFee = createMoneyLabel();
        lblTotal = createMoneyLabel();
        lblTotal.setForeground(AppColors.PRIMARY);

        addField(form, gbc, 0, "Kỳ hóa đơn", cboPeriod);
        addField(form, gbc, 1, "Phòng/căn", cboRoom);
        addField(form, gbc, 2, "Chỉ số điện cũ", txtOldElectric);
        addField(form, gbc, 3, "Chỉ số điện mới", txtNewElectric);
        addField(form, gbc, 4, "Chỉ số nước cũ", txtOldWater);
        addField(form, gbc, 5, "Chỉ số nước mới", txtNewWater);
        addField(form, gbc, 6, "Phụ phí khác", txtExtraFee);
        addField(form, gbc, 7, "Hạn thanh toán", txtDueDate);

        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2;
        form.add(new JLabel("Ghi chú"), gbc);
        gbc.gridy = 9;
        JScrollPane noteScroll = new JScrollPane(txtNote);
        noteScroll.setPreferredSize(new Dimension(200, 80));
        form.add(noteScroll, gbc);

        gbc.gridy = 10;
        form.add(buildSummaryPanel(), gbc);

        content.add(form, BorderLayout.CENTER);
        content.add(buildActions(), BorderLayout.SOUTH);

        cboRoom.addActionListener(e -> updateOldIndexes());
        updateOldIndexes();
    }

    private JPanel buildSummaryPanel() {
        JPanel summary = new JPanel(new GridBagLayout());
        summary.setOpaque(false);
        summary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        addSummary(summary, gbc, 0, "Tiền phòng", lblRoomFee);
        addSummary(summary, gbc, 1, "Tiền điện", lblElectricFee);
        addSummary(summary, gbc, 2, "Tiền nước", lblWaterFee);
        addSummary(summary, gbc, 3, "Phí dịch vụ", lblServiceFee);
        addSummary(summary, gbc, 4, "Tổng cộng", lblTotal);
        return summary;
    }

    private JPanel buildActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        panel.setOpaque(false);
        btnClose = new RoundedButton("Đóng", AppColors.WHITE, AppColors.TEXT, AppColors.BORDER);
        btnCalculate = new RoundedButton("Tính tiền", AppColors.WHITE, AppColors.TEXT, AppColors.BORDER);
        btnDraft = new RoundedButton("Tạo bản nháp mới", AppColors.WHITE, AppColors.PRIMARY, AppColors.PRIMARY);
        btnIssue = new RoundedButton("Phát hành hóa đơn", AppColors.PRIMARY, AppColors.WHITE, AppColors.PRIMARY);
        panel.add(btnClose);
        panel.add(btnCalculate);
        panel.add(btnDraft);
        panel.add(btnIssue);
        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.3;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(comp, gbc);
    }

    private void addSummary(JPanel panel, GridBagConstraints gbc, int row, String label, JLabel value) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(value, gbc);
    }

    private JTextField createReadOnlyField() {
        JTextField field = new JTextField();
        field.setEditable(false);
        return field;
    }

    private JLabel createMoneyLabel() {
        JLabel label = new JLabel("0 VNĐ");
        return label;
    }

    private void updateOldIndexes() {
        Room room = (Room) cboRoom.getSelectedItem();
        if (room != null) {
            txtOldElectric.setText(String.valueOf(room.getOldElectric()));
            txtOldWater.setText(String.valueOf(room.getOldWater()));
        }
    }

    public InvoiceFormData getFormData() {
        InvoiceFormData data = new InvoiceFormData();
        data.setPeriod((String) cboPeriod.getSelectedItem());
        data.setRoom((Room) cboRoom.getSelectedItem());
        data.setNewElectric(parseInt(txtNewElectric.getText(), "Chỉ số điện mới"));
        data.setNewWater(parseInt(txtNewWater.getText(), "Chỉ số nước mới"));
        data.setExtraFee(parseDouble(txtExtraFee.getText(), "Phụ phí khác"));
        data.setDueDate(parseDate(txtDueDate.getText()));
        data.setNote(txtNote.getText());
        return data;
    }

    private int parseInt(String text, String fieldName) {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException(fieldName + " phải là số nguyên.");
        }
    }

    private double parseDouble(String text, String fieldName) {
        try {
            return Double.parseDouble(text.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException(fieldName + " phải là số.");
        }
    }

    private Date parseDate(String text) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(text.trim());
        } catch (ParseException ex) {
            throw new IllegalArgumentException("Hạn thanh toán không đúng định dạng dd/MM/yyyy.");
        }
    }

    public void showCalculatedInvoice(Invoice invoice) {
        this.calculatedInvoice = invoice;
        lblRoomFee.setText(CurrencyUtils.formatMoney(invoice.getRoomFee()) + " VNĐ");
        lblElectricFee.setText(CurrencyUtils.formatMoney(invoice.getElectricFee()) + " VNĐ (" + invoice.getElectricUsage() + " số)");
        lblWaterFee.setText(CurrencyUtils.formatMoney(invoice.getWaterFee()) + " VNĐ (" + invoice.getWaterUsage() + " số)");
        lblServiceFee.setText(CurrencyUtils.formatMoney(invoice.getServiceFee()) + " VNĐ");
        lblTotal.setText(CurrencyUtils.formatMoney(invoice.getTotal()) + " VNĐ");
    }

    public Invoice getCalculatedInvoice() {
        return calculatedInvoice;
    }

    public void clearCalculation() {
        calculatedInvoice = null;
    }

    public RoundedButton getBtnCalculate() { return btnCalculate; }
    public RoundedButton getBtnIssue() { return btnIssue; }
    public RoundedButton getBtnDraft() { return btnDraft; }
    public RoundedButton getBtnClose() { return btnClose; }

    public boolean confirmIssue() {
        int option = JOptionPane.showConfirmDialog(this,
                "Xác nhận phát hành hóa đơn?", "Xác nhận",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return option == JOptionPane.YES_OPTION;
    }
}
