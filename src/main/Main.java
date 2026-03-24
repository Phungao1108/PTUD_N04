package main;

import javax.swing.*;

import ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        // Sử dụng giao diện của Hệ điều hành (Windows/Mac) thay vì giao diện mặc định của Java
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Không thể thiết lập System LookAndFeel");
        }

        // Mở màn hình Login
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}