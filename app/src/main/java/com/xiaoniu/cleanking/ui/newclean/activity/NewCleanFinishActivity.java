package com.xiaoniu.cleanking.ui.newclean.activity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.chuanshanjia.TTAdManagerHolder;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.activity.GameActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.NewsItemInfoRuishi;
import com.xiaoniu.cleanking.ui.main.bean.NewsType;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.VideoItemInfo;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.main.presenter.CleanFinishPresenter;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.news.adapter.NewsListAdapter;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.FromHomeCleanFinishEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.ui.tool.notify.utils.NotifyUtils;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.PermissionUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.http.EHttp;
import com.xiaoniu.common.http.callback.ApiCallback;
import com.xiaoniu.common.http.request.HttpRequest;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cn.jzvd.Jzvd;

/**
 * 1.2.0 版本以后清理完成 显示资讯
 */
public class NewCleanFinishActivity extends BaseActivity<CleanFinishPresenter> implements View.OnClickListener {

    private static final String TAG = "AD_DEMO";
    private String mTitle = "";
    private TextView mTitleTv;
    private TextView mTvSize;
    private TextView mTvGb;
    private TextView mTvQl;

    private XRecyclerView mRecyclerView;
    private NewsListAdapter mNewsAdapter;
    private NewsType mType = NewsType.TOUTIAO;
    private static final int PAGE_NUM = 20;//每一页数据
    private boolean mIsRefresh = true;
    private ImageView mBtnLeft;

    public View v_quicken, v_power, v_notification, v_wechat, v_file, v_cool, v_clean_all, v_game;
    public View line_quicken, line_power, line_notification, line_wechat, line_file, line_clean_all, line_game;
    private ImageView iv_advert_logo, iv_advert, iv_advert_logo2, iv_advert2;
    private TextView tv_advert, tv_advert_content, tv_advert2, tv_advert_content2, tv_download2;
    private View v_advert, v_advert2, mRecommendV;
    private TextView tv_quicken, tv_power, tv_notification;
    private ImageView iv_quicken, iv_power, iv_notification;
    private LottieAnimationView mLottieAd;

    private NativeUnifiedADData mNativeUnifiedADData, mNativeUnifiedADData2;
    private NativeUnifiedAD mAdManager, mAdManager2;
    private MediaView mMediaView, mMediaView2;
    private NativeAdContainer mContainer, mContainer2;

    private String mAdvertId = ""; //广告位1
    private String mAdvertId2 = ""; //广告位2
    private String mSecondAdvertId = ""; //备用id
    private String mSecondAdvertId2 = ""; //备用id
    private boolean isScreenSwitchOpen; //插屏广告开关
    private int mScreenShowCount; //插屏广告展示次数

    private int page_index = 1;
    private int mShowCount; //推荐显示的数量
    private int mRamScale; //所有应用所占内存大小

    public static String sourcePage = "";
    public static String currentPage = "";
    String createEventCode = "";
    String createEventName = "";
    String returnEventName = "";
    String sysReturnEventName = "";

    //穿山甲相关 begin
    private FrameLayout mChuanShanJiaVideo;
    private TTAdNative mTTAdNative;
    private FrameLayout mChuanShanJiaVideo2;
    private TTAdNative mTTAdNative2;
    //穿山甲相关 end

    //插屏广告相关 begin
    private HScreen mHandlerScreen = new HScreen();
    private TTAdNative mTTAdNativeScreen;
    private NativeUnifiedAD mAdManagerScreen;
    private NativeUnifiedADData mNativeUnifiedADDataScreen;
    private String mAdvertScreenId = ""; //插屏广告id
    private String mSecondAdvertScreenId = ""; //插屏广告备用id
    private boolean mIsScreenAdSuccess; //插屏广告是否拉取成功
    //插屏广告相关 end

    private AnimationDrawable mAnimationDrawable;
    FileQueryUtils fileQueryUtils;

