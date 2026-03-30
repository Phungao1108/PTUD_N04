-- =============================================
-- 1. KHỞI TẠO CẤU TRÚC BẢNG
-- =============================================

CREATE TABLE TaiKhoan (
    maTK VARCHAR(50) PRIMARY KEY,
    tenDangNhap VARCHAR(50) UNIQUE NOT NULL,
    matKhauHash VARCHAR(255) NOT NULL,
    vaiTro VARCHAR(20) CHECK (vaiTro IN ('ADMIN', 'MANAGER')),
    trangThai VARCHAR(20) DEFAULT 'ACTIVE',
    isDeleted BIT DEFAULT 0
);
GO

CREATE TABLE LoaiPhong (
    maLoaiPhong VARCHAR(50) PRIMARY KEY,
    tenLoaiPhong NVARCHAR(100) NOT NULL,
    dienTichChuan DECIMAL(10, 2),
    mota NVARCHAR(255),
    isDeleted BIT DEFAULT 0
);
GO

CREATE TABLE CoSoVatChat (
    id VARCHAR(50) PRIMARY KEY,
    ten NVARCHAR(100) NOT NULL,
    loai VARCHAR(20) CHECK (loai IN ('KHU_VUC', 'TANG', 'PHONG')),
    idCha VARCHAR(50) NULL,
    trangThaiPhong VARCHAR(20) DEFAULT 'TRONG',
    maLoaiPhong VARCHAR(50) NULL,
    isDeleted BIT DEFAULT 0,
    FOREIGN KEY (idCha) REFERENCES CoSoVatChat(id),
    FOREIGN KEY (maLoaiPhong) REFERENCES LoaiPhong(maLoaiPhong)
);
GO

CREATE TABLE DichVu (
    maDV VARCHAR(50) PRIMARY KEY,
    tenDV NVARCHAR(100) NOT NULL,
    donVi NVARCHAR(20),
    loaiDichVu VARCHAR(20) CHECK (loaiDichVu IN ('CO_DINH', 'CHI_SO')),
    isDeleted BIT DEFAULT 0
);
GO

CREATE TABLE BangGia (
    maBG VARCHAR(50) PRIMARY KEY,
    ngayHieuLuc DATE NOT NULL,
    ngayKetThuc DATE NULL,
    trangThai VARCHAR(20) DEFAULT 'DANG_AP_DUNG',
    isDeleted BIT DEFAULT 0
);
GO

CREATE TABLE DonGiaPhong (
    maBG VARCHAR(50),
    maLoaiPhong VARCHAR(50),
    giaTheoThang DECIMAL(18, 2) NOT NULL,
    PRIMARY KEY (maBG, maLoaiPhong),
    FOREIGN KEY (maBG) REFERENCES BangGia(maBG),
    FOREIGN KEY (maLoaiPhong) REFERENCES LoaiPhong(maLoaiPhong)
);
GO

CREATE TABLE DonGiaDichVu (
    maBG VARCHAR(50),
    maDV VARCHAR(50),
    gia DECIMAL(18, 2) NOT NULL,
    PRIMARY KEY (maBG, maDV),
    FOREIGN KEY (maBG) REFERENCES BangGia(maBG),
    FOREIGN KEY (maDV) REFERENCES DichVu(maDV)
);
GO

CREATE TABLE KhachThue (
    maKhach VARCHAR(50) PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    soCCCD VARCHAR(20) UNIQUE NOT NULL,
    sdt VARCHAR(15),
    trangThai VARCHAR(20) DEFAULT 'ACTIVE',
    isDeleted BIT DEFAULT 0
);
GO

CREATE TABLE HopDong (
    maHopDong VARCHAR(50) PRIMARY KEY,
    maPhong VARCHAR(50) NOT NULL,
    maKhachChinh VARCHAR(50) NOT NULL,
    ngayBatDau DATE NOT NULL,
    ngayKetThuc DATE NOT NULL,
    tienDatCoc DECIMAL(18, 2) DEFAULT 0,
    trangThai VARCHAR(20) DEFAULT 'HIEU_LUC',
    isDeleted BIT DEFAULT 0,
    FOREIGN KEY (maPhong) REFERENCES CoSoVatChat(id),
    FOREIGN KEY (maKhachChinh) REFERENCES KhachThue(maKhach)
);
GO

