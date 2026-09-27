package com.generated.qualityTrace.models;

/**
 * 留样历次处置记录：REGISTERED（留样登记）/ DESTROYED（到期销毁）。
 * 追溯查询按发生时间倒序返回，作为封存样处置链证据。
 */
public class SampleDisposition {
  public Long id;
  public String sampleCode;
  public String action;
  public String operatorId;
  public String occurredAt;
  public String remark;
}
