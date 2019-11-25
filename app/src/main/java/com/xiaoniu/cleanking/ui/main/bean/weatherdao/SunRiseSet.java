package com.xiaoniu.cleanking.ui.main.bean.weatherdao;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 日出日落时间
 */
public class SunRiseSet implements Parcelable {

    private String date;

    /**
     * 日出
     */
    public SunRise sunrise;

    /**
     * 日落
     */
    public SunSet sunset;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public SunRise getSunRise() {
        return sunrise;
    }

    public void setSunRise(SunRise sunRise) {
        this.sunrise = sunRise;
    }

    public SunSet getSunSet() {
        return sunset;
    }

    public void setSunSet(SunSet sunSet) {
        this.sunset = sunSet;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeParcelable(this.sunrise, flags);
        dest.writeParcelable(this.sunset, flags);
    }

    public SunRiseSet() {
    }

    protected SunRiseSet(Parcel in) {
        this.date = in.readString();
        this.sunrise = in.readParcelable(SunRise.class.getClassLoader());
        this.sunset = in.readParcelable(SunSet.class.getClassLoader());
    }

    public static final Creator<SunRiseSet> CREATOR = new Creator<SunRiseSet>() {
        @Override
        public SunRiseSet createFromParcel(Parcel source) {
            return new SunRiseSet(source);
        }

        @Override
        public SunRiseSet[] newArray(int size) {
            return new SunRiseSet[size];
        }
    };
}