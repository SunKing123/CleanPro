package com.xiaoniu.cleanking.ui.main.bean.weatherdao;


import android.content.Context;
import android.text.TextUtils;

import com.geek.webpage.entity.WebPageEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class WeatherUtils {

    /**
     * @param T  温度
     * @param RH 相对湿度
     * @return
     */
    public static double getCalcHeat(double T, double RH) {
        if (RH < 1) {
            RH *= 100;
        }
        T = 1.8 * T + 32;
        double HI = 0.5 * (T + 61 + (T - 68) * 1.2 + RH * 0.094);
        if (HI >= 80) {
            HI = -42.379 + 2.04901523 * T + 10.14333127 * RH - .22475541 * T * RH
                    - .00683783 * T * T - .05481717 * RH * RH + .00122874 * T * T * RH
                    + .00085282 * T * RH * RH - .00000199 * T * T * RH * RH;
        }
        if (RH < 13 && 80 < T && T < 112) {
            double ADJUSTMENT = (13 - RH) / 4 * Math.sqrt((17 - Math.abs(T - 95)) / 17);
            HI -= ADJUSTMENT;
        } else if (RH > 85 && 80 < T && T < 87) {
            double ADJUSTMENT = (RH - 85) * (87 - T) / 50;
            HI += ADJUSTMENT;
        }

        return (double) Math.round(((HI - 32) / 1.8) * 100) / 100;
    }

    /**
     * 获取天气情况
     *
     * @param skycon skycon
     * @return
     */
    public static String getWeather(String skycon) {
        String weather = "";
        switch (skycon) {
            case "CLEAR_DAY":
                weather = "晴";
//                晴（白天）
                break;
            case "CLEAR_NIGHT":
                weather = "晴";
//                （夜间）
                break;
            case "PARTLY_CLOUDY_DAY":
                weather = "多云";
//                （白天）
                break;
            case "PARTLY_CLOUDY_NIGHT":
                weather = "多云";
//                （夜间）
                break;
            case "CLOUDY":
                weather = "阴";
                break;
            case "LIGHT_HAZE":
                weather = "轻度雾霾";
                break;
            case "MODERATE_HAZE":
                weather = "中度雾霾";
                break;
            case "HEAVY_HAZE":
                weather = "重度雾霾";
                break;
            case "RAIN":
                weather = "雨";
                break;
            case "LIGHT_RAIN":
                weather = "小雨";
                break;
            case "MODERATE_RAIN":
                weather = "中雨";
                break;
            case "HEAVY_RAIN":
                weather = "大雨";
                break;
            case "STORM_RAIN":
                weather = "暴雨";
                break;
            case "FOG":
                weather = "雾";
                break;
            case "SNOW":
                weather = "雪";
                break;
            case "LIGHT_SNOW":
                weather = "小雪";
                break;
            case "MODERATE_SNOW":
                weather = "中雪";
                break;
            case "HEAVY_SNOW":
                weather = "大雪";
                break;
            case "STORM_SNOW":
                weather = "暴雪";
                break;
            case "DUST":
                weather = "浮尘";
                break;
            case "SAND":
                weather = "沙尘";
                break;
            case "WIND":
                weather = "大风";
                break;
            case "THUNDER_SHOWER":
                weather = "雷阵雨";
                break;
            case "HAIL":
                weather = "冰雹";
                break;
            case "SLEET":
                weather = "雨夹雪";
                break;

        }

        return weather;
    }








    /**
     * 获取渐变颜色
     *
     * @param dWeatherAqi
     * @return
     */
    public static int getGradientColorAqi(Double dWeatherAqi) {
        int weatherAqi = dWeatherAqi.intValue();
        int color = 0;
        if (weatherAqi >= 0 && weatherAqi <= 50) {
            color = 0;
        } else if (weatherAqi > 50 && weatherAqi <= 100) {
            color = 1;
        } else if (weatherAqi > 100 && weatherAqi <= 150) {
            color = 2;
        } else if (weatherAqi > 150 && weatherAqi <= 200) {
            color = 3;
        } else if (weatherAqi > 200 && weatherAqi <= 300) {
            color = 4;
        } else {
            color = 5;
        }

        return color;
    }

    /**
     * 获取风力
     *
     * @param windSpeed 风速  公里每小时
     * @return
     */
    public static String getwindSpeed(Double windSpeed) {
        float speed = ((float) Math.round(windSpeed * 5 / 18 * 10)) / 10;
        String str = "0级";
        if (speed >= 0.0 && speed < 0.3) {
            str = "0级";
        } else if (speed >= 0.3 && speed < 1.6) {
            str = "1级";
        } else if (speed >= 1.6 && speed < 3.4) {
            str = "2级";
        } else if (speed >= 3.4 && speed < 5.5) {
            str = "3级";
        } else if (speed >= 5.5 && speed < 8.0) {
            str = "4级";
        } else if (speed >= 8.0 && speed < 10.8) {
            str = "5级";
        } else if (speed >= 10.8 && speed < 13.9) {
            str = "6级";
        } else if (speed >= 13.9 && speed < 17.2) {
            str = "7级";
        } else if (speed >= 17.2 && speed < 20.8) {
            str = "8级";
        } else if (speed >= 20.8 && speed < 24.5) {
            str = "9级";
        } else if (speed >= 24.5 && speed < 28.5) {
            str = "10级";
        } else if (speed >= 28.5 && speed < 32.7) {
            str = "11级";
        } else if (speed >= 32.7 && speed < 37) {
            str = "12级";
        } else if (speed >= 37 && speed < 42) {
            str = "13级";
        } else if (speed >= 42 && speed < 46) {
            str = "14级";
        } else if (speed >= 46 && speed < 52) {
            str = "15级";
        } else if (speed >= 52 && speed < 60) {
            str = "16级";
        } else if (speed >= 60 && speed < 70) {
            str = "17级";
        } else if (speed >= 70) {
            str = "18级";
        }

        return str;
    }

    /**
     * 获取风力
     *
     * @param windSpeed 风速  公里每小时
     * @return
     */
    public static int newgetwindSpeed(Double windSpeed) {
        float speed = ((float) Math.round(windSpeed * 5 / 18 * 10)) / 10;
        int str = 0;
        if (speed >= 0.0 && speed < 0.3) {
            str = 0;
        } else if (speed >= 0.3 && speed < 1.6) {
            str = 1;
        } else if (speed >= 1.6 && speed < 3.4) {
            str = 2;
        } else if (speed >= 3.4 && speed < 5.5) {
            str = 3;
        } else if (speed >= 5.5 && speed < 8.0) {
            str = 4;
        } else if (speed >= 8.0 && speed < 10.8) {
            str = 5;
        } else if (speed >= 10.8 && speed < 13.9) {
            str = 6;
        } else if (speed >= 13.9 && speed < 17.2) {
            str = 7;
        } else if (speed >= 17.2 && speed < 20.8) {
            str = 8;
        } else if (speed >= 20.8 && speed < 24.5) {
            str = 9;
        } else if (speed >= 24.5 && speed < 28.5) {
            str = 10;
        } else if (speed >= 28.5 && speed < 32.7) {
            str = 11;
        } else if (speed >= 32.7 && speed < 37) {
            str = 12;
        } else if (speed >= 37 && speed < 42) {
            str = 13;
        } else if (speed >= 42 && speed < 46) {
            str = 14;
        } else if (speed >= 46 && speed < 52) {
            str = 15;
        } else if (speed >= 52 && speed < 60) {
            str = 16;
        } else if (speed >= 60 && speed < 70) {
            str = 17;
        } else if (speed >= 70) {
            str = 18;
        }

        return str;
    }

    public static String getWindDirection(Double windDirection) {
        String direction = "北风";
        if (windDirection == 0 || windDirection == 360) {
            direction = "北风";
        } else if (windDirection > 0 && windDirection < 90) {
            direction = "东北风";
        } else if (windDirection == 90) {
            direction = "东风";
        } else if (windDirection > 90 && windDirection < 180) {
            direction = "东南风";
        } else if (windDirection == 180) {
            direction = "南风";
        } else if (windDirection > 180 && windDirection < 270) {
            direction = "西南风";
        } else if (windDirection == 270) {
            direction = "西风";
        } else if (windDirection > 270 && windDirection < 360)
            direction = "西北风";
        return direction;
    }


}
