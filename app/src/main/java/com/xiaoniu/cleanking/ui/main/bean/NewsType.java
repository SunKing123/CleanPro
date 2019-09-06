package com.xiaoniu.cleanking.ui.main.bean;

public enum NewsType {
    VIDEO("视频", ""),
    TOUTIAO("头条", "toutiao"),
    SHEHUI("社会", "shehui"),
    GUONEI("国内", "guonei"),
    GUOJI("国际", "guoji"),
    YULE("娱乐", "yule");

    private String name;
    private String value;

    NewsType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