CREATE TABLE ChiPhi (
    maCS VARCHAR(50) PRIMARY KEY,
    maPhong VARCHAR(50) NOT NULL,
    ky VARCHAR(10) NOT NULL,
    dien_cu INT DEFAULT 0,
    dien_moi INT NOT NULL,
    nuoc_cu INT DEFAULT 0,
    nuoc_moi INT NOT NULL,
    ngayGhi DATE DEFAULT GETDATE(),
    FOREIGN KEY (maPhong) REFERENCES CoSoVatChat(id),
    CONSTRAINT UNQ_PhongKy UNIQUE(maPhong, ky),
    CONSTRAINT CHK_Dien CHECK (dien_moi >= dien_cu),
    CONSTRAINT CHK_Nuoc CHECK (nuoc_moi >= nuoc_cu)
);
GO

CREATE TABLE HoaDon (
    maHoaDon VARCHAR(50) PRIMARY KEY,
    maPhong VARCHAR(50) NOT NULL,
    maTK_NguoiLap VARCHAR(50),
    ky VARCHAR(10) NOT NULL,
    ngayLap DATETIME DEFAULT CURRENT_TIMESTAMP,
    trangThai VARCHAR(20) DEFAULT 'CHO_THANH_TOAN',
    isDeleted BIT DEFAULT 0,
    FOREIGN KEY (maPhong) REFERENCES CoSoVatChat(id),
    FOREIGN KEY (maTK_NguoiLap) REFERENCES TaiKhoan(maTK)
);
GO

CREATE TABLE ChiTietHoaDon (
    maCT VARCHAR(50) PRIMARY KEY,
    maHoaDon VARCHAR(50) NOT NULL,
    tenNoiDung NVARCHAR(255),
    soLuong DECIMAL(10, 2) DEFAULT 1,
    donGiaSnapshot DECIMAL(18, 2) NOT NULL,
    thanhTien AS (soLuong * donGiaSnapshot),
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon)
);
GO

CREATE TABLE NhatKyHeThong (
    maLog INT IDENTITY(1,1) PRIMARY KEY,
    maHoaDon VARCHAR(50),
    maTK VARCHAR(50),
    thoiGian DATETIME DEFAULT CURRENT_TIMESTAMP,
    hanhDong NVARCHAR(255),
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
    FOREIGN KEY (maTK) REFERENCES TaiKhoan(maTK)
);
GO

-- =============================================
-- 2. INDEXES & VIEW
-- =============================================

CREATE INDEX IX_Phong_TrangThai ON CoSoVatChat(trangThaiPhong) WHERE loai = 'PHONG';
GO

CREATE INDEX IX_KhachThue_CCCD ON KhachThue(soCCCD);
GO

CREATE INDEX IX_HoaDon_Ky ON HoaDon(ky);
GO

CREATE VIEW v_HoaDonTong AS
SELECT
    h.maHoaDon,
    h.maPhong,
    h.ky,
    h.ngayLap,
    h.trangThai,
    ISNULL(SUM(ct.thanhTien), 0) AS TongTienThanhToan
FROM HoaDon h
LEFT JOIN ChiTietHoaDon ct ON h.maHoaDon = ct.maHoaDon
WHERE h.isDeleted = 0
GROUP BY h.maHoaDon, h.maPhong, h.ky, h.ngayLap, h.trangThai;
GO

-- =============================================
-- 3. TRIGGER CHECK MÃ PHÒNG
-- =============================================

