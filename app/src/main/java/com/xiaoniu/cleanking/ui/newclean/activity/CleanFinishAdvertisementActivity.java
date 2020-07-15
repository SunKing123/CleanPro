package com.xiaoniu.cleanking.ui.newclean.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.main.presenter.CleanFinishAdvertisementPresenter;
import com.xiaoniu.cleanking.ui.tool.notify.event.FromHomeCleanFinishEvent;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;
import com.xiaoniu.statistic.NiuDataAPI;
import com.xnad.sdk.ad.listener.AbsAdCallBack;
import com.xnad.sdk.ad.widget.TemplateView;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

import cn.jzvd.Jzvd;

/**
 * 自渲染广告位3
 */
public class CleanFinishAdvertisementActivity extends BaseActivity<CleanFinishAdvertisementPresenter> implements View.OnClickListener {

    private String mTitle;
    private TextView mTitleTv;
    private TextView mTvSize;
    private TextView mTvGb;
    private TextView mTvQl;
    private ImageView mErrorIv;
    private LinearLayout toolBar;
    String sourcePage = "";
    String currentPage = "";
    String createEventCode = "";
    String createEventName = "";
    String returnEventName = "";
    String sysReturnEventName = "";
    ViewGroup ad_container_pos03;

    private String TAG = "GeekSdk";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_finish_layout_adv;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void initView() {
        StatusBarUtil.setTransparentForWindow(this);
        findViewById(R.id.btnLeft).setOnClickListener(this);
        toolBar = (LinearLayout) findViewById(R.id.toolBar);
        StatusBarUtil.setPaddingTop(mContext, toolBar);
        mErrorIv = (ImageView) findViewById(R.id.iv_error);
        mTitleTv = (TextView) findViewById(R.id.tvTitle);
        ad_container_pos03 = findViewById(R.id.ad_container_pos03);
        mTvSize = findViewById(R.id.tv_size);
        mTvGb = findViewById(R.id.tv_clear_finish_gb_title);
        mTvQl = findViewById(R.id.tv_ql);
        changeUI(getIntent());
        getPageData();
        if (NetworkUtils.isNetConnected()) {
            initPos03Ad();
        } else {
            ad_container_pos03.setVisibility(View.GONE);
//            mErrorIv.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        changeUI(intent);
    }

    private void changeUI(Intent intent) {

        if (intent != null) {
            mTitle = intent.getStringExtra("title");
            String num = intent.getStringExtra("num");
            String unit = intent.getStringExtra("unit");
            mTvSize.setText(num);
            mTvGb.setText(unit);

            EventBus.getDefault().post(new FromHomeCleanFinishEvent(mTitle));
            if (TextUtils.isEmpty(mTitle))
                mTitle = getString(R.string.app_name);
            if (getString(R.string.app_name).contains(mTitle)) {
                //清理管家极速版
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
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
            } else if (getString(R.string.tool_qq_clear).contains(mTitle)) {
                //QQ专清
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("手机很干净");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            } else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
                //一键加速
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已加速");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快试试其他功能吧！");
                }

            } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
                //超强省电
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }

            } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {
                //通知栏清理
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("通知栏很干净");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他炫酷功能");
                }
            } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {
                //微信专情
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已清理");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快试试其他功能吧！");
                }
            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) {
                //手机降温
                mTvSize.setText("");
                int tem = new Random().nextInt(3) + 1;
                mTvGb.setText("成功降温" + tem + "°C");
                mTvGb.setTextSize(20);
                mTvQl.setText("60s后达到最佳降温效果");
            } else if (getString(R.string.game_quicken).contains(mTitle)) {
                //游戏加速
                mTvGb.setText("%");
                mTvSize.setText(NumberUtils.mathRandom(25, 50));
                mTvQl.setText("已提速");
            } else if (getString(R.string.virus_kill).contains(mTitle)) {
                mTvSize.setText(R.string.virus_guard);
                mTvSize.setTextSize(20);
            }
            mTitleTv.setText(mTitle);
        }
    }

    @Override
    public void onClick(View v) {
        String functionName = "";
        String functionPosition = "";

        switch (v.getId()) {
            case R.id.btnLeft:
                if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
                    StatisticsUtils.trackClick("return_back", returnEventName, sourcePage, "one_click_acceleration_clean_up_page");
                } else {
                    StatisticsUtils.trackClick("return_click", returnEventName, sourcePage, currentPage);
                }
                finish();
                break;
        }
        //1.21 版本推荐清理_标识sourcePage,其他""
        String sourcePage = getString(R.string.tool_suggest_clean).contains(mTitle) ? "scanning_result_page" : "";
        StatisticsUtils.trackFunctionClickItem("recommendation_function_click", functionName, getIntent().hasExtra("home") ? "home_page" : sourcePage, "home_page_clean_up_page", functionName, functionPosition);
    }

    @Override
    public void onBackPressed() {
        if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
            StatisticsUtils.trackClick("system_return_click", sysReturnEventName, sourcePage, "one_click_acceleration_clean_up_page");
        } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
            StatisticsUtils.trackClick("system_return_click", sysReturnEventName, sourcePage, currentPage);
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        pageStart();
        super.onResume();
    }


    //获取埋点参数
    void getPageData() {
        sourcePage = AppHolder.getInstance().getCleanFinishSourcePageId();
        if (getString(R.string.app_name).contains(mTitle)) {
            //清理管家极速版
            currentPage = "clean_success_page";
        } else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
            //清理管家极速版
            currentPage = "boost_success_page";
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
        } else if (getIntent().hasExtra("action") && getIntent().getStringExtra("action").equals("lock")) {
            String lockAction = PreferenceUtil.getInstants().get("lock_action");
            if (lockAction.equals("file")) {
                //锁屏页面
                currentPage = "lock_screen_clean_success_page";
                createEventName = "锁屏清理结果页创建时";
                createEventCode = "lock_screen_clean_success_page_custom";
                returnEventName = "锁屏清理结果页返回";
                sysReturnEventName = "锁屏清理结果页返回";
            } else if (lockAction.equals("ram")) {
                //锁屏页面
                currentPage = "lock_screen_boost_success_page";
                createEventName = "锁屏加速结果页创建时";
                createEventCode = "lock_screen_boost_success_page_custom";
                returnEventName = "锁屏加速结果页返回";
                sysReturnEventName = "锁屏加速结果页返回";
            } else if (lockAction.equals("virus")) {
                //锁屏页面
                currentPage = "lock_screen_virus_killing_success_page";
                createEventName = "锁屏病毒查杀结果页创建时";
                createEventCode = "lock_screen_clean_success_page_custom";
                returnEventName = "锁屏病毒查杀结果页返回";
                sysReturnEventName = "锁屏病毒查杀结果页返回";
            }

        } else {
            currentPage = "clean_up_page_view_immediately";
        }

        //页面创建事件埋点
        StatisticsUtils.customTrackEvent(createEventCode, createEventName, sourcePage, currentPage);
    }

    /*---------------------------------------- 埋点---------------------------------------------------------------------*/
    //获取焦点
    public void pageStart() {

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
        } else if (getIntent().hasExtra("action") && getIntent().getStringExtra("action").equals("lock")) {
            //锁屏清理结果页展示浏览
            NiuDataAPI.onPageStart("lock_screen_clean_success_page_view_page", "锁屏清理结果页展示浏览");
        } else {
            NiuDataAPI.onPageStart("clean_up_page_view_immediately", "清理完成页浏览");
        }
    }


    //失去焦点
    public void onPageEnd() {
        if (getString(R.string.app_name).contains(mTitle)) {
            //清理管家极速版
            NiuDataAPIUtil.onPageEnd(source_page, currentPage, "clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
            //一键加速
            NiuDataAPIUtil.onPageEnd(source_page, currentPage, "boost_success_page_view_page", "加速结果出现时");
        } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
            //1.2.1清理完成页面
            NiuDataAPIUtil.onPageEnd(source_page, currentPage, "clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_phone_clean).contains(mTitle)) {
            //手机清理
            NiuDataAPIUtil.onPageEnd(source_page, currentPage, "clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
            //超强省电
            NiuDataAPIUtil.onPageEnd(source_page, currentPage, "powersave_success_page_view_page", "省电结果出现时");
        } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {
            //微信专情
            NiuDataAPIUtil.onPageEnd(source_page, currentPage, "wxclean_success_page_view_page", "微信清理结果页出现时");
        } else if (getString(R.string.tool_qq_clear).contains(mTitle)) {
            //QQ专清
//            NiuDataAPI.onPageStart("clean_up_page_view_immediately", "清理完成页浏览");
        } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {
            //通知栏清理
            NiuDataAPIUtil.onPageEnd(source_page, currentPage, "notification_clean_success_page_view_page", "通知栏清理结果页出现时");
        } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) {
            //手机降温
            NiuDataAPIUtil.onPageEnd(source_page, currentPage, "cooling_success_page_view_page", "降温结果页出现时");

        } else if (getIntent().hasExtra("action") && getIntent().getStringExtra("action").equals("lock")) {
            String lockAction = PreferenceUtil.getInstants().get("lock_action");
            if (lockAction.equals("file")) {
                //锁屏清理结果页展示浏览
                NiuDataAPIUtil.onPageEnd(source_page, currentPage, "lock_screen_clean_success_page_view_page", "锁屏清理结果页展示浏览");
            } else if (lockAction.equals("ram")) {
                //锁屏页面
                NiuDataAPIUtil.onPageEnd(source_page, currentPage, "lock_screen_boost_success_page_view_page", "锁屏加速结果页展示浏览");
            } else if (lockAction.equals("virus")) {
                //锁屏页面
                NiuDataAPIUtil.onPageEnd(source_page, currentPage, "lock_screen_virus_killing_success_page_view_page", "锁屏病毒查杀结果页展示浏览");
            }

        } else {
            NiuDataAPI.onPageEnd("clean_up_page_view_immediately", "清理完成页浏览");
        }
    }

    @Override
    protected void onPause() {
        onPageEnd();
        Jzvd.releaseAllVideos();
        super.onPause();
    }

    @Override
    public void netError() {

    }

    public void initPos03Ad() {


        if (!AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_FINISH, PositionId.DRAW_THREE_CODE))
            return;

        StatisticsUtils.customTrackEvent("ad_request_sdk_3","功能完成页广告位3发起请求",sourcePage,"success_page");

        AdRequestParams params=new AdRequestParams.Builder().setAdId(MidasConstants.FINISH02_FEEED_ID)
                .setActivity(this)
                .setViewContainer(ad_container_pos03).setViewWidthOffset(24)
                .build();
        MidasRequesCenter.requestAd(params, new AbsAdCallBack() {
            @Override
            public void onAdLoadSuccess(com.xnad.sdk.ad.entity.AdInfo adInfo) {
                super.onAdLoadSuccess(adInfo);
//                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "clod_splash_page", "clod_splash_page");
            }

            @Override
            public void onAdError(com.xnad.sdk.ad.entity.AdInfo adInfo, int i, String s) {
                super.onAdError(adInfo, i, s);
//                jumpActivity();
            }

            @Override
            public void onShowError(int i, String s) {
                super.onShowError(i, s);
//                jumpActivity();
            }

            @Override
            public void onAdShow(com.xnad.sdk.ad.entity.AdInfo adInfo) {
                super.onAdShow(adInfo);
            }

            @Override
            public void onAdClicked(com.xnad.sdk.ad.entity.AdInfo adInfo) {
                super.onAdClicked(adInfo);
            }

            @Override
            public void onAdClose(com.xnad.sdk.ad.entity.AdInfo adInfo, TemplateView templateView) {
                super.onAdClose(adInfo, templateView);
//                jumpActivity();
            }
        });
    }

    private int mBottomAdShowCount = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
               // showBottomAd();
                return;
        }
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
