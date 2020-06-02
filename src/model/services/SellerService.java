package model.services;

import model.dao.DAOFactory;
import model.dao.SellerDao;
import model.entities.Seller;

import java.util.List;

public class SellerService {

    private SellerDao sellerDao = DAOFactory.createSellerDAO();

    public List<Seller> findAll() {
        return sellerDao.findAll();
    }

    public void insertOrUpdate(Seller seller) {
        if (seller.getId() == null) {
            sellerDao.insert(seller);
        } else {
            sellerDao.update(seller);
        }
    }

    public void delete(Seller seller) {
        sellerDao.deleteById(seller.getId());
    }

}
