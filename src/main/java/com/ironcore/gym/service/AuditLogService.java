package com.ironcore.gym.service;

import com.ironcore.gym.entity.AuditLog;
import com.ironcore.gym.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String action, String entityName, String entityId, String performedBy, String details) {
        try {
            AuditLog log = new AuditLog(action, entityName, entityId,
                    (performedBy != null ? performedBy : "SYSTEM"), details);
            auditLogRepository.save(log);
        } catch (Exception e) {
            // Non-blocking for audit logs
        }
    }

    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop15ByOrderByTimestampDesc();
    }

    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable);
    }
}
