package com.xiaoniu.cleanking.ui.viruskill.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xinxiaolong on 2020/7/21.
 * email：xinxiaolong123@foxmail.com
 */
public class ScanTextItemModel implements Parcelable {
    public int id;
    public String name;
    public boolean warning;
    //扫描状态 0：加载中 1：无风险 2：有风险
    public int state;

    public ScanTextItemModel(){

    }

    protected ScanTextItemModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        warning = in.readByte() != 0;
        state = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeByte((byte) (warning ? 1 : 0));
        dest.writeInt(state);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScanTextItemModel> CREATOR = new Creator<ScanTextItemModel>() {
        @Override
        public ScanTextItemModel createFromParcel(Parcel in) {
            return new ScanTextItemModel(in);
        }

        @Override
        public ScanTextItemModel[] newArray(int size) {
            return new ScanTextItemModel[size];
        }
    };
}
