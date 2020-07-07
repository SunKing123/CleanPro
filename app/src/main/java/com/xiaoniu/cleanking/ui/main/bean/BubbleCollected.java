package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

import java.util.List;

/**
 * @author zhengzhihao
 * @date 2020/7/6 12
 * @mail：zhengzhihao@hellogeek.com
 */
public class BubbleCollected extends BaseEntity {


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {

        private boolean collected;
        private String uuid;
        private int locationNum;
        private int goldCount;

        public boolean isCollected() {
            return collected;
        }

        public void setCollected(boolean collected) {
            this.collected = collected;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public int getLocationNum() {
            return locationNum;
        }

        public void setLocationNum(int locationNum) {
            this.locationNum = locationNum;
        }

        public int getGoldCount() {
            return goldCount;
        }

        public void setGoldCount(int goldCount) {
            this.goldCount = goldCount;
        }
    }
}
