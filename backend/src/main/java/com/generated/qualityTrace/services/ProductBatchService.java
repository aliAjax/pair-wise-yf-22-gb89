package com.generated.qualityTrace.services;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
@Service
public class ProductBatchService {
  private static final Logger log = LoggerFactory.getLogger(ProductBatchService.class);
  private final ProductBatchRepository repo;
  public ProductBatchService(ProductBatchRepository repo){this.repo=repo;}
  public List<Map<String,Object>> list(){return repo.findAll();}
  public ProductBatch getById(Long id) {
    return repo.findById(id).orElseThrow(() -> new IllegalArgumentException(ErrorCodes.BATCH_NOT_FOUND + ":" + ErrorMessages.BATCH_NOT_FOUND));
  }
  public ProductBatch getByBatchNo(String batchNo) {
    return repo.findByBatchNo(batchNo).orElseThrow(() -> new IllegalArgumentException(ErrorCodes.BATCH_NOT_FOUND + ":" + ErrorMessages.BATCH_NOT_FOUND));
  }
  public void markReleased(Long id) {
    repo.updateStatus(id, BatchStatus.RELEASED.name());
    log.info("{} batchId={}", LogTemplates.BATCH_RELEASED, id);
  }
  public void markPending(Long id) {
    repo.updateStatus(id, BatchStatus.PENDING.name());
    log.info("{} batchId={}", LogTemplates.BATCH_PENDING, id);
  }
}
