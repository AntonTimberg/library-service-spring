package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() {
        if ("test".equals(System.getProperty("env"))) {
            String jdbcUrl = System.getProperty("test.jdbc.url");
            String username = System.getProperty("test.jdbc.username");
            String password = System.getProperty("test.jdbc.password");
            try {
                return DriverManager.getConnection(jdbcUrl, username, password);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                AppConfig config = new AppConfig();
                String jdbcUrl = config.getProperty("jdbcUrl");
                String username = config.getProperty("username");
                String password = config.getProperty("password");

                Class.forName("org.postgresql.Driver");
                return DriverManager.getConnection(jdbcUrl, username, password);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Ошибка при подключении к базе данных", e);
            }
        }
    }
}

