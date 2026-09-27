package com.generated.qualityTrace.models;

/** 质量检验（首检/巡检/终检）。终检 result_status 决定批次是否可放行。 */
public class QualityInspection {
  public Long id;
  public Long batchId;
  public String inspectorId;
  public String inspectionType;
  public String standardVersion;
  public String resultStatus;
  public String inspectedAt;
}
