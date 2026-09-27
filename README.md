# 制造业质量追溯 API 服务

面向小型制造工厂的批次质量追溯后端服务，覆盖工单、批次、检验项、不良记录、**留样封存与到期销毁**和追溯查询。

## 留样并入放行流程

终检合格不再直接放行批次，留样登记是放行的前置条件：

1. 提交终检时同时登记**样品编号、柜位、留样到期日**。
2. 柜位已被封存样占用，或留样到期日早于检验日期时：终检记录照常落库，但**批次保留为「待处理」**（`PENDING_HANDLING`），接口返回 409/422；柜位占用在仓储层加锁判定，两位同事并发抢同一柜位时恰好一方成功。
3. **留样登记成功后批次才转为「已放行」**（`RELEASED`）。待处理批次补齐留样后可重新提交放行。
4. 留样到期后由**质量经理**登记销毁（RBAC 拦截，其他角色 401/403）；未到期不能销毁（422），重复销毁返回 409；销毁后柜位释放、可被新批次复用。
5. 追溯查询返回样品编号、柜位、留样状态和历次处置（登记/销毁），原有各清单接口照常可用。

### 关键接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/quality-inspection` | 提交检验；终检携带 `sampleCode/locationCode/retentionExpiry`，成功 201 放行，拦截 409/422 待处理 |
| GET | `/api/retained-samples` | 留样清单（含状态与历次处置） |
| GET | `/api/retained-samples/{sampleCode}` | 留样详情 |
| POST | `/api/retained-samples/{sampleCode}/dispose` | 到期销毁登记，需请求头 `X-User-Role: QUALITY_MANAGER` |
| GET | `/api/trace/{batchNo}` | 批次全链路追溯：批次/检验项/不良 + 留样编号、柜位、状态、历次处置 |
| GET | `/api/audit-logs` | 操作与追溯事件日志 |
| GET | `/api/{work-order,product-batch,quality-inspection,inspection-item-result,defect-record}` | 原有清单，照常可用 |

终检提交示例：

```bash
curl -X POST http://localhost:21114/api/quality-inspection \
  -H 'Content-Type: application/json' \
  -d '{"batchNo":"PB-2026-0902","inspectorId":"QA-02","inspectionType":"FINAL",
       "resultStatus":"PASS","inspectedAt":"2026-09-27",
       "sampleCode":"S-2026-0010","locationCode":"B-02-05",
       "retentionExpiry":"2027-09-27",
       "items":[{"itemCode":"DENSITY","itemName":"密度","measuredValue":"1.25","limitMin":"1.20","limitMax":"1.30"}]}'
```

到期销毁示例：

```bash
curl -X POST http://localhost:21114/api/retained-samples/S-2025-0088/dispose \
  -H 'Content-Type: application/json' -H 'X-User-Role: QUALITY_MANAGER' \
  -d '{"operatorId":"QM-01","remark":"到期常规销毁"}'
```

## 快速启动

```bash
cp .env.example .env && docker compose up -d
```

## 访问地址或 CLI 示例

后端健康检查：<http://localhost:21114/health>

后端健康检查：<http://localhost:21114/health>


## 本地开发方式


- 后端：进入 `backend` 后按技术栈运行开发命令，接口统一挂在 `/api`。


## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | - |
| 后端 | Spring Boot 3 + Java 17 + MyBatis-Plus |
| 数据库 | PostgreSQL 15 |
| 部署 | Docker Compose |

## 项目目录结构

```text

backend/src/routes, controllers, services, models, repositories, middlewares, constants, constructors, utils, types, config
```

## 环境变量说明

- `COMPOSE_PROJECT_NAME`: Compose 项目名，默认 `quality-trace`

- `BACKEND_PORT`: 后端端口，默认 `21114`
- `DB_PORT`: 数据库宿主机端口
- `DB_USER/DB_PASSWORD/DB_NAME`: 本地数据库凭据

## Docker 部署说明

- 根 Compose 文件不写 `version`，顶层 `name: quality-trace`。
- 容器名均使用 `${COMPOSE_PROJECT_NAME:-quality-trace}` 前缀。
- 数据库使用命名卷，避免绑定中文路径。
- 常见问题：端口占用时修改 `.env` 中端口后重启；需要重置数据时执行 `docker compose down -v`。

## 枚举/常量出现位置清单

- WorkOrderStatus: constants/WorkOrderStatus、types/WorkOrderStatus、constructors、logTemplates、errorMessages、筛选器、展示组件/控制器均有引用。
- InspectionResultStatus: constants/InspectionResultStatus、types/InspectionResultStatus、constructors、logTemplates、errorMessages、筛选器、展示组件/控制器均有引用。
- DefectSeverity: constants/DefectSeverity、types/DefectSeverity、constructors、logTemplates、errorMessages、筛选器、展示组件/控制器均有引用。
- BatchStatus（留样放行流程新增）: `constants/BatchStatus`（IN_PROGRESS/PENDING_HANDLING/RELEASED）、`constants/StatusLabels#BATCH_STATUS_LABELS`、`repositories/ProductBatchRepository`（种子状态）、`services/BatchReleaseCoordinator`（状态流转）、`constructors/ProductBatchDtoFactory`（详情/列表展示）、`utils/Formatters#batchStatus`、`constants/LogTemplates`（BATCH_HOLD/BATCH_RELEASE/BATCH_STATUS_CHANGE）、`constants/ErrorCodes`（BATCH_ALREADY_RELEASED）、`database/init.sql`（product_batch.batch_status 注释）。
- RetentionSampleStatus（留样状态）: `constants/RetentionSampleStatus`（RETAINED/DESTROYED）、`models/RetainedSample`、`repositories/RetainedSampleRepository`（柜位占用判定与销毁置位）、`services/RetainedSampleService`、`constructors/RetainedSampleDtoFactory`、`constants/StatusLabels#SAMPLE_STATUS_LABELS`、`utils/Formatters#sampleStatus`、`database/init.sql`（retained_sample.status 与部分唯一索引）。
- DispositionAction（留样历次处置）: `constants/DispositionAction`（REGISTERED/DESTROYED）、`repositories/SampleDispositionRepository`、`services/RetainedSampleService`、`constructors/SampleDispositionDtoFactory`、`constants/StatusLabels#DISPOSITION_ACTION_LABELS`、追溯接口 `services/TraceQueryService`、`database/init.sql`（sample_disposition.action）。
- UserRole（RBAC 角色）: `constants/UserRole`、`middlewares/RbacMiddleware`（销毁限定 QUALITY_MANAGER）、`config/WebConfig`（拦截路径）、`constants/ErrorCodes`（AUTH_REQUIRED/RBAC_DENIED）。

## 为什么会牵一发动全身

实体字段、枚举、日志模板、错误消息、构造器、筛选器和展示组件被刻意拆散到多个目录；修改一个状态值通常需要同步类型、构造器、服务、控制器、store、页面、README 与数据库种子。

## License

MIT
