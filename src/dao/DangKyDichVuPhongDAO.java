package dao;

import entity.DangKyDichVuPhong;
import java.util.List;

public interface DangKyDichVuPhongDAO {
    
    void insert(DangKyDichVuPhong obj);

    void update(DangKyDichVuPhong obj);

    void delete(String id);

    DangKyDichVuPhong findById(String id);

    List<DangKyDichVuPhong> findAll();
}
