package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.DispositionAction;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.RetainedSampleDtoFactory;
import com.generated.qualityTrace.models.RetainedSample;
import com.generated.qualityTrace.models.SampleDisposition;
import com.generated.qualityTrace.repositories.RetainedSampleRepository;
import com.generated.qualityTrace.repositories.SampleDispositionRepository;
import com.generated.qualityTrace.utils.BusinessException;
import com.generated.qualityTrace.utils.Formatters;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * 留样生命周期服务：随终检登记封存，到期后由质量经理登记销毁。
 * 登记是否放行由 BatchReleaseCoordinator 编排；本类负责留样本身的规则与落库。
 */
@Service
public class RetainedSampleService {

  private final RetainedSampleRepository sampleRepository;
  private final SampleDispositionRepository dispositionRepository;
  private final AuditLogService auditLogService;

  public RetainedSampleService(RetainedSampleRepository sampleRepository,
                               SampleDispositionRepository dispositionRepository,
                               AuditLogService auditLogService) {
    this.sampleRepository = sampleRepository;
    this.dispositionRepository = dispositionRepository;
    this.auditLogService = auditLogService;
  }

  /** 留样登记结果：success=false 时 reason 为 LOCATION_OCCUPIED 或 SAMPLE_CODE_DUPLICATE。 */
  public record RegistrationResult(boolean success, String reason) {
  }

  /**
   * 登记留样（终检合格后调用）。
   * @return 登记结果；失败时调用方必须保留批次待处理。
   */
  public RegistrationResult register(Long batchId, String batchNo, String sampleCode,
                                     String locationCode, String retentionExpiry,
                                     String inspectedAt, String inspectorId) {
    RetainedSample sample = new RetainedSample();
    sample.sampleCode = sampleCode;
    sample.batchId = batchId;
    sample.batchNo = batchNo;
    sample.locationCode = locationCode;
    sample.retentionExpiry = retentionExpiry;
    sample.registeredAt = Formatters.now();
    sample.registeredBy = inspectorId;

    String reason;
    if (sampleRepository.existsSampleCode(sampleCode)) {
      reason = ErrorCodes.SAMPLE_CODE_DUPLICATE;
    } else if (sampleRepository.isLocationOccupied(locationCode)) {
      reason = ErrorCodes.LOCATION_OCCUPIED;
    } else {
      reason = null;
    }
    if (reason != null) {
      auditLogService.record(inspectorId,
          MessageFormat.format(LogTemplates.SAMPLE_REGISTER_BLOCKED, batchNo, reason),
          "RetainedSample", sampleCode);
      return new RegistrationResult(false, reason);
    }

    if (!sampleRepository.tryRegister(sample)) {
      // 并发抢柜位兜底
      auditLogService.record(inspectorId,
          MessageFormat.format(LogTemplates.SAMPLE_REGISTER_BLOCKED,
              batchNo, ErrorCodes.LOCATION_OCCUPIED),
          "RetainedSample", sampleCode);
      return new RegistrationResult(false, ErrorCodes.LOCATION_OCCUPIED);
    }

    String now = Formatters.now();
    dispositionRepository.append(sampleCode, DispositionAction.REGISTERED.name(),
        inspectorId, now, "随终检合格登记封存，到期日 " + retentionExpiry);
    auditLogService.record(inspectorId,
        MessageFormat.format(LogTemplates.SAMPLE_REGISTER,
            sampleCode, batchNo, locationCode, retentionExpiry),
        "RetainedSample", sampleCode);
    return new RegistrationResult(true, null);
  }

  /**
   * 到期销毁登记（仅质量经理，角色由 RbacMiddleware 拦截）。
   * 未到期不能销毁：今天早于留样到期日即拒绝（到期日当天允许销毁）。
   */
  public Map<String, Object> disposeByManager(String sampleCode, String managerId,
                                              String remark) {
    RetainedSample sample = sampleRepository.findBySampleCode(sampleCode)
        .orElseThrow(() -> BusinessException.notFound(
            ErrorCodes.SAMPLE_NOT_FOUND,
            String.format(ErrorMessages.SAMPLE_NOT_FOUND, sampleCode)));

    if ("DESTROYED".equals(sample.status)) {
      auditLogService.record(managerId,
          MessageFormat.format(LogTemplates.SAMPLE_DESTROY_BLOCKED,
              sampleCode, ErrorCodes.SAMPLE_ALREADY_DESTROYED),
          "RetainedSample", sampleCode);
      throw BusinessException.conflict(
          ErrorCodes.SAMPLE_ALREADY_DESTROYED,
          String.format(ErrorMessages.SAMPLE_ALREADY_DESTROYED, sampleCode));
    }

    LocalDate today = LocalDate.now();
    LocalDate expiry = Formatters.parseDate(sample.retentionExpiry);
    if (today.isBefore(expiry)) {
      auditLogService.record(managerId,
          MessageFormat.format(LogTemplates.SAMPLE_DESTROY_BLOCKED,
              sampleCode, ErrorCodes.RETENTION_NOT_EXPIRED),
          "RetainedSample", sampleCode);
      throw BusinessException.unprocessable(
          ErrorCodes.RETENTION_NOT_EXPIRED,
          String.format(ErrorMessages.RETENTION_NOT_EXPIRED,
              sampleCode, sample.retentionExpiry));
    }

    String now = Formatters.now();
    sampleRepository.markDestroyed(sampleCode, now, managerId);
    dispositionRepository.append(sampleCode, DispositionAction.DESTROYED.name(),
        managerId, now, remark == null || remark.isBlank() ? "到期销毁" : remark);
    auditLogService.record(managerId,
        MessageFormat.format(LogTemplates.SAMPLE_DESTROY,
            sampleCode, sample.locationCode, managerId),
        "RetainedSample", sampleCode);

    return getBySampleCode(sampleCode);
  }

  public Map<String, Object> getBySampleCode(String sampleCode) {
    RetainedSample sample = sampleRepository.findBySampleCode(sampleCode)
        .orElseThrow(() -> BusinessException.notFound(
            ErrorCodes.SAMPLE_NOT_FOUND,
            String.format(ErrorMessages.SAMPLE_NOT_FOUND, sampleCode)));
    List<SampleDisposition> history =
        dispositionRepository.findBySampleCode(sampleCode);
    return RetainedSampleDtoFactory.create(sample, history);
  }

  public List<Map<String, Object>> list() {
    return sampleRepository.findAll().stream()
        .map(s -> RetainedSampleDtoFactory.create(
            s, dispositionRepository.findBySampleCode(s.sampleCode)))
        .toList();
  }
}
