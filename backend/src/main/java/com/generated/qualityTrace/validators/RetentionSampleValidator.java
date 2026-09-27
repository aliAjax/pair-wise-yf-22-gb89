package com.generated.qualityTrace.validators;
import java.time.LocalDate;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.types.QualityInspectionPayload;
public final class RetentionSampleValidator {
  public static void validateFinalInspection(QualityInspectionPayload p) {
    if (p==null || blank(p.batchId()) || blank(p.inspectedAt()) || blank(p.sampleNo()) || blank(p.cabinetSlot()) || blank(p.retainUntil()))
      throw new IllegalArgumentException(ErrorCodes.RETENTION_PAYLOAD_INVALID + ":" + ErrorMessages.RETENTION_PAYLOAD_INVALID);
    try { LocalDate.parse(p.inspectedAt()); LocalDate.parse(p.retainUntil()); }
    catch (Exception e) { throw new IllegalArgumentException(ErrorCodes.RETENTION_PAYLOAD_INVALID + ":" + ErrorMessages.RETENTION_PAYLOAD_INVALID); }
  }
  private static boolean blank(String s){ return s==null || s.isBlank(); }
}
