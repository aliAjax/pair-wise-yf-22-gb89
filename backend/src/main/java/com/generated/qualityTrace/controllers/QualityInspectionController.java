package com.generated.qualityTrace.controllers;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import com.generated.qualityTrace.routes.QualityInspectionRoutes;
import com.generated.qualityTrace.services.QualityInspectionService;
import com.generated.qualityTrace.types.QualityInspectionPayload;
@RestController
@RequestMapping(QualityInspectionRoutes.PATH)
public class QualityInspectionController {
  private final QualityInspectionService service;
  public QualityInspectionController(QualityInspectionService service){this.service=service;}
  @GetMapping
  public List<Map<String,Object>> list(){return service.list();}
  @PostMapping("/final")
  public Map<String,Object> submitFinal(@RequestBody QualityInspectionPayload payload,
      @RequestHeader(value="X-Operator", required=false, defaultValue="system") String operator) {
    return service.submitFinalInspection(payload, operator);
  }
}
