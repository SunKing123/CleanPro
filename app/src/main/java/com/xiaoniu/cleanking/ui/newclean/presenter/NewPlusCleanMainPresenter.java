package com.xiaoniu.cleanking.ui.newclean.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.comm.jksdk.utils.DisplayUtil;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.base.ScanDataHolder;
import com.xiaoniu.cleanking.bean.JunkWrapper;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.AdposUtil;
import com.xiaoniu.cleanking.midas.CMAbsAdCallBack;
import com.xiaoniu.cleanking.midas.IOnAdClickListener;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.midas.VideoAbsAdCallBack;
import com.xiaoniu.cleanking.ui.login.activity.LoginWeiChatActivity;
import com.xiaoniu.cleanking.ui.main.bean.BubbleCollected;
import com.xiaoniu.cleanking.ui.main.bean.BubbleConfig;
import com.xiaoniu.cleanking.ui.main.bean.BubbleDouble;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.InteractionSwitchList;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.bean.SecondJunkInfo;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.ui.newclean.bean.GoldCoinDialogParameter;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.ui.newclean.dialog.GoldCoinDialog;
import com.xiaoniu.cleanking.ui.newclean.fragment.NewPlusCleanMainFragment;
import com.xiaoniu.cleanking.ui.newclean.model.NewScanModel;
import com.xiaoniu.cleanking.ui.newclean.util.RequestUserInfoUtil;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.CollectionUtils;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.PhoneInfoUtils;
import com.xiaoniu.cleanking.utils.net.Common3Subscriber;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.ErrorCode;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;
import com.xnad.sdk.ad.entity.AdInfo;
import com.xnad.sdk.ad.listener.AbsAdCallBack;
import com.xnad.sdk.ad.widget.TemplateView;
import com.xnad.sdk.config.AdParameter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

//import com.tbruyelle.rxpermissions2.RxPermissions;

public class NewPlusCleanMainPresenter extends RxPresenter<NewPlusCleanMainFragment, NewScanModel> {

    private FileQueryUtils mFileQueryUtils;
    private CompositeDisposable compositeDisposable;
    private LinkedHashMap<ScanningResultType, JunkGroup> mJunkGroups = new LinkedHashMap<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private AdParameter mAdParameter;
    @Inject
    NoClearSPHelper mPreferencesHelper;

    @Inject
    public NewPlusCleanMainPresenter() {
        compositeDisposable = new CompositeDisposable();
    }

    private int fileCount = 0;

    private long totalJunk = 0;

    boolean isScaning = false;   //避免重复扫描

