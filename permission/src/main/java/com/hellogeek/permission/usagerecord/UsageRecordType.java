package com.hellogeek.permission.usagerecord;

public enum UsageRecordType {
    TYPE_VIEW_PAGE(1),//页面展示事件
    TYPE_CLICK(2),//页面点击事件
    TYPE_CUSTOM(3);//页面常规事件
    private int recordType;

    UsageRecordType(int type) {
        this.recordType = type;
    }

    public int getValue() {
        return recordType;
    }
}
