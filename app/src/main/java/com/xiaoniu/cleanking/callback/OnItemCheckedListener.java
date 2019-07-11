package com.xiaoniu.cleanking.callback;

import com.xiaoniu.cleanking.ui.main.bean.ThirdLevelEntity;

/**
 * 条目选择监听
 */
public interface OnItemCheckedListener {
    void onItemChecked(boolean isCheck, ThirdLevelEntity entity);
}
