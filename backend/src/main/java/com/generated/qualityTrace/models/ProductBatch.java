package com.generated.qualityTrace.models;

/**
 * 产品批次。batch_status 取值见 constants/BatchStatus：
 * IN_PROGRESS / PENDING_HANDLING / RELEASED。
 */
public class ProductBatch {
  public Long id;
  public String batchNo;
  public Long workOrderId;
  public Integer quantity;
  public String materialLotNo;
  public String producedAt;
  public String batchStatus;
}
