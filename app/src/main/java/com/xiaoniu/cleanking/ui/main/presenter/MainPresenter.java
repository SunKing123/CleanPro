package com.xiaoniu.cleanking.ui.main.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.comm.jksdk.http.base.BaseResponse;
import com.geek.push.GeekPush;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.bean.PopupWindowType;
import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.localpush.LocalPushConfigModel;
import com.xiaoniu.cleanking.ui.localpush.LocalPushType;
import com.xiaoniu.cleanking.ui.localpush.RomUtils;
import com.xiaoniu.cleanking.ui.login.bean.LoginDataBean;
import com.xiaoniu.cleanking.ui.login.bean.UserInfoBean;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.main.bean.IconsEntity;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.InsideAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.Patch;
import com.xiaoniu.cleanking.ui.main.bean.PushSettingList;
import com.xiaoniu.cleanking.ui.main.bean.RedPacketEntity;
import com.xiaoniu.cleanking.ui.main.bean.WeatherForecastResponseEntity;
import com.xiaoniu.cleanking.ui.main.bean.weatherdao.LocationCityInfo;
import com.xiaoniu.cleanking.ui.main.bean.weatherdao.WeatherCity;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.ui.weather.activity.WeatherForecastActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.FileUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.PermissionUtils;
import com.xiaoniu.cleanking.utils.net.Common2Subscriber;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.CommonSubscriber;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;
import com.xiaoniu.cleanking.utils.update.UpdateUtil;
import com.xiaoniu.cleanking.utils.user.UserHelper;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.ContextUtils;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xnad.sdk.ad.entity.AdInfo;
import com.xnad.sdk.ad.listener.AbsAdCallBack;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

//import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * Created by tie on 2017/5/15.
 */
public class MainPresenter extends RxPresenter<MainActivity, MainModel> implements AMapLocationListener {

    private final RxAppCompatActivity mActivity;

    private UpdateAgent mUpdateAgent;

    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;

    @Inject
    public MainPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    /**
     * 游客登录
     */
    public void visitorLogin() {//冷启 热启都是需要调游客登录，即使已登录
//        if (UserHelper.init().isLogin()) {//已经登录跳过
//            return;
//        }
        if (!UserHelper.init().checkUserToken()) {//掉登状态
            return;
        }
        Gson gson = new Gson();
        Map<String, Object> paramsMap = new HashMap<>();
        if (UserHelper.init().isWxLogin()) {
            paramsMap.put("userType", 1);
            paramsMap.put("openId", UserHelper.init().getOpenID());
        } else {
            paramsMap.put("userType", 2);
            paramsMap.put("openId", AndroidUtil.getDeviceID());
        }
        String json = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mModel.visitorLogin(body, new CommonSubscriber<LoginDataBean>() {
            @Override
            public void getData(LoginDataBean loginDataBean) {
                UserInfoBean infoBean = loginDataBean.getData();
                if (infoBean != null) {
//                    infoBean.userType = 2;
                    UserHelper.init().saveUserInfo(infoBean);
                }
            }

            @Override
            public void showExtraOp(String message) {

            }

            @Override
            public void netConnectError() {

            }
        });
    }

    /**
     * 版本更新
     */
    public void queryAppVersion() {
        mModel.queryAppVersion(new Common4Subscriber<AppVersion>() {

            @Override
            public void getData(AppVersion updateInfoEntity) {
                setAppVersion(updateInfoEntity);
            }

            @Override
            public void showExtraOp(String code, String message) {
                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                checkAdviceOrRedPacketDialog();
            }

            @Override
            public void showExtraOp(String message) {
//                checkAdviceOrRedPacketDialog();
            }

            @Override
            public void netConnectError() {

            }
        });
    }


