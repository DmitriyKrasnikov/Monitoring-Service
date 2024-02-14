package service.audit;

import annotations.Loggable;
import dao.audit.AuditRepository;
import model.audit.ActionType;
import model.audit.AuditLog;
import utils.ServiceFactory;

import java.time.LocalDateTime;
import java.util.List;

@Loggable
public class AuditServiceImpl implements AuditService {
    private final AuditRepository auditRepository = ServiceFactory.getAuditStorage();

    @Override
    public void recordAction(int userId, ActionType actionType, String actionDescription) {
        AuditLog auditLog = new AuditLog(userId, actionType, LocalDateTime.now(), actionDescription);
        auditRepository.recordAction(auditLog);
    }

    @Override
    public List<AuditLog> getUserActions(int userId) {
        return auditRepository.getUserActions(userId);
    }
}