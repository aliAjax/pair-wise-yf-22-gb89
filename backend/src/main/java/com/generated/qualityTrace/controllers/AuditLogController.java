package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.routes.AuditLogRoutes;
import com.generated.qualityTrace.services.AuditLogService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 操作日志/追溯事件日志查询。 */
@RestController
@RequestMapping(AuditLogRoutes.PATH)
public class AuditLogController {

  private final AuditLogService service;

  public AuditLogController(AuditLogService service) {
    this.service = service;
  }

  @GetMapping
  public List<Map<String, Object>> list() {
    return service.list();
  }
}
