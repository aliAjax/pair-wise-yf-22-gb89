package com.generated.qualityTrace.constants;

import java.util.Map;

/** 列表筛选 / 详情展示共用的状态中文文案。 */
public final class StatusLabels {
  public static final Map<String, String> BATCH_STATUS_LABELS = Map.of(
      "IN_PROGRESS", "在制待检",
      "PENDING_HANDLING", "待处理",
      "RELEASED", "已放行");

  public static final Map<String, String> SAMPLE_STATUS_LABELS = Map.of(
      "RETAINED", "封存中",
      "DESTROYED", "已销毁");

  public static final Map<String, String> DISPOSITION_ACTION_LABELS = Map.of(
      "REGISTERED", "留样登记",
      "DESTROYED", "到期销毁");

  public static String batchStatus(String status) {
    return BATCH_STATUS_LABELS.getOrDefault(status, status);
  }

  public static String sampleStatus(String status) {
    return SAMPLE_STATUS_LABELS.getOrDefault(status, status);
  }

  public static String dispositionAction(String action) {
    return DISPOSITION_ACTION_LABELS.getOrDefault(action, action);
  }

  private StatusLabels() {}
}
