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
import javax.swing.JTextField;

import com.team.invoice.components.AppColors;
import com.team.invoice.components.HintTextField;
import com.team.invoice.components.RoundedButton;
import com.team.invoice.components.RoundedPanel;
import com.team.invoice.entity.DichVu;
import com.team.invoice.entity.LoaiPhong;
import com.team.invoice.service.BangGiaService;
import com.team.invoice.service.DichVuService;
import com.team.invoice.service.LoaiPhongService;

public class ThemDonGiaDialog extends JDialog {
    private JComboBox<String> cboPhanLoai;
    private JComboBox<String> cboDoiTuong;
    private JTextField txtGia;
    
    private BangGiaService bangGiaService;
    private String selectedMaBG;
    private Runnable onSuccess;
    
    // Lưu trữ ID thực sự của phòng/dịch vụ ẩn đằng sau Tên hiển thị
    private List<LoaiPhong> listLP;
    private List<DichVu> listDV;

    public ThemDonGiaDialog(Frame owner, String maBG, BangGiaService service, Runnable onSuccess) {
        super(owner, true);
        this.selectedMaBG = maBG;
        this.bangGiaService = service;
        this.onSuccess = onSuccess;

        setTitle("Thêm Đơn Giá Vào Đợt: " + maBG);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColors.BG);

        initComponents();
        loadDoiTuong(); // Load dữ liệu lần đầu
    }

    private void initComponents() {
        RoundedPanel mainPanel = new RoundedPanel(15, Color.WHITE, AppColors.BORDER);
        mainPanel.setLayout(new GridLayout(3, 2, 10, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        cboPhanLoai = new JComboBox<>(new String[]{"Phòng", "Dịch Vụ"});
        cboPhanLoai.setBackground(Color.WHITE);
        cboPhanLoai.addActionListener(e -> loadDoiTuong());

        cboDoiTuong = new JComboBox<>();
        cboDoiTuong.setBackground(Color.WHITE);

        txtGia = new HintTextField("Nhập số tiền (VNĐ)...");

        mainPanel.add(new JLabel("Phân Loại:")); mainPanel.add(cboPhanLoai);
        mainPanel.add(new JLabel("Chọn Đối Tượng:")); mainPanel.add(cboDoiTuong);
        mainPanel.add(new JLabel("Đơn Giá:")); mainPanel.add(txtGia);

        add(mainPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(AppColors.BG);

        RoundedButton btnSave = new RoundedButton("Thêm Đơn Giá", AppColors.PRIMARY, Color.WHITE, AppColors.PRIMARY);
        btnSave.setPreferredSize(new Dimension(130, 35));
        btnSave.addActionListener(e -> saveDonGia());

        bottomPanel.add(btnSave);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadDoiTuong() {
        cboDoiTuong.removeAllItems();
        if (cboPhanLoai.getSelectedIndex() == 0) {
            listLP = new LoaiPhongService().getAllLoaiPhong();
            for (LoaiPhong lp : listLP) cboDoiTuong.addItem(lp.getTenLoaiPhong());
        } else {
            listDV = new DichVuService().getAllDichVu();
            for (DichVu dv : listDV) cboDoiTuong.addItem(dv.getTenDV());
        }
    }

    private void saveDonGia() {
        try {
            double gia = Double.parseDouble(txtGia.getText().trim());
            int index = cboDoiTuong.getSelectedIndex();
            if (index < 0) return;

            boolean success = false;
            if (cboPhanLoai.getSelectedIndex() == 0) {
                // Thêm giá phòng
                String maLP = listLP.get(index).getMaLoaiPhong();
                // Ta có thể dùng lại hàm updateGiaPhong, DAO của nó cần đổi thành lệnh INSERT hoặc UPDATE
                // Tạm gọi Insert DAO trực tiếp ở đây hoặc thông qua Service
                success = new com.team.invoice.dao.BangGiaDAO().insertGiaPhong(selectedMaBG, maLP, gia);
            } else {
                // Thêm giá dịch vụ
                String maDV = listDV.get(index).getMaDV();
                success = new com.team.invoice.dao.BangGiaDAO().insertGiaDichVu(selectedMaBG, maDV, gia);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Đã thêm đơn giá thành công!");
                if (onSuccess != null) onSuccess.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Đơn giá cho mục này có thể đã tồn tại trong đợt giá. Hãy dùng chức năng Sửa!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ!");
        }
    }
}