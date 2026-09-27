package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.utils.Formatters;
import java.util.LinkedHashMap;
import java.util.Map;

/** 工单响应 DTO 构造器，service/controller 不得散写工单结构。 */
public final class WorkOrderDtoFactory {

  public static Map<String, Object> create(WorkOrder o) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", o.id);
    dto.put("orderNo", o.orderNo);
    dto.put("productCode", o.productCode);
    dto.put("productName", o.productName);
    dto.put("plannedQty", o.plannedQty);
    dto.put("lineCode", o.lineCode);
    dto.put("startAt", o.startAt);
    dto.put("status", o.status);
    return dto;
  }

  private WorkOrderDtoFactory() {}
}
