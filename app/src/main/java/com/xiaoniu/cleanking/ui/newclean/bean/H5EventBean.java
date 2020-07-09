package com.xiaoniu.cleanking.ui.newclean.bean;

import java.io.Serializable;

public class H5EventBean implements Serializable {
    /**
     * 事件类型
     * 0 ，按返回键时，h5请求拦截时回调
     */
    private String eventCode;
    /**
     * 对应类型的额外信息
     */
    private String eventMsg;

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventMsg() {
        return eventMsg;
    }

    public void setEventMsg(String eventMsg) {
        this.eventMsg = eventMsg;
    }
}
