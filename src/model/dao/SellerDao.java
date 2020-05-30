package model.dao;

import model.entities.Seller;

import java.util.List;

public interface SellerDao {

    void insert(Seller seller);
    void update(Seller seller);
    void deleteById(int id);
    Seller findById(int id);
    List<Seller> findAll();
    List<Seller> findByDepartment(Integer departmentId);

}
