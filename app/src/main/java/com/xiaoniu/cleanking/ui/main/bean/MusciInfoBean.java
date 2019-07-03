package com.xiaoniu.cleanking.ui.main.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * 音乐信息
 * Created by lang.chen on 2019/7/1
 */
public class MusciInfoBean implements Serializable {

    //文件名
    public String name;
    //播放时长
    public String time;
    //文件路径
    public String path;
    //文件大小
    public long packageSize;
    //是否可选择
    public boolean isSelect;

    @Override
    public String toString() {
        return super.toString();
    }
}
