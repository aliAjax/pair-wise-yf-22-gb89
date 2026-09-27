package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.routes.RetainedSampleRoutes;
import com.generated.qualityTrace.services.RetainedSampleService;
import com.generated.qualityTrace.types.SampleDispositionPayload;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** 留样：清单/详情查询 + 到期销毁登记（销毁由 RbacMiddleware 限定质量经理）。 */
@RestController
@RequestMapping(RetainedSampleRoutes.PATH)
public class RetainedSampleController {

  private final RetainedSampleService service;

  public RetainedSampleController(RetainedSampleService service) {
    this.service = service;
  }

  @GetMapping
  public List<Map<String, Object>> list() {
    return service.list();
  }

  @GetMapping("/{sampleCode}")
  public Map<String, Object> detail(@PathVariable String sampleCode) {
    return service.getBySampleCode(sampleCode);
  }

  /** 到期销毁登记：未到期不能销毁；角色必须为 QUALITY_MANAGER。 */
  @PostMapping("/{sampleCode}/dispose")
  @ResponseStatus(HttpStatus.OK)
  public Map<String, Object> dispose(@PathVariable String sampleCode,
                                     @RequestBody(required = false) SampleDispositionPayload payload) {
    String operatorId = payload == null || payload.operatorId() == null
        ? "QM-00" : payload.operatorId();
    String remark = payload == null ? null : payload.remark();
    return service.disposeByManager(sampleCode, operatorId, remark);
  }
}
