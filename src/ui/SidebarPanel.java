
// src/ui/SidebarPanel.java
package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SidebarPanel extends JPanel {
	public static final String[] MENU_ITEMS = { "Tổng quan", "Phòng", "Khách thuê", "Hóa đơn", "Bảo trì", "Cài đặt" };

	public SidebarPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(300, 0));
		setBackground(Color.WHITE);

		JLabel logo = new JLabel("MiniApart");
		logo.setFont(new Font("SansSerif", Font.BOLD, 28));
		logo.setForeground(new Color(88, 86, 214));
		logo.setBorder(BorderFactory.createEmptyBorder(30, 30, 40, 30));
		add(logo);

		for (String item : MENU_ITEMS) {
			JButton menuBtn = new JButton("  " + item);
			menuBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
			menuBtn.setMaximumSize(new Dimension(300, 50));
			menuBtn.setBackground(Color.WHITE);
			menuBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
			menuBtn.setForeground(new Color(80, 80, 80));
			menuBtn.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
			menuBtn.setFocusPainted(false);
			menuBtn.setHorizontalAlignment(SwingConstants.LEFT);
			menuBtn.setActionCommand(item);
			add(menuBtn);
		}
	}

	public void setMenuAction(ActionListener listener) {
		for (Component c : getComponents()) {
			if (c instanceof JButton) {
				((JButton) c).addActionListener(listener);
			}
		}
	}
}
