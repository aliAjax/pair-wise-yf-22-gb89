package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.routes.QualityInspectionRoutes;
import com.generated.qualityTrace.services.QualityInspectionService;
import com.generated.qualityTrace.types.QualityInspectionPayload;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(QualityInspectionRoutes.PATH)
public class QualityInspectionController {

  private final QualityInspectionService service;

  public QualityInspectionController(QualityInspectionService service) {
    this.service = service;
  }

  /** 原有检验清单照常可用。 */
  @GetMapping
  public List<Map<String, Object>> list() {
    return service.list();
  }

  /**
   * 提交检验（首检/巡检/终检）。终检随请求登记留样：
   * 留样登记成功批次才放行（201）；柜位占用/到期日非法时批次待处理（409/422）。
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, Object> submit(@RequestBody QualityInspectionPayload payload) {
    return service.submit(payload);
  }
}
