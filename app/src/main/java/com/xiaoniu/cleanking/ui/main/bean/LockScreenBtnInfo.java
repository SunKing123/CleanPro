package com.xiaoniu.cleanking.ui.main.bean;

/**
 * @author zhengzhihao
 * @date 2019/11/23 15
 * @mail：zhengzhihao@hellogeek.com
 */
public class LockScreenBtnInfo {
    private int post = 0;
    private String checkResult = "";
    private boolean isNormal = false;
    private long reShowTime = 0;


    public long getReShowTime() {
        return reShowTime;
    }

    public void setReShowTime(long reShowTime) {
        this.reShowTime = reShowTime;
    }

    public LockScreenBtnInfo(int post) {
        this.post = post;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public boolean isNormal() {
        return isNormal;
    }

    public void setNormal(boolean normal) {
        isNormal = normal;
    }
}
