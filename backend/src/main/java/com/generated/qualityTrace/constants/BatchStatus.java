package com.generated.qualityTrace.constants;

/**
 * 产品批次状态：
 * IN_PROGRESS 在制待检；PENDING_HANDLING 待处理（终检不合格，或留样登记被柜位/到期日拦截）；
 * RELEASED 已放行（终检合格且留样登记成功）。
 */
public enum BatchStatus {
  IN_PROGRESS,
  PENDING_HANDLING,
  RELEASED
}