    /**
     * 激活极光
     */
    public void commitJPushAlias() {
        if (!PreferenceUtil.getIsSaveJPushAliasCurrentVersion(AppApplication.getInstance())) {
            GeekPush.clearAllTag();
            new Handler().postDelayed(() -> {
                LogUtils.e("======极光正在注册===");
                // GeekPush.bindAlias(DeviceUtils.getUdid());
                GeekPush.addTag(Constant.APP_NAME + "_" + BuildConfig.VERSION_CODE);
                GeekPush.addTag(BuildConfig.PUSH_TAG);//区分推送环境
                mModel.commitJPushAlias(new Common4Subscriber<BaseEntity>() {
                    @Override
                    public void showExtraOp(String code, String message) {

                    }

                    @Override
                    public void getData(BaseEntity baseEntity) {
                        LogUtils.e("======极光注册成功===");
                        PreferenceUtil.saveJPushAliasCurrentVersion(true);
                    }

                    @Override
                    public void showExtraOp(String message) {

                    }

                    @Override
                    public void netConnectError() {

                    }
                });

            }, 5000);

        }

    }

    /**
     * 操作记录(PUSH消息)
     *
     * @param type（1-立即清理 2-一键加速 3-手机清理 4-文件清理 5-微信专清 6-手机降温 7-qq专清）
     */
    public void commitJpushClickTime(int type) {
        mModel.commitJPushClickTime(type, new Common4Subscriber<BaseEntity>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(BaseEntity baseEntity) {

            }

            @Override
            public void showExtraOp(String message) {

            }

            @Override
            public void netConnectError() {

            }
        });
    }

    static class LoadFileTask implements Runnable {
        private Patch result;
        private WeakReference<Context> weakReference;
        private UpdateUtil.PatchCallback callback;

        public LoadFileTask(Context context, final Patch result, UpdateUtil.PatchCallback callback) {
            this.result = result;
            weakReference = new WeakReference<>(context);
            this.callback = callback;
        }

        @Override
        public void run() {
            Activity activity = (RxAppCompatActivity) weakReference.get();
//            UpdateUtil.loadFile(activity, result.getData().getPatchUrl(), result.getData().getPatchEncryption(), callback);
        }
    }

    //音乐文件
    private Set<String> cachesMusicFiles = new HashSet<>();
    //apk文件
    private Set<String> cachesApkFies = new HashSet<>();
    //视频文件
    private Set<String> cachesVideo = new HashSet<>();

    /**
     * 文件缓存
     */
    public void saveCacheFiles() {

        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            try {
                //storage/emulated/0  内置SD卡路径下
                final String path = Environment.getExternalStorageDirectory().getPath();
                //scanMusicFile(path);
                scanAllFile(path);
                queryAllMusic();
                emitter.onNext("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String value) {
                        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putStringSet(SpCacheConfig.CACHES_KEY_MUSCI, cachesMusicFiles);
                        editor.putStringSet(SpCacheConfig.CACHES_KEY_APK, cachesApkFies);
                        editor.putStringSet(SpCacheConfig.CACHES_KEY_VIDEO, cachesVideo);
                        editor.commit();

                        mView.onScanFileSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

    private void scanAllFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] f = file.listFiles();
            if (null != f) {
                for (File file1 : f) {
                    String fileName = file1.getName().toLowerCase();
                    if (file1.isDirectory()) {
                        scanAllFile(path + "/" + file1.getName());
                    } else if (fileName.endsWith(".mp4") && file1.length() != 0) {
                        cachesVideo.add(file1.getPath());
                    } else if (fileName.endsWith(".mp3") && file1.length() != 0) {
                        //cachesMusicFiles.add(file1.getPath());
                    } else if (fileName.endsWith(".apk")) {
                        cachesApkFies.add(file1.getPath());
                    }
                }
            }
        }
    }

    private void queryAllMusic() {
        Cursor cursor = mActivity.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA}, null
                , null, null);

        try {
            //solve umeng error -> Caused by: java.lang.OutOfMemoryError
            while (cursor.moveToNext()) {
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                File file = new File(url);
                if (null != file) {
                    cachesMusicFiles.add(file.getPath());
                }
            }
        } catch (OutOfMemoryError e) {
        }

        try {
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAppVersion(AppVersion result) {
        if (result != null && result.getData() != null) {
            PreferenceUtil.saveHaseUpdateVersion(result.getData().isPopup);
            if (result.getData().isPopup) {
                if (mUpdateAgent == null) {
                    mUpdateAgent = new UpdateAgent(mActivity, result, () -> {
                    });
                    mUpdateAgent.check();
                } else {
                    mUpdateAgent.check();
                }
            }
        }
        checkAdviceOrRedPacketDialog();
    }

    /*
     * 从服务端获取本地推送的配置
     */

    public void getLocalPushConfigFromServer() {
        mModel.getLocalPushConfigFromServer(new Common4Subscriber<LocalPushConfigModel>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(LocalPushConfigModel localPushConfigModel) {
                List<LocalPushConfigModel.Item> dataList = localPushConfigModel.getData();
                if (dataList != null && dataList.size() > 0) {
                    List<LocalPushConfigModel.Item> pushConfigList = new ArrayList<>();


                    List<Integer> onlyCode = new ArrayList<>(4);
                    onlyCode.add(LocalPushType.TYPE_NOW_CLEAR);
                    onlyCode.add(LocalPushType.TYPE_SPEED_UP);
                    onlyCode.add(LocalPushType.TYPE_PHONE_COOL);
                    onlyCode.add(LocalPushType.TYPE_SUPER_POWER);

                    for (LocalPushConfigModel.Item item : dataList) {
                        if (onlyCode.contains(item.getOnlyCode())) {
                            pushConfigList.add(item);
                        }
                    }



                    /*-------------测试专用 start----------------*/

                  /*  pushConfigList.clear();
                    LocalPushConfigModel.Item item = new LocalPushConfigModel.Item();
                    item.setOnlyCode(LocalPushType.TYPE_SPEED_UP);
                    item.setIconUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1591777551401&di=7c53ecd102576214fee3076839555207&imgtype=0&src=http%3A%2F%2Fa3.att.hudong.com%2F68%2F61%2F300000839764127060614318218_950.jpg");
                    item.setDailyLimit(3);
                    //阈值 降温和省电专用
                    item.setThresholdNum(10);
                    item.setFunctionUsedInterval(1);
                    item.setPopWindowInterval(1);
                    item.setTitle("手机内存占用#快假数据的的的的假数据的");
                    item.setContent("假数据的content假数据的content假数据的content假数据的content");
                    pushConfigList.add(item);
*/



                    /*-------------测试专用 end----------------*/


                    //将从服务器获取的本地推送配置信息保存在SP中
                    PreferenceUtil.saveLocalPushConfig(new Gson().toJson(pushConfigList));
                }

                //限制华为设置启动包活；
//                if (Build.MANUFACTURER.toLowerCase().contains("huawei")) {
                    //启动保活进程
                    mView.start();
//                }
            }

            @Override
            public void showExtraOp(String message) {
                ToastUtils.showShort(message);
            }

            @Override
            public void netConnectError() {

            }
        });
    }

    /**
     * 本地Push配置
     */
    @Deprecated
    public void getPushSetList() {
        mModel.getLocalPushSet(new Common4Subscriber<PushSettingList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(PushSettingList pushSettingList) {
                List<PushSettingList.DataBean> list = pushSettingList.getData();
                if (list != null && list.size() > 0) {
                    //添加通知栏类型_作为状态栏更新条件
                    /* /**
                     * code : push_1
                     * title : 垃圾清理
                     * content : 垃圾过多严重影响手机使用
                     * position : 立即清理页面
                     * url : cleankingmajor://com.xiaoniu.cleanking/native?name=main&main_index=4
                     * thresholdSign : 1
                     * thresholdNum : 200
                     * interValTime : 2
                     * dailyLimit : 12
                     */
                    PushSettingList.DataBean dataBean = new PushSettingList.DataBean();
                    dataBean.setCodeX("push10");//通知栏类型
                    dataBean.setTitle("通知栏");
                    dataBean.setContent("通知栏");
                    // dataBean.setUrl(SchemeConstant.LocalPushScheme.SCHEME_NOTIFY_ACTIVITY);

                    dataBean.setThresholdNum(60);
                    dataBean.setInterValTime(60);//每个小时监测
                    dataBean.setLastTime(0);
                    pushSettingList.getData().add(dataBean);
                    PreferenceUtil.saveCleanLog(new Gson().toJson(pushSettingList.getData()));
                } else {//网络配置异常时读取本地
                    PreferenceUtil.saveCleanLog(FileUtils.readJSONFromAsset(mActivity, "action_log.json"));
                }
            }

            @Override
            public void showExtraOp(String message) {

            }

            @Override
            public void netConnectError() {

            }
        });
    }

    //显示内部插屏广告
    public void showInsideScreenDialog(String appID) {
        if (mActivity == null || TextUtils.isEmpty(appID)) {
            return;
        }
        if (!mActivity.hasWindowFocus())
            return;
        StatisticsUtils.customTrackEvent("ad_request_sdk", "内部插屏广告发起请求", "", "inside_advertising_ad_page");
        AdRequestParams params = new AdRequestParams.Builder()
                .setActivity(mActivity).setAdId(appID).build();
        MidasRequesCenter.requestAd(params, new AbsAdCallBack() {
            @Override
            public void onAdShow(AdInfo adInfo) {
                super.onAdShow(adInfo);
                LogUtils.e("====首页内部插屏广告展出======");
            }
        });

    }

    /**
     * 判断广告弹窗和红包弹窗
     */
    public void checkAdviceOrRedPacketDialog() {
        if (!mActivity.hasWindowFocus())//未获取焦点
            return;
        if (!AppUtils.checkStoragePermission(mActivity))//未授权谭庄
            return;
        //展示内部插屏广告
        if (null != mActivity && null != AppHolder.getInstance().getInsertAdSwitchMap() && !PreferenceUtil.isHaseUpdateVersion()) {
            InsertAdSwitchInfoList.DataBean dataBean = AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_NEIBU_SCREEN);
            LogUtils.e("======databean:" + new Gson().toJson(dataBean));
            if (dataBean != null && dataBean.isOpen()) {//内部插屏广告
                if (!TextUtils.isEmpty(dataBean.getInternalAdRate()) && dataBean.getInternalAdRate().contains(",")) {
                    List<String> internalList = Arrays.asList(dataBean.getInternalAdRate().split(","));
                    InsideAdEntity inside = PreferenceUtil.getColdAndHotStartCount();
                    int startCount = inside.getCount();
                    LogUtils.e("=======count:" + startCount);
                    if (internalList.contains(String.valueOf(startCount))) {
                        showInsideScreenDialog(MidasConstants.MAIN_INSIDE_SCREEN_ID);
                        return;
                    }


                }
            }

        }
        if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_3G
                || NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_2G
                || NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO)
            return;
        RedPacketEntity pushSettingList = AppHolder.getInstance().getPopupDataEntity();
        RedPacketEntity.DataBean data = AppHolder.getInstance().getPopupDataFromListByType(pushSettingList, PopupWindowType.POPUP_RED_PACKET);
        if (data != null) {
            mView.getRedPacketListSuccess(data);
        }

    }

    /**
     * 底部icon
     */
    public void getIconList() {
        mModel.getIconList(new Common4Subscriber<IconsEntity>() {
            @Override
            public void showExtraOp(String code, String message) {
            }

            @Override
            public void getData(IconsEntity iconsEntity) {
                AppHolder.getInstance().setIconsEntityList(iconsEntity);
                mView.getIconListSuccess(iconsEntity);
            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
            }
        });
    }


    //获取本地推送弹框权限
    @SuppressLint("CheckResult")
    public void requestPopWindowPermission() {
        if (mView == null) {
            return;
        }
        if (!RomUtils.checkFloatWindowPermission(mActivity)) {
            new AlertDialog.Builder(mActivity).setTitle("提示").setMessage("").setMessage("您的手机没有授予悬浮窗权限，请开启后再试")
                    .setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RomUtils.applyPermission(mActivity, () -> new Handler().postDelayed(() -> {
                                if (!RomUtils.checkFloatWindowPermission(mActivity)) {
                                    // 授权失败
                                    ToastUtils.showShort("PopWindow授权失败");
                                } else {
                                    //授权成功
                                    ToastUtils.showShort("PopWindow推送授权成功");
                                }
                            }, 2000));
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }


    /**
     * 插屏广告开关
     */
    public void getScreenSwitch() {
        mModel.getScreentSwitch(new Common4Subscriber<InsertAdSwitchInfoList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(InsertAdSwitchInfoList switchInfoList) {
                AppHolder.getInstance().setInsertAdSwitchInfoList(switchInfoList);
            }

            @Override
            public void showExtraOp(String message) {

            }

            @Override
            public void netConnectError() {

            }
        });
    }

    /***
     * 获取弹窗信息
     */
    public void getPopupData() {
        mModel.getRedPacketListFromServer(new Common4Subscriber<RedPacketEntity>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(RedPacketEntity pushSettingList) {
                AppHolder.getInstance().setPopupDataEntity(pushSettingList);
            }

            @Override
            public void showExtraOp(String message) {

            }

            @Override
            public void netConnectError() {

            }
        });


    }


    /*--*/

    //获取定位权限
    @SuppressLint("CheckResult")
    public void requestLocationPermission() {
        if (mView == null) {
            return;
        }

        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
        if (null == mView) return;
        new RxPermissions(mView).request(permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    //开始
                    if (mView == null)
                        return;
                    requestLocation();
                } else {
                    if (mView == null)
                        return;
                    if (PermissionUtils.hasPermissionDeniedForever(mView, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        //永久拒绝权限
                        PreferenceUtil.getInstants().saveInt("isGetWeatherInfo", 0);
                    } else {
                        //拒绝权限
                        PreferenceUtil.getInstants().saveInt("isGetWeatherInfo", 0);
                    }
                }
            }
        });
    }

    /**
     * 执行定位操作
     */
    @Deprecated
    public void requestLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(ContextUtils.getApplication());
        //设置定位回调监听
        mLocationClient.setLocationListener(MainPresenter.this);
        mLocationOption = new AMapLocationClientOption();

        AMapLocationClientOption option = new AMapLocationClientOption();
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(option);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            //todo
//            mLocationClient.stopLocation();
//            mLocationClient.startLocation();
        }
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
//        StringBuffer sb = new StringBuffer();
        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        if (location.getErrorCode() == 0) {//errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
            //获取到地理位置后关掉定位
            mLocationClient.stopLocation();
            String province = location.getProvince();
            String city = location.getCity();
            String district = location.getDistrict();
            String latitude = String.valueOf(location.getLatitude());
            String longitude = String.valueOf(location.getLongitude());

            LocationCityInfo cityInfo = new LocationCityInfo(longitude,
                    latitude,
                    location.getCountry(),
                    province,
                    city,
                    district,
                    location.getStreet(),
                    location.getPoiName(),
                    location.getAoiName(),
                    location.getAddress()
            );
            if (!TextUtils.isEmpty(city))
                PreferenceUtil.getInstants().save("city", city);
            dealLocationSuccess(cityInfo);
        } else {
            PreferenceUtil.getInstants().saveInt("isGetWeatherInfo", 0);
        }
    }


    private void dealLocationSuccess(LocationCityInfo locationCityInfo) {

        if (locationCityInfo == null) {
            return;
        }
        WeatherCity weatherCity = requestUpdateTableLocation(locationCityInfo);
        String positionArea = "";
        if (!TextUtils.isEmpty(locationCityInfo.getAoiName())) {
            //高德
            positionArea = locationCityInfo.getDistrict() + locationCityInfo.getAoiName();

        }
        uploadPositionCity(weatherCity, locationCityInfo.getLatitude(), locationCityInfo.getLongitude(), positionArea);
    }


    /**
     * 用户定位信息上报
     *
     * @param positionCity 定位城市
     * @param latitude
     * @param longitude
     * @param positionArea 定位城市的详细地址，如“申江路...”
     */
    public void uploadPositionCity(@NonNull WeatherCity positionCity, @NonNull String latitude,
                                   @NonNull String longitude, @NonNull String positionArea) {
        LogUtils.d("uploadPositionCity");
        if (positionCity == null) {
            return;
        }

        if (mModel == null || mView == null) {
            return;
        }
        if (AndroidUtil.isFastDoubleClick()) {
            return;
        }
        requestWeatherVideo(positionCity.getAreaCode());
    }

    /**
     * 获取天气视频
     *
     * @param areaCode
     */
    public void requestWeatherVideo(String areaCode) {
        String cacheData = MmkvUtil.getString(Constant.DEFAULT_WEATHER_VIDEO_INFO, "");
        if (!TextUtils.isEmpty(cacheData)) {
            WeatherForecastResponseEntity cacheBean = new Gson().fromJson(cacheData, WeatherForecastResponseEntity.class);
            if (null != cacheBean && (System.currentTimeMillis() - cacheBean.getResponseTime()) < 60 * 60 * 1000) {//一小时内用缓存数据
                WeatherForecastActivity.launch(mActivity, cacheBean, cacheBean.getPublishSource());
                return;
            }
        }
        mModel.getWeatherVideo(areaCode, new Common2Subscriber<BaseResponse<WeatherForecastResponseEntity>>() {
            @Override
            public void getData(BaseResponse<WeatherForecastResponseEntity> entityBaseResponse) {
                try {
                    LogUtils.i("zz--" + new Gson().toJson(entityBaseResponse));
                    WeatherForecastResponseEntity weatherForecastResponseEntity = entityBaseResponse.getData();
                    weatherForecastResponseEntity.setResponseTime(System.currentTimeMillis());
                    MmkvUtil.saveString(Constant.DEFAULT_WEATHER_VIDEO_INFO, new Gson().toJson(weatherForecastResponseEntity));
                    WeatherForecastActivity.launch(mActivity, entityBaseResponse.getData(), entityBaseResponse.getData().getPublishSource());
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void netConnectError() {

            }
        });
    }

    /************************************* 定位相关 **************************************/
    /**
     * 定位成功之后调用该方法，更新数据库表定位状态，处理完成后回调 updateTableLocationComplete
     *
     * @param locationCityInfo
     */
    public WeatherCity requestUpdateTableLocation(LocationCityInfo locationCityInfo) {
        if (mModel == null || mView == null) {
            return null;
        }
        WeatherCity weatherCity = mModel.updateTableLocation(locationCityInfo);
        if (weatherCity != null) {
            String detailAddress;
            if (!TextUtils.isEmpty(locationCityInfo.getAoiName())) {
                //高德,优先用aoi
                detailAddress = locationCityInfo.getAoiName();
            } else if (!TextUtils.isEmpty(locationCityInfo.getPoiName())) {
                //高德,再用poi
                detailAddress = locationCityInfo.getPoiName();
            } else {
                //百度，用街道地址
                detailAddress = locationCityInfo.getStreet();
            }
            weatherCity.setDetailAddress(detailAddress);
        }
        return weatherCity;
    }


}
