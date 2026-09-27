package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.models.ProductBatch;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/** 产品批次数据访问。内存存储 + 种子数据；写操作由 service 编排。 */
@Repository
public class ProductBatchRepository {

  private final Map<Long, ProductBatch> store = new LinkedHashMap<>();
  private final AtomicLong idSequence = new AtomicLong(100);

  public ProductBatchRepository() {
    seed(1L, "PB-2026-0901", 10L, 1200, "MAT-A77", "2026-09-20",
        BatchStatus.RELEASED.name());
    seed(2L, "PB-2026-0902", 10L, 800, "MAT-A78", "2026-09-22",
        BatchStatus.IN_PROGRESS.name());
    seed(3L, "PB-2026-0903", 11L, 640, "MAT-B02", "2026-09-25",
        BatchStatus.IN_PROGRESS.name());
  }

  private void seed(Long id, String batchNo, Long workOrderId, int qty,
                    String materialLotNo, String producedAt, String status) {
    ProductBatch b = new ProductBatch();
    b.id = id;
    b.batchNo = batchNo;
    b.workOrderId = workOrderId;
    b.quantity = qty;
    b.materialLotNo = materialLotNo;
    b.producedAt = producedAt;
    b.batchStatus = status;
    store.put(id, b);
  }

  public synchronized List<ProductBatch> findAll() {
    return new ArrayList<>(store.values());
  }

  public synchronized Optional<ProductBatch> findByBatchNo(String batchNo) {
    return store.values().stream().filter(b -> b.batchNo.equals(batchNo)).findFirst();
  }

  public synchronized Optional<ProductBatch> findById(Long id) {
    return Optional.ofNullable(store.get(id));
  }

  public synchronized ProductBatch save(ProductBatch batch) {
    if (batch.id == null) {
      batch.id = idSequence.incrementAndGet();
    }
    store.put(batch.id, batch);
    return batch;
  }

  public synchronized void updateStatus(Long id, String batchStatus) {
    ProductBatch b = store.get(id);
    if (b != null) {
      b.batchStatus = batchStatus;
    }
  }
}
