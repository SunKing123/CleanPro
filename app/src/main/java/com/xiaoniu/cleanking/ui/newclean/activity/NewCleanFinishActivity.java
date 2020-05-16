package com.xiaoniu.cleanking.ui.newclean.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.enums.AdType;
import com.xiaoniu.cleanking.ad.interfaces.AdShowCallBack;
import com.xiaoniu.cleanking.ad.mvp.presenter.AdPresenter;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.activity.GameActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.NewsItemInfo;
import com.xiaoniu.cleanking.ui.main.bean.NewsItemInfoRuishi;
import com.xiaoniu.cleanking.ui.main.bean.NewsType;
import com.xiaoniu.cleanking.ui.main.bean.VideoItemInfo;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.main.presenter.CleanFinishPresenter;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.ui.news.adapter.ComFragmentAdapter;
import com.xiaoniu.cleanking.ui.news.adapter.NewsListAdapter;
import com.xiaoniu.cleanking.ui.news.adapter.NewsTypeNavigatorAdapter;
import com.xiaoniu.cleanking.ui.news.fragment.NewsListFragment;
import com.xiaoniu.cleanking.ui.news.listener.OnClickNewsItemListener;
import com.xiaoniu.cleanking.ui.news.utils.NewsUtils;
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
import com.xiaoniu.cleanking.utils.ScreenUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.MeasureViewPager;
import com.xiaoniu.cleanking.widget.OperatorNestedScrollView;
import com.xiaoniu.cleanking.widget.magicIndicator.MagicIndicator;
import com.xiaoniu.cleanking.widget.magicIndicator.ViewPagerHelper;
import com.xiaoniu.cleanking.widget.magicIndicator.buildins.commonnavigator.CommonNavigator;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 1.2.0 版本以后清理完成 显示资讯
 */
public class NewCleanFinishActivity extends BaseActivity<CleanFinishPresenter> implements View.OnClickListener, NestedScrollView.OnScrollChangeListener  {

    private static final String TAG = "AD_DEMO";


    /*< XD added for feed begin */
    @BindView(R.id.layout_root)
    LinearLayout layoutRoot;
    @BindView(R.id.v_clean_finish_top)
    RelativeLayout vHomeTop;
    @BindView(R.id.v_clean_finish_top_normal)
    LinearLayout vTopTitleNormal;    // normal
    @BindView(R.id.ll_top_xiding)
    LinearLayout vTopTitleXiding;

