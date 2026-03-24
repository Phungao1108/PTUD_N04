package ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ContentPanel extends JPanel {
	private CardLayout cardLayout;
	private Map<String, JComponent> panelMap = new HashMap<>();

	public ContentPanel() {
		cardLayout = new CardLayout();
		setLayout(cardLayout);

		addPanel("Tổng quan", new JLabel("Dashboard", SwingConstants.CENTER));
		addPanel("Phòng", new JLabel("Màn hình Phòng", SwingConstants.CENTER));
		addPanel("Khách thuê", new JLabel("Màn hình Khách thuê", SwingConstants.CENTER));
		addPanel("Hóa đơn", new JLabel("Màn hình Hóa đơn", SwingConstants.CENTER));
		addPanel("Bảo trì", new JLabel("Màn hình Bảo trì", SwingConstants.CENTER));
		addPanel("Cài đặt", new JLabel("Màn hình Cài đặt", SwingConstants.CENTER));
	}

	public void addPanel(String name, JComponent panel) {
		add(panel, name);
		panelMap.put(name, panel);
	}

	public void showPanel(java.awt.event.ActionEvent e) {
		String name = e.getActionCommand();
		cardLayout.show(this, name);
	}
}
