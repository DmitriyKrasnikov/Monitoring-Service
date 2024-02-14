package dao.mapper;

import model.readings.MeterReadings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

public class MeterReadingMapper {

    public MeterReadings map(ResultSet resultSet) throws SQLException {
        Map<String, Integer> readings = new HashMap<>();
        Month month = null;

        while (resultSet.next()) {
            String meterType = resultSet.getString("meter_type");

            readings.put(meterType, resultSet.getInt("reading"));
            month = Month.of(resultSet.getInt("month"));
        }

        return (readings.isEmpty() || month == null) ? null : new MeterReadings(readings, month);
    }
}