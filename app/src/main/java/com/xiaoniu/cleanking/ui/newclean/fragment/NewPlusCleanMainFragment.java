package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.comm.jksdk.utils.MmkvUtil;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.base.ScanDataHolder;
import com.xiaoniu.cleanking.constant.RouteConstants;
import com.xiaoniu.cleanking.midas.AdposUtil;
import com.xiaoniu.cleanking.midas.IOnAdClickListener;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.ui.main.activity.CleanMusicManageActivity;
import com.xiaoniu.cleanking.ui.main.activity.CleanVideoManageActivity;
import com.xiaoniu.cleanking.ui.main.activity.ImageActivity;
import com.xiaoniu.cleanking.ui.main.activity.NetWorkActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.main.bean.BubbleCollected;
import com.xiaoniu.cleanking.ui.main.bean.BubbleConfig;
import com.xiaoniu.cleanking.ui.main.bean.BubbleDouble;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.InteractionSwitchList;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.main.event.LifecycEvent;
import com.xiaoniu.cleanking.ui.main.model.GoldCoinDoubleModel;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.GoldCoinSuccessActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.ui.newclean.interfice.FragmentOnFocusListenable;
import com.xiaoniu.cleanking.ui.newclean.listener.IBullClickListener;
import com.xiaoniu.cleanking.ui.newclean.presenter.NewPlusCleanMainPresenter;
import com.xiaoniu.cleanking.ui.newclean.view.ObservableScrollView;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.FromHomeCleanFinishEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.FunctionCompleteEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.UserInfoEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.WeatherInfoRequestEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
import com.xiaoniu.cleanking.ui.view.HomeInteractiveView;
import com.xiaoniu.cleanking.ui.view.HomeMainTableView;
import com.xiaoniu.cleanking.ui.view.HomeToolTableView;
import com.xiaoniu.cleanking.ui.viruskill.ArmVirusKillActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.anim.FloatAnimManager;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.ClearCardView;
import com.xiaoniu.cleanking.widget.LuckBubbleView;
import com.xiaoniu.cleanking.widget.OneKeyCircleButtonView;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.common.utils.Points;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.xiaoniu.cleanking.utils.user.UserHelper.EXIT_SUCCESS;
import static com.xiaoniu.cleanking.utils.user.UserHelper.LOGIN_SUCCESS;

/**
 * Created by xinxiaolong on 2020/6/30.
 * email：xinxiaolong123@foxmail.com
 */
public class NewPlusCleanMainFragment extends BaseFragment<NewPlusCleanMainPresenter> implements IBullClickListener , FragmentOnFocusListenable {

    @BindView(R.id.view_lottie_top)
    OneKeyCircleButtonView view_lottie_top;
    @BindView(R.id.home_main_table)
    HomeMainTableView homeMainTableView;
    @BindView(R.id.home_tool_table)
    HomeToolTableView homeToolTableView;
    @BindView(R.id.layout_clean_top)
    RelativeLayout layoutCleanTop;
    @BindView(R.id.tv_withDraw)
    TextView tvWithDraw;
    @BindView(R.id.tv_coin_num)
    TextView tvCoinNum;

    @BindView(R.id.clear_card_video)
    ClearCardView clearVideoLayout;
    @BindView(R.id.clear_card_image)
    ClearCardView clearImageLayout;
    @BindView(R.id.clear_card_sound)
    ClearCardView clearSoundLayout;

    @BindView(R.id.ad_one)
    FrameLayout adLayoutOne;
    @BindView(R.id.ad_two)
    FrameLayout adLayoutTwo;
    @BindView(R.id.ad_three)
    FrameLayout adLayoutThree;
    @BindView(R.id.layout_scroll)
    ObservableScrollView mScrollView;
    @BindView(R.id.image_interactive)
    public HomeInteractiveView imageInteractive;

    private boolean isThreeAdvOpen = false;
    private boolean hasInitThreeAdvOnOff = false;

    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale = 0;
    private RxPermissions rxPermissions;

