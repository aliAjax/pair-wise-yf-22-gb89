package com.generated.qualityTrace.models;

/** 操作日志 / 追溯事件日志，由 service 写操作时经 AuditLogService 落库。 */
public class AuditLog {
  public Long id;
  public String actor;
  public String action;
  public String targetType;
  public String targetId;
  public String createdAt;
}
