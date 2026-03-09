package dao;

import entity.TaiKhoan;
import java.util.List;

public interface TaiKhoanDAO {
    
    void insert(TaiKhoan obj);

    void update(TaiKhoan obj);

    void delete(String id);

    TaiKhoan findById(String id);

    List<TaiKhoan> findAll();
}
