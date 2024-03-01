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

/**
 * Реализация сервиса показаний счетчиков.
 * Этот класс реализует интерфейс MeterService и предоставляет методы для получения текущих показаний, отправки текущих показаний, получения показаний за месяц, получения истории показаний, получения всех текущих показаний и валидации показаний.
 *
 * @Loggable Аннотация, указывающая, что вызовы методов этого класса должны быть залогированы.
 */
@Loggable
public class MeterServiceImpl implements MeterService {
    private final ReadingsRepository readingsRepository = ServiceFactory.getReadingsStorage();
    private final AuditService auditService = ServiceFactory.getAuditService();

    /**
     * Получает текущие показания счетчиков пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return MeterReadings Объект MeterReadings, содержащий текущие показания счетчиков.
     */
    @Override
    public MeterReadings getCurrentReadings(Integer userId) {
        return readingsRepository.getCurrentReadings(userId).get();
    }

    /**
     * Отправляет текущие показания счетчиков пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @param readings Объект MeterReadings, содержащий текущие показания счетчиков.
     * @return String Сообщение о результате операции.
     */
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

    /**
     * Получает показания счетчиков пользователя за указанный месяц.
     *
     * @param userId Идентификатор пользователя.
     * @param month Месяц.
     * @return MeterReadings Объект MeterReadings, содержащий показания счетчиков за указанный месяц.
     */
    @Override
    public MeterReadings getReadingsForMonth(Integer userId, Month month) {
        Optional<MeterReadings> optionalReadings = readingsRepository.getReadingsForMonth(userId, month);
        return optionalReadings.orElse(null);
    }

    /**
     * Получает историю показаний счетчиков пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return Collection<MeterReadings> Коллекция объектов MeterReadings, каждый из которых содержит показания счетчиков за определенный месяц.
     */
    @Override
    public Collection<MeterReadings> getReadingHistory(Integer userId) {
        auditService.recordAction(userId, ActionType.VIEW_READING_HISTORY, "User viewed the history of readings");
        return readingsRepository.getReadingsHistory(userId);
    }

    /**
     * Получает все текущие показания счетчиков всех пользователей.
     *
     * @return Map<String, MeterReadings> Карта, где ключ - имя пользователя, а значение - объект MeterReadings, содержащий текущие показания счетчиков.
     */
    @Override
    public Map<String, MeterReadings> getAllCurrentReadings() {
        return readingsRepository.getAllCurrentReadings();
    }

    /**
     * Проверяет, являются ли показания счетчиков действительными.
     *
     * @param r Объект MeterReadings, содержащий показания счетчиков.
     * @return boolean Возвращает true, если показания действительны, иначе false.
     */
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