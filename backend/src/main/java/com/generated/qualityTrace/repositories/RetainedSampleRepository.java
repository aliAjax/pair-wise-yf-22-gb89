package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.constants.RetentionSampleStatus;
import com.generated.qualityTrace.models.RetainedSample;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * 留样数据访问。柜位是物理互斥资源：仅 RETAINED 状态的留样占用柜位；
 * 已 DESTROYED 的留样释放柜位，柜位可被新批次重新使用。
 * “两位同事抢同一个柜位”由 synchronized + status 判定共同拦截。
 */
@Repository
public class RetainedSampleRepository {

  private final Map<Long, RetainedSample> store = new LinkedHashMap<>();
  private final AtomicLong idSequence = new AtomicLong(100);

  public RetainedSampleRepository() {
    // PB-2026-0901 已放行并封存留样：A-01-03 柜位被占用（用于演示柜位冲突）
    RetainedSample seed = new RetainedSample();
    seed.id = 1L;
    seed.sampleCode = "S-2026-0001";
    seed.batchId = 1L;
    seed.batchNo = "PB-2026-0901";
    seed.locationCode = "A-01-03";
    seed.retentionExpiry = "2027-09-21";
    seed.registeredAt = "2026-09-21 10:15:00";
    seed.registeredBy = "QA-01";
    seed.status = RetentionSampleStatus.RETAINED.name();
    store.put(seed.id, seed);

    // 已到期、待质量经理销毁的留样（演示到期销毁流程；销毁后 A-01-04 柜位释放）
    RetainedSample expired = new RetainedSample();
    expired.id = 2L;
    expired.sampleCode = "S-2025-0088";
    expired.batchId = 1L;
    expired.batchNo = "PB-2026-0901";
    expired.locationCode = "A-01-04";
    expired.retentionExpiry = "2026-09-26";
    expired.registeredAt = "2025-09-26 09:00:00";
    expired.registeredBy = "QA-01";
    expired.status = RetentionSampleStatus.RETAINED.name();
    store.put(expired.id, expired);
  }

  public synchronized List<RetainedSample> findAll() {
    return new ArrayList<>(store.values());
  }

  public synchronized Optional<RetainedSample> findBySampleCode(String sampleCode) {
    return store.values().stream()
        .filter(s -> s.sampleCode.equals(sampleCode))
        .findFirst();
  }

  public synchronized Optional<RetainedSample> findByBatchId(Long batchId) {
    return store.values().stream()
        .filter(s -> s.batchId.equals(batchId))
        .findFirst();
  }

  /** 柜位是否被封存中的留样占用。 */
  public synchronized boolean isLocationOccupied(String locationCode) {
    return store.values().stream()
        .anyMatch(s -> s.locationCode.equals(locationCode)
            && RetentionSampleStatus.RETAINED.name().equals(s.status));
  }

  public synchronized boolean existsSampleCode(String sampleCode) {
    return store.values().stream().anyMatch(s -> s.sampleCode.equals(sampleCode));
  }

  /**
   * 登记留样。原子判定样品编号唯一与柜位占用：
   * 返回 false 表示柜位已占用或样品编号重复，调用方必须保留批次待处理。
   */
  public synchronized boolean tryRegister(RetainedSample sample) {
    if (existsSampleCode(sample.sampleCode) || isLocationOccupied(sample.locationCode)) {
      return false;
    }
    if (sample.id == null) {
      sample.id = idSequence.incrementAndGet();
    }
    sample.status = RetentionSampleStatus.RETAINED.name();
    store.put(sample.id, sample);
    return true;
  }

  public synchronized void markDestroyed(String sampleCode, String destroyedAt,
                                         String destroyedBy) {
    findBySampleCode(sampleCode).ifPresent(s -> {
      s.status = RetentionSampleStatus.DESTROYED.name();
      s.destroyedAt = destroyedAt;
      s.destroyedBy = destroyedBy;
    });
  }
}