    /**
     * 互动式广告开关
     */
    public void getInteractionSwitch() {
        //过审开关打开，直接跳过
        if (AppHolder.getInstance().getAuditSwitch()) {
            return;
        }
        mModel.getInteractionSwitch(new Common4Subscriber<InteractionSwitchList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(InteractionSwitchList switchInfoList) {
                for (InteractionSwitchList.DataBean dataBean : switchInfoList.getData()) {
                    if (TextUtils.equals(dataBean.getSwitcherKey(), "page_lock")) {
                        try {
                            MmkvUtil.saveString(PositionId.LOCK_INTERACTIVE, JSONObject.toJSONString(dataBean));
                            Log.e("dong", JSONObject.toJSONString(dataBean));
                        } catch (Exception e) {
                        }
                    }
                }
                mView.getInteractionSwitchSuccess(switchInfoList);
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
     * 获取到可以加速的应用名单Android O以下的获取最近使用情况
     */
    @SuppressLint("CheckResult")
    public void getAccessListBelow() {
//        mView.showLoadingDialog();
        Observable.create((ObservableOnSubscribe<ArrayList<FirstJunkInfo>>) e -> {
            //获取到可以加速的应用名单
            mFileQueryUtils = new FileQueryUtils();
            //文件加载进度回调
            mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
                @Override
                public void currentNumber() {

                }

                @Override
                public void increaseSize(long p0) {

                }

                @Override
                public void scanFile(String p0) {

                }
            });
            ArrayList<FirstJunkInfo> listInfo = mFileQueryUtils.getRunningProcess();
            if (listInfo == null) {
                listInfo = new ArrayList<>();
            }
            e.onNext(listInfo);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                    if (mView == null) return;
//                    mView.cancelLoadingDialog();
                    try {
                        // mView.getAccessListBelow(strings);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    public void readyScanningJunk() {
        if (isScaning == true)
            return;
        LogUtils.i("readyScanningJunk()");
        mFileQueryUtils = new FileQueryUtils();
        //初始化扫描模型
        initScanningJunkModel();
        //初始化扫描监听
        initScanningListener();

    }


    /**
     * 检查文件存贮权限
     */
    @SuppressLint("CheckResult")
    public void checkStoragePermission() {
        if (mView.getActivity() == null || mView.getActivity().isFinishing()) {
            return;
        }

        PermissionUtils.permission(PermissionConstants.STORAGE).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
                LogUtils.i("checkStoragePermission()---true");
                readyScanningJunk();
                scanningJunk();
            }

            @Override
            public void onDenied() {
                if (hasPermissionDeniedForever()) {//点击拒绝
                    LogUtils.i("checkStoragePermission()---denied");
                    mView.permissionDenied();
                } else {//点击永久拒绝
                    LogUtils.i("checkStoragePermission()---denied--faile");
                }

            }
        }).request();

    }

    /**
     * 是否有权限被永久拒绝
     */
    private boolean hasPermissionDeniedForever() {
        boolean hasDeniedForever = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M&&mView!=null) {
            if (mView.getActivity().shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                hasDeniedForever = true;
            }
        }
        return hasDeniedForever;
    }


