package com.xiaoniu.cleanking.ui.localpush;

import com.xiaoniu.cleanking.base.BaseEntity;

public class DayLimit extends BaseEntity {
    private Long updateTime;
    private int alreadyPopCount;


    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public int getAlreadyPopCount() {
        return alreadyPopCount;
    }

    public void setAlreadyPopCount(int alreadyPopCount) {
        this.alreadyPopCount = alreadyPopCount;
    }
}