CREATE TRIGGER trg_ValidateRoomID
ON CoSoVatChat
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1
        FROM inserted i
        LEFT JOIN CoSoVatChat cha ON i.idCha = cha.id
        WHERE i.loai = 'PHONG'
          AND (
                cha.id IS NULL
                OR cha.loai <> 'TANG'
                OR i.id NOT LIKE cha.id + '.%'
              )
    )
    BEGIN
        RAISERROR (N'Mã phòng phải có dạng [MãTầng].[SốPhòng] và phải thuộc một tầng hợp lệ!', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END
END;
GO

-- =============================================
-- 4. SEED DATA
-- =============================================

-- ===== TÀI KHOẢN =====
INSERT INTO TaiKhoan (maTK, tenDangNhap, matKhauHash, vaiTro, trangThai, isDeleted)
VALUES
('TK01', 'admin', 'pass', 'ADMIN', 'ACTIVE', 0);
GO

-- ===== LOẠI PHÒNG =====
INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, dienTichChuan, mota, isDeleted)
VALUES
('LP01', N'Phòng VIP', 30.0, N'Nội thất cao cấp', 0),
('LP02', N'Phòng Thường', 20.0, N'Phòng tiêu chuẩn', 0);
GO

-- ===== KHU / TẦNG =====
INSERT INTO CoSoVatChat (id, ten, loai, idCha, trangThaiPhong, maLoaiPhong, isDeleted)
VALUES
('a',  N'Khu A',  'KHU_VUC', NULL, 'TRONG', NULL, 0),
('a1', N'Tầng 1', 'TANG',    'a',  'TRONG', NULL, 0),
('a2', N'Tầng 2', 'TANG',    'a',  'TRONG', NULL, 0),
('a3', N'Tầng 3', 'TANG',    'a',  'TRONG', NULL, 0),
('a4', N'Tầng 4', 'TANG',    'a',  'TRONG', NULL, 0),

('b',  N'Khu B',  'KHU_VUC', NULL, 'TRONG', NULL, 0),
('b1', N'Tầng 1', 'TANG',    'b',  'TRONG', NULL, 0),
('b2', N'Tầng 2', 'TANG',    'b',  'TRONG', NULL, 0),
('b3', N'Tầng 3', 'TANG',    'b',  'TRONG', NULL, 0),
('b4', N'Tầng 4', 'TANG',    'b',  'TRONG', NULL, 0);
GO

-- ===== PHÒNG =====
INSERT INTO CoSoVatChat (id, ten, loai, idCha, trangThaiPhong, maLoaiPhong, isDeleted)
VALUES
('a1.01', N'Phòng 101', 'PHONG', 'a1', 'DANG_THUE', 'LP01', 0),
('a1.02', N'Phòng 102', 'PHONG', 'a1', 'DANG_THUE', 'LP01', 0),
('a1.03', N'Phòng 103', 'PHONG', 'a1', 'DANG_THUE', 'LP02', 0),

('a2.01', N'Phòng 201', 'PHONG', 'a2', 'TRONG',     'LP01', 0),
('a2.02', N'Phòng 202', 'PHONG', 'a2', 'TRONG',     'LP02', 0),

('b1.01', N'Phòng 101', 'PHONG', 'b1', 'DANG_THUE', 'LP02', 0),
('b1.02', N'Phòng 102', 'PHONG', 'b1', 'TRONG',     'LP01', 0);
GO

-- ===== KHÁCH THUÊ =====
INSERT INTO KhachThue (maKhach, hoTen, soCCCD, sdt, trangThai, isDeleted)
VALUES
('KT01', N'Lê Minh',        '079000123456', '0900000001', 'ACTIVE',   0),
('KT02', N'Nguyễn Văn An',  '079111111111', '0901111111', 'ACTIVE',   0),
('KT03', N'Trần Thị Bình',  '079222222222', '0902222222', 'ACTIVE',   0),
('KT04', N'Lê Minh Cường',  '079333333333', '0903333333', 'INACTIVE', 0),
('KT05', N'Phạm Quốc Dũng', '079444444444', '0904444444', 'ACTIVE',   0),
('KT06', N'Hoàng Thị Em',   '079555555555', '0905555555', 'ACTIVE',   0),
('KT07', N'Võ Thanh Phong', '079666666666', '0906666666', 'ACTIVE',   0),
('KT08', N'Đặng Quốc Huy',  '079777777777', '0907777777', 'ACTIVE',   0);
GO

