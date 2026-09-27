package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.utils.BusinessException;
import com.generated.qualityTrace.utils.Formatters;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.springframework.stereotype.Component;

/**
 * 留样登记入参/业务校验，终检提交与追溯展示共同依赖：
 * 1) 样品编号、柜位、留样到期日必填；
 * 2) 日期格式必须为 ISO（yyyy-MM-dd）；
 * 3) 留样到期日不得早于检验日期（到期日当天视为合法）。
 * 柜位占用冲突由 RetainedSampleRepository 判定（状态化数据），本类只负责纯规则。
 */
@Component
public class RetentionRegistrationValidator {

  public LocalDate validateExpiry(String sampleCode, String locationCode,
                                  String retentionExpiry, String inspectedAt) {
    if (isBlank(sampleCode)) {
      throw BusinessException.invalidPayload(
          String.format(ErrorMessages.INVALID_PAYLOAD, "sampleCode is required"));
    }
    if (isBlank(locationCode)) {
      throw BusinessException.invalidPayload(
          String.format(ErrorMessages.INVALID_PAYLOAD, "locationCode is required"));
    }
    if (isBlank(retentionExpiry)) {
      throw BusinessException.invalidPayload(
          String.format(ErrorMessages.INVALID_PAYLOAD, "retentionExpiry is required"));
    }

    LocalDate expiry;
    LocalDate inspectionDate;
    try {
      expiry = Formatters.parseDate(retentionExpiry);
      inspectionDate = Formatters.parseDate(inspectedAt);
    } catch (DateTimeParseException e) {
      throw BusinessException.invalidPayload(String.format(
          ErrorMessages.INVALID_PAYLOAD,
          "date must be ISO format yyyy-MM-dd: " + e.getParsedString()));
    }

    if (expiry.isBefore(inspectionDate)) {
      throw BusinessException.unprocessable(
          ErrorCodes.RETENTION_DATE_INVALID,
          String.format(ErrorMessages.RETENTION_DATE_INVALID, retentionExpiry, inspectedAt));
    }
    return expiry;
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
