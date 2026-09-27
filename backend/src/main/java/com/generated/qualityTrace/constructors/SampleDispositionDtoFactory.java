package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.constants.StatusLabels;
import com.generated.qualityTrace.models.SampleDisposition;
import java.util.LinkedHashMap;
import java.util.Map;

/** 留样历次处置记录 DTO 构造器，追溯查询与留样详情共用。 */
public final class SampleDispositionDtoFactory {

  public static Map<String, Object> create(SampleDisposition d) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", d.id);
    dto.put("sampleCode", d.sampleCode);
    dto.put("action", d.action);
    dto.put("actionLabel", StatusLabels.dispositionAction(d.action));
    dto.put("operatorId", d.operatorId);
    dto.put("occurredAt", d.occurredAt);
    dto.put("remark", d.remark);
    return dto;
  }

  private SampleDispositionDtoFactory() {}
}
