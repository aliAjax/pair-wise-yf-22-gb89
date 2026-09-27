package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.InspectionItemResult;
import java.util.LinkedHashMap;
import java.util.Map;

/** 检验项结果响应 DTO 构造器。 */
public final class InspectionItemResultDtoFactory {

  public static Map<String, Object> create(InspectionItemResult r) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", r.id);
    dto.put("inspectionId", r.inspectionId);
    dto.put("itemCode", r.itemCode);
    dto.put("itemName", r.itemName);
    dto.put("measuredValue", r.measuredValue);
    dto.put("limitMin", r.limitMin);
    dto.put("limitMax", r.limitMax);
    dto.put("itemStatus", r.itemStatus);
    return dto;
  }

  private InspectionItemResultDtoFactory() {}
}
