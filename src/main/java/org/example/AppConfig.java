package org.example;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final String PROP_FILE = "/application.properties";
    private final Properties configProps = new Properties();

    public AppConfig() {
        try (InputStream inputStream = getClass().getResourceAsStream(PROP_FILE)) {

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Не удалось загрузить файл конфигурации " + PROP_FILE + ".");
        }
    }

    public String getProperty(String key) {
        return configProps.getProperty(key);
    }

    public int getIntProperty(String key) {
        return Integer.parseInt(configProps.getProperty(key));
    }
}
