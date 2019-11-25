package com.xiaoniu.cleanking.ui.main.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.xiaoniu.cleanking.base.BaseEntity;

/**
 * 代码描述<p>
 *
 * @author xiajianbin
 * @since 2019/4/16 14:30
 */

public class WeatherResponseContent implements Parcelable {


    /**
     * areaCode : 101020600
     * content : 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
     */

    private String areaCode;
    private String content;

    public String getAreaCode() {
        return areaCode;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.areaCode);
        dest.writeString(this.content);
    }

    public WeatherResponseContent() {
    }

    protected WeatherResponseContent(Parcel in) {
        this.areaCode = in.readString();
        this.content = in.readString();
    }

    public static final Parcelable.Creator<WeatherResponseContent> CREATOR = new Parcelable.Creator<WeatherResponseContent>() {
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
