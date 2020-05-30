package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Db {

    private static Connection conn = null;

    public static Connection getConnection() {
        if (conn == null) {
            try {
                Properties props = loadProperties();
                String url = props.getProperty("urlDataBase");
                conn = DriverManager.getConnection(url, props);
            } catch (SQLException e) {
                throw new DBException(e.getMessage());
            }
        }
        return conn;
    }

    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new DBException(e.getMessage());
            }
        }
    }

    public static void beginTransaction() {
        if (conn != null) {
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                throw new DBException(e.getMessage());
            }
        }
    }

    public static void commitTransaction() {
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                throw new DBException(e.getMessage());
            }
        }
    }

    public static void rollbackTransaction() {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                throw new DBException(e.getMessage());
            }
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream("db.properties")) {
            props.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

}
