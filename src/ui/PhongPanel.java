package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhongPanel extends JPanel {

    private final List<RoomCardData> rooms;
    private JPanel cardsPanel;
    private JLabel totalRoomLabel;
    private JLabel occupiedRoomLabel;
    private JLabel emptyRoomLabel;

    public PhongPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        rooms = new ArrayList<>(Arrays.asList(
                new RoomCardData("P101", "Tầng 1 • Studio", "4,500,000 đ/tháng", "Đang thuê", "Nguyễn Văn A"),
                new RoomCardData("P102", "Tầng 1 • 1 Khách 1 Ngủ", "5,500,000 đ/tháng", "Trống", "Chưa có khách"),
                new RoomCardData("P201", "Tầng 2 • Studio", "4,200,000 đ/tháng", "Đang thuê", "Trần Thị B"),
                new RoomCardData("P202", "Tầng 2 • Studio", "4,200,000 đ/tháng", "Đang sửa", "Chưa có khách"),
                new RoomCardData("P301", "Tầng 3 • Duplex", "6,000,000 đ/tháng", "Đang thuê", "Lê Văn C"),
                new RoomCardData("P302", "Tầng 3 • Duplex", "6,000,000 đ/tháng", "Đang thuê", "Phạm Thị D")
        ));

        add(createHeader(), BorderLayout.NORTH);
        add(createContentArea(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 246, 250));

        JLabel title = new JLabel("<html><h1 style='margin:0; font-family:sans-serif;'>Quản lý phòng</h1><span style='color:gray; font-family:sans-serif;'>Danh sách các phòng trong tòa nhà</span></html>");

        JButton btnAddRoom = new JButton("+ Thêm phòng mới");
        btnAddRoom.setBackground(new Color(88, 86, 214));
        btnAddRoom.setForeground(Color.WHITE);
        btnAddRoom.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAddRoom.setFocusPainted(false);
        btnAddRoom.setPreferredSize(new Dimension(180, 38));
        btnAddRoom.addActionListener(this::handleAddRoomClick);

        header.add(title, BorderLayout.WEST);
        header.add(btnAddRoom, BorderLayout.EAST);
        return header;
    }

    private JPanel createContentArea() {
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(new Color(245, 246, 250));

        content.add(createSummaryCards(), BorderLayout.NORTH);
        content.add(createFilters(), BorderLayout.CENTER);

        return content;
    }

    private JPanel createSummaryCards() {
        JPanel summary = new JPanel(new GridLayout(1, 3, 20, 0));
        summary.setBackground(new Color(245, 246, 250));

        totalRoomLabel = new JLabel();
        occupiedRoomLabel = new JLabel();
        emptyRoomLabel = new JLabel();

        summary.add(buildSummaryCard("Tất cả phòng", totalRoomLabel, new Color(88, 86, 214)));
        summary.add(buildSummaryCard("Đang thuê", occupiedRoomLabel, new Color(46, 204, 113)));
        summary.add(buildSummaryCard("Đang trống", emptyRoomLabel, new Color(52, 152, 219)));

        updateSummaryLabels();
        return summary;
    }

    private JPanel buildSummaryCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(new Color(120, 120, 120));
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        valueLabel.setForeground(accentColor);

        JLabel lblSub = new JLabel("Cập nhật thời gian thực");
        lblSub.setForeground(new Color(160, 160, 160));
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createFilters() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 20));
        wrapper.setBackground(new Color(245, 246, 250));

        JPanel filterRow = new JPanel(new BorderLayout(15, 0));
        filterRow.setBackground(new Color(245, 246, 250));

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(360, 40));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(5, 15, 5, 15)));
        searchField.setText("Tìm theo mã phòng, khách thuê...");
        searchField.setForeground(Color.GRAY);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusPanel.setBackground(new Color(245, 246, 250));
        String[] statusOptions = {"Tất cả trạng thái", "Đang thuê", "Trống", "Đang sửa"};
        JComboBox<String> cbStatus = new JComboBox<>(statusOptions);
        cbStatus.setPreferredSize(new Dimension(160, 40));

        JButton btnMoreFilter = new JButton("Lọc thêm");
        btnMoreFilter.setPreferredSize(new Dimension(120, 40));
        statusPanel.add(cbStatus);
        statusPanel.add(btnMoreFilter);

        filterRow.add(searchField, BorderLayout.CENTER);
        filterRow.add(statusPanel, BorderLayout.EAST);

        wrapper.add(filterRow, BorderLayout.NORTH);

        cardsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        cardsPanel.setBackground(new Color(245, 246, 250));

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(245, 246, 250));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        wrapper.add(scrollPane, BorderLayout.CENTER);

        refreshRoomCards();
        return wrapper;
    }

    private JPanel createRoomCard(RoomCardData data) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(20, 20, 20, 20)));

        JPanel titleRow = new JPanel(new BorderLayout(10, 0));
        titleRow.setBackground(Color.WHITE);

        JLabel roomCode = new JLabel(data.code);
        roomCode.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel status = new JLabel(data.status, SwingConstants.CENTER);
        status.setFont(new Font("SansSerif", Font.BOLD, 12));
        status.setOpaque(true);
        status.setForeground(getStatusForeground(data.status));
        status.setBackground(getStatusBackground(data.status));
        status.setBorder(new EmptyBorder(4, 12, 4, 12));

        titleRow.add(roomCode, BorderLayout.WEST);
        titleRow.add(status, BorderLayout.EAST);

        JLabel levelInfo = new JLabel(data.levelInfo);
        levelInfo.setForeground(new Color(120, 120, 120));

        JLabel price = new JLabel("Giá thuê: " + data.price);
        price.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel tenant = new JLabel("Khách thuê: " + data.tenant);
        tenant.setForeground(new Color(90, 90, 90));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        footer.setBackground(Color.WHITE);
        JButton btnDetail = new JButton("Chi tiết");
        btnDetail.setFocusPainted(false);
        JButton btnContract = new JButton(data.status.equals("Trống") ? "Tạo hợp đồng" : "Ghi chú");
        btnContract.setFocusPainted(false);
        JButton btnEdit = new JButton("Sửa");
        btnEdit.setFocusPainted(false);
        JButton btnDelete = new JButton("Xóa");
        btnDelete.setFocusPainted(false);

        btnEdit.addActionListener(e -> showEditRoomDialog(data));
        btnDelete.addActionListener(e -> handleDeleteRoom(data));

        footer.add(btnDetail);
        footer.add(btnContract);
        footer.add(btnEdit);
        footer.add(btnDelete);

        card.add(titleRow, BorderLayout.NORTH);
        JPanel body = new JPanel();
        body.setBackground(Color.WHITE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(levelInfo);
        body.add(Box.createVerticalStrut(10));
        body.add(price);
        body.add(Box.createVerticalStrut(5));
        body.add(tenant);
        card.add(body, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    private void refreshRoomCards() {
        if (cardsPanel == null) {
            return;
        }
        cardsPanel.removeAll();
        for (RoomCardData data : rooms) {
            cardsPanel.add(createRoomCard(data));
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
        updateSummaryLabels();
    }

    private void updateSummaryLabels() {
        if (totalRoomLabel == null) {
            return;
        }
        int total = rooms.size();
        int occupied = 0;
        int empty = 0;
        for (RoomCardData data : rooms) {
            if ("Trống".equalsIgnoreCase(data.status)) {
                empty++;
            } else if ("Đang thuê".equalsIgnoreCase(data.status)) {
                occupied++;
            }
        }
        totalRoomLabel.setText(total + " phòng");
        occupiedRoomLabel.setText(occupied + " phòng");
        emptyRoomLabel.setText(empty + " phòng");
    }

    private void handleAddRoomClick(ActionEvent e) {
        showRoomDialog(null);
    }

    private void showEditRoomDialog(RoomCardData room) {
        showRoomDialog(room);
    }

    private void showRoomDialog(RoomCardData room) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        boolean isEdit = room != null;
        String title = isEdit ? "Chỉnh sửa phòng" : "Thêm phòng mới";

        JDialog dialog = new JDialog(parent, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(480, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(0, 15));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JTextField txtCode = new JTextField();
        JTextField txtInfo = new JTextField();
        JTextField txtPrice = new JTextField();
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Đang thuê", "Trống", "Đang sửa"});
        JTextField txtTenant = new JTextField();

        if (isEdit) {
            txtCode.setText(room.code);
            txtCode.setEditable(false);
            txtInfo.setText(room.levelInfo);
            txtPrice.setText(room.price);
            cbStatus.setSelectedItem(room.status);
            txtTenant.setText(room.tenant);
        }

        form.add(new JLabel("Mã phòng"), gbc);
        gbc.gridx = 1;
        form.add(txtCode, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Thông tin tầng / loại"), gbc);
        gbc.gridx = 1;
        form.add(txtInfo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Giá thuê"), gbc);
        gbc.gridx = 1;
        form.add(txtPrice, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Trạng thái"), gbc);
        gbc.gridx = 1;
        form.add(cbStatus, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Khách thuê"), gbc);
        gbc.gridx = 1;
        form.add(txtTenant, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton(isEdit ? "Lưu thay đổi" : "Lưu phòng");
        btnCancel.addActionListener(ev -> dialog.dispose());
        btnSave.addActionListener(ev -> {
            String code = txtCode.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập mã phòng", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String info = txtInfo.getText().trim();
            String price = txtPrice.getText().trim();
            String status = (String) cbStatus.getSelectedItem();
            String tenant = txtTenant.getText().trim();

            if (info.isEmpty()) {
                info = "Chưa cập nhật";
            }
            if (price.isEmpty()) {
                price = "0 đ/tháng";
            }
            if (status.equals("Trống") && tenant.isEmpty()) {
                tenant = "Chưa có khách";
            } else if (tenant.isEmpty()) {
                tenant = "Chưa cập nhật";
            }

            if (isEdit) {
                room.levelInfo = info;
                room.price = price;
                room.status = status;
                room.tenant = tenant;
            } else {
                rooms.add(new RoomCardData(code, info, price, status, tenant));
            }
            refreshRoomCards();
            dialog.dispose();
        });

        actions.add(btnCancel);
        actions.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void handleDeleteRoom(RoomCardData room) {
        int choice = JOptionPane.showConfirmDialog(this,
                "Xóa phòng " + room.code + " khỏi danh sách?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            rooms.remove(room);
            refreshRoomCards();
        }
    }

    private Color getStatusBackground(String status) {
        switch (status) {
            case "Đang sửa":
                return new Color(255, 245, 235);
            case "Trống":
                return new Color(237, 235, 255);
            default:
                return new Color(225, 250, 235);
        }
    }

    private Color getStatusForeground(String status) {
        switch (status) {
            case "Đang sửa":
                return new Color(214, 122, 14);
            case "Trống":
                return new Color(88, 86, 214);
            default:
                return new Color(40, 100, 60);
        }
    }

    private static class RoomCardData {
        final String code;
        String levelInfo;
        String price;
        String status;
        String tenant;

        RoomCardData(String code, String levelInfo, String price, String status, String tenant) {
            this.code = code;
            this.levelInfo = levelInfo;
            this.price = price;
            this.status = status;
            this.tenant = tenant;
        }
    }
}
