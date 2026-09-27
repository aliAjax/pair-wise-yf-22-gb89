package com.generated.qualityTrace.models;

/**
 * 留存样品（留样）。留样随终检提交一起登记，登记成功后批次才放行。
 * 状态见 constants/RetentionSampleStatus：RETAINED（封存中，占用柜位）/ DESTROYED（已销毁，柜位释放）。
 */
public class RetainedSample {
  public Long id;
  public String sampleCode;
  public Long batchId;
  public String batchNo;
  public String locationCode;
  public String retentionExpiry;
  public String registeredAt;
  public String registeredBy;
  public String status;
  public String destroyedAt;
  public String destroyedBy;
}
