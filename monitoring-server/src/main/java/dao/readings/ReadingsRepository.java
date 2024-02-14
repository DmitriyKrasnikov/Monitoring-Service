package dao.readings;

import model.readings.MeterReadings;

import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReadingsRepository {
    Optional<MeterReadings> getCurrentReadings(Integer userId);

    Boolean addNewReadings(Integer userId, MeterReadings meterReadings);

    Optional<MeterReadings> getReadingsForMonth(Integer userId, Month month);

    Collection<MeterReadings> getReadingsHistory(Integer userId);

    Map<String, MeterReadings> getAllCurrentReadings();

    List<String> getAllMeterTypes();
}
