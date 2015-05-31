package org.db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by HP on 29.05.2015.
 */
public class ConnectionManager {
    private static final String URL = "jdbc:mysql://localhost:3306/mlada_db_chat";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "3333333";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }
}
