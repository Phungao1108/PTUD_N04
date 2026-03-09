package dao;

import entity.ThanhToan;
import java.util.List;

public interface ThanhToanDAO {
    
    void insert(ThanhToan obj);

    void update(ThanhToan obj);

    void delete(String id);

    ThanhToan findById(String id);

    List<ThanhToan> findAll();
}
