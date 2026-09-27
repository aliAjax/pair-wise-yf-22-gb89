package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constructors.ProductBatchDtoFactory;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ProductBatchService {

  private final ProductBatchRepository repo;

  public ProductBatchService(ProductBatchRepository repo) {
    this.repo = repo;
  }

  /** 原有批次清单照常可用，字段扩充为真实批次字段与状态文案。 */
  public List<Map<String, Object>> list() {
    return repo.findAll().stream().map(ProductBatchDtoFactory::create).toList();
  }
}
