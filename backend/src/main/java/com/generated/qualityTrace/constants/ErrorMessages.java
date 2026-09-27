package com.generated.qualityTrace.constants;

public final class ErrorMessages {
  public static final String AUTH_REQUIRED = "missing token";
  public static final String RBAC_DENIED = "role denied";
  public static final String INVALID_PAYLOAD = "invalid request payload: %s";
  public static final String BATCH_NOT_FOUND = "batch not found: %s";
  public static final String SAMPLE_NOT_FOUND = "retained sample not found: %s";
  public static final String LOCATION_OCCUPIED = "cabinet location already occupied: %s";
  public static final String RETENTION_DATE_INVALID =
      "retention expiry date %s is earlier than inspection date %s";
  public static final String RETENTION_NOT_EXPIRED =
      "sample %s is within retention period (expires %s), destruction is not allowed";
  public static final String SAMPLE_ALREADY_DESTROYED = "sample %s has already been destroyed";
  public static final String SAMPLE_CODE_DUPLICATE = "sample code already registered: %s";
  public static final String INSPECTION_NOT_PASSED =
      "final inspection result is %s, batch is held for handling";
  public static final String BATCH_ALREADY_RELEASED =
      "batch %s is already released with a retained sample";

  private ErrorMessages() {}
}
