package com.xiaoniu.cleanking.ui.newclean.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

import java.io.Serializable;
import java.util.List;

public class BallRewardBean extends BaseEntity {
    public DataBean data;

    public static class DataBean implements Serializable {
        public List<BallBean> drumCoinList;

        public int exchangeStatus;//步数兑换状态：1 - 领取金币，2 - 继续努力，3 - 今日已达成

        public static class BallBean implements Serializable {
            private String code;
            //randomShowNum1：randomShowNum2,随机数字可见金币气泡
            // randomHideNum：随机数字不可见金币气泡
            // exchangeStep：步数兑换金币气泡
            private int goldAmount;//金币数量 乘以系数的
            private String steps;//步数（type为exchangeStep才有）

            private String position;

            private int totalGoldAmount;//总金币数

            //后台返回
            private int actualGetGold;

            //系数
            private double coefficient;

            public double getCoefficient() {
                return coefficient;
            }

            public void setCoefficient(double coefficient) {
                this.coefficient = coefficient;
            }

            public int getActualGetGold() {
                return actualGetGold;
            }

            public void setActualGetGold(int actualGetGold) {
                this.actualGetGold = actualGetGold;
            }

            public int getTotalGoldAmount() {
                return totalGoldAmount;
            }

            public void setTotalGoldAmount(int totalGoldAmount) {
                this.totalGoldAmount = totalGoldAmount;
            }

            public String getPosition() {
                return position;
            }

            public void setPosition(String position) {
                this.position = position;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getSteps() {
                return steps;
            }

            public void setSteps(String steps) {
                this.steps = steps;
            }


            public int getGoldAmount() {
                return goldAmount;
            }

            public void setGoldAmount(int goldAmount) {
                this.goldAmount = goldAmount;
            }

        }
    }

}
