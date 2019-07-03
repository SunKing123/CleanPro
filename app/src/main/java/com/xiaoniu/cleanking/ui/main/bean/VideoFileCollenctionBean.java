package com.xiaoniu.cleanking.ui.main.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 一个按日期格式的视频文件的整合
 * Created by lang.chen on 2019/7/3
 */
public class VideoFileCollenctionBean implements Serializable {

    //保存的是年月日
    public  String date;

    /**
     * 所有日期对应的文件集合
     */
    public List<VideoInfoBean> lists=new ArrayList<>();
}

