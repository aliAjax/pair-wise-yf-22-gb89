package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.constants.StatusLabels;
import com.generated.qualityTrace.models.ProductBatch;
import java.util.LinkedHashMap;
import java.util.Map;

/** 批次响应 DTO 构造器，附带批次状态中文文案供列表筛选/详情展示共用。 */
public final class ProductBatchDtoFactory {

  public static Map<String, Object> create(ProductBatch b) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", b.id);
    dto.put("batchNo", b.batchNo);
    dto.put("workOrderId", b.workOrderId);
    dto.put("quantity", b.quantity);
    dto.put("materialLotNo", b.materialLotNo);
    dto.put("producedAt", b.producedAt);
    dto.put("batchStatus", b.batchStatus);
    dto.put("batchStatusLabel", StatusLabels.batchStatus(b.batchStatus));
    return dto;
  }

  private ProductBatchDtoFactory() {}
}
