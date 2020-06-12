package com.xiaoniu.cleanking.ui.localpush;

import com.google.gson.annotations.SerializedName;
import com.xiaoniu.cleanking.base.BaseEntity;

import java.util.List;

public class LocalPushConfigModel extends BaseEntity {

    private List<LocalPushConfigModel.Item> data;

    public List<Item> getData() {
        return data;
    }

    public void setData(List<Item> data) {
        this.data = data;
    }

    public static class Item {

        private String code;
        private String position;
        private String url;
        private int thresholdSign;
        private int interValTime;


        private int dailyLimit;//日限制次数
        private String iconUrl;//推送图标
        @SerializedName("functionValTime")
        private int functionUsedInterval;//功能使用完间隔推送时间，单位时间为分
        @SerializedName("popValTime")
        private int popWindowInterval;//弹框推送间隔时间，单位时间为分
        private String title;
        private String content;
        private int thresholdNum;//省电功能专用，当前电量的百分比小于这个数值的时候才弹出通知
        //1.立即清理 2.一键加速 3.手机清理 4.文件清理 5.微信专清 6.手机温降温 7.QQ专清 8.通知栏 9.超强省电
        private int onlyCode;



        //本地变量-温度
        public int temp;
        public int power;


        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public int getFunctionUsedInterval() {
            return functionUsedInterval;
        }

        public void setFunctionUsedInterval(int functionUsedInterval) {
            this.functionUsedInterval = functionUsedInterval;
        }

        public int getPopWindowInterval() {
            return popWindowInterval;
        }

        public void setPopWindowInterval(int popWindowInterval) {
            this.popWindowInterval = popWindowInterval;
        }

        public int getOnlyCode() {
            return onlyCode;
        }

        public void setOnlyCode(int onlyCode) {
            this.onlyCode = onlyCode;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getThresholdSign() {
            return thresholdSign;
        }

        public void setThresholdSign(int thresholdSign) {
            this.thresholdSign = thresholdSign;
        }

        public int getThresholdNum() {
            return thresholdNum;
        }

        public void setThresholdNum(int thresholdNum) {
            this.thresholdNum = thresholdNum;
        }

        public int getInterValTime() {
            return interValTime;
        }

        public void setInterValTime(int interValTime) {
            this.interValTime = interValTime;
        }

        public int getDailyLimit() {
            return dailyLimit;
        }

        public void setDailyLimit(int dailyLimit) {
            this.dailyLimit = dailyLimit;
        }
    }

}
