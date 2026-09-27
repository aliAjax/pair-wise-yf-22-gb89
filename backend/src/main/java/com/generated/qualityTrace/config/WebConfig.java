package com.generated.qualityTrace.config;

import com.generated.qualityTrace.middlewares.AuditLogMiddleware;
import com.generated.qualityTrace.middlewares.RbacMiddleware;
import com.generated.qualityTrace.routes.RetainedSampleRoutes;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** 中间件挂载：销毁登记走 RBAC（仅质量经理）；写操作走审计日志。 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final RbacMiddleware rbacMiddleware;
  private final AuditLogMiddleware auditLogMiddleware;

  public WebConfig(RbacMiddleware rbacMiddleware, AuditLogMiddleware auditLogMiddleware) {
    this.rbacMiddleware = rbacMiddleware;
    this.auditLogMiddleware = auditLogMiddleware;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(rbacMiddleware)
        .addPathPatterns(RetainedSampleRoutes.DISPOSE);

    registry.addInterceptor(auditLogMiddleware)
        .addPathPatterns("/api/**");
  }
}
