package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

import java.util.List;

/**
 * @author zhengzhihao
 * @date 2020/7/6 12
 * @mail：zhengzhihao@hellogeek.com
 */
public class BubbleDouble extends BaseEntity {



    /**
     * data : {"collected":true,"uuid":"4ac7f670625445dfb76539102c80adf2","locationNum":2,"goldCount":75,"canDouble":1}
     */

    private BubbleDouble.DataBean data;

    public BubbleDouble.DataBean getData() {
        return data;
    }

    public void setData(BubbleDouble.DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        private int goldCount;

        public int getGoldCount() {
            return goldCount;
        }

        public void setGoldCount(int goldCount) {
            this.goldCount = goldCount;
        }
    }
}
