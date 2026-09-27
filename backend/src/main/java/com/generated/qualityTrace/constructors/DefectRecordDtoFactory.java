package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.DefectRecord;
import java.util.LinkedHashMap;
import java.util.Map;

/** 不良记录响应 DTO 构造器。 */
public final class DefectRecordDtoFactory {

  public static Map<String, Object> create(DefectRecord d) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", d.id);
    dto.put("batchId", d.batchId);
    dto.put("defectType", d.defectType);
    dto.put("defectQty", d.defectQty);
    dto.put("severity", d.severity);
    dto.put("rootCause", d.rootCause);
    dto.put("dispositionStatus", d.dispositionStatus);
    return dto;
  }

  private DefectRecordDtoFactory() {}
}
