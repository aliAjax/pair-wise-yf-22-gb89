package com.generated.qualityTrace.utils;

import com.generated.qualityTrace.constants.ErrorCodes;
import java.util.Map;

/** 业务规则异常：service / controller 分层包装，禁止在全局位置吞掉。 */
public class BusinessException extends RuntimeException {
  private final String code;
  private final int httpStatus;
  private Map<String, Object> details;

  public BusinessException(String code, String message, int httpStatus) {
    super(message);
    this.code = code;
    this.httpStatus = httpStatus;
  }

  public BusinessException withDetails(Map<String, Object> details) {
    this.details = details;
    return this;
  }

  public static BusinessException invalidPayload(String message) {
    return new BusinessException(ErrorCodes.INVALID_PAYLOAD, message, 400);
  }

  public static BusinessException notFound(String code, String message) {
    return new BusinessException(code, message, 404);
  }

  public static BusinessException conflict(String code, String message) {
    return new BusinessException(code, message, 409);
  }

  public static BusinessException unprocessable(String code, String message) {
    return new BusinessException(code, message, 422);
  }

  public String getCode() {
    return code;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

  public Map<String, Object> getDetails() {
    return details;
  }
}