    @BindView(R.id.layout_scroll)
    OperatorNestedScrollView mNestedScrollView;

//    @BindView(R.id.close_feed_empty_view)
//    View close_feed_empty_view;
    @BindView(R.id.home_feeds)
    LinearLayout homeFeeds;    // 信息流
    @BindView(R.id.feed_view_pager)
    MeasureViewPager feedViewPager;   // feed pager
    @BindView(R.id.feed_indicator)
    MagicIndicator feedIndicator;
    @BindView(R.id.fl_top_nav)
    LinearLayout mFLTopNav;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_top_xiding_back)
    TextView tvTopXidingBack;

    private String mTitleType = "white";
    private static final String KEY_TYPE = "TYPE";
    private NewsType[] mNewTypes = {NewsType.TOUTIAO, NewsType.SHEHUI, NewsType.GUOJI, NewsType.YUN_SHI, NewsType.JIAN_KANG, NewsType.REN_WEN};
    private NewsTypeNavigatorAdapter mNewsTypeNaviAdapter;
    private ComFragmentAdapter mNewsListFragmentAdapter;
    private List<com.xiaoniu.common.base.BaseFragment> mNewsListFragments;  // NewsListFragments
    private boolean canXiding = true;
    public LayoutInflater mInflater;
    private int mStatusBarHeight;
    private int mStickyHeight;
    private int mRootHeight;
    /* XD added for feed End >*/

    private String mTitle = "";
    private TextView mTitleTv;
    private TextView mTvSize;
    private TextView mTvGb;
    private TextView mTvQl;

    private XRecyclerView mRecyclerView;
    private NewsListAdapter mNewsAdapter;     //
    private NewsType mType = NewsType.TOUTIAO;
    private ImageView mBtnLeft;

    public View v_quicken, v_power, v_notification, v_wechat, v_file, v_cool, v_clean_all, v_game;
    public View line_quicken, line_power, line_notification, line_wechat, line_file, line_clean_all, line_game;
    private View  mRecommendV;
    private FrameLayout adUpView;
    private LinearLayout adUpContainer;
    private FrameLayout adDownView;
    private LinearLayout adDownContainer;
    private TextView tv_quicken, tv_power, tv_notification;
    private ImageView iv_quicken, iv_power, iv_notification;

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

    FileQueryUtils fileQueryUtils;

    int processNum = 0;

    /*< XD added for feed begin */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        initVariable(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    protected void initVariable(Bundle arguments) {
        mNewTypes = NewsUtils.sNewTypes;
        mNewsListFragments = new ArrayList<>();
        if (arguments != null) {
            mTitleType = arguments.getString(KEY_TYPE);
        }
    }
    /* XD added for feed End >*/

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
        mInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        mStatusBarHeight = ScreenUtil.getStatusBarHeight(this);
        mStickyHeight = ScreenUtil.dp2px(this, 80);

        EventBus.getDefault().register(this);
        fileQueryUtils = new FileQueryUtils();
        processNum = fileQueryUtils.getRunningProcess().size();
        mTitle = getIntent().getStringExtra("title");
        if (TextUtils.isEmpty(mTitle)) {
            mTitle = getString(R.string.app_name);
        }
        if (getString(R.string.tool_one_key_speed).contains(mTitle)
                || getString(R.string.tool_notification_clean).contains(mTitle)
                || getString(R.string.tool_super_power_saving).contains(mTitle)) {
            EventBus.getDefault().post(new FromHomeCleanFinishEvent(mTitle));
        }
        mBtnLeft = findViewById(R.id.btnLeft);
        mTitleTv = findViewById(R.id.tvTitle);
        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);

        mNewsAdapter = new NewsListAdapter(this,PositionId.KEY_CLEAN_FINISH_NEWS);

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
        adUpView= header.findViewById(R.id.v_advert);
        adUpContainer = header.findViewById(R.id.native_ad_container);

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
        line_file = headerTool.findViewById(R.id.line_file);
        line_clean_all = headerTool.findViewById(R.id.line_clean_all);
        line_game = headerTool.findViewById(R.id.line_game);

        tv_quicken = headerTool.findViewById(R.id.tv_quicken);
        tv_power = headerTool.findViewById(R.id.tv_power);
        tv_notification = headerTool.findViewById(R.id.tv_notification);

        iv_quicken = headerTool.findViewById(R.id.iv_quicken);
        iv_power = headerTool.findViewById(R.id.iv_power);
        iv_notification = headerTool.findViewById(R.id.iv_notification);
        adDownView= headerTool.findViewById(R.id.v_advert);
        adDownContainer = headerTool.findViewById(R.id.native_ad_container);
        mTitleTv.setText(mTitle);

        initFeedView();  // XD added 20200509

        getPageData();
        setListener();
        loadData();
