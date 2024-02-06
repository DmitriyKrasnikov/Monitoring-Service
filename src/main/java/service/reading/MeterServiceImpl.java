package service.reading;

import dao.readings.ReadingsStorage;
import model.audit.ActionType;
import model.readings.MeterReadings;
import model.readings.MeterType;
import service.audit.AuditService;
import utils.ServiceFactory;

import java.time.Month;
import java.util.Collection;
import java.util.Map;

public class MeterServiceImpl implements MeterService {
    private final ReadingsStorage readingsStorage = ServiceFactory.getReadingsStorage();
    private final AuditService auditService = ServiceFactory.getAuditService();

    @Override
    public MeterReadings getCurrentReadings(Integer userId) {
        return readingsStorage.getCurrentReadings(userId);
    }

    @Override
    public String postCurrentReadings(Integer userId, MeterReadings readings) {
        if (readingsStorage.hasReadings(userId, readings.getMonth())) {
            return "The readings for this month has already been submitted";
        }
        if (!validateReadings(readings)) {
            return "Incorrect readings";
        }
        readingsStorage.addNewReadings(userId, readings);
        auditService.recordAction(userId, ActionType.SUBMIT_READING, "User submitted readings");
        return "Readings added";
    }

    @Override
    public MeterReadings getReadingsForMonth(Integer userId, Month month) {
        return readingsStorage.getReadingsForMonth(userId, month);
    }

    @Override
    public Collection<MeterReadings> getReadingHistory(Integer userId) {
        auditService.recordAction(userId, ActionType.VIEW_READING_HISTORY, "User viewed the history of readings");
        return readingsStorage.getReadingsHistory(userId);
    }

    @Override
    public Map<String, MeterReadings> getAllCurrentReadings() {
        return readingsStorage.getAllCurrentReadings();
    }

    @Override
    public boolean validateReadings(MeterReadings r) {
        if (r == null || r.getMonth() == null) {
            return false;
        }
        for (MeterType meterType : MeterType.values()) {
            Integer reading = r.getReadings().get(meterType);
            if (reading == null || reading <= 0) {
                return false;
            }
        }
        return true;
    }
}