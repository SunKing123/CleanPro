package com.xiaoniu.cleanking.ui.newclean.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.comm.jksdk.utils.DisplayUtil;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jess.arms.utils.DeviceUtils;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.constant.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.activity.AgentWebViewActivity;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.activity.GameActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.main.bean.BottoomAdList;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.main.presenter.CleanFinishPresenter;
import com.xiaoniu.cleanking.ui.news.adapter.NewsListAdapter;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.FromHomeCleanFinishEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.ui.tool.notify.utils.NotifyUtils;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.PermissionUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Random;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.jzvd.Jzvd;

import static android.view.View.VISIBLE;

/**
 * 1.2.0 版本以后清理完成 显示资讯
 */
public class NewCleanFinishActivity extends BaseActivity<CleanFinishPresenter> implements View.OnClickListener {

    private static final String TAG = "GeekSdk";
    private String mTitle = "";
    private TextView mTitleTv;
    private TextView mTvSize;
    private TextView mTvGb;
    private TextView mTvQl;
    private XRecyclerView mRecyclerView;
    private NewsListAdapter mNewsAdapter;
    private ImageView mBtnLeft;
    public View v_quicken, v_power, v_notification, v_wechat, v_file, v_cool, v_clean_all, v_game;
    public View line_quicken, line_power, line_notification, line_wechat, line_cool, line_clean_all, line_game;
    private View mRecommendV;
    private TextView tv_quicken, tv_power, tv_notification;
    private ImageView iv_quicken, iv_power, iv_notification;
    private boolean isScreenSwitchOpen; //插屏广告开关
    private int mScreenShowCount; //插屏广告展示次数
    private ViewGroup advContentView;
    private int mShowCount; //推荐显示的数量
    private int mRamScale; //所有应用所占内存大小
    public static String sourcePage = "";
    public static String currentPage = "";
    String createEventCode = "";
    String createEventName = "";
    String returnEventName = "";
    String sysReturnEventName = "";
    private boolean mIsFromHomeMain; //是否来自首页主功能区
    boolean isOpenOne = false;
    boolean isOpenTwo = false;

    FileQueryUtils fileQueryUtils;
    int processNum = 0;

    private FrameLayout ad_container_pos01, ad_container_pos02;
    private ImageView error_ad_iv1, error_ad_iv2;

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
        mIsFromHomeMain = getIntent().getBooleanExtra("main", false);
        fileQueryUtils = new FileQueryUtils();
        processNum = fileQueryUtils.getRunningProcess().size();
        mTitle = getIntent().getStringExtra("title");
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

        ad_container_pos01 = header.findViewById(R.id.ad_container_pos01);
        error_ad_iv1 = header.findViewById(R.id.error_ad_iv1);
        mTvSize = header.findViewById(R.id.tv_size);
        mTvGb = header.findViewById(R.id.tv_clear_finish_gb_title);
        mTvSize.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/FuturaRound-Medium.ttf"));
        mTvGb.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/FuturaRound-Medium.ttf"));
        mTvQl = header.findViewById(R.id.tv_ql);
        advContentView = headerTool.findViewById(R.id.finish_framelayout_ad);
        ad_container_pos02 = headerTool.findViewById(R.id.ad_container_pos02);
        error_ad_iv2 = headerTool.findViewById(R.id.error_ad_iv2);
        mRecommendV = headerTool.findViewById(R.id.v_recommend);
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
        line_cool = headerTool.findViewById(R.id.line_cool);
        line_clean_all = headerTool.findViewById(R.id.line_clean_all);
        line_game = headerTool.findViewById(R.id.line_game);

        tv_quicken = headerTool.findViewById(R.id.tv_quicken);
        tv_power = headerTool.findViewById(R.id.tv_power);
        tv_notification = headerTool.findViewById(R.id.tv_notification);

