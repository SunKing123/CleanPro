package com.geek.webpage.eventbus;

/**
 * 代码描述<p>
 *
 * @author anhuiqing
 * @since 2019/6/11 16:41
 */
public class BaseEventBus<T> {
    public String action;
    public T data;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

