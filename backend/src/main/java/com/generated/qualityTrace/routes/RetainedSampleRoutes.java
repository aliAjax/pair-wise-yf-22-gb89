package com.generated.qualityTrace.routes;

/** 留样路由：清单/详情/到期销毁登记。销毁仅质量经理（RbacMiddleware 拦截）。 */
public final class RetainedSampleRoutes {
  public static final String PATH = "/api/retained-samples";
  public static final String DISPOSE = "/api/retained-samples/{sampleCode}/dispose";
  public static final String DETAIL = "/api/retained-samples/{sampleCode}";

  private RetainedSampleRoutes() {}
}
