package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.AuditLog;
import java.util.LinkedHashMap;
import java.util.Map;

/** 操作日志响应 DTO 构造器。 */
public final class AuditLogDtoFactory {

  public static Map<String, Object> create(AuditLog l) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", l.id);
    dto.put("actor", l.actor);
    dto.put("action", l.action);
    dto.put("targetType", l.targetType);
    dto.put("targetId", l.targetId);
    dto.put("createdAt", l.createdAt);
    return dto;
  }

  private AuditLogDtoFactory() {}
}