//        getSwitchInfoListSuccess();
    }

    /*< XD added for feed 20200215 begin */
    /**
     * @author xd.he
     */
    private void initFeedView() {
        vTopTitleXiding.setBackgroundResource(R.drawable.bg_gradient_clean_finish_tobar);
        tvTopXidingBack.setText(getResources().getString(R.string.xiding_back_to_xxx, mTitle));
        mNestedScrollView.setOnScrollChangeListener(this);
        feedViewPager.setOffscreenPageLimit(10);
        requestFeedHeight();
        feedViewPager.setNeedScroll(false);
        feedViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                clickCauseXiding(true);
                StatisticsUtils.trackClickNewsTab("content_cate_click", "“分类”点击", "selected_page", "information_page", i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        initMagicIndicator();
    }

    private void requestFeedHeight() {
        homeFeeds.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (NewsUtils.isShowCleanFinishFeed()) {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) homeFeeds.getLayoutParams();
                        mRootHeight = layoutRoot.getHeight();
                        params.height = mRootHeight - mFLTopNav.getHeight();  //  mStatusBarHeight
                        homeFeeds.setLayoutParams(params);
                        mNestedScrollView.scrollTo(mNestedScrollView.getScrollX(), 0);
                        mNestedScrollView.requestLayout();
                    } else {
                        homeFeeds.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initMagicIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setSkimOver(true);
        mNewsTypeNaviAdapter = new NewsTypeNavigatorAdapter(mNewTypes, true);
        mNewsTypeNaviAdapter.setOnClickListener(new NewsTypeNavigatorAdapter.OnClickListener() {
            @Override
            public void onClickTitleView(int index) {
                feedViewPager.setCurrentItem(index);
                feedViewPager.setClickTab(true);
            }
        });
        commonNavigator.setAdapter(mNewsTypeNaviAdapter);
        feedIndicator.setBackgroundColor(getResources().getColor(R.color.transparent));
        feedIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(feedIndicator, feedViewPager);
    }

    /* XD added for feed 20200215 End >*/

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
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
            }

        }
    }


    private String getConfigkey() {
        String configkey = "";
        if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
            //一键加速
            configkey = PositionId.KEY_JIASU;
        } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
            //1.2.1清理完成页面_建议清理
            configkey = PositionId.KEY_CLEAN_ALL;

        } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
            //超强省电
            configkey = PositionId.KEY_CQSD;
        } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {
            //微信专情
            configkey = PositionId.KEY_WECHAT;
        }  else if (getString(R.string.tool_qq_clear).contains(mTitle)) {
            //qq专情
            configkey = PositionId.KEY_QQ;
        }else if (getString(R.string.tool_notification_clean).contains(mTitle)) {
            //通知栏清理
            configkey = PositionId.KEY_NOTIFY;
        } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) {
            //手机降温
            configkey = PositionId.KEY_COOL;
        } else if (getString(R.string.game_quicken).contains(mTitle)) {
            //游戏加速
            configkey = PositionId.KEY_GAME;
        }
        return configkey;
    }


    private void changeUI(Intent intent) {
        if (intent != null) {
            String num = intent.getStringExtra("num");
            String unit = intent.getStringExtra("unit");
            mTvGb.setText(unit);
            if (TextUtils.isEmpty(mTitle)) {
                mTitle = getString(R.string.app_name);
            }
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
            } else { //建议清理
                PreferenceUtil.saveCleanFinishClickCount(PreferenceUtil.getCleanFinishClickCount() + 1);
            }
            finish();
        });
    }

    protected void loadData() {
        //页面创建事件埋点
        StatisticsUtils.customTrackEvent(createEventCode, createEventName, sourcePage, currentPage);
        String configKey=getConfigkey();
        if(!TextUtils.isEmpty(configKey)){
            loadAdUp(configKey);
            loadAdDown(configKey);
        }
//        startLoadData();  // XD delete 20200512
        loadFeedData();  // XD add for feed
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
        } else if (getString(R.string.game_quicken).contains(mTitle)) {
            //游戏加速
            NiuDataAPI.onPageStart("gameboost_success_page_view_page", "游戏加速结果页展示浏览");
        } else {
            NiuDataAPI.onPageStart("clean_up_page_view_immediately", "清理完成页浏览");
        }

        super.onResume();

        StatusBarCompat.setStatusBarColor(mContext, getResources().getColor(R.color.color_27D698), true);

    }

    @Override
    protected void onPause() {
        if (getString(R.string.app_name).contains(mTitle)) {
            //悟空清理
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
        //Umeng ---
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * @deprecated
     */
    public void startLoadData() {
        if (!NetworkUtils.isNetConnected()) {
            if (mRecyclerView != null) {
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
            jsonObject.put("pageSize", NewsUtils.NEWS_PAGE_SIZE);
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


    /**
     * 加载上广告
     * @param configKey
     */
    private void loadAdUp(String configKey){
        AdRequestParamentersBean adRequestParamentersBean = new AdRequestParamentersBean(configKey,
                PositionId.DRAW_ONE_CODE,
                this,
                AdType.Template,
                (int) ScreenUtils.getScreenWidthDp(this)-16,
                0);
        new AdPresenter().requestAd(adRequestParamentersBean, new AdShowCallBack() {
            @Override
            public void onAdShowCallBack(View view) {
                Log.d("ad_status", " scan onAdShowCallBack"+((view==null)?"null":"not null"));

                if(adUpView!=null && adUpContainer!=null){
                    adUpContainer.setVisibility(View.VISIBLE);
                    adUpView.removeAllViews();
                    adUpView.addView(view);
                }
            }

            @Override
            public void onCloseCallback() {
                if(adUpContainer!=null){
                    adUpContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String message) {
                if(adUpContainer!=null){
                    adUpContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 加载下广告
     * @param configKey
     */
    private void loadAdDown(String configKey){
        AdRequestParamentersBean adRequestParamentersBean = new AdRequestParamentersBean(configKey,
                PositionId.DRAW_TWO_CODE,
                this,
                AdType.Template,
                (int) ScreenUtils.getScreenWidthDp(this)-16,
                0);
        new AdPresenter().requestAd(adRequestParamentersBean, new AdShowCallBack() {
            @Override
            public void onAdShowCallBack(View view) {
                Log.d("ad_status", " scan onAdShowCallBack"+((view==null)?"null":"not null"));

                if(adDownView!=null && adDownContainer!=null){
                    adDownContainer.setVisibility(View.VISIBLE);

                    adDownView.removeAllViews();
                    adDownView.addView(view);
                }
            }

            @Override
            public void onCloseCallback() {
                if(adDownContainer!=null){
                    adDownContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String message) {
                if(adDownContainer!=null){
                    adDownContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showFeedView() {
        homeFeeds.setVisibility(View.VISIBLE);   // 显示信息流
        feedViewPager.setVisibility(View.VISIBLE);
    }

    protected void loadFeedData() {
        showFeedView();
        for (int i = 0; i < mNewTypes.length; i++) {
            NewsListFragment listFragment = NewsListFragment.getInstance(mNewTypes[i],PositionId.KEY_CLEAN_FINISH_NEWS);
            final int index = i;
            listFragment.setOnClickItemListener(new OnClickNewsItemListener() {
                @Override
                public void onClickItem(int position, NewsItemInfo itemInfo) {
                    clickCauseXiding(false);
                }
            });
            mNewsListFragments.add(listFragment);
        }
        mNewsListFragmentAdapter = new ComFragmentAdapter(getSupportFragmentManager(), mNewsListFragments);
        feedViewPager.setAdapter(mNewsListFragmentAdapter);
    }


    /**
     * click Cause Xiding
     */
    private void clickCauseXiding(boolean isAnimation) {
        feedViewPager.setNeedScroll(false);
        Rect rect = new Rect();
        homeFeeds.getGlobalVisibleRect(rect);
        int dy = rect.top - vHomeTop.getHeight() - mStatusBarHeight;
        if (dy != 0) {
            doXiDingStickyAnim(mNestedScrollView.getScrollY() + dy, isAnimation);
        }
    }

    @Override
    public void onScrollChange(NestedScrollView nestedScrollView, int x, int y, int lastx, int lasty) {
        if (NewsUtils.isShowCleanFinishFeed() && canXiding) {
            //处理吸顶操作
            cheekRootHeight();
            Rect rect = new Rect();
            homeFeeds.getGlobalVisibleRect(rect);
            int dy = rect.top - vHomeTop.getHeight() - mStatusBarHeight;  // flow top - titleTop Height  - statusBarHeight
            int changeY = y - lasty;
            if (dy> 0 && dy <= mStickyHeight && changeY > 0) {
                if (changeY < 20) {
                    doXiDingStickyAnim(y + dy, true, 300);
                } else {
                    doXiDingStickyAnim(y + dy, false);
                }
            }
        }
    }

    private void cheekRootHeight() {
        int rootHeight = layoutRoot.getHeight();
        if (mRootHeight != rootHeight) {
            requestFeedHeight();
        }
    }

    private void doXiDingStickyAnim(int scrolltoY, boolean isAnimation) {
        doXiDingStickyAnim(scrolltoY, isAnimation, 400);
    }

    private void doXiDingStickyAnim(int scrolltoY, boolean isAnimation, int duration) {
        mNestedScrollView.setNeedScroll(false);
        canXiding = false;
        if (isAnimation) {
            scrollAnima(mNestedScrollView.getScrollY(), scrolltoY, duration);
        } else {
            mNestedScrollView.scrollTo(mNestedScrollView.getScrollX(), scrolltoY);
            canXiding = true;
        }
        updateTitle(true);
    }

    @OnClick(R.id.fl_xiding_top_back)
    public void onClickGoBackToClean() {
        goBackToClean(true);
    }

    private void goBackToClean(boolean isAnimation) {
        canXiding = false;
        if (mNewsListFragmentAdapter != null) {
            mNewsListFragmentAdapter.resetScrollToTop();
        }
        mNestedScrollView.setNeedScroll(true);
        if (isAnimation) {
            scrollAnima(mNestedScrollView.getScrollY(), 0, 400);
        } else {
            mNestedScrollView.scrollTo(mNestedScrollView.getScrollX(), 0);
            canXiding = true;
        }
        updateTitle(false);
    }


    private void updateTitle(boolean xiding) {
        if (xiding) {
            vTopTitleNormal.setVisibility(View.GONE);  // lltop_narmal
            vTopTitleXiding.setVisibility(View.VISIBLE);
        } else {
            vTopTitleNormal.setVisibility(View.VISIBLE);
            vTopTitleXiding.setVisibility(View.GONE);
        }
    }

    private void scrollAnima(int start, int end, int duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获取动画过程中的渐变值
                int animatedValue = (int) animation.getAnimatedValue();
                if (mNestedScrollView != null) {
                    mNestedScrollView.scrollTo(0, animatedValue);
                }
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {
                canXiding = false;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                canXiding = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    private void startDetail() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    /* XD added for feed End >*/

}
