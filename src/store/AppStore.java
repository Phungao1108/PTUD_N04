package store;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AppStore {
    public static class Tenant {
        private String maKhach;
        private String hoTen;
        private String soCCCD;
        private String sdt;
        private String trangThai;
        private String maPhongDangO;

        public Tenant(String maKhach, String hoTen, String soCCCD, String sdt, String trangThai, String maPhongDangO) {
            this.maKhach = maKhach;
            this.hoTen = hoTen;
            this.soCCCD = soCCCD;
            this.sdt = sdt;
            this.trangThai = trangThai;
            this.maPhongDangO = maPhongDangO;
        }

        public String getMaKhach() { return maKhach; }
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        public String getSoCCCD() { return soCCCD; }
        public void setSoCCCD(String soCCCD) { this.soCCCD = soCCCD; }
        public String getSdt() { return sdt; }
        public void setSdt(String sdt) { this.sdt = sdt; }
        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
        public String getMaPhongDangO() { return maPhongDangO; }
        public void setMaPhongDangO(String maPhongDangO) { this.maPhongDangO = maPhongDangO; }
    }

    public static class Room {
        private String id;
        private String ten;
        private String maLoaiPhong;
        private String trangThaiPhong;
        private double giaTheoThang;
        private double giaDien;
        private double giaNuoc;
        private double phiDichVu;
        private int chiSoDienCu;
        private int chiSoNuocCu;
        private String kyChiSoGanNhat;

        public Room(String id, String ten, String maLoaiPhong, String trangThaiPhong, double giaTheoThang,
                double giaDien, double giaNuoc, double phiDichVu, int chiSoDienCu, int chiSoNuocCu) {
            this.id = id;
            this.ten = ten;
            this.maLoaiPhong = maLoaiPhong;
            this.trangThaiPhong = trangThaiPhong;
            this.giaTheoThang = giaTheoThang;
            this.giaDien = giaDien;
            this.giaNuoc = giaNuoc;
            this.phiDichVu = phiDichVu;
            this.chiSoDienCu = chiSoDienCu;
            this.chiSoNuocCu = chiSoNuocCu;
            this.kyChiSoGanNhat = "03/2026";
        }

        public String getId() { return id; }
        public String getTen() { return ten; }
        public String getMaLoaiPhong() { return maLoaiPhong; }
        public String getTrangThaiPhong() { return trangThaiPhong; }
        public void setTrangThaiPhong(String trangThaiPhong) { this.trangThaiPhong = trangThaiPhong; }
        public double getGiaTheoThang() { return giaTheoThang; }
        public void setGiaTheoThang(double giaTheoThang) { this.giaTheoThang = giaTheoThang; }
        public double getGiaDien() { return giaDien; }
        public void setGiaDien(double giaDien) { this.giaDien = giaDien; }
        public double getGiaNuoc() { return giaNuoc; }
        public void setGiaNuoc(double giaNuoc) { this.giaNuoc = giaNuoc; }
        public double getPhiDichVu() { return phiDichVu; }
        public void setPhiDichVu(double phiDichVu) { this.phiDichVu = phiDichVu; }
        public int getChiSoDienCu() { return chiSoDienCu; }
        public void setChiSoDienCu(int chiSoDienCu) { this.chiSoDienCu = chiSoDienCu; }
        public int getChiSoNuocCu() { return chiSoNuocCu; }
        public void setChiSoNuocCu(int chiSoNuocCu) { this.chiSoNuocCu = chiSoNuocCu; }
        public String getKyChiSoGanNhat() { return kyChiSoGanNhat; }
        public void setKyChiSoGanNhat(String kyChiSoGanNhat) { this.kyChiSoGanNhat = kyChiSoGanNhat; }
    }

    public static class Contract {
        private String maHopDong;
        private String maPhong;
        private String maKhachChinh;
        private Date ngayBatDau;
        private Date ngayKetThuc;
        private double tienDatCoc;
        private String trangThai;

        public Contract(String maHopDong, String maPhong, String maKhachChinh, Date ngayBatDau,
                Date ngayKetThuc, double tienDatCoc, String trangThai) {
            this.maHopDong = maHopDong;
            this.maPhong = maPhong;
            this.maKhachChinh = maKhachChinh;
            this.ngayBatDau = ngayBatDau;
            this.ngayKetThuc = ngayKetThuc;
            this.tienDatCoc = tienDatCoc;
            this.trangThai = trangThai;
        }

        public String getMaHopDong() { return maHopDong; }
        public String getMaPhong() { return maPhong; }
        public String getMaKhachChinh() { return maKhachChinh; }
        public Date getNgayBatDau() { return ngayBatDau; }
        public void setNgayBatDau(Date ngayBatDau) { this.ngayBatDau = ngayBatDau; }
        public Date getNgayKetThuc() { return ngayKetThuc; }
        public void setNgayKetThuc(Date ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
        public double getTienDatCoc() { return tienDatCoc; }
        public void setTienDatCoc(double tienDatCoc) { this.tienDatCoc = tienDatCoc; }
        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    }

    public interface DataListener { void onDataChanged(); }

    private static final AppStore INSTANCE = new AppStore();
    private final List<Tenant> tenants = new ArrayList<Tenant>();
    private final List<Room> rooms = new ArrayList<Room>();
    private final List<Contract> contracts = new ArrayList<Contract>();
    private final List<DataListener> listeners = new ArrayList<DataListener>();
    private int tenantSeq = 2;
    private int contractSeq = 2;

    public static AppStore getInstance() { return INSTANCE; }

    private AppStore() { seed(); }

    private void seed() {
        rooms.add(new Room("a1.01", "Phòng 101", "LP01", "DANG_O", 5000000, 3500, 18000, 300000, 1000, 50));
        rooms.add(new Room("a1.02", "Phòng 102", "LP01", "TRONG", 4500000, 3500, 18000, 250000, 800, 45));
        rooms.add(new Room("a1.03", "Phòng 103", "LP01", "TRONG", 4300000, 3500, 18000, 250000, 650, 40));
        rooms.add(new Room("a2.01", "Phòng 201", "LP02", "TRONG", 5600000, 3800, 20000, 350000, 1200, 60));
        tenants.add(new Tenant("KT01", "Lê Minh", "079000123456", "0909000111", "DANG_THUE", "a1.01"));
        tenants.add(new Tenant("KT02", "Trần Hương", "079000456789", "0909000222", "CHO_THUE", null));
        contracts.add(new Contract("HD01", "a1.01", "KT01", parse("2026-01-01"), parse("2027-01-01"), 5000000, "HIEU_LUC"));
    }

    public synchronized List<Tenant> getTenants() { return new ArrayList<Tenant>(tenants); }
    public synchronized List<Room> getRooms() { return new ArrayList<Room>(rooms); }
    public synchronized List<Contract> getContracts() { return new ArrayList<Contract>(contracts); }
    public synchronized void addListener(DataListener listener) { if (!listeners.contains(listener)) listeners.add(listener); }
    public synchronized void removeListener(DataListener listener) { listeners.remove(listener); }
    private synchronized void fireChange() {
        List<DataListener> copy = new ArrayList<DataListener>(listeners);
        for (int i = 0; i < copy.size(); i++) copy.get(i).onDataChanged();
    }

    public synchronized Tenant addTenant(String hoTen, String cccd, String sdt) {
        if (isBlank(hoTen) || isBlank(cccd)) throw new IllegalArgumentException("Họ tên và CCCD là bắt buộc.");
        if (findTenantByCCCD(cccd) != null) throw new IllegalArgumentException("CCCD đã tồn tại trong hệ thống.");
        Tenant tenant = new Tenant(String.format("KT%02d", tenantSeq++), hoTen.trim(), cccd.trim(), safe(sdt), "CHO_THUE", null);
        tenants.add(0, tenant);
        fireChange();
        return tenant;
    }

    public synchronized void updateTenant(String maKhach, String hoTen, String cccd, String sdt) {
        Tenant tenant = getTenant(maKhach);
        if (tenant == null) throw new IllegalArgumentException("Không tìm thấy khách thuê.");
        Tenant same = findTenantByCCCD(cccd);
        if (same != null && !same.getMaKhach().equals(maKhach)) throw new IllegalArgumentException("CCCD đã tồn tại.");
        tenant.setHoTen(hoTen.trim());
        tenant.setSoCCCD(cccd.trim());
        tenant.setSdt(safe(sdt));
        fireChange();
    }

    public synchronized void removeTenant(String maKhach) {
        if (hasActiveContractForTenant(maKhach)) throw new IllegalArgumentException("Không thể xóa khách đang có hợp đồng hiệu lực.");
        Tenant target = getTenant(maKhach);
        if (target != null) {
            tenants.remove(target);
            fireChange();
        }
    }

    public synchronized Contract createContract(String maKhach, String maPhong, Date ngayBatDau, Date ngayKetThuc, double tienDatCoc) {
        validateContract(maKhach, maPhong, ngayBatDau, ngayKetThuc, tienDatCoc, null);
        Contract contract = new Contract(String.format("HD%02d", contractSeq++), maPhong, maKhach, ngayBatDau, ngayKetThuc, tienDatCoc, "HIEU_LUC");
        contracts.add(0, contract);
        applyContractEffect(contract);
        fireChange();
        return contract;
    }

    public synchronized void renewContract(String maHopDong, Date newEndDate) {
        Contract contract = getContract(maHopDong);
        if (contract == null) throw new IllegalArgumentException("Không tìm thấy hợp đồng.");
        if (!"HIEU_LUC".equals(contract.getTrangThai())) throw new IllegalArgumentException("Chỉ gia hạn hợp đồng còn hiệu lực.");
        if (newEndDate == null || !newEndDate.after(contract.getNgayKetThuc())) throw new IllegalArgumentException("Ngày kết thúc mới phải lớn hơn ngày hiện tại của hợp đồng.");
        contract.setNgayKetThuc(newEndDate);
        fireChange();
    }

    public synchronized void updateContract(String maHopDong, Date ngayBatDau, Date ngayKetThuc, double tienDatCoc) {
        Contract contract = getContract(maHopDong);
        if (contract == null) throw new IllegalArgumentException("Không tìm thấy hợp đồng.");
        if (!"HIEU_LUC".equals(contract.getTrangThai())) throw new IllegalArgumentException("Chỉ được sửa hợp đồng còn hiệu lực.");
        validateContract(contract.getMaKhachChinh(), contract.getMaPhong(), ngayBatDau, ngayKetThuc, tienDatCoc, maHopDong);
        contract.setNgayBatDau(ngayBatDau);
        contract.setNgayKetThuc(ngayKetThuc);
        contract.setTienDatCoc(tienDatCoc);
        fireChange();
    }

    public synchronized void endContract(String maHopDong) {
        Contract contract = getContract(maHopDong);
        if (contract == null) throw new IllegalArgumentException("Không tìm thấy hợp đồng.");
        contract.setTrangThai("DA_KET_THUC");
        clearContractEffect(contract);
        fireChange();
    }

    private void validateContract(String maKhach, String maPhong, Date ngayBatDau, Date ngayKetThuc, double tienDatCoc, String excludeMaHopDong) {
        if (getTenant(maKhach) == null) throw new IllegalArgumentException("Khách thuê không tồn tại.");
        Room room = getRoom(maPhong);
        if (room == null) throw new IllegalArgumentException("Phòng không tồn tại.");
        if (ngayBatDau == null || ngayKetThuc == null || !ngayKetThuc.after(ngayBatDau)) throw new IllegalArgumentException("Ngày hợp đồng không hợp lệ.");
        if (tienDatCoc < 0) throw new IllegalArgumentException("Tiền đặt cọc không được âm.");
        if (hasAnotherActiveContractForRoom(maPhong, excludeMaHopDong)) throw new IllegalArgumentException("Phòng đã có hợp đồng còn hiệu lực.");
        if (hasAnotherActiveContractForTenant(maKhach, excludeMaHopDong)) throw new IllegalArgumentException("Khách này đang có hợp đồng hiệu lực.");
        if (!"TRONG".equals(room.getTrangThaiPhong()) && excludeMaHopDong == null) throw new IllegalArgumentException("Phòng không ở trạng thái trống.");
    }

    private void applyContractEffect(Contract contract) {
        Room room = getRoom(contract.getMaPhong());
        if (room != null) room.setTrangThaiPhong("DANG_O");
        Tenant tenant = getTenant(contract.getMaKhachChinh());
        if (tenant != null) {
            tenant.setTrangThai("DANG_THUE");
            tenant.setMaPhongDangO(contract.getMaPhong());
        }
    }

    private void clearContractEffect(Contract contract) {
        Room room = getRoom(contract.getMaPhong());
        if (room != null) room.setTrangThaiPhong("TRONG");
        Tenant tenant = getTenant(contract.getMaKhachChinh());
        if (tenant != null) {
            tenant.setMaPhongDangO(null);
            tenant.setTrangThai("CHO_THUE");
        }
    }

    public synchronized void updateRoomPricing(String maPhong, double giaTheoThang, double giaDien, double giaNuoc, double phiDichVu) {
        Room room = getRoom(maPhong);
        if (room == null) throw new IllegalArgumentException("Không tìm thấy phòng.");
        if (giaTheoThang <= 0 || giaDien < 0 || giaNuoc < 0 || phiDichVu < 0) throw new IllegalArgumentException("Đơn giá không hợp lệ.");
        room.setGiaTheoThang(giaTheoThang);
        room.setGiaDien(giaDien);
        room.setGiaNuoc(giaNuoc);
        room.setPhiDichVu(phiDichVu);
        fireChange();
    }

    public synchronized void recordMeters(String maPhong, String ky, int dienMoi, int nuocMoi) {
        Room room = getRoom(maPhong);
        if (room == null) throw new IllegalArgumentException("Không tìm thấy phòng.");
        if (!hasActiveContractForRoom(maPhong)) throw new IllegalArgumentException("Chỉ ghi chỉ số cho phòng có hợp đồng hiệu lực.");
        if (dienMoi < room.getChiSoDienCu()) throw new IllegalArgumentException("Chỉ số điện mới phải >= chỉ số cũ.");
        if (nuocMoi < room.getChiSoNuocCu()) throw new IllegalArgumentException("Chỉ số nước mới phải >= chỉ số cũ.");
        room.setChiSoDienCu(dienMoi);
        room.setChiSoNuocCu(nuocMoi);
        room.setKyChiSoGanNhat(ky);
        fireChange();
    }

    public synchronized Tenant getTenant(String maKhach) {
        for (int i = 0; i < tenants.size(); i++) if (tenants.get(i).getMaKhach().equals(maKhach)) return tenants.get(i);
        return null;
    }

    public synchronized Room getRoom(String maPhong) {
        for (int i = 0; i < rooms.size(); i++) if (rooms.get(i).getId().equals(maPhong)) return rooms.get(i);
        return null;
    }

    public synchronized Contract getContract(String maHopDong) {
        for (int i = 0; i < contracts.size(); i++) if (contracts.get(i).getMaHopDong().equals(maHopDong)) return contracts.get(i);
        return null;
    }

    public synchronized Tenant findTenantByCCCD(String cccd) {
        if (isBlank(cccd)) return null;
        for (int i = 0; i < tenants.size(); i++) if (cccd.trim().equalsIgnoreCase(tenants.get(i).getSoCCCD())) return tenants.get(i);
        return null;
    }

    public synchronized boolean hasActiveContractForRoom(String maPhong) {
        return hasAnotherActiveContractForRoom(maPhong, null);
    }

    public synchronized boolean hasActiveContractForTenant(String maKhach) {
        return hasAnotherActiveContractForTenant(maKhach, null);
    }

    private synchronized boolean hasAnotherActiveContractForRoom(String maPhong, String excludeMaHopDong) {
        for (int i = 0; i < contracts.size(); i++) {
            Contract c = contracts.get(i);
            if (maPhong.equals(c.getMaPhong()) && "HIEU_LUC".equals(c.getTrangThai()) && (excludeMaHopDong == null || !excludeMaHopDong.equals(c.getMaHopDong()))) return true;
        }
        return false;
    }

    private synchronized boolean hasAnotherActiveContractForTenant(String maKhach, String excludeMaHopDong) {
        for (int i = 0; i < contracts.size(); i++) {
            Contract c = contracts.get(i);
            if (maKhach.equals(c.getMaKhachChinh()) && "HIEU_LUC".equals(c.getTrangThai()) && (excludeMaHopDong == null || !excludeMaHopDong.equals(c.getMaHopDong()))) return true;
        }
        return false;
    }

    public synchronized Contract getActiveContractForRoom(String maPhong) {
        for (int i = 0; i < contracts.size(); i++) {
            Contract c = contracts.get(i);
            if (maPhong.equals(c.getMaPhong()) && "HIEU_LUC".equals(c.getTrangThai())) return c;
        }
        return null;
    }

    public synchronized String getTenantNameForRoom(String maPhong) {
        Contract c = getActiveContractForRoom(maPhong);
        if (c == null) return "Chưa có khách";
        Tenant t = getTenant(c.getMaKhachChinh());
        return t == null ? "Chưa có khách" : t.getHoTen();
    }

    public synchronized List<Room> getRoomsWithActiveContract() {
        List<Room> result = new ArrayList<Room>();
        for (int i = 0; i < rooms.size(); i++) if (hasActiveContractForRoom(rooms.get(i).getId())) result.add(rooms.get(i));
        return result;
    }

    public synchronized List<Room> searchRooms(String keyword, String status) {
        List<Room> result = new ArrayList<Room>();
        String safeKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            boolean matchKeyword = safeKeyword.isEmpty() || room.getId().toLowerCase().contains(safeKeyword) || room.getTen().toLowerCase().contains(safeKeyword);
            boolean matchStatus = status == null || status.trim().isEmpty() || "Tất cả".equals(status) || status.equals(room.getTrangThaiPhong());
            if (matchKeyword && matchStatus) result.add(room);
        }
        return result;
    }

    public synchronized List<Tenant> searchTenants(String keyword) {
        String safeKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        if (safeKeyword.isEmpty()) return getTenants();
        List<Tenant> result = new ArrayList<Tenant>();
        for (int i = 0; i < tenants.size(); i++) {
            Tenant t = tenants.get(i);
            String room = t.getMaPhongDangO() == null ? "" : t.getMaPhongDangO();
            if (t.getHoTen().toLowerCase().contains(safeKeyword) || t.getSoCCCD().toLowerCase().contains(safeKeyword) || safe(t.getSdt()).toLowerCase().contains(safeKeyword) || room.toLowerCase().contains(safeKeyword)) result.add(t);
        }
        return result;
    }

    public synchronized List<Contract> searchContracts(String keyword, String status) {
        String safeKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        List<Contract> result = new ArrayList<Contract>();
        for (int i = 0; i < contracts.size(); i++) {
            Contract c = contracts.get(i);
            Tenant t = getTenant(c.getMaKhachChinh());
            boolean matchKeyword = safeKeyword.isEmpty() || c.getMaHopDong().toLowerCase().contains(safeKeyword) || c.getMaPhong().toLowerCase().contains(safeKeyword)
                    || (t != null && t.getHoTen().toLowerCase().contains(safeKeyword));
            boolean matchStatus = status == null || status.trim().isEmpty() || "Tất cả".equals(status) || status.equals(c.getTrangThai());
            if (matchKeyword && matchStatus) result.add(c);
        }
        return result;
    }

    public static Date parse(String value) {
        try { return new SimpleDateFormat("yyyy-MM-dd").parse(value); }
        catch (Exception ex) { return new Date(); }
    }

    public static String format(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String money(double value) {
        return new DecimalFormat("#,##0").format(value);
    }

    private String safe(String v) { return v == null ? "" : v.trim(); }
    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
}
