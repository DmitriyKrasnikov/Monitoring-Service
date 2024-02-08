package service.audit;

import model.audit.ActionType;
import model.audit.AuditLog;

import java.util.List;

public interface AuditService {
    void recordAction(int userId, ActionType actionType, String actionDescription);

    List<AuditLog> getUserActions(int userId);
}
