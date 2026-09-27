package com.generated.qualityTrace.types;

/** 留样销毁登记请求（仅质量经理）。销毁时间由服务端取当前日期。 */
public record SampleDispositionPayload(
    String operatorId,
    String remark) {
}
