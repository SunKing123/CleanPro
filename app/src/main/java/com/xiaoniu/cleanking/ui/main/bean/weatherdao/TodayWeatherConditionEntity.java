package com.xiaoniu.cleanking.ui.main.bean.weatherdao;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.ArrayList;
import java.util.List;

/**
 * 今日天气数据接口
 * @author xiangzhenbiao
 * @since 2019/4/19 11:41
 */
public class TodayWeatherConditionEntity implements Parcelable {

    /**
     * 日出日落
     */
    public List<SunRiseSet> astro;

    private List<TodayTemperatureEntity> temperature;

    private List<TodaySkyCondition> skycon;

    private List<TodayAqi> aqi;

    public List<TodayTemperatureEntity> getTemperature() {
        return temperature;
    }

    public void setTemperature(List<TodayTemperatureEntity> temperature) {
        this.temperature = temperature;
    }

    public List<TodaySkyCondition> getSkycon() {
        return skycon;
    }

    public void setSkycon(List<TodaySkyCondition> skycon) {
        this.skycon = skycon;
    }

    public List<TodayAqi> getAqi() {
        return aqi;
    }

    public void setAqi(List<TodayAqi> aqi) {
        this.aqi = aqi;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.astro);
        dest.writeList(this.temperature);
        dest.writeList(this.skycon);
        dest.writeList(this.aqi);
    }

    public TodayWeatherConditionEntity() {
    }

    protected TodayWeatherConditionEntity(Parcel in) {
        this.astro = in.createTypedArrayList(SunRiseSet.CREATOR);
        this.temperature = new ArrayList<TodayTemperatureEntity>();
        in.readList(this.temperature, TodayTemperatureEntity.class.getClassLoader());
        this.skycon = new ArrayList<TodaySkyCondition>();
        in.readList(this.skycon, TodaySkyCondition.class.getClassLoader());
        this.aqi = new ArrayList<TodayAqi>();
        in.readList(this.aqi, TodayAqi.class.getClassLoader());
    }

    public static final Creator<TodayWeatherConditionEntity> CREATOR = new Creator<TodayWeatherConditionEntity>() {
        @Override
        public TodayWeatherConditionEntity createFromParcel(Parcel source) {
            return new TodayWeatherConditionEntity(source);
        }

        @Override
        public TodayWeatherConditionEntity[] newArray(int size) {
            return new TodayWeatherConditionEntity[size];
        }
    };
}
