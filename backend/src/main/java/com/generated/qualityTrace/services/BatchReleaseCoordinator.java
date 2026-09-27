package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.constants.DispositionAction;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.InspectionType;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.InspectionItemResultDtoFactory;
import com.generated.qualityTrace.constructors.ProductBatchDtoFactory;
import com.generated.qualityTrace.constructors.QualityInspectionDtoFactory;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.repositories.InspectionItemResultRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.types.InspectionItemResultPayload;
import com.generated.qualityTrace.types.QualityInspectionPayload;
import com.generated.qualityTrace.utils.BusinessException;
import com.generated.qualityTrace.utils.Formatters;
import com.generated.qualityTrace.validators.RetentionRegistrationValidator;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * 批次放行编排服务：把留样并入放行流程。
 * 终检提交 = 检验记录 + 留样登记 + 批次状态流转，任一留样校验不通过则批次保留待处理。
 */
@Service
public class BatchReleaseCoordinator {

  private final ProductBatchRepository batchRepository;
  private final QualityInspectionRepository inspectionRepository;
  private final InspectionItemResultRepository itemResultRepository;
  private final RetainedSampleService retainedSampleService;
  private final RetentionRegistrationValidator retentionValidator;
  private final AuditLogService auditLogService;

  public BatchReleaseCoordinator(ProductBatchRepository batchRepository,
                                 QualityInspectionRepository inspectionRepository,
                                 InspectionItemResultRepository itemResultRepository,
                                 RetainedSampleService retainedSampleService,
                                 RetentionRegistrationValidator retentionValidator,
                                 AuditLogService auditLogService) {
    this.batchRepository = batchRepository;
    this.inspectionRepository = inspectionRepository;
    this.itemResultRepository = itemResultRepository;
    this.retainedSampleService = retainedSampleService;
    this.retentionValidator = retentionValidator;
    this.auditLogService = auditLogService;
  }

  /**
   * 提交检验。终检（FINAL）且合格时：校验留样三要素 -> 登记留样 -> 批次转 RELEASED。
   * 终检不合格、柜位被占用、到期日早于检验日期：批次置 PENDING_HANDLING，不放行。
   * 首检/巡检仅记录检验结果，不改变批次状态。
   */
  public Map<String, Object> submitInspection(QualityInspectionPayload payload) {
    validatePayload(payload);

    ProductBatch batch = batchRepository.findByBatchNo(payload.batchNo())
        .orElseThrow(() -> BusinessException.notFound(
            ErrorCodes.BATCH_NOT_FOUND,
            String.format(ErrorMessages.BATCH_NOT_FOUND, payload.batchNo())));

    String inspectedAt = payload.inspectedAt() == null || payload.inspectedAt().isBlank()
        ? Formatters.today()
        : payload.inspectedAt();
    validateDateFormat(inspectedAt);

    boolean isFinal = InspectionType.FINAL.name().equals(payload.inspectionType());
    boolean passed = InspectionResultStatus.PASS.name().equals(payload.resultStatus());

    // 已放行批次已有封存留样，终检合格提交不得重复登记（防止追溯找不到封存样）
    if (isFinal && passed && BatchStatus.RELEASED.name().equals(batch.batchStatus)) {
      throw BusinessException.conflict(
          ErrorCodes.BATCH_ALREADY_RELEASED,
          String.format(ErrorMessages.BATCH_ALREADY_RELEASED, batch.batchNo));
    }

    // 1) 检验记录先落库（终检被留样拦截时，检验结论仍可追溯）
    QualityInspection inspection = new QualityInspection();
    inspection.batchId = batch.id;
    inspection.inspectorId = payload.inspectorId();
    inspection.inspectionType = payload.inspectionType();
    inspection.standardVersion = payload.standardVersion();
    inspection.resultStatus = payload.resultStatus();
    inspection.inspectedAt = inspectedAt;
    inspection = inspectionRepository.save(inspection);
    saveItemResults(inspection.id, payload.items());

    auditLogService.record(payload.inspectorId(),
        MessageFormat.format(LogTemplates.INSPECTION_CREATE,
            batch.batchNo, payload.inspectionType()),
        "QualityInspection", String.valueOf(inspection.id));

    if (!isFinal) {
      return buildResponse(batch, inspection, false, null);
    }

    // 2) 终检不合格（FAIL / CONDITIONAL_PASS / RECHECK）-> 批次待处理，不登记留样
    if (!passed) {
      return holdBatch(batch, inspection, payload, ErrorCodes.INSPECTION_NOT_PASSED);
    }

    // 3) 留样三要素缺失 -> 422，批次待处理
    if (payload.retentionExpiry() == null
        || payload.retentionExpiry().isBlank()
        || payload.sampleCode() == null
        || payload.sampleCode().isBlank()
        || payload.locationCode() == null
        || payload.locationCode().isBlank()) {
      return holdBatch(batch, inspection, payload, ErrorCodes.INVALID_PAYLOAD);
    }

    try {
      retentionValidator.validateExpiry(payload.sampleCode(), payload.locationCode(),
          payload.retentionExpiry(), inspectedAt);
    } catch (BusinessException e) {
      return holdBatch(batch, inspection, payload, e.getCode());
    }

    // 4) 柜位占用 / 样品编号重复 -> 409，批次待处理（register 内部写拦截日志）
    RetainedSampleService.RegistrationResult registration = retainedSampleService.register(
        batch.id, batch.batchNo, payload.sampleCode(), payload.locationCode(),
        payload.retentionExpiry(), inspectedAt, payload.inspectorId());
    if (!registration.success()) {
      return holdBatch(batch, inspection, payload, registration.reason());
    }

    // 5) 留样登记成功 -> 批次放行
    String previousStatus = batch.batchStatus;
    batchRepository.updateStatus(batch.id, BatchStatus.RELEASED.name());
    batch.batchStatus = BatchStatus.RELEASED.name();
    auditLogService.record(payload.inspectorId(),
        MessageFormat.format(LogTemplates.BATCH_RELEASE,
            batch.batchNo, payload.sampleCode()),
        "ProductBatch", batch.batchNo);
    auditLogService.record(payload.inspectorId(),
        MessageFormat.format(LogTemplates.BATCH_STATUS_CHANGE,
            batch.batchNo, previousStatus, BatchStatus.RELEASED.name()),
        "ProductBatch", batch.batchNo);
    auditLogService.record(payload.inspectorId(),
        MessageFormat.format(LogTemplates.FINAL_INSPECTION_PASS, batch.batchNo),
        "ProductBatch", batch.batchNo);

    return buildResponse(batch, inspection, true, null);
  }

