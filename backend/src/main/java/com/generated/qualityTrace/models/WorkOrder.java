package com.generated.qualityTrace.models;

/** 生产工单。status 取值见 constants/WorkOrderStatus。 */
public class WorkOrder {
  public Long id;
  public String orderNo;
  public String productCode;
  public String productName;
  public Integer plannedQty;
  public String lineCode;
  public String startAt;
  public String status;
}
