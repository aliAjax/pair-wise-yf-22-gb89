package com.generated.qualityTrace.services;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.RetentionSampleStatus;
import com.generated.qualityTrace.constructors.RetentionSampleDtoFactory;
import com.generated.qualityTrace.middlewares.RbacMiddleware;
import com.generated.qualityTrace.models.RetentionSample;
import com.generated.qualityTrace.models.RetentionSampleEvent;
import com.generated.qualityTrace.repositories.RetentionSampleEventRepository;
import com.generated.qualityTrace.repositories.RetentionSampleRepository;
import com.generated.qualityTrace.types.RetentionSamplePayload;
@Service
public class RetentionSampleService {
  private static final Logger log = LoggerFactory.getLogger(RetentionSampleService.class);
  private final RetentionSampleRepository repo;
  private final RetentionSampleEventRepository eventRepo;
  public RetentionSampleService(RetentionSampleRepository repo, RetentionSampleEventRepository eventRepo) { this.repo=repo; this.eventRepo=eventRepo; }
  public List<Map<String,Object>> list() {
    List<Map<String,Object>> out = new ArrayList<>();
    for (RetentionSample s : repo.findAll()) out.add(RetentionSampleDtoFactory.toResponse(s));
    return out;
  }
  public boolean cabinetOccupied(String cabinetSlot) { return repo.cabinetOccupied(cabinetSlot); }
  public RetentionSample register(RetentionSamplePayload p, Long batchId, String operator) {
    RetentionSample s = new RetentionSample(null, p.sampleNo(), batchId, p.cabinetSlot(), p.retainUntil(), RetentionSampleStatus.STORED.name());
    repo.save(s);
    eventRepo.save(new RetentionSampleEvent(null, s.id, "REGISTER", operator, LogTemplates.RETENTION_REGISTER, LocalDateTime.now().toString()));
    log.info("{} sampleNo={} cabinetSlot={} batchId={}", LogTemplates.RETENTION_REGISTER, s.sampleNo, s.cabinetSlot, batchId);
    return s;
  }
  public Map<String,Object> destroy(Long id, String role, String operator) {
    RbacMiddleware.requireQualityManager(role);
    RetentionSample s = repo.findById(id).orElseThrow(() -> new IllegalArgumentException(ErrorCodes.SAMPLE_NOT_FOUND + ":" + ErrorMessages.SAMPLE_NOT_FOUND));
    if (RetentionSampleStatus.DESTROYED.name().equals(s.status))
      throw new IllegalStateException(ErrorCodes.SAMPLE_ALREADY_DESTROYED + ":" + ErrorMessages.SAMPLE_ALREADY_DESTROYED);
    if (LocalDate.now().isBefore(LocalDate.parse(s.retainUntil)))
      throw new IllegalStateException(ErrorCodes.SAMPLE_NOT_EXPIRED + ":" + ErrorMessages.SAMPLE_NOT_EXPIRED);
    s.status = RetentionSampleStatus.DESTROYED.name(); s.destroyedBy = operator; s.destroyedAt = LocalDate.now().toString();
    repo.save(s);
    eventRepo.save(new RetentionSampleEvent(null, s.id, "DESTROY", operator, LogTemplates.RETENTION_DESTROY, LocalDateTime.now().toString()));
    log.info("{} sampleNo={} operator={}", LogTemplates.RETENTION_DESTROY, s.sampleNo, operator);
    return RetentionSampleDtoFactory.toResponse(s);
  }
  public Map<String,Object> trace(String batchNo, Long batchId) {
    List<RetentionSample> samples = repo.findAllByBatchId(batchId);
    Map<Long,List<RetentionSampleEvent>> eventsBySample = new LinkedHashMap<>();
    for (RetentionSample s : samples) eventsBySample.put(s.id, eventRepo.findBySampleId(s.id));
    return RetentionSampleDtoFactory.traceResponse(batchNo, samples, eventsBySample);
  }
}