    /**
     * 监听扫描状态
     */
    private void initScanningListener() {
        if (isScaning == true)
            return;
        if (compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }

        mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {

            @Override
            public void currentNumber() {
            }

            @Override
            public void increaseSize(long increaseSize) {
                totalJunk += increaseSize;
                mHandler.post(() -> {
                    if (mView != null)
                        mView.setScanningJunkTotal(totalJunk);
                });
            }

            @Override
            public void scanFile(String filePath) {

            }
        });
    }

    private Disposable disposable;

    @SuppressLint("CheckResult")
    public void scanningJunk() {
        if (isScaning == true)
            return;

        isScaning = true;
        disposable = Observable.create(e -> {
            try {
                fileCount = 0;
                totalJunk = 0;
                //扫描进程占用内存情况
                ArrayList<FirstJunkInfo> runningProcess = mFileQueryUtils.getRunningProcess();
                e.onNext(new JunkWrapper(ScanningResultType.MEMORY_JUNK, runningProcess));

                //扫描apk安装包
                List<FirstJunkInfo> apkJunkInfos = mFileQueryUtils.queryAPkFile();
                if (CollectionUtils.isEmpty(apkJunkInfos)) {
                    apkJunkInfos.addAll(mFileQueryUtils.queryAPkFile());
                }
                e.onNext(new JunkWrapper(ScanningResultType.APK_JUNK, apkJunkInfos));

                //扫描卸载残余垃圾
                ArrayList<FirstJunkInfo> leaveDataInfo = mFileQueryUtils.getOmiteCache();
                e.onNext(new JunkWrapper(ScanningResultType.UNINSTALL_JUNK, leaveDataInfo));

                //缓存垃圾
                HashMap<ScanningResultType, ArrayList<FirstJunkInfo>> junkResultMap = mFileQueryUtils.getFileJunkResult();
                if (!CollectionUtils.isEmpty(junkResultMap)) {
                    ArrayList<FirstJunkInfo> adJunkInfoList = junkResultMap.get(ScanningResultType.AD_JUNK);
                    ArrayList<FirstJunkInfo> cacheJunkInfoList = junkResultMap.get(ScanningResultType.CACHE_JUNK);
                    e.onNext(new JunkWrapper(ScanningResultType.CACHE_JUNK, cacheJunkInfoList));
                    e.onNext(new JunkWrapper(ScanningResultType.AD_JUNK, adJunkInfoList));
                }

                //扫描完成表示
                e.onNext("FINISH");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::dispatchScanningJunkResult);

        compositeDisposable.add(disposable);

    }

    //清除扫描任务
    public void stopScanning() {
        fileCount = 0;
        totalJunk = 0;
        isScaning = false;
        ScanDataHolder.getInstance().setScanState(0);
        if (null != disposable && !disposable.isDisposed()) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
        if (null != compositeDisposable && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
            compositeDisposable.dispose();
        }
    }

    /**
     * 分发扫描结果
     */
    private void dispatchScanningJunkResult(Object scanningResult) {

        if (scanningResult instanceof JunkWrapper) {
            JunkWrapper wrapper = (JunkWrapper) scanningResult;
            if (wrapper.type == ScanningResultType.UNINSTALL_JUNK) {
                setUninstallJunkResult(wrapper);
            } else if (wrapper.type == ScanningResultType.APK_JUNK) {
                setApkJunkResult(wrapper);
            } else if (wrapper.type == ScanningResultType.MEMORY_JUNK) {
                setMemoryJunkResult(wrapper);
            } else if (wrapper.type == ScanningResultType.CACHE_JUNK) {
                setCacheJunkResult(wrapper);
            } else if (wrapper.type == ScanningResultType.AD_JUNK) {
//                setAdJunkResult(wrapper);
            }
        }

        if (scanningResult instanceof String && "FINISH".equals(scanningResult)) {
            if (mView != null) {
                JunkGroup junkGroup = mJunkGroups.get(ScanningResultType.MEMORY_JUNK);
                if (junkGroup != null) {
                    junkGroup.isScanningOver = true;
                }
//                final List<JunkGroup> scanningModelList = new ArrayList<>(mJunkGroups.values());
                mView.setScanningFinish(mJunkGroups);
                isScaning = false;
//                getView().setInitScanningModel(scanningModelList);
//                //计算总的扫描时间，并回传记录
//                long scanningCountTime = System.currentTimeMillis() - scanningStartTime;
//                getView().setScanningCountTime(scanningCountTime);
//                //计算扫描文件总数
//                getView().setScanningFileCount(fileCount);
            }
        }

    }


    /**
     * 回传广告垃圾
     */
    private void setAdJunkResult(JunkWrapper wrapper) {
        List<FirstJunkInfo> firstJunkList = wrapper.junkInfoList;
        JunkGroup junkGroup = mJunkGroups.get(ScanningResultType.AD_JUNK);
        if (junkGroup != null) {
            for (FirstJunkInfo info : firstJunkList) {
                junkGroup.mChildren.add(info);
                junkGroup.mSize += info.getTotalSize();
                if (info.getSubGarbages() != null && info.getSubGarbages().size() > 0) {
                    for (SecondJunkInfo secondJunk : info.getSubGarbages()) {
                        fileCount += secondJunk.getFilesCount();
                    }
                }
            }
            junkGroup.isScanningOver = true;
        }
    }

    /**
     * 回传缓存垃圾
     */
    private void setCacheJunkResult(JunkWrapper wrapper) {
        List<FirstJunkInfo> firstJunkList = wrapper.junkInfoList;
        JunkGroup junkGroup = mJunkGroups.get(ScanningResultType.CACHE_JUNK);
        if (junkGroup != null) {
            for (FirstJunkInfo info : firstJunkList) {
                junkGroup.mChildren.add(info);
                junkGroup.mSize += info.getTotalSize();

                if (info.getSubGarbages() != null && info.getSubGarbages().size() > 0) {
                    for (SecondJunkInfo secondJunk : info.getSubGarbages()) {
                        fileCount += secondJunk.getFilesCount();
                    }
                }
            }
            junkGroup.isScanningOver = true;
        }
    }

    /**
     * 回传卸载残余垃圾
     */
    private void setUninstallJunkResult(JunkWrapper wrapper) {
        List<FirstJunkInfo> firstJunkList = wrapper.junkInfoList;
        JunkGroup junkGroup = mJunkGroups.get(ScanningResultType.UNINSTALL_JUNK);
        if (junkGroup != null) {
            for (FirstJunkInfo info : firstJunkList) {
                junkGroup.mChildren.add(info);
                junkGroup.mSize += info.getTotalSize();

                if (info.getSubGarbages() != null && info.getSubGarbages().size() > 0) {
                    for (SecondJunkInfo secondJunk : info.getSubGarbages()) {
                        fileCount += secondJunk.getFilesCount();
                    }
                }
            }
            junkGroup.isScanningOver = true;
        }
    }

    /**
     * 回传无用安装包垃圾
     */
    private void setApkJunkResult(JunkWrapper wrapper) {
        List<FirstJunkInfo> firstJunkList = wrapper.junkInfoList;
        JunkGroup junkGroup = mJunkGroups.get(ScanningResultType.APK_JUNK);
        if (junkGroup != null) {
            for (FirstJunkInfo info : firstJunkList) {
                junkGroup.mChildren.add(info);
                junkGroup.mSize += info.getTotalSize();
            }
            fileCount += mJunkGroups.size();
            junkGroup.isScanningOver = true;
        }
    }

    /**
     * 回传内存垃圾
     */
    private void setMemoryJunkResult(JunkWrapper wrapper) {
        List<FirstJunkInfo> firstJunkList = wrapper.junkInfoList;
        JunkGroup junkGroup = mJunkGroups.get(ScanningResultType.MEMORY_JUNK);
        if (junkGroup != null) {
            for (FirstJunkInfo info : firstJunkList) {
                junkGroup.mChildren.add(info);
                junkGroup.mSize += info.getTotalSize();
            }
        }
    }


    /**
     * 初始化扫描模型
     */
    private void initScanningJunkModel() {
        //缓存垃圾
        mJunkGroups.put(ScanningResultType.CACHE_JUNK, new JunkGroup(ScanningResultType.CACHE_JUNK.getTitle(),
                ScanningResultType.CACHE_JUNK.getType()));

        //卸载残留
        mJunkGroups.put(ScanningResultType.UNINSTALL_JUNK, new JunkGroup(ScanningResultType.UNINSTALL_JUNK.getTitle(),
                ScanningResultType.UNINSTALL_JUNK.getType()));

        //广告垃圾
        mJunkGroups.put(ScanningResultType.AD_JUNK, new JunkGroup(ScanningResultType.AD_JUNK.getTitle(),
                ScanningResultType.AD_JUNK.getType()));

        //无用安装包
        mJunkGroups.put(ScanningResultType.APK_JUNK, new JunkGroup(ScanningResultType.APK_JUNK.getTitle(),
                ScanningResultType.APK_JUNK.getType()));

        //内存加速
        mJunkGroups.put(ScanningResultType.MEMORY_JUNK, new JunkGroup(ScanningResultType.MEMORY_JUNK.getTitle(),
                ScanningResultType.MEMORY_JUNK.getType()));

    }


    public void prepareVideoAd(ViewGroup viewGroup) {
        if (viewGroup == null || mView == null || mView.getActivity() == null) {
            return;
        }
        //尝试预加载，丝滑般的体验...
        AdRequestParams params = new AdRequestParams.Builder()
                .setAdId(MidasConstants.MAIN_THREE_AD_ID).setActivity(mView.getActivity()).setViewWidthOffset(28)
                .setViewContainer(viewGroup).build();
        MidasRequesCenter.preLoad(params);
//        MidasAdSdk.getAdsManger().askIsReady(mView.getActivity(), MidasConstants.MAIN_THREE_AD_ID, result -> isReadSuccess = result);
    }

    public void fillVideoAd(ViewGroup viewGroup, IOnAdClickListener onAdClick) {
        AdRequestParams params = new AdRequestParams.Builder()
                .setAdId(MidasConstants.MAIN_THREE_AD_ID).setActivity(mView.getActivity())
                .setViewContainer(viewGroup).setViewWidthOffset(28).build();
        MidasRequesCenter.requestAd(params, new AdvCallBack(MidasConstants.MAIN_THREE_AD_ID, onAdClick));
    }

    public void showAdviceLayout(ViewGroup viewGroup, String adviceID, IOnAdClickListener onAdClick) {
        if (viewGroup == null || mView == null || mView.getActivity() == null) {
            return;
        }
        AdRequestParams params = new AdRequestParams.Builder()
                .setAdId(adviceID).setActivity(mView.getActivity())
                .setViewWidth(ScreenUtils.getScreenWidth(mView.getActivity()) - DisplayUtil.dip2px(mView.getActivity(), 28))
                .setViewContainer(viewGroup).build();

        MidasRequesCenter.requestAd(params, new AdvCallBack(adviceID, onAdClick));
    }

    static class AdvCallBack extends CMAbsAdCallBack {
        String advId;
        String title = "";
        IOnAdClickListener onAdClick;

        AdvCallBack(String advId, IOnAdClickListener onAdClick) {
            this.advId = advId;
            this.onAdClick = onAdClick;
            switch (advId) {
                case MidasConstants.MAIN_ONE_AD_ID:
                    title = "one";
                    break;
                case MidasConstants.MAIN_TWO_AD_ID:
                    title = "two";
                    break;
                case MidasConstants.MAIN_THREE_AD_ID:
                    title = "three";
                    break;
            }
        }

        @Override
        public void onAdLoadSuccess(AdInfo adInfo) {
            super.onAdLoadSuccess(adInfo);
        }

        @Override
        public void onShowError(int i, String s) {
            super.onShowError(i, s);
        }

        @Override
        public void onAdShow(AdInfo adInfo) {
            super.onAdShow(adInfo);

        }

        @Override
        public void onAdClicked(AdInfo adInfo) {
            super.onAdClicked(adInfo);
            if (onAdClick != null) {
                onAdClick.onClick(advId);
            }
        }

        @Override
        public void onAdClose(AdInfo adInfo) {
            super.onAdClose(adInfo);
        }

        @Override
        public void onAdClose(AdInfo adInfo, TemplateView templateView) {
            super.onAdClose(adInfo, templateView);
            LogUtils.e("====首页广告one====templateView:点击关闭按钮");
        }

        public void onReadyToShow(AdInfo var1) {
            var1.getAdParameter().getViewContainer().setVisibility(View.VISIBLE);
        }

    }


    //更新金币列表
    public void refBullList() {
        String auditSwitch = MmkvUtil.getString(SpCacheConfig.AuditSwitch, "0");
        if (!TextUtils.equals(auditSwitch, "1"))
            return;
        mModel.getGoleGonfigs(new Common3Subscriber<BubbleConfig>() {
            @Override
            public void showExtraOp(String code, String message) {  //关心错误码；
                ToastUtils.showShort(message);
            }

            @Override
            public void getData(BubbleConfig bubbleConfig) {
                mView.setTopBubbleView(bubbleConfig);

            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
                ToastUtils.showShort(R.string.notwork_error);
            }
        }, RxUtil.<ImageAdEntity>rxSchedulerHelper(mView));
    }


    //领取金币
    public void bullCollect(int locationNum) {
        mModel.goleCollect(new Common3Subscriber<BubbleCollected>() {
            @Override
            public void showExtraOp(String code, String message) {  //关心错误码
                if (TextUtils.equals(code, ErrorCode.LOGIN_EXCEPTION)) {
                    mView.getActivity().startActivity(new Intent(mView.getActivity(), LoginWeiChatActivity.class));
                }
                ToastUtils.showShort(message);
            }

            @Override
            public void getData(BubbleCollected bubbleConfig) {
                //实时更新金币信息
                RequestUserInfoUtil.getUserCoinInfo();
                if (null != bubbleConfig && null != bubbleConfig.getData()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("position_id", bubbleConfig.getData().getLocationNum());
                    map.put("gold_number", bubbleConfig.getData().getGoldCount());
                    StatisticsUtils.customTrackEvent("number_of_gold_coins_issued", "首页金币领取弹窗金币发放数", "home_page_gold_coin_pop_up_window", "home_page_gold_coin_pop_up_window", map);


                    mView.bubbleCollected(bubbleConfig);
                }

            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
                ToastUtils.showShort(R.string.notwork_error);
            }
        }, RxUtil.<ImageAdEntity>rxSchedulerHelper(mView), locationNum);
    }


    //金币翻倍
    public void bullDouble(String uuid, int locationNum, int goldCount, int doubledMagnification) {
        mModel.goldDouble(new Common3Subscriber<BubbleDouble>() {
            @Override
            public void showExtraOp(String code, String message) {  //关心错误码；
                ToastUtils.showShort(message);
            }

            @Override
            public void getData(BubbleDouble bubbleDouble) {
                RequestUserInfoUtil.getUserCoinInfo(); //更新UI金币信息；
                mView.bubbleDoubleSuccess(bubbleDouble, locationNum,1);
            }

            @Override
            public void showExtraOp(String message) {
                ToastUtils.showShort(message);
            }

            @Override
            public void netConnectError() {
                ToastUtils.showShort(R.string.notwork_error);
            }
        }, RxUtil.<ImageAdEntity>rxSchedulerHelper(mView), uuid, locationNum, goldCount, doubledMagnification);
    }

    //金币领取广告弹窗
    public void showGetGoldCoinDialog(BubbleCollected dataBean) {
        GoldCoinDialogParameter bean = new GoldCoinDialogParameter();
        bean.dialogType = 1;
        bean.obtainCoinCount = dataBean.getData().getGoldCount();
        bean.doubleNums = dataBean.getData().getDoubledMagnification();
        //广告位1开关控制
        if (AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_HOME_GOLD_PAGE, PositionId.DRAW_ONE_CODE)) {
            bean.adId = AdposUtil.getAdPos(dataBean.getData().getLocationNum(), 0);
            Map<String, Object> mapJson = new HashMap<>();
            mapJson.put("position_id", String.valueOf(dataBean.getData().getLocationNum()));
            StatisticsUtils.customTrackEvent("ad_request_sdk_1", "首页金币领取弹窗上广告发起请求", "home_page_gold_coin_pop_up_window", "home_page_gold_coin_pop_up_window", mapJson);
        }
        bean.isDouble = true;
        bean.isRewardOpen = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_HOME_GOLD_PAGE, PositionId.DRAW_TWO_CODE);//激励视频广告位开关
        bean.totalCoinCount = dataBean.getData().getTotalGoldCount();
        //广告回调
        bean.advCallBack = new AbsAdCallBack() {
            @Override
            public void onAdShow(AdInfo adInfo) {

            }

            @Override
            public void onAdError(AdInfo adInfo, int i, String s) {

            }
        };
        //翻倍回调
        bean.onDoubleClickListener = (v) -> {
            try {
                if (AndroidUtil.isFastDoubleBtnClick(1000)) {
                    return;
                }
                //翻倍按钮点击
                org.json.JSONObject exJson = new org.json.JSONObject();
                exJson.put("position_id", dataBean.getData().getLocationNum());
                StatisticsUtils.trackClick("double_the_gold_coin_click", "金币翻倍按钮点击", "home_page_gold_coin_pop_up_window", "home_page_gold_coin_pop_up_window", exJson);

                //翻倍视频请求
                Map<String, Object> mapJson = new HashMap<>();
                mapJson.put("position_id", String.valueOf(dataBean.getData().getLocationNum()));
                StatisticsUtils.customTrackEvent("ad_request_sdk_2", "首页翻倍激励视频广告发起请求", "home_page_gold_coin_pop_up_window", "home_page_gold_coin_pop_up_window", mapJson);

                ViewGroup viewGroup = (ViewGroup) mView.getActivity().getWindow().getDecorView();
                AdRequestParams params = new AdRequestParams.Builder().
                        setActivity(mView.getActivity()).
                        setViewContainer(viewGroup).
                        setAdId(AdposUtil.getAdPos(dataBean.getData().getLocationNum(), 1)).build();
                MidasRequesCenter.requestAdVideo(params, new VideoAbsAdCallBack() {
                    @Override
                    public void onShowError(int i, String s) {
                        ToastUtils.showLong("网络异常");
                        GoldCoinDialog.dismiss();

                    }

                    @Override
                    public void onAdError(AdInfo adInfo, int i, String s) {
                        ToastUtils.showLong("网络异常");
                        GoldCoinDialog.dismiss();
                    }

                    @Override
                    public void onAdVideoComplete(AdInfo adInfo) {
                        super.onAdVideoComplete(adInfo);
                        if (!mView.getActivity().isFinishing()) {
                            GoldCoinDialog.dismiss();
                        }
                    }

                    @Override
                    public void onAdClose(AdInfo adInfo, boolean isComplete) {
                        try {
                            org.json.JSONObject exJson = new org.json.JSONObject();
                            exJson.put("position_id", dataBean.getData().getLocationNum());
                            StatisticsUtils.trackClick("incentive_video_ad_click", "首页金币翻倍激励视频广告关闭点击", "home_page_gold_coin_pop_up_window_incentive_video_page", "home_page_gold_coin_pop_up_window_incentive_video_page", exJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!mView.getActivity().isFinishing()) {
                            if (isComplete) {
                                mView.bubbleDouble(dataBean);
                            }
                            GoldCoinDialog.dismiss();
                        }
                    }


                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        bean.closeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    org.json.JSONObject exJson = new org.json.JSONObject();
                    exJson.put("position_id", dataBean.getData().getLocationNum());
                    StatisticsUtils.trackClick("close_click", "弹窗关闭点击", "home_page_gold_coin_pop_up_window", "home_page_gold_coin_pop_up_window", exJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        //bean.adVideoId = MidasConstants.CLICK_GET_DOUBLE_COIN_BUTTON;
        bean.context = mView.getActivity();
        StatisticsUtils.customTrackEvent("home_page_gold_coin_pop_up_window_custom", "首页金币领取弹窗曝光", "home_page_gold_coin_pop_up_window", "home_page_gold_coin_pop_up_window");
        GoldCoinDialog.showGoldCoinDialog(bean);
        adPrevData(AdposUtil.getAdPos(1, 3));//位置三预加载
    }

    //金币位置预加载
    public void goldAdprev() {
        adPrevData(AdposUtil.getAdPos(1, 0));//位置一预加载
        adPrevData(AdposUtil.getAdPos(1, 1));//位置二预加载

    }

    //广告预加载
    public void adPrevData(String posId) {
        try {
            AdRequestParams params = new AdRequestParams.Builder()
                    .setAdId(posId).setActivity(mView.getActivity()).setViewWidthOffset(45).build();
            MidasRequesCenter.preLoad(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 埋点事件
     */
    private void initNiuData() {
        if (!mPreferencesHelper.isUploadImei()) {
            //有没有传过imei
            String imei = PhoneInfoUtils.getIMEI(mView.getActivity());
            LogUtils.i("--zzh--" + imei);
            if (TextUtils.isEmpty(imei)) {
                NiuDataAPI.setIMEI("");
                mPreferencesHelper.setUploadImeiStatus(false);
            } else {
                NiuDataAPI.setIMEI(imei);
                mPreferencesHelper.setUploadImeiStatus(true);
            }
        }
    }


}
