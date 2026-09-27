package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constructors.WorkOrderDtoFactory;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderService {

  private final WorkOrderRepository repo;

  public WorkOrderService(WorkOrderRepository repo) {
    this.repo = repo;
  }

  public List<Map<String, Object>> list() {
    return repo.findAll().stream().map(WorkOrderDtoFactory::create).toList();
  }
}
