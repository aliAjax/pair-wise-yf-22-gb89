package com.generated.qualityTrace.services;
import java.time.LocalDate;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.InspectionType;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.QualityInspectionDtoFactory;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.models.RetentionSample;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.types.QualityInspectionPayload;
import com.generated.qualityTrace.types.RetentionSamplePayload;
import com.generated.qualityTrace.validators.RetentionSampleValidator;
@Service
public class QualityInspectionService {
  private static final Logger log = LoggerFactory.getLogger(QualityInspectionService.class);
  private final QualityInspectionRepository repo;
  private final ProductBatchService batchService;
  private final RetentionSampleService retentionService;
  public QualityInspectionService(QualityInspectionRepository repo, ProductBatchService batchService, RetentionSampleService retentionService) {
    this.repo=repo; this.batchService=batchService; this.retentionService=retentionService;
  }
  public List<Map<String,Object>> list(){return repo.findAll();}
  public Map<String,Object> submitFinalInspection(QualityInspectionPayload p, String operator) {
    RetentionSampleValidator.validateFinalInspection(p);
    final Long batchId;
    try { batchId = Long.parseLong(p.batchId()); }
    catch (NumberFormatException e) { throw new IllegalArgumentException(ErrorCodes.BATCH_NOT_FOUND + ":" + ErrorMessages.BATCH_NOT_FOUND); }
    ProductBatch batch = batchService.getById(batchId);
    if (LocalDate.parse(p.retainUntil()).isBefore(LocalDate.parse(p.inspectedAt()))) {
      batchService.markPending(batch.id);
      log.warn("{} batchId={} reason=retainUntil<inspectedAt", LogTemplates.RETENTION_BLOCKED, batch.id);
      throw new IllegalStateException(ErrorCodes.RETAIN_UNTIL_INVALID + ":" + ErrorMessages.RETAIN_UNTIL_INVALID);
    }
    if (retentionService.cabinetOccupied(p.cabinetSlot())) {
      batchService.markPending(batch.id);
      log.warn("{} batchId={} cabinetSlot={}", LogTemplates.RETENTION_BLOCKED, batch.id, p.cabinetSlot());
      throw new IllegalStateException(ErrorCodes.CABINET_OCCUPIED + ":" + ErrorMessages.CABINET_OCCUPIED);
    }
    QualityInspection qi = new QualityInspection();
    qi.batchId=batch.id; qi.inspectionType=InspectionType.FINAL.name();
    qi.resultStatus=p.resultStatus(); qi.inspectedAt=p.inspectedAt();
    repo.save(qi);
    RetentionSample sample = retentionService.register(new RetentionSamplePayload(p.sampleNo(), p.cabinetSlot(), p.retainUntil()), batch.id, operator);
    batchService.markReleased(batch.id);
    return QualityInspectionDtoFactory.releaseResponse(qi, sample, batchService.getById(batch.id));
  }
}
