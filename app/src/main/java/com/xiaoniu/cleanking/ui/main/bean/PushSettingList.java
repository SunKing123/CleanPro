package com.xiaoniu.cleanking.ui.main.bean;

import com.google.gson.annotations.SerializedName;
import com.xiaoniu.cleanking.base.BaseEntity;

import java.util.List;


public class PushSettingList extends BaseEntity {


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * code : push_1
         * title : 垃圾清理
         * content : 垃圾过多严重影响手机使用
         * position : 立即清理页面
         * url : cleanking://com.xiaoniu.cleanking/native?name=main&main_index=4
         * thresholdSign : 1
         * thresholdNum : 200
         * interValTime : 2
         * dailyLimit : 12
         */


    /*code	String	模板编码，本地推送唯一编码
    title	String	模板标题
    position	String	跳转页面(中文表述，暂时不用)
    content	String	推送内容，一键加速（push_speed_up）中，#代表占位符，替换成当前已用内存百分比
    url	String	跳转页面
    thresholdMin	int	最低阈值，0则代表无限制
    thresholdMax	int	最高阈值，0则代表无限制
    interValTime	int	每次推送间隔时间（单位：小时）
    dailyLimit	int	日限定次数*/
        @SerializedName("code")
        private String codeX;
        private String title;
        private String content;
        private String position;
        private String url;
        private int thresholdSign;
        private int thresholdNum;
        private int interValTime;
        private int dailyLimit;
        private long lastTime= 0;   //最后一次操作时间


        public long getLastTime() {
            return lastTime;
        }

        public void setLastTime(long lastTime) {
            this.lastTime = lastTime;
        }

        public String getCodeX() {
            return codeX;
        }

        public void setCodeX(String codeX) {
            this.codeX = codeX;
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
