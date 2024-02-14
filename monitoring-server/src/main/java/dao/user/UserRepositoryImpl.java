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
 * Класс UserRepositoryImpl представляет собой реализацию интерфейса UserRepository.
 * Этот класс использует Map для хранения зарегистрированных пользователей.
 */
@Loggable
public class UserRepositoryImpl implements UserRepository {
    private static final Logger logger = LoggerConfig.getLogger();
    private final UserMapper userMapper = new UserMapper();

    private PreparedStatement prepareStatement(Connection connection, String sql, String... parameters) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < parameters.length; i++) {
            preparedStatement.setString(i + 1, parameters[i]);
        }
        return preparedStatement;
    }

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