package dao;

import entity.KhachThue;
import java.util.List;

public interface KhachThueDAO {
    
    void insert(KhachThue obj);

    void update(KhachThue obj);

    void delete(String id);

    KhachThue findById(String id);

    List<KhachThue> findAll();
}
