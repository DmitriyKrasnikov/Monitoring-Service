package dao.readings;

import annotations.Loggable;
import dao.mapper.MeterReadingMapper;
import model.readings.MeterReadings;
import org.slf4j.Logger;
import utils.DBConnectionManager;
import utils.LoggerConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Loggable
public class ReadingsRepositoryImpl implements ReadingsRepository {
    private static final Logger logger = LoggerConfig.getLogger();
    private final MeterReadingMapper meterReadingMapper = new MeterReadingMapper();

    private PreparedStatement prepareStatement(Connection connection, String sql, Object... parameters) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] instanceof String) {
                preparedStatement.setString(i + 1, (String) parameters[i]);
            } else if (parameters[i] instanceof Integer) {
                preparedStatement.setInt(i + 1, (Integer) parameters[i]);
            } else {
                throw new IllegalArgumentException("Неизвестный тип параметра: " + parameters[i].getClass());
            }
        }
        return preparedStatement;
    }

    @Override
    public Boolean addNewReadings(Integer userId, MeterReadings meterReadings) {
        String sql = """
            INSERT INTO meter_readings (user_id, meter_type_id, reading, month)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection connection = DBConnectionManager.getConnection()) {
            connection.setAutoCommit(false);

            for (Map.Entry<String, Integer> entry : meterReadings.getReadings().entrySet()) {
                String meterType = entry.getKey();
                Integer reading = entry.getValue();

                Integer meterTypeId = getMeterTypeId(meterType, connection);

                if (meterTypeId == null) {
                    logger.error("Неизвестный тип счетчика: " + meterType);
                    return false;
                }

                try (PreparedStatement preparedStatement = prepareStatement(connection, sql, userId, meterTypeId, reading,
                        meterReadings.getMonth().getValue())) {
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        logger.info("Показания счетчиков успешно добавлены.");
                    } else {
                        logger.warn("Не удалось добавить показания счетчиков.");
                        return false;
                    }
                } catch (SQLException e) {
                    logger.error("Ошибка при добавлении показаний счетчиков: " + e.getMessage());
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении показаний счетчиков: " + e.getMessage());
            return false;
        }
        return true;
    }

    private Integer getMeterTypeId(String meterType, Connection connection) {
        String sql = "SELECT meter_type_id FROM meter_types WHERE meter_type = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, meterType);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("meter_type_id");
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении meter_type_id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Optional<MeterReadings> getCurrentReadings(Integer userId) {
        String sql = """
            SELECT
                meter_readings.meter_type_id,
                meter_types.meter_type,
                meter_readings.reading,
                meter_readings.month
            FROM
                meter_readings
            JOIN
                meter_types ON meter_readings.meter_type_id = meter_types.meter_type_id
            WHERE
                meter_readings.user_id = ?
            ORDER BY
                meter_readings.month ASC
            """;

        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = prepareStatement(connection, sql, userId);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            MeterReadings meterReadings = meterReadingMapper.map(resultSet);
            return Optional.ofNullable(meterReadings);

        } catch (SQLException e) {
            logger.error("Ошибка при получении текущих показаний: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Optional<MeterReadings> getReadingsForMonth(Integer userId, Month month) {
        String sql = """
            SELECT
                meter_readings.meter_type_id,
                meter_types.meter_type,
                meter_readings.reading,
                meter_readings.month
            FROM
                meter_readings
            JOIN
                meter_types ON meter_readings.meter_type_id = meter_types.meter_type_id
            WHERE
                meter_readings.user_id = ? AND meter_readings.month = ?
            """;

        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = prepareStatement(connection, sql, userId, month.getValue());
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return Optional.of(meterReadingMapper.map(resultSet));
            }

        } catch (SQLException e) {
            logger.error("Ошибка при получении показаний за месяц: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Collection<MeterReadings> getReadingsHistory(Integer userId) {
        String sql = """
                SELECT mt.meter_type, mr.reading, mr.month
                FROM meter_readings mr
                JOIN meter_types mt ON mr.meter_type_id = mt.meter_type_id
                WHERE mr.user_id = ?
                ORDER BY mr.month
                """;

        Map<Month, Map<String, Integer>> readingsHistoryMap = new HashMap<>();

        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = prepareStatement(connection, sql, userId);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String meterType = resultSet.getString("meter_type");

                Month month = Month.of(resultSet.getInt("month"));
                readingsHistoryMap.computeIfAbsent(month, k -> new HashMap<>()).put(meterType,
                        resultSet.getInt("reading"));
            }

        } catch (SQLException e) {
            logger.error("Ошибка при получении истории показаний: " + e.getMessage());
        }

        return readingsHistoryMap.entrySet().stream()
                .map(entry -> new MeterReadings(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, MeterReadings> getAllCurrentReadings() {
        String sql = """
                SELECT u.username, mt.meter_type, mr.reading, mr.month
                FROM users u
                JOIN meter_readings mr ON u.user_id = mr.user_id
                JOIN meter_types mt ON mr.meter_type_id = mt.meter_type_id
                WHERE mr.month = (SELECT MAX(month) FROM meter_readings WHERE user_id = u.user_id)
                """;

        Map<String, Map<String, Integer>> allCurrentReadingsMap = new HashMap<>();
        Map<String, Month> monthMap = new HashMap<>();

        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = prepareStatement(connection, sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String meterType = resultSet.getString("meter_type");

                Month month = Month.of(resultSet.getInt("month"));
                allCurrentReadingsMap.computeIfAbsent(username, k -> new HashMap<>()).put(meterType,
                        resultSet.getInt("reading"));
                monthMap.put(username, month);
            }

        } catch (SQLException e) {
            logger.error("Ошибка при получении текущих показаний всех пользователей: " + e.getMessage());
        }

        return allCurrentReadingsMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new MeterReadings(entry.getValue(),
                        monthMap.get(entry.getKey()))));
    }

    @Override
    public List<String> getAllMeterTypes() {
        List<String> meterTypes = new ArrayList<>();
        String sql = "SELECT meter_type FROM meter_types";
        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                meterTypes.add(resultSet.getString("meter_type"));
            }

        } catch (SQLException e) {
            logger.error("Ошибка при получении всех типов счетчиков: " + e.getMessage());
        }
        return meterTypes;
    }
}