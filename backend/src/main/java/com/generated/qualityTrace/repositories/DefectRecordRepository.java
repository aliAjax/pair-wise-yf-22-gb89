package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.models.DefectRecord;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

/** 不良记录数据访问，追溯树按批次聚合。 */
@Repository
public class DefectRecordRepository {

  private final Map<Long, DefectRecord> store = new LinkedHashMap<>();

  public DefectRecordRepository() {
    seed(1L, 2L, "外观划伤", 6, DefectSeverity.MINOR.name(), "包装边缘毛刺", "DISPOSED");
    seed(2L, 3L, "尺寸超差", 12, DefectSeverity.MAJOR.name(), "夹具松动", "OPEN");
  }

  private void seed(Long id, Long batchId, String type, int qty,
                    String severity, String cause, String disposition) {
    DefectRecord d = new DefectRecord();
    d.id = id;
    d.batchId = batchId;
    d.defectType = type;
    d.defectQty = qty;
    d.severity = severity;
    d.rootCause = cause;
    d.dispositionStatus = disposition;
    store.put(id, d);
  }

  public synchronized List<DefectRecord> findAll() {
    return new ArrayList<>(store.values());
  }

  public synchronized List<DefectRecord> findByBatchId(Long batchId) {
    return store.values().stream().filter(d -> d.batchId.equals(batchId)).toList();
  }
}
