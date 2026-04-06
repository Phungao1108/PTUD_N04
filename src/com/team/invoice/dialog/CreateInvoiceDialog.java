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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.team.invoice.components.AppColors;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.entity.Invoice;
import com.team.invoice.entity.InvoiceFormData;
import com.team.invoice.entity.Room;
import com.team.invoice.util.CurrencyUtils;

public class CreateInvoiceDialog extends JDialog {
    private JTextField txtPeriod;
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
        setSize(980, 720);
        setMinimumSize(new Dimension(920, 680));
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColors.BG);

        JPanel outer = new JPanel(new BorderLayout(0, 18));
        outer.setBackground(AppColors.BG);
        outer.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        add(outer, BorderLayout.CENTER);

        outer.add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(18, 0));
        center.setOpaque(false);

        RoundedPanel leftCard = new RoundedPanel(24, AppColors.WHITE, AppColors.BORDER);
        leftCard.setLayout(new BorderLayout(0, 16));
        leftCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        RoundedPanel rightCard = new RoundedPanel(24, AppColors.WHITE, AppColors.BORDER);
        rightCard.setLayout(new BorderLayout(0, 16));
        rightCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rightCard.setPreferredSize(new Dimension(320, 0));

        leftCard.add(createSectionTitle("Thông tin hóa đơn", "Nhập kỳ thu, phòng, chỉ số điện nước và hạn thanh toán."), BorderLayout.NORTH);
        rightCard.add(createSectionTitle("Kết quả tính tiền", "Hệ thống sẽ hiển thị chi tiết từng khoản sau khi bấm tính tiền."), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtPeriod = createInputField();
        txtPeriod.setText(new java.text.SimpleDateFormat("MM/yyyy").format(new Date()));
        cboRoom = new JComboBox<Room>(rooms.toArray(new Room[0]));
        txtOldElectric = createReadOnlyField();
        txtNewElectric = createInputField();
        txtOldWater = createReadOnlyField();
        txtNewWater = createInputField();
        txtExtraFee = createInputField();
        txtExtraFee.setText("0");
        txtDueDate = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        styleField(txtDueDate, false);
        txtDueDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        txtNote = new JTextArea(5, 20);
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        txtNote.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtNote.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        txtNote.setBackground(Color.WHITE);

        styleCombo(cboRoom);

        lblRoomFee = createMoneyLabel();
        lblElectricFee = createMoneyLabel();
        lblWaterFee = createMoneyLabel();
        lblServiceFee = createMoneyLabel();
        lblTotal = createMoneyLabel();
        lblTotal.setForeground(AppColors.PRIMARY);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 18));

        addField(form, gbc, 0, "Kỳ hóa đơn (MM/yyyy)", txtPeriod);
        addField(form, gbc, 1, "Phòng/căn", cboRoom);
        addField(form, gbc, 2, "Chỉ số điện cũ", txtOldElectric);
        addField(form, gbc, 3, "Chỉ số điện mới", txtNewElectric);
        addField(form, gbc, 4, "Chỉ số nước cũ", txtOldWater);
        addField(form, gbc, 5, "Chỉ số nước mới", txtNewWater);
        addField(form, gbc, 6, "Phụ phí khác", txtExtraFee);
        addField(form, gbc, 7, "Hạn thanh toán", txtDueDate);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        JLabel lblNote = new JLabel("Ghi chú");
        lblNote.setFont(new Font("SansSerif", Font.BOLD, 13));
        form.add(lblNote, gbc);

        gbc.gridy = 9;
        JScrollPane noteScroll = new JScrollPane(txtNote);
        noteScroll.setPreferredSize(new Dimension(200, 110));
        noteScroll.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        form.add(noteScroll, gbc);

        leftCard.add(form, BorderLayout.CENTER);
        rightCard.add(buildSummaryPanel(), BorderLayout.CENTER);

        center.add(leftCard, BorderLayout.CENTER);
        center.add(rightCard, BorderLayout.EAST);

        outer.add(center, BorderLayout.CENTER);
        outer.add(buildActions(), BorderLayout.SOUTH);

        cboRoom.addActionListener(e -> updateOldIndexes());
        updateOldIndexes();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Lập hóa đơn");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(AppColors.TEXT);

        JLabel subtitle = new JLabel("Tính toán và phát hành hóa đơn hàng tháng cho từng phòng.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(AppColors.MUTED);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(subtitle);

        header.add(text, BorderLayout.WEST);
        return header;
    }

    private JPanel createSectionTitle(String titleText, String subtitleText) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(AppColors.TEXT);

        JLabel subtitle = new JLabel("<html><body style='width:260px'>" + subtitleText + "</body></html>");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(AppColors.MUTED);

        panel.add(title);
        panel.add(subtitle);
        return panel;
    }

    private JPanel buildSummaryPanel() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);

        JPanel summary = new JPanel(new GridBagLayout());
        summary.setOpaque(false);
        summary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addSummary(summary, gbc, 0, "Tiền phòng", lblRoomFee);
        addSummary(summary, gbc, 1, "Tiền điện", lblElectricFee);
        addSummary(summary, gbc, 2, "Tiền nước", lblWaterFee);
        addSummary(summary, gbc, 3, "Phí dịch vụ", lblServiceFee);
        addSummary(summary, gbc, 4, "Tổng cộng", lblTotal);

        JLabel hint = new JLabel("<html><body style='width:250px'>Sau khi bấm <b>Tính tiền</b>, hệ thống sẽ cập nhật chi tiết từng khoản ở khung này.</body></html>");
        hint.setForeground(AppColors.MUTED);
        hint.setFont(new Font("SansSerif", Font.PLAIN, 13));
        hint.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        wrap.add(summary, BorderLayout.NORTH);
        wrap.add(hint, BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel buildActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);

        btnClose = new RoundedButton("Đóng", AppColors.WHITE, AppColors.TEXT, AppColors.BORDER);
        btnCalculate = new RoundedButton("Tính tiền", AppColors.WHITE, AppColors.TEXT, AppColors.BORDER);
        btnDraft = new RoundedButton("Tạo bản nháp mới", AppColors.WHITE, AppColors.PRIMARY, AppColors.PRIMARY);
        btnIssue = new RoundedButton("Phát hành hóa đơn", AppColors.PRIMARY, AppColors.WHITE, AppColors.PRIMARY);

        btnClose.setPreferredSize(new Dimension(120, 38));
        btnCalculate.setPreferredSize(new Dimension(120, 38));
        btnDraft.setPreferredSize(new Dimension(150, 38));
        btnIssue.setPreferredSize(new Dimension(160, 38));

        panel.add(btnClose);
        panel.add(btnCalculate);
        panel.add(btnDraft);
        panel.add(btnIssue);
        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component comp) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(AppColors.TEXT);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.32;
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.68;
        panel.add(comp, gbc);
    }

    private void addSummary(JPanel panel, GridBagConstraints gbc, int row, String label, JLabel value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(AppColors.TEXT);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.5;
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        panel.add(value, gbc);
    }

    private JTextField createReadOnlyField() {
        JTextField field = new JTextField();
        styleField(field, true);
        return field;
    }

    private JTextField createInputField() {
        JTextField field = new JTextField();
        styleField(field, false);
        return field;
    }

    private void styleField(JTextField field, boolean readOnly) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        field.setPreferredSize(new Dimension(260, 40));
        if (readOnly) {
            field.setEditable(false);
            field.setBackground(new Color(245, 247, 250));
            field.setForeground(AppColors.MUTED);
        } else {
            field.setBackground(Color.WHITE);
            field.setForeground(AppColors.TEXT);
        }
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(new Dimension(260, 40));
    }

    private JLabel createMoneyLabel() {
        JLabel label = new JLabel("0 VNĐ");
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private void updateOldIndexes() {
        Room room = (Room) cboRoom.getSelectedItem();
        if (room != null) {
            txtOldElectric.setText(String.valueOf(room.getOldElectric()));
            txtOldWater.setText(String.valueOf(room.getOldWater()));
        } else {
            txtOldElectric.setText("");
            txtOldWater.setText("");
        }
    }

    public InvoiceFormData getFormData() {
        InvoiceFormData data = new InvoiceFormData();
        data.setPeriod(txtPeriod.getText());
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
        lblRoomFee.setText("0 VNĐ");
        lblElectricFee.setText("0 VNĐ");
        lblWaterFee.setText("0 VNĐ");
        lblServiceFee.setText("0 VNĐ");
        lblTotal.setText("0 VNĐ");
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
