package com.digios.slowmoserver.websocketserver;

import com.digios.slowmoserver.PhotoMakerAlgoritm2;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
    final static Logger logger = Logger.getLogger(DataBase.class);

    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/slowmo_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private Connection con;

    public DataBase() {
    }

    private static Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            return dbConnection;
        } catch (SQLException e) {
            logger.error(e);
        }
        return dbConnection;
    }

    public static void addFile(String fileName) {
        Connection dbConnection = null;
        Statement statement = null;

        String sql = "INSERT INTO slowmo_files (file_path) " +
                " VALUES"
                + "('" + fileName + "'); ";

        try {
            dbConnection = getDBConnection();
            statement = dbConnection.createStatement();
            statement.execute(sql);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (dbConnection != null) {
                try {
                    dbConnection.close();
                }
                catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }

    public static void addUserInfo(String fileName, String email) {
        Connection dbConnection = null;
        Statement statement = null;

        String sql = "INSERT INTO slowmo_user_data (file_path, email) " +
                " VALUES"
                + "('" + fileName + "', '" + email + "'); ";

        try {
            dbConnection = getDBConnection();
            statement = dbConnection.createStatement();
            statement.execute(sql);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (dbConnection != null) {
                try {
                    dbConnection.close();
                }
                catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }
}