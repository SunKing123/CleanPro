package com.xiaoniu.cleanking.ui.main.bean;

/**
 * @author zhengzhihao
 * @date 2019/10/30 13
 * @mail：zhengzhihao@hellogeek.com
 *
 * 后台操作日志
 */
public class CleanLogInfo {

    /**
     * actionId : 0
     * actionName : 垃圾清理
     * spaceLong : 60
     * decide : 200
     * popContant :
     * lastTime : 11000
     * other01 :
     */

    private int actionId;
    private String actionName;
    private long spaceLong;
    private int decide;
    private String popContant;
    private long lastTime;
    private String other01;

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public long getSpaceLong() {
        return spaceLong;
    }

    public void setSpaceLong(long spaceLong) {
        this.spaceLong = spaceLong;
    }

    public int getDecide() {
        return decide;
    }

    public void setDecide(int decide) {
        this.decide = decide;
    }

    public String getPopContant() {
        return popContant;
    }

    public void setPopContant(String popContant) {
        this.popContant = popContant;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public String getOther01() {
        return other01;
    }

    public void setOther01(String other01) {
        this.other01 = other01;
    }
}
