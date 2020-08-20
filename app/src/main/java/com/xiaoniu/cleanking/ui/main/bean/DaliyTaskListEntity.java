package com.xiaoniu.cleanking.ui.main.bean;

/**
 * 我的页面
 * 日常任务列表
 */
public class DaliyTaskListEntity  {
    /**
     * id : 1
     * appName : gj_clean
     * positionCode : opearte_page_main
     * taskName : 测试
     * taskIcon : https://getvideo-test.oss-cn-shanghai.aliyuncs.com/clean/banner/image/121ebfa5b4ab4619b0dfb0f33b0c4797.jpeg
     * mainTitle : 测试文案
     * subtitleTitle : 测试标题
     * goldIcon : https://getvideo-test.oss-cn-shanghai.aliyuncs.com/clean/banner/image/18cac2e52b504a62b34f5374ca03c42a.jpeg
     * goldNum : 100
     * doubledMagnification : 21
     * linkType : 1
     * linkUrl : 111
     * versionCodes : 40,42
     * state : 1
     * isCollect : 0
     */

    private int id;
    private String appName;
    private String positionCode;
    private String taskName;
    private String taskIcon;
    private String mainTitle;
    private String subtitleTitle;
    private String goldIcon;
    private int goldNum;
    private int doubledMagnification;
    private int linkType;
    private String linkUrl;
    private String versionCodes;
    private int state;
    private int isCollect;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskIcon() {
        return taskIcon;
    }

    public void setTaskIcon(String taskIcon) {
        this.taskIcon = taskIcon;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public String getSubtitleTitle() {
        return subtitleTitle;
    }

    public void setSubtitleTitle(String subtitleTitle) {
        this.subtitleTitle = subtitleTitle;
    }

    public String getGoldIcon() {
        return goldIcon;
    }

    public void setGoldIcon(String goldIcon) {
        this.goldIcon = goldIcon;
    }

    public int getGoldNum() {
        return goldNum;
    }

    public void setGoldNum(int goldNum) {
        this.goldNum = goldNum;
    }

    public int getDoubledMagnification() {
        return doubledMagnification;
    }

    public void setDoubledMagnification(int doubledMagnification) {
        this.doubledMagnification = doubledMagnification;
    }

    public int getLinkType() {
        return linkType;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getVersionCodes() {
        return versionCodes;
    }

    public void setVersionCodes(String versionCodes) {
        this.versionCodes = versionCodes;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }
}
