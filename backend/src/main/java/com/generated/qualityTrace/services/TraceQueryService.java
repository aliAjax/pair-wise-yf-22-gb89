package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constructors.DefectRecordDtoFactory;
import com.generated.qualityTrace.constructors.InspectionItemResultDtoFactory;
import com.generated.qualityTrace.constructors.ProductBatchDtoFactory;
import com.generated.qualityTrace.constructors.QualityInspectionDtoFactory;
import com.generated.qualityTrace.constructors.RetainedSampleDtoFactory;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.RetainedSample;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.repositories.InspectionItemResultRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.repositories.RetainedSampleRepository;
import com.generated.qualityTrace.repositories.SampleDispositionRepository;
import com.generated.qualityTrace.utils.BusinessException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * 批次全链路追溯查询。留样并入流程后，追溯树必须显示：
 * 样品编号、柜位、留样状态与历次处置（登记/销毁），并保留检验项与不良记录节点。
 */
@Service
public class TraceQueryService {

  private final ProductBatchRepository batchRepository;
  private final QualityInspectionRepository inspectionRepository;
  private final InspectionItemResultRepository itemResultRepository;
  private final DefectRecordRepository defectRecordRepository;
  private final RetainedSampleRepository retainedSampleRepository;
  private final SampleDispositionRepository sampleDispositionRepository;

  public TraceQueryService(ProductBatchRepository batchRepository,
                           QualityInspectionRepository inspectionRepository,
                           InspectionItemResultRepository itemResultRepository,
                           DefectRecordRepository defectRecordRepository,
                           RetainedSampleRepository retainedSampleRepository,
                           SampleDispositionRepository sampleDispositionRepository) {
    this.batchRepository = batchRepository;
    this.inspectionRepository = inspectionRepository;
    this.itemResultRepository = itemResultRepository;
    this.defectRecordRepository = defectRecordRepository;
    this.retainedSampleRepository = retainedSampleRepository;
    this.sampleDispositionRepository = sampleDispositionRepository;
  }

  public Map<String, Object> trace(String batchNo) {
    ProductBatch batch = batchRepository.findByBatchNo(batchNo)
        .orElseThrow(() -> BusinessException.notFound(
            ErrorCodes.BATCH_NOT_FOUND,
            String.format(ErrorMessages.BATCH_NOT_FOUND, batchNo)));

    Map<String, Object> root = new LinkedHashMap<>();
    root.put("batch", ProductBatchDtoFactory.create(batch));

    // 检验树：每次检验 + 检验项结果
    List<Map<String, Object>> inspections = inspectionRepository.findByBatchId(batch.id)
        .stream()
        .map(insp -> {
          Map<String, Object> node = QualityInspectionDtoFactory.create(insp);
          node.put("itemResults",
              itemResultRepository.findByInspectionId(insp.id).stream()
                  .map(InspectionItemResultDtoFactory::create)
                  .toList());
          return node;
        })
        .toList();
    root.put("inspections", inspections);

    root.put("defects", defectRecordRepository.findByBatchId(batch.id).stream()
        .map(DefectRecordDtoFactory::create)
        .toList());

    // 留样节点：样品编号、柜位、状态、到期日、历次处置（未登记留样时为 null）
    RetainedSample sample = retainedSampleRepository.findByBatchId(batch.id).orElse(null);
    if (sample == null) {
      root.put("retainedSample", null);
    } else {
      root.put("retainedSample", RetainedSampleDtoFactory.create(
          sample, sampleDispositionRepository.findBySampleCode(sample.sampleCode)));
    }
    return root;
  }
}
