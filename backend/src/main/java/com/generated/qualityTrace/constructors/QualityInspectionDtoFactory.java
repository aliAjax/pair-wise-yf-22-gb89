package com.generated.qualityTrace.constructors;
import java.util.*;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.models.RetentionSample;
public final class QualityInspectionDtoFactory {
  public static Map<String,Object> create(){ return Map.of("id",1,"name","质量检验"); }
  public static Map<String,Object> releaseResponse(QualityInspection qi, RetentionSample s, ProductBatch b) {
    Map<String,Object> m = new LinkedHashMap<>();
    m.put("inspectionId", qi.id); m.put("batchId", b.id); m.put("batchNo", b.batchNo==null?"":b.batchNo); m.put("batchStatus", b.status);
    m.put("sampleNo", s.sampleNo); m.put("cabinetSlot", s.cabinetSlot); m.put("retainUntil", s.retainUntil); m.put("sampleStatus", s.status);
    return m;
  }
}