    private AlertDialog dlg;
    private CompositeDisposable compositeDisposable;
    //判断重新启动
    boolean isFirstCreate = false;
    private boolean isDenied = false;
    private boolean isSlide;//正在滑动
    FloatAnimManager mFloatAnimManager;

    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_plus_clean_main;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        rxPermissions = new RxPermissions(requireActivity());
        compositeDisposable = new CompositeDisposable();
        mPresenter.getInteractionSwitch();
        mPresenter.refBullList();
        homeMainTableView.initViewState();
        homeToolTableView.initViewState();
        mFloatAnimManager = new FloatAnimManager(imageInteractive, DisplayUtils.dip2px(180));
        isFirstCreate = true;
        initEvent();
        showHomeLottieView();
        initClearItemCard();
        checkAndUploadPoint();
        initListener();
        //金币前两个位置预加载
        mPresenter.goldAdprev();
        StatisticsUtils.customTrackEvent("home_page_custom", "首页页面创建", "home_page", "home_page");
        LogUtils.i("zz------22---"+System.currentTimeMillis());

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && isFirstCreate) {
            refreshAdAll();
            isFirstCreate = false;
        }

    }

    private void initListener() {
        mScrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(int x, int y, int oldx, int oldy, boolean isBottom) {
                if (y == 0) {
                    if (isSlide) {
                        //滑动过到静止状态
                        isSlide = false;
                        mFloatAnimManager.showFloatAdvertView();
                    }
                } else {
                    isSlide = true;
                    mFloatAnimManager.hindFloatAdvertView();
                }
            }

            @Override
            public void onScrollState(int scrollState) {
//                if (scrollState == STATE_SLIDING) {//正在滑动
//                    isSlide = true;
//                    mFloatAnimManager.hindFloatAdvertView();
//                } else {
//                    if (isSlide) {
//                        //滑动过到静止状态
//                        isSlide = false;
//                        mFloatAnimManager.showFloatAdvertView();
//                    }
//                }
            }
        });
    }


    private void initClearItemCard() {

        clearVideoLayout.setLeftTitle("视频文件");
        clearVideoLayout.setLeftIcon(R.mipmap.clear_icon_video);
        clearVideoLayout.setClearItemImage(R.mipmap.clear_image_video);
        clearVideoLayout.setClearItemContent("视频文件批量删除");
        clearVideoLayout.setClearItemSubContent("有效节省空间");
        clearVideoLayout.setOnClickListener(view -> {
            StatisticsUtils.trackClick("video_file_click", "用户在首页点击【视频文件】", "home_page", "home_page");
            //跳转到视频清理
            startActivity(new Intent(getActivity(), CleanVideoManageActivity.class));
        });

        clearImageLayout.setLeftTitle("图片");
        clearImageLayout.setLeftIcon(R.mipmap.clear_icon_img);
        clearImageLayout.setClearItemImage(R.mipmap.clear_image_pic);
        clearImageLayout.setClearItemContent("智能相册管理");
        clearImageLayout.setClearItemSubContent("一键删除无用照片");
        clearImageLayout.setOnClickListener(view -> {
            StatisticsUtils.trackClick("picture_file_Click", "用户在首页点击【图片文件】", "home_page", "home_page");
            Intent intent = new Intent(getActivity(), ImageActivity.class);
            startActivity(intent);
        });

        clearSoundLayout.setLeftTitle("音频文件");
        clearSoundLayout.setLeftIcon(R.mipmap.clear_icon_sound);
        clearSoundLayout.setClearItemImage(R.mipmap.clear_image_audio);
        clearSoundLayout.setClearItemContent("清除过期音频文件");
        clearSoundLayout.setClearItemSubContent("释放更多可用空间");
        clearSoundLayout.setOnClickListener(view -> {
            StatisticsUtils.trackClick("audio_file_Click", "用户在首页点击音频文件", "home_page", "home_page");
            //跳转到音乐清理
            startActivity(new Intent(getActivity(), CleanMusicManageActivity.class));
        });
    }


    /**
     * 权限埋点上报
     */
    private void checkAndUploadPoint() {
        //SD卡读写权限是否打开埋点上报
        String storagePrmStatus = AppUtils.checkStoragePermission(getActivity()) ? "open" : "close";
        StatisticsUtils.customCheckPermission(Points.STORAGE_PERMISSION_EVENT_CODE, Points.STORAGE_PERMISSION_EVENT_NAME, storagePrmStatus, "", "home_page");
        //读取手机状态权限是否打开埋点上报
        String phoneStatePrmStatus = AppUtils.checkPhoneStatePermission(getActivity()) ? "open" : "close";
        StatisticsUtils.customCheckPermission(Points.DEVICE_IDENTIFICATION_EVENT_CODE, Points.DEVICE_IDENTIFICATION_EVENT_NAME, phoneStatePrmStatus, "", "home_page");
    }


    private void initEvent() {

        imageInteractive.setClickListener(data -> {
                    StatisticsUtils.trackClick(Points.MainHome.SCRAPING_BUOY_CLICK_CODE, Points.MainHome.SCRAPING_BUOY_CLICK_NAME, "home_page", "home_page");
                    onInteractiveListener.onClick(null);
                }
        );

        homeMainTableView.setOnItemClickListener(item -> {
            switch (item) {
                case HomeMainTableView.ITEM_ONE_KEY:
                    onOneKeySpeedClick();
                    break;
                case HomeMainTableView.ITEM_KILL_VIRUS:
                    onKillVirusClick();
                    break;
                case HomeMainTableView.ITEM_ELECTRIC:
                    onElectricClick();
                    break;
            }
        });
        homeToolTableView.setOnItemClickListener(item -> {
            switch (item) {
                case HomeToolTableView.ITEM_WX:
                    onCleanWxClick();
                    break;
                case HomeToolTableView.ITEM_TEMPERATURE:
                    onCoolingClick();
                    break;
                case HomeToolTableView.ITEM_NOTIFY:
                    onCleanNotifyClick();
                    break;
                case HomeToolTableView.ITEM_NETWORK:
                    onNetworkSpeedClick();
                    break;
                case HomeToolTableView.ITEM_FOLDER:
                    onCleanFolderClick();
                    break;
            }
        });

        tvWithDraw.setOnClickListener(v -> {
            if (onWithDrawListener != null)
                onWithDrawListener.onClick(v);
        });
    }


    /*
     *********************************************************************************************************************************************************
     ************************************************************load advInfo*********************************************************************************
     *********************************************************************************************************************************************************
     */

    private void refreshAdAll() {
        refreshAd(MidasConstants.MAIN_ONE_AD_ID);
        refreshAd(MidasConstants.MAIN_TWO_AD_ID);
        refreshAd(MidasConstants.MAIN_THREE_AD_ID);
        showAdVideo();
    }

    private void refreshAd(String adId) {
        switch (adId) {
            case MidasConstants.MAIN_ONE_AD_ID:
                if (AppHolder.getInstance().checkAdSwitch(PositionId.KEY_MAIN_ONE_AD)) {
                    StatisticsUtils.customTrackEvent("ad_request_sdk_1", "首页广告位1发起广告请求数", "", "home_page");
                    mPresenter.showAdviceLayout(adLayoutOne, adId, adClick);
                }
                break;
            case MidasConstants.MAIN_TWO_AD_ID:
                if (AppHolder.getInstance().checkAdSwitch(PositionId.KEY_MAIN_TWO_AD)) {
                    StatisticsUtils.customTrackEvent("ad_request_sdk_2", "首页广告位2发起广告请求数", "", "home_page");
                    mPresenter.showAdviceLayout(adLayoutTwo, adId, adClick);
                }
                break;
            case MidasConstants.MAIN_THREE_AD_ID:
                if (AppHolder.getInstance().checkAdSwitch(PositionId.KEY_MAIN_THREE_AD)) {
                    if (getAdLayoutThreeVisible()) {
                        StatisticsUtils.customTrackEvent("ad_request_sdk_3", "首页广告位3发起广告请求数", "", "home_page");
                        mPresenter.showAdviceLayout(adLayoutThree, adId, adClick);
                    } else {
                        mPresenter.prepareVideoAd(adLayoutThree);
                    }
                }
                break;
        }
    }

    IOnAdClickListener adClick = this::refreshAd;


    private boolean getAdLayoutThreeVisible() {
        Rect scrollBounds = new Rect();
        mScrollView.getHitRect(scrollBounds);
        return adLayoutThree.getLocalVisibleRect(scrollBounds);
    }

    boolean isRequest = false;


    private void showAdVideo() {
        mScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            Rect scrollBounds = new Rect();
            mScrollView.getHitRect(scrollBounds);
            if (clearVideoLayout.getLocalVisibleRect(scrollBounds)) {
                //子控件至少有一个像素在可视范围内
                if (!isRequest) {
                    mPresenter.fillVideoAd(adLayoutThree, adClick);
                }
                isRequest = true;
            } else {
                //子控件完全不在可视范围内
            }
        });

    }


    /*
     *********************************************************************************************************************************************************
     ************************************************************fragment lifecycle***************************************************************************
     *********************************************************************************************************************************************************
     */

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);


        if (!hidden && getActivity() != null) {
            NiuDataAPI.onPageStart("home_page_view_page", "首页浏览");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_fff7f8fa), true);
            } else {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_fff7f8fa), false);
            }
            //重新检测头部扫描状态
            checkScanState();
            //刷新广告数据
            if(!isFirstCreate){
                refreshAdAll();
            }
            //金币配置刷新
            mPresenter.refBullList();
        } else {
            NiuDataAPI.onPageEnd("home_page_view_page", "首页浏览");
        }



    }


    @Override
    public void onResume() {
        super.onResume();
        checkScanState();
        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPowerSize = new FileQueryUtils().getRunningProcess().size();
        imageInteractive.loadNextDrawable();
        NiuDataAPI.onPageStart("home_page_view_page", "首页浏览");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /*
     *********************************************************************************************************************************************************
     ************************************************************eventBus notify******************************************************************************
     *********************************************************************************************************************************************************
     */

    //功能使用完成通知
    @Subscribe
    public void fromFunctionCompleteEvent(FunctionCompleteEvent event) {
        if (event == null || event.getTitle() == null) {
            return;
        }
        switch (event.getTitle()) {
            case "一键加速":
                homeMainTableView.oneKeySpeedUsedStyle();
                break;
            case "超强省电":
                homeMainTableView.electricUsedStyle();
                break;
            case "病毒查杀":
                homeMainTableView.killVirusUsedStyle();
                break;
            case "通知栏清理":
                homeToolTableView.postDelayed(() -> homeToolTableView.notifyUsedStyle(), 2000);
                break;
            case "手机降温":
                homeToolTableView.coolingUsedStyle();
                break;
            case "微信专清":
                homeToolTableView.wxCleanUsedStyle();
                break;
            case "网络加速":
                //文案一直显示“有效提高20%”,暂不做刷新

                break;
        }
    }

    /**
     * 热启动回调
     */
    @Subscribe
    public void changeLifeCycleEvent(LifecycEvent lifecycEvent) {
        if (null == view_lottie_top) return;
        //热启动后重新检测权限
        isDenied = false;
        homeMainTableView.initViewState();
        homeToolTableView.initViewState();
        mPresenter.refBullList();//金币配置刷新；
    }

    //完成页返回通知
    @Subscribe
    public void fromHomeCleanFinishEvent(FromHomeCleanFinishEvent event) {

    }

    //更新用户信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userInfoUpdate(UserInfoEvent event) {

        if (AppHolder.getInstance().getAuditSwitch()) {
            tvCoinNum.setVisibility(View.GONE);
            tvWithDraw.setVisibility(View.GONE);
        } else if (event != null && event.infoBean != null) {
            tvCoinNum.setVisibility(View.VISIBLE);
            tvWithDraw.setVisibility(View.VISIBLE);
            tvCoinNum.setText(String.valueOf(event.infoBean.getGold()));
        }
    }

    //用户登录状态改变通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userLoginInfo(String eventCode) {
        switch (eventCode) {
            //退出登录
            case EXIT_SUCCESS:
                tvCoinNum.setVisibility(View.GONE);
                tvWithDraw.setVisibility(View.GONE);
                break;
            //登录成功
            case LOGIN_SUCCESS:

                break;
        }
    }

    /*
     *********************************************************************************************************************************************************
     ************************************************************head oneKey clean start**********************************************************************
     *********************************************************************************************************************************************************
     */
