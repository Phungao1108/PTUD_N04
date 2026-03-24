
package ui;

import javax.swing.*;
import java.awt.*;
import service.AuthService;

public class LoginFrame extends JFrame {
	private AuthService authService = new AuthService();

	public LoginFrame() {
		setTitle("Đăng nhập / Đăng ký - MiniApart");
		setSize(500, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Đăng nhập", createLoginPanel());
		tabbedPane.addTab("Đăng ký", createRegisterPanel());

		add(tabbedPane);
	}

	private JPanel createLoginPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 15, 10, 15);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel lblUsername = new JLabel("Tên đăng nhập / Email:");
		JTextField txtUsername = new JTextField(25);

		JLabel lblPassword = new JLabel("Mật khẩu:");
		JPasswordField txtPassword = new JPasswordField(25);

		JButton btnLogin = new JButton("Đăng nhập");
		JLabel lblMessage = new JLabel(" ", SwingConstants.CENTER);
		lblMessage.setForeground(Color.RED);

		btnLogin.addActionListener(e -> {
			String user = txtUsername.getText();
			String pass = new String(txtPassword.getPassword());
			if (authService.login(user, pass)) {
				new MainFrame().setVisible(true);
				this.dispose();
			} else {
				lblMessage.setText("Sai tài khoản hoặc mật khẩu!");
			}
		});

		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(lblUsername, gbc);
		gbc.gridy = 1;
		panel.add(txtUsername, gbc);
		gbc.gridy = 2;
		panel.add(lblPassword, gbc);
		gbc.gridy = 3;
		panel.add(txtPassword, gbc);
		gbc.gridy = 4;
		panel.add(btnLogin, gbc);
		gbc.gridy = 5;
		panel.add(lblMessage, gbc);

		return panel;
	}

	private JPanel createRegisterPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 15, 10, 15);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel lblUsername = new JLabel("Tên đăng nhập:");
		JTextField txtUsername = new JTextField(25);

		JLabel lblPassword = new JLabel("Mật khẩu:");
		JPasswordField txtPassword = new JPasswordField(25);

		JButton btnRegister = new JButton("Đăng ký");
		JLabel lblMessage = new JLabel(" ", SwingConstants.CENTER);
		lblMessage.setForeground(Color.RED);

		btnRegister.addActionListener(e -> {
			String user = txtUsername.getText();
			String pass = new String(txtPassword.getPassword());
			if (authService.register(user, pass)) {
				lblMessage.setForeground(new Color(0, 128, 0));
				lblMessage.setText("Đăng ký thành công! Đăng nhập ngay.");
			} else {
				lblMessage.setForeground(Color.RED);
				lblMessage.setText("Tài khoản đã tồn tại!");
			}
		});

		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(lblUsername, gbc);
		gbc.gridy = 1;
		panel.add(txtUsername, gbc);
		gbc.gridy = 2;
		panel.add(lblPassword, gbc);
		gbc.gridy = 3;
		panel.add(txtPassword, gbc);
		gbc.gridy = 4;
		panel.add(btnRegister, gbc);
		gbc.gridy = 5;
		panel.add(lblMessage, gbc);

		return panel;
	}
}
