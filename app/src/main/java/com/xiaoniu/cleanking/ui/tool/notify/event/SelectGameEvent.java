package com.xiaoniu.cleanking.ui.tool.notify.event;

import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;

import java.util.ArrayList;

/**
 * Created by Xilei on 2019/10/31.
 * <p>
 */

public class SelectGameEvent {

    private ArrayList<FirstJunkInfo> list;

    public SelectGameEvent(ArrayList<FirstJunkInfo> list) {
        this.list = list;
    }

    public ArrayList<FirstJunkInfo> getList() {
        return list;
    }
}
