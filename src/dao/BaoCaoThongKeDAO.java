package dao;


import entity.BaoCaoThongKe;
import java.util.List;

public interface BaoCaoThongKeDAO {
    
    void insert(BaoCaoThongKe obj);

    void update(BaoCaoThongKe obj);

    void delete(String id);

    BaoCaoThongKe findById(String id);

    List<BaoCaoThongKe> findAll();
}
