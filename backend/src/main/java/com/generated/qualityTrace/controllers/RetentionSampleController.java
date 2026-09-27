package com.generated.qualityTrace.controllers;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.routes.RetentionSampleRoutes;
import com.generated.qualityTrace.services.ProductBatchService;
import com.generated.qualityTrace.services.RetentionSampleService;
@RestController
@RequestMapping(RetentionSampleRoutes.PATH)
public class RetentionSampleController {
  private final RetentionSampleService service;
  private final ProductBatchService batchService;
  public RetentionSampleController(RetentionSampleService service, ProductBatchService batchService) { this.service=service; this.batchService=batchService; }
  @GetMapping
  public List<Map<String,Object>> list() { return service.list(); }
  @GetMapping("/trace/{batchNo}")
  public Map<String,Object> trace(@PathVariable String batchNo) {
    ProductBatch batch = batchService.getByBatchNo(batchNo);
    return service.trace(batch.batchNo, batch.id);
  }
  @PostMapping("/{id}/destroy")
  public Map<String,Object> destroy(@PathVariable Long id,
      @RequestHeader(value="X-Role", required=false) String role,
      @RequestHeader(value="X-Operator", required=false, defaultValue="system") String operator) {
    return service.destroy(id, role, operator);
  }
}
