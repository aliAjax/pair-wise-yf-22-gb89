package com.generated.qualityTrace.utils;
import com.generated.qualityTrace.constants.RetentionSampleStatus;
public final class Formatters {
  public static String audit(String type, long id){ return type + "#" + id; }
  public static String retentionSampleStatusText(String status){
    if (RetentionSampleStatus.STORED.name().equals(status)) return "已封存";
    if (RetentionSampleStatus.DESTROYED.name().equals(status)) return "已销毁";
    return "未知";
  }
  public static String cabinetLabel(String cabinetSlot){ return "柜位-" + (cabinetSlot==null?"":cabinetSlot); }
}
