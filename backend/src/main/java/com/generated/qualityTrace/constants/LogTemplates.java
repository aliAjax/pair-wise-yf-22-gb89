package com.generated.qualityTrace.constants;

/**
 * 操作日志 / 追溯事件日志模板。留样并入放行流程后，每个实体至少 4 条模板，
 * 写操作（终检提交、留样登记、销毁登记）均经 AuditLogService 落库。
 */
public final class LogTemplates {
  // 通用
  public static final String CREATE = "create";
  public static final String UPDATE = "update";
  public static final String STATUS = "status";
  public static final String EXPORT = "export";

  // 产品批次（含放行流程）
  public static final String BATCH_CREATE = "批次创建: batchNo={0}";
  public static final String BATCH_STATUS_CHANGE = "批次状态变更: batchNo={0}, {1} -> {2}";
  public static final String BATCH_HOLD = "批次待处理: batchNo={0}, 原因={1}";
  public static final String BATCH_RELEASE = "批次放行: batchNo={0}, 样品编号={1}";

  // 质量检验
  public static final String INSPECTION_CREATE = "检验提交: batchNo={0}, 类型={1}";
  public static final String FINAL_INSPECTION_PASS = "终检合格: batchNo={0}";
  public static final String FINAL_INSPECTION_FAIL = "终检不合格: batchNo={0}, 批次置待处理";
  public static final String INSPECTION_ITEM_RESULT = "检验项判定: itemCode={0}, 结果={1}";

  // 留样
  public static final String SAMPLE_REGISTER =
      "留样登记: 样品编号={0}, batchNo={1}, 柜位={2}, 到期日={3}";
  public static final String SAMPLE_REGISTER_BLOCKED =
      "留样登记拦截: batchNo={0}, 原因={1}, 批次保留待处理";
  public static final String SAMPLE_DESTROY = "留样销毁登记: 样品编号={0}, 柜位={1}, 操作人={2}";
  public static final String SAMPLE_DESTROY_BLOCKED = "留样销毁拦截: 样品编号={0}, 原因={1}";

  private LogTemplates() {}
}
