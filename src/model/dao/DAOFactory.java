package model.dao;

import db.Db;
import model.dao.impl.DepartmentDaoJdbc;
import model.dao.impl.SellerDaoJdbc;

public class DAOFactory {

    public static SellerDao createSellerDAO() {
        return new SellerDaoJdbc(Db.getConnection());
    }

    public static DepartmentDao createDepartmentDAO() {
        return new DepartmentDaoJdbc(Db.getConnection());
    }
}
