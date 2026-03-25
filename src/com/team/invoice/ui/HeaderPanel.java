
// src/ui/HeaderPanel.java
package com.team.invoice.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class HeaderPanel extends JPanel {
	private JButton btnToggle;

	public HeaderPanel() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(0, 100));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

		JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 30));
		leftHeader.setBackground(Color.WHITE);

		btnToggle = new JButton("☰");
		btnToggle.setFont(new Font("SansSerif", Font.BOLD, 20));
		btnToggle.setFocusPainted(false);
		btnToggle.setBackground(Color.WHITE);
		btnToggle.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
		btnToggle.setPreferredSize(new Dimension(45, 40));

		JPanel searchBox = new JPanel(new BorderLayout());
		searchBox.setBackground(Color.WHITE);
		searchBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
		JTextField txtSearch = new JTextField(30);
		txtSearch.setBorder(new EmptyBorder(5, 10, 5, 10));
		txtSearch.setPreferredSize(new Dimension(300, 38));
		searchBox.add(txtSearch, BorderLayout.CENTER);

		leftHeader.add(btnToggle);
		leftHeader.add(searchBox);

		JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 30));
		rightHeader.setBackground(Color.WHITE);
		JLabel userInfo = new JLabel(
				"<html><div style='text-align: right;'><b>Admin</b><br><span style='color:gray;'>Quản lý</span></div></html>");
		rightHeader.add(userInfo);

		add(leftHeader, BorderLayout.WEST);
		add(rightHeader, BorderLayout.EAST);
	}

	public void setToggleAction(ActionListener listener) {
		btnToggle.addActionListener(listener);
	}
}
