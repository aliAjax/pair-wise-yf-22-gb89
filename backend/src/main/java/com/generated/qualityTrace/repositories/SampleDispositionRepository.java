package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.constants.DispositionAction;
import com.generated.qualityTrace.models.SampleDisposition;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/** 留样历次处置数据访问：登记（REGISTERED）与销毁（DESTROYED）均落库。 */
@Repository
public class SampleDispositionRepository {

  private final Map<Long, SampleDisposition> store = new LinkedHashMap<>();
  private final AtomicLong idSequence = new AtomicLong(100);

  public SampleDispositionRepository() {
    SampleDisposition seed = new SampleDisposition();
    seed.id = 1L;
    seed.sampleCode = "S-2026-0001";
    seed.action = DispositionAction.REGISTERED.name();
    seed.operatorId = "QA-01";
    seed.occurredAt = "2026-09-21 10:15:00";
    seed.remark = "随终检合格登记封存";
    store.put(seed.id, seed);

    SampleDisposition expiredSeed = new SampleDisposition();
    expiredSeed.id = 2L;
    expiredSeed.sampleCode = "S-2025-0088";
    expiredSeed.action = DispositionAction.REGISTERED.name();
    expiredSeed.operatorId = "QA-01";
    expiredSeed.occurredAt = "2025-09-26 09:00:00";
    expiredSeed.remark = "随终检合格登记封存，到期日 2026-09-26";
    store.put(expiredSeed.id, expiredSeed);
  }

  public synchronized SampleDisposition append(String sampleCode, String action,
                                               String operatorId, String occurredAt,
                                               String remark) {
    SampleDisposition d = new SampleDisposition();
    d.id = idSequence.incrementAndGet();
    d.sampleCode = sampleCode;
    d.action = action;
    d.operatorId = operatorId;
    d.occurredAt = occurredAt;
    d.remark = remark;
    store.put(d.id, d);
    return d;
  }

  /** 历次处置，按发生时间倒序（最近一次在前）。 */
  public synchronized List<SampleDisposition> findBySampleCode(String sampleCode) {
    return store.values().stream()
        .filter(d -> d.sampleCode.equals(sampleCode))
        .sorted(Comparator.comparing((SampleDisposition d) -> d.occurredAt).reversed())
        .toList();
  }
}
