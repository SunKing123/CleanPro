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
    public static final int GROUP_AD = 5;         //广告垃圾
    public String mName;
    public long mSize;
    public boolean isChecked;
    public boolean isCheckPart = false;
    public ArrayList<FirstJunkInfo> mChildren;
    public ArrayList<OtherJunkInfo> otherChildren;
    public boolean isExpand;
    public boolean needExpand = true;
    public boolean isScanningOver = false;  //是否完成了扫描
    public int junkType;

    public JunkGroup() {
    }

    public JunkGroup(String mName, int junkType) {
        this.mName = mName;
        this.junkType = junkType;
        this.isChecked = true;
        this.mChildren = new ArrayList<>();
        this.isExpand = true;
        this.needExpand = true;
    }

    protected JunkGroup(Parcel in) {
        mName = in.readString();
        mSize = in.readLong();
        isChecked = in.readByte() != 0;
        isCheckPart = in.readByte() != 0;
        isExpand = in.readByte() != 0;
        needExpand = in.readByte() != 0;
        isScanningOver = in.readByte() != 0;
        junkType = in.readInt();
    }

    public static final Creator<JunkGroup> CREATOR = new Creator<JunkGroup>() {
        @Override
        public JunkGroup createFromParcel(Parcel in) {
            return new JunkGroup(in);
        }

        @Override
        public JunkGroup[] newArray(int size) {
            return new JunkGroup[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeLong(mSize);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeByte((byte) (isCheckPart ? 1 : 0));
        dest.writeByte((byte) (isExpand ? 1 : 0));
        dest.writeByte((byte) (needExpand ? 1 : 0));
        dest.writeByte((byte) (isScanningOver ? 1 : 0));
        dest.writeInt(junkType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JunkGroup junkGroup = (JunkGroup) o;

        if (mSize != junkGroup.mSize) return false;
        if (isChecked != junkGroup.isChecked) return false;
        if (isCheckPart != junkGroup.isCheckPart) return false;
        if (isExpand != junkGroup.isExpand) return false;
        if (needExpand != junkGroup.needExpand) return false;
        if (isScanningOver != junkGroup.isScanningOver) return false;
        if (junkType != junkGroup.junkType) return false;
        if (mName != null ? !mName.equals(junkGroup.mName) : junkGroup.mName != null) return false;
        if (mChildren != null ? !mChildren.equals(junkGroup.mChildren) : junkGroup.mChildren != null)
            return false;
        return otherChildren != null ? otherChildren.equals(junkGroup.otherChildren) : junkGroup.otherChildren == null;
    }

    @Override
    public int hashCode() {
        int result = mName != null ? mName.hashCode() : 0;
        result = 31 * result + (int) (mSize ^ (mSize >>> 32));
        result = 31 * result + (isChecked ? 1 : 0);
        result = 31 * result + (isCheckPart ? 1 : 0);
        result = 31 * result + (mChildren != null ? mChildren.hashCode() : 0);
        result = 31 * result + (otherChildren != null ? otherChildren.hashCode() : 0);
        result = 31 * result + (isExpand ? 1 : 0);
        result = 31 * result + (needExpand ? 1 : 0);
        result = 31 * result + (isScanningOver ? 1 : 0);
        result = 31 * result + junkType;
        return result;
    }
}
