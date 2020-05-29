package model.services;

import model.entities.Department;

import java.util.List;

public class DepartmentService {

    public List<Department> findAll() {
        var departments = List.of(new Department(1,"Books"),
                new Department(2,"Smartphones"), new Department(3,"Computers"));
        return departments;
    }
}
