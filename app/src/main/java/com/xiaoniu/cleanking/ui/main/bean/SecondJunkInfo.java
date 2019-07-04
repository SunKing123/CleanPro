package com.xiaoniu.cleanking.ui.main.bean;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class SecondJunkInfo implements Parcelable {
    private String appGarbageName;
    private String filecatalog;
    private int filesCount;
    private Bitmap garbageIcon;
    private String garbageName;
    private long garbageSize;
    private String garbagetype;
    private boolean isChecked;
    private String packageName;
    
    public String getAppGarbageName() {
        return this.appGarbageName;
    }
    
    public String getFilecatalog() {
        return this.filecatalog;
    }
    
    public int getFilesCount() {
        return this.filesCount;
    }
    
    public Bitmap getGarbageIcon() {
        return this.garbageIcon;
    }
    
    public String getGarbageName() {
        return this.garbageName;
    }
    
    public long getGarbageSize() {
        return this.garbageSize;
    }
    
    public String getGarbagetype() {
        return this.garbagetype;
    }
    
    public int getItemType() {
        return 2;
    }
    
    public String getPackageName() {
        return this.packageName;
    }
    
    public boolean isChecked() {
        return this.isChecked;
    }
    
    public void setAppGarbageName(final String appGarbageName) {
        this.appGarbageName = appGarbageName;
    }
    
    public void setChecked(final boolean isChecked) {
        this.isChecked = isChecked;
    }
    
    public void setFilecatalog(final String filecatalog) {
        this.filecatalog = filecatalog;
    }
    
    public void setFilesCount(final int filesCount) {
        this.filesCount = filesCount;
    }
    
    public void setGarbageIcon(final Bitmap garbageIcon) {
        this.garbageIcon = garbageIcon;
    }
    
    public void setGarbageName(final String garbageName) {
        this.garbageName = garbageName;
    }
    
    public void setGarbageSize(final long garbageSize) {
        this.garbageSize = garbageSize;
    }
    
    public void setGarbagetype(final String garbagetype) {
        this.garbagetype = garbagetype;
    }
    
    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appGarbageName);
        dest.writeString(this.filecatalog);
        dest.writeInt(this.filesCount);
        dest.writeParcelable(this.garbageIcon, flags);
        dest.writeString(this.garbageName);
        dest.writeLong(this.garbageSize);
        dest.writeString(this.garbagetype);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeString(this.packageName);
    }

    public SecondJunkInfo() {
    }

    protected SecondJunkInfo(Parcel in) {
        this.appGarbageName = in.readString();
        this.filecatalog = in.readString();
        this.filesCount = in.readInt();
        this.garbageIcon = in.readParcelable(Drawable.class.getClassLoader());
        this.garbageName = in.readString();
        this.garbageSize = in.readLong();
        this.garbagetype = in.readString();
        this.isChecked = in.readByte() != 0;
        this.packageName = in.readString();
    }

    public static final Parcelable.Creator<SecondJunkInfo> CREATOR = new Parcelable.Creator<SecondJunkInfo>() {
        @Override
        public SecondJunkInfo createFromParcel(Parcel source) {
            return new SecondJunkInfo(source);
        }

        @Override
        public SecondJunkInfo[] newArray(int size) {
            return new SecondJunkInfo[size];
        }
    };
}
