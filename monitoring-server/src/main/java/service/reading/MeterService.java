package service.reading;

import annotations.Loggable;
import model.readings.MeterReadings;

import java.time.Month;
import java.util.Collection;
import java.util.Map;
@Loggable
public interface MeterService {

    MeterReadings getCurrentReadings(Integer userId);

    String postCurrentReadings(Integer userId, MeterReadings readings);

    MeterReadings getReadingsForMonth(Integer userId, Month month);

    Collection<MeterReadings> getReadingHistory(Integer userId);

    Map<String, MeterReadings> getAllCurrentReadings();

    boolean validateReadings(MeterReadings r);
}
