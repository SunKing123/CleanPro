package com.hellogeek.permission.Integrate.interfaces;

import java.util.Map;

public interface PermissionRecordCallback {
    void usagePermissionRecord(int usageType, String currentPage, String sourcePage, String eventCode, String eventName, Map<String, String> extraMap);
}
