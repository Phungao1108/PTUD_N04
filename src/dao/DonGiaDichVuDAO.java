package dao;

import entity.DonGiaDichVu;
import java.util.List;

public interface DonGiaDichVuDAO {
    
    void insert(DonGiaDichVu obj);

    void update(DonGiaDichVu obj);

    void delete(String id);

    DonGiaDichVu findById(String id);

    List<DonGiaDichVu> findAll();
}
