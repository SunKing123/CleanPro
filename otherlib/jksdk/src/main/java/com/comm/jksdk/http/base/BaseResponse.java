package com.comm.jksdk.http.base;


import com.comm.jksdk.http.ErrorCode;

import java.io.Serializable;

/**
 * 代码描述<p>
 *
 *     网络返回数据基类
 *
 * @author anhuiqing
 * @since 2019/3/30 18:10
 */

public class BaseResponse<T> implements Serializable {
    private int code;
    private String msg;
    private T data;
    private long timestamp;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public boolean isSuccess(){
        if (code == ErrorCode.SUCCESS){
            return true;
        }else {
            return false;
        }

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}