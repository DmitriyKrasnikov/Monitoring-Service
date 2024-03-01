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

/**
 * Реализация репозитория показаний счетчиков.
 * Этот класс реализует интерфейс ReadingsRepository и предоставляет методы для добавления новых показаний счетчиков.
 *
 * @Loggable Аннотация, указывающая, что вызовы методов этого класса должны быть залогированы.
 */
@Loggable
public class ReadingsRepositoryImpl implements ReadingsRepository {
    private static final Logger logger = LoggerConfig.getLogger();
    private final MeterReadingMapper meterReadingMapper = new MeterReadingMapper();

    /**
     * Подготавливает SQL-запрос для выполнения в базе данных.
     *
     * @param connection Объект Connection, представляющий соединение с базой данных.
     * @param sql SQL-запрос.
     * @param parameters Параметры SQL-запроса.
     * @return PreparedStatement Объект PreparedStatement, представляющий подготовленный SQL-запрос.
     * @throws SQLException В случае ошибки SQL.
     */
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

    /**
     * Добавляет новые показания счетчиков в базу данных.
     *
     * @param userId Идентификатор пользователя.
     * @param meterReadings Объект MeterReadings, содержащий показания счетчиков.
     * @return Boolean Возвращает true, если показания успешно добавлены, иначе false.
     */
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

    /**
     * Получает идентификатор типа счетчика из базы данных.
     *
     * @param meterType Тип счетчика.
     * @param connection Объект Connection, представляющий соединение с базой данных.
     * @return Integer Идентификатор типа счетчика.
     */
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

    /**
     * Получает текущие показания счетчиков пользователя из базы данных.
     *
     * @param userId Идентификатор пользователя.
     * @return Optional<MeterReadings> Объект MeterReadings, содержащий текущие показания счетчиков, или пустой Optional, если показания отсутствуют.
     */
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

    /**
     * Получает показания счетчиков пользователя за указанный месяц из базы данных.
     *
     * @param userId Идентификатор пользователя.
     * @param month Месяц.
     * @return Optional<MeterReadings> Объект MeterReadings, содержащий показания счетчиков за указанный месяц, или пустой Optional, если показания отсутствуют.
     */
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

    /**
     * Получает историю показаний счетчиков пользователя из базы данных.
     *
     * @param userId Идентификатор пользователя.
     * @return Collection<MeterReadings> Коллекция объектов MeterReadings, каждый из которых содержит показания счетчиков за определенный месяц.
     */
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

    /**
     * Получает все текущие показания счетчиков всех пользователей из базы данных.
     *
     * @return Map<String, MeterReadings> Карта, где ключ - имя пользователя, а значение - объект MeterReadings, содержащий текущие показания счетчиков.
     */
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

    /**
     * Получает все типы счетчиков из базы данных.
     *
     * @return List<String> Список всех типов счетчиков.
     */
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