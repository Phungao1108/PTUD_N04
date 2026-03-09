package dao;

import entity.HoaDon;
import java.util.List;

public interface HoaDonDAO {
    
    void insert(HoaDon obj);

    void update(HoaDon obj);

    void delete(String id);

    HoaDon findById(String id);

    List<HoaDon> findAll();
}
