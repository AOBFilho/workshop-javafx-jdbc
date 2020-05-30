package model.dao.impl;

import db.DBException;
import db.DBIntegrityException;
import db.Db;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoJdbc implements DepartmentDao {

    private Connection conn;

    public DepartmentDaoJdbc(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Department department) {
        String sql = "INSERT INTO department (Name) VALUES (?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1,department.getName());
            Db.beginTransaction();
            int linesAffected = preparedStatement.executeUpdate();
            Db.commitTransaction();
            if (linesAffected > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    department.setId(resultSet.getInt(1));
                }
            } else {
                throw new SQLException("No row affected!");
            }
        } catch (SQLException e) {
            Db.rollbackTransaction();
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void update(Department department) {
        String sql = "UPDATE department SET Name = ? WHERE Id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1,department.getName());
            preparedStatement.setInt(2,department.getId());
            Db.beginTransaction();
            int rowsAffected = preparedStatement.executeUpdate();
            Db.commitTransaction();
            if (rowsAffected <= 0) {
                throw new SQLException("No row affected!");
            }
        } catch (SQLException e) {
            Db.rollbackTransaction();
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM department WHERE Id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            Db.beginTransaction();
            preparedStatement.executeUpdate();
            Db.commitTransaction();
        } catch (SQLIntegrityConstraintViolationException e) {
            Db.rollbackTransaction();
            throw new DBIntegrityException(e.getMessage());
        } catch (SQLException e) {
            Db.rollbackTransaction();
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public Department findById(int id) {
        Department department = null;
        String sql = "SELECT Id,Name FROM department WHERE Id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                department = new Department(resultSet.getInt("Id"),resultSet.getString("Name"));
            }
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
        return department;
    }

    @Override
    public List<Department> findAll() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT Id,Name FROM department ORDER BY Name";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                departments.add(new Department(resultSet.getInt("Id"), resultSet.getString("Name")));
            }
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
        return departments;
    }
}
