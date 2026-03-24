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

CREATE TABLE LoaiPhong (
    maLoaiPhong VARCHAR(50) PRIMARY KEY,
    tenLoaiPhong NVARCHAR(100) NOT NULL,
    dienTichChuan DECIMAL(10, 2),
    mota NVARCHAR(255),
    isDeleted BIT DEFAULT 0
);

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

CREATE TABLE DichVu (
    maDV VARCHAR(50) PRIMARY KEY,
    tenDV NVARCHAR(100) NOT NULL,
    donVi NVARCHAR(20),
    loaiDichVu VARCHAR(20) CHECK (loaiDichVu IN ('CO_DINH', 'CHI_SO')),
    isDeleted BIT DEFAULT 0
);

CREATE TABLE BangGia (
    maBG VARCHAR(50) PRIMARY KEY,
    ngayHieuLuc DATE NOT NULL,
    ngayKetThuc DATE NULL,
    trangThai VARCHAR(20) DEFAULT 'DANG_AP_DUNG',
    isDeleted BIT DEFAULT 0
);

CREATE TABLE DonGiaPhong (
    maBG VARCHAR(50),
    maLoaiPhong VARCHAR(50),
    giaTheoThang DECIMAL(18, 2) NOT NULL,
    PRIMARY KEY (maBG, maLoaiPhong),
    FOREIGN KEY (maBG) REFERENCES BangGia(maBG),
    FOREIGN KEY (maLoaiPhong) REFERENCES LoaiPhong(maLoaiPhong)
);

CREATE TABLE DonGiaDichVu (
    maBG VARCHAR(50),
    maDV VARCHAR(50),
    gia DECIMAL(18, 2) NOT NULL,
    PRIMARY KEY (maBG, maDV),
    FOREIGN KEY (maBG) REFERENCES BangGia(maBG),
    FOREIGN KEY (maDV) REFERENCES DichVu(maDV)
);

CREATE TABLE KhachThue (
    maKhach VARCHAR(50) PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    soCCCD VARCHAR(20) UNIQUE NOT NULL,
    sdt VARCHAR(15),
    trangThai VARCHAR(20) DEFAULT 'ACTIVE',
    isDeleted BIT DEFAULT 0
);

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

CREATE TABLE ChiTietHoaDon (
    maCT VARCHAR(50) PRIMARY KEY,
    maHoaDon VARCHAR(50) NOT NULL,
    tenNoiDung NVARCHAR(255),
    soLuong DECIMAL(10, 2) DEFAULT 1,
    donGiaSnapshot DECIMAL(18, 2) NOT NULL,
    thanhTien AS (soLuong * donGiaSnapshot), 
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon)
);

CREATE TABLE NhatKyHeThong (
    maLog INT IDENTITY(1,1) PRIMARY KEY,
    maHoaDon VARCHAR(50),
    maTK VARCHAR(50),
    thoiGian DATETIME DEFAULT CURRENT_TIMESTAMP,
    hanhDong NVARCHAR(255),
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
    FOREIGN KEY (maTK) REFERENCES TaiKhoan(maTK)
);

-- =============================================
-- 2. INDEXES & VIEWS
-- =============================================

CREATE INDEX IX_Phong_TrangThai ON CoSoVatChat(trangThaiPhong) WHERE loai = 'PHONG';
CREATE INDEX IX_KhachThue_CCCD ON KhachThue(soCCCD);
CREATE INDEX IX_HoaDon_Ky ON HoaDon(ky);

GO
CREATE VIEW v_HoaDonTong Hop AS
SELECT 
    h.maHoaDon, h.maPhong, h.ky, h.ngayLap, h.trangThai,
    ISNULL(SUM(ct.thanhTien), 0) AS TongTienThanhToan
FROM HoaDon h
LEFT JOIN ChiTietHoaDon ct ON h.maHoaDon = ct.maHoaDon
WHERE h.isDeleted = 0
GROUP BY h.maHoaDon, h.maPhong, h.ky, h.ngayLap, h.trangThai;

-- =============================================
-- 3. TRIGGERS
-- =============================================

GO
CREATE TRIGGER trg_ValidateRoomID
ON CoSoVatChat
AFTER INSERT, UPDATE
AS
BEGIN
    IF EXISTS (
        SELECT 1 FROM inserted i JOIN CoSoVatChat cha ON i.idCha = cha.id
        WHERE i.loai = 'PHONG' AND i.id NOT LIKE cha.id + '.%'
    )
    BEGIN
        RAISERROR (N'Mã Phòng sai định dạng [MãTầng].[SốPhòng]', 16, 1);
        ROLLBACK TRANSACTION;
    END
END;

-- =============================================
-- 4. DỮ LIỆU MẪU (SEED DATA)
-- =============================================

INSERT INTO TaiKhoan VALUES ('TK01', 'admin', 'pass', 'ADMIN', 'ACTIVE', 0);
INSERT INTO LoaiPhong VALUES ('LP01', N'Phòng VIP', 30.0, N'Nội thất cao cấp', 0);

INSERT INTO CoSoVatChat (id, ten, loai, idCha) VALUES ('a', N'Khu A', 'KHU_VUC', NULL);
INSERT INTO CoSoVatChat (id, ten, loai, idCha) VALUES ('a1', N'Tầng 1', 'TANG', 'a');
INSERT INTO CoSoVatChat (id, ten, loai, idCha, maLoaiPhong) VALUES ('a1.01', N'Phòng 101', 'PHONG', 'a1', 'LP01');

INSERT INTO KhachThue (maKhach, hoTen, soCCCD, trangThai) VALUES ('KT01', N'Lê Minh', '079000123456', 'ACTIVE');
INSERT INTO HopDong VALUES ('HD01', 'a1.01', 'KT01', '2026-01-01', '2027-01-01', 5000000, 'HIEU_LUC', 0);

INSERT INTO ChiPhi (maCS, maPhong, ky, dien_cu, dien_moi, nuoc_cu, nuoc_moi) 
VALUES ('CS01', 'a1.01', '03/2026', 1000, 1150, 50, 58);

INSERT INTO HoaDon (maHoaDon, maPhong, maTK_NguoiLap, ky) VALUES ('INV01', 'a1.01', 'TK01', '03/2026');
INSERT INTO ChiTietHoaDon VALUES ('CT01', 'INV01', N'Tiền nhà', 1, 5000000);
INSERT INTO ChiTietHoaDon VALUES ('CT02', 'INV01', N'Tiền điện (150 số)', 150, 3500);