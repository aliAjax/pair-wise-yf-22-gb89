package com.generated.qualityTrace.repositories;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.QualityInspection;
@Repository
public class QualityInspectionRepository {
  private final List<QualityInspection> store = new ArrayList<>();
  private final AtomicLong seq = new AtomicLong(100);
  public List<Map<String,Object>> findAll(){ return List.of(Map.of("id",1,"name","质量检验","status","READY")); }
  public QualityInspection save(QualityInspection qi) { if (qi.id==null) qi.id=seq.incrementAndGet(); store.add(qi); return qi; }
}
