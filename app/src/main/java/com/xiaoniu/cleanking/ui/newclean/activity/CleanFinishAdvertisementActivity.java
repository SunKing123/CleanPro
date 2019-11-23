package com.xiaoniu.cleanking.ui.newclean.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.TTAdNative;
import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.main.presenter.CleanFinishAdvertisementPresenter;
import com.xiaoniu.cleanking.ui.tool.notify.event.FromHomeCleanFinishEvent;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

import cn.jzvd.Jzvd;

/**
 * 自渲染广告位3
 */
public class CleanFinishAdvertisementActivity extends BaseActivity<CleanFinishAdvertisementPresenter> implements View.OnClickListener {

    private static final String TAG = "AD_DEMO";
    private String mTitle;
    private TextView mTitleTv;
    private TextView mTvSize;
    private TextView mTvGb;
    private TextView mTvQl;
    private ImageView   mErrorIv;
    private View mViewContent;
    private NativeUnifiedADData mNativeUnifiedADData;
    private NativeUnifiedAD mAdManager;
    private String mAdvertId = ""; //广告id
    private String mSecondAdvertId = ""; //备用id
    private LinearLayout toolBar;
    String sourcePage = "";
    String currentPage = "";
    String createEventCode = "";
    String createEventName = "";
    String returnEventName = "";
    String sysReturnEventName = "";
    FrameLayout ad_container_pos03;
    //穿山甲相关 begin
    private TTAdNative mTTAdNative;
    //穿山甲相关 end

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
        mPresenter.getSwitchInfoList();
        findViewById(R.id.btnLeft).setOnClickListener(this);
        mViewContent = findViewById(R.id.v_content);
        toolBar = (LinearLayout)findViewById(R.id.toolBar);
        StatusBarUtil.setPaddingTop(mContext,toolBar);
        mErrorIv = (ImageView) findViewById(R.id.iv_error);
        mTitleTv = (TextView) findViewById(R.id.tvTitle);
        ad_container_pos03 = (FrameLayout)findViewById(R.id.ad_container_pos03);
        mTvSize = findViewById(R.id.tv_size);
        mTvGb = findViewById(R.id.tv_clear_finish_gb_title);
        mTvQl = findViewById(R.id.tv_ql);
        changeUI(getIntent());
        getPageData();
        initPos03Ad();
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
            }
            mTitleTv.setText(mTitle);
        }
    }

    /**
     * 拉取广告开关成功
     *
     * @return
     */
    public void getSwitchInfoListSuccess(SwitchInfoList list) {
        if (null == list || null == list.getData() || list.getData().size() <= 0) return;
        for (SwitchInfoList.DataBean switchInfoList : list.getData()) {

            if (getString(R.string.tool_one_key_speed).contains(mTitle)) { //一键加速
                if (PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) { //超强省电
                if (PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {//通知栏清理
                if (PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {//微信专情
                if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) { //手机降温
                if (PositionId.KEY_COOL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.tool_qq_clear).contains(mTitle)) { //QQ专清
                if (PositionId.KEY_QQ.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.tool_phone_clean).contains(mTitle)) { //手机清理
                if (PositionId.KEY_PHONE.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.game_quicken).contains(mTitle)) { //游戏加速
                if (PositionId.KEY_GAME.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.virus_kill).contains(mTitle)) { //病毒查杀
                if (PositionId.KEY_VIRUS.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.network_quicken).contains(mTitle)) { //网络加速
                if (PositionId.KEY_NET.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            } else { //立即清理
                if (PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            }
        }
        Log.d(TAG, "mAdvertId=" + mAdvertId);
        Log.d(TAG, "mSecondAdvertId=" + mSecondAdvertId);

    }

    /**
     * 拉取广告开关失败
     *
     * @return
     */
    public void getSwitchInfoListFail(String message) {

        mViewContent.setVisibility(View.GONE);
        mErrorIv.setVisibility(View.VISIBLE);
        ToastUtils.showShort(message);
    }

    /**
     * 拉取广告开关失败
     *
     * @return
     */
    public void getSwitchInfoListConnectError() {

        mViewContent.setVisibility(View.GONE);
        mErrorIv.setVisibility(View.VISIBLE);
        ToastUtils.showShort("网络连接失败，请假查您的网络连接");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
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
        if (mNativeUnifiedADData != null) {
            // 必须要在Actiivty.onResume()时通知到广告数据，以便重置广告恢复状态
            mNativeUnifiedADData.resume();
        }
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
    public void onDestroy() {
        super.onDestroy();

        if (mNativeUnifiedADData != null) {
            // 必须要在Actiivty.destroy()时通知到广告数据，以便释放内存
            mNativeUnifiedADData.destroy();
        }
    }


    private static final int AD_COUNT = 1;
    private static final int MSG_INIT_AD = 0;
    private static final int MSG_VIDEO_START = 1;

    @Override
    public void netError() {

    }




    public void initPos03Ad(){
        AdManager adManager = GeekAdSdk.getAdsManger();
        adManager.loadAd(this,"success_page_ad_3", new AdListener() {
            @Override
            public void adSuccess(AdInfo info) {
                if (info == null) {
                    LogUtils.e("DEMO>>>adSuccess， AdInfo is empty");
                } else {
                    LogUtils.e("DEMO>>>adSuccess， "+ info.toString());
                }
                View adView = adManager.getAdView();
                if (adView != null) {
                    ad_container_pos03.removeAllViews();
                    ad_container_pos03.addView(adView);
                }
            }

            @Override
            public void adExposed(AdInfo info) {
                if (info == null) {
                    LogUtils.e("DEMO>>>adExposed， AdInfo is empty");
                } else {
                    LogUtils.e("DEMO>>>adExposed， "+ info.toString());
                }
                LogUtils.e("adExposed");

            }

            @Override
            public void adClicked(AdInfo info) {
                if (info == null) {
                    LogUtils.e("DEMO>>>adClicked， AdInfo is empty");
                } else {
                    LogUtils.e("DEMO>>>adClicked， "+ info.toString());
                }
            }

            @Override
            public void adError(int errorCode, String errorMsg) {
                LogUtils.e("DEMO>>>adError： "+errorMsg);
            }
        });



    }






}
