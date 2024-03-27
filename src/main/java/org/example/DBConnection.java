package org.example;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                AppConfig config = new AppConfig();
                String jdbcUrl = config.getProperty("jdbcUrl");
                String username = config.getProperty("username");
                String password = config.getProperty("password");

                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(jdbcUrl, username, password);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Ошибка при подключении к базе данных", e);
            }
        }
        return connection;
    }
}
