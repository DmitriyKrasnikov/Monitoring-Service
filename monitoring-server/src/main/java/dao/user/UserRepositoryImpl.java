package dao.user;

import annotations.Loggable;
import dao.mapper.UserMapper;
import model.user.User;
import org.slf4j.Logger;
import utils.DBConnectionManager;
import utils.LoggerConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Реализация репозитория пользователей.
 * Этот класс реализует интерфейс UserRepository и предоставляет методы для добавления нового пользователя,
 * поиска пользователя по электронной почте, получения идентификатора пользователя по имени и валидации пользователя.
 *
 * @Loggable Аннотация, указывающая, что вызовы методов этого класса должны быть залогированы.
 */
@Loggable
public class UserRepositoryImpl implements UserRepository {
    private static final Logger logger = LoggerConfig.getLogger();
    private final UserMapper userMapper = new UserMapper();

    /**
     * Подготавливает SQL-запрос для выполнения в базе данных.
     *
     * @param connection Объект Connection, представляющий соединение с базой данных.
     * @param sql SQL-запрос.
     * @param parameters Параметры SQL-запроса.
     * @return PreparedStatement Объект PreparedStatement, представляющий подготовленный SQL-запрос.
     * @throws SQLException В случае ошибки SQL.
     */
    private PreparedStatement prepareStatement(Connection connection, String sql, String... parameters) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < parameters.length; i++) {
            preparedStatement.setString(i + 1, parameters[i]);
        }
        return preparedStatement;
    }

    /**
     * Добавляет нового пользователя в базу данных.
     *
     * @param username Имя пользователя.
     * @param email Электронная почта пользователя.
     * @param password Пароль пользователя.
     * @param salt Соль для хеширования пароля.
     */
    @Override
    public void addNewUser(String username, String email, String password, String salt) {
        String sql = """
        INSERT INTO users (username, email, password, salt)
        VALUES (?, ?, ?, ?)
        """;

        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = prepareStatement(connection, sql, username, email, password, salt)) {

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Пользователь успешно добавлен.");
            } else {
                logger.warn("Не удалось добавить пользователя.");
            }
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении пользователя: " + e.getMessage());
        }
    }

    /**
     * Ищет пользователя по электронной почте в базе данных.
     *
     * @param email Электронная почта пользователя.
     * @return Optional<User> Объект User, содержащий данные пользователя, или пустой Optional, если пользователь не найден.
     */
    @Override
    public Optional<User> findByEmail(String email) {
        String sql = """
        SELECT user_id, username, email, password, salt, is_admin
        FROM users
        WHERE email = ?
        """;

        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = prepareStatement(connection, sql, email);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                User user = userMapper.map(resultSet);
                return Optional.ofNullable(user);
            }

        } catch (SQLException e) {
            logger.error("Ошибка при поиске пользователя: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Получает идентификатор пользователя по имени из базы данных.
     *
     * @param name Имя пользователя.
     * @return Integer Идентификатор пользователя.
     */
    @Override
    public Integer getUserIdFromName(String name) {
        String sql = """
        SELECT user_id
        FROM users
        WHERE username = ?
        """;

        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = prepareStatement(connection, sql, name);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            }

        } catch (SQLException e) {
            logger.error("Ошибка при получении ID пользователя: " + e.getMessage());
        }
        return null;
    }

    /**
     * Проверяет, существует ли пользователь с указанной электронной почтой и паролем в базе данных.
     *
     * @param email Электронная почта пользователя.
     * @param password Пароль пользователя.
     * @return boolean Возвращает true, если пользователь существует, иначе false.
     */
    @Override
    public boolean validateUser(String email, String password) {
        String sql = """
        SELECT *
        FROM users
        WHERE email = ? AND password = ?
        """;

        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = prepareStatement(connection, sql, email, password);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            return resultSet.next();

        } catch (SQLException e) {
            logger.error("Ошибка валидации пользователя: " + e.getMessage());
        }
        return false;
    }
}