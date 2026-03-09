package dao;

import entity.Phong;
import java.util.List;

public interface PhongDAO {
    
    void insert(Phong obj);

    void update(Phong obj);

    void delete(String id);

    Phong findById(String id);

    List<Phong> findAll();
}
