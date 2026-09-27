package com.generated.qualityTrace.constants;

/**
 * RBAC 角色：质检员 / 产线主管 / 质量经理 / 审计员。
 * 留样销毁仅 QUALITY_MANAGER 可登记（见 RbacMiddleware）。
 */
public enum UserRole {
  QUALITY_INSPECTOR,
  LINE_SUPERVISOR,
  QUALITY_MANAGER,
  AUDITOR
}
