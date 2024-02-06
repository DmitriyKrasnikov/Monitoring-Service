package utils;

import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnectionManager {
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;

    private static final Logger logger = LoggerConfig.getLogger();

    static {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/db.changelog/liquibase.properties")) {
            prop.load(input);

            URL = prop.getProperty("url");
            USERNAME = prop.getProperty("username");
            PASSWORD = prop.getProperty("password");

        } catch (FileNotFoundException e) {
            logger.error("Не удалось найти файл свойств");
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error("Произошла ошибка при чтении файла свойств");
            logger.error(e.getMessage());
        }
    }

    public static void setConnectionDetails(String url, String username, String password) {
        URL = url;
        USERNAME = username;
        PASSWORD = password;
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            logger.error("Ошибка при подключении к базе данных: " + e.getMessage());
        }
        return connection;
    }
}