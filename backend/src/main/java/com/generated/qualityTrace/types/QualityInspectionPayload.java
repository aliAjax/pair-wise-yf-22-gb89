package com.generated.qualityTrace.types;

import java.util.List;

/**
 * 提交检验（首检/巡检/终检）请求。
 * 终检（inspectionType=FINAL）必须携带留样三要素：sampleCode / locationCode / retentionExpiry，
 * 留样登记成功后批次才转为已放行。
 */
public record QualityInspectionPayload(
    String batchNo,
    String inspectorId,
    String inspectionType,
    String standardVersion,
    String resultStatus,
    String inspectedAt,
    String sampleCode,
    String locationCode,
    String retentionExpiry,
    List<InspectionItemResultPayload> items) {
}
