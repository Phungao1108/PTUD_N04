package com.team.invoice.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import com.team.invoice.components.AppColors;
import com.team.invoice.components.HintTextField;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.components.UITheme;
import com.team.invoice.entity.BangGia;
import com.team.invoice.entity.DichVu;
import com.team.invoice.entity.DonGiaDichVu;
import com.team.invoice.entity.DonGiaPhong;
import com.team.invoice.entity.LoaiPhong;
import com.team.invoice.service.BangGiaService;
import com.team.invoice.service.DichVuService;
import com.team.invoice.service.LoaiPhongService;

public class BangGiaUI extends JPanel {

    // --- CÁC TRƯỜNG THÔNG TIN CHUNG (MASTER) ---
    private HintTextField txtMaBG;
    private HintTextField txtNgayBatDau; 
    private HintTextField txtNgayKetThuc;
    private JLabel lblTrangThai;

    // --- BẢNG CHI TIẾT (DETAIL) ---
    private DefaultTableModel modelPhong;
    private JTable tablePhong;
    
    private DefaultTableModel modelDichVu;
    private JTable tableDichVu;

    private BangGiaService bangGiaService;
    
    private RoundedButton btnThemMoi;
    private RoundedButton btnHuy;
    private RoundedButton btnDongBo;
    private RoundedButton btnChot;
    
    // Biến lưu trạng thái màn hình
    private boolean isCreatingMode = false;