//    @Deprecated
    private void showHomeLottieView() {
//        int screenWidth = ScreenUtils.getScreenWidth(mContext);
//        RelativeLayout.LayoutParams textLayout = (RelativeLayout.LayoutParams) view_lottie_top.getLayoutParams();
//        textLayout.setMargins(0, -Float.valueOf(screenWidth * 0.1f).intValue(), 0, 0);
//        view_lottie_top.setLayoutParams(textLayout);
        LuckBubbleView lftop = view_lottie_top.findViewById(R.id.lftop);
        LuckBubbleView lfbottom = view_lottie_top.findViewById(R.id.lfbotm);
        LuckBubbleView rttop = view_lottie_top.findViewById(R.id.rttop);
        LuckBubbleView rtbottom = view_lottie_top.findViewById(R.id.rtbotm);
        lftop.setIBullListener(this);
        lfbottom.setIBullListener(this);
        rttop.setIBullListener(this);
        rtbottom.setIBullListener(this);

    }

    /**
     * EventBus 立即清理完成后，更新首页显示文案
     */
    @Subscribe
    public void onEventClean(CleanEvent cleanEvent) {
        if (cleanEvent != null) {
            if (cleanEvent.isCleanAminOver()) {
                String cleanedCache = MmkvUtil.getString(SpCacheConfig.MKV_KEY_HOME_CLEANED_DATA, "");
                CountEntity countEntity = new Gson().fromJson(cleanedCache, CountEntity.class);
                view_lottie_top.setClendedState(countEntity);
            }
        }
    }

    public void setScanningFinish(LinkedHashMap<ScanningResultType, JunkGroup> junkGroups) {
        long totalJunkSize = 0;
        for (Map.Entry<ScanningResultType, JunkGroup> map : junkGroups.entrySet()) {
            totalJunkSize += map.getValue().mSize;
        }
        CountEntity mCountEntity = CleanUtil.formatShortFileSize(totalJunkSize);
        ScanDataHolder.getInstance().setTotalSize(totalJunkSize);
        ScanDataHolder.getInstance().setmCountEntity(mCountEntity);
        ScanDataHolder.getInstance().setmJunkGroups(junkGroups);
        ScanDataHolder.getInstance().setScanState(1);
        if (view_lottie_top != null)
            view_lottie_top.scanFinish(totalJunkSize);
    }

    public void setScanningJunkTotal(long totalSize) {
        if (null != view_lottie_top)
            view_lottie_top.setTotalSize(totalSize);
    }

    public void permissionDenied() {//授权被拒绝
        if (null != view_lottie_top)
            view_lottie_top.setNoSize();
        isDenied = true;
    }

    //重新检测头部扫描状态
    public void checkScanState() {
        if (AppUtils.checkStoragePermission(getActivity())) {//已经获得权限
            if (PreferenceUtil.getNowCleanTime()) {  //清理结果五分钟以外
                if (ScanDataHolder.getInstance().getScanState() > 0 && null != ScanDataHolder.getInstance().getmCountEntity() && ScanDataHolder.getInstance().getTotalSize() > 50 * 1024 * 1024) {//扫描缓存5分钟内_展示缓存结果
                    setScanningJunkTotal(ScanDataHolder.getInstance().getTotalSize()); //展示缓存结果
                    view_lottie_top.scanFinish(ScanDataHolder.getInstance().getTotalSize());
                } else {//重新开始扫描
//                    mPresenter.cleanData();
                    mPresenter.readyScanningJunk();
                    mPresenter.scanningJunk();
                }
            } else { //清理结果五分钟以内
                String cleanedCache = MmkvUtil.getString(SpCacheConfig.MKV_KEY_HOME_CLEANED_DATA, "");
                CountEntity countEntity = new Gson().fromJson(cleanedCache, CountEntity.class);
                view_lottie_top.setClendedState(countEntity);

            }
        } else {//未取得权限
            LogUtils.i("--checkScanState()");
            //避免重复弹出
            new Handler().postDelayed(() -> {
                if (!isDenied) {
                    mPresenter.checkStoragePermission();  //重新开始扫描
                }
            }, 200);
            //未授权默认样式——存在大量垃圾；
            if (null != view_lottie_top)
                view_lottie_top.setNoSize();

        }
    }


    /**
     * 点击立即清理
     */
    @OnClick({R.id.iv_center, R.id.layout_temp})
    public void nowClean(View view) {
        switch (view.getId()) {
            case R.id.iv_center:
                StatisticsUtils.trackClick("home_page_clean_click", "用户在首页点击【立即清理】", "home_page", "home_page");
                if (PreferenceUtil.getNowCleanTime()) { //清理缓存五分钟_未扫过或者间隔五分钟以上
                    ToastUtils.showShort("清理已间隔5分钟");
                    if (ScanDataHolder.getInstance().getScanState() > 0 && ScanDataHolder.getInstance().getmJunkGroups().size() > 0) {//扫描缓存5分钟内——直接到扫描结果页
                        //读取扫描缓存
                        ToastUtils.showShort("跳转清理页面");
                        startActivity(NowCleanActivity.class);
                    } else {    //scanState ==0: 扫描中
                        checkStoragePermission();
                    }
                } else {
                    ToastUtils.showShort("清理未间隔5分钟");
                    String cleanedCache = MmkvUtil.getString(SpCacheConfig.MKV_KEY_HOME_CLEANED_DATA, "");
                    CountEntity countEntity = new Gson().fromJson(cleanedCache, CountEntity.class);
                    if (null != countEntity && getActivity() != null && this.isAdded()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("title", getResources().getString(R.string.tool_suggest_clean));
                        bundle.putString("num", countEntity.getTotalSize());
                        bundle.putString("unit", countEntity.getUnit());
                        bundle.putBoolean("unused",true);
                        Intent intent = new Intent(requireActivity(), NewCleanFinishActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        ToastUtils.showShort("跳转NewCleanFinishActivity.class");
                    } else {
                        //判断扫描缓存；
                        if (ScanDataHolder.getInstance().getScanState() > 0 && ScanDataHolder.getInstance().getmJunkGroups().size() > 0) {//扫描缓存5分钟内——直接到扫描结果页
                            //读取扫描缓存
                            ToastUtils.showShort("跳转NowCleanActivity.class");
                            startActivity(NowCleanActivity.class);
                        } else {                //scanState ==0: 扫描中
                            checkStoragePermission();
                        }
                    }
                }
                break;

            case R.id.layout_temp://获取天气信息；
                StatisticsUtils.trackClick("weather_forecast_click", "用户在首页点击【天气预报】模板", "home_page", "home_page");
                EventBus.getDefault().post(new WeatherInfoRequestEvent(0));
                break;
        }

    }


    /**
     * 检查文件存贮权限
     */
    private void checkStoragePermission() {
        ToastUtils.showShort("清理checkStoragePermission()");
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Disposable disposable = rxPermissions.request(permissions)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        mPresenter.stopScanning();
                        ToastUtils.showShort("跳转NowCleanActivity.class");
                        startActivity(NowCleanActivity.class);
                    } else {
                        if (hasPermissionDeniedForever()) {  //点击拒绝
                            ToastUtils.showShort("跳转NowCleanActivity.class");
                            startActivity(NowCleanActivity.class);
                        } else {                            //点击永久拒绝
                            showPermissionDialog();
                            ToastUtils.showShort("弹框提醒打开权限");
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void showPermissionDialog() {
        dlg = new AlertDialog.Builder(requireContext()).create();
        if (requireActivity().isFinishing()) {
            return;
        }
        dlg.show();
        Window window = dlg.getWindow();
        if (window != null) {
            window.setContentView(R.layout.alite_redp_send_dialog);
            WindowManager.LayoutParams lp = window.getAttributes();
            //这里设置居中
            lp.gravity = Gravity.CENTER;
            window.setAttributes(lp);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView btnOk = window.findViewById(R.id.btnOk);

            TextView btnCancle = window.findViewById(R.id.btnCancle);
            TextView tipTxt = window.findViewById(R.id.tipTxt);
            TextView content = window.findViewById(R.id.content);
            btnCancle.setText("取消");
            btnOk.setText("去设置");
            tipTxt.setText("提示!");
            content.setText("清理功能无法使用，请先开启文件读写权限。");
            btnOk.setOnClickListener(v -> {
                dlg.dismiss();
                goSetting();
            });
            btnCancle.setOnClickListener(v -> dlg.dismiss());
        }
    }


    public void goSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            requireActivity().startActivity(intent);
        }
    }

    /**
     * 是否有权限被永久拒绝
     */
    private boolean hasPermissionDeniedForever() {
        boolean hasDeniedForever = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                hasDeniedForever = true;
            }
        }
        return hasDeniedForever;
    }

    /*
     *********************************************************************************************************************************************************
     ************************************************************oneKey speed start************************************************************************
     *********************************************************************************************************************************************************
     */

    //click one key speed
    public void onOneKeySpeedClick() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.ONKEY);
        StatisticsUtils.trackClick(Points.MainHome.BOOST_CLICK_CODE, Points.MainHome.BOOST_CLICK_NAME, "home_page", "home_page");
        //保存本次清理完成时间 保证每次清理时间间隔为3分钟
        if (!PreferenceUtil.getCleanTime()) {
            initThreeAdvOnOffInfo();
            EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
            if (isThreeAdvOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_one_key_speed), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_one_key_speed));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_one_key_speed));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                bundle.putBoolean("unused",true);
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_one_key_speed));
            startActivity(PhoneAccessActivity.class, bundle);
        }
    }

    private void initThreeAdvOnOffInfo() {
        if (hasInitThreeAdvOnOff) {
            return;
        }
        hasInitThreeAdvOnOff = true;
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_AD_PAGE_FINISH.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isThreeAdvOpen = switchInfoList.isOpen();
                    break;
                }
            }
        }
    }

    /*
     * *********************************************************************************************************************************************************
     * **********************************************************kill virus start*******************************************************************************
     * *********************************************************************************************************************************************************
     */

    //click kill virus
    private void onKillVirusClick() {
        StatisticsUtils.trackClick(Points.MainHome.VIRUS_KILLING_CLICK_CODE, Points.MainHome.VIRUS_KILLING_CLICK_NAME, "home_page", "home_page");
        startKillVirusActivity();
    }

    //start kill virus page
    private void startKillVirusActivity() {
        if (PreferenceUtil.getVirusKillTime()) {
            startActivity(ArmVirusKillActivity.class);
        } else {
            Intent intent = new Intent(getActivity(), NewCleanFinishActivity.class);
            intent.putExtra("title", "病毒查杀");
            intent.putExtra("main", false);
            intent.putExtra("unused",true);
            startActivity(intent);
        }
    }


    /*
     * *********************************************************************************************************************************************************
     * **********************************************************electric start*********************************************************************************
     * *********************************************************************************************************************************************************
     */

    public void onElectricClick() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.SUPER_POWER_SAVING);
        StatisticsUtils.trackClick(Points.MainHome.POWERSAVE_CLICK_CODE, Points.MainHome.POWERSAVE_CLICK_NAME, "home_page", "home_page");
        if (PreferenceUtil.getPowerCleanTime()) {
            startActivity(PhoneSuperPowerActivity.class);
        } else {
            initThreeAdvOnOffInfo();
            if (isThreeAdvOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_super_power_saving), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_super_power_saving));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_super_power_saving));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                bundle.putBoolean("unused",true);
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }

    /*
     * *********************************************************************************************************************************************************
     * **********************************************************folder clean start*****************************************************************************
     * *********************************************************************************************************************************************************
     */

    public void onCleanFolderClick() {
        StatisticsUtils.trackClick(Points.MainHome.DEEP_CLEANING_CLICK_CODE, Points.MainHome.DEEP_CLEANING_CLICK_NAME, AppHolder.getInstance().getSourcePageId(), "acceleration_page");
        startActivity(RouteConstants.CLEAN_BIG_FILE_ACTIVITY);
    }

    /**
     * 获取互动式广告成功
     */
    public void getInteractionSwitchSuccess(InteractionSwitchList switchInfoList) {
        if (null == switchInfoList || null == switchInfoList.getData() || switchInfoList.getData().size() <= 0)
            return;
        if (switchInfoList.getData().get(0).isOpen()) {
            imageInteractive.setDataList(switchInfoList.getData().get(0).getSwitchActiveLineDTOList());
            imageInteractive.loadNextDrawable();
            mFloatAnimManager.setIsNeedOptionAnimation(true);
        } else {
            mFloatAnimManager.setIsNeedOptionAnimation(false);
            imageInteractive.setVisibility(View.GONE);
        }
    }



    /*
     * *********************************************************************************************************************************************************
     * **********************************************************wxClean start**********************************************************************************
     * *********************************************************************************************************************************************************
     */

    /**
     * 微信专清
     */
    public void onCleanWxClick() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.WETCHAT_CLEAN);
        StatisticsUtils.trackClick(Points.MainHome.WXCLEAN_CLICK_CODE, Points.MainHome.WXCLEAN_CLICK_NAME, "home_page", "home_page");
        if (!AndroidUtil.isAppInstalled(SpCacheConfig.CHAT_PACKAGE)) {
            ToastUtils.showShort(R.string.tool_no_install_chat);
            return;
        }
        if (PreferenceUtil.getWeChatCleanTime()) {
            // 每次清理间隔 至少3秒
            startActivity(WechatCleanHomeActivity.class);
        } else {
            initThreeAdvOnOffInfo();
            if (isThreeAdvOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_chat_clear), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_chat_clear));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_chat_clear));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                bundle.putBoolean("unused",true);
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }


    /*
     * *********************************************************************************************************************************************************
     * **********************************************************notifyClean start******************************************************************************
     * *********************************************************************************************************************************************************
     */
    public void onCleanNotifyClick() {
//        ADUtilsKt.preloadingSplashAd(getActivity(), PositionId.AD_FINISH_BEFOR);
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");

        StatisticsUtils.trackClick(Points.MainHome.NOTIFICATION_CLEAN_CLICK_CODE, Points.MainHome.NOTIFICATION_CLEAN_CLICK_NAME, AppHolder.getInstance().getSourcePageId(), "home_page");
        if (PreferenceUtil.getNotificationCleanTime()) {
            NotifyCleanManager.startNotificationCleanActivity(getActivity(), 0);
        } else {
            initThreeAdvOnOffInfo();
            if (isThreeAdvOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_notification_clean), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_notification_clean));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_notification_clean));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                bundle.putBoolean("unused",true);
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }


    /*
     * *********************************************************************************************************************************************************
     * **********************************************************cooling start**********************************************************************************
     * *********************************************************************************************************************************************************
     */
    public void onCoolingClick() {
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        StatisticsUtils.trackClick(Points.MainHome.COOLING_CLICK_CODE, Points.MainHome.COOLING_CLICK_NAME, AppHolder.getInstance().getSourcePageId(), "home_page");

        if (PreferenceUtil.getCoolingCleanTime()) {
            startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
        } else {
            initThreeAdvOnOffInfo();
            if (isThreeAdvOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_phone_temperature_low), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_phone_temperature_low));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_phone_temperature_low));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                bundle.putBoolean("unused",true);
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }

    /*
     * *********************************************************************************************************************************************************
     * **********************************************************network  speed start***************************************************************************
     * *********************************************************************************************************************************************************
     */
    private void onNetworkSpeedClick() {
        StatisticsUtils.trackClick(Points.MainHome.NETWORK_ACCELERATION_CLICK_CODE, Points.MainHome.NETWORK_ACCELERATION_CLICK_NAME, AppHolder.getInstance().getSourcePageId(), "home_page");
        if (PreferenceUtil.getSpeedNetWorkTime()) {
            startActivity(NetWorkActivity.class);
        } else {
            Intent intent = new Intent(getActivity(), NewCleanFinishActivity.class);
            String num = PreferenceUtil.getSpeedNetworkValue();
            intent.putExtra("title", "网络加速");
            intent.putExtra("main", false);
            intent.putExtra("num", num);
            intent.putExtra("unused",true);
            startActivity(intent);
        }
    }

    /*
     * *********************************************************************************************************************************************************
     * ********************************************************** 刷新头部样式 ***************************************************************************************
     * *********************************************************************************************************************************************************
     */

    /**
     * 刷新金币显示
     */
    public void setTopBubbleView(BubbleConfig dataBean) {
        if (null != view_lottie_top && null != dataBean) {
            view_lottie_top.refBubbleView(dataBean);
        }
    }


    /*
     * *********************************************************************************************************************************************************
     * ********************************************************** 头部金币点击 ***************************************************************************************
     * *********************************************************************************************************************************************************
     */
    @Override
    public void clickBull(BubbleConfig.DataBean ballBean, int pos) {
        if (ballBean == null) {
            ToastUtils.showShort(R.string.net_error);
            return;
        }
        if (!AndroidUtil.isFastDoubleClick()) {
            mPresenter.bullCollect(ballBean.getLocationNum());
            StatisticsUtils.trackClick("withdrawal_click", "在首页点击提现", "home_page", "home_page");
        }
    }


    /**
     * 金币领取成功
     */
    public void bubbleCollected(BubbleCollected dataBean) {
        if (null != dataBean) {
            mPresenter.refBullList();//刷新金币列表；
        }
        assert dataBean != null && dataBean.getData() != null;
        mPresenter.showGetGoldCoinDialog(dataBean);
    }


    /**
     * 激励视频播放完成金币翻倍
     */
    public void bubbleDouble(BubbleCollected dataBean) {
        if (null != dataBean) {
            mPresenter.bullDouble(dataBean.getData().getUuid(), dataBean.getData().getLocationNum(), dataBean.getData().getGoldCount());//刷新金币列表；
        }
    }

    /**
     * 翻倍成功
     */
    public void bubbleDoubleSuccess(BubbleDouble dataBean, int localNum) {
        if (null == dataBean)
            return;
        mPresenter.refBullList();//刷新金币列表；
        int num = dataBean.getData().getGoldCount();
        String adId = "";
        if (AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_HOME_GOLD_PAGE, PositionId.DRAW_THREE_CODE)) {//广告位3开关
            adId = AdposUtil.getAdPos(localNum, 2);
        }
        startGoldSuccess(adId, num, localNum);
    }

    private void startGoldSuccess(String adId, int num, int index) {
        GoldCoinDoubleModel model = new GoldCoinDoubleModel(adId, num, index, Points.MainGoldCoin.SUCCESS_PAGE);
        GoldCoinSuccessActivity.Companion.start(mActivity, model);
    }

    /*
     * *********************************************************************************************************************************************************
     * ********************************************************** others ***************************************************************************************
     * *********************************************************************************************************************************************************
     */

    View.OnClickListener onInteractiveListener;

    public void setOnInteractiveClickListener(View.OnClickListener listener) {
        this.onInteractiveListener = listener;
    }

    View.OnClickListener onWithDrawListener;

    public void setOnWithDrawClickListener(View.OnClickListener listener) {
        this.onWithDrawListener = listener;
    }
}
