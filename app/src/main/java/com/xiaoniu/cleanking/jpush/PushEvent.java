package com.xiaoniu.cleanking.jpush;

/**
 * 接收到消息事件<p>
 *
 * @since 2019/5/15 22:14
 */
public class PushEvent {
    private String type;
    private Object data;

    public PushEvent(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
