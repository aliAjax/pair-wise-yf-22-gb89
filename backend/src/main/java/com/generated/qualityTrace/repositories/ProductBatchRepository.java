package com.generated.qualityTrace.repositories;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.models.ProductBatch;
@Repository
public class ProductBatchRepository {
  private final Map<Long, ProductBatch> store = new LinkedHashMap<>();
  private final AtomicLong seq = new AtomicLong(100);
  public ProductBatchRepository() {
    ProductBatch b1 = new ProductBatch(); b1.id=1L; b1.name="产品批次"; b1.batchNo="B20260920-01"; b1.status=BatchStatus.PENDING.name(); store.put(b1.id, b1);
    ProductBatch b2 = new ProductBatch(); b2.id=2L; b2.name="产品批次"; b2.batchNo="B20260915-01"; b2.status=BatchStatus.RELEASED.name(); store.put(b2.id, b2);
  }
  public List<Map<String,Object>> findAll() {
    List<Map<String,Object>> out = new ArrayList<>();
    for (ProductBatch b : store.values()) out.add(Map.of("id", b.id, "name", b.name, "batchNo", b.batchNo==null?"":b.batchNo, "status", b.status==null?"":b.status));
    return out;
  }
  public Optional<ProductBatch> findById(Long id) { return Optional.ofNullable(store.get(id)); }
  public Optional<ProductBatch> findByBatchNo(String batchNo) { return store.values().stream().filter(b -> Objects.equals(b.batchNo, batchNo)).findFirst(); }
  public ProductBatch save(ProductBatch b) { if (b.id==null) b.id=seq.incrementAndGet(); store.put(b.id, b); return b; }
  public void updateStatus(Long id, String status) { ProductBatch b = store.get(id); if (b!=null) b.status=status; }
}
