package com.xiaoniu.cleanking.callback;

import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;

/**
 * 条目选择监听
 */
public interface OnCleanListSelectListener {
    void onGroupSelected(int position, boolean isChecked);

    void onFistChilSelected(int groupPsition ,int childPosition,boolean isChecked);
}
