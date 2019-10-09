package com.xiaoniu.cleanking.ui.newclean.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.pi.AdData;
import com.qq.e.comm.util.AdError;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.main.bean.NewsListInfo;
import com.xiaoniu.cleanking.ui.main.bean.NewsType;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.VideoItemInfo;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.news.adapter.NewsListAdapter;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.http.EHttp;
import com.xiaoniu.common.http.callback.ApiCallback;
import com.xiaoniu.common.http.request.HttpRequest;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.jzvd.Jzvd;

/**
 * 1.2.0 版本以后清理完成 显示资讯
 */
public class NewCleanFinishActivity extends BaseActivity implements NativeExpressAD.NativeExpressADListener, View.OnClickListener {

    private static final String TAG = "NewCleanFinishActivity";
    private String mTitle;
    private TextView mTvSize;
    private TextView mTvGb;
    private TextView mTvQl;
    private ViewGroup mContainer;
    private NativeExpressADView mNativeExpressADView;
    private NativeExpressAD mNativeExpressAD;

    private XRecyclerView mRecyclerView;
    private NewsListAdapter mNewsAdapter;
    private NewsType mType = NewsType.TOUTIAO;
    private static final int PAGE_NUM = 20;//每一页数据
    private boolean mIsRefresh = true;
    private ImageView mIvSpeedClean;
    private ImageView mIvPowerClean;
    private ImageView mIvNotificationClean;
    private ImageView mIvWeChatClean;
    private ImageView mIvFileClean;
    private ImageView mIvCooling;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_finish_layout;
    }

    @Override
    protected void initVariable(Intent intent) {
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
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
        mTvQl = header.findViewById(R.id.tv_ql);
        mContainer = header.findViewById(R.id.container);

        mIvSpeedClean = headerTool.findViewById(R.id.iv_speed_clean);
        mIvPowerClean = headerTool.findViewById(R.id.iv_power_clean);
        mIvNotificationClean = headerTool.findViewById(R.id.iv_notification_clean);
        mIvWeChatClean = headerTool.findViewById(R.id.iv_wechat_clean);
        mIvFileClean = headerTool.findViewById(R.id.iv_file_clean);
        mIvCooling = headerTool.findViewById(R.id.iv_cooling);
    }

    private void changeUI(Intent intent) {
        if (PreferenceUtil.isNoFirstOpenCLeanFinishApp()) {
            if (AppHolder.getInstance().getSwitchInfoList() != null) {
                for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.FINISH_ID.equals(switchInfoList.getId())) {
                        if (switchInfoList.isIsOpen()) {
                            //加载广告
                            refreshAd();
                        }
                    }
                }
            }
        } else {
            PreferenceUtil.saveFirstOpenCLeanFinishApp();
        }

        if (intent != null) {
            mTitle = intent.getStringExtra("title");
            String num = intent.getStringExtra("num");
            String unit = intent.getStringExtra("unit");
            mTvSize.setText(num);
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
            } else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
                //一键加速
                mIvSpeedClean.setVisibility(View.GONE);
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已加速");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快试试其他功能吧！");
                }
            } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
                CleanEvent cleanEvent = new CleanEvent();
                cleanEvent.setCleanAminOver(true);
                EventBus.getDefault().post(cleanEvent);
                //建议清理
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
                //超强省电
                mIvPowerClean.setVisibility(View.GONE);
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            } else if (getString(R.string.tool_chat_clear).contains(mTitle) || getString(R.string.tool_chat_clear_n).contains(mTitle)) {
                //微信专情
                mIvWeChatClean.setVisibility(View.GONE);
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已清理");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快试试其他功能吧！");
                }
            } else if (getString(R.string.tool_qq_clear).contains(mTitle)) {
                //QQ专清
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("手机很干净");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {
                //通知栏清理
                mIvNotificationClean.setVisibility(View.GONE);
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("通知栏很干净");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他炫酷功能");
                }
            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) {
                //手机降温
                mIvCooling.setVisibility(View.GONE);
                mTvSize.setText("");
                int tem = new Random().nextInt(3) + 1;
                mTvGb.setText("成功降温" + tem + "°C");
                mTvGb.setTextSize(20);
                mTvQl.setText("60s后达到最佳降温效果");
            }
            setLeftTitle(mTitle);
        }

        //是否显示推荐功能（一键加速，超强省电，通知栏清理，微信专清，文件清理，手机降温）
        showTool();
    }

    /**
     * 是否显示推荐功能项
     */
    private void showTool() {
        if (!PreferenceUtil.getWeChatCleanTime()) {
            // 微信清理间隔时间至少3分钟 否则隐藏功能项
            mIvWeChatClean.setVisibility(View.GONE);
        }
        if (!PreferenceUtil.getCleanTime()) {
            // 一键加速间隔时间至少3分钟  否则隐藏功能项
            mIvSpeedClean.setVisibility(View.GONE);
        }
        if (!PreferenceUtil.getNotificationCleanTime()) {
            // 通知栏清理间隔时间至少3分钟 否则隐藏
            mIvNotificationClean.setVisibility(View.GONE);
        }
        if (!PreferenceUtil.getCoolingCleanTime()) {
            // 手机降温间隔时间至少3分钟 否则隐藏
            mIvCooling.setVisibility(View.GONE);
        }
        if (!PreferenceUtil.getPowerCleanTime()) {
            // 超强省电间隔时间至少3分钟 否则隐藏
            mIvPowerClean.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        changeUI(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_speed_clean:
                //一键加速
                speedClean();
                break;
            case R.id.iv_power_clean:
                //超强省电
                powerClean();
                break;
            case R.id.iv_notification_clean:
                //通知栏清理
                notificationClean();
                break;
            case R.id.iv_wechat_clean:
                //微信专清
                weChatClean();
                break;
            case R.id.iv_file_clean:
                //文件清理
                fileClean();
                break;
            case R.id.iv_cooling:
                //手机降温
                coolingClean();
                break;
        }
        finish();
    }

    /**
     * 一键加速
     */
    public void speedClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId("boost_click");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.ONKEY);
        StatisticsUtils.trackClick("boost_click", "用户在首页点击【一键加速】按钮", "home_page", "home_page");
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
        StatisticsUtils.trackClick("powersave_click", "用户在首页点击【超强省电】按钮", "home_page", "home_page");
    }

    /**
     * 通知栏清理
     */
    public void notificationClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId("notification_clean_click");
        //通知栏清理
        NotifyCleanManager.startNotificationCleanActivity(this, 0);
        StatisticsUtils.trackClick("notification_clean_click", "用户在首页点击【通知清理】按钮", AppHolder.getInstance().getSourcePageId(), "home_page");
    }

    /**
     * 微信专清
     */
    public void weChatClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId("wxclean_click");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.WETCHAT_CLEAN);

        StatisticsUtils.trackClick("wxclean_click", "用户在首页点击【微信专清】按钮", "home_page", "home_page");
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
        StatisticsUtils.trackClick("file_clean_click", "用户在首页点击【文件清理】按钮", "home_page", "home_page");
        startActivity(FileManagerHomeActivity.class);
    }

    /**
     * 手机降温
     */
    public void coolingClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId("cooling_click");
        startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
        StatisticsUtils.trackClick("cooling_click", "用户在首页点击【手机降温】按钮", AppHolder.getInstance().getSourcePageId(), "home_page");
    }

    @Override
    protected void setListener() {
        mIvSpeedClean.setOnClickListener(this);
        mIvPowerClean.setOnClickListener(this);
        mIvNotificationClean.setOnClickListener(this);
        mIvWeChatClean.setOnClickListener(this);
        mIvFileClean.setOnClickListener(this);
        mIvCooling.setOnClickListener(this);

        mBtnLeft.setOnClickListener(v -> {
            if (getString(R.string.tool_one_key_speed).contains(mTitle)){
                StatisticsUtils.trackClick("return_back", "\"一键加速返回\"点击", AppHolder.getInstance().getSourcePageId(), "one_click_acceleration_clean_up_page");
            }else if(getString(R.string.tool_suggest_clean).contains(mTitle)){
                StatisticsUtils.trackClick("return_back_click", "用户在垃圾清理完成页点击【建议清理】返回", "scanning_result_page", "home_page_clean_up_page");
            }
            finish();
        });

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mIsRefresh = true;
                startLoadData();
            }

            @Override
            public void onLoadMore() {
                mIsRefresh = false;
                startLoadData();
            }
        });

    }

    @Override
    protected void loadData() {
        Intent intent = getIntent();
        changeUI(intent);

        mIsRefresh = false;
        startLoadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (getString(R.string.tool_one_key_speed).contains(mTitle)){
            StatisticsUtils.trackClick("return_back", "\"一键加速返回\"点击", "selected_page", "one_click_acceleration_clean_up_page");
        }else if(getString(R.string.tool_suggest_clean).contains(mTitle)){
            StatisticsUtils.trackClick("system_return_back_click", "用户在垃圾清理完成页点击【建议清理】返回", "scanning_result_page", "home_page_clean_up_page");
        }

        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        if (getString(R.string.app_name).contains(mTitle)) {
            //悟空清理
            NiuDataAPI.onPageStart("clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
            //一键加速
            NiuDataAPI.onPageStart("boost_success_page_view_page", "加速结果出现时");
        } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
            //1.2.1清理完成页面
            NiuDataAPI.onPageStart("home_page_clean_up_page_view_page", "用户在垃圾清理完成页浏览");
        } else if (getString(R.string.tool_phone_clean).contains(mTitle)) {
            //手机清理
            NiuDataAPI.onPageStart("clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
            //超强省电
            NiuDataAPI.onPageStart("powersave_success_page_view_page", "省电结果出现时");
        } else if (getString(R.string.tool_chat_clear).contains(mTitle) || getString(R.string.tool_chat_clear_n).contains(mTitle)) {
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
        } else {
            NiuDataAPI.onPageStart("clean_up_page_view_immediately", "清理完成页浏览");
        }


        super.onResume();
    }

    @Override
    protected void onPause() {
        Jzvd.releaseAllVideos();
        if (getString(R.string.app_name).contains(mTitle)) {
            //悟空清理
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "clean_success_page", "clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
            //一键加速
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "boost_success_page", "boost_success_page_view_page", "加速结果出现时");
        } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
            //1.2.1清理完成页面
            NiuDataAPI.onPageEnd("home_page_clean_up_page_view_page", "用户在垃圾清理完成页浏览");
        } else if (getString(R.string.tool_phone_clean).contains(mTitle)) {
            //手机清理
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "clean_success_page", "clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
            //超强省电
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "powersave_success_page", "powersave_success_page_view_page", "省电结果出现时");
        } else if (getString(R.string.tool_chat_clear).contains(mTitle) || getString(R.string.tool_chat_clear_n).contains(mTitle)) {
            //微信专情
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "wxclean_success_page", "wxclean_success_page_view_page", "微信清理结果页出现时");
        } else if (getString(R.string.tool_qq_clear).contains(mTitle)) {
            //QQ专清
            NiuDataAPI.onPageStart("clean_up_page_view_immediately", "清理完成页浏览");
        } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {
            //通知栏清理
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "notification_clean_success_page", "notification_clean_success_page_view_page", "通知栏清理结果页出现时");
        } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) {
            //手机降温
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "cooling_success_page", "cooling_success_page_view_page", "降温结果页出现时");
        } else {
            NiuDataAPI.onPageEnd("clean_up_page_view_immediately", "清理完成页浏览");
        }


        super.onPause();
    }

    @Override
    public void onADLoaded(List<NativeExpressADView> adList) {
        // 释放前一个展示的NativeExpressADView的资源
        if (mNativeExpressADView != null) {
            mNativeExpressADView.destroy();
        }

        if (mContainer.getVisibility() != View.VISIBLE) {
            mContainer.setVisibility(View.VISIBLE);
        }

        if (mContainer.getChildCount() > 0) {
            mContainer.removeAllViews();
        }

        mNativeExpressADView = adList.get(0);
        if (mNativeExpressADView.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
            mNativeExpressADView.setMediaListener(mediaListener);
        }
        // 广告可见才会产生曝光，否则将无法产生收益。
        mContainer.addView(mNativeExpressADView);
        mNativeExpressADView.render();
    }

    /**
     * 获取播放器实例
     * <p>
     * 仅当视频回调{@link NativeExpressMediaListener#onVideoInit(NativeExpressADView)}调用后才会有返回值
     *
     * @param videoPlayer
     * @return
     */
    private String getVideoInfo(AdData.VideoPlayer videoPlayer) {
        if (videoPlayer != null) {
            StringBuilder videoBuilder = new StringBuilder();
            videoBuilder.append("{state:").append(videoPlayer.getVideoState()).append(",")
                    .append("duration:").append(videoPlayer.getDuration()).append(",")
                    .append("position:").append(videoPlayer.getCurrentPosition()).append("}");
            return videoBuilder.toString();
        }
        return null;
    }

    private NativeExpressMediaListener mediaListener = new NativeExpressMediaListener() {
        @Override
        public void onVideoInit(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoInit: " + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
        }

        @Override
        public void onVideoLoading(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoLoading");
        }

        @Override
        public void onVideoReady(NativeExpressADView nativeExpressADView, long l) {
            Log.i(TAG, "onVideoReady");
        }

        @Override
        public void onVideoStart(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoStart: "
                    + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
        }

        @Override
        public void onVideoPause(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoPause: "
                    + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
        }

        @Override
        public void onVideoComplete(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoComplete: "
                    + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
        }

        @Override
        public void onVideoError(NativeExpressADView nativeExpressADView, AdError adError) {
            Log.i(TAG, "onVideoError");
        }

        @Override
        public void onVideoPageOpen(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoPageOpen");
        }

        @Override
        public void onVideoPageClose(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoPageClose");
        }
    };

    private void refreshAd() {
        try {
            /**
             *  如果选择支持视频的模版样式，请使用{@link PositionId#NATIVE_EXPRESS_SUPPORT_VIDEO_POS_ID}
             */
            mNativeExpressAD = new NativeExpressAD(this, getMyADSize(), PositionId.APPID, PositionId.CLEAN_FINISH_ID, this); // 这里的Context必须为Activity
            mNativeExpressAD.setVideoOption(new VideoOption.Builder()
                    .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS) // 设置什么网络环境下可以自动播放视频
                    .setAutoPlayMuted(true) // 设置自动播放视频时，是否静音
                    .build()); // setVideoOption是可选的，开发者可根据需要选择是否配置
            mNativeExpressAD.setMaxVideoDuration(5);
            /**
             * 如果广告位支持视频广告，强烈建议在调用loadData请求广告前调用setVideoPlayPolicy，有助于提高视频广告的eCPM值 <br/>
             * 如果广告位仅支持图文广告，则无需调用
             */

            /**
             * 设置本次拉取的视频广告，从用户角度看到的视频播放策略<p/>
             *
             * "用户角度"特指用户看到的情况，并非SDK是否自动播放，与自动播放策略AutoPlayPolicy的取值并非一一对应 <br/>
             *
             * 如自动播放策略为AutoPlayPolicy.WIFI，但此时用户网络为4G环境，在用户看来就是手工播放的
             */
            mNativeExpressAD.setVideoPlayPolicy(getVideoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS, this));  // 本次拉回的视频广告，在用户看来是否为自动播放的
            mNativeExpressAD.loadAD(1);
        } catch (NumberFormatException e) {
            Log.w(TAG, "ad size invalid.");
            Toast.makeText(this, "请输入合法的宽高数值", Toast.LENGTH_SHORT).show();
        }
    }

    private ADSize getMyADSize() {
        int w = ADSize.FULL_WIDTH;
        int h = -2;
        return new ADSize(w, h);
    }

    @Override
    public void onRenderFail(NativeExpressADView nativeExpressADView) {
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", PositionId.CLEAN_FINISH_ID, "优量汇", "fail", "success_page", "success_page");
    }

    @Override
    public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", PositionId.CLEAN_FINISH_ID, "优量汇", "success", "success_page", "success_page");
    }

    @Override
    public void onADExposure(NativeExpressADView nativeExpressADView) {
        StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", PositionId.CLEAN_FINISH_ID, "优量汇", "success_page", "success_page");
    }

    @Override
    public void onADClicked(NativeExpressADView nativeExpressADView) {
        StatisticsUtils.clickAD("ad_click", "广告点击", "1", PositionId.CLEAN_FINISH_ID, "优量汇", "success_page", "success_page");

        if (mContainer != null && mContainer.getChildCount() > 0) {
            mContainer.removeAllViews();
            mContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onADClosed(NativeExpressADView nativeExpressADView) {
        if (mContainer != null && mContainer.getChildCount() > 0) {
            mContainer.removeAllViews();
            mContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onNoAD(AdError adError) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 使用完了每一个NativeExpressADView之后都要释放掉资源
        if (mNativeExpressADView != null) {
            mNativeExpressADView.destroy();
        }
        if (mRecyclerView != null) {
            mRecyclerView.destroy(); // this will totally release XR's memory
            mRecyclerView = null;
        }
    }

    public static int getVideoPlayPolicy(int autoPlayPolicy, Context context) {
        if (autoPlayPolicy == VideoOption.AutoPlayPolicy.ALWAYS) {
            return VideoOption.VideoPlayPolicy.AUTO;
        } else if (autoPlayPolicy == VideoOption.AutoPlayPolicy.WIFI) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiNetworkInfo != null && wifiNetworkInfo.isConnected() ? VideoOption.VideoPlayPolicy.AUTO
                    : VideoOption.VideoPlayPolicy.MANUAL;
        } else if (autoPlayPolicy == VideoOption.AutoPlayPolicy.NEVER) {
            return VideoOption.VideoPlayPolicy.MANUAL;
        }
        return VideoOption.VideoPlayPolicy.UNKNOWN;
    }

    public void startLoadData() {
        if (!NetworkUtils.isNetConnected()) {
            if (mIsRefresh) {
                if (mRecyclerView != null)
                    mRecyclerView.refreshComplete();
            } else {
                if (mRecyclerView != null)
                    mRecyclerView.loadMoreComplete();
            }
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
        String type = mType.getValue();
        String lastId = SPUtil.getLastNewsID(mType.getName());
        if (mIsRefresh) {
            lastId = "";
        }
        String url = SpCacheConfig.NEWS_BASEURL + "&type=" + type + "&startkey=" + lastId + "&num=" + PAGE_NUM;
        EHttp.get(this, url, new ApiCallback<NewsListInfo>(null) {
            @Override
            public void onFailure(Throwable e) {
            }

            @Override
            public void onSuccess(NewsListInfo result) {
                if (result != null && result.data != null && result.data.size() > 0) {
                    SPUtil.setLastNewsID(mType.getName(), result.data.get(result.data.size() - 1).rowkey);
                    if (mIsRefresh) {
                        mNewsAdapter.setData(result.data);
                    } else {
                        mNewsAdapter.addData(result.data);
                    }
                }
            }

            @Override
            public void onComplete() {
                if (mIsRefresh) {
                    if (mRecyclerView != null)
                        mRecyclerView.refreshComplete();
                } else {
                    if (mRecyclerView != null)
                        mRecyclerView.loadMoreComplete();
                }
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

        EHttp.post(this, SpCacheConfig.VIDEO_BASEURL, request, new ApiCallback<ArrayList<VideoItemInfo>>() {

            @Override
            public void onComplete() {
                if (mIsRefresh) {
                    if (mRecyclerView != null)
                        mRecyclerView.refreshComplete();
                } else {
                    if (mRecyclerView != null)
                        mRecyclerView.loadMoreComplete();
                }
            }

            @Override
            public void onFailure(Throwable e) {
                Log.i("123", e.toString());
            }

            @Override
            public void onSuccess(ArrayList<VideoItemInfo> result) {
                if (result != null && result.size() > 0) {
                    SPUtil.setLastNewsID(mType.getName(), result.get(result.size() - 1).videoId);
                    if (mIsRefresh) {
                        mNewsAdapter.setData(result);
                    } else {
                        mNewsAdapter.addData(result);
                    }
                }
            }
        });
    }
}
