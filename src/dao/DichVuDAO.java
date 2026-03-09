package dao;

import entity.DichVu;
import java.util.List;

public interface DichVuDAO {
    
    void insert(DichVu obj);

    void update(DichVu obj);

    void delete(String id);

    DichVu findById(String id);

    List<DichVu> findAll();
}
