package com.xiaoniu.cleanking.ui.main.bean.weatherdao;

import com.google.gson.annotations.SerializedName;

/**
 * 今日天气温度
 * @author xiangzhenbiao
 * @since 2019/4/19 11:43
 */
public class TodayTemperatureEntity {

    //时间
    @SerializedName("date")
    private String date;

    //平均温度,如：14.06
    @SerializedName("avg")
    private double averageTemperature;

    //最低温度,如：13.79
    @SerializedName("min")
    private double minTemperature;

    //最高温度,如：23
    @SerializedName("max")
    private double maxTemperature;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(double averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }
}
