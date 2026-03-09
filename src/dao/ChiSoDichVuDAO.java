package dao;

import entity.ChiSoDichVu;
import java.util.List;

public interface ChiSoDichVuDAO {
    
    void insert(ChiSoDichVu obj);

    void update(ChiSoDichVu obj);

    void delete(String id);

    ChiSoDichVu findById(String id);

    List<ChiSoDichVu> findAll();
}
