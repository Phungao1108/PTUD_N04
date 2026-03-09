package dao;

import entity.ChiTietHoaDon;
import java.util.List;

public interface ChiTietHoaDonDAO {
    
    void insert(ChiTietHoaDon obj);

    void update(ChiTietHoaDon obj);

    void delete(String id);

    ChiTietHoaDon findById(String id);

    List<ChiTietHoaDon> findAll();
}
