package com.hellogeek.permission.usagerecord;

import android.util.Log;

import com.hellogeek.permission.Integrate.PermissionIntegrate;

import java.util.HashMap;
import java.util.Map;

public class UsageBuider {
    public static final String TAG = UsageBuider.class.getSimpleName();
    private Map<String, String> extraMap = new HashMap<>();
    private String page;
    private String sourcePage;
    private int usageType;
    private String eventCode;
    private String eventName;

    public UsageBuider() {

    }

    /**
     * must need
     *
     * @param page
     * @return
     */
    public UsageBuider setPage(String page) {
        this.page = page;
        return this;
    }

    /**
     * choice
     *
     * @param sourcePage
     * @return
     */
    public UsageBuider setSourcePage(String sourcePage) {
        this.sourcePage = sourcePage;
        return this;
    }

    /**
     * must need
     *
     * @param usageType
     * @return
     */
    public UsageBuider setUsageType(int usageType) {
        this.usageType = usageType;
        return this;
    }

    /**
     * must need
     *
     * @param eventCode
     * @return
     */
    public UsageBuider setEventCode(String eventCode) {
        this.eventCode = eventCode;
        return this;
    }

    /**
     * must need
     *
     * @param eventName
     * @return
     */
    public UsageBuider setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    /**
     * choice
     *
     * @param key
     * @param value
     * @return
     */
    public UsageBuider setExtra(String key, String value) {
        extraMap.put(key, value);
        return this;
    }

    public UsageBuider setExtraMap(Map<String, String> map) {
        extraMap.putAll(map);
        return this;
    }

    /**
     * must send usage data
     */
    public void send() {
        if (PermissionIntegrate.getPermission().getPermissionRecordCallBack() != null) {
            PermissionIntegrate.getPermission().getPermissionRecordCallBack().usagePermissionRecord(usageType, page, sourcePage, eventCode, eventName, extraMap);
        }
//        PermissionIntegrate.getPermission().usageRecord(usageType, page, sourcePage, eventCode, eventName, extraMap);
        Log.i(TAG, this.toString());
    }

    @Override
    public String toString() {
        return "UsageBuider{" +
                "extraMap=" + extraMap +
                ", page='" + page + '\'' +
                ", sourcePage='" + sourcePage + '\'' +
                ", usageType=" + usageType +
                ", eventCode='" + eventCode + '\'' +
                ", eventName='" + eventName + '\'' +
                '}';
    }
}
