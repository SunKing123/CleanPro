package com.xiaoniu.cleanking.ui.main.bean;

import java.io.Serializable;

/**
 * 视频信息
 * Created by lang.chen on 2019/7/1
 */
public class VideoInfoBean implements Serializable {

    //日期
    public String date;
    //文件名
    public String name;
    //文件路径
    public String path="";
    //文件大小
    public long packageSize;
    //是否可选择
    public boolean isSelect;

    /**
     * 标题为0,内容为1
     */
    public int itemType;
}
