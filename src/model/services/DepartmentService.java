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

    public void insertOrUpdate(Department department) {
        if (department.getId() == null) {
            departmentDao.insert(department);
        } else {
            departmentDao.update(department);
        }
    }
}
