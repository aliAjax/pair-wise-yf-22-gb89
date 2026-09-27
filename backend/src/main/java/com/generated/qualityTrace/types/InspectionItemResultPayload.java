package com.generated.qualityTrace.types;

/** 检验项结果录入：指标实测值与上下限，service 逐项判定 itemStatus。 */
public record InspectionItemResultPayload(
    String itemCode,
    String itemName,
    String measuredValue,
    String limitMin,
    String limitMax,
    String itemStatus) {
}