        iv_quicken = headerTool.findViewById(R.id.iv_quicken);
        iv_power = headerTool.findViewById(R.id.iv_power);
        iv_notification = headerTool.findViewById(R.id.iv_notification);
        mTitleTv.setText(mTitle);
        mPresenter.getScreentSwitch();
        getPageData();
        setListener();
        loadData();
        initGeekAd();
    }

    private void initGeekAd() {
        if (null != AppHolder.getInstance() && null != AppHolder.getInstance().getSwitchInfoList()
                && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {

            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (getString(R.string.tool_suggest_clean).contains(mTitle) && PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey())) { //建议清理
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_ONE_CODE)) {
                        isOpenOne = switchInfoList.isOpen();
                    }
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_TWO_CODE)) {
                        isOpenTwo = switchInfoList.isOpen();
                    }
                } else if (getString(R.string.tool_one_key_speed).contains(mTitle) && PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey())) { //一键加速
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_ONE_CODE)) {
                        isOpenOne = switchInfoList.isOpen();
                    }
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_TWO_CODE)) {
                        isOpenTwo = switchInfoList.isOpen();
                    }
                } else if (getString(R.string.tool_super_power_saving).contains(mTitle) && PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey())) { //超强省电
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_ONE_CODE)) {
                        isOpenOne = switchInfoList.isOpen();
                    }
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_TWO_CODE)) {
                        isOpenTwo = switchInfoList.isOpen();
                    }
                } else if (getString(R.string.tool_notification_clean).contains(mTitle) && PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey())) {//通知栏清理
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_ONE_CODE)) {
                        isOpenOne = switchInfoList.isOpen();
                    }
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_TWO_CODE)) {
                        isOpenTwo = switchInfoList.isOpen();
                    }
                } else if (getString(R.string.tool_chat_clear).contains(mTitle)) { //微信清理
                    if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey())) {
                        if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_ONE_CODE)) {
                            isOpenOne = switchInfoList.isOpen();
                        }
                        if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_TWO_CODE)) {
                            isOpenTwo = switchInfoList.isOpen();
                        }
                    }
                } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle) && PositionId.KEY_COOL.equals(switchInfoList.getConfigKey())) { //手机降温
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_ONE_CODE)) {
                        isOpenOne = switchInfoList.isOpen();
                    }
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_TWO_CODE)) {
                        isOpenTwo = switchInfoList.isOpen();
                    }
                } else if (getString(R.string.tool_qq_clear).contains(mTitle) && PositionId.KEY_QQ.equals(switchInfoList.getConfigKey())) { //QQ专清
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_ONE_CODE)) {
                        isOpenOne = switchInfoList.isOpen();
                    }
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_TWO_CODE)) {
                        isOpenTwo = switchInfoList.isOpen();
                    }
                } else if (getString(R.string.tool_phone_clean).contains(mTitle) && PositionId.KEY_PHONE.equals(switchInfoList.getConfigKey())) { //手机清理
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_ONE_CODE)) {
                        isOpenOne = switchInfoList.isOpen();
                    }
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_TWO_CODE)) {
                        isOpenTwo = switchInfoList.isOpen();
                    }
                } else if (getString(R.string.game_quicken).contains(mTitle) && PositionId.KEY_GAME.equals(switchInfoList.getConfigKey())) { //游戏加速
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_ONE_CODE)) {
                        isOpenOne = switchInfoList.isOpen();
                    }
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_TWO_CODE)) {
                        isOpenTwo = switchInfoList.isOpen();
                    }
                } else if (getString(R.string.virus_kill).contains(mTitle) && PositionId.KEY_VIRUS.equals(switchInfoList.getConfigKey())) { //病毒查杀
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_ONE_CODE)) {
                        isOpenOne = switchInfoList.isOpen();
                    }
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_TWO_CODE)) {
                        isOpenTwo = switchInfoList.isOpen();
                    }
                } else if (getString(R.string.network_quicken).contains(mTitle) && PositionId.KEY_NET.equals(switchInfoList.getConfigKey())) { //网络加速
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_ONE_CODE)) {
                        isOpenOne = switchInfoList.isOpen();
                    }
                    if (switchInfoList.getAdvertPosition().equals(PositionId.DRAW_TWO_CODE)) {
                        isOpenTwo = switchInfoList.isOpen();
                    }
                }
            }
            if (isOpenOne) {
                initPos01Ad();
            }

            if (isOpenTwo) {
                initAd02();
            }
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
            createEventCode = "boost_success_page_custom";
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
            if (mIsFromHomeMain) {
                currentPage = "main_function_area_gameboost_success_page";
                createEventName = "主功能区游戏加速结果页创建时";
                createEventCode = "main_function_area_gameboost_success_page_custom";
                returnEventName = "主功能区游戏加速结果页返回";
                sysReturnEventName = "主功能区游戏加速结果页返回";
            } else {
                currentPage = "gameboost_success_page";
                createEventName = "游戏加速结果页创建时";
                createEventCode = "gameboost_success_page_custom";
                returnEventName = "游戏加速结果页返回";
                sysReturnEventName = "游戏加速结果页返回";
            }
        } else if (getString(R.string.virus_kill).contains(mTitle)) {
            //病毒查杀
            currentPage = "virus_killing_success_page";
            createEventName = "病毒查杀结果页创建时";
            createEventCode = "virus_killing_success_page_custom";
            returnEventName = "用户在病毒查杀结果页返回";
            sysReturnEventName = "用户在病毒查杀结果页返回";
        } else if (getString(R.string.network_quicken).contains(mTitle)) {
            //网络加速
            currentPage = "network_acceleration_success_page";
            createEventName = "网络加速结果页创建时";
            createEventCode = "network_acceleration_success_page_view_page";
            returnEventName = "用户在网络加速结果页返回";
            sysReturnEventName = "用户在网络加速结果页返回";
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
                if (mIsFromHomeMain) {
                    currentPage = "main_function_area_direct_gameboots_success_page";
                    createEventName = "主功能区直接跳游戏加速结果页创建时";
                    createEventCode = "main_function_area_direct_game_success_page_custom";
                } else {
                    currentPage = "direct_gameboots_success_page";
                    createEventName = "直接跳游戏加速结果页创建时";
                    createEventCode = "direct_game_success_page_custom";
                }
            }
        }
    }

    /**
     * 拉取插屏广告开关成功
     *
     * @return
     */
    public void getScreentSwitchSuccess(InsertAdSwitchInfoList list) {
        for (InsertAdSwitchInfoList.DataBean switchInfoList : list.getData()) {
            if (getString(R.string.tool_suggest_clean).contains(mTitle) && PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey())) { //建议清理
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            } else if (getString(R.string.tool_one_key_speed).contains(mTitle) && PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey())) { //一键加速
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            } else if (getString(R.string.tool_super_power_saving).contains(mTitle) && PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey())) { //超强省电
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            } else if (getString(R.string.tool_notification_clean).contains(mTitle) && PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey())) {//通知栏清理
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            } else if (getString(R.string.tool_chat_clear).contains(mTitle)) { //微信清理
                if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey())) {
                    isScreenSwitchOpen = switchInfoList.isOpen();
                    mScreenShowCount = switchInfoList.getShowRate();
                }
            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle) && PositionId.KEY_COOL.equals(switchInfoList.getConfigKey())) { //手机降温
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            } else if (getString(R.string.tool_qq_clear).contains(mTitle) && PositionId.KEY_QQ.equals(switchInfoList.getConfigKey())) { //QQ专清
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            } else if (getString(R.string.tool_phone_clean).contains(mTitle) && PositionId.KEY_PHONE.equals(switchInfoList.getConfigKey())) { //手机清理
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            } else if (getString(R.string.game_quicken).contains(mTitle) && PositionId.KEY_GAME.equals(switchInfoList.getConfigKey())) { //游戏加速
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            } else if (getString(R.string.virus_kill).contains(mTitle) && PositionId.KEY_VIRUS.equals(switchInfoList.getConfigKey())) { //病毒查杀
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            } else if (getString(R.string.network_quicken).contains(mTitle) && PositionId.KEY_NET.equals(switchInfoList.getConfigKey())) { //网络加速
                isScreenSwitchOpen = switchInfoList.isOpen();
                mScreenShowCount = switchInfoList.getShowRate();
            }
        }
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
                int tem = PreferenceUtil.getCleanCoolNum();
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
                mTvGb.setText("安全");
                mTvGb.setTextSize(25);
                mTvQl.setText(R.string.virus_guard);
                mTvSize.setText("");
            } else if (getString(R.string.network_quicken).contains(mTitle)) {
                mTvSize.setText(num);
                mTvGb.setText("%");
                mTvQl.setText("已提速");
                mTvQl.setTextSize(20);
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

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    /**
     * 是否显示推荐功能项
     */
    private void showTool() {
        mShowCount = 0;
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
            if (mShowCount >= 3) return;
            line_game.setVisibility(View.VISIBLE);
        }
        if (!getString(R.string.tool_phone_temperature_low).contains(mTitle) && !PreferenceUtil.isCleanCoolUsed()) {
            // 手机降温间隔时间至少3分钟 否则隐藏
            if (mShowCount >= 3) return;
            mShowCount++;
            v_cool.setVisibility(View.VISIBLE);
            if (mShowCount >= 3) return;
            line_cool.setVisibility(View.VISIBLE);
        }

        //文件清理
        if (mShowCount >= 3) return;
        mShowCount++;
        v_file.setVisibility(View.VISIBLE);
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
            EventBus.getDefault().post(new FromHomeCleanFinishEvent(mTitle));
            StatisticsUtils.trackClick("return_click", returnEventName, sourcePage, currentPage);
            //插屏广告拉取失败禁止跳转到插屏广告页
          /*  if (mIsScreenAdSuccess) {
                finish();
                return;
            }*/
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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    //获取第一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findFirstVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    //recyclerView滑动到底部再滑动回顶部后重新执行动画

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
                }
            });
        }

    }

    protected void loadData() {
        //页面创建事件埋点
        StatisticsUtils.customTrackEvent(createEventCode, createEventName, sourcePage, currentPage);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().post(new FromHomeCleanFinishEvent(mTitle));
        StatisticsUtils.trackClick("system_return_click", sysReturnEventName, sourcePage, currentPage);

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
        initBottomAdv();

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
        StatusBarCompat.setStatusBarColor(mContext, getResources().getColor(R.color.color_27D698), true);
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


        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mRecyclerView != null) {
            mRecyclerView.destroy(); // this will totally release XR's memory
            mRecyclerView = null;
        }
        //Umeng --- Caused by: java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity
        if (Util.isOnMainThread()) {
            Glide.get(this).clearMemory();
        }
    }

    private static final int AD_COUNT = 1;
    private static final int MSG_INIT_AD = 0;
    private static final int MSG_VIDEO_START = 1;

    @Override
    public void netError() {

    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null || listInfo.size() <= 0) return;
        if (null == fileQueryUtils)
            fileQueryUtils = new FileQueryUtils();
        mRamScale = fileQueryUtils.computeTotalSize(listInfo);
        changeUI(getIntent());
    }

    public void initPos01Ad() {
        if (!isOpenOne) {
           return;
        }
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", sourcePage, currentPage);
        AdManager adManager = GeekAdSdk.getAdsManger();

        adManager.loadNativeTemplateAd(this, PositionId.AD_CLEAN_FINISH_POSITION_FOUR, Float.valueOf(DisplayUtil.px2dp(NewCleanFinishActivity.this, DisplayUtil.getScreenWidth(NewCleanFinishActivity.this)) - 28), new AdListener() {
            @Override
            public void adSuccess(AdInfo info) {
                if (info == null) {
                    Log.d(TAG, "DEMO>>>adSuccess1， AdInfo is empty");
                } else {
                    StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", sourcePage, currentPage);
                    Log.d(TAG, "DEMO>>>adSuccess1， " + info.toString());
                    if (info.getAdView() != null && null != ad_container_pos01) {
                        View adView = info.getAdView();
                        ad_container_pos01.removeAllViews();
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, (int) DeviceUtils.dpToPixel(NewCleanFinishActivity.this, 12), 0, (int) DeviceUtils.dpToPixel(NewCleanFinishActivity.this, 12));
                        adView.setLayoutParams(params);
                        adView.setBackground(getResources().getDrawable(R.drawable.bg_btn_white12));
                        ad_container_pos01.addView(adView);
                    }
                }
            }

            @Override
            public void adExposed(AdInfo info) {
                if (info == null) {
                    Log.d(TAG, "DEMO>>>adExposed1， AdInfo is empty");
                } else {
                    StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), sourcePage, currentPage, info.getAdTitle());
                    Log.d(TAG, "DEMO>>>adExposed1， ");
                }

            }

            @Override
            public void adClicked(AdInfo info) {
                if (null == info) return;
                Log.d(TAG, "adClicked1");
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), sourcePage, currentPage, info.getAdTitle());
                initPos01Ad();
            }

            @Override
            public void adError(AdInfo info, int errorCode, String errorMsg) {
                Log.d(TAG, "adError 111： " + errorMsg);
                if (null != info) {
                    StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "fail", sourcePage, currentPage);
                }
                //打底样式
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showBottomAd();
                    }
                });

            }
        });
    }

    private int mBottomAdShowCount = 0;

    /**
     * 打底广告
     */
    private void showBottomAd() {
        if (null != AppHolder.getInstance().getBottomAdList() &&
                AppHolder.getInstance().getBottomAdList().size() > 0) {
            for (BottoomAdList.DataBean dataBean : AppHolder.getInstance().getBottomAdList()) {
                if (dataBean.getSwitcherKey().equals(PositionId.KEY_CLEAN_ALL)
                        && dataBean.getAdvertPosition().equals(PositionId.DRAW_ONE_CODE)) {
                    if (dataBean.getShowType() == 1) { //循环
                        mBottomAdShowCount = PreferenceUtil.getFinishAdOneCount();
                        if (mBottomAdShowCount >= dataBean.getAdvBottomPicsDTOS().size() - 1) {
                            PreferenceUtil.saveFinishAdOneCount(0);
                        } else {
                            PreferenceUtil.saveFinishAdOneCount(PreferenceUtil.getFinishAdOneCount() + 1);
                        }
                    } else { //随机
                        if (dataBean.getAdvBottomPicsDTOS().size() == 1) {
                            mBottomAdShowCount = 0;
                        } else {
                            mBottomAdShowCount = new Random().nextInt(dataBean.getAdvBottomPicsDTOS().size() - 1);
                        }
                    }
                    ad_container_pos01.setVisibility(View.GONE);
                    error_ad_iv1.setVisibility(View.VISIBLE);
                    StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", " ", "自定义广告", sourcePage, currentPage, dataBean.getSwitcherName());
                    GlideUtils.loadImage(this, dataBean.getAdvBottomPicsDTOS().get(mBottomAdShowCount).getImgUrl(), error_ad_iv1);
                    error_ad_iv1.setOnClickListener(v -> {
                        StatisticsUtils.clickAD("ad_click", "广告点击", "1", " ", "自定义广告", sourcePage, currentPage, dataBean.getSwitcherName());
                        AppHolder.getInstance().setCleanFinishSourcePageId(currentPage);
                        startActivityForResult(new Intent(this, AgentWebViewActivity.class)
                                .putExtra(ExtraConstant.WEB_URL, dataBean.getAdvBottomPicsDTOS().get(mBottomAdShowCount).getLinkUrl())
                                .putExtra(ExtraConstant.WEB_FROM, "FinishActivity"), 100);
                    });
                }
            }
        }
    }

    public void initAd02() {
        StatisticsUtils.customADRequest("ad_request", "广告请求", "2", " ", " ", "all_ad_request", sourcePage, currentPage);
        AdManager adManager = GeekAdSdk.getAdsManger();
        adManager.loadAd(this, PositionId.AD_CLEAN_FINISH_POSITION_TWO, new AdListener() {
            @Override
            public void adSuccess(AdInfo info) {
                if (null != info) {
                    Log.d(TAG, "DEMO>>>adSuccess2， " + info.toString());
                    StatisticsUtils.customADRequest("ad_request", "广告请求", "2", info.getAdId(), info.getAdSource(), "success", sourcePage, currentPage);
                    if (info.getAdView() != null) {
                        ad_container_pos02.removeAllViews();
                        ad_container_pos02.addView(info.getAdView());
                    }
                }
            }

            @Override
            public void adExposed(AdInfo info) {
                Log.d(TAG, "adExposed");
                if (null == info) return;
                Log.d(TAG, "DEMO>>>adExposed2， ");
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "2", info.getAdId(), info.getAdSource(), sourcePage, currentPage, info.getAdTitle());
            }

            @Override
            public void adClicked(AdInfo info) {
                Log.d(TAG, "adClicked2");
                if (null == info) return;
                StatisticsUtils.clickAD("ad_click", "广告点击", "2", info.getAdId(), info.getAdSource(), sourcePage, currentPage, info.getAdTitle());
            }

            @Override
            public void adError(AdInfo info, int errorCode, String errorMsg) {
                Log.d(TAG, "adError2222");
                if (null != info) {
                    StatisticsUtils.customADRequest("ad_request", "广告请求", "2", info.getAdId(), info.getAdSource(), "fail", sourcePage, currentPage);
                }
                showBottomAd2();
            }
        });
    }

    private int mBottomAdShowCount2 = 0;

    /**
     * 打底广告
     */
    private void showBottomAd2() {
        if (null != AppHolder.getInstance().getBottomAdList() &&
                AppHolder.getInstance().getBottomAdList().size() > 0) {
            for (BottoomAdList.DataBean dataBean : AppHolder.getInstance().getBottomAdList()) {
                if (dataBean.getSwitcherKey().equals(PositionId.KEY_CLEAN_ALL)
                        && dataBean.getAdvertPosition().equals(PositionId.DRAW_TWO_CODE)) {
                    if (dataBean.getShowType() == 1) { //循环
                        mBottomAdShowCount2 = PreferenceUtil.getFinishAdTwoCount();
                        if (mBottomAdShowCount2 >= dataBean.getAdvBottomPicsDTOS().size() - 1) {
                            PreferenceUtil.saveFinishAdTwoCount(0);
                        } else {
                            PreferenceUtil.saveFinishAdTwoCount(PreferenceUtil.getFinishAdTwoCount() + 1);
                        }
                    } else { //随机
                        if (dataBean.getAdvBottomPicsDTOS().size() == 1) {
                            mBottomAdShowCount2 = 0;
                        } else {
                            mBottomAdShowCount2 = new Random().nextInt(dataBean.getAdvBottomPicsDTOS().size() - 1);
                        }
                    }
                    ad_container_pos02.setVisibility(View.GONE);
                    error_ad_iv2.setVisibility(View.VISIBLE);
                    StatisticsUtils.customAD("ad_show", "广告展示曝光", "2", " ", "自定义广告", sourcePage, currentPage, dataBean.getSwitcherName());
                    GlideUtils.loadImage(this, dataBean.getAdvBottomPicsDTOS().get(mBottomAdShowCount2).getImgUrl(), error_ad_iv2);
                    error_ad_iv2.setOnClickListener(v -> {
                        StatisticsUtils.clickAD("ad_click", "广告点击", "2", " ", "自定义广告", sourcePage, currentPage, dataBean.getSwitcherName());
                        AppHolder.getInstance().setCleanFinishSourcePageId(currentPage);
                        startActivityForResult(new Intent(this, AgentWebViewActivity.class)
                                .putExtra(ExtraConstant.WEB_URL, dataBean.getAdvBottomPicsDTOS().get(mBottomAdShowCount2).getLinkUrl())
                                .putExtra(ExtraConstant.WEB_FROM, "FinishActivity"), 200);
                    });
                }
            }
        }
    }


    private void initBottomAdv() {
        boolean isOpen = false;
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_AD_PAGE_FINISH.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isOpen = switchInfoList.isOpen();
                    break;
                }
            }
        }
        if (!isOpen) return;
        if (null == advContentView) return;
        StatisticsUtils.customADRequest("ad_request", "广告请求", "3", "", "", "all_ad_request", "success_page_bottom_ad", "success_page_bottom_ad");
        AdManager adManager = GeekAdSdk.getAdsManger();
        adManager.loadAd(this, PositionId.DRAW_FINISH_CODE
                , new AdListener() {
                    @Override
                    public void adSuccess(AdInfo info) {
                        if (null != info) {
                            StatisticsUtils.customADRequest("ad_request", "广告请求", "3", info.getAdId(), info.getAdSource(), "success", "success_page_bottom_ad", "success_page_bottom_ad");
                            if (null != advContentView && null != info.getAdView()) {
                                advContentView.setVisibility(VISIBLE);
                                advContentView.removeAllViews();
                                advContentView.addView(info.getAdView());
                            }
                        }
                    }

                    @Override
                    public void adExposed(AdInfo info) {
                        if (null == info) return;
                        StatisticsUtils.customAD("ad_show", "广告展示曝光", "3", info.getAdId(), info.getAdSource(), "success_page_bottom_ad", "success_page_bottom_ad", info.getAdTitle());
                    }

                    @Override
                    public void adClicked(AdInfo info) {
                        if (null == info) return;
                        StatisticsUtils.clickAD("ad_click", "广告点击", "3", info.getAdId(), info.getAdSource(), "success_page_bottom_ad", "success_page_bottom_ad", info.getAdTitle());

                    }

                    @Override
                    public void adClose(AdInfo info) {
                        if (null == info) return;
                        advContentView.setVisibility(View.GONE);
                    }

                    @Override
                    public void adError(AdInfo info, int errorCode, String errorMsg) {
                        if (null == info) return;
                        StatisticsUtils.customADRequest("ad_request", "广告请求", "3", info.getAdId(), info.getAdSource(), "fail", "success_page_bottom_ad", "success_page_bottom_ad");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100:
                showBottomAd();
                return;
            case 200:
                showBottomAd2();
                return;
        }
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

}
