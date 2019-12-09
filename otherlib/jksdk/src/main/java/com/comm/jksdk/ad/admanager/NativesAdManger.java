package com.comm.jksdk.ad.admanager;//package com.comm.jksdk.ad.admanager;
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import com.comm.jksdk.ad.listener.AdListener;
//import com.comm.jksdk.ad.listener.AdManager;
//import com.comm.jksdk.ad.listener.FirstAdListener;
//import com.comm.jksdk.ad.view.CommAdView;
//import com.comm.jksdk.ad.view.chjview.CHJAdView;
//import com.comm.jksdk.ad.view.ylhview.YLHAdView;
//import com.comm.jksdk.bean.ConfigBean;
//import com.comm.jksdk.config.AdsConfig;
//import com.comm.jksdk.config.InitBaseConfig;
//import com.comm.jksdk.constant.Constants;
//import com.comm.jksdk.http.utils.LogUtils;
//import com.comm.jksdk.utils.SpUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * 原生广告管理器
// *
// * @author: docking
// * @date: 2019/9/7 10:22
// * @description: todo ...
// **/
//public class NativesAdManger implements AdManager {
//    protected final String TAG = "GeekAdSdk-->";
//    private Context mContext;
//    private CommAdView mAdView = null;
//    private List<ConfigBean.AdListBean.AdsInfosBean> adsInfoslist = new ArrayList();
//
//    private static NativesAdManger instance;
//    private RelativeLayout adParentView;
//
//    public NativesAdManger() {
//    }
//
//    public static NativesAdManger getInstance() {
//        if (instance == null) {
//            synchronized (NativesAdManger.class) {
//                if (instance == null) {
//                    instance = new NativesAdManger();
//                }
//            }
//        }
//        return instance;
//    }
//
//
//    /**
//     * 广告ID
//     */
//    private String mAdId;
//    /**
//     * 广告监听器
//     */
//    private AdListener mAdListener;
//
//    /**
//     * 广告样式
//     */
//    private String adStyle = "";
//    private int adRequestTimeOut;
//    /**
//     * 广告类型
//     */
//    private String adType;
//    /**
//     * 默认配置Key
//     */
//    private String defaultConfigKey = "";
//
//    /**
//     * 请求方式：0 - SDK 1 - API
//     */
//    private int requestType = 0;
//
//    private boolean firstRequestAd = true;
//
//    public NativesAdManger setDefaultConfigKey(String defaultConfigKey) {
//        this.defaultConfigKey = defaultConfigKey;
//        return this;
//    }
//
//    public NativesAdManger setCity(String city) {
//        Constants.city = city;
//        return this;
//    }
//
//    public NativesAdManger setProvince(String province) {
//        Constants.province = province;
//        return this;
//    }
//
//    public NativesAdManger setLongitude(String longitude) {
//        Constants.longitude = longitude;
//        return this;
//    }
//
//    public NativesAdManger setLatitude(String latitude) {
//        Constants.latitude = latitude;
//        return this;
//    }
//
//    public NativesAdManger setUserActive(Long userActive) {
//        Constants.userActive = userActive;
//        return this;
//    }
//
//    public NativesAdManger setMarketName(String marketName) {
//        Constants.marketName = marketName;
//        return this;
//    }
//
//    public NativesAdManger setProductName(String productName) {
//        Constants.productName = productName;
//        return this;
//    }
//
//    public NativesAdManger setBid(int bid) {
//        Constants.bid = bid;
//        return this;
//    }
//
//    public NativesAdManger setContext(Context context) {
//        this.mContext = context;
//        Constants.mContext = mContext;
//        return this;
//    }
//
//
//    public NativesAdManger setAdPositionId(String adPositionId) {
//
//        Constants.adPositionId = adPositionId;
//        return this;
//    }
//
//    public NativesAdManger setAdListener(AdListener adListener) {
//        this.mAdListener = adListener;
//        return this;
//    }
//
//    /**
//     * 获取本地配置信息
//     *
//     * @return
//     */
//    public NativesAdManger getConfig() {
//        //获取本地配置信息
//        ConfigBean.AdListBean mConfigInfoBean = AdsConfig.getInstance(mContext).getConfig(Constants.adPositionId);
//
//        if (mConfigInfoBean != null) {
//            if (Constants.adPositionId.equals(mConfigInfoBean.getAdPosition())) {
//                //当前广告位所对应的配置信息 存储到curAdlist
//                adStyle = mConfigInfoBean.getAdStyle();
//                adRequestTimeOut = mConfigInfoBean.getAdRequestTimeOut();
//
//                adsInfoslist.clear();
//                adsInfoslist.addAll(mConfigInfoBean.getAdsInfos());
//
//            }
//
//
//        }
//        // TODO: 2019/9/25 从缓存中取出数据
//        getCacheConfig();
//
//        return this;
//    }
//
//    /**
//     * 创建广告View
//     *
//     * @param adType 广告样式
//     */
//    private void createAdView(String adType, String mAdId) {
//
//        if (Constants.AdType.ChuanShanJia.equals(adType)) {
//            mAdView = new CHJAdView(mContext, adStyle,"",  mAdId);
//        } else if (Constants.AdType.YouLiangHui.equals(adType)) {
//            mAdView = new YLHAdView(mContext, adStyle,"", mAdId);
//        } else {
//            // 暂不处理
//        }
//
//        if (mAdView != null && mAdListener != null) {
//            //向客户端提供接口
//            mAdView.setAdListener(mAdListener);
//            //ylh请求失败请求chj广告接口回掉
//            mAdView.setPollingAdListener(mFirstAdListener);
//        }
//
//        adParentView.removeAllViews();
//        adParentView.addView(mAdView);
//        requestAd();
//    }
//
//
//    /**
//     * 请求广告
//     */
//    private void requestAd() {
//        if (mAdView != null) {
//            //第一次请求广告保存请求时间
//            if (firstRequestAd) {
//                Long curTime = System.currentTimeMillis();
//                SpUtils.putLong(Constants.SPUtils.FIRST_REQUEST_AD_TIME, curTime);
//            }
//            mAdView.requestAd(requestType, adRequestTimeOut);
//        }
//    }
//
//    @Override
//    public RelativeLayout getAdView() {
//        return adParentView;
//    }
//
//    private FirstAdListener mFirstAdListener = new FirstAdListener() {
//        @Override
//        public void firstAdError(int errorCode, String errorMsg) {
//            LogUtils.w(TAG, "回传--->请求第一个广告失败");
//            firstRequestAd = false;
//            getCacheConfig();
//
//        }
//    };
//
//    /**
//     * 获取默认广告配置
//     */
//    private void getCacheConfig() {
//        // TODO: 2019/9/25 从缓存中取出下一个广告配置
//        // TODO: 2019/9/25 如果不存在数据，则不轮循
//        if (adsInfoslist != null && adsInfoslist.size() > 0) {
//            ConfigBean.AdListBean.AdsInfosBean mAdsInfosBean = adsInfoslist.remove(0);
//            if (mAdsInfosBean != null) {
//                adType = mAdsInfosBean.getAdUnion();
//                mAdId = mAdsInfosBean.getAdId();
//
//                requestType = mAdsInfosBean.getRequestType();
//                if (!TextUtils.isEmpty(adType)) {
//                    if (adType.equals(Constants.AdType.YouLiangHui)) {
////                        Constants.YLH_APPID = mAdsInfosBean.getAdsAppId();
////                        Constants.YLH_APPNAME = mAdsInfosBean.getAdsAppName();
//                        //保存优量汇广告APPID  APPNAME
//                        SpUtils.putString(Constants.SPUtils.YLH_APPID, mAdsInfosBean.getAdsAppId());
//                        SpUtils.putString(Constants.SPUtils.YLH_APPNAME, mAdsInfosBean.getAdsAppName());
//                    } else {
////                        Constants.CHJ_APPID = mAdsInfosBean.getAdsAppId();
////                        Constants.CHJ_APPNAME = mAdsInfosBean.getAdsAppName();
//                        // 保存穿山甲广告APPID  APPNAME
//                        SpUtils.putString(Constants.SPUtils.CHJ_APPID, mAdsInfosBean.getAdsAppId());
//                        SpUtils.putString(Constants.SPUtils.CHJ_APPNAME, mAdsInfosBean.getAdsAppName());
//                    }
//                    //创建广告样式
//                    if (TextUtils.isEmpty(mAdId)) {
//                        LogUtils.w(TAG, "广告id为空，请检查");
//                        Toast.makeText(mContext, "广告id为空，请检查", Toast.LENGTH_SHORT).show();
//
//                        return;
//                    }
//                    if (TextUtils.isEmpty(adType)) {
//                        LogUtils.w(TAG, "广告adType为空，请检查");
//                        Toast.makeText(mContext, "广告类型为空，请检查", Toast.LENGTH_SHORT).show();
//
//                        return;
//                    }
//                    if (TextUtils.isEmpty(adStyle)) {
//                        Toast.makeText(mContext, "广告样式为空，请检查", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    createAdView(adType, mAdId);
//
//
//                }
//            }
//        } else {
//            Toast.makeText(mContext, "第一个广告请求失败，后台配置了一个广告源，至少两个广告源，请检查？", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    /**
//     * 请求优量汇广告
//     */
//    public NativesAdManger build() {
//        adParentView = new RelativeLayout(mContext);
//        firstRequestAd = true;
//        getConfig();
//        return this;
//    }
//
//    /**
//     * 从cms请求广告配置
//     */
//    public void requestConfig() {
//        if (mContext == null) {
//            return;
//        }
//        AdsConfig.getInstance(mContext).requestConfig();
//    }
//
//
//    /**
//     * 初始化SDK
//     */
//    public void init(Context mContext, String chjAppId, String chjAppName) {
//        if (mContext == null) {
//            LogUtils.w(TAG, "初始化SDK时Context为null，请检查");
//            return;
//        }
//        if (TextUtils.isEmpty(chjAppId)) {
//            LogUtils.w(TAG, "初始化SDK时chjAppId为空，请检查");
//            return;
//        }
//        if (TextUtils.isEmpty(chjAppName)) {
//            LogUtils.w(TAG, "初始化SDK时chjAppName为空，请检查");
//            return;
//        }
//        Constants.mContext = mContext;
//        Constants.CHJ_APPID = chjAppId;
//        Constants.CHJ_APPNAME = chjAppName;
//        //初始化基本配置信息
//        InitBaseConfig.getInstance().init(mContext);
//    }
//
//    @Override
//    public void loadAd(String position, AdListener listener) {
//
//    }
//}
