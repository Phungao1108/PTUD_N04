
package com.team.invoice.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
	private SidebarPanel sidebarPanel;
	private ContentPanel contentPanel;
	private HeaderPanel headerPanel;

	public MainFrame() {
		setTitle("MiniApart - Quản lý chung cư");
		setSize(1440, 1024);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		headerPanel = new HeaderPanel();
		sidebarPanel = new SidebarPanel();
		contentPanel = new ContentPanel();

		headerPanel.setToggleAction(e -> sidebarPanel.setVisible(!sidebarPanel.isVisible()));
		sidebarPanel.setMenuAction(contentPanel::showPanel);

		add(headerPanel, BorderLayout.NORTH);
		add(sidebarPanel, BorderLayout.WEST);
		add(contentPanel, BorderLayout.CENTER);
	}

	public SidebarPanel getSidebarPanel() {
		return sidebarPanel;
	}

	public ContentPanel getContentPanel() {
		return contentPanel;
	}

	public HeaderPanel getHeaderPanel() {
		return headerPanel;
	}
}
