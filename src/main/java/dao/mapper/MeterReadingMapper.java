package dao.mapper;

import model.readings.MeterReadings;
import model.readings.MeterType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

public class MeterReadingMapper {

    public MeterReadings map(ResultSet resultSet) throws SQLException {
        Map<MeterType, Integer> readings = new HashMap<>();
        Month month = null;

        while (resultSet.next()) {
            MeterType meterType = getMeterType(resultSet.getInt("meter_type_id"));

            readings.put(meterType, resultSet.getInt("reading"));
            month = Month.of(resultSet.getInt("month"));
        }

        return (readings.isEmpty() || month == null) ? null : new MeterReadings(readings, month);
    }

    public MeterType getMeterType(int meterTypeId) {
        return switch (meterTypeId) {
            case 1 -> MeterType.HEATING;
            case 2 -> MeterType.HOT_WATER;
            case 3 -> MeterType.COLD_WATER;
            default -> throw new IllegalArgumentException("Неизвестный тип счетчика: " + meterTypeId);
        };
    }
}