package com.xiaoniu.cleanking.ui.main.bean;

public enum NewsType {
    VIDEO("视频", ""),
    TOUTIAO("推荐", "toutiao"),  //  "头条" -> 推荐
    SHEHUI("热点", "shehui"),    // "社会"  ->  热点
    GUONEI("国内", "guonei"),    //
    GUOJI("国际", "guoji"),
    YULE("娱乐", "yule"),        //
    YUN_SHI("运势", "xingzuo"), // "星座" -> "运势"
    JIAN_KANG("健康","jiankang"),
    REN_WEN("人文", "renwen");;

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
