package com.xiaoniu.cleanking.ui.main.bean;

public enum NewsType {
    VIDEO("视频", "video",1),
    TOUTIAO("头条", "headlines",0),
    SHEHUI("社会", "society",2),
    GUONEI("国内", "domestic",3),
    GUOJI("国际", "lnternational",4),
    YULE("娱乐", "entertainment",5);
    private String name;
    private String value;
    private int index;

    NewsType(String name, String value,int index) {
        this.name = name;
        this.value = value;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }
}
