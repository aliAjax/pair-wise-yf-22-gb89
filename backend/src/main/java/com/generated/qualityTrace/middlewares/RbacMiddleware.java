package com.generated.qualityTrace.middlewares;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
public class RbacMiddleware {
  public static final String ROLE_QUALITY_MANAGER="QUALITY_MANAGER";
  public static void requireQualityManager(String role) {
    if (!ROLE_QUALITY_MANAGER.equals(role)) throw new SecurityException(ErrorCodes.RBAC_DENIED + ":" + ErrorMessages.RBAC_DENIED);
  }
}
