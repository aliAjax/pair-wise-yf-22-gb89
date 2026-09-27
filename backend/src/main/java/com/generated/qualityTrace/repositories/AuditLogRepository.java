package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.models.AuditLog;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/** 操作日志 / 追溯事件日志数据访问，所有写操作均追加一条。 */
@Repository
public class AuditLogRepository {

  private final Map<Long, AuditLog> store = new LinkedHashMap<>();
  private final AtomicLong idSequence = new AtomicLong(1000);

  public synchronized AuditLog append(String actor, String action,
                                      String targetType, String targetId, String createdAt) {
    AuditLog log = new AuditLog();
    log.id = idSequence.incrementAndGet();
    log.actor = actor;
    log.action = action;
    log.targetType = targetType;
    log.targetId = targetId;
    log.createdAt = createdAt;
    store.put(log.id, log);
    return log;
  }

  public synchronized List<AuditLog> findAll() {
    return new ArrayList<>(store.values());
  }
}