    int processNum = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_finish_layout;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        fileQueryUtils = new FileQueryUtils();
        processNum = fileQueryUtils.getRunningProcess().size();
        mTitle = getIntent().getStringExtra("title");
        if (getString(R.string.tool_one_key_speed).contains(mTitle)
                || getString(R.string.tool_notification_clean).contains(mTitle)
                || getString(R.string.tool_super_power_saving).contains(mTitle)) {
            EventBus.getDefault().post(new FromHomeCleanFinishEvent(mTitle));
        }
        mPresenter.getSwitchInfoList();
        mBtnLeft = (ImageView) findViewById(R.id.btnLeft);
        mTitleTv = (TextView) findViewById(R.id.tvTitle);
        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);

        mNewsAdapter = new NewsListAdapter(this);
        View header = LayoutInflater.from(this).inflate(R.layout.layout_finish_head, findViewById(android.R.id.content), false);
        View headerTool = LayoutInflater.from(this).inflate(R.layout.layout_finish_head_tool, findViewById(android.R.id.content), false);
        mRecyclerView.addHeaderView(header);
        mRecyclerView.addHeaderView(headerTool);
        mRecyclerView.setLimitNumberToCallLoadMore(1);
        mRecyclerView.setAdapter(mNewsAdapter);
        mRecyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);

        mTvSize = header.findViewById(R.id.tv_size);
        mTvGb = header.findViewById(R.id.tv_clear_finish_gb_title);
        mTvSize.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/FuturaRound-Medium.ttf"));
        mTvGb.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/FuturaRound-Medium.ttf"));
        mTvQl = header.findViewById(R.id.tv_ql);

        mContainer = header.findViewById(R.id.native_ad_container);
        mMediaView = header.findViewById(R.id.gdt_media_view);
        v_advert = header.findViewById(R.id.v_advert);
        iv_advert_logo = header.findViewById(R.id.iv_advert_logo);
        iv_advert = header.findViewById(R.id.iv_advert);
        tv_advert = header.findViewById(R.id.tv_advert);
        tv_advert_content = header.findViewById(R.id.tv_advert_content);
        mChuanShanJiaVideo = header.findViewById(R.id.v_video_chuanshanjia);

        mChuanShanJiaVideo2 = headerTool.findViewById(R.id.v_video_chuanshanjia);
        mRecommendV = headerTool.findViewById(R.id.v_recommend);
        mContainer2 = headerTool.findViewById(R.id.native_ad_container);
        mMediaView2 = headerTool.findViewById(R.id.gdt_media_view);
        v_advert2 = headerTool.findViewById(R.id.v_advert);
        iv_advert_logo2 = headerTool.findViewById(R.id.iv_advert_logo);
        iv_advert2 = headerTool.findViewById(R.id.iv_advert);
        tv_advert2 = headerTool.findViewById(R.id.tv_advert);
        tv_advert_content2 = headerTool.findViewById(R.id.tv_advert_content);
        tv_download2 = headerTool.findViewById(R.id.tv_download);

        v_clean_all = headerTool.findViewById(R.id.v_clean_all);
        v_game = headerTool.findViewById(R.id.v_game);
        v_quicken = headerTool.findViewById(R.id.v_quicken);
        v_power = headerTool.findViewById(R.id.v_power);
        v_notification = headerTool.findViewById(R.id.v_notification);
        v_wechat = headerTool.findViewById(R.id.v_wechat);
        v_file = headerTool.findViewById(R.id.v_file);
        v_cool = headerTool.findViewById(R.id.v_cool);
        line_quicken = headerTool.findViewById(R.id.line_quicken);
        line_power = headerTool.findViewById(R.id.line_power);
        line_notification = headerTool.findViewById(R.id.line_notification);
        line_wechat = headerTool.findViewById(R.id.line_wechat);
        line_file = headerTool.findViewById(R.id.line_file);
        line_clean_all = headerTool.findViewById(R.id.line_clean_all);
        line_game = headerTool.findViewById(R.id.line_game);

        tv_quicken = headerTool.findViewById(R.id.tv_quicken);
        tv_power = headerTool.findViewById(R.id.tv_power);
        tv_notification = headerTool.findViewById(R.id.tv_notification);

        iv_quicken = headerTool.findViewById(R.id.iv_quicken);
        iv_power = headerTool.findViewById(R.id.iv_power);
        iv_notification = headerTool.findViewById(R.id.iv_notification);
        mTitleTv.setText(mTitle);

        mLottieAd = (LottieAnimationView) header.findViewById(R.id.lottie_ad);
        mLottieAd.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mLottieAd.playAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mPresenter.getScreentSwitch();
        getPageData();
        setListener();
        loadData();
        initChuanShanJia();
        initChuanShanJia2();
        initChuanShanJiaScreen();


        View ad_bg = header.findViewById(R.id.v_video);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ad_bg.setBackground(getResources().getDrawable(R.drawable.anim_ad));
            if (ad_bg.getBackground() instanceof AnimationDrawable) {
                mAnimationDrawable = (AnimationDrawable) ad_bg.getBackground();
            }
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (null != mAnimationDrawable && !mAnimationDrawable.isRunning()) { //判断是否在运行
            mAnimationDrawable.start();
        }
    }

    //获取埋点参数
    void getPageData() {
        sourcePage = AppHolder.getInstance().getCleanFinishSourcePageId();
        if (getString(R.string.app_name).contains(mTitle)) {
            //悟空清理
            currentPage = "clean_success_page";
        } else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
            //一键加速
            currentPage = "boost_success_page";
            createEventName = "加速结果页创建时";
            createEventCode = "boost_success_page_page_custom";
            returnEventName = "用户在加速结果页返回";
            sysReturnEventName = "用户在加速结果页返回";

        } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
            //1.2.1清理完成页面_建议清理
            currentPage = "clean_success_page";
            createEventName = "清理结果页创建时";
            createEventCode = "clean_success_page_custom";
            returnEventName = "用户在清理结果页返回";
            sysReturnEventName = "用户在清理结果页返回";
        } else if (getString(R.string.tool_phone_clean).contains(mTitle)) {
            //手机清理
            currentPage = "clean_success_page";
        } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
            //超强省电
            currentPage = "powersave_success_page";
            createEventName = "省电结果页创建时";
            createEventCode = "powersave_success_page_custom";
            returnEventName = "省电结果页出现返回";
            sysReturnEventName = "省电结果页出现返回";
        } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {
            //微信专情
            currentPage = "wxclean_success_page";
            createEventName = "微信结果页创建时";
            createEventCode = "wxclean_success_page_custom";
            returnEventName = "用户在微信清理结果页返回";
            sysReturnEventName = "用户在微信清理结果页返回";
        } else if (getString(R.string.tool_qq_clear).contains(mTitle)) {
            //QQ专清
            currentPage = "clean_up_page_view_immediately";
        } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {
            //通知栏清理
            currentPage = "notification_clean_success_page";
            createEventName = "通知栏清理结果页出现时";
            createEventCode = "notification_clean_success_page_custom";
            returnEventName = "通知栏清理结果页出现返回";
            sysReturnEventName = "通知栏清理结果页出现返回";
        } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) {
            //手机降温
            currentPage = "cooling_success_page";
            createEventName = "降温结果页创建时";
            createEventCode = "cooling_success_page_custom";
            returnEventName = "用户在降温结果页返回";
            sysReturnEventName = "用户在降温结果页返回";
        } else if (getString(R.string.game_quicken).contains(mTitle)) {
            //游戏加速
            currentPage = "gameboost_success_page";
            createEventName = "游戏加速结果页创建时";
            createEventCode = "gameboost_success_page_custom";
            returnEventName = "游戏加速结果页返回";
            sysReturnEventName = "游戏加速结果页返回";
        } else {
            currentPage = "clean_up_page_view_immediately";
        }

        //首页三分钟以内直接进入当前页情况
        if (sourcePage != null && sourcePage.equals("home_page")) {
            if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
                //一键加速
                currentPage = "direct_boost_success_page";
                createEventName = "直接跳加速结果页创建时";
                createEventCode = "direct_boost_success_page_custom";

            } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
                //1.2.1清理完成页面_建议清理
                currentPage = "direct_clean_success_page";
                createEventName = "直接跳清理结果页创建时";
                createEventCode = "direct_clean_success_page_custom";

            } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
                //超强省电
                currentPage = "direct_powersave_success_page";
                createEventName = "直接跳省电结果页创建时";
                createEventCode = "direct_powersave_success_page_custom";

            } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {
                //微信专情
                currentPage = "direct_wxclean_success_page";
                createEventName = "直接跳微信结果页创建时";
                createEventCode = "direct_wxclean_success_page_custom";

            } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {
                //通知栏清理
                currentPage = "direct_notification_clean_success_page";
                createEventName = "直接跳通知结果页创建时";
                createEventCode = "direct_notification_clean_success_page_custo";

            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) {
                //手机降温
                currentPage = "direct_cooling_success_page";
                createEventName = "直接跳降温结果页创建时";
                createEventCode = "direct_cooling_success_page_custom";
            } else if (getString(R.string.game_quicken).contains(mTitle)) {
                //游戏加速
                currentPage = "direct_gameboots_success_page";
                createEventName = "直接跳游戏加速结果页创建时";
                createEventCode = "direct_game_success_page_custom";
            } else if (getString(R.string.virus_kill).contains(mTitle)) {
                //病毒查杀
                currentPage = "virus_killing_success_page";
                createEventName = "病毒查杀结果页创建时";
                createEventCode = "virus_killing_success_page_custom";
            } else if (getString(R.string.network_quicken).contains(mTitle)) {
                //网络加速
                currentPage = "network_acceleration_success_page";
                createEventName = "网络加速结果页创建时";
                createEventCode = "network_acceleration_success_page_custom";
            }

        }
    }


    /**
     * 拉取广告开关成功
     *
     * @return
     */
    public void getSwitchInfoListSuccess(SwitchInfoList list) {
        if (null == list || null == list.getData() || list.getData().size() <= 0 || TextUtils.isEmpty(mTitle))
            return;
        for (SwitchInfoList.DataBean switchInfoList : list.getData()) {

            if (getString(R.string.tool_one_key_speed).contains(mTitle)) { //一键加速
                if (PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
                if (PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId2 = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) { //超强省电
                if (PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
                if (PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId2 = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {//通知栏清理
                if (PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
                if (PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId2 = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {//微信专情
                if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
                if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId2 = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) { //手机降温
                if (PositionId.KEY_COOL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_COOL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_COOL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
                if (PositionId.KEY_COOL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId2 = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.tool_qq_clear).contains(mTitle)) { //QQ专清
                if (PositionId.KEY_QQ.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_QQ.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_QQ.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
                if (PositionId.KEY_QQ.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId2 = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.tool_phone_clean).contains(mTitle)) { //手机清理
                if (PositionId.KEY_PHONE.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_PHONE.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_PHONE.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
                if (PositionId.KEY_PHONE.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId2 = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.game_quicken).contains(mTitle)) { //游戏加速
                if (PositionId.KEY_GAME.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_GAME.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_GAME.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
                if (PositionId.KEY_GAME.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId2 = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.virus_kill).contains(mTitle)) { //病毒查杀
                if (PositionId.KEY_VIRUS.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_VIRUS.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_VIRUS.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
                if (PositionId.KEY_VIRUS.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId2 = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.network_quicken).contains(mTitle)) { //网络加速
                if (PositionId.KEY_NET.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_NET.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_NET.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
                if (PositionId.KEY_NET.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId2 = switchInfoList.getSecondAdvertId();
                }
            } else { //建议清理
                if (PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                }
                if (PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
                if (PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mSecondAdvertId2 = switchInfoList.getSecondAdvertId();
                }
            }
        }
        Log.d(TAG, "mAdvertId=" + mAdvertId);
        Log.d(TAG, "mAdvertId2=" + mAdvertId2);
        Log.d(TAG, "mSecondAdvertId=" + mSecondAdvertId);
        Log.d(TAG, "mSecondAdvertId2=" + mSecondAdvertId2);
        loadListAd();
        loadListAd2();
    }

    /**
     * 拉取广告开关失败
     *
     * @return
     */
    public void getSwitchInfoListFail() {

    }

    /**
     * 拉取插屏广告开关成功
     *
     * @return
     */
    public void getScreentSwitchSuccess(SwitchInfoList list) {
        for (SwitchInfoList.DataBean switchInfoList : list.getData()) {
            if (getString(R.string.tool_suggest_clean).contains(mTitle) && PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey())) { //建议清理
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
                mAdvertScreenId = switchInfoList.getAdvertId();
                mSecondAdvertScreenId = switchInfoList.getSecondAdvertId();
            } else if (getString(R.string.tool_one_key_speed).contains(mTitle) && PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey())) { //一键加速
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
                mAdvertScreenId = switchInfoList.getAdvertId();
                mSecondAdvertScreenId = switchInfoList.getSecondAdvertId();
            } else if (getString(R.string.tool_super_power_saving).contains(mTitle) && PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey())) { //超强省电
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
                mAdvertScreenId = switchInfoList.getAdvertId();
                mSecondAdvertScreenId = switchInfoList.getSecondAdvertId();
            } else if (getString(R.string.tool_notification_clean).contains(mTitle) && PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey())) {//通知栏清理
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
                mAdvertScreenId = switchInfoList.getAdvertId();
                mSecondAdvertScreenId = switchInfoList.getSecondAdvertId();
            } else if (getString(R.string.tool_chat_clear).contains(mTitle)) { //微信清理
                if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey())) {
                    isScreenSwitchOpen = switchInfoList.isOpen();
                    mScreenShowCount = switchInfoList.getShowRate();
                    mAdvertScreenId = switchInfoList.getAdvertId();
                    mSecondAdvertScreenId = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle) && PositionId.KEY_COOL.equals(switchInfoList.getConfigKey())) { //手机降温
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
                mAdvertScreenId = switchInfoList.getAdvertId();
                mSecondAdvertScreenId = switchInfoList.getSecondAdvertId();
            } else if (getString(R.string.tool_qq_clear).contains(mTitle) && PositionId.KEY_QQ.equals(switchInfoList.getConfigKey())) { //QQ专清
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
                mAdvertScreenId = switchInfoList.getAdvertId();
                mSecondAdvertScreenId = switchInfoList.getSecondAdvertId();
            } else if (getString(R.string.tool_phone_clean).contains(mTitle) && PositionId.KEY_PHONE.equals(switchInfoList.getConfigKey())) { //手机清理
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
                mAdvertScreenId = switchInfoList.getAdvertId();
                mSecondAdvertScreenId = switchInfoList.getSecondAdvertId();
            } else if (getString(R.string.game_quicken).contains(mTitle) && PositionId.KEY_GAME.equals(switchInfoList.getConfigKey())) { //游戏加速
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
                mAdvertScreenId = switchInfoList.getAdvertId();
                mSecondAdvertScreenId = switchInfoList.getSecondAdvertId();
            } else if (getString(R.string.virus_kill).contains(mTitle) && PositionId.KEY_VIRUS.equals(switchInfoList.getConfigKey())) { //病毒查杀
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
                mAdvertScreenId = switchInfoList.getAdvertId();
                mSecondAdvertScreenId = switchInfoList.getSecondAdvertId();
            } else if (getString(R.string.network_quicken).contains(mTitle) && PositionId.KEY_NET.equals(switchInfoList.getConfigKey())) { //网络加速
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
                mAdvertScreenId = switchInfoList.getAdvertId();
                mSecondAdvertScreenId = switchInfoList.getSecondAdvertId();
            }
        }
        loadListAdScreen();
    }

    /**
     * 优量汇广告位1
     */
    private void initNativeUnifiedAD() {
        mAdManager = new NativeUnifiedAD(this, PositionId.APPID, mSecondAdvertId, new NativeADUnifiedListener() {

            @Override
            public void onNoAD(AdError adError) {
                mContainer.setVisibility(View.GONE);
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mSecondAdvertId, "优量汇", "fail", sourcePage, currentPage);
                Log.d(TAG, "onNoAd error code: " + adError.getErrorCode() + ", error msg: " + adError.getErrorMsg());
            }

            @Override
            public void onADLoaded(List<NativeUnifiedADData> ads) {
                Log.d(TAG, "ads.size()=" + ads.size());
                if (ads != null && ads.size() > 0) {
                    mContainer.setVisibility(View.VISIBLE);
                    Message msg = Message.obtain();
                    msg.what = MSG_INIT_AD;
                    mNativeUnifiedADData = ads.get(0);
                    msg.obj = mNativeUnifiedADData;
                    mHandler.sendMessage(msg);
                }
            }
        });
        /**
         * 如果广告位支持视频广告，强烈建议在调用loadData请求广告前，调用下面两个方法，有助于提高视频广告的eCPM值 <br/>
         * 如果广告位仅支持图文广告，则无需调用
         */

        /**
         * 设置本次拉取的视频广告，从用户角度看到的视频播放策略<p/>
         *
         * "用户角度"特指用户看到的情况，并非SDK是否自动播放，与自动播放策略AutoPlayPolicy的取值并非一一对应 <br/>
         *
         * 例如开发者设置了VideoOption.AutoPlayPolicy.NEVER，表示从不自动播放 <br/>
         * 但满足某种条件(如晚上10点)时，开发者调用了startVideo播放视频，这在用户看来仍然是自动播放的
         */
        mAdManager.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO); // 本次拉回的视频广告，从用户的角度看是自动播放的

        /**
         * 设置在视频广告播放前，用户看到显示广告容器的渲染者是SDK还是开发者 <p/>
         *
         * 一般来说，用户看到的广告容器都是SDK渲染的，但存在下面这种特殊情况： <br/>
         *
         * 1. 开发者将广告拉回后，未调用bindMediaView，而是用自己的ImageView显示视频的封面图 <br/>
         * 2. 用户点击封面图后，打开一个新的页面，调用bindMediaView，此时才会用到SDK的容器 <br/>
         * 3. 这种情形下，用户先看到的广告容器就是开发者自己渲染的，其值为VideoADContainerRender.DEV
         * 4. 如果觉得抽象，可以参考NativeADUnifiedDevRenderContainerActivity的实现
         */
        mAdManager.setVideoADContainerRender(VideoOption.VideoADContainerRender.SDK); // 视频播放前，用户看到的广告容器是由SDK渲染的
        mAdManager.loadData(AD_COUNT);

    }

    /**
     * 优量汇广告位2
     */
    private void initNativeUnifiedAD2() {
        mAdManager2 = new NativeUnifiedAD(this, PositionId.APPID, mSecondAdvertId2, new NativeADUnifiedListener() {
            @Override
            public void onNoAD(AdError adError) {
                mContainer2.setVisibility(View.GONE);
                StatisticsUtils.customADRequest("ad_request", "广告请求", "2", mSecondAdvertId2, "优量汇", "fail", sourcePage, currentPage);
                Log.d(TAG, "onNoAd error code222: " + adError.getErrorCode() + ", error msg: " + adError.getErrorMsg());
            }

            @Override
            public void onADLoaded(List<NativeUnifiedADData> ads) {
                Log.d(TAG, "ads.size()2222=" + ads.size());
                if (ads != null && ads.size() > 0) {
                    mContainer2.setVisibility(View.VISIBLE);
                    Message msg2 = Message.obtain();
                    msg2.what = MSG_INIT_AD2;
                    mNativeUnifiedADData2 = ads.get(0);
                    msg2.obj = mNativeUnifiedADData2;
                    mHandler.sendMessage(msg2);
                }
            }
        });
        /**
         * 如果广告位支持视频广告，强烈建议在调用loadData请求广告前，调用下面两个方法，有助于提高视频广告的eCPM值 <br/>
         * 如果广告位仅支持图文广告，则无需调用
         */

        /**
         * 设置本次拉取的视频广告，从用户角度看到的视频播放策略<p/>
         *
         * "用户角度"特指用户看到的情况，并非SDK是否自动播放，与自动播放策略AutoPlayPolicy的取值并非一一对应 <br/>
         *
         * 例如开发者设置了VideoOption.AutoPlayPolicy.NEVER，表示从不自动播放 <br/>
         * 但满足某种条件(如晚上10点)时，开发者调用了startVideo播放视频，这在用户看来仍然是自动播放的
         */
        mAdManager2.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO); // 本次拉回的视频广告，从用户的角度看是自动播放的

        /**
         * 设置在视频广告播放前，用户看到显示广告容器的渲染者是SDK还是开发者 <p/>
         *
         * 一般来说，用户看到的广告容器都是SDK渲染的，但存在下面这种特殊情况： <br/>
         *
         * 1. 开发者将广告拉回后，未调用bindMediaView，而是用自己的ImageView显示视频的封面图 <br/>
         * 2. 用户点击封面图后，打开一个新的页面，调用bindMediaView，此时才会用到SDK的容器 <br/>
         * 3. 这种情形下，用户先看到的广告容器就是开发者自己渲染的，其值为VideoADContainerRender.DEV
         * 4. 如果觉得抽象，可以参考NativeADUnifiedDevRenderContainerActivity的实现
         */
        mAdManager2.setVideoADContainerRender(VideoOption.VideoADContainerRender.SDK); // 视频播放前，用户看到的广告容器是由SDK渲染的
        mAdManager2.loadData(AD_COUNT);
    }

    private void changeUI(Intent intent) {
        if (intent != null) {
            String num = intent.getStringExtra("num");
            String unit = intent.getStringExtra("unit");
            mTvGb.setText(unit);
            if (TextUtils.isEmpty(mTitle))
                mTitle = getString(R.string.app_name);
            if (getString(R.string.app_name).contains(mTitle)) {
                //悟空清理
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
                if (PreferenceUtil.getIsCheckedAll()) {
                    CleanEvent cleanEvent = new CleanEvent();
                    cleanEvent.setCleanAminOver(true);
                    EventBus.getDefault().post(cleanEvent);
                }
                //建议清理
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                } else {
                    mTvSize.setText(num);
                    mTvQl.setText("已清理");
                }
            } else if (getString(R.string.tool_qq_clear).contains(mTitle)) {
                //QQ专清
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("手机很干净");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                } else {
                    mTvSize.setText(num);
                }
            } else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
                //一键加速
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已加速");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快试试其他功能吧！");
                } else {
                    mTvSize.setText(num);
                    mTvQl.setText("垃圾已清理");
                }
            } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
                //超强省电
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                } else {
                    mTvSize.setText(num);
                }
            } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {
                //通知栏清理
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("通知栏很干净");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他炫酷功能");
                } else {
                    mTvSize.setText(num);
                }
            } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {
                //微信专情
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已清理");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快试试其他功能吧！");
                } else {
                    mTvSize.setText(num);
                }
            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) {
                //手机降温
                mTvSize.setText("");
                int tem = new Random().nextInt(3) + 1;
                mTvGb.setText("成功降温" + tem + "°C");
                mTvGb.setTextSize(20);
                mTvQl.setText("60s后达到最佳降温效果");
                mTvSize.setText(num);
            } else if (getString(R.string.game_quicken).contains(mTitle)) {
                //游戏加速
                mTvSize.setText(num);
                mTvGb.setText("%");
                mTvQl.setText("已提速");
            } else if (getString(R.string.virus_kill).contains(mTitle)) {
                mTvSize.setText(R.string.virus_guard);
                mTvSize.setTextSize(20);
            } else if (getString(R.string.network_quicken).contains(mTitle)) {
                mTvSize.setText(num);
                mTvGb.setText("%");
                mTvQl.setText("已提速");
            }

            if (!PermissionUtils.isUsageAccessAllowed(this)) {
                iv_quicken.setImageResource(R.drawable.icon_yjjs_o_clean);
                tv_quicken.setTextColor(ContextCompat.getColor(this, R.color.color_FFAC01));
                tv_quicken.setText(getString(R.string.internal_storage_scale_hint));
            } else {
                GlideUtils.loadDrawble(this, R.drawable.icon_quicken_clean_gif, iv_quicken);
                tv_quicken.setTextColor(ContextCompat.getColor(this, R.color.color_FF4545));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tv_quicken.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
                } else {
                    tv_quicken.setText(getString(R.string.internal_storage_scale, String.valueOf(mRamScale)) + "%");
                }
            }

            if (!PermissionUtils.isUsageAccessAllowed(this)) {
                iv_power.setImageResource(R.drawable.icon_power_o_clean);
                tv_power.setTextColor(ContextCompat.getColor(this, R.color.color_FFAC01));
                tv_power.setText(getString(R.string.power_consumption_thread));
            } else {
                GlideUtils.loadDrawble(this, R.drawable.icon_power_clean_gif, iv_power);
                tv_power.setTextColor(ContextCompat.getColor(this, R.color.color_FF4545));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tv_power.setText(getString(R.string.power_consumption_num, NumberUtils.mathRandom(8, 15)));
                } else {
                    tv_power.setText(getString(R.string.power_consumption_num, processNum + ""));
                }
            }

            if (!NotifyUtils.isNotificationListenerEnabled()) {
                iv_notification.setImageResource(R.drawable.icon_notify_o_clean);
                tv_notification.setTextColor(ContextCompat.getColor(this, R.color.color_FFAC01));
                tv_notification.setText(R.string.find_harass_notify);
            } else {
                GlideUtils.loadDrawble(this, R.drawable.icon_notify_clean_gif, iv_notification);
                tv_notification.setTextColor(ContextCompat.getColor(this, R.color.color_FF4545));
                tv_notification.setText(getString(R.string.find_harass_notify_num, NotifyCleanManager.getInstance().getAllNotifications().size() + ""));
            }
        }
        //是否显示推荐功能（一键加速，超强省电，通知栏清理，微信专清，文件清理，手机降温）
        showTool();
    }

    /**
     * 是否显示推荐功能项
     */
    private void showTool() {
        mRecommendV.setVisibility(View.VISIBLE);
        if (!getString(R.string.tool_suggest_clean).contains(mTitle) && !PreferenceUtil.isCleanAllUsed()) {
            mShowCount++;
            v_clean_all.setVisibility(View.VISIBLE);
            line_clean_all.setVisibility(View.VISIBLE);
        }
        if (!getString(R.string.tool_one_key_speed).contains(mTitle)) {
            if (!PermissionUtils.isUsageAccessAllowed(this)) {
                mShowCount++;
                v_quicken.setVisibility(View.VISIBLE);
                line_quicken.setVisibility(View.VISIBLE);
            } else if (!PreferenceUtil.isCleanJiaSuUsed()) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || mRamScale > 20) {
                    mShowCount++;
                    v_quicken.setVisibility(View.VISIBLE);
                    line_quicken.setVisibility(View.VISIBLE);
                }
            }
        }
        if (!getString(R.string.tool_super_power_saving).contains(mTitle)) {
            if (!PermissionUtils.isUsageAccessAllowed(this)) {
                mShowCount++;
                v_power.setVisibility(View.VISIBLE);
                if (mShowCount < 3) {
                    line_power.setVisibility(View.VISIBLE);
                }
            } else if (!PreferenceUtil.isCleanPowerUsed()) {
                // 超强省电间隔时间至少3分钟 否则隐藏
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || processNum > 0) {
                    mShowCount++;
                    v_power.setVisibility(View.VISIBLE);
                    if (mShowCount < 3) {
                        line_power.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        if (!getString(R.string.tool_notification_clean).contains(mTitle)) {
            if (mShowCount >= 3) return;
            if (!NotifyUtils.isNotificationListenerEnabled()) {
                // 通知栏清理间隔时间至少3分钟 否则隐藏
                mShowCount++;
                v_notification.setVisibility(View.VISIBLE);
                if (mShowCount < 3) {
                    line_notification.setVisibility(View.VISIBLE);
                }
            } else if (!PreferenceUtil.isCleanNotifyUsed()) {
                if (NotifyUtils.isNotificationListenerEnabled() && NotifyCleanManager.getInstance().getAllNotifications().size() > 0) {
                    // 通知栏清理间隔时间至少3分钟 否则隐藏
                    mShowCount++;
                    v_notification.setVisibility(View.VISIBLE);
                    if (mShowCount < 3) {
                        line_notification.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        if (!getString(R.string.tool_chat_clear).contains(mTitle)) {
            if (!PreferenceUtil.isCleanWechatUsed()) {
                // 微信清理间隔时间至少3分钟 否则隐藏功能项
                if (mShowCount >= 3) return;
                mShowCount++;
                v_wechat.setVisibility(View.VISIBLE);
                if (mShowCount < 3) {
                    line_wechat.setVisibility(View.VISIBLE);
                }
            }
        }
        if (!getString(R.string.game_quicken).contains(mTitle) && !PreferenceUtil.isCleanGameUsed()) {
            if (mShowCount >= 3) return;
            mShowCount++;
            v_game.setVisibility(View.VISIBLE);
            line_game.setVisibility(View.VISIBLE);
        }
        if (!getString(R.string.tool_phone_temperature_low).contains(mTitle) && !PreferenceUtil.isCleanCoolUsed()) {
            // 手机降温间隔时间至少3分钟 否则隐藏
            if (mShowCount >= 3) return;
            mShowCount++;
            v_cool.setVisibility(View.VISIBLE);
        }

        //文件清理
        if (mShowCount >= 3) return;
        mShowCount++;
        v_file.setVisibility(View.VISIBLE);
        if (mShowCount < 3) {
            line_file.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Build.VERSION.SDK_INT < 26) {
            mPresenter.getAccessListBelow();
        } else {
            changeUI(getIntent());
        }
    }

    @Override
    public void onClick(View v) {
        String functionName = "";
        String functionPosition = "";
        switch (v.getId()) {
            case R.id.v_clean_all:
                functionName = "建议清理";
                functionPosition = "1";
                allClean();
                break;
            case R.id.v_quicken:
                //一键加速
                functionName = "一键加速";
                functionPosition = "2";
                speedClean();
                break;
            case R.id.v_power:
                //超强省电
                v_power.setEnabled(false);
                functionName = "超强省电";
                functionPosition = "3";
                powerClean();
                break;
            case R.id.v_notification:
                //通知栏清理
                functionName = "通知栏清理";
                functionPosition = "4";
                notificationClean();
                break;
            case R.id.v_wechat:
                //微信专清
                functionName = "微信专清";
                functionPosition = "5";
                weChatClean();
                break;
            case R.id.v_game:
                functionName = "游戏加速";
                functionPosition = "6";
                gameClean();
                break;
            case R.id.v_cool:
                //手机降温
                functionName = "手机降温";
                functionPosition = "7";
                coolingClean();
                break;
            case R.id.v_file:
                //文件清理
                functionName = "文件清理";
                functionPosition = "8";
                fileClean();
                break;


        }
        //1.21 版本推荐清理_标识sourcePage,其他""
        StatisticsUtils.trackFunctionClickItem("recommendation_function_click", functionName, getIntent().hasExtra("home") ? "home_page" : sourcePage, currentPage, functionName, functionPosition);
//        finish();
    }

    /**
     * @param event
     */
    @Subscribe
    public void finishCleanFinishActivityEvent(FinishCleanFinishActivityEvent event) {
        finish();
    }

    /**
     * 建议清理
     */
    public void allClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId(currentPage);
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.ONKEY);
        startActivity(NowCleanActivity.class);
    }

    /**
     * 游戏加速
     */
    public void gameClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId(currentPage);
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.ONKEY);
        startActivity(GameActivity.class);
    }

    /**
     * 一键加速
     */
    public void speedClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId("boost_click");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.ONKEY);

        Bundle bundle = new Bundle();
        bundle.putString(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_one_key_speed));
        startActivity(PhoneAccessActivity.class, bundle);
    }

    /**
     * 超强省电
     */
    public void powerClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId("powersave_click");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.SUPER_POWER_SAVING);
        startActivity(PhoneSuperPowerActivity.class);
    }

    /**
     * 通知栏清理
     */
    public void notificationClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId("notification_clean_click");
        //通知栏清理
        NotifyCleanManager.startNotificationCleanActivity(this, 0);
    }

    /**
     * 微信专清
     */
    public void weChatClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId("wxclean_click");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.WETCHAT_CLEAN);

        if (!AndroidUtil.isAppInstalled(SpCacheConfig.CHAT_PACKAGE)) {
            ToastUtils.showShort(R.string.tool_no_install_chat);
            return;
        }
        if (PreferenceUtil.getWeChatCleanTime()) {
            // 每次清理间隔 至少3秒
            startActivity(WechatCleanHomeActivity.class);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.tool_chat_clear));
            bundle.putString("num", "");
            bundle.putString("unit", "");
            startActivity(NewCleanFinishActivity.class, bundle);
        }
    }

    /**
     * 文件清理
     */
    public void fileClean() {
        startActivity(FileManagerHomeActivity.class);
    }

    /**
     * 手机降温
     */
    public void coolingClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId("cooling_click");
        startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
    }

    protected void setListener() {
        v_quicken.setOnClickListener(this);
        v_power.setOnClickListener(this);
        v_notification.setOnClickListener(this);
        v_wechat.setOnClickListener(this);
        v_file.setOnClickListener(this);
        v_cool.setOnClickListener(this);
        v_game.setOnClickListener(this);
        v_clean_all.setOnClickListener(this);

        mBtnLeft.setOnClickListener(v -> {
            if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
                StatisticsUtils.trackClick("return_back", returnEventName, sourcePage, currentPage);
            } else {
                StatisticsUtils.trackClick("return_click", returnEventName, sourcePage, currentPage);
            }

            //插屏广告拉取失败禁止跳转到插屏广告页
            if (mIsScreenAdSuccess) {
                finish();
                return;
            }
            //使用的第mScreenShowCount几倍次 并且插屏开关打开 展示
            if (isScreenSwitchOpen) {
                int count = 0;
                boolean isClick = false;
                if (getString(R.string.tool_one_key_speed).contains(mTitle)) { //一键加速
                    count = PreferenceUtil.getCleanFinishClickJiaSuCount();
                    isClick = (PreferenceUtil.getCleanFinishClickJiaSuCount() % mScreenShowCount == 0);
                } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) { //超强省电
                    count = PreferenceUtil.getCleanFinishClickPowerCount();
                    isClick = (PreferenceUtil.getCleanFinishClickPowerCount() % mScreenShowCount == 0);
                } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {//通知栏清理
                    count = PreferenceUtil.getCleanFinishClickNotifyCount();
                    isClick = (PreferenceUtil.getCleanFinishClickNotifyCount() % mScreenShowCount == 0);
                } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {//微信专情
                    count = PreferenceUtil.getCleanFinishClickWechatCount();
                    isClick = (PreferenceUtil.getCleanFinishClickWechatCount() % mScreenShowCount == 0);
                } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) { //手机降温
                    count = PreferenceUtil.getCleanFinishClickCoolCount();
                    isClick = (PreferenceUtil.getCleanFinishClickCoolCount() % mScreenShowCount == 0);
                } else if (getString(R.string.tool_qq_clear).contains(mTitle)) { //QQ专清
                    count = PreferenceUtil.getCleanFinishClickQQCount();
                    isClick = (PreferenceUtil.getCleanFinishClickQQCount() % mScreenShowCount == 0);
                } else if (getString(R.string.tool_phone_clean).contains(mTitle)) { //手机清理
                    count = PreferenceUtil.getCleanFinishClickPhoneCount();
                    isClick = (PreferenceUtil.getCleanFinishClickPhoneCount() % mScreenShowCount == 0);
                } else if (getString(R.string.game_quicken).contains(mTitle)) { //游戏加速
                    count = PreferenceUtil.getCleanFinishClickGameCount();
                    isClick = (PreferenceUtil.getCleanFinishClickGameCount() % mScreenShowCount == 0);
                } else if (getString(R.string.virus_kill).contains(mTitle)) { //病毒查杀
                    count = PreferenceUtil.getCleanFinishClickVirusCount();
                    isClick = (PreferenceUtil.getCleanFinishClickVirusCount() % mScreenShowCount == 0);
                } else if (getString(R.string.virus_kill).contains(mTitle)) { //网络加速
                    count = PreferenceUtil.getCleanFinishClickNetCount();
                    isClick = (PreferenceUtil.getCleanFinishClickNetCount() % mScreenShowCount == 0);
                } else { //建议清理
                    count = PreferenceUtil.getCleanFinishClickCount();
                    isClick = (PreferenceUtil.getCleanFinishClickCount() % mScreenShowCount == 0);
                }

                if (count == 0 || isClick) {
                    startActivity(new Intent(this, InsertScreenFinishActivity.class).putExtra("title", mTitle));
                }
            }

            if (getString(R.string.tool_one_key_speed).contains(mTitle)) { //一键加速
                PreferenceUtil.saveCleanFinishClickJiaSuCount(PreferenceUtil.getCleanFinishClickJiaSuCount() + 1);
            } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) { //超强省电
                PreferenceUtil.saveCleanFinishClickPowerCount(PreferenceUtil.getCleanFinishClickPowerCount() + 1);
            } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {//通知栏清理
                PreferenceUtil.saveCleanFinishClickNotifyCount(PreferenceUtil.getCleanFinishClickNotifyCount() + 1);
            } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {//微信专情
                PreferenceUtil.saveCleanFinishClickWechatCount(PreferenceUtil.getCleanFinishClickWechatCount() + 1);
            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) { //手机降温
                PreferenceUtil.saveCleanFinishClickCoolCount(PreferenceUtil.getCleanFinishClickCoolCount() + 1);
            } else if (getString(R.string.tool_qq_clear).contains(mTitle)) { //QQ专清
                PreferenceUtil.saveCleanFinishClickQQCount(PreferenceUtil.getCleanFinishClickQQCount() + 1);
            } else if (getString(R.string.tool_phone_clean).contains(mTitle)) { //手机清理
                PreferenceUtil.saveCleanFinishClickPhoneCount(PreferenceUtil.getCleanFinishClickPhoneCount() + 1);
            } else if (getString(R.string.game_quicken).contains(mTitle)) { //游戏加速
                PreferenceUtil.saveCleanFinishClickGameCount(PreferenceUtil.getCleanFinishClickGameCount() + 1);
            } else if (getString(R.string.virus_kill).contains(mTitle)) { //病毒查杀
                PreferenceUtil.saveCleanFinishClickVirusCount(PreferenceUtil.getCleanFinishClickVirusCount() + 1);
            } else if (getString(R.string.network_quicken).contains(mTitle)) { //网络加速
                PreferenceUtil.saveCleanFinishClickNetCount(PreferenceUtil.getCleanFinishClickNetCount() + 1);
            } else { //建议清理
                PreferenceUtil.saveCleanFinishClickCount(PreferenceUtil.getCleanFinishClickCount() + 1);
            }
            finish();
        });

        /*mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                startLoadData();
            }
        });*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    //获取第一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findFirstVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    //recyclerView滑动到底部再滑动回顶部后重新执行动画
                    if (null != mLottieAd && lastVisibleItem == 1) {
                        mLottieAd.playAnimation();
                    }
                }
            });
        } else {
            mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    //获取第一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findFirstVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    //recyclerView滑动到底部再滑动回顶部后重新执行动画
                    if (null != mLottieAd && lastVisibleItem == 1) {
                        mLottieAd.playAnimation();
                    }
                }
            });
        }
    }

    protected void loadData() {
        //页面创建事件埋点
        StatisticsUtils.customTrackEvent(createEventCode, createEventName, sourcePage, currentPage);
//        startLoadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getString(R.string.tool_one_key_speed).contains(mTitle) || getString(R.string.game_quicken).contains(mTitle)) {
            StatisticsUtils.trackClick("system_return_back_click", sysReturnEventName, sourcePage, currentPage);
        } else {
            StatisticsUtils.trackClick("system_return_click", sysReturnEventName, sourcePage, currentPage);
        }

        //插屏广告老去失败禁止跳转到插屏广告页
        if (mIsScreenAdSuccess) {
            finish();
            return;
        }

        //使用的第mScreenShowCount几倍次 并且插屏开关打开 展示
        if (isScreenSwitchOpen) {
            int count = 0;
            boolean isClick = false;
            if (getString(R.string.tool_one_key_speed).contains(mTitle)) { //一键加速
                count = PreferenceUtil.getCleanFinishClickJiaSuCount();
                isClick = (PreferenceUtil.getCleanFinishClickJiaSuCount() % mScreenShowCount == 0);
            } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) { //超强省电
                count = PreferenceUtil.getCleanFinishClickPowerCount();
                isClick = (PreferenceUtil.getCleanFinishClickPowerCount() % mScreenShowCount == 0);
            } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {//通知栏清理
                count = PreferenceUtil.getCleanFinishClickNotifyCount();
                isClick = (PreferenceUtil.getCleanFinishClickNotifyCount() % mScreenShowCount == 0);
            } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {//微信专情
                count = PreferenceUtil.getCleanFinishClickWechatCount();
                isClick = (PreferenceUtil.getCleanFinishClickWechatCount() % mScreenShowCount == 0);
            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) { //手机降温
                count = PreferenceUtil.getCleanFinishClickCoolCount();
                isClick = (PreferenceUtil.getCleanFinishClickCoolCount() % mScreenShowCount == 0);
            } else if (getString(R.string.tool_qq_clear).contains(mTitle)) { //QQ专清
                count = PreferenceUtil.getCleanFinishClickQQCount();
                isClick = (PreferenceUtil.getCleanFinishClickQQCount() % mScreenShowCount == 0);
            } else if (getString(R.string.tool_phone_clean).contains(mTitle)) { //手机清理
                count = PreferenceUtil.getCleanFinishClickPhoneCount();
                isClick = (PreferenceUtil.getCleanFinishClickPhoneCount() % mScreenShowCount == 0);
            } else if (getString(R.string.game_quicken).contains(mTitle)) { //游戏加速
                count = PreferenceUtil.getCleanFinishClickGameCount();
                isClick = (PreferenceUtil.getCleanFinishClickGameCount() % mScreenShowCount == 0);
            } else if (getString(R.string.virus_kill).contains(mTitle)) { //病毒查杀
                count = PreferenceUtil.getCleanFinishClickVirusCount();
                isClick = (PreferenceUtil.getCleanFinishClickVirusCount() % mScreenShowCount == 0);
            } else if (getString(R.string.network_quicken).contains(mTitle)) { //网络加速
                count = PreferenceUtil.getCleanFinishClickNetCount();
                isClick = (PreferenceUtil.getCleanFinishClickNetCount() % mScreenShowCount == 0);
            } else { //建议清理
                count = PreferenceUtil.getCleanFinishClickCount();
                isClick = (PreferenceUtil.getCleanFinishClickCount() % mScreenShowCount == 0);
            }
            if (count == 0 || isClick) {
                startActivity(new Intent(this, InsertScreenFinishActivity.class).putExtra("title", mTitle));
            }
        }

        if (getString(R.string.tool_one_key_speed).contains(mTitle)) { //一键加速
            PreferenceUtil.saveCleanFinishClickJiaSuCount(PreferenceUtil.getCleanFinishClickJiaSuCount() + 1);
        } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) { //超强省电
            PreferenceUtil.saveCleanFinishClickPowerCount(PreferenceUtil.getCleanFinishClickPowerCount() + 1);
        } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {//通知栏清理
            PreferenceUtil.saveCleanFinishClickNotifyCount(PreferenceUtil.getCleanFinishClickNotifyCount() + 1);
        } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {//微信专情
            PreferenceUtil.saveCleanFinishClickWechatCount(PreferenceUtil.getCleanFinishClickWechatCount() + 1);
        } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) { //手机降温
            PreferenceUtil.saveCleanFinishClickCoolCount(PreferenceUtil.getCleanFinishClickCoolCount() + 1);
        } else if (getString(R.string.tool_qq_clear).contains(mTitle)) { //QQ专清
            PreferenceUtil.saveCleanFinishClickQQCount(PreferenceUtil.getCleanFinishClickQQCount() + 1);
        } else if (getString(R.string.tool_phone_clean).contains(mTitle)) { //手机清理
            PreferenceUtil.saveCleanFinishClickPhoneCount(PreferenceUtil.getCleanFinishClickPhoneCount() + 1);
        } else if (getString(R.string.game_quicken).contains(mTitle)) { //游戏加速
            PreferenceUtil.saveCleanFinishClickGameCount(PreferenceUtil.getCleanFinishClickGameCount() + 1);
        } else if (getString(R.string.virus_kill).contains(mTitle)) { //病毒查杀
            PreferenceUtil.saveCleanFinishClickVirusCount(PreferenceUtil.getCleanFinishClickVirusCount() + 1);
        } else if (getString(R.string.network_quicken).contains(mTitle)) { //网络加速
            PreferenceUtil.saveCleanFinishClickNetCount(PreferenceUtil.getCleanFinishClickNetCount() + 1);
        } else { //建议清理
            PreferenceUtil.saveCleanFinishClickCount(PreferenceUtil.getCleanFinishClickCount() + 1);
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        v_power.setEnabled(true);
        if (Build.VERSION.SDK_INT < 26) {
            mPresenter.getAccessListBelow();
        } else {
            changeUI(getIntent());
        }
        /*---------------------------------------- 埋点---------------------------------------------------------------------*/
        if (getString(R.string.app_name).contains(mTitle)) {
            //清理管家极速版
            NiuDataAPI.onPageStart("clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
            //一键加速
            NiuDataAPI.onPageStart("boost_success_page_view_page", "加速结果出现时");
        } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
            //1.2.1清理完成页面
            NiuDataAPI.onPageStart("clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_phone_clean).contains(mTitle)) {
            //手机清理
            NiuDataAPI.onPageStart("clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
            //超强省电
            NiuDataAPI.onPageStart("powersave_success_page_view_page", "省电结果出现时");
        } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {
            //微信专情
            NiuDataAPI.onPageStart("wxclean_success_page_view_page", "微信清理结果页出现时");
        } else if (getString(R.string.tool_qq_clear).contains(mTitle)) {
            //QQ专清
            NiuDataAPI.onPageStart("clean_up_page_view_immediately", "清理完成页浏览");
        } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {
            //通知栏清理
            NiuDataAPI.onPageStart("notification_clean_success_page_view_page", "通知栏清理结果页出现时");
        } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) {
            //手机降温
            NiuDataAPI.onPageStart("cooling_success_page_view_page", "降温结果页出现时");
        } else if (getString(R.string.game_quicken).contains(mTitle)) {
            //游戏加速
            NiuDataAPI.onPageStart("gameboost_success_page_view_page", "游戏加速结果页展示浏览");
        } else if (getString(R.string.virus_kill).contains(mTitle)) {
            NiuDataAPI.onPageStart("virus_killing_success_page_view_page", "病毒查杀结果页展示浏览");
        } else if (getString(R.string.network_quicken).contains(mTitle)) {
            NiuDataAPI.onPageStart("network_acceleration_success_page_view_page", "网络加速结果页展示浏览");
        } else {
            NiuDataAPI.onPageStart("clean_up_page_view_immediately", "清理完成页浏览");
        }

        super.onResume();
        if (mNativeUnifiedADData != null) {
            // 必须要在Actiivty.onResume()时通知到广告数据，以便重置广告恢复状态
            mNativeUnifiedADData.resume();
        }
        if (mNativeUnifiedADData2 != null) {
            // 必须要在Actiivty.onResume()时通知到广告数据，以便重置广告恢复状态
            mNativeUnifiedADData2.resume();
        }

        StatusBarCompat.setStatusBarColor(mContext, getResources().getColor(R.color.color_27D698), true);

        if (null != mAnimationDrawable) {
            mAnimationDrawable.start();
        }
    }

    @Override
    protected void onPause() {
        Jzvd.releaseAllVideos();
        if (getString(R.string.app_name).contains(mTitle)) {
            //清理管家极速版
            NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, "clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
            //一键加速
            NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, "boost_success_page_view_page", "加速结果出现时");
        } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
            //1.2.1清理完成页面
            NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, "clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_phone_clean).contains(mTitle)) {
            //手机清理
            NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, "clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
            //超强省电
            NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, "powersave_success_page_view_page", "省电结果出现时");
        } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {
            //微信专情
            NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, "wxclean_success_page_view_page", "微信清理结果页出现时");
        } else if (getString(R.string.tool_qq_clear).contains(mTitle)) {
            //QQ专清
//            NiuDataAPI.onPageStart("clean_up_page_view_immediately", "清理完成页浏览");
        } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {
            //通知栏清理
            NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, "notification_clean_success_page_view_page", "通知栏清理结果页出现时");
        } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) {
            //手机降温
            NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, "cooling_success_page_view_page", "降温结果页出现时");
        } else if (getString(R.string.game_quicken).contains(mTitle)) {
            //游戏加速
            NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, "gameboost_success_page_view_page", "游戏加速结果页展示浏览");
        } else if (getString(R.string.virus_kill).contains(mTitle)) {
            NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, "virus_killing_success_page_view_page", "病毒查杀结果页展示浏览");
        } else if (getString(R.string.network_quicken).contains(mTitle)) {
            NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, "network_acceleration_success_page_view_page", "网络加速结果页展示浏览");
        } else {
            NiuDataAPI.onPageEnd("clean_up_page_view_immediately", "清理完成页浏览");
        }

        if (null != mAnimationDrawable) {
            mAnimationDrawable.stop();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mNativeUnifiedADData != null) {
            // 必须要在Actiivty.destroy()时通知到广告数据，以便释放内存
            mNativeUnifiedADData.destroy();
        }
        if (mNativeUnifiedADData2 != null) {
            // 必须要在Actiivty.destroy()时通知到广告数据，以便释放内存
            mNativeUnifiedADData2.destroy();
        }

        if (mRecyclerView != null) {
            mRecyclerView.destroy(); // this will totally release XR's memory
            mRecyclerView = null;
        }
        //Umeng --- Caused by: java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity
        if (Util.isOnMainThread()) {
            Glide.get(this).clearMemory();
        }
        //Umeng ---
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (null != mHandlerScreen) {
            mHandlerScreen.removeCallbacksAndMessages(null);
        }
        if (null != mAnimationDrawable && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
    }

    public void startLoadData() {
        if (!NetworkUtils.isNetConnected()) {

            if (mRecyclerView != null)
                mRecyclerView.loadMoreComplete();

            return;
        }

        if (mType != null) {
            if (mType == NewsType.VIDEO) {
                loadVideoData();
            } else {
                loadNewsData();
            }
        }
    }

    private void loadNewsData() {
        String type = mType.getName();
        String url = SpCacheConfig.RUISHI_BASEURL + "bd/news/list?media=563&submedia=779&category=" + type + "&page=" + page_index;
        EHttp.get(this, url, new ApiCallback<List<NewsItemInfoRuishi>>(null) {
            @Override
            public void onFailure(Throwable e) {
            }

            @Override
            public void onSuccess(List<NewsItemInfoRuishi> result) {
                if (result != null && result.size() > 0) {
                    page_index++;
                    mNewsAdapter.addData(result);
                }
            }

            @Override
            public void onComplete() {
                if (mRecyclerView != null)
                    mRecyclerView.loadMoreComplete();

            }
        });
    }

    private void loadVideoData() {
        //请求参数设置：比如一个json字符串
        JSONObject jsonObject = new JSONObject();
        try {
            String lastId = SPUtil.getLastNewsID(mType.getName());
            jsonObject.put("pageSize", PAGE_NUM);
            jsonObject.put("lastId", lastId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpRequest request = new HttpRequest.Builder()
                .addBodyParams(jsonObject.toString())
                .build();

        EHttp.post(this, BuildConfig.VIDEO_BASE_URL, request, new ApiCallback<ArrayList<VideoItemInfo>>() {

            @Override
            public void onComplete() {
                if (mRecyclerView != null)
                    mRecyclerView.loadMoreComplete();

            }

            @Override
            public void onFailure(Throwable e) {
                Log.i("123", e.toString());
            }

            @Override
            public void onSuccess(ArrayList<VideoItemInfo> result) {
                if (result != null && result.size() > 0) {
                    SPUtil.setLastNewsID(mType.getName(), result.get(result.size() - 1).videoId);
                    mNewsAdapter.addData(result);

                }
            }
        });
    }

    private final H mHandler = new H(this);
    private static final int AD_COUNT = 1;
    private static final int MSG_INIT_AD = 0;
    private static final int MSG_VIDEO_START = 1;
    private static final int MSG_INIT_AD2 = 3;
    private static final int MSG_VIDEO_START2 = 4;

    @Override
    public void netError() {

    }

    private class H extends Handler {
        private WeakReference<NewCleanFinishActivity> mActivity;

        public H(NewCleanFinishActivity activity) {
            mActivity = new WeakReference<NewCleanFinishActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            NewCleanFinishActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case MSG_INIT_AD:
                        NativeUnifiedADData ad = (NativeUnifiedADData) msg.obj;
                        Log.d(TAG, String.format(Locale.getDefault(), "(pic_width,pic_height) = (%d , %d)", ad
                                        .getPictureWidth(),
                                ad.getPictureHeight()));
                        initAd(ad);
                        Log.d(TAG, "eCPM = " + mNativeUnifiedADData.getECPM() + " , eCPMLevel = " + mNativeUnifiedADData.getECPMLevel());
                        break;
                    case MSG_VIDEO_START:
                        iv_advert.setVisibility(View.GONE);
                        mMediaView.setVisibility(View.VISIBLE);
                        break;
                    case MSG_INIT_AD2:
                        NativeUnifiedADData ad2 = (NativeUnifiedADData) msg.obj;
                        Log.d(TAG, String.format(Locale.getDefault(), "(pic_width,pic_height) = (%d , %d)", ad2
                                        .getPictureWidth(),
                                ad2.getPictureHeight()));
                        initAd2(ad2);
                        Log.d(TAG, "eCPM = " + mNativeUnifiedADData2.getECPM() + " , eCPMLevel = " + mNativeUnifiedADData2.getECPMLevel());
                        break;
                    case MSG_VIDEO_START2:
                        iv_advert2.setVisibility(View.GONE);
                        mMediaView2.setVisibility(View.VISIBLE);
                        break;
                }
            }

        }

    }

    //加载广告
    private void initAd(final NativeUnifiedADData ad) {
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mSecondAdvertId, "优量汇", "success", sourcePage, currentPage);
        renderAdUi(ad);
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(v_advert);
        // 将布局与广告进行绑定
        ad.bindAdToView(this, mContainer, null, clickableViews);
        // 设置广告事件监听
        ad.setNativeAdEventListener(new NativeADEventListener() {
            @Override
            public void onADExposed() {
                //广告请求
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", mSecondAdvertId, "优量汇", sourcePage, currentPage, ad.getTitle());
                Log.d(TAG, "广告曝光");
            }

            @Override
            public void onADClicked() {
                Log.d(TAG, "onADClicked: " + " clickUrl: " + ad.ext.get("clickUrl"));
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", mSecondAdvertId, "优量汇", sourcePage, currentPage, ad.getTitle());

            }

            @Override
            public void onADError(AdError error) {
                Log.d(TAG, "错误回调 error code :" + error.getErrorCode()
                        + "  error msg: " + error.getErrorMsg());
            }

            @Override
            public void onADStatusChanged() {
                Log.d(TAG, "广告状态变化");
//                updateAdAction(mDownloadButton, ad);
            }
        });

        if (ad.getAdPatternType() == AdPatternType.NATIVE_VIDEO) { //视频类型
            mHandler.sendEmptyMessage(MSG_VIDEO_START);

            VideoOption.Builder builder = new VideoOption.Builder();
            builder.setAutoPlayMuted(true) //设置视频广告在预览页自动播放时是否静音
                    .setEnableDetailPage(true) //点击视频是否跳转到详情页
                    .setEnableUserControl(false) //设置是否允许用户在预览页点击视频播放器区域控制视频的暂停或播放
                    .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS);

            VideoOption videoOption = builder.build();
            // 视频广告需对MediaView进行绑定，MediaView必须为容器mContainer的子View
            ad.bindMediaView(mMediaView, videoOption,
                    // 视频相关回调
                    new NativeADMediaListener() {
                        @Override
                        public void onVideoInit() {
                            Log.d(TAG, "onVideoInit: ");
                        }

                        @Override
                        public void onVideoLoading() {
                            Log.d(TAG, "onVideoLoading: ");
                        }

                        @Override
                        public void onVideoReady() {
                            Log.d(TAG, "onVideoReady: ");
                        }

                        @Override
                        public void onVideoLoaded(int videoDuration) {
                            Log.d(TAG, "onVideoLoaded: ");

                        }

                        @Override
                        public void onVideoStart() {
                           /* Log.d(TAG, "onVideoStart: ");
                            if (mNativeUnifiedADData2 != null) {
                                mNativeUnifiedADData2.pauseVideo();
                            }*/
                        }

                        @Override
                        public void onVideoPause() {
                            Log.d(TAG, "onVideoPause: ");
                        }

                        @Override
                        public void onVideoResume() {
                           /* Log.d(TAG, "onVideoResume: ");
                            if (mNativeUnifiedADData2 != null) {
                                mNativeUnifiedADData2.pauseVideo();
                            }*/
                        }

                        @Override
                        public void onVideoCompleted() {
                            if (mNativeUnifiedADData != null) {
                                mNativeUnifiedADData.startVideo();
                            }
                            Log.d(TAG, "onVideoCompleted: ");
                        }

                        @Override
                        public void onVideoError(AdError error) {
                            Log.d(TAG, "onVideoError: ");
                        }

                        @Override
                        public void onVideoStop() {

                        }

                        @Override
                        public void onVideoClicked() {

                        }
                    });
        }
    }

    //加载广告2
    private void initAd2(final NativeUnifiedADData ad) {
        StatisticsUtils.customADRequest("ad_request", "广告请求", "2", mSecondAdvertId2, "优量汇", "success", sourcePage, currentPage);
        renderAdUi2(ad);
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(v_advert2);
        // 将布局与广告进行绑定
        ad.bindAdToView(this, mContainer2, null, clickableViews);
        // 设置广告事件监听
        ad.setNativeAdEventListener(new NativeADEventListener() {
            @Override
            public void onADExposed() {
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "2", mSecondAdvertId2, "优量汇", sourcePage, currentPage, ad.getTitle());
                Log.d(TAG, "广告曝光");
            }

            @Override
            public void onADClicked() {
                Log.d(TAG, "onADClicked: " + " clickUrl: " + ad.ext.get("clickUrl"));
                StatisticsUtils.clickAD("ad_click", "广告点击", "2", mSecondAdvertId2, "优量汇", sourcePage, currentPage, ad.getTitle());
            }

            @Override
            public void onADError(AdError error) {
                Log.d(TAG, "错误回调 error code :" + error.getErrorCode()
                        + "  error msg: " + error.getErrorMsg());
            }

            @Override
            public void onADStatusChanged() {
                Log.d(TAG, "广告状态变化");
                updateAdAction(tv_download2, ad);
            }
        });
        updateAdAction(tv_download2, ad);

        if (ad.getAdPatternType() == AdPatternType.NATIVE_VIDEO) { //视频类型
            mHandler.sendEmptyMessage(MSG_VIDEO_START2);

            VideoOption.Builder builder = new VideoOption.Builder();
            builder.setAutoPlayMuted(true) //设置视频广告在预览页自动播放时是否静音
                    .setEnableDetailPage(true) //点击视频是否跳转到详情页
                    .setEnableUserControl(false) //设置是否允许用户在预览页点击视频播放器区域控制视频的暂停或播放
                    .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS); //不自动播放
            VideoOption videoOption = builder.build();
            // 视频广告需对MediaView进行绑定，MediaView必须为容器mContainer的子View
            ad.bindMediaView(mMediaView2, videoOption,
                    // 视频相关回调
                    new NativeADMediaListener() {
                        @Override
                        public void onVideoInit() {
                            Log.d(TAG, "onVideoInit: ");
                        }

                        @Override
                        public void onVideoLoading() {
                            Log.d(TAG, "onVideoLoading: ");
                        }

                        @Override
                        public void onVideoReady() {
                            Log.d(TAG, "onVideoReady: ");
                        }

                        @Override
                        public void onVideoLoaded(int videoDuration) {
                            Log.d(TAG, "onVideoLoaded: ");

                        }

                        @Override
                        public void onVideoStart() {
                            Log.d(TAG, "onVideoStart: ");
                            /*if (mNativeUnifiedADData != null) {
                                mNativeUnifiedADData.pauseVideo();
                            }*/
                        }

                        @Override
                        public void onVideoPause() {
                            Log.d(TAG, "onVideoPause: ");
                        }

                        @Override
                        public void onVideoResume() {
                            Log.d(TAG, "onVideoResume: ");
                          /*  if (mNativeUnifiedADData != null) {
                                mNativeUnifiedADData.resumeVideo();
                            }*/
                        }

                        @Override
                        public void onVideoCompleted() {
                            if (mNativeUnifiedADData2 != null) {
                                mNativeUnifiedADData2.startVideo();
                            }
                            Log.d(TAG, "onVideoCompleted: ");
                        }

                        @Override
                        public void onVideoError(AdError error) {
                            Log.d(TAG, "onVideoError: ");
                        }

                        @Override
                        public void onVideoStop() {

                        }

                        @Override
                        public void onVideoClicked() {

                        }
                    });
        }
    }

    public static void updateAdAction(TextView button, NativeUnifiedADData ad) {
        if (!ad.isAppAd()) {
            button.setText("浏览");
            return;
        }
        switch (ad.getAppStatus()) {
            case 0:
                button.setText("立即下载");
                break;
            case 1:
                button.setText("立即启动");
                break;
            case 2:
                button.setText("立即更新");
                break;
            case 4:
                button.setText(ad.getProgress() + "%");
                break;
            case 8:
                button.setText("立即安装");
                break;
            case 16:
                button.setText("下载失败");
                break;
            default:
                button.setText("浏览");
                break;
        }
    }

    // 获取广告资源并加载到UI
    private void renderAdUi(NativeUnifiedADData ad) {
        int patternType = ad.getAdPatternType();
        Log.d("AD_DEMO", "广告类型=" + patternType);

        tv_advert.setText(ad.getTitle());
        tv_advert_content.setText(ad.getDesc());
        GlideUtils.loadRoundImage(this, ad.getIconUrl(), iv_advert_logo, 20);
        if (patternType == AdPatternType.NATIVE_VIDEO) {
            iv_advert.setVisibility(View.GONE);
        } else {
            GlideUtils.loadImage(this, ad.getImgUrl(), iv_advert);
        }

//        mLottieAd.useHardwareAcceleration(true);
        mLottieAd.setAnimation("clean_finish_download.json");
        mLottieAd.setImageAssetsFolder("images_clean_download");
        mLottieAd.playAnimation();
    }

    // 获取广告资源并加载到UI
    private void renderAdUi2(NativeUnifiedADData ad) {
        int patternType = ad.getAdPatternType();
        Log.d("AD_DEMO", "广告类型222=" + patternType);

        tv_advert2.setText(ad.getTitle());
        tv_advert_content2.setText(ad.getDesc());
        GlideUtils.loadRoundImage(this, ad.getIconUrl(), iv_advert_logo2, 20);
        if (patternType == AdPatternType.NATIVE_VIDEO) {
            iv_advert2.setVisibility(View.GONE);
        } else {
            GlideUtils.loadImage(this, ad.getImgUrl(), iv_advert2);
        }
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null || listInfo.size() <= 0) return;
        if (null == fileQueryUtils)
            fileQueryUtils = new FileQueryUtils();
        mRamScale = fileQueryUtils.computeTotalSize(listInfo);
        changeUI(getIntent());
    }


    /**
     * 初始化穿山甲
     */
    private void initChuanShanJia() {
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        mTTAdNative = ttAdManager.createAdNative(getApplicationContext());
        //申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
//        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
    }

    /**
     * 初始化穿山甲
     */
    private void initChuanShanJia2() {
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        mTTAdNative2 = ttAdManager.createAdNative(getApplicationContext());
        //申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
//        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
    }

    /**
     * 加载穿山甲广告位1
     */
    private void loadListAd() {
        //feed广告请求类型参数
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(mAdvertId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 320)
                .setAdCount(1)
                .build();
        //调用feed广告异步请求接口
        mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "穿山甲加载失败=" + message);
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mAdvertId, "穿山甲", "fail", sourcePage, currentPage);
                initNativeUnifiedAD();
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> ads) {
                //加载成功的回调 请确保您的代码足够健壮，可以处理异常情况；
                if (null == ads || ads.isEmpty()) return;
                Log.d(TAG, "穿山甲----广告请求成功--ads.size()=" + ads.size());
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mAdvertId, "穿山甲", "success", sourcePage, currentPage);
//                mLottieAd.useHardwareAcceleration(true);
                mLottieAd.setAnimation("clean_finish_download.json");
                mLottieAd.setImageAssetsFolder("images_clean_download");
                mLottieAd.playAnimation();
                mContainer.setVisibility(View.VISIBLE);
                tv_advert.setText(ads.get(0).getTitle());
                tv_advert_content.setText(ads.get(0).getDescription());

                TTImage icon = ads.get(0).getIcon();
                if (!NewCleanFinishActivity.this.isFinishing() && icon != null && icon.isValid()) {
                    GlideUtils.loadRoundImage(NewCleanFinishActivity.this, icon.getImageUrl(), iv_advert_logo, 20);
                }
                Log.d(TAG, "穿山甲--广告类型=" + ads.get(0).getImageMode());
                Log.d(TAG, "穿山甲--广告交互类型=" + ads.get(0).getInteractionType());
                if (ads.get(0).getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO) { //视频
                    View video = ads.get(0).getAdView();
                    if (video != null) { //展示视频
                        if (video.getParent() == null) {
                            mChuanShanJiaVideo.removeAllViews();
                            mChuanShanJiaVideo.addView(video);
                        }
                    }

                    //视频播放监听
                    ads.get(0).setVideoAdListener(new TTFeedAd.VideoAdListener() {
                        @Override
                        public void onVideoLoad(TTFeedAd ad) {
                            Log.d(TAG, "穿山甲视频---- onVideoLoad");
                        }

                        @Override
                        public void onVideoError(int errorCode, int extraCode) {
                            Log.d(TAG, "穿山甲视频---- onVideoError");
                        }

                        @Override
                        public void onVideoAdStartPlay(TTFeedAd ad) {
                            Log.d(TAG, "穿山甲视频---- onVideoAdStartPlay");
                        }

                        @Override
                        public void onVideoAdPaused(TTFeedAd ad) {
                            Log.d(TAG, "穿山甲视频---- onVideoAdPaused");
                        }

                        @Override
                        public void onVideoAdContinuePlay(TTFeedAd ad) {
                            Log.d(TAG, "穿山甲视频---- onVideoAdContinuePlay");
                        }

                        @Override
                        public void onProgressUpdate(long current, long duration) {
//                            Log.d(TAG, "穿山甲视频---- onProgressUpdate");
                        }

                        @Override
                        public void onVideoAdComplete(TTFeedAd ad) {
                            Log.d(TAG, "穿山甲视频---- onVideoAdComplete");
                        }
                    });

                } else {
                    mChuanShanJiaVideo.setVisibility(View.GONE);
                    TTImage image = ads.get(0).getImageList().get(0);
                    if (image != null && image.isValid()) {
                        GlideUtils.loadImage(NewCleanFinishActivity.this, image.getImageUrl(), iv_advert);
                    }
                }

                //可以被点击的view, 也可以把convertView放进来意味item可被点击
                List<View> clickViewList = new ArrayList<>();
                clickViewList.add(v_advert);
                //触发创意广告的view（点击下载或拨打电话）
                List<View> creativeViewList = new ArrayList<>();
                creativeViewList.add(v_advert);
                //如果需要点击图文区域也能进行下载或者拨打电话动作，请将图文区域的view传入
