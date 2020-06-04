package com.comm.jksdk.config;

import android.content.Context;
import android.text.TextUtils;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.api.ConfigService;
import com.comm.jksdk.bean.ConfigBean;
import com.comm.jksdk.bean.PositionInfo;
import com.comm.jksdk.config.listener.ConfigListener;
import com.comm.jksdk.constant.Constants;
import com.comm.jksdk.http.OkHttpWrapper;
import com.comm.jksdk.http.base.BaseResponse;
import com.comm.jksdk.http.utils.AppInfoUtils;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.CollectionUtils;
import com.comm.jksdk.utils.JsonUtils;
import com.comm.jksdk.utils.MmkvUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author liupengbing
 * @date 2019/9/27
 */
public class AdsConfig {
    private static Context mContext;
    protected final String TAG = "GeekSdk";
    private static AdsConfig mAdsConfig = null;
    private Gson mGson = new Gson();
    private String mConfigInfo;
    //    private ConfigBean.AdListBean mConfigInfoBean;
    private ArrayList<PositionInfo> posInfoList;

    //本地的配置信息
    private static List<ConfigBean.AdListBean> adsInfoslist = new ArrayList();

    private AdsConfig() {

    }

    public static AdsConfig getInstance(Context context) {
        mContext = context;
        if (mAdsConfig == null) {
            synchronized (AdsConfig.class) {
                if (mAdsConfig == null) {
                    mAdsConfig = new AdsConfig();
                }
            }
        }
        return mAdsConfig;
    }


