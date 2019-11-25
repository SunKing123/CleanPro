package com.xiaoniu.cleanking.ui.main.bean.weatherdao;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 天气状况
 * @author xiangzhenbiao
 * @since 2019/4/19 11:44
 */
public class TodaySkyCondition implements Serializable {

    //时间
    @SerializedName("date")
    private String date;

    //天气值，如："CLOUDY"
    @SerializedName("value")
    private String skyConditionValue;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSkyConditionValue() {
        return skyConditionValue;
    }

    public void setSkyConditionValue(String skyConditionValue) {
        this.skyConditionValue = skyConditionValue;
    }
}
