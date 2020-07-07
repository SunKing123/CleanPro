package com.xiaoniu.cleanking.api;

import com.comm.jksdk.http.Api;
import com.comm.jksdk.http.base.BaseResponse;
import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.ui.main.bean.WeatherForecastResponseEntity;
import com.xiaoniu.cleanking.ui.main.bean.WeatherResponseContent;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;


public interface WeatherDataApiService {

    /**
     *
     * 72小时天气
     *
     * @return
     */
    //**72小时
    @GET("/weatapi/weatherRecordHour/seventyTwoHours")
    Flowable<WeatherResponseContent> getWeather72HourList(@Query("areaCode") String areaCode);

//
//    @Headers({DOMAIN_NAME_HEADER + Api.WEATHER_DOMAIN_NAME})
    @GET("/weatapi/forecastvideo/getForecastVideo")
    Flowable<BaseResponse<WeatherForecastResponseEntity>> getWeatherForecastInfo(@Query("areaCode") String areaCode);
}
