package com.team.invoice.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.team.invoice.components.AppColors;
import com.team.invoice.components.HintTextField;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.entity.LoaiPhong;
import com.team.invoice.entity.Phong;
import com.team.invoice.service.LoaiPhongService;
import com.team.invoice.service.PhongService;
import com.team.invoice.util.FocusUtils;

public class PhongDialog extends JDialog {
    private JComboBox<String> cboLoaiCSVC;
    private JComboBox<String> cboTrucThuoc;
    
    private HintTextField txtMa;
    private HintTextField txtTen;
    private JComboBox<String> cboLoaiPhong;
    private JComboBox<String> cboTrangThai;

    private RoundedButton btnSave;

    private final PhongService phongService;
    private final Runnable onSuccess;

    public PhongDialog(Frame owner, PhongService service, Phong phong, Runnable onSuccess) {
        super(owner, true);
        this.phongService = service;
        this.onSuccess = onSuccess;

        setTitle("Quản Lý Cơ Sở Vật Chất");
        setSize(520, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColors.BG);

        initComponents();
        setupEventHandlers();
        
        // Kích hoạt trạng thái mặc định ban đầu là chọn PHÒNG
        cboLoaiCSVC.setSelectedItem("PHÒNG"); 
        
        FocusUtils.enableClearFocusOnClick(this.getContentPane());
    }

    private void initComponents() {
        RoundedPanel mainPanel = new RoundedPanel(15, Color.WHITE, AppColors.BORDER);
        mainPanel.setLayout(new GridLayout(6, 2, 10, 16));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Combo box chọn loại đối tượng
        cboLoaiCSVC = new JComboBox<>(new String[] { "PHÒNG", "TẦNG", "TÒA" });
        cboTrucThuoc = new JComboBox<>();
        txtMa = new HintTextField("Mã CSVC (VD: T1, T1.01)");
        txtTen = new HintTextField("Tên CSVC");
        cboLoaiPhong = new JComboBox<>();
        cboTrangThai = new JComboBox<>(new String[] {  "HOẠT ĐỘNG","KHÔNG HOẠT ĐỘNG" });

        mainPanel.add(new JLabel("Loại thêm mới (*):"));
        mainPanel.add(cboLoaiCSVC);

        mainPanel.add(new JLabel("Trực thuộc (*):"));
        mainPanel.add(cboTrucThuoc);

        mainPanel.add(new JLabel("Mã (*):"));
        mainPanel.add(txtMa);

        mainPanel.add(new JLabel("Tên (*):"));
        mainPanel.add(txtTen);

        mainPanel.add(new JLabel("Loại Phòng:"));
        mainPanel.add(cboLoaiPhong);

        mainPanel.add(new JLabel("Trạng Thái (*):"));
        mainPanel.add(cboTrangThai);

        add(mainPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(AppColors.BG);

        RoundedButton btnCancel = new RoundedButton("Hủy", Color.WHITE, AppColors.TEXT, AppColors.BORDER);
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.addActionListener(e -> dispose());

        btnSave = new RoundedButton("Lưu", AppColors.PRIMARY, Color.WHITE, AppColors.PRIMARY);
        btnSave.setPreferredSize(new Dimension(100, 35));
        btnSave.addActionListener(e -> saveData());

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnSave);

        add(bottomPanel, BorderLayout.SOUTH);
        
        loadLoaiPhongToComboBox();
    }

    private void loadLoaiPhongToComboBox() {
        cboLoaiPhong.removeAllItems();
        LoaiPhongService loaiPhongService = new LoaiPhongService();
        List<LoaiPhong> dsLoaiPhong = loaiPhongService.getAllLoaiPhong();
        if (dsLoaiPhong != null) {
            for (LoaiPhong lp : dsLoaiPhong) {
                cboLoaiPhong.addItem(lp.getMaLoaiPhong());
            }
        }
    }

    private void setupEventHandlers() {
        // Lắng nghe sự kiện thay đổi loại CSVC để cập nhật giao diện
        cboLoaiCSVC.addActionListener(e -> {
            String loai = (String) cboLoaiCSVC.getSelectedItem();
            cboTrucThuoc.removeAllItems();

            if ("TÒA".equals(loai)) {
                cboTrucThuoc.setEnabled(false);
                cboLoaiPhong.setSelectedItem(null);
                cboLoaiPhong.setEnabled(false);
                cboTrangThai.setSelectedItem("HOẠT ĐỘNG");
            } else if ("TẦNG".equals(loai)) {
                cboTrucThuoc.setEnabled(true);
                cboLoaiPhong.setEnabled(false);
                cboLoaiPhong.setSelectedItem(null);
                cboTrangThai.setSelectedItem("HOẠT ĐỘNG");
                
                // Gọi service lấy danh sách Tòa (Cần đảm bảo PhongService đã có hàm getAllToaIds)
                List<String> toas = phongService.getAllToaIds();
                if (toas != null) toas.forEach(cboTrucThuoc::addItem);
                
            } else if ("PHÒNG".equals(loai)) {
                cboTrucThuoc.setEnabled(true);
                cboLoaiPhong.setEnabled(true);
                cboTrangThai.setSelectedItem("TRONG");
                
                List<String> tangs = phongService.getAllTangIds();
                if (tangs != null) tangs.forEach(cboTrucThuoc::addItem);
            }
        });
    }

    
    private void saveData() {
        String loaiCsvc = cboLoaiCSVC.getSelectedItem().toString(); 
        String ma = txtMa.getText().trim();
        String ten = txtTen.getText().trim();

        if (ma.isEmpty() || ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã và Tên không được để trống!");
            return;
        }

        String idCha = null;
        if (cboTrucThuoc.isEnabled()) {
            if (cboTrucThuoc.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn mục trực thuộc (Tòa/Tầng)!");
                return;
            }
            idCha = cboTrucThuoc.getSelectedItem().toString();
        }

        String maLoaiPhong = null;
        if (cboLoaiPhong.isEnabled() && cboLoaiPhong.getSelectedItem() != null) {
            maLoaiPhong = cboLoaiPhong.getSelectedItem().toString();
        }

        String trangThai = cboTrangThai.getSelectedItem().toString();

        // Validation định dạng mã theo phân cấp
        if ("PHÒNG".equals(loaiCsvc)) {
            if (!ma.startsWith(idCha + ".")) {
                JOptionPane.showMessageDialog(this, "Mã phòng phải bắt đầu bằng Tầng trực thuộc.\nVí dụ: Tầng " + idCha + " -> Mã phòng: " + idCha + ".01");
                return;
            }
        }

        // Tạo Entity chung (hiện tại bạn đang tái sử dụng class Phong cho cả Tòa và Tầng)
        Phong p = new Phong();
        p.setMaPhong(ma);
        p.setTenPhong(ten);
        p.setIdCha(idCha);
        p.setMaLoaiPhong(maLoaiPhong);
        p.setTrangThai(trangThai);

        // Quy đổi string sang mã loại trong database
        String dbLoai = "PHONG";
        if ("TÒA".equals(loaiCsvc)) dbLoai = "TOA";
        else if ("TẦNG".equals(loaiCsvc)) dbLoai = "TANG";

        // Gọi phương thức lưu trữ (Lưu ý: PhongService cần có hàm createCSVC nhận 2 tham số như dưới đây)
        String message = phongService.createCSVC(p, dbLoai);
        JOptionPane.showMessageDialog(this, message);

        if ("Thành công".equals(message)) {
            if (onSuccess != null) onSuccess.run();
            dispose();
        }
    }
}