    public BangGiaUI() {
        bangGiaService = new BangGiaService();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(AppColors.BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createTopPanel(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        centerPanel.add(createMasterInfoPanel());
        centerPanel.add(UITheme.vspace(15));
        centerPanel.add(createDetailTabbedPane());

        add(centerPanel, BorderLayout.CENTER);
        
        setupActions();
        loadData();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Quản Lý Bảng Giá (Phòng & Dịch Vụ)");
        lblTitle.setFont(lblTitle.getFont().deriveFont(20f));
        topPanel.add(lblTitle, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        btnHuy = new RoundedButton("Hủy bỏ", Color.WHITE, AppColors.TEXT, AppColors.BORDER);
        btnHuy.setVisible(false);
        
        btnThemMoi = UITheme.primaryButton("Thêm Đợt Giá Mới");
        btnDongBo = new RoundedButton("Đồng bộ Hạng mục", Color.WHITE, AppColors.PRIMARY, AppColors.PRIMARY);
        btnChot = new RoundedButton("Chốt Đợt Giá Này", Color.WHITE, Color.RED, Color.RED);
        
        actionPanel.add(btnHuy);
        actionPanel.add(btnThemMoi);
        actionPanel.add(btnDongBo);
        actionPanel.add(btnChot);

        topPanel.add(actionPanel, BorderLayout.EAST);
        return topPanel;
    }

    private RoundedPanel createMasterInfoPanel() {
        RoundedPanel panel = new RoundedPanel(15, Color.WHITE, AppColors.BORDER);
        panel.setLayout(new GridLayout(2, 4, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        txtMaBG = new HintTextField("VD: BG01"); 
        txtNgayBatDau = new HintTextField("dd/MM/yyyy"); 
        txtNgayKetThuc = new HintTextField("dd/MM/yyyy (Có thể trống)"); 
        
        lblTrangThai = new JLabel("---");
        lblTrangThai.setFont(lblTrangThai.getFont().deriveFont(14f));

        panel.add(new JLabel("Mã Bảng Giá (*):")); panel.add(txtMaBG);
        panel.add(new JLabel("Ngày Bắt Đầu (*):")); panel.add(txtNgayBatDau);
        panel.add(new JLabel("Ngày Kết Thúc:")); panel.add(txtNgayKetThuc);
        panel.add(new JLabel("Trạng Thái:")); panel.add(lblTrangThai);

        return panel;
    }

    private JTabbedPane createDetailTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(tabbedPane.getFont().deriveFont(14f));

        modelPhong = new DefaultTableModel(new String[]{"Mã Loại Phòng", "Tên Loại Phòng", "Đơn Giá (VNĐ/Tháng)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 2; }
        };
        tablePhong = new JTable(modelPhong);
        UITheme.styleTable(tablePhong);
        tabbedPane.addTab("Đơn Giá Loại Phòng", new JScrollPane(tablePhong));

        modelDichVu = new DefaultTableModel(new String[]{"Mã Dịch Vụ", "Tên Dịch Vụ", "Đơn Vị Tính", "Đơn Giá (VNĐ)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 3; }
        };
        tableDichVu = new JTable(modelDichVu);
        UITheme.styleTable(tableDichVu);
        tabbedPane.addTab("Đơn Giá Dịch Vụ", new JScrollPane(tableDichVu));

        return tabbedPane;
    }

    // --- ĐIỀU KHIỂN TRẠNG THÁI GIAO DIỆN ---
    private void setInputEditable(boolean editable) {
        txtMaBG.setEditable(editable);
        txtNgayBatDau.setEditable(editable);
        txtNgayKetThuc.setEditable(editable);
        
        Color bgColor = editable ? Color.WHITE : new Color(245, 245, 245);
        txtMaBG.setBackground(bgColor);
        txtNgayBatDau.setBackground(bgColor);
        txtNgayKetThuc.setBackground(bgColor);
    }

    private void toggleCreateMode(boolean isCreating) {
        this.isCreatingMode = isCreating;
        setInputEditable(isCreating);
        
        btnHuy.setVisible(isCreating);
        btnDongBo.setVisible(!isCreating);
        btnChot.setVisible(!isCreating);
        
        if (isCreating) {
            btnThemMoi.setText("LƯU BẢNG GIÁ NÀY");
            btnThemMoi.setBackground(new Color(46, 204, 113)); 
            
            txtMaBG.setText("");
            txtNgayBatDau.setText("");
            txtNgayKetThuc.setText("");
            lblTrangThai.setText("ĐANG TẠO MỚI...");
            lblTrangThai.setForeground(Color.BLUE);
            
            loadHangMucMacDinh();
        } else {
            btnThemMoi.setText("Thêm Đợt Giá Mới");
            btnThemMoi.setBackground(AppColors.PRIMARY); 
            loadData(); 
        }
    }

    private void loadHangMucMacDinh() {
        modelPhong.setRowCount(0);
        for (LoaiPhong lp : new LoaiPhongService().getAllLoaiPhong()) {
            modelPhong.addRow(new Object[]{lp.getMaLoaiPhong(), lp.getTenLoaiPhong(), "0"});
        }
        
        modelDichVu.setRowCount(0);
        for (DichVu dv : new DichVuService().getAllDichVu()) {
            modelDichVu.addRow(new Object[]{dv.getMaDV(), dv.getTenDV(), dv.getDonVi(), "0"});
        }
    }

    // --- XỬ LÝ SỰ KIỆN ---
    private void setupActions() {
        btnThemMoi.addActionListener(e -> {
            if (!isCreatingMode) {
                toggleCreateMode(true);
            } else {
                saveBangGiaMoi();
            }
        });

        btnHuy.addActionListener(e -> toggleCreateMode(false));

        btnChot.addActionListener(e -> {
            String maBG = txtMaBG.getText();
            if (maBG.isEmpty()) return;
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn chốt bảng giá này vào ngày hôm nay?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (bangGiaService.chotDotGiaCu(maBG, new java.util.Date())) {
                    JOptionPane.showMessageDialog(this, "Đã chốt bảng giá thành công!");
                    loadData(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi chốt bảng giá!");
                }
            }
        });

        btnDongBo.addActionListener(e -> {
            String maBG = txtMaBG.getText();
            if (!maBG.isEmpty()) {
                String msg = bangGiaService.boSungDonGiaThieu(maBG);
                JOptionPane.showMessageDialog(this, msg);
                loadData();
            }
        });

        modelPhong.addTableModelListener(e -> {
            if (!isCreatingMode && e.getType() == TableModelEvent.UPDATE && e.getColumn() == 2) {
                int row = e.getFirstRow();
                String maBG = txtMaBG.getText();
                String maLP = modelPhong.getValueAt(row, 0).toString();
                try {
                    double giaMoi = Double.parseDouble(modelPhong.getValueAt(row, 2).toString());
                    bangGiaService.updateGiaPhong(maBG, maLP, giaMoi);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Đơn giá không hợp lệ!");
                    loadData();
                }
            }
        });

        modelDichVu.addTableModelListener(e -> {
            if (!isCreatingMode && e.getType() == TableModelEvent.UPDATE && e.getColumn() == 3) {
                int row = e.getFirstRow();
                String maBG = txtMaBG.getText();
                String maDV = modelDichVu.getValueAt(row, 0).toString();
                try {
                    double giaMoi = Double.parseDouble(modelDichVu.getValueAt(row, 3).toString());
                    bangGiaService.updateGiaDichVu(maBG, maDV, giaMoi);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Đơn giá không hợp lệ!");
                    loadData();
                }
            }
        });
    }

    private void saveBangGiaMoi() {
        if (tablePhong.isEditing()) tablePhong.getCellEditor().stopCellEditing();
        if (tableDichVu.isEditing()) tableDichVu.getCellEditor().stopCellEditing();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        BangGia bg = new BangGia();
        bg.setMaBG(txtMaBG.getText().trim());
        
        try {
            bg.setNgayHieuLuc(sdf.parse(txtNgayBatDau.getText().trim()));
            if (!txtNgayKetThuc.getText().trim().isEmpty()) {
                bg.setNgayKetThuc(sdf.parse(txtNgayKetThuc.getText().trim()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày tháng phải đúng định dạng dd/MM/yyyy!");
            return;
        }

        List<DonGiaPhong> phongs = new ArrayList<>();
        for (int i = 0; i < modelPhong.getRowCount(); i++) {
            double gia = Double.parseDouble(modelPhong.getValueAt(i, 2).toString());
            phongs.add(new DonGiaPhong(bg.getMaBG(), modelPhong.getValueAt(i, 0).toString(), gia));
        }

        List<DonGiaDichVu> dichVus = new ArrayList<>();
        for (int i = 0; i < modelDichVu.getRowCount(); i++) {
            double gia = Double.parseDouble(modelDichVu.getValueAt(i, 3).toString());
            dichVus.add(new DonGiaDichVu(bg.getMaBG(), modelDichVu.getValueAt(i, 0).toString(), gia));
        }

        String message = bangGiaService.createBangGiaToanDien(bg, phongs, dichVus);
        JOptionPane.showMessageDialog(this, message);
        if (message.equals("Thành công")) {
            toggleCreateMode(false);
        }
    }

    // --- TẢI DỮ LIỆU ---
    public void loadData() {
        setInputEditable(false); 
        
        List<BangGia> listBG = bangGiaService.getAllBangGia();
        if (listBG == null || listBG.isEmpty()) {
            txtMaBG.setText(""); txtNgayBatDau.setText(""); txtNgayKetThuc.setText("");
            lblTrangThai.setText("CHƯA CÓ BẢNG GIÁ NÀO");
            modelPhong.setRowCount(0); modelDichVu.setRowCount(0);
            return;
        }

        BangGia activeBG = listBG.stream()
                .filter(bg -> "DANG_AP_DUNG".equals(bg.getTrangThai()))
                .findFirst()
                .orElse(listBG.get(listBG.size() - 1));

        txtMaBG.setText(activeBG.getMaBG());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtNgayBatDau.setText(activeBG.getNgayHieuLuc() != null ? sdf.format(activeBG.getNgayHieuLuc()) : "");
        txtNgayKetThuc.setText(activeBG.getNgayKetThuc() != null ? sdf.format(activeBG.getNgayKetThuc()) : "");
        
        lblTrangThai.setText(activeBG.getTrangThai());
        if ("DANG_AP_DUNG".equals(activeBG.getTrangThai())) {
            lblTrangThai.setForeground(Color.GREEN.darker());
            tablePhong.setEnabled(true); tableDichVu.setEnabled(true);
        } else {
            lblTrangThai.setForeground(Color.RED);
            tablePhong.setEnabled(false); tableDichVu.setEnabled(false);
        }

        modelPhong.setRowCount(0);
        List<DonGiaPhong> dsPhong = bangGiaService.getDonGiaPhongByBG(activeBG.getMaBG());
        if (dsPhong != null) {
            for (DonGiaPhong dp : dsPhong) {
                modelPhong.addRow(new Object[]{dp.getMaLoaiPhong(), dp.getTenLoaiPhong(), dp.getGiaTheoThang()});
            }
        }

        modelDichVu.setRowCount(0);
        List<DonGiaDichVu> dsDichVu = bangGiaService.getDonGiaDichVuByBG(activeBG.getMaBG());
        if (dsDichVu != null) {
            for (DonGiaDichVu dd : dsDichVu) {
                modelDichVu.addRow(new Object[]{dd.getMaDV(), dd.getTenDV(), dd.getDonVi(), dd.getGia()});
            }
        }
    }
    
}