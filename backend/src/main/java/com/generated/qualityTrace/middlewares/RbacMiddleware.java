package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.UserRole;
import com.generated.qualityTrace.utils.Formatters;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * RBAC 中间件。销毁登记仅质量经理（QUALITY_MANAGER）可执行：
 * 角色经 X-User-Role 请求头传入（与 JWT 解析后的角色同源）。
 */
@Component
public class RbacMiddleware implements HandlerInterceptor {

  private static final Logger log = LoggerFactory.getLogger(RbacMiddleware.class);

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler) throws IOException {
    String role = request.getHeader("X-User-Role");
    if (role == null || role.isBlank()) {
      log.warn("rbac blocked: missing role, path={}", request.getRequestURI());
      writeError(response, 401, ErrorCodes.AUTH_REQUIRED, ErrorMessages.AUTH_REQUIRED);
      return false;
    }
    if (!UserRole.QUALITY_MANAGER.name().equals(role)) {
      log.warn("rbac blocked: role={}, path={}", role, request.getRequestURI());
      writeError(response, 403, ErrorCodes.RBAC_DENIED,
          ErrorMessages.RBAC_DENIED + ": QUALITY_MANAGER required");
      return false;
    }
    return true;
  }

  private void writeError(HttpServletResponse response, int status, String code,
                          String message) throws IOException {
    response.setStatus(status);
    response.setContentType("application/json;charset=UTF-8");
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", code);
    body.put("message", message);
    body.put("timestamp", Formatters.now());
    response.getWriter().write(new com.fasterxml.jackson.databind.ObjectMapper()
        .writeValueAsString(body));
  }
}
