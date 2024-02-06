package dao.user;

import org.slf4j.Logger;
import utils.DBConnectionManager;
import utils.LoggerConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс UserStorageImpl представляет собой реализацию интерфейса UserStorage.
 * Этот класс использует Map для хранения зарегистрированных пользователей.
 */
public class UserStorageImpl implements UserStorage {
    private static final Logger logger = LoggerConfig.getLogger();

    private PreparedStatement prepareStatement(String sql, String... parameters) throws SQLException {
        Connection connection = DBConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < parameters.length; i++) {
            preparedStatement.setString(i + 1, parameters[i]);
        }
        return preparedStatement;
    }

    @Override
    public void addNewUser(String username, String email, String password) {
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = prepareStatement(sql, username, email, password)) {

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
    public boolean isRegister(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (PreparedStatement preparedStatement = prepareStatement(sql, email);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }

        } catch (SQLException e) {
            logger.error("Ошибка при проверке регистрации пользователя: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Integer getUserIdFromEmail(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?";

        try (PreparedStatement preparedStatement = prepareStatement(sql, email);
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
    public Integer getUserIdFromName(String name) {
        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (PreparedStatement preparedStatement = prepareStatement(sql, name);
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
    public boolean isAdmin(String email) {
        String sql = "SELECT is_admin FROM users WHERE email = ?";

        try (PreparedStatement preparedStatement = prepareStatement(sql, email);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getBoolean("is_admin");
            }

        } catch (SQLException e) {
            logger.error("Ошибка при проверке статуса администратора: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean validateUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (PreparedStatement preparedStatement = prepareStatement(sql, email, password);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            return resultSet.next();

        } catch (SQLException e) {
            logger.error("Ошибка валидации пользователя: " + e.getMessage());
        }
        return false;
    }
}