//            creativeViewList.add(convertView);
                //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
                ads.get(0).registerViewForInteraction((ViewGroup) v_advert, clickViewList, creativeViewList, new TTNativeAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, TTNativeAd ad) {
                        if (ad != null) {
                            Log.d(TAG, "广告" + ad.getTitle() + "被点击");
                            StatisticsUtils.clickAD("ad_click", "广告点击", "1", mAdvertId, "穿山甲", sourcePage, currentPage, ad.getTitle());
                        }
                    }

                    @Override
                    public void onAdCreativeClick(View view, TTNativeAd ad) {
                        if (ad != null) {
                            Log.d(TAG, "广告" + ad.getTitle() + "被创意按钮被点击");
                            StatisticsUtils.clickAD("ad_click", "广告点击", "1", mAdvertId, "穿山甲", sourcePage, currentPage, ad.getTitle());
                        }
                    }

                    @Override
                    public void onAdShow(TTNativeAd ad) {
                        if (ad != null && !isShowOne) {
                            Log.d(TAG, "广告----" + ad.getTitle() + "----展示");
                            StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", mAdvertId, "穿山甲", sourcePage, currentPage, ad.getTitle());
                            isShowOne = true;
                        }
                    }
                });
            }
        });
    }

    private boolean isShowOne; //广告位1的广告是否已展示曝光
    private boolean isShowTwo; //广告位1的广告是否已展示曝光

    /**
     * 加载穿山甲广告位2
     */
    private void loadListAd2() {
        //feed广告请求类型参数
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(mAdvertId2)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 320)
                .setAdCount(1)
                .build();
        //调用feed广告异步请求接口
        mTTAdNative2.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "穿山甲2加载失败=" + message);
                StatisticsUtils.customADRequest("ad_request", "广告请求", "2", mAdvertId2, "穿山甲", "fail", sourcePage, currentPage);
                initNativeUnifiedAD2();
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> ads) {
                //加载成功的回调 请确保您的代码足够健壮，可以处理异常情况；
                if (null == ads || ads.isEmpty()) return;
                Log.d(TAG, "穿山甲2----广告请求成功--ads.size()=" + ads.size());
                StatisticsUtils.customADRequest("ad_request", "广告请求", "2", mAdvertId2, "穿山甲", "success", sourcePage, currentPage);
                mContainer2.setVisibility(View.VISIBLE);
                tv_advert2.setText(ads.get(0).getTitle());
                tv_advert_content2.setText(ads.get(0).getDescription());

                TTImage icon = ads.get(0).getIcon();
                if (!NewCleanFinishActivity.this.isFinishing() && icon != null && icon.isValid()) {
                    GlideUtils.loadRoundImage(NewCleanFinishActivity.this, icon.getImageUrl(), iv_advert_logo2, 20);
                }
                Log.d(TAG, "穿山甲2--广告类型=" + ads.get(0).getImageMode());
                Log.d(TAG, "穿山甲2--广告交互类型=" + ads.get(0).getInteractionType());
                if (ads.get(0).getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO) { //视频
                    View video = ads.get(0).getAdView();
                    if (video != null) { //展示视频
                        if (video.getParent() == null) {
                            mChuanShanJiaVideo2.removeAllViews();
                            mChuanShanJiaVideo2.addView(video);
                        }
                    }
                    //视频播放监听
                    ads.get(0).setVideoAdListener(new TTFeedAd.VideoAdListener() {
                        @Override
                        public void onVideoLoad(TTFeedAd ad) {
                            Log.d(TAG, "穿山甲2视频---- onVideoLoad");
                        }

                        @Override
                        public void onVideoError(int errorCode, int extraCode) {
                            Log.d(TAG, "穿山甲2视频---- onVideoError");
                        }

                        @Override
                        public void onVideoAdStartPlay(TTFeedAd ad) {
                            Log.d(TAG, "穿山甲2视频---- onVideoAdStartPlay");
                        }

                        @Override
                        public void onVideoAdPaused(TTFeedAd ad) {
                            Log.d(TAG, "穿山甲2视频---- onVideoAdPaused");
                        }

                        @Override
                        public void onVideoAdContinuePlay(TTFeedAd ad) {
                            Log.d(TAG, "穿山甲2视频---- onVideoAdContinuePlay");
                        }

                        @Override
                        public void onProgressUpdate(long current, long duration) {
//                            Log.d(TAG, "穿山甲2视频---- onProgressUpdate");
                        }

                        @Override
                        public void onVideoAdComplete(TTFeedAd ad) {
                            Log.d(TAG, "穿山甲2视频---- onVideoAdComplete");
                        }
                    });

                } else {
                    mChuanShanJiaVideo2.setVisibility(View.GONE);
                    TTImage image = ads.get(0).getImageList().get(0);
                    if (image != null && image.isValid()) {
                        GlideUtils.loadImage(NewCleanFinishActivity.this, image.getImageUrl(), iv_advert2);
                    }
                }

                //可以被点击的view, 也可以把convertView放进来意味item可被点击
                List<View> clickViewList = new ArrayList<>();
                clickViewList.add(v_advert2);
                //触发创意广告的view（点击下载或拨打电话）
                List<View> creativeViewList = new ArrayList<>();
                creativeViewList.add(v_advert2);
                //如果需要点击图文区域也能进行下载或者拨打电话动作，请将图文区域的view传入
//            creativeViewList.add(convertView);
                //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
                ads.get(0).registerViewForInteraction((ViewGroup) v_advert2, clickViewList, creativeViewList, new TTNativeAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, TTNativeAd ad) {
                        if (ad != null) {
                            Log.d(TAG, "广告2" + ad.getTitle() + "被点击");
                            StatisticsUtils.clickAD("ad_click", "广告点击", "2", mAdvertId2, "穿山甲", sourcePage, currentPage, ad.getTitle());
                        }
                    }

                    @Override
                    public void onAdCreativeClick(View view, TTNativeAd ad) {
                        if (ad != null) {
                            Log.d(TAG, "广告2" + ad.getTitle() + "被创意按钮被点击");
                            StatisticsUtils.clickAD("ad_click", "广告点击", "2", mAdvertId2, "穿山甲", sourcePage, currentPage, ad.getTitle());
                        }
                    }

                    @Override
                    public void onAdShow(TTNativeAd ad) {
                        if (ad != null && !isShowTwo) {
                            Log.d(TAG, "广告2---" + ad.getTitle() + "----展示");
                            StatisticsUtils.customADRequest("ad_show", "广告展示曝光", "2", mAdvertId2, "穿山甲", "success", sourcePage, currentPage);
                            isShowTwo = true;
                        }
                    }
                });
            }
        });
    }

    /**
     * 初始化穿山甲(插屏广告)
     */
    private void initChuanShanJiaScreen() {
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        mTTAdNativeScreen = ttAdManager.createAdNative(getApplicationContext());
        //申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
//        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
    }

    /**
     * 加载穿山甲广告(插屏广告)
     */
    private void loadListAdScreen() {
        //feed广告请求类型参数
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(mAdvertScreenId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 320)
                .setAdCount(1)
                .build();
        //调用feed广告异步请求接口
        mTTAdNativeScreen.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "穿山甲插屏广告加载失败=" + message);
                StatisticsUtils.customADRequest("ad_request", "完成页插屏广告请求", "1", mAdvertScreenId, "穿山甲", "fail", sourcePage, currentPage);
                initNativeUnifiedADScreen();
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> ads) {
                //加载成功的回调 请确保您的代码足够健壮，可以处理异常情况；
                if (null == ads || ads.isEmpty()) return;
                Log.d(TAG, "穿山甲插屏广告----广告请求成功--ads.size()=" + ads.size());
                StatisticsUtils.customADRequest("ad_request", "完成页插屏广告请求", "1", mAdvertScreenId, "穿山甲", "success", sourcePage, currentPage);
            }
        });
    }

    /**
     * 优量汇广告(插屏广告)
     */
    private void initNativeUnifiedADScreen() {
        mAdManagerScreen = new NativeUnifiedAD(this, PositionId.APPID, mSecondAdvertScreenId, new NativeADUnifiedListener() {

            @Override
            public void onNoAD(AdError adError) {
                Log.d(TAG, "插屏广告----onNoAd error code: " + adError.getErrorCode() + ", error msg: " + adError.getErrorMsg());
                StatisticsUtils.customADRequest("ad_request", "完成页插屏广告请求", "1", mSecondAdvertScreenId, "优量汇", "fail", sourcePage, currentPage);
                mIsScreenAdSuccess = true;
            }

            @Override
            public void onADLoaded(List<NativeUnifiedADData> ads) {
                if (ads != null && ads.size() > 0) {
                    Message msg = Message.obtain();
                    msg.what = MSG_INIT_AD;
                    mNativeUnifiedADDataScreen = ads.get(0);
                    msg.obj = mNativeUnifiedADDataScreen;
                    mHandlerScreen.sendMessage(msg);
                }
            }
        });
        /**
         * 如果广告位支持视频广告，强烈建议在调用loadData请求广告前，调用下面两个方法，有助于提高视频广告的eCPM值 <br/>
         * 如果广告位仅支持图文广告，则无需调用
         */

        /**
         * 设置本次拉取的视频广告，从用户角度看到的视频播放策略<p/>
         *
         * "用户角度"特指用户看到的情况，并非SDK是否自动播放，与自动播放策略AutoPlayPolicy的取值并非一一对应 <br/>
         *
         * 例如开发者设置了VideoOption.AutoPlayPolicy.NEVER，表示从不自动播放 <br/>
         * 但满足某种条件(如晚上10点)时，开发者调用了startVideo播放视频，这在用户看来仍然是自动播放的
         */
        mAdManagerScreen.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO); // 本次拉回的视频广告，从用户的角度看是自动播放的

        /**
         * 设置在视频广告播放前，用户看到显示广告容器的渲染者是SDK还是开发者 <p/>
         *
         * 一般来说，用户看到的广告容器都是SDK渲染的，但存在下面这种特殊情况： <br/>
         *
         * 1. 开发者将广告拉回后，未调用bindMediaView，而是用自己的ImageView显示视频的封面图 <br/>
         * 2. 用户点击封面图后，打开一个新的页面，调用bindMediaView，此时才会用到SDK的容器 <br/>
         * 3. 这种情形下，用户先看到的广告容器就是开发者自己渲染的，其值为VideoADContainerRender.DEV
         * 4. 如果觉得抽象，可以参考NativeADUnifiedDevRenderContainerActivity的实现
         */
        mAdManagerScreen.setVideoADContainerRender(VideoOption.VideoADContainerRender.SDK); // 视频播放前，用户看到的广告容器是由SDK渲染的
        mAdManagerScreen.loadData(AD_COUNT);
    }

    private class HScreen extends Handler {
        public HScreen() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT_AD:
                    NativeUnifiedADData ad = (NativeUnifiedADData) msg.obj;
                    Log.d(TAG, String.format(Locale.getDefault(), "(pic_width,pic_height) = (%d , %d)", ad
                                    .getPictureWidth(),
                            ad.getPictureHeight()));
                    initAdScreen(ad);
                    Log.d(TAG, "eCPM = " + mNativeUnifiedADDataScreen.getECPM() + " , eCPMLevel = " + mNativeUnifiedADDataScreen.getECPMLevel());
                    break;
                case MSG_VIDEO_START:
                    Log.d("AD_DEMO", "handleMessage");
//                    iv_advert.setVisibility(View.GONE);
//                    mMediaView.setVisibility(View.VISIBLE);
                    break;
            }
        }

    }

    /**
     * 加载优量汇广告(插屏)
     *
     * @param ad
     */
    private void initAdScreen(final NativeUnifiedADData ad) {

        // 设置广告事件监听
        ad.setNativeAdEventListener(new NativeADEventListener() {
            @Override
            public void onADExposed() {
                StatisticsUtils.customADRequest("ad_request", "完成页插屏广告请求", "1", mSecondAdvertScreenId, "优量汇", "success", sourcePage, currentPage);
                Log.d(TAG, "插屏---广告曝光");
            }

            @Override
            public void onADClicked() {
            }

            @Override
            public void onADError(AdError error) {
                Log.d(TAG, "插屏---错误回调 error code :" + error.getErrorCode() + "  error msg: " + error.getErrorMsg());
            }

            @Override
            public void onADStatusChanged() {
            }
        });
    }
}