-- ===== DỊCH VỤ =====
INSERT INTO DichVu (maDV, tenDV, donVi, loaiDichVu, isDeleted)
VALUES
('DV01', N'Điện',  N'kWh',   'CHI_SO',  0),
('DV02', N'Nước',  N'm3',    'CHI_SO',  0),
('DV03', N'Wifi',  N'Tháng', 'CO_DINH', 0),
('DV04', N'Rác',   N'Tháng', 'CO_DINH', 0);
GO

-- ===== BẢNG GIÁ =====
INSERT INTO BangGia (maBG, ngayHieuLuc, ngayKetThuc, trangThai, isDeleted)
VALUES
('BG01', '2026-01-01', NULL, 'DANG_AP_DUNG', 0);
GO

INSERT INTO DonGiaPhong (maBG, maLoaiPhong, giaTheoThang)
VALUES
('BG01', 'LP01', 5000000),
('BG01', 'LP02', 3000000);
GO

INSERT INTO DonGiaDichVu (maBG, maDV, gia)
VALUES
('BG01', 'DV01', 3500),
('BG01', 'DV02', 15000),
('BG01', 'DV03', 100000),
('BG01', 'DV04', 50000);
GO

-- ===== HỢP ĐỒNG =====
INSERT INTO HopDong (maHopDong, maPhong, maKhachChinh, ngayBatDau, ngayKetThuc, tienDatCoc, trangThai, isDeleted)
VALUES
('HD01', 'a1.01', 'KT01', '2026-01-01', '2027-01-01', 5000000, 'HIEU_LUC', 0),
('HD02', 'a1.02', 'KT02', '2026-01-01', '2026-12-31', 3000000, 'HIEU_LUC', 0),
('HD03', 'a1.03', 'KT03', '2026-02-01', '2026-12-31', 2000000, 'HIEU_LUC', 0),
('HD04', 'b1.01', 'KT05', '2026-03-01', '2027-03-01', 2500000, 'HIEU_LUC', 0);
GO

-- ===== CHỈ SỐ / CHI PHÍ =====
INSERT INTO ChiPhi (maCS, maPhong, ky, dien_cu, dien_moi, nuoc_cu, nuoc_moi, ngayGhi)
VALUES
('CS01', 'a1.01', '03/2026', 1000, 1150, 50, 58, GETDATE()),
('CS02', 'a1.02', '03/2026', 100, 150, 10, 15, GETDATE()),
('CS03', 'a1.03', '03/2026', 200, 260, 20, 28, GETDATE());
GO

-- ===== HÓA ĐƠN =====
INSERT INTO HoaDon (maHoaDon, maPhong, maTK_NguoiLap, ky, ngayLap, trangThai, isDeleted)
VALUES
('INV01', 'a1.01', 'TK01', '03/2026', GETDATE(), 'CHO_THANH_TOAN', 0),
('INV02', 'a1.02', 'TK01', '03/2026', GETDATE(), 'DA_THANH_TOAN', 0);
GO

-- ===== CHI TIẾT HÓA ĐƠN =====
INSERT INTO ChiTietHoaDon (maCT, maHoaDon, tenNoiDung, soLuong, donGiaSnapshot)
VALUES
('CT01', 'INV01', N'Tiền phòng', 1, 5000000),
('CT02', 'INV01', N'Tiền điện', 150, 3500),
('CT03', 'INV01', N'Tiền nước', 8, 15000),
('CT04', 'INV02', N'Tiền phòng', 1, 5000000);
GO

-- ===== NHẬT KÝ HỆ THỐNG =====
INSERT INTO NhatKyHeThong (maHoaDon, maTK, hanhDong)
VALUES
('INV01', 'TK01', N'Tạo hóa đơn tháng 03/2026');
GO

-- =============================================
-- 5. QUERY TEST
-- =============================================

SELECT
    k.maKhach,
    k.hoTen,
    k.soCCCD,
    k.sdt,
    CASE
        WHEN EXISTS (
            SELECT 1
            FROM HopDong h
            WHERE h.maKhachChinh = k.maKhach
              AND h.trangThai = 'HIEU_LUC'
              AND h.isDeleted = 0
        ) THEN N'Đang thuê'
        ELSE N'Đã trả phòng'
    END AS trangThai
FROM KhachThue k
WHERE k.isDeleted = 0
ORDER BY k.maKhach;
GO