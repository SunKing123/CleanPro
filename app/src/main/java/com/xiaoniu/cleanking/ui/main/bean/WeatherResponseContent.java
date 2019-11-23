package com.xiaoniu.cleanking.ui.main.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 代码描述<p>
 *
 * @author xiajianbin
 * @since 2019/4/16 14:30
 */

public class WeatherResponseContent implements Parcelable {
    private String areaCode;
    private String content;

    private String living;
    public String getAreaCode() {
        return areaCode;
    }

    public String getLiving() {
        return living;
    }

    public void setLiving(String living) {
        this.living = living;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public WeatherResponseContent() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.areaCode);
        dest.writeString(this.content);
        dest.writeString(this.living);
    }

    protected WeatherResponseContent(Parcel in) {
        this.areaCode = in.readString();
        this.content = in.readString();
        this.living = in.readString();
    }

    public static final Creator<WeatherResponseContent> CREATOR = new Creator<WeatherResponseContent>() {
        @Override
        public WeatherResponseContent createFromParcel(Parcel source) {
            return new WeatherResponseContent(source);
        }

        @Override
        public WeatherResponseContent[] newArray(int size) {
            return new WeatherResponseContent[size];
        }
    };
}