  /** 保留批次待处理：状态置 PENDING_HANDLING，记日志并抛出带批次状态的异常。 */
  private Map<String, Object> holdBatch(ProductBatch batch, QualityInspection inspection,
                                        QualityInspectionPayload payload, String reason) {
    String previousStatus = batch.batchStatus;
    batchRepository.updateStatus(batch.id, BatchStatus.PENDING_HANDLING.name());
    batch.batchStatus = BatchStatus.PENDING_HANDLING.name();
    auditLogService.record(payload.inspectorId(),
        MessageFormat.format(LogTemplates.BATCH_HOLD, batch.batchNo, reason),
        "ProductBatch", batch.batchNo);
    auditLogService.record(payload.inspectorId(),
        MessageFormat.format(LogTemplates.BATCH_STATUS_CHANGE,
            batch.batchNo, previousStatus, BatchStatus.PENDING_HANDLING.name()),
        "ProductBatch", batch.batchNo);
    if (InspectionResultStatus.FAIL.name().equals(payload.resultStatus())) {
      auditLogService.record(payload.inspectorId(),
          MessageFormat.format(LogTemplates.FINAL_INSPECTION_FAIL, batch.batchNo),
          "ProductBatch", batch.batchNo);
    }

    int httpStatus = ErrorCodes.RETENTION_DATE_INVALID.equals(reason)
        || ErrorCodes.INVALID_PAYLOAD.equals(reason) ? 422 : 409;
    String message = switch (reason) {
      case ErrorCodes.LOCATION_OCCUPIED ->
          String.format(ErrorMessages.LOCATION_OCCUPIED, payload.locationCode());
      case ErrorCodes.SAMPLE_CODE_DUPLICATE ->
          String.format(ErrorMessages.SAMPLE_CODE_DUPLICATE, payload.sampleCode());
      case ErrorCodes.RETENTION_DATE_INVALID ->
          String.format(ErrorMessages.RETENTION_DATE_INVALID,
              payload.retentionExpiry(), inspection.inspectedAt);
      case ErrorCodes.INSPECTION_NOT_PASSED ->
          String.format(ErrorMessages.INSPECTION_NOT_PASSED, payload.resultStatus());
      default -> "final inspection not released: " + reason;
    };

    Map<String, Object> details = buildResponse(batch, inspection, false, reason);
    throw new BusinessException(reason, message, httpStatus).withDetails(details);
  }

