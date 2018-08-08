package com.digios.slowmoserver.websocketserver;

import com.digios.slowmoserver.Config;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    final static Logger logger = Logger.getLogger(DataBase.class);

    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_CONNECTION = Config.INSTANCE.dbConnection();//"jdbc:mysql://localhost:3306/slowmo_db?autoReconnect=true&useSSL=false";
    private static final String DB_USER = Config.INSTANCE.dbUser();//"root";
    private static final String DB_PASSWORD = Config.INSTANCE.dbPassword();//"123456";

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

    public static List<String> getFiles() {
        Connection dbConnection = null;
        Statement statement = null;

        List<String> files = new ArrayList<>();

        String sql = "SELECT * FROM slowmo_files " +
                " WHERE DATE(date_time) = CURDATE()" +
                " ORDER BY date_time";

        try {
            dbConnection = getDBConnection();
            statement = dbConnection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                files.add(rs.getString("file_path"));
            }
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

        return files;
    }
}
