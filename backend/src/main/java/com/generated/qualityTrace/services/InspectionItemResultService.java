package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constructors.InspectionItemResultDtoFactory;
import com.generated.qualityTrace.repositories.InspectionItemResultRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class InspectionItemResultService {

  private final InspectionItemResultRepository repo;

  public InspectionItemResultService(InspectionItemResultRepository repo) {
    this.repo = repo;
  }

  public List<Map<String, Object>> list() {
    return repo.findAll().stream().map(InspectionItemResultDtoFactory::create).toList();
  }
}
