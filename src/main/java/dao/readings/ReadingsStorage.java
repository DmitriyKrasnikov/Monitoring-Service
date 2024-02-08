package dao.readings;

import model.readings.MeterReadings;

import java.time.Month;
import java.util.Collection;
import java.util.Map;

public interface ReadingsStorage {
    boolean hasReadings(Integer userId, Month month);

    MeterReadings getCurrentReadings(Integer userId);

    void addNewReadings(Integer userId, MeterReadings meterReadings);

    MeterReadings getReadingsForMonth(Integer userId, Month month);

    Collection<MeterReadings> getReadingsHistory(Integer userId);

    Map<String, MeterReadings> getAllCurrentReadings();
}
