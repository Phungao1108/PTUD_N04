package com.team.invoice.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.team.invoice.components.AppColors;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.UITheme;
import com.team.invoice.entity.Phong;
import com.team.invoice.service.BangGiaService;
import com.team.invoice.service.PhongService;

public class PhongUI extends JPanel {

    private DefaultTableModel modelPhong;
    private JTable tablePhong;

    private PhongService phongService;
    private BangGiaService bangGiaService;
    
    private RoundedButton btnThemPhong;

    public PhongUI() {
        phongService = new PhongService();
        bangGiaService = new BangGiaService();

        setLayout(new BorderLayout(10, 10));
        setBackground(AppColors.BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initUI();
        loadData();
    }

    private void initUI() {
        // --- Header ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Danh Sách Phòng & Giá Cước");
        lblTitle.setFont(lblTitle.getFont().deriveFont(20f));
        topPanel.add(lblTitle, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        btnThemPhong = UITheme.primaryButton("Thêm Phòng Mới");
        btnThemPhong.setBackground(new Color(46, 204, 113)); 
        btnThemPhong.addActionListener(e -> showThemPhongDialog());
        
        actionPanel.add(btnThemPhong);
        topPanel.add(actionPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- Table ---
        // Đã thêm cột Thao tác
        String[] columnNames = {
            "Cấu Trúc (Tòa -> Tầng -> Mã Phòng)", 
            "Tên Phòng", 
            "Loại Phòng", 
            "Trạng Thái", 
            "Giá (VNĐ/Tháng)",
            "Thao Tác"
        };
        
        modelPhong = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // CHỈ CHO PHÉP EDIT CỘT THAO TÁC (Cột index 5) ĐỂ BẮT SỰ KIỆN CLICK NÚT
                // VÀ CHỈ CHO PHÉP KHI ĐÓ LÀ DÒNG PHÒNG (Giá trị cột 5 không rỗng)
                if(column == 5) {
                    Object val = getValueAt(row, 5);
                    return val != null && !val.toString().trim().isEmpty();
                }
                return false; 
            }
        };
        
        tablePhong = new JTable(modelPhong);
        UITheme.styleTable(tablePhong);
        tablePhong.setRowHeight(40); // Tăng chiều cao dòng một chút để chứa nút bấm cho đẹp
        
        // Chỉnh độ rộng các cột
        tablePhong.getColumnModel().getColumn(0).setPreferredWidth(250); 
        tablePhong.getColumnModel().getColumn(1).setPreferredWidth(130);
        tablePhong.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablePhong.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablePhong.getColumnModel().getColumn(4).setPreferredWidth(150);
        tablePhong.getColumnModel().getColumn(5).setPreferredWidth(150); // Cột thao tác
        
        // Gắn Custom Renderers
        setupTableRenderer();

        JScrollPane scrollPane = new JScrollPane(tablePhong);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupTableRenderer() {
        // 1. Renderer tô đậm cho các cột thông thường (Từ 0 đến 4)
        DefaultTableCellRenderer standardRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String categoryText = table.getValueAt(row, 0).toString();

                if (categoryText.startsWith("Tòa")) {
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                    c.setBackground(new Color(230, 230, 230));
                    c.setForeground(Color.BLACK);
                } else if (categoryText.trim().startsWith("Tầng")) {
                    c.setFont(c.getFont().deriveFont(Font.BOLD | Font.ITALIC));
                    c.setBackground(new Color(245, 245, 245));
                    c.setForeground(Color.DARK_GRAY);
                } else {
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }

                if (isSelected && !categoryText.startsWith("Tòa") && !categoryText.trim().startsWith("Tầng")) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                }

                return c;
            }
        };

        for (int i = 0; i < 5; i++) {
            tablePhong.getColumnModel().getColumn(i).setCellRenderer(standardRenderer);
        }

