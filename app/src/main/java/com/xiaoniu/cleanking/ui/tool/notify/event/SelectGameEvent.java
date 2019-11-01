package com.xiaoniu.cleanking.ui.tool.notify.event;

import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xilei on 2019/10/31.
 * <p>
 */

public class SelectGameEvent {

    private List<FirstJunkInfo> mAllList;
    private ArrayList<FirstJunkInfo> list;
    private boolean mIsNotSelectAll;

    public SelectGameEvent(List<FirstJunkInfo> allList, ArrayList<FirstJunkInfo> list, boolean isNotSelectAll) {
        this.mAllList = allList;
        this.list = list;
        this.mIsNotSelectAll = isNotSelectAll;
    }

    public List<FirstJunkInfo> getAllList() {
        return mAllList;
    }

    public ArrayList<FirstJunkInfo> getList() {
        return list;
    }

    public boolean isNotSelectAll() {
        return mIsNotSelectAll;
    }
}
