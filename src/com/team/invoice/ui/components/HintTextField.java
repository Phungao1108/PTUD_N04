package com.team.invoice.ui.components;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JTextField;

public class HintTextField extends JTextField {
    private final String hint;

    public HintTextField(String hint) {
        this.hint = hint;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().trim().length() == 0 && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(165, 171, 183));
            FontMetrics fm = g2.getFontMetrics();
            Insets insets = getInsets();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(hint, insets.left + 2, y);
            g2.dispose();
        }
    }
}
