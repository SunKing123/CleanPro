package com.xiaoniu.cleanking.ui.main.bean.weatherdao;

import android.os.Parcel;
import android.os.Parcelable;

public class SunSet implements Parcelable {

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

    public SunSet() {
    }

    protected SunSet(Parcel in) {
        this.time = in.readString();
    }

    public static final Creator<SunSet> CREATOR = new Creator<SunSet>() {
        @Override
        public SunSet createFromParcel(Parcel source) {
            return new SunSet(source);
        }

        @Override
        public SunSet[] newArray(int size) {
            return new SunSet[size];
        }
    };
}
