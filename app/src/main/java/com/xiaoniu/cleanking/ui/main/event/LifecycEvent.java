package com.xiaoniu.cleanking.ui.main.event;

/**
 * @author zhengzhihao
 * @date 2019/10/24 16
 * @mail：zhengzhihao@hellogeek.com
 */
public class LifecycEvent {

    private boolean isActivity = true;

    public LifecycEvent(boolean isActivity) {
        this.isActivity = isActivity;
    }

    public boolean isActivity() {
        return isActivity;
    }

    public void setActivity(boolean activity) {
        isActivity = activity;
    }
}
