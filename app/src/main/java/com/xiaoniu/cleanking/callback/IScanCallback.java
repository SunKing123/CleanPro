package com.xiaoniu.cleanking.callback;


import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;

import java.util.ArrayList;

/**
 * Created by mazhuang on 16/1/14.
 */
public interface IScanCallback {
    void onBegin();

    void onProgress(FirstJunkInfo info);

    void onFinish(ArrayList<FirstJunkInfo> children);
}
