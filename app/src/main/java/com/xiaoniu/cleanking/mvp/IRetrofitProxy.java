package com.xiaoniu.cleanking.mvp;

public interface IRetrofitProxy {

    <T> T create(Class<T> serviceClass);
}
