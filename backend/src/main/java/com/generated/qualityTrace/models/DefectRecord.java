package com.generated.qualityTrace.models;

/** 不良记录。处置状态 disposition_status 会在追溯树中呈现。 */
public class DefectRecord {
  public Long id;
  public Long batchId;
  public String defectType;
  public Integer defectQty;
  public String severity;
  public String rootCause;
  public String dispositionStatus;
}
