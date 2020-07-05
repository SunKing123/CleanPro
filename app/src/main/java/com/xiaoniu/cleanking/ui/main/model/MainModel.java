package com.xiaoniu.cleanking.ui.main.model;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.geek.push.GeekPush;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.api.WeatherDataApiService;
import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.base.BaseModel;
import com.xiaoniu.cleanking.ui.localpush.LocalPushConfigModel;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.bean.BottoomAdList;
import com.xiaoniu.cleanking.ui.main.bean.DeviceInfo;
import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendEntity;
import com.xiaoniu.cleanking.ui.main.bean.IconsEntity;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.Patch;
import com.xiaoniu.cleanking.ui.main.bean.PushSettingList;
import com.xiaoniu.cleanking.ui.main.bean.RedPacketEntity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.WeatherResponseContent;
import com.xiaoniu.cleanking.ui.main.bean.WebUrlEntity;
import com.xiaoniu.cleanking.ui.main.bean.weatherdao.GreenDaoManager;
import com.xiaoniu.cleanking.ui.main.bean.weatherdao.LocationCityInfo;
import com.xiaoniu.cleanking.ui.main.bean.weatherdao.WeatherCity;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.net.Common2Subscriber;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.ChannelUtil;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author tie
 * @date 2017/5/15
 */
public class MainModel extends BaseModel {

    private final RxAppCompatActivity mActivity;

    @Inject
    UserApiService mService;

    @Inject
    WeatherDataApiService weatherDataApiService;

    @Inject
    public MainModel(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    public void queryAppVersion(Common4Subscriber<AppVersion> commonSubscriber) {
//        mService.queryAppVersion().compose(RxUtil.<AppVersion>rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
    }

    public void getWebUrl(Common4Subscriber<WebUrlEntity> commonSubscriber) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mService.getWebUrl(body).compose(RxUtil.rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
    }

    /**
     * 激活极光推送
     *
     * @param commonSubscriber
     */
    @SuppressLint("CheckResult")
    public void commitJPushAlias(Common4Subscriber<BaseEntity> commonSubscriber) {
        String rid = GeekPush.getRid();
        if (TextUtils.isEmpty(rid)) return;
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("registrationId", rid);
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mService.commitJPushAlias(body).compose(RxUtil.rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
    }

    /**
     * 操作记录(PUSH消息)
     *
     * @param type（1-立即清理 2-一键加速 3-手机清理 4-文件清理 5-微信专清 6-手机降温 7-qq专清）
     */
    public void commitJPushClickTime(int type, Common4Subscriber<BaseEntity> commonSubscriber) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mService.commitJPushClickTime(body).compose(RxUtil.rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
    }


    public void queryAuditSwitch(Common4Subscriber<AuditSwitch> commonSubscriber) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("channel", ChannelUtil.getChannel());
        map.put("appVersion", AppUtils.getVersionName(mActivity, mActivity.getPackageName()));
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mService.queryAuditSwitch(body).compose(RxUtil.rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
    }

    public void getBottomAdList(Common4Subscriber<BottoomAdList> commonSubscriber) {
        mService.getBottomAdList().compose(RxUtil.rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
    }

    /**
     * 广告开关
     *
     * @param commonSubscriber
     */
    public void getSwitchInfoList(Common4Subscriber<SwitchInfoList> commonSubscriber) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("channel", ChannelUtil.getChannel());
        map.put("appVersion", AppUtils.getVersionName(mActivity, mActivity.getPackageName()));
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mService.getSwitchInfoList(body).compose(RxUtil.rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
    }

    /**
     * 插屏广告开关
     *
     * @param commonSubscriber
     */
    public void getScreentSwitch(Common4Subscriber<InsertAdSwitchInfoList> commonSubscriber) {
        mService.getScreentSwitch().compose(RxUtil.rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
    }

    /*
     * 从服务端获取本地推送的相关接口
     */
    @SuppressLint("CheckResult")
    public void getLocalPushConfigFromServer(Common4Subscriber<LocalPushConfigModel> commonSubscriber) {
        mService.getLocalPushConfig().compose(RxUtil.rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
    }


    /**
     * 本地推送配置
     *
     * @param commonSubscriber
     */
    public void getLocalPushSet(Common4Subscriber<PushSettingList> commonSubscriber) {
        mService.getPushLocalSet().compose(RxUtil.rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
    }

    /**
     * 红包
     *
     * @param commonSubscriber
     */
    public void getRedPacketListFromServer(Common4Subscriber<RedPacketEntity> commonSubscriber) {
        mService.getRedPacketList().compose(RxUtil.rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
    }

    /**
     * 底部icon
     *
     * @param commonSubscriber
     */
    public void getIconList(Common4Subscriber<IconsEntity> commonSubscriber) {
        mService.getIconList().compose(RxUtil.rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
    }

    /**
     * 游戏加速列表顶部广告
     */
    public void getRecommendList(Common4Subscriber<HomeRecommendEntity> commonSubscriber) {
        mService.getRecommendList("opearte_page_add_game").compose(RxUtil.rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
    }

    /**
     * 上报Device消息
     *
     * @param commonSubscriber
     */
    public void pushDeviceInfo(DeviceInfo deviceInfo, Common4Subscriber<BaseEntity> commonSubscriber) {
        Gson gson = new Gson();
        String json = gson.toJson(deviceInfo);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mService.pushDeviceInfo(body).compose(RxUtil.rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
    }


    /**
     * 游戏加速列表顶部广告
     */
    public void getWeather72HourList(String areaCode, Common2Subscriber<WeatherResponseContent> commonSubscriber) {
        weatherDataApiService.getWeather72HourList(areaCode).compose(RxUtil.rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
    }


    public WeatherCity updateTableLocation(LocationCityInfo locationCityInfo) {
        WeatherCity locationWeatherCity = GreenDaoManager.getInstance().updateTableLocation(locationCityInfo);
        LogUtils.d("-zzh-:code-" + locationWeatherCity.getAreaCode());
        if (locationWeatherCity != null && !TextUtils.isEmpty(locationWeatherCity.getAreaCode())) {
            String areaCode = locationWeatherCity.getAreaCode();
            return locationWeatherCity;

        }
        return locationWeatherCity;
    }
    /**
     * 上报Device消息
     *
     * @param
     */
//    public void uploadPositionCity(Common4Subscriber<WeatherResponseContent> commonSubscriber) {
/*        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("uuid", UUID.randomUUID().toString());
        requestParams.put("latitude", "31.20941");
        requestParams.put("longitude", "121.625985");
        requestParams.put("areaCode", "101020600");
        requestParams.put("position", "");
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), mGson.toJson(requestParams));*/
//        mService.getWeather72HourList("101020600").compose(RxUtil.rxSchedulerHelper(mActivity)).subscribeWith(commonSubscriber);
//    }


}