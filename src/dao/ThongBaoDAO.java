package dao;

import entity.ThongBao;
import java.util.List;

public interface ThongBaoDAO {
    
    void insert(ThongBao obj);

    void update(ThongBao obj);

    void delete(String id);

    ThongBao findById(String id);

    List<ThongBao> findAll();
}
