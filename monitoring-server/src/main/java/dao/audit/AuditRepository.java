package dao.audit;

import model.audit.AuditLog;

import java.util.List;

public interface AuditRepository {
    void recordAction(AuditLog auditLog);

    List<AuditLog> getUserActions(int userId);
}
