package utils;

import annotations.Loggable;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Менеджер соединения с базой данных.
 * Он аннотирован @Loggable.
 */
@Loggable
public class DBConnectionManager {
    /**
     * URL базы данных.
     */
    private static String URL;

    /**
     * Имя пользователя базы данных.
     */
    private static String USERNAME;

    /**
     * Пароль базы данных.
     */
    private static String PASSWORD;

    /**
     * Логгер для записи сообщений об ошибках и другой информации.
     */
    private static final Logger logger = LoggerConfig.getLogger();

    static {
        Properties prop = new Properties();
        try (InputStream input = DBConnectionManager.class.getClassLoader().getResourceAsStream("db.changelog/liquibase.properties")) {
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

    /**
     * Устанавливает детали соединения.
     *
     * @param url      URL базы данных
     * @param username имя пользователя базы данных
     * @param password пароль базы данных
     */
    public static void setConnectionDetails(String url, String username, String password) {
        URL = url;
        USERNAME = username;
        PASSWORD = password;
    }

    /**
     * Получает соединение с базой данных.
     *
     * @return соединение с базой данных
     * @throws SQLException если произошла ошибка при подключении к базе данных
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver"); // Загрузка драйвера
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            logger.error("Ошибка при подключении к базе данных: " + e.getMessage());
            throw e; // Перебрасываем исключение
        } catch (ClassNotFoundException e) {
            logger.error("Ошибка при загрузке драйвера: " + e.getMessage());
            throw new SQLException(e);
        }

        if (connection == null) {
            logger.error("Соединение не было установлено. Проверьте URL, имя пользователя и пароль.");
            throw new SQLException("Соединение не было установлено");
        }

        return connection;
    }
}