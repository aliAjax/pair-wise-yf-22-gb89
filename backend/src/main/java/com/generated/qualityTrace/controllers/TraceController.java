package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.routes.TraceRoutes;
import com.generated.qualityTrace.services.TraceQueryService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 批次全链路追溯：样品编号、柜位、留样状态、历次处置 + 检验/不良树。 */
@RestController
@RequestMapping(TraceRoutes.PATH)
public class TraceController {

  private final TraceQueryService service;

  public TraceController(TraceQueryService service) {
    this.service = service;
  }

  @GetMapping("/{batchNo}")
  public Map<String, Object> trace(@PathVariable String batchNo) {
    return service.trace(batchNo);
  }
}
