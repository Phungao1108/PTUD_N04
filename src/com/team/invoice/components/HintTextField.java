package com.team.invoice.components;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class HintTextField extends JTextField {
    private final String hint;
    private boolean showingHint = true;

    private final Color hintColor = new Color(150, 150, 150);
    private final Color textColor = Color.BLACK;
    private final Color borderColor = new Color(200, 200, 200);

    public HintTextField(String hint) {
        super();
        this.hint = hint;

        setFont(getFont().deriveFont(13f));
        setOpaque(true);
        setBackground(Color.WHITE);

        Border lineBorder = BorderFactory.createLineBorder(borderColor, 1);
        Border paddingBorder = new EmptyBorder(4, 10, 4, 10);
        setBorder(new CompoundBorder(lineBorder, paddingBorder));

        showHint();

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (showingHint) {
                    showingHint = false;
                    HintTextField.super.setText("");
                    setForeground(textColor);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (HintTextField.super.getText().trim().isEmpty()) {
                    showHint();
                }
            }
        });
    }

    private void showHint() {
        showingHint = true;
        setForeground(hintColor);
        HintTextField.super.setText(hint);
    }

    @Override
    public String getText() {
        return showingHint ? "" : super.getText();
    }

    public boolean isShowingHint() {
        return showingHint;
    }

    public void setActualText(String text) {
        if (text == null || text.trim().isEmpty()) {
            showHint();
        } else {
            showingHint = false;
            HintTextField.super.setText(text);
            setForeground(textColor);
        }
    }
}