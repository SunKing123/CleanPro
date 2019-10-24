package com.xiaoniu.cleanking.ui.newclean.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.main.presenter.CleanFinishAdvertisementPresenter;
import com.xiaoniu.cleanking.ui.tool.notify.event.FromHomeCleanFinishEvent;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    private ImageView iv_advert_logo, iv_advert, mErrorIv;
    private TextView tv_advert, tv_advert_content;
    private TextView mBtnDownload;
    private View mViewContent;

    private NativeUnifiedADData mNativeUnifiedADData;
    private NativeUnifiedAD mAdManager;
    private MediaView mMediaView;
    private NativeAdContainer mContainer;

    private String mAdvertId = ""; //广告位1


    String sourcePage = "";
    String currentPage = "";
    String createEventCode = "";
    String createEventName = "";
    String returnEventName = "";
    String sysReturnEventName = "";

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
        mPresenter.getSwitchInfoList();
        findViewById(R.id.btnLeft).setOnClickListener(this);
        mViewContent = findViewById(R.id.v_content);
        mErrorIv = (ImageView) findViewById(R.id.iv_error);
        mTitleTv = (TextView) findViewById(R.id.tvTitle);

        mTvSize = findViewById(R.id.tv_size);
        mTvGb = findViewById(R.id.tv_clear_finish_gb_title);
        mTvQl = findViewById(R.id.tv_ql);

        mContainer = findViewById(R.id.native_ad_container);
        mMediaView = findViewById(R.id.gdt_media_view);
        iv_advert_logo = findViewById(R.id.iv_advert_logo);
        iv_advert = findViewById(R.id.iv_advert);
        tv_advert = findViewById(R.id.tv_advert);
        tv_advert_content = findViewById(R.id.tv_advert_content);

        mBtnDownload = findViewById(R.id.tv_download);
        changeUI(getIntent());
        getPageData();
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
                //悟空清理
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
                    initNativeUnifiedAD();
                }
            } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) { //超强省电
                if (PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    initNativeUnifiedAD();
                }
            } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {//通知栏清理
                if (PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    initNativeUnifiedAD();
                }
            } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {//微信专情
                if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    initNativeUnifiedAD();
                }
            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) { //手机降温
                if (PositionId.KEY_COOL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    initNativeUnifiedAD();
                }
            } else if (getString(R.string.tool_qq_clear).contains(mTitle)) { //QQ专清
                if (PositionId.KEY_QQ.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    initNativeUnifiedAD();
                }
            } else if (getString(R.string.tool_phone_clean).contains(mTitle)) { //手机清理
                if (PositionId.KEY_PHONE.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    initNativeUnifiedAD();
                }
            } else { //立即清理
                if (PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    initNativeUnifiedAD();
                }
            }
        }
        Log.d(TAG, "mAdvertId=" + mAdvertId);
    }

    /**
     * 拉取广告开关失败
     *
     * @return
     */
    public void getSwitchInfoListFail(String message) {
        mBtnDownload.setVisibility(View.GONE);
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
        mBtnDownload.setVisibility(View.GONE);
        mViewContent.setVisibility(View.GONE);
        mErrorIv.setVisibility(View.VISIBLE);
        ToastUtils.showShort("网络连接失败，请假查您的网络连接");
    }

    private void initNativeUnifiedAD() {
        mAdManager = new NativeUnifiedAD(this, PositionId.APPID, mAdvertId, new NativeADUnifiedListener() {

            @Override
            public void onNoAD(AdError adError) {
                Log.d(TAG, "onNoAd error code: " + adError.getErrorCode() + ", error msg: " + adError.getErrorMsg());
                mContainer.setVisibility(View.GONE);
                mBtnDownload.setVisibility(View.GONE);
                mViewContent.setVisibility(View.GONE);
                mErrorIv.setVisibility(View.VISIBLE);
                StatisticsUtils.customADRequest("ad_request", "广告请求", "3", mAdvertId, "优量汇", "fail", sourcePage, currentPage);
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
            //悟空清理
            currentPage = "clean_success_page";
        } else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
            //一键加速
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
            //悟空清理
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
            //悟空清理
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

    private H mHandler = new H();
    private static final int AD_COUNT = 1;
    private static final int MSG_INIT_AD = 0;
    private static final int MSG_VIDEO_START = 1;

    @Override
    public void netError() {

    }

    private class H extends Handler {
        public H() {
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
                    initAd(ad);
                    Log.d(TAG, "eCPM = " + mNativeUnifiedADData.getECPM() + " , eCPMLevel = " + mNativeUnifiedADData.getECPMLevel());
                    break;
                case MSG_VIDEO_START:
                    Log.d("AD_DEMO", "handleMessage");
                    iv_advert.setVisibility(View.GONE);
                    mMediaView.setVisibility(View.VISIBLE);
                    break;
            }
        }

    }

    //加载广告
    private void initAd(final NativeUnifiedADData ad) {
        StatisticsUtils.customADRequest("ad_request", "广告请求", "3", mAdvertId, "优量汇", "success", sourcePage, currentPage);
        renderAdUi(ad);

        List<View> clickableViews = new ArrayList<>();
        // 所有广告类型，注册mDownloadButton的点击事件
        clickableViews.add(mBtnDownload);
        ad.bindAdToView(this, mContainer, null, clickableViews);

        // 设置广告事件监听
        ad.setNativeAdEventListener(new NativeADEventListener() {
            @Override
            public void onADExposed() {
                StatisticsUtils.customADRequest("ad_show", "广告展示曝光", "3", mAdvertId, "优量汇", "success", sourcePage, currentPage);
                Log.d(TAG, "广告曝光");
            }

            @Override
            public void onADClicked() {
                Log.d(TAG, "onADClicked: " + " clickUrl: " + ad.ext.get("clickUrl"));
                StatisticsUtils.clickAD("ad_click", "广告点击", "3", mAdvertId, "优量汇", sourcePage, currentPage, ad.getTitle());

            }

            @Override
            public void onADError(AdError error) {
                Log.d(TAG, "错误回调 error code :" + error.getErrorCode()
                        + "  error msg: " + error.getErrorMsg());
            }

            @Override
            public void onADStatusChanged() {
                Log.d(TAG, "广告状态变化");
                updateAdAction(mBtnDownload, ad);
            }
        });
        updateAdAction(mBtnDownload, ad);

        if (ad.getAdPatternType() == AdPatternType.NATIVE_VIDEO) { //视频类型
            mHandler.sendEmptyMessage(MSG_VIDEO_START);

            VideoOption.Builder builder = new VideoOption.Builder();
            builder.setAutoPlayMuted(true) //设置视频广告在预览页自动播放时是否静音
                    .setEnableDetailPage(false) //点击视频是否跳转到详情页
                    .setEnableUserControl(true) //设置是否允许用户在预览页点击视频播放器区域控制视频的暂停或播放
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
                            Log.d(TAG, "onVideoStart: ");
                        }

                        @Override
                        public void onVideoPause() {
                            Log.d(TAG, "onVideoPause: ");
                        }

                        @Override
                        public void onVideoResume() {
                            Log.d(TAG, "onVideoResume: ");
                        }

                        @Override
                        public void onVideoCompleted() {
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
    }
}
