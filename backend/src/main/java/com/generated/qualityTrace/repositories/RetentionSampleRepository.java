package com.generated.qualityTrace.repositories;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.constants.RetentionSampleStatus;
import com.generated.qualityTrace.models.RetentionSample;
@Repository
public class RetentionSampleRepository {
  private final Map<Long, RetentionSample> store = new LinkedHashMap<>();
  private final AtomicLong seq = new AtomicLong(100);
  public RetentionSampleRepository() {
    save(new RetentionSample(1L, "RS-2026-0001", 2L, "A-01", "2026-12-31", RetentionSampleStatus.STORED.name()));
  }
  public List<RetentionSample> findAll() { return new ArrayList<>(store.values()); }
  public Optional<RetentionSample> findById(Long id) { return Optional.ofNullable(store.get(id)); }
  public Optional<RetentionSample> findByBatchId(Long batchId) { return store.values().stream().filter(s -> Objects.equals(s.batchId, batchId)).findFirst(); }
  public List<RetentionSample> findAllByBatchId(Long batchId) {
    List<RetentionSample> out = new ArrayList<>();
    for (RetentionSample s : store.values()) if (Objects.equals(s.batchId, batchId)) out.add(s);
    return out;
  }
  public boolean cabinetOccupied(String cabinetSlot) {
    return store.values().stream().anyMatch(s -> Objects.equals(s.cabinetSlot, cabinetSlot) && RetentionSampleStatus.STORED.name().equals(s.status));
  }
  public RetentionSample save(RetentionSample s) { if (s.id==null) s.id=seq.incrementAndGet(); store.put(s.id, s); return s; }
}
