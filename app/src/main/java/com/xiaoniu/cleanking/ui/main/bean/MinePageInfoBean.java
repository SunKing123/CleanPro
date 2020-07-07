package com.xiaoniu.cleanking.ui.main.bean;


import com.xiaoniu.cleanking.base.BaseEntity;

/**
 * deprecation:用户实体类
 * author:ayb
 * time:2018/9/20
 */
public class MinePageInfoBean extends BaseEntity {

    private DataBean data;


    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        /**
         * amount : 42.04
         * gold : 42000
         * currentGold : 0
         * totalGold : 180
         */

        private double amount;
        private int gold;
        private int currentGold;
        private int totalGold;

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public int getGold() {
            return gold;
        }

        public void setGold(int gold) {
            this.gold = gold;
        }

        public int getCurrentGold() {
            return currentGold;
        }

        public void setCurrentGold(int currentGold) {
            this.currentGold = currentGold;
        }

        public int getTotalGold() {
            return totalGold;
        }

        public void setTotalGold(int totalGold) {
            this.totalGold = totalGold;
        }
    }



}
