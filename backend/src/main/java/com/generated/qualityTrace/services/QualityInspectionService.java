package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constructors.QualityInspectionDtoFactory;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.types.QualityInspectionPayload;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class QualityInspectionService {

  private final QualityInspectionRepository repo;
  private final BatchReleaseCoordinator releaseCoordinator;

  public QualityInspectionService(QualityInspectionRepository repo,
                                  BatchReleaseCoordinator releaseCoordinator) {
    this.repo = repo;
    this.releaseCoordinator = releaseCoordinator;
  }

  /** 原有检验清单照常可用。 */
  public List<Map<String, Object>> list() {
    return repo.findAll().stream().map(QualityInspectionDtoFactory::create).toList();
  }

  /** 提交检验：终检经放行编排，留样登记成功后批次才转为已放行。 */
  public Map<String, Object> submit(QualityInspectionPayload payload) {
    return releaseCoordinator.submitInspection(payload);
  }
}
