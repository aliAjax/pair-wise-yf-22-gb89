package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.constants.StatusLabels;
import com.generated.qualityTrace.models.RetainedSample;
import com.generated.qualityTrace.models.SampleDisposition;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 留样响应 DTO 构造器。追溯查询必须显示样品编号、柜位、状态与历次处置，
 * 因此该工厂同时装配 dispositionHistory。
 */
public final class RetainedSampleDtoFactory {

  public static Map<String, Object> create(RetainedSample s, List<SampleDisposition> history) {
    List<Map<String, Object>> historyDto =
        history.stream().map(SampleDispositionDtoFactory::create).toList();

    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", s.id);
    dto.put("sampleCode", s.sampleCode);
    dto.put("batchNo", s.batchNo);
    dto.put("locationCode", s.locationCode);
    dto.put("retentionExpiry", s.retentionExpiry);
    dto.put("registeredAt", s.registeredAt);
    dto.put("registeredBy", s.registeredBy);
    dto.put("status", s.status);
    dto.put("statusLabel", StatusLabels.sampleStatus(s.status));
    dto.put("destroyedAt", s.destroyedAt);
    dto.put("destroyedBy", s.destroyedBy);
    dto.put("dispositionHistory", historyDto);
    return dto;
  }

  private RetainedSampleDtoFactory() {}
}
