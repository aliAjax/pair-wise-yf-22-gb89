package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.utils.BusinessException;
import com.generated.qualityTrace.utils.Formatters;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 全局异常处理中间件：service/controller 已分别包装，此处只做统一 JSON 响应。 */
@RestControllerAdvice
public class ErrorHandlerMiddleware {

  private static final Logger log = LoggerFactory.getLogger(ErrorHandlerMiddleware.class);

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
    log.warn("business exception: code={}, message={}", ex.getCode(), ex.getMessage());
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", ex.getCode());
    body.put("message", ex.getMessage());
    body.put("timestamp", Formatters.now());
    if (ex.getDetails() != null) {
      body.put("details", ex.getDetails());
    }
    return ResponseEntity.status(ex.getHttpStatus()).body(body);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex) {
    return badRequest(ErrorCodes.INVALID_PAYLOAD,
        String.format(ErrorMessages.INVALID_PAYLOAD, "malformed JSON body"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
    log.error("unexpected exception", ex);
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", "INTERNAL_ERROR");
    body.put("message", ex.getMessage());
    body.put("timestamp", Formatters.now());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }

  private ResponseEntity<Map<String, Object>> badRequest(String code, String message) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", code);
    body.put("message", message);
    body.put("timestamp", Formatters.now());
    return ResponseEntity.badRequest().body(body);
  }
}
