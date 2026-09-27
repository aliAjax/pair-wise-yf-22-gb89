package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.models.QualityInspection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/** 质量检验数据访问：终检提交后逐条落库，追溯树按批次聚合。 */
@Repository
public class QualityInspectionRepository {

  private final Map<Long, QualityInspection> store = new LinkedHashMap<>();
  private final AtomicLong idSequence = new AtomicLong(100);

  public QualityInspectionRepository() {
    seed(1L, 1L, "QA-01", "FINAL", "STD-V3",
        InspectionResultStatus.PASS.name(), "2026-09-21");
    seed(2L, 2L, "QA-02", "PATROL", "STD-V3",
        InspectionResultStatus.PASS.name(), "2026-09-23");
  }

  private void seed(Long id, Long batchId, String inspector, String type,
                    String stdVersion, String result, String inspectedAt) {
    QualityInspection i = new QualityInspection();
    i.id = id;
    i.batchId = batchId;
    i.inspectorId = inspector;
    i.inspectionType = type;
    i.standardVersion = stdVersion;
    i.resultStatus = result;
    i.inspectedAt = inspectedAt;
    store.put(id, i);
  }

  public synchronized List<QualityInspection> findAll() {
    return new ArrayList<>(store.values());
  }

  public synchronized List<QualityInspection> findByBatchId(Long batchId) {
    return store.values().stream().filter(i -> i.batchId.equals(batchId)).toList();
  }

  public synchronized QualityInspection save(QualityInspection inspection) {
    if (inspection.id == null) {
      inspection.id = idSequence.incrementAndGet();
    }
    store.put(inspection.id, inspection);
    return inspection;
  }
}
