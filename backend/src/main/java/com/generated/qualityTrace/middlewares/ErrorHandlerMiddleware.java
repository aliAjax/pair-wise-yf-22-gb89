package com.generated.qualityTrace.middlewares;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
public class ErrorHandlerMiddleware {
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Map<String,String>> conflict(IllegalStateException e) { return build(HttpStatus.CONFLICT, e.getMessage()); }
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String,String>> badRequest(IllegalArgumentException e) { return build(HttpStatus.BAD_REQUEST, e.getMessage()); }
  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<Map<String,String>> forbidden(SecurityException e) { return build(HttpStatus.FORBIDDEN, e.getMessage()); }
  private static ResponseEntity<Map<String,String>> build(HttpStatus status, String raw) {
    String[] parts = raw==null ? new String[]{"ERROR"} : raw.split(":", 2);
    return ResponseEntity.status(status).body(Map.of("code", parts[0], "message", parts.length>1 ? parts[1] : ""));
  }
}
