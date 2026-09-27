package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constructors.AuditLogDtoFactory;
import com.generated.qualityTrace.repositories.AuditLogRepository;
import com.generated.qualityTrace.utils.Formatters;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/** 操作日志服务：所有放行/留样写操作经此统一落库。 */
@Service
public class AuditLogService {

  private final AuditLogRepository auditLogRepository;

  public AuditLogService(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  public void record(String actor, String action, String targetType, String targetId) {
    auditLogRepository.append(actor, action, targetType, targetId, Formatters.now());
  }

  public List<Map<String, Object>> list() {
    return auditLogRepository.findAll().stream().map(AuditLogDtoFactory::create).toList();
  }
}
