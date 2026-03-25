package com.team.invoice.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;

import com.team.invoice.ui.components.AppColors;
import com.team.invoice.ui.components.HintTextField;
import com.team.invoice.ui.components.RoundedButton;
import com.team.invoice.ui.components.RoundedPanel;

public final class UITheme {
    private UITheme() {}

    public static final Color BG = AppColors.BG;
    public static final Color WHITE = AppColors.WHITE;
    public static final Color TEXT = AppColors.TEXT;
    public static final Color MUTED = AppColors.MUTED;
    public static final Color BORDER = AppColors.BORDER;
    public static final Color PRIMARY = AppColors.PRIMARY;
    public static final Color SUCCESS = AppColors.SUCCESS;
    public static final Color WARNING = AppColors.WARNING;
    public static final Color DANGER = AppColors.DANGER;
    public static final Color INFO = AppColors.INFO;

    public static void stylePage(JPanel panel) {
        panel.setOpaque(true);
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
    }

    public static JPanel createHeader(String titleText, String subtitleText, Component actions) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new javax.swing.BoxLayout(left, javax.swing.BoxLayout.Y_AXIS));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel(subtitleText);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(MUTED);

        left.add(title);
        left.add(vspace(8));
        left.add(subtitle);

        panel.add(left, BorderLayout.WEST);
        if (actions != null) {
            panel.add(actions, BorderLayout.EAST);
        }
        return panel;
    }

    public static RoundedPanel createCard() {
        RoundedPanel panel = new RoundedPanel(24, WHITE, BORDER);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        return panel;
    }

    public static RoundedPanel createStatCard(String title, String value, String sub, Color valueColor) {
        RoundedPanel card = new RoundedPanel(22, WHITE, BORDER);
        card.setLayout(new javax.swing.BoxLayout(card, javax.swing.BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblTitle.setForeground(MUTED);
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblValue.setForeground(valueColor);
        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSub.setForeground(MUTED);

        card.add(lblTitle);
        card.add(vspace(8));
        card.add(lblValue);
        card.add(vspace(8));
        card.add(lblSub);
        return card;
    }

    public static RoundedButton primaryButton(String text) {
        RoundedButton button = new RoundedButton(text, PRIMARY, WHITE, PRIMARY);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 42));
        return button;
    }

    public static RoundedButton secondaryButton(String text) {
        RoundedButton button = new RoundedButton(text, WHITE, TEXT, BORDER);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(118, 42));
        return button;
    }

    public static HintTextField searchField(String hint, int width) {
        HintTextField field = new HintTextField(hint);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBorder(new EmptyBorder(0, 12, 0, 12));
        field.setPreferredSize(new Dimension(width, 44));
        return field;
    }

    public static RoundedPanel wrapField(JComponent component) {
        RoundedPanel panel = new RoundedPanel(16, WHITE, BORDER);
        panel.setLayout(new BorderLayout());
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    public static void styleCombo(JComboBox<String> combo, int width) {
        combo.setPreferredSize(new Dimension(width, 44));
        combo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        combo.setFocusable(false);
        combo.setBackground(WHITE);
        combo.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    public static void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER), new EmptyBorder(10, 12, 10, 12)));
    }

    public static void styleTextArea(JTextArea area) {
        area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(10, 12, 10, 12));
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(236, 240, 255));
        table.setSelectionForeground(TEXT);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setForeground(TEXT);
        table.setBackground(WHITE);
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(WHITE);
        header.setForeground(MUTED);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(100, 42));
    }

    public static JScrollPane wrapTable(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(WHITE);
        return scroll;
    }

    public static JLabel badge(String text, Color fg, Color bg) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(bg);
        label.setForeground(fg);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setBorder(new EmptyBorder(6, 10, 6, 10));
        return label;
    }

    public static JPanel actionBar(Component... actions) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);
        for (int i = 0; i < actions.length; i++) panel.add(actions[i]);
        return panel;
    }

    public static Component vspace(int height) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(10, height));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return panel;
    }

    public static void installGlobalLook() {
        UIManager.put("TabbedPane.selected", WHITE);
        UIManager.put("TabbedPane.contentBorderInsets", new java.awt.Insets(8, 8, 8, 8));
        UIManager.put("TabbedPane.font", new Font("SansSerif", Font.BOLD, 14));
        UIManager.put("OptionPane.background", WHITE);
        UIManager.put("Panel.background", WHITE);
    }
}
