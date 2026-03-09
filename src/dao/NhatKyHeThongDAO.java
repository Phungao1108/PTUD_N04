package dao;

import entity.NhatKyHeThong;
import java.util.List;

public interface NhatKyHeThongDAO {
    
    void insert(NhatKyHeThong obj);

    void update(NhatKyHeThong obj);

    void delete(String id);

    NhatKyHeThong findById(String id);

    List<NhatKyHeThong> findAll();
}
