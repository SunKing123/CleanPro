package com.xiaoniu.cleanking.ui.newclean.bean;

import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;

import java.util.List;

public class JunkWrapper {

    public ScanningResultType type;
    public List<FirstJunkInfo> junkInfoList;

    public JunkWrapper(ScanningResultType type, List<FirstJunkInfo> junkInfoList) {
        this.type = type;
        this.junkInfoList = junkInfoList;
    }
}
