package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.models.WorkOrder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

/** 生产工单数据访问。 */
@Repository
public class WorkOrderRepository {

  private final Map<Long, WorkOrder> store = new LinkedHashMap<>();

  public WorkOrderRepository() {
    seed(10L, "WO-2026-001", "P-1001", "密封胶条", 2000, "LINE-A",
        "2026-09-18", WorkOrderStatus.RUNNING.name());
    seed(11L, "WO-2026-002", "P-2042", "结构件支架", 1000, "LINE-B",
        "2026-09-24", WorkOrderStatus.RUNNING.name());
  }

  private void seed(Long id, String orderNo, String productCode, String productName,
                    int plannedQty, String lineCode, String startAt, String status) {
    WorkOrder o = new WorkOrder();
    o.id = id;
    o.orderNo = orderNo;
    o.productCode = productCode;
    o.productName = productName;
    o.plannedQty = plannedQty;
    o.lineCode = lineCode;
    o.startAt = startAt;
    o.status = status;
    store.put(id, o);
  }

  public synchronized List<WorkOrder> findAll() {
    return new ArrayList<>(store.values());
  }
}
