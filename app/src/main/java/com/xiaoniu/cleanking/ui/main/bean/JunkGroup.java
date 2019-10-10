package com.xiaoniu.cleanking.ui.main.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by mazhuang on 16/1/14.
 */
public class JunkGroup implements Parcelable {
    public static final int GROUP_PROCESS = 0;
    public static final int GROUP_CACHE = 1;
    public static final int GROUP_APK = 2;
    public static final int GROUP_UNINSTALL = 3;
    public static final int GROUP_AD = 4;
    public String mName;
    public long mSize;
    public boolean isChecked;
    public ArrayList<FirstJunkInfo> mChildren;
    public boolean isExpand;

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeLong(this.mSize);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeList(this.mChildren);
    }

    public JunkGroup() {
    }

    protected JunkGroup(Parcel in) {
        this.mName = in.readString();
        this.mSize = in.readLong();
        this.isChecked = in.readByte() != 0;
        this.mChildren = new ArrayList<FirstJunkInfo>();
        in.readList(this.mChildren, FirstJunkInfo.class.getClassLoader());
    }

    public static final Parcelable.Creator<JunkGroup> CREATOR = new Parcelable.Creator<JunkGroup>() {
        @Override
        public JunkGroup createFromParcel(Parcel source) {
            return new JunkGroup(source);
        }

        @Override
        public JunkGroup[] newArray(int size) {
            return new JunkGroup[size];
        }
    };
}
