package dao.audit;

import model.audit.ActionType;
import model.audit.AuditLog;
import org.slf4j.Logger;
import utils.DBConnectionManager;
import utils.LoggerConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditStorageImpl implements AuditStorage {
    private static final Logger logger = LoggerConfig.getLogger();

    private PreparedStatement prepareStatement(String sql, Object... parameters) throws SQLException {
        Connection connection = DBConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] instanceof String) {
                preparedStatement.setString(i + 1, (String) parameters[i]);
            } else if (parameters[i] instanceof Integer) {
                preparedStatement.setInt(i + 1, (Integer) parameters[i]);
            } else if (parameters[i] instanceof LocalDateTime) {
                preparedStatement.setTimestamp(i + 1, Timestamp.valueOf((LocalDateTime) parameters[i]));
            } else {
                throw new IllegalArgumentException("Неизвестный тип параметра: " + parameters[i].getClass());
            }
        }
        return preparedStatement;
    }

    @Override
    public void recordAction(AuditLog auditLog) {
        String sql = "INSERT INTO audit_schema.audit_logs (user_id, action_type_id, action_time, related_data) " +
                "VALUES (?, ?, ?, ?)";

        int actionTypeId = switch (auditLog.getActionType()) {
            case LOGIN -> 1;
            case LOGOUT -> 2;
            case SUBMIT_READING -> 3;
            case VIEW_READING_HISTORY -> 4;
            case REGISTER -> 5;
        };

        try (PreparedStatement preparedStatement = prepareStatement(sql, auditLog.getUserId(), actionTypeId,
                auditLog.getActionTime(), auditLog.getActionDescription())) {

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Действие пользователя успешно записано.");
            } else {
                logger.warn("Не удалось записать действие пользователя.");
            }
        } catch (SQLException e) {
            logger.error("Ошибка при записи действия пользователя: " + e.getMessage());
        }
    }

    @Override
    public List<AuditLog> getUserActions(int userId) {
        String sql = "SELECT action_type_id, action_time, related_data FROM audit_schema.audit_logs WHERE user_id = ?" +
                " ORDER BY action_time DESC";

        List<AuditLog> auditLogs = new ArrayList<>();

        try (PreparedStatement preparedStatement = prepareStatement(sql, userId);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                ActionType actionType = switch (resultSet.getInt("action_type_id")) {
                    case 1 -> ActionType.LOGIN;
                    case 2 -> ActionType.LOGOUT;
                    case 3 -> ActionType.SUBMIT_READING;
                    case 4 -> ActionType.VIEW_READING_HISTORY;
                    case 5 -> ActionType.REGISTER;
                    default -> throw new IllegalArgumentException("Неизвестный тип действия: " +
                            resultSet.getInt("action_type_id"));
                };

                LocalDateTime actionTime = resultSet.getTimestamp("action_time").toLocalDateTime();
                String actionDescription = resultSet.getString("related_data");

                auditLogs.add(new AuditLog(userId, actionType, actionTime, actionDescription));
            }

        } catch (SQLException e) {
            logger.error("Ошибка при получении действий пользователя: " + e.getMessage());
        }

        return auditLogs;
    }
}