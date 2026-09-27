package com.generated.qualityTrace.models;

/** 检验项结果，逐项录入后汇总为 QualityInspection 结论。 */
public class InspectionItemResult {
  public Long id;
  public Long inspectionId;
  public String itemCode;
  public String itemName;
  public String measuredValue;
  public String limitMin;
  public String limitMax;
  public String itemStatus;
}
