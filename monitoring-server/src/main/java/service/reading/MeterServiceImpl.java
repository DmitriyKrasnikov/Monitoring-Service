package service.reading;

import annotations.Loggable;
import dao.readings.ReadingsRepository;
import model.audit.ActionType;
import model.readings.MeterReadings;
import service.audit.AuditService;
import utils.ServiceFactory;

import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Loggable
public class MeterServiceImpl implements MeterService {
    private final ReadingsRepository readingsRepository = ServiceFactory.getReadingsStorage();
    private final AuditService auditService = ServiceFactory.getAuditService();

    @Override
    public MeterReadings getCurrentReadings(Integer userId) {
        return readingsRepository.getCurrentReadings(userId).get();
    }

    @Override
    public String postCurrentReadings(Integer userId, MeterReadings readings) {
        Optional<MeterReadings> existingReadings = readingsRepository.getReadingsForMonth(userId, readings.getMonth());
        if (existingReadings.isPresent()) {
            return "The readings for this month have already been submitted";
        }
        if (!validateReadings(readings)) {
            return "Incorrect readings";
        }
        auditService.recordAction(userId, ActionType.SUBMIT_READING, "User submitted readings");
        return readingsRepository.addNewReadings(userId, readings)? "Readings added" : "Something was wrong";
    }

    @Override
    public MeterReadings getReadingsForMonth(Integer userId, Month month) {
        Optional<MeterReadings> optionalReadings = readingsRepository.getReadingsForMonth(userId, month);
        return optionalReadings.orElse(null);
    }

    @Override
    public Collection<MeterReadings> getReadingHistory(Integer userId) {
        auditService.recordAction(userId, ActionType.VIEW_READING_HISTORY, "User viewed the history of readings");
        return readingsRepository.getReadingsHistory(userId);
    }

    @Override
    public Map<String, MeterReadings> getAllCurrentReadings() {
        return readingsRepository.getAllCurrentReadings();
    }

    @Override
    public boolean validateReadings(MeterReadings r) {
        if (r == null || r.getMonth() == null) {
            return false;
        }
        List<String> allMeterTypes = readingsRepository.getAllMeterTypes();
        for (String meterType : allMeterTypes) {
            Integer reading = r.getReadings().get(meterType);
            if (reading == null || reading <= 0) {
                return false;
            }
        }
        return true;
    }
}