    /**
     * 从cms请求广告配置
     */
    public void requestConfig(final ConfigListener listener) {
        getConfigInfo().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<ConfigBean>>() {
                    @Override
                    public void accept(BaseResponse<ConfigBean> ConfigInfoBean) {
                        if (ConfigInfoBean == null) {
                            if (listener != null) {
                                listener.adError(1, "配置信息请求失败，请求结果为空");
                            }
                            return;
                        }
                        if (!ConfigInfoBean.isSuccess()) {
                            LogUtils.d(TAG, "accept->配置信息请求失败:" + ConfigInfoBean.getCode()
                                    + ConfigInfoBean.getMsg());
//                            Toast.makeText(mContext, "accept->配置信息请求失败:" + ConfigInfoBean.getCode()
//                                    +ConfigInfoBean.getMsg(), Toast.LENGTH_LONG).show();
                            if (listener != null) {
                                listener.adError(1, "配置信息请求失败,code:" + ConfigInfoBean.getCode() + " msg:" + ConfigInfoBean.getMsg());
                            }
                            return;
                        }

                        ConfigBean configBean = ConfigInfoBean.getData();
                        if (configBean == null) {
                            LogUtils.d(TAG, "accept->配置信息为空 ");
//                            Toast.makeText(mContext, "accept->配置信息为空 ", Toast.LENGTH_LONG).show();
                            if (listener != null) {
                                listener.adError(1, "配置信息请求失败,configBean为空。");
                            }
                            return;
                        }

                        List<ConfigBean.AdListBean> configList = configBean.getAdList();
                        if (configList == null || configList.size() == 0) {
                            LogUtils.d(TAG, "accept->配置信息为空 ");
//                            Toast.makeText(mContext, "accept->配置信息为空 ", Toast.LENGTH_LONG).show();
                            if (listener != null) {
                                listener.adError(1, "配置信息请求失败,configList为空。");
                            }
                            return;
                        }
//                        for (int i = 0; i < configList.size(); i++) {
//                            // "isChange": 0,//是否变更：0 - 无  1 - 有
//                            if (configList.get(i).getIsChange() == 1) {
//                                //更新数据
//                                //对象转json保存到sp
//                                String adPosition = configList.get(i).getAdPosition();
//                                if (TextUtils.isEmpty(adPosition)) {
//                                    return;
//                                }
//
//                                String configInfo = mGson.toJson(configList.get(i));
//                                SpUtils.putString(adPosition, configInfo);
//                            }
//
//                        }
                        //对象转json保存到sp
                        //保存总json
                        setAdsInfoslist(configBean);
                        LogUtils.d(TAG, "accept->配置信息请求成功: ");
//                        Toast.makeText(mContext, "accept->配置信息请求成功: "+configInfo, Toast.LENGTH_LONG).show();

                        if (listener != null) {
                            listener.adSuccess(configList);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.d(TAG, "accept->配置信息请求失败" + throwable.getMessage());
//                        Toast.makeText(mContext, "accept->配置信息请求失败" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        if (listener != null) {
                            listener.adError(1, "配置信息请求失败," + throwable.getMessage());
                        }
                    }
                });
        return;
    }


    /**
     * 发请求
     *
     * @return
     */
    private Observable<BaseResponse<ConfigBean>> getConfigInfo() {
        Map<String, Object> requestParams = CollectionUtils.createMap();
        RequestBody requestBody = null;
        int bid = AdsConfig.getBid();
        if (bid > 0) {
            requestParams.put("bid", bid);
        }
        String productName = GeekAdSdk.getRroductName();
        requestParams.put("productName", productName);
        String marketName = GeekAdSdk.getChannel();
        requestParams.put("marketName", marketName);
        requestParams.put("versionCode", AppInfoUtils.getVerCode(GeekAdSdk.getContext()));
        requestParams.put("osSystem", 1);
        long userActive = AdsConfig.getUserActive();
        if (userActive < 0) {
            userActive = System.currentTimeMillis();
            AdsConfig.setUserActive(userActive);
        }
        requestParams.put("userActive", userActive);
        requestParams.put("ts", System.currentTimeMillis());
        String latitude = AdsConfig.getLatitude();
        if (!TextUtils.isEmpty(latitude)) {
            requestParams.put("latitude", latitude);
        }
        String longitude = AdsConfig.getLongitude();
        if (!TextUtils.isEmpty(longitude)) {
            requestParams.put("longitude", longitude);
        }
        requestParams.put("province", Constants.province);
        requestParams.put("city", Constants.city);
        requestParams.put("modelVersion", "");
        requestParams.put("sdkVersion", 1);

//        Boolean posInfosBoolean=getPositionInfos();
//        if (posInfosBoolean) {
//            requestParams.put("positionInfos", posInfoList);
//        }
        String requstData = mGson.toJson(requestParams);
        LogUtils.d(TAG, "requstData->" + requstData);

        requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requstData);
        return OkHttpWrapper.getInstance().getRetrofit().create(ConfigService.class).getConfig(requestBody);
    }

    /**
     * 获取所有位置对于信息
     *
     * @return
     */
    private Boolean getPositionInfos() {
        posInfoList = new ArrayList<PositionInfo>();
        posInfoList.clear();
        Boolean positionInfos = false;
        // 从sp获取配置信息
        mConfigInfo = MmkvUtil.getString(Constants.SPUtils.CONFIG_INFO, "");
        if (!TextUtils.isEmpty(mConfigInfo)) {
//            BaseResponse<ConfigBean> mConfigInfoBean = mGson.fromJson(mConfigInfo, new TypeToken<BaseResponse<ConfigBean>>() {
//            }.getType());
            ConfigBean configBean = JsonUtils.decode(mConfigInfo, ConfigBean.class);
            if (configBean != null) {
                List<ConfigBean.AdListBean> adListBeans = configBean.getAdList();
                if (!CollectionUtils.isEmpty(adListBeans)) {
                    for (ConfigBean.AdListBean adListBean : adListBeans) {
                        PositionInfo posInfo = new PositionInfo();
                        posInfo.adPosition = adListBean.getAdPosition();
                        posInfo.adVersion = adListBean.getAdVersion();
                        posInfo.productId = adListBean.getProductId();
                        posInfoList.add(posInfo);
                    }
                    positionInfos = true;
                }
//                if (mConfigInfoBean.getData() != null) {
//                    if (mConfigInfoBean.getData().getAdList() != null && mConfigInfoBean.getData().getAdList().size() > 0) {
//                        for (int i = 0; i < mConfigInfoBean.getData().getAdList().size(); i++) {
//                            PositionInfo posInfo = new PositionInfo();
//                            posInfo.adPosition = mConfigInfoBean.getData().getAdList().get(i).getAdPosition();
//                            posInfo.adVersion = mConfigInfoBean.getData().getAdList().get(i).getAdVersion();
//                            posInfo.productId = mConfigInfoBean.getData().getAdList().get(i).getProductId();
//                            posInfoList.add(posInfo);
//                        }
//                        positionInfos=true;
//                    }
//                }
            }

        }
        return positionInfos;

    }

    /**
     * 获取本地保存配置信息
     */
    public ConfigBean.AdListBean getConfig(String postion) {
        if (TextUtils.isEmpty(postion)) {
            return null;
        }
        String confString= MmkvUtil.getString(Constants.SPUtils.CONFIG_INFO + "_" + postion.trim() ,"");
        if(TextUtils.isEmpty(confString)){
            return null;
        }
        LogUtils.d("zz---"+postion.trim()+"----"+confString);
        ConfigBean.AdListBean adListBeans = new Gson().fromJson(confString,ConfigBean.AdListBean.class);
        return adListBeans;
//        // 从sp获取配置信息
//        if (!TextUtils.isEmpty(cmsConfigKey)) {
//            mConfigInfo = SpUtils.getString(cmsConfigKey, "");
//            mConfigInfoBean = mGson.fromJson(mConfigInfo, new TypeToken<ConfigBean.AdListBean>() {}.getType());
//        }
//        if (TextUtils.isEmpty(mConfigInfo)) {
//            // 获取默认配置（客户端）
//            if (!TextUtils.isEmpty(defaultConfigKey)) {
//                String allConfigInfo = SpUtils.getString(defaultConfigKey, "");
//                BaseResponse<ConfigBean> allConfigInfoBean = mGson.fromJson(allConfigInfo, new TypeToken<BaseResponse<ConfigBean>>() {
//                }.getType());
//                if (allConfigInfoBean != null) {
//                    if (allConfigInfoBean.getData() != null) {
//                        if (allConfigInfoBean.getData().getAdList() != null && allConfigInfoBean.getData().getAdList().size() > 0) {
//                            for (int i = 0; i < allConfigInfoBean.getData().getAdList().size(); i++) {
//                                String adPosition = allConfigInfoBean.getData().getAdList().get(i).getAdPosition();
//                                if (!TextUtils.isEmpty(adPosition)) {
//                                    if (cmsConfigKey.equals(adPosition)) {
//                                        mConfigInfoBean = allConfigInfoBean.getData().getAdList().get(i);
//                                        LogUtils.w(TAG,"DATA：客户端默认配置信息");
//                                        Toast.makeText(mContext, "DATA：客户端默认配置信息", Toast.LENGTH_SHORT).show();
//
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                }
//            }else {
//                LogUtils.w(TAG,"默认defaultConfigKey为空");
//            }
//
//        }else{
//            LogUtils.w(TAG,"DATA：cms上次请求配置信息");
//        }
//
//        return mConfigInfoBean;
    }

    /**
     * 获取bid
     *
     * @return
     */
    public static int getBid() {
        if (Constants.bid > 0) {
            return Constants.bid;
        }
        Constants.bid = MmkvUtil.getInt(Constants.SPUtils.BID, -1);
        return Constants.bid;
    }

    /**
     * 设置bid
     *
     * @return
     */
    public static void setBid(int bid) {
        MmkvUtil.saveInt(Constants.SPUtils.BID, bid);
        Constants.bid = bid;
    }

    /**
     * 获取用户激活时间
     *
     * @return
     */
    public static long getUserActive() {
        if (Constants.userActive > 0) {
            return Constants.userActive;
        }
        Constants.userActive = MmkvUtil.getLong(Constants.SPUtils.USER_ACTIVE, -1);
        return Constants.userActive;
    }

    /**
     * 设置用户激活时间
     *
     * @param userActive
     */
    public static void setUserActive(long userActive) {
        MmkvUtil.saveLong(Constants.SPUtils.USER_ACTIVE, userActive);
        Constants.userActive = userActive;
    }

    /**
     * 设置经度
     *
     * @param longitude
     */
    public static void setLongitude(String longitude) {
        MmkvUtil.saveString(Constants.SPUtils.LONGITUDE, longitude);
        Constants.longitude = longitude;
    }

    /**
     * 获取经度
     *
     * @return
     */
    public static String getLongitude() {
        if (!TextUtils.isEmpty(Constants.longitude)) {
            return Constants.longitude;
        }
        Constants.longitude = MmkvUtil.getString(Constants.SPUtils.LONGITUDE, "");
        return Constants.longitude;
    }

    /**
     * 设置纬度
     */
    public static void setLatitude(String latitude) {
        MmkvUtil.saveString(Constants.SPUtils.LATITUDE, latitude);
        Constants.latitude = latitude;
    }

    /**
     * 获取纬度
     *
     * @return
     */
    public static String getLatitude() {
        if (!TextUtils.isEmpty(Constants.latitude)) {
            return Constants.latitude;
        }
        Constants.latitude = MmkvUtil.getString(Constants.SPUtils.LATITUDE, "");
        return Constants.latitude;
    }

    /**
     * 设置业务线标识
     *
     * @param productName
     */
    public static void setProductName(String productName) {
        Constants.productName = productName;
    }

    /**
     * 获取业务线标识
     *
     * @return
     */
    public static String getProductName() {
        return Constants.productName;
    }

    /**
     * 根据业务线获取app name
     *
     * @return
     */
    public static String getProductAppName() {
        if (TextUtils.isEmpty(Constants.productName)) {
            return "未知";
        }
        String appName = "";
        switch (Constants.productName) {
            case "12":
                appName = "吉日历";
                break;
            case "13":
                appName = "即刻天气";
                break;
            case "131":
                appName = "知心天气";
                break;
            case "17":
                appName = "玲珑视频";
                break;
            case "18":
                appName = "悟空清理";
                break;
            case "181":
                appName = "清理管家极速版";
                break;
            case "19":
                appName = "最来电";
                break;
        }
        return appName;
    }

    /**
     * 获取本地配置信息
     *
     * @return
     */
    public static List<ConfigBean.AdListBean> getAdsInfoslist() {
        if (!CollectionUtils.isEmpty(adsInfoslist)) {
            return adsInfoslist;
        }
        String adInfo = MmkvUtil.getString(Constants.SPUtils.CONFIG_INFO, "");
        if (TextUtils.isEmpty(adInfo)) {
            return adsInfoslist;
        }
        ConfigBean configBean = JsonUtils.decode(adInfo, ConfigBean.class);
        adsInfoslist = configBean.getAdList();
        return adsInfoslist;
    }

    /**
     * 设置本地配置信息
     *
     * @param configBean
     */
    public static void setAdsInfoslist(ConfigBean configBean) {
        if (configBean == null) {
            return;
        }
        adsInfoslist.clear();
        adsInfoslist.addAll(configBean.getAdList());
        String configInfo = new Gson().toJson(configBean);
        LogUtils.i("GeekSdk--configInfo---" + configInfo);
        MmkvUtil.saveString(Constants.SPUtils.CONFIG_INFO, configInfo);
        //以position_id作为key独立保存单个ad_infp
        for (ConfigBean.AdListBean bean : adsInfoslist) {
            MmkvUtil.saveString(Constants.SPUtils.CONFIG_INFO + "_" + bean.getAdPosition(), new Gson().toJson(bean));
        }
//        //初始化穿山甲sdk
//        String csjAppId = "";
//        for (ConfigBean.AdListBean adListBean : adsInfoslist) {
//            List<ConfigBean.AdListBean.AdsInfosBean> adsInfosBeans = adListBean.getAdsInfos();
//            for (ConfigBean.AdListBean.AdsInfosBean adsInfosBean : adsInfosBeans) {
//                adsInfosBean.getAdUnion()
//            }
//        }
    }


}
