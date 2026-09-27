package com.generated.qualityTrace.middlewares;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.generated.qualityTrace.services.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * 审计日志中间件：记录所有写操作（POST/PUT/PATCH/DELETE）的访问事件；
 * 领域级操作日志（放行、留样登记、销毁）仍由 service 经 AuditLogService 记录。
 */
@Component
public class AuditLogMiddleware implements HandlerInterceptor {

  private static final Logger log = LoggerFactory.getLogger(AuditLogMiddleware.class);

  private final AuditLogService auditLogService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public AuditLogMiddleware(AuditLogService auditLogService) {
    this.auditLogService = auditLogService;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                              Object handler, Exception ex) {
    String method = request.getMethod();
    if (!("POST".equals(method) || "PUT".equals(method)
        || "PATCH".equals(method) || "DELETE".equals(method))) {
      return;
    }
    String actor = request.getHeader("X-User-Id");
    if (actor == null || actor.isBlank()) {
      actor = "anonymous";
    }
    String action = method + " " + request.getRequestURI();
    auditLogService.record(actor, action, "HTTP", request.getRequestURI());
    log.info("audit: actor={}, action={}, status={}", actor, action, response.getStatus());

    // 被拦截器拒绝的请求（如 RBAC 403）额外写一条拒绝事件
    if (response.getStatus() >= 400 && response instanceof ContentCachingResponseWrapper) {
      log.warn("audit blocked request: {} -> {}", action, response.getStatus());
    }
  }

  /** 序列化辅助，保留给将来写拒绝请求体使用。 */
  @SuppressWarnings("unused")
  private String toJson(Map<String, Object> body) {
    try {
      return objectMapper.writeValueAsString(new LinkedHashMap<>(body));
    } catch (Exception e) {
      return "{}";
    }
  }
}
