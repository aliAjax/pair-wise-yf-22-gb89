package com.generated.qualityTrace.repositories;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.RetentionSampleEvent;
@Repository
public class RetentionSampleEventRepository {
  private final List<RetentionSampleEvent> store = new ArrayList<>();
  private final AtomicLong seq = new AtomicLong(100);
  public RetentionSampleEventRepository() {
    save(new RetentionSampleEvent(null, 1L, "REGISTER", "system", "seed retention sample", "2026-09-15T10:00:00"));
  }
  public List<RetentionSampleEvent> findBySampleId(Long sampleId) {
    List<RetentionSampleEvent> out = new ArrayList<>();
    for (RetentionSampleEvent e : store) if (Objects.equals(e.sampleId, sampleId)) out.add(e);
    return out;
  }
  public RetentionSampleEvent save(RetentionSampleEvent e) { if (e.id==null) e.id=seq.incrementAndGet(); store.add(e); return e; }
}
