package com.xiaoniu.common.http.model;

/**
 * 可以自定义类型继承自ApiResult，并重新get方法
 */
public abstract class ApiResult<T> {
    public abstract int getCode();
    public abstract String getMsg();
    public abstract T getData();
    public abstract boolean isResultOk();//根据code值判断服务端返回的结果是否成功
}
