package dao;

import entity.HopDongNguoiThue;
import java.util.List;

public interface HopDongNguoiThueDAO {
    
    void insert(HopDongNguoiThue obj);

    void update(HopDongNguoiThue obj);

    void delete(String id);

    HopDongNguoiThue findById(String id);

    List<HopDongNguoiThue> findAll();
}
