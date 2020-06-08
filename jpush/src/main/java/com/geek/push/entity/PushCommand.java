package com.geek.push.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.geek.push.core.PushResultCode;

/**
 * push command entity class
 * Created by pyt on 2017/5/10.
 */
public class PushCommand implements Parcelable, PushResultCode {

    public static final Creator<PushCommand> CREATOR = new Creator<PushCommand>() {
        @Override
        public PushCommand createFromParcel(Parcel source) {
            return new PushCommand(source);
        }

        @Override
        public PushCommand[] newArray(int size) {
            return new PushCommand[size];
        }
    };
    private int type;
    private int resultCode;
    private String registerId;
    private String extraMsg;
    private String error;

    public PushCommand() {
    }

    public PushCommand(int type, int resultCode, String registerId, String extraMsg, String error) {
        this.type = type;
        this.resultCode = resultCode;
        this.registerId = registerId;
        this.extraMsg = extraMsg;
        this.error = error;
    }

    protected PushCommand(Parcel in) {
        this.type = in.readInt();
        this.resultCode = in.readInt();
        this.registerId = in.readString();
        this.extraMsg = in.readString();
        this.error = in.readString();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public String getExtraMsg() {
        return extraMsg;
    }

    public void setExtraMsg(String extraMsg) {
        this.extraMsg = extraMsg;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "Command{" +
                "type=" + getTypeText(type) +
                ", resultCode=" + resultCode +
                ", registerId='" + registerId + '\'' +
                ", extraMsg='" + extraMsg + '\'' +
                ", error='" + error + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeInt(this.resultCode);
        dest.writeString(this.registerId);
        dest.writeString(this.extraMsg);
        dest.writeString(this.error);
    }

    public String getTypeText(int type) {
        String typeText = null;
        switch (type) {
            case TYPE_REGISTER:
                typeText = "TYPE_REGISTER";
                break;

            case TYPE_UNREGISTER:
                typeText = "TYPE_UNREGISTER";
                break;

            case TYPE_ADD_TAG:
                typeText = "TYPE_ADD_TAG";
                break;
            case TYPE_DEL_TAG:
                typeText = "TYPE_DEL_TAG";
                break;

            case TYPE_BIND_ALIAS:
                typeText = "TYPE_BIND_ALIAS";
                break;

            case TYPE_UNBIND_ALIAS:
                typeText = "TYPE_UNBIND_ALIAS";
                break;
            case TYPE_CHECK_TAG:
                typeText = "TYPE_CHECK_TAG";
                break;
        }
        return typeText;

    }

}
