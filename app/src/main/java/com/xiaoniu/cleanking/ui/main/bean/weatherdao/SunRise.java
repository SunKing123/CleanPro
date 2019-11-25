package com.xiaoniu.cleanking.ui.main.bean.weatherdao;

import android.os.Parcel;
import android.os.Parcelable;

public class SunRise implements Parcelable{
    public String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.time);
    }

    public SunRise() {
    }

    protected SunRise(Parcel in) {
        this.time = in.readString();
    }

    public static final Creator<SunRise> CREATOR = new Creator<SunRise>() {
        @Override
        public SunRise createFromParcel(Parcel source) {
            return new SunRise(source);
        }

        @Override
        public SunRise[] newArray(int size) {
            return new SunRise[size];
        }
    };
}