        // 2. Renderer và Editor ĐẶC BIỆT cho cột "Thao Tác" (Cột index 5)
        tablePhong.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        tablePhong.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JTable()));
    }

    public void loadData() {
        modelPhong.setRowCount(0);

        List<Phong> danhSachToanBoPhong = phongService.getAllPhong();
        
        if (danhSachToanBoPhong == null || danhSachToanBoPhong.isEmpty()) {
            return;
        }

        Map<String, Map<String, List<Phong>>> treeData = new TreeMap<>();

        for (Phong p : danhSachToanBoPhong) {
            String toa = p.getToa();
            String tang = p.getTang();
            treeData.computeIfAbsent(toa, k -> new TreeMap<>()).computeIfAbsent(tang, k -> new ArrayList<>()).add(p);
        }

        for (Map.Entry<String, Map<String, List<Phong>>> toaEntry : treeData.entrySet()) {
            // Cột 5 để "" (Rỗng) để không hiển thị nút ở dòng Tòa
            modelPhong.addRow(new Object[]{"Tòa " + toaEntry.getKey(), "", "", "", "", ""});

            for (Map.Entry<String, List<Phong>> tangEntry : toaEntry.getValue().entrySet()) {
                // Cột 5 để "" (Rỗng) để không hiển thị nút ở dòng Tầng
                modelPhong.addRow(new Object[]{"    Tầng " + tangEntry.getKey(), "", "", "", "", ""});

                for (Phong p : tangEntry.getValue()) {
                    String giaHienThi = bangGiaService.getGiaPhongChoUI(p.getMaLoaiPhong());
                    
                    modelPhong.addRow(new Object[]{
                        "        Mã: " + p.getMaPhong(),
                        p.getTenPhong() != null ? p.getTenPhong() : "---",
                        p.getMaLoaiPhong() != null ? p.getMaLoaiPhong() : "---",
                        p.getTrangThai() != null ? p.getTrangThai() : "---",
                        giaHienThi,
                        p.getMaPhong() // GỬI MÃ PHÒNG VÀO CỘT 5 ĐỂ XỬ LÝ SỰ KIỆN NÚT BẤM
                    });
                }
            }
        }
    }

    // =========================================================
    // CÁC HÀM XỬ LÝ CHỨC NĂNG CHÍNH (THÊM, SỬA, XÓA)
    // =========================================================

    private void showThemPhongDialog() {
        JOptionPane.showMessageDialog(this, "Chức năng thêm phòng mới!\nVui lòng nối Form thêm phòng vào đây.");
        // Gợi ý code sau khi ráp form:
        // ThemPhongDialog dialog = new ThemPhongDialog(null, true);
        // dialog.setVisible(true);
        // loadData(); 
    }

    private void showSuaPhongDialog(String maPhong) {
        JOptionPane.showMessageDialog(this, "Mở form CẬP NHẬT cho phòng: " + maPhong);
        // Gợi ý code sau khi ráp form:
        // SuaPhongDialog dialog = new SuaPhongDialog(null, true, maPhong);
        // dialog.setVisible(true);
        // loadData(); 
    }

    private void thucHienXoaPhong(String maPhong) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa phòng [" + maPhong + "] không?\n(Hành động này không thể hoàn tác)", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
        if (confirm == JOptionPane.YES_OPTION) {
            // BỎ COMMENT ĐOẠN DƯỚI ĐÂY KHI RÁP SERVICE XÓA
            /*
            boolean success = phongService.deletePhong(maPhong);
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa phòng thành công!");
                loadData(); // Tự động load lại UI
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Không thể xóa phòng này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            */
            
            // Code test tạm thời (nhớ xóa khi ráp service thật)
            JOptionPane.showMessageDialog(this, "Đã giả lập xóa xong phòng " + maPhong);
            loadData();
        }
    }

    // =========================================================
    // CÁC LỚP TIỆN ÍCH ĐỂ HIỂN THỊ NÚT TRONG JTABLE (SWING MAGIC)
    // =========================================================

    // Panel chứa 2 nút bấm
    class ActionPanel extends JPanel {
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");

        public ActionPanel() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);
            
            // Làm đẹp nút Sửa
            btnEdit.setBackground(new Color(52, 152, 219)); // Xanh dương
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Làm đẹp nút Xóa
            btnDelete.setBackground(new Color(231, 76, 60)); // Đỏ
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

            add(btnEdit);
            add(btnDelete);
        }
    }

    // Lớp hiển thị Panel (Vẽ nút lên bảng)
    class ButtonRenderer implements TableCellRenderer {
        private ActionPanel panel = new ActionPanel();
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            // Nếu là dòng Tòa/Tầng (Giá trị truyền vào bị rỗng), hiển thị khoảng trắng
            if (value == null || value.toString().trim().isEmpty()) {
                JPanel emptyPanel = new JPanel();
                emptyPanel.setBackground(new Color(245, 245, 245)); 
                if (table.getValueAt(row, 0).toString().startsWith("Tòa")) {
                    emptyPanel.setBackground(new Color(230, 230, 230));
                }
                return emptyPanel;
            }

            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return panel;
        }
    }

    // Lớp xử lý sự kiện click chuột
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private ActionPanel panel = new ActionPanel();
        private String currentMaPhong;

        public ButtonEditor(JTable table) {
            panel.btnEdit.addActionListener(e -> {
                fireEditingStopped(); // Dừng trạng thái edit bảng
                showSuaPhongDialog(currentMaPhong); // Gọi hàm cập nhật
            });

            panel.btnDelete.addActionListener(e -> {
                fireEditingStopped(); // Dừng trạng thái edit bảng
                thucHienXoaPhong(currentMaPhong); // Gọi hàm xóa
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
            
            // Bỏ qua nếu là dòng Tòa/Tầng
            if (value == null || value.toString().trim().isEmpty()) {
                return new JLabel();
            }

            currentMaPhong = value.toString(); // Lấy mã phòng từ model để xử lý Sửa/Xóa
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentMaPhong;
        }
    }
}