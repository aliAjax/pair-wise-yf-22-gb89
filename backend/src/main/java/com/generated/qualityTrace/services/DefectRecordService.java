package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constructors.DefectRecordDtoFactory;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DefectRecordService {

  private final DefectRecordRepository repo;

  public DefectRecordService(DefectRecordRepository repo) {
    this.repo = repo;
  }

  public List<Map<String, Object>> list() {
    return repo.findAll().stream().map(DefectRecordDtoFactory::create).toList();
  }
}
