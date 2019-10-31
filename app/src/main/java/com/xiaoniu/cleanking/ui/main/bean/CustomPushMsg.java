package com.xiaoniu.cleanking.ui.main.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.geek.push.entity.PushMsg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author zhengzhihao
 * @date 2019/10/31 21
 * @mail：zhengzhihao@hellogeek.com
 */
public class CustomPushMsg  implements Parcelable{

    public static final Parcelable.Creator<CustomPushMsg> CREATOR = new Parcelable.Creator<CustomPushMsg>() {
        public CustomPushMsg createFromParcel(Parcel source) {
            return new CustomPushMsg(source);
        }

        public CustomPushMsg[] newArray(int size) {
            return new CustomPushMsg[size];
        }
    };
    private int notifyId;
    private String title;
    private String content;
    private String msg;
    private String extraMsg;
    private Map<String, String> keyValue;

    public CustomPushMsg(int notifyId, String title, String content, String msg, String extraMsg, Map<String, String> keyValue) {
        this.notifyId = notifyId;
        this.title = title;
        this.content = content;
        this.msg = msg;
        this.extraMsg = extraMsg;
        this.keyValue = keyValue;
    }

    protected CustomPushMsg(Parcel in) {
        this.notifyId = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        this.msg = in.readString();
        this.extraMsg = in.readString();
        int keyValueSize = in.readInt();
        this.keyValue = new HashMap(keyValueSize);

        for(int i = 0; i < keyValueSize; ++i) {
            String key = in.readString();
            String value = in.readString();
            this.keyValue.put(key, value);
        }

    }

    public int getNotifyId() {
        return this.notifyId;
    }

    public void setNotifyId(int notifyId) {
        this.notifyId = notifyId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getExtraMsg() {
        return this.extraMsg;
    }

    public void setExtraMsg(String extraMsg) {
        this.extraMsg = extraMsg;
    }

    public Map<String, String> getKeyValue() {
        return this.keyValue;
    }

    public void setKeyValue(Map<String, String> keyValue) {
        this.keyValue = keyValue;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.notifyId);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.msg);
        dest.writeString(this.extraMsg);
        if (this.keyValue != null) {
            dest.writeInt(this.keyValue.size());
            Iterator var3 = this.keyValue.entrySet().iterator();

            while(var3.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry)var3.next();
                dest.writeString((String)entry.getKey());
                dest.writeString((String)entry.getValue());
            }
        }

    }

    public String toString() {
        return "Msg{notifyId=" + this.notifyId + ", title='" + this.title + '\'' + ", content='" + this.content + '\'' + ", msg='" + this.msg + '\'' + ", extraMsg='" + this.extraMsg + '\'' + ", keyValue=" + this.keyValue + '}';
    }
}
