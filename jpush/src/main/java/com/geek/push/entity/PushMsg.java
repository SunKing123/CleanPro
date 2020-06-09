package com.geek.push.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Message entity class
 * Created by pyt on 2017/5/10.
 */
public class PushMsg implements Parcelable {

    private int notifyId;
    private String title;
    private String content;
    private String msg;
    //额外消息（例如小米推送里面的传输数据）
    private String extraMsg;
    //对应所谓的键值对(初始化值，防止序列化出错)
    private Map<String, String> keyValue;
    private String msgId;

    public PushMsg(int notifyId, String title, String content, String msg, String extraMsg, Map<String, String> keyValue) {
        this.notifyId = notifyId;
        this.title = title;
        this.content = content;
        this.msg = msg;
        this.extraMsg = extraMsg;
        this.keyValue = keyValue;
    }

    public PushMsg(int notifyId, String title, String content, String msg, String msgId, String extraMsg, Map<String, String> keyValue) {
        this.notifyId = notifyId;
        this.title = title;
        this.content = content;
        this.msg = msg;
        this.msgId = msgId;
        this.extraMsg = extraMsg;
        this.keyValue = keyValue;
    }

    public int getNotifyId() {
        return notifyId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getMsg() {
        return msg;
    }

    public String getExtraMsg() {
        return extraMsg;
    }

    public Map<String, String> getKeyValue() {
        return keyValue;
    }

    public String getMsgId() {
        return msgId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.notifyId);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.msg);
        dest.writeString(this.msgId);
        dest.writeString(this.extraMsg);
        if (keyValue != null) {
            dest.writeInt(this.keyValue.size());
            for (Map.Entry<String, String> entry : this.keyValue.entrySet()) {
                dest.writeString(entry.getKey());
                dest.writeString(entry.getValue());
            }
        }
    }

    protected PushMsg(Parcel in) {
        this.notifyId = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        this.msg = in.readString();
        this.msgId = in.readString();
        this.extraMsg = in.readString();
        int keyValueSize = in.readInt();
        this.keyValue = new HashMap<String, String>(keyValueSize);
        for (int i = 0; i < keyValueSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.keyValue.put(key, value);
        }
    }

    public static final Creator<PushMsg> CREATOR = new Creator<PushMsg>() {
        @Override
        public PushMsg createFromParcel(Parcel source) {
            return new PushMsg(source);
        }

        @Override
        public PushMsg[] newArray(int size) {
            return new PushMsg[size];
        }
    };

    @Override
    public String toString() {
        return "PushMsg{" +
                "notifyId=" + notifyId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", msg='" + msg + '\'' +
                ", extraMsg='" + extraMsg + '\'' +
                ", keyValue=" + keyValue +
                ", msgId='" + msgId + '\'' +
                '}';
    }
}
