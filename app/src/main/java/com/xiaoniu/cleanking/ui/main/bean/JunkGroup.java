package com.xiaoniu.cleanking.ui.main.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by mazhuang on 16/1/14.
 */
public class JunkGroup implements Parcelable {
    public static final int GROUP_CACHE = 0;      //缓存垃圾
    public static final int GROUP_UNINSTALL = 1;  //卸载残留
    public static final int GROUP_APK = 2;        //安装无用包
    public static final int GROUP_PROCESS = 3;    //运行内存清理
    public static final int GROUP_OTHER = 4;      //其他垃圾
    public String mName;
    public long mSize;
    public boolean isChecked;
    public boolean isCheckPart = false;
    public ArrayList<FirstJunkInfo> mChildren;
    public ArrayList<OtherJunkInfo> otherChildren;
    public boolean isExpand;
    public boolean needExpand = true;


    public ArrayList<OtherJunkInfo> getOtherChildren() {
        return otherChildren;
    }

    public void setOtherChildren(ArrayList<OtherJunkInfo> otherChildren) {
        this.otherChildren = otherChildren;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public long getmSize() {
        return mSize;
    }

    public void setmSize(long mSize) {
        this.mSize = mSize;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public ArrayList<FirstJunkInfo> getmChildren() {
        return mChildren;
    }

    public void setmChildren(ArrayList<FirstJunkInfo> mChildren) {
        this.mChildren = mChildren;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public boolean isNeedExpand() {
        return needExpand;
    }

    public void setNeedExpand(boolean needExpand) {
        this.needExpand = needExpand;
    }


    public boolean isCheckPart() {
        return isCheckPart;
    }

    public void setCheckPart(boolean checkPart) {
        isCheckPart = checkPart;
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
        dest.writeByte(this.isCheckPart ? (byte) 1 : (byte) 0);
        dest.writeList(this.mChildren);
        dest.writeList(this.otherChildren);
        dest.writeByte(this.isExpand ? (byte) 1 : (byte) 0);
        dest.writeByte(this.needExpand ? (byte) 1 : (byte) 0);
    }

    public JunkGroup() {
    }

    protected JunkGroup(Parcel in) {
        this.mName = in.readString();
        this.mSize = in.readLong();
        this.isChecked = in.readByte() != 0;
        this.isCheckPart = in.readByte() != 0;
        this.mChildren = new ArrayList<FirstJunkInfo>();
        in.readList(this.mChildren, FirstJunkInfo.class.getClassLoader());
        this.otherChildren = new ArrayList<OtherJunkInfo>();
        in.readList(this.otherChildren, OtherJunkInfo.class.getClassLoader());
        this.isExpand = in.readByte() != 0;
        this.needExpand = in.readByte() != 0;
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
