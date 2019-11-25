package com.xiaoniu.cleanking.api;

import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.ui.main.bean.WeatherResponseContent;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface WeatherDataApiService {

    /**
     *
     * 72小时天气
     *
     * @return
     */
    //**72小时
    @GET("/weatherRecordHour/seventyTwoHours")
    Flowable<WeatherResponseContent> getWeather72HourList(@Query("areaCode") String areaCode);

}
