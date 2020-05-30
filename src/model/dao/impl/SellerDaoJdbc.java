package model.dao.impl;

import db.DBException;
import db.DBIntegrityException;
import db.Db;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJdbc implements SellerDao {

    private Connection conn;

    public SellerDaoJdbc(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Seller seller) {
        String sql = "INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1,seller.getName());
            preparedStatement.setString(2,seller.getEmail());
            preparedStatement.setDate(3,java.sql.Date.valueOf(seller.getBirthDate()));
            preparedStatement.setDouble(4,seller.getBaseSalary());
            preparedStatement.setInt(5,seller.getDepartment().getId());
            Db.beginTransaction();
            int rowsAffected = preparedStatement.executeUpdate();
            Db.commitTransaction();
            if (rowsAffected > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    seller.setId(resultSet.getInt(1));
                }
            } else {
                throw new SQLException("No row affected");
            }
        } catch (SQLException e) {
            Db.rollbackTransaction();
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void update(Seller seller) {
        String sql = "UPDATE seller SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? WHERE Id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1,seller.getName());
            preparedStatement.setString(2,seller.getEmail());
            preparedStatement.setDate(3,java.sql.Date.valueOf(seller.getBirthDate()));
            preparedStatement.setDouble(4,seller.getBaseSalary());
            preparedStatement.setInt(5,seller.getDepartment().getId());
            preparedStatement.setInt(6,seller.getId());
            Db.beginTransaction();
            int rowsAffected = preparedStatement.executeUpdate();
            Db.commitTransaction();
            if (rowsAffected == 0) {
                throw new SQLException("No row affected");
            }
        } catch (SQLException e) {
            Db.rollbackTransaction();
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM seller WHERE Id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1,id);
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
    public Seller findById(int id) {
        var sql = "SELECT "
            +"seller.Id, seller.Name, seller.Email, seller.BirthDate, seller.BaseSalary, "
            +"seller.DepartmentId, department.Name DepartmentName "
            +"FROM seller "
            +"INNER JOIN department ON department.Id = seller.DepartmentId "
            +"WHERE seller.Id = ? ";

        Seller seller = null;
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                seller = instantiateSeller(resultSet,instantiateDepartment(resultSet));
            }
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
        return seller;
    }

    @Override
    public List<Seller> findAll() {
        var sql = "SELECT "
            +"seller.Id, seller.Name, seller.Email, seller.BirthDate, seller.BaseSalary, "
            +"seller.DepartmentId, department.Name DepartmentName "
            +"FROM seller "
            +"INNER JOIN department ON department.Id = seller.DepartmentId "
            +"ORDER BY seller.Name";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            List<Seller> sellers = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            Map<Integer,Department> departmentMap = new HashMap<>();
            while (resultSet.next()) {
                Integer departmentId = resultSet.getInt("DepartmentId");
                Department department;
                if (!departmentMap.containsKey(departmentId)) {
                    department = instantiateDepartment(resultSet);
                    departmentMap.put(departmentId,department);
                } else {
                    department = departmentMap.get(departmentId);
                }
                Seller seller = instantiateSeller(resultSet,department);
                sellers.add(seller);
            }
            return sellers;
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public List<Seller> findByDepartment(Integer departmentId) {
        var sql = "SELECT "
            +"seller.Id, seller.Name, seller.Email, seller.BirthDate, seller.BaseSalary, "
            +"seller.DepartmentId, department.Name DepartmentName "
            +"FROM seller "
            +"INNER JOIN department ON department.Id = seller.DepartmentId "
            +"WHERE seller.DepartmentId = ? "
            +"ORDER BY seller.Name";


        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1,departmentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Seller> sellers = new ArrayList<>();
            Department department = null;
            while (resultSet.next()) {
                if (department == null) {
                    department = instantiateDepartment(resultSet);
                }
                Seller seller = instantiateSeller(resultSet,department);
                sellers.add(seller);
            }
            return sellers;
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    private Seller instantiateSeller(ResultSet resultSet, Department department) throws SQLException {
        Seller seller = new Seller();
        seller.setId(resultSet.getInt("Id"));
        seller.setName(resultSet.getString("Name"));
        seller.setEmail(resultSet.getString("Email"));
        seller.setBirthDate(resultSet.getDate("BirthDate").toLocalDate());
        seller.setBaseSalary(resultSet.getDouble("BaseSalary"));
        seller.setDepartment(department);
        return seller;
    }

    private Department instantiateDepartment(ResultSet resultSet) throws SQLException {
        Department department = new Department();
        department.setId(resultSet.getInt("DepartmentId"));
        department.setName(resultSet.getString("DepartmentName"));
        return department;
    }
}
