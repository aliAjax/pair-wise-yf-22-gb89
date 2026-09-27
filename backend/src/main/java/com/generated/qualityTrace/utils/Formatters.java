package com.generated.qualityTrace.utils;

import com.generated.qualityTrace.constants.StatusLabels;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 混合格式化工具：日期、状态文本、审计目标 ID 等，多个 service / 展示层共同依赖。
 * 修改日期或状态格式会牵一发动全身。
 */
public final class Formatters {

  private static final DateTimeFormatter DATE_TIME =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public static String audit(String type, long id) {
    return type + "#" + id;
  }

  public static String today() {
    return LocalDate.now().toString();
  }

  public static String now() {
    return LocalDateTime.now().format(DATE_TIME);
  }

  public static LocalDate parseDate(String value) {
    return LocalDate.parse(value);
  }

  public static String batchStatus(String status) {
    return StatusLabels.batchStatus(status);
  }

  public static String sampleStatus(String status) {
    return StatusLabels.sampleStatus(status);
  }

  private Formatters() {}
}
