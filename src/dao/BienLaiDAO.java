package dao;

import entity.BienLai;
import java.util.List;

public interface BienLaiDAO {
    
    void insert(BienLai obj);

    void update(BienLai obj);

    void delete(String id);

    BienLai findById(String id);

    List<BienLai> findAll();
}
