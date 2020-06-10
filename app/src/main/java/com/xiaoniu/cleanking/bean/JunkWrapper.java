package com.xiaoniu.cleanking.bean;

import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;

import java.util.List;

public class JunkWrapper {

    public ScanningResultType type;
    public List<FirstJunkInfo> junkInfoList;

    public JunkWrapper(ScanningResultType type, List<FirstJunkInfo> junkInfoList) {
        this.type = type;
        this.junkInfoList = junkInfoList;
    }
}
