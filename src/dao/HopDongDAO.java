package dao;

import entity.HopDong;
import java.util.List;

public interface HopDongDAO {
    
    void insert(HopDong obj);

    void update(HopDong obj);

    void delete(String id);

    HopDong findById(String id);

    List<HopDong> findAll();
}
