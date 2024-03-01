package service.audit;

import annotations.Loggable;
import dao.audit.AuditRepository;
import model.audit.ActionType;
import model.audit.AuditLog;
import utils.ServiceFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация сервиса аудита.
 * Этот класс реализует интерфейс AuditService и предоставляет методы для записи действий пользователя и получения действий пользователя.
 *
 * @Loggable Аннотация, указывающая, что вызовы методов этого класса должны быть залогированы.
 */
@Loggable
public class AuditServiceImpl implements AuditService {
    private final AuditRepository auditRepository = ServiceFactory.getAuditStorage();

    /**
     * Записывает действие пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @param actionType Тип действия.
     * @param actionDescription Описание действия.
     */
    @Override
    public void recordAction(int userId, ActionType actionType, String actionDescription) {
        AuditLog auditLog = new AuditLog(userId, actionType, LocalDateTime.now(), actionDescription);
        auditRepository.recordAction(auditLog);
    }

    /**
     * Получает действия пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return List<AuditLog> Список действий пользователя.
     */
    @Override
    public List<AuditLog> getUserActions(int userId) {
        return auditRepository.getUserActions(userId);
    }
}