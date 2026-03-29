package com.team.invoice.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.KeyboardFocusManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;

public class FocusUtils {

    public static void enableClearFocusOnClick(Container container) {
        MouseAdapter clearFocusListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Object source = e.getSource();

                if (source instanceof JTextField || source instanceof JComboBox || source instanceof JTable) {
                    return;
                }

                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
            }
        };

        addMouseListenerRecursively(container, clearFocusListener);
    }

    private static void addMouseListenerRecursively(Component comp, MouseAdapter listener) {
        comp.addMouseListener(listener);

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                addMouseListenerRecursively(child, listener);
            }
        }
    }
}