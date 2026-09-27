package com.generated.qualityTrace.constructors;
import java.util.*;
import com.generated.qualityTrace.models.RetentionSample;
import com.generated.qualityTrace.models.RetentionSampleEvent;
import com.generated.qualityTrace.utils.Formatters;
public final class RetentionSampleDtoFactory {
  public static Map<String,Object> create(){ return Map.of("id",1,"name","留样"); }
  public static Map<String,Object> toResponse(RetentionSample s) {
    Map<String,Object> m = new LinkedHashMap<>();
    m.put("id", s.id); m.put("sampleNo", s.sampleNo); m.put("batchId", s.batchId);
    m.put("cabinetSlot", s.cabinetSlot); m.put("cabinetLabel", Formatters.cabinetLabel(s.cabinetSlot));
    m.put("retainUntil", s.retainUntil); m.put("status", s.status);
    m.put("statusText", Formatters.retentionSampleStatusText(s.status));
    m.put("destroyedBy", s.destroyedBy==null?"":s.destroyedBy); m.put("destroyedAt", s.destroyedAt==null?"":s.destroyedAt);
    return m;
  }
  public static Map<String,Object> traceResponse(String batchNo, List<RetentionSample> samples, Map<Long,List<RetentionSampleEvent>> eventsBySample) {
    Map<String,Object> m = new LinkedHashMap<>();
    m.put("batchNo", batchNo);
    List<Map<String,Object>> list = new ArrayList<>();
    for (RetentionSample s : samples) {
      Map<String,Object> item = new LinkedHashMap<>(toResponse(s));
      List<Map<String,Object>> ev = new ArrayList<>();
      for (RetentionSampleEvent e : eventsBySample.getOrDefault(s.id, List.of()))
        ev.add(Map.of("action", e.action, "operator", e.operator, "detail", e.detail, "createdAt", e.createdAt));
      item.put("events", ev);
      list.add(item);
    }
    m.put("samples", list);
    return m;
  }
}
