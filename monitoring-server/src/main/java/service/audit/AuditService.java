package service.audit;

import annotations.Loggable;
import model.audit.ActionType;
import model.audit.AuditLog;

import java.util.List;
@Loggable
public interface AuditService {
    void recordAction(int userId, ActionType actionType, String actionDescription);

    List<AuditLog> getUserActions(int userId);
}
