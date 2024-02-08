package dao.readings;

import dao.mapper.MeterReadingMapper;
import model.readings.MeterReadings;
import model.readings.MeterType;
import org.slf4j.Logger;
import utils.DBConnectionManager;
import utils.LoggerConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ReadingsStorageImpl implements ReadingsStorage {
    private static final Logger logger = LoggerConfig.getLogger();
    private final MeterReadingMapper meterReadingMapper = new MeterReadingMapper();

    private PreparedStatement prepareStatement(String sql, Object... parameters) throws SQLException {
        Connection connection = DBConnectionManager.getConnection();
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
    public void addNewReadings(Integer userId, MeterReadings meterReadings) {
        String sql = "INSERT INTO meter_readings (user_id, meter_type_id, reading, month) VALUES (?, ?, ?, ?)";

        for (Map.Entry<MeterType, Integer> entry : meterReadings.getReadings().entrySet()) {
            MeterType meterType = entry.getKey();
            Integer reading = entry.getValue();

            int meterTypeId = switch (meterType) {
                case HEATING -> 1;
                case HOT_WATER -> 2;
                case COLD_WATER -> 3;
                default -> throw new IllegalArgumentException("Неизвестный тип счетчика: " + meterType);
            };

            try (PreparedStatement preparedStatement = prepareStatement(sql, userId, meterTypeId, reading,
                    meterReadings.getMonth().getValue())) {
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    logger.info("Показания счетчиков успешно добавлены.");
                } else {
                    logger.warn("Не удалось добавить показания счетчиков.");
                }
            } catch (SQLException e) {
                logger.error("Ошибка при добавлении показаний счетчиков: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean hasReadings(Integer userId, Month month) {
        String sql = "SELECT COUNT(*) FROM meter_readings WHERE user_id = ? AND month = ?";

        try (PreparedStatement preparedStatement = prepareStatement(sql, userId, month.getValue())) {
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            logger.error("Ошибка при проверке показаний счетчиков: " + e.getMessage());
        }

        return false;
    }


    @Override
    public MeterReadings getCurrentReadings(Integer userId) {
        String sql = "SELECT meter_type_id, reading, month FROM meter_readings WHERE user_id = ? ORDER BY month ASC";

        try (PreparedStatement preparedStatement = prepareStatement(sql, userId);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            return meterReadingMapper.map(resultSet);

        } catch (SQLException e) {
            logger.error("Ошибка при получении текущих показаний: " + e.getMessage());
        }

        return null;
    }

    @Override
    public MeterReadings getReadingsForMonth(Integer userId, Month month) {
        String sql = "SELECT meter_type_id, reading,month FROM meter_readings WHERE user_id = ? AND month = ?";

        try (PreparedStatement preparedStatement = prepareStatement(sql, userId, month.getValue());
             ResultSet resultSet = preparedStatement.executeQuery()) {

            return meterReadingMapper.map(resultSet);

        } catch (SQLException e) {
            logger.error("Ошибка при получении показаний за месяц: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Collection<MeterReadings> getReadingsHistory(Integer userId) {
        String sql = "SELECT meter_type_id, reading, month FROM meter_readings WHERE user_id = ? ORDER BY month";

        Map<Month, Map<MeterType, Integer>> readingsHistoryMap = new HashMap<>();

        try (PreparedStatement preparedStatement = prepareStatement(sql, userId);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                MeterType meterType = meterReadingMapper.getMeterType(resultSet.getInt("meter_type_id"));

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
        String sql = "SELECT u.username, m.meter_type_id, m.reading, m.month FROM users u JOIN meter_readings m ON" +
                " u.user_id = m.user_id WHERE m.month = (SELECT MAX(month) FROM meter_readings WHERE user_id = u.user_id)";

        Map<String, Map<MeterType, Integer>> allCurrentReadingsMap = new HashMap<>();
        Map<String, Month> monthMap = new HashMap<>();

        try (PreparedStatement preparedStatement = prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                MeterType meterType = meterReadingMapper.getMeterType(resultSet.getInt("meter_type_id"));

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
}