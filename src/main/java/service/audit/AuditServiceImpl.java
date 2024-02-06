package service.audit;

import dao.audit.AuditStorage;
import model.audit.ActionType;
import model.audit.AuditLog;
import utils.ServiceFactory;

import java.time.LocalDateTime;
import java.util.List;

public class AuditServiceImpl implements AuditService {
    private final AuditStorage auditStorage = ServiceFactory.getAuditStorage();

    @Override
    public void recordAction(int userId, ActionType actionType, String actionDescription) {
        AuditLog auditLog = new AuditLog(userId, actionType, LocalDateTime.now(), actionDescription);
        auditStorage.recordAction(auditLog);
    }

    @Override
    public List<AuditLog> getUserActions(int userId) {
        return auditStorage.getUserActions(userId);
    }
}