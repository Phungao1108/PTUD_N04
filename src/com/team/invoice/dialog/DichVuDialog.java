package com.team.invoice.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;

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
import com.team.invoice.service.DichVuService;

public class DichVuDialog extends JDialog {
    private JTextField txtMaDV;
    private JTextField txtTenDV;
    private JTextField txtDonVi;
    private JComboBox<String> cbLoaiDichVu;
    
    private DichVuService dichVuService;
    private DichVu currentDichVu;
    private boolean isEditMode;
    private Runnable onSuccess;

    public DichVuDialog(Frame owner, DichVuService service, DichVu dichVu, Runnable onSuccess) {
        super(owner, true); // true = Modal dialog (phải đóng form này mới bấm được màn hình dưới)
        this.dichVuService = service;
        this.currentDichVu = dichVu;
        this.onSuccess = onSuccess;
        
        // Nếu dichVu truyền vào khác null và có mã DV, nghĩa là đang ở chế độ Sửa
        this.isEditMode = (dichVu != null && dichVu.getMaDV() != null && !dichVu.getMaDV().isEmpty());

        setTitle(isEditMode ? "Cập nhật Dịch Vụ" : "Thêm Dịch Vụ Mới");
        setSize(450, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColors.BG);

        initComponents();
        
        if (isEditMode) {
            fillData();
        }
    }

    private void initComponents() {
        // Panel chứa Form nhập liệu
        RoundedPanel mainPanel = new RoundedPanel(15, Color.WHITE, AppColors.BORDER);
        mainPanel.setLayout(new GridLayout(4, 2, 10, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        txtMaDV = new HintTextField("Nhập mã dịch vụ (VD: DV01)...");
        txtTenDV = new HintTextField("Nhập tên (VD: Điện, Nước)...");
        txtDonVi = new HintTextField("Nhập đơn vị (VD: kWh, Khối)...");
        cbLoaiDichVu = new JComboBox<>(new String[]{"CO_DINH", "CHI_SO"});
        cbLoaiDichVu.setBackground(Color.WHITE);

        // Nếu là sửa thì không cho phép đổi Mã Dịch Vụ (Primary Key)
        if (isEditMode) {
            txtMaDV.setEditable(false);
            txtMaDV.setBackground(AppColors.BG); 
            txtMaDV.setToolTipText("Không thể thay đổi mã dịch vụ");
        }

        mainPanel.add(new JLabel("Mã Dịch Vụ (*):")); mainPanel.add(txtMaDV);
        mainPanel.add(new JLabel("Tên Dịch Vụ (*):")); mainPanel.add(txtTenDV);
        mainPanel.add(new JLabel("Đơn Vị (*):")); mainPanel.add(txtDonVi);
        mainPanel.add(new JLabel("Loại Dịch Vụ (*):")); mainPanel.add(cbLoaiDichVu);

        add(mainPanel, BorderLayout.CENTER);

        // Panel chứa các nút bấm
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(AppColors.BG);

        RoundedButton btnCancel = new RoundedButton("Hủy", Color.WHITE, AppColors.TEXT, AppColors.BORDER);
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.addActionListener(e -> dispose());

        RoundedButton btnSave = new RoundedButton("Lưu", AppColors.PRIMARY, Color.WHITE, AppColors.PRIMARY);
        btnSave.setPreferredSize(new Dimension(100, 35));
        btnSave.addActionListener(e -> saveDichVu());

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnSave);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void fillData() {
        txtMaDV.setText(currentDichVu.getMaDV());
        txtTenDV.setText(currentDichVu.getTenDV());
        txtDonVi.setText(currentDichVu.getDonVi());
        cbLoaiDichVu.setSelectedItem(currentDichVu.getLoaiDichVu());
    }

    private void saveDichVu() {
        if (currentDichVu == null) {
            currentDichVu = new DichVu();
        }
        
        // Lấy dữ liệu từ giao diện đưa vào Object
        currentDichVu.setMaDV(txtMaDV.getText().trim());
        currentDichVu.setTenDV(txtTenDV.getText().trim());
        currentDichVu.setDonVi(txtDonVi.getText().trim());
        currentDichVu.setLoaiDichVu((String) cbLoaiDichVu.getSelectedItem());

        // Gọi Service để xử lý logic thêm/sửa
        String message;
        if (isEditMode) {
            message = dichVuService.updateDichVu(currentDichVu);
        } else {
            message = dichVuService.createDichVu(currentDichVu);
        }

        // Hiển thị thông báo
        JOptionPane.showMessageDialog(this, message);
        
        // Nếu thành công thì đóng form và tải lại bảng ở màn hình chính
        if (message.equals("Thành công")) {
            if (onSuccess != null) {
                onSuccess.run(); 
            }
            dispose(); 
        }
    }
}