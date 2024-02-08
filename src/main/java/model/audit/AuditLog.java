package model.audit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    private int userId;
    private ActionType actionType;
    private LocalDateTime actionTime;
    private String actionDescription;
}