  private void saveItemResults(Long inspectionId, List<InspectionItemResultPayload> items) {
    if (items == null) {
      return;
    }
    for (InspectionItemResultPayload p : items) {
      com.generated.qualityTrace.models.InspectionItemResult r =
          new com.generated.qualityTrace.models.InspectionItemResult();
      r.inspectionId = inspectionId;
      r.itemCode = p.itemCode();
      r.itemName = p.itemName();
      r.measuredValue = p.measuredValue();
      r.limitMin = p.limitMin();
      r.limitMax = p.limitMax();
      r.itemStatus = p.itemStatus() != null ? p.itemStatus() : judgeItem(p);
      itemResultRepository.save(r);
      auditLogService.record("system",
          MessageFormat.format(LogTemplates.INSPECTION_ITEM_RESULT,
              p.itemCode(), r.itemStatus),
          "InspectionItemResult", p.itemCode());
    }
  }

  /** 未显式给出判定时，按数值上下限自动判定 PASS/FAIL。 */
  private String judgeItem(InspectionItemResultPayload p) {
    try {
      double value = Double.parseDouble(p.measuredValue());
      if (p.limitMin() != null && value < Double.parseDouble(p.limitMin())) {
        return "FAIL";
      }
      if (p.limitMax() != null && value > Double.parseDouble(p.limitMax())) {
        return "FAIL";
      }
      return "PASS";
    } catch (NumberFormatException e) {
      return "UNJUDGED";
    }
  }

  private Map<String, Object> buildResponse(ProductBatch batch, QualityInspection inspection,
                                            boolean released, String reason) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("released", released);
    dto.put("reason", reason);
    dto.put("batch", ProductBatchDtoFactory.create(batch));
    dto.put("inspection", QualityInspectionDtoFactory.create(inspection));

    List<Map<String, Object>> items = itemResultRepository.findByInspectionId(inspection.id)
        .stream()
        .map(InspectionItemResultDtoFactory::create)
        .toList();
    dto.put("itemResults", items);
    dto.put("disposition", released ? List.of(DispositionAction.REGISTERED.name()) : List.of());
    return dto;
  }

  private void validatePayload(QualityInspectionPayload payload) {
    if (payload == null) {
      throw BusinessException.invalidPayload(
          String.format(ErrorMessages.INVALID_PAYLOAD, "body is required"));
    }
    if (isBlank(payload.batchNo())) {
      throw BusinessException.invalidPayload(
          String.format(ErrorMessages.INVALID_PAYLOAD, "batchNo is required"));
    }
    if (isBlank(payload.inspectionType())) {
      throw BusinessException.invalidPayload(
          String.format(ErrorMessages.INVALID_PAYLOAD, "inspectionType is required"));
    }
    if (isBlank(payload.resultStatus())) {
      throw BusinessException.invalidPayload(
          String.format(ErrorMessages.INVALID_PAYLOAD, "resultStatus is required"));
    }
    try {
      InspectionType.valueOf(payload.inspectionType());
    } catch (IllegalArgumentException e) {
      throw BusinessException.invalidPayload(
          String.format(ErrorMessages.INVALID_PAYLOAD,
              "inspectionType must be FIRST/PATROL/FINAL"));
    }
    try {
      InspectionResultStatus.valueOf(payload.resultStatus());
    } catch (IllegalArgumentException e) {
      throw BusinessException.invalidPayload(
          String.format(ErrorMessages.INVALID_PAYLOAD,
              "resultStatus must be PASS/FAIL/CONDITIONAL_PASS/RECHECK"));
    }
  }

  private void validateDateFormat(String date) {
    try {
      Formatters.parseDate(date);
    } catch (RuntimeException e) {
      throw BusinessException.invalidPayload(
          String.format(ErrorMessages.INVALID_PAYLOAD,
              "inspectedAt must be ISO format yyyy-MM-dd"));
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
