CREATE TABLE IF NOT EXISTS work_order (
  id INTEGER PRIMARY KEY,
  order_no TEXT,
  product_code TEXT,
  product_name TEXT,
  planned_qty TEXT,
  line_code TEXT,
  start_at TEXT,
  status TEXT
);

CREATE TABLE IF NOT EXISTS product_batch (
  id INTEGER PRIMARY KEY,
  batch_no TEXT,
  work_order_id TEXT,
  quantity TEXT,
  material_lot_no TEXT,
  produced_at TEXT,
  -- IN_PROGRESS 在制待检 / PENDING_HANDLING 待处理（留样登记被拦截）/ RELEASED 已放行（留样登记成功）
  batch_status TEXT
);

CREATE TABLE IF NOT EXISTS quality_inspection (
  id INTEGER PRIMARY KEY,
  batch_id TEXT,
  inspector_id TEXT,
  inspection_type TEXT,
  standard_version TEXT,
  result_status TEXT,
  inspected_at TEXT
);

CREATE TABLE IF NOT EXISTS inspection_item_result (
  id INTEGER PRIMARY KEY,
  inspection_id TEXT,
  item_code TEXT,
  item_name TEXT,
  measured_value TEXT,
  limit_min TEXT,
  limit_max TEXT,
  item_status TEXT
);

CREATE TABLE IF NOT EXISTS defect_record (
  id INTEGER PRIMARY KEY,
  batch_id TEXT,
  defect_type TEXT,
  defect_qty TEXT,
  severity TEXT,
  root_cause TEXT,
  disposition_status TEXT
);

-- 留存样品（留样）：随终检提交登记，登记成功后批次才转为已放行
CREATE TABLE IF NOT EXISTS retained_sample (
  id INTEGER PRIMARY KEY,
  sample_code TEXT UNIQUE,
  batch_id TEXT,
  batch_no TEXT,
  location_code TEXT,
  retention_expiry TEXT,
  registered_at TEXT,
  registered_by TEXT,
  -- RETAINED 封存中（占用柜位）/ DESTROYED 已销毁（柜位释放）
  status TEXT,
  destroyed_at TEXT,
  destroyed_by TEXT
);

-- 留样历次处置：REGISTERED 留样登记 / DESTROYED 到期销毁（质量经理登记）
CREATE TABLE IF NOT EXISTS sample_disposition (
  id INTEGER PRIMARY KEY,
  sample_code TEXT,
  action TEXT,
  operator_id TEXT,
  occurred_at TEXT,
  remark TEXT
);

CREATE TABLE IF NOT EXISTS audit_log (
  id INTEGER PRIMARY KEY,
  actor TEXT,
  action TEXT,
  target_type TEXT,
  target_id TEXT,
  created_at TEXT
);

-- 柜位互斥：仅封存中的留样占用柜位，已销毁的柜位可复用
CREATE UNIQUE INDEX IF NOT EXISTS ux_retained_sample_active_location
  ON retained_sample (location_code)
  WHERE status = 'RETAINED';
