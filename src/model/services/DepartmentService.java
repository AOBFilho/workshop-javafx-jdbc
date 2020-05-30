package model.services;

import model.dao.DAOFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.util.List;

public class DepartmentService {

    private DepartmentDao departmentDao = DAOFactory.createDepartmentDAO();

    public List<Department> findAll() {
        return departmentDao.findAll();
    }
}
