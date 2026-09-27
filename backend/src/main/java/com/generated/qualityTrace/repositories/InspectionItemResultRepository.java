package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.models.InspectionItemResult;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/** 检验项结果数据访问：随检验提交逐项写入。 */
@Repository
public class InspectionItemResultRepository {

  private final Map<Long, InspectionItemResult> store = new LinkedHashMap<>();
  private final AtomicLong idSequence = new AtomicLong(100);

  public InspectionItemResultRepository() {
    seed(1L, 1L, "DENSITY", "密度", "1.24", "1.20", "1.30", "PASS");
    seed(2L, 1L, "HARDNESS", "硬度", "82", "70", "90", "PASS");
  }

  private void seed(Long id, Long inspectionId, String itemCode, String itemName,
                    String measured, String min, String max, String status) {
    InspectionItemResult r = new InspectionItemResult();
    r.id = id;
    r.inspectionId = inspectionId;
    r.itemCode = itemCode;
    r.itemName = itemName;
    r.measuredValue = measured;
    r.limitMin = min;
    r.limitMax = max;
    r.itemStatus = status;
    store.put(id, r);
  }

  public synchronized List<InspectionItemResult> findAll() {
    return new ArrayList<>(store.values());
  }

  public synchronized List<InspectionItemResult> findByInspectionId(Long inspectionId) {
    return store.values().stream()
        .filter(r -> r.inspectionId.equals(inspectionId))
        .toList();
  }

  public synchronized InspectionItemResult save(InspectionItemResult result) {
    if (result.id == null) {
      result.id = idSequence.incrementAndGet();
    }
    store.put(result.id, result);
    return result;
  }
}
