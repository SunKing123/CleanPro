package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.comm.jksdk.ad.listener.VideoAdListener;
import com.comm.jksdk.utils.DisplayUtil;
import com.google.gson.Gson;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.ApplicationDelegate;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.cleanking.ui.main.activity.AgentWebViewActivity;
import com.xiaoniu.cleanking.ui.main.activity.GameActivity;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.activity.NetWorkActivity;
import com.xiaoniu.cleanking.ui.main.activity.NewsActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneThinActivity;
import com.xiaoniu.cleanking.ui.main.activity.VirusKillActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendEntity;
import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendListEntity;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.InteractionSwitchList;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.VirusLlistEntity;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.main.event.LifecycEvent;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.newclean.presenter.NewCleanMainPresenter;
import com.xiaoniu.cleanking.ui.news.adapter.HomeRecommendAdapter;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.FromHomeCleanFinishEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.InternalStoragePremEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.ui.tool.notify.utils.NotifyUtils;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.ImageUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
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
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.VISIBLE;

/**
 * 1.2.1 新版本清理主页
 */
public class NewCleanMainFragment extends BaseFragment<NewCleanMainPresenter> implements HomeRecommendAdapter.onCheckListener {

    private long firstTime;
    @BindView(R.id.tv_clean_type)
    TextView mTvCleanType;
    @BindView(R.id.tv_clean_type01)
    TextView mTvCleanType01;

    @BindView(R.id.line_shd)
    LinearLayout lineShd;
    @BindView(R.id.text_wjgl)
    LinearLayout textWjgl;
    @BindView(R.id.view_phone_thin)
    View viewPhoneThin;
    @BindView(R.id.view_news)
    View viewNews;
    @BindView(R.id.v_game_clean)
    View viewGame;
    @BindView(R.id.tv_acc)
    TextView mAccTv;
    @BindView(R.id.tv_noti_clear)
    TextView mNotiClearTv;
    @BindView(R.id.tv_electricity)
    TextView mElectricityTv;
    @BindView(R.id.iv_acc)
    ImageView mAccIv;
    @BindView(R.id.iv_noti_clear)
    ImageView mNotiClearIv;
    @BindView(R.id.iv_electricity)
    ImageView mElectricityIv;
    @BindView(R.id.iv_acc_g)
    ImageView mAccFinishIv;
    @BindView(R.id.iv_noti_g)
    ImageView mNotiClearFinishIv;
    @BindView(R.id.iv_electricity_g)
    ImageView mElectricityFinishIv;
    @BindView(R.id.iv_interaction)
    ImageView mInteractionIv;
    @BindView(R.id.image_ad_bottom_first)
    ImageView mImageFirstAd;
    @BindView(R.id.image_ad_bottom_second)
    ImageView mImageSecondAd;
    @BindView(R.id.view_lottie_home)
    LottieAnimationView mLottieHomeView;
    @BindView(R.id.tv_now_clean)
    ImageView tvNowClean;
    @BindView(R.id.recycleview)
    RecyclerView mRecyclerView;
    @BindView(R.id.layout_scroll)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.v_no_net)
    View mNoNetView;
    @BindView(R.id.virus_tv)
    TextView mVirusTv;
    @BindView(R.id.virus_iv)
    ImageView mVirusIv;
    @BindView(R.id.v_top_view)
    View mTopContentView;
    @BindView(R.id.framelayout_top_ad)
    FrameLayout mTopAdFramelayout;
    @BindView(R.id.framelayout_center_ad)
    FrameLayout mCenterAdFramelayout;

    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //使用内存占总RAM的比例
    private int mInteractionPoistion; //互动式广告position
    private int mShowCount;

    private List<VirusLlistEntity> mVirusList;
    private HomeRecommendAdapter mRecommendAdapter;
    private List<InteractionSwitchList.DataBean.SwitchActiveLineDTOList> mInteractionList;
    private int mVirusPoistion;
    private AdManager mAdManager;
    private boolean mIsFristShowTopAd; //是否第一次展示头图广告
    private boolean isGameMain; //点击的是主功能的游戏加速还是推荐下的游戏加速
    private boolean mIsClickAdTopDetail; //顶部广告点击是否跳转详情还是下载
    private boolean mIsClickAdCenterDetail; //顶部广告点击是否跳转详情还是下载
    private boolean mIsTopAdExposed; //广告是否曝光
    private boolean mIsCenterAdExposed; //广告是否曝光

    private static final String TAG = "GeekSdk";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_clean_main;
    }

    @Override
    protected void initView() {
        tvNowClean.setVisibility(View.VISIBLE);
        EventBus.getDefault().register(this);
        showHomeLottieView();
        initRecyclerView();
        mPresenter.getRecommendList();
        mPresenter.requestBottomAd();
        mPresenter.getInteractionSwitch();
        if (Build.VERSION.SDK_INT < 26) {
            mPresenter.getAccessListBelow();
        }
        if (PreferenceUtil.isFirstForHomeIcon()) {
            PreferenceUtil.saveFirstForHomeIcon(false);
        } else {
            if (!PreferenceUtil.getCleanTime()) {
                mAccFinishIv.setVisibility(View.VISIBLE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_yjjs, mAccIv);
                mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(15, 30)) + "%");
            } else {
                mShowCount++;
                if (!PermissionUtils.isUsageAccessAllowed(getActivity())) {
                    mAccFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_yjjs_o, mAccIv);
                    mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                    mAccTv.setText(getString(R.string.tool_one_key_speed));
                } else {
                    mAccFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_quicken, mAccIv);
                    mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
                }
            }

            if (!NotifyUtils.isNotificationListenerEnabled()) {
                mShowCount++;
                mNotiClearFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq_o, mNotiClearIv);
                mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mNotiClearTv.setText(R.string.find_harass_notify);
            } else {
                if (!PreferenceUtil.getNotificationCleanTime()) {
                    mNotiClearFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    mNotiClearTv.setText(R.string.finished_clean_notify_hint);
                } else if (NotifyCleanManager.getInstance().getAllNotifications().size() > 0) {
                    mShowCount++;
                    mNotiClearFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_notify, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mNotiClearTv.setText(getString(R.string.find_harass_notify_num, NotifyCleanManager.getInstance().getAllNotifications().size() + ""));
                }
            }

            if (mShowCount < 2 && AndroidUtil.getElectricityNum(getActivity()) <= 70) {
                if (!PreferenceUtil.getPowerCleanTime()) {
                    mElectricityFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    if (TextUtils.isEmpty(PreferenceUtil.getLengthenAwaitTime())) {
                        mElectricityTv.setText(getString(R.string.lengthen_time, "40"));
                    } else {
                        mElectricityTv.setText(getString(R.string.lengthen_time, PreferenceUtil.getLengthenAwaitTime()));
                    }
                } else {
                    mShowCount++;
                    mElectricityFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power_gif, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mElectricityTv.setText(getString(R.string.power_consumption_num, NumberUtils.mathRandom(8, 15)));
                }
            }
        }
        //状态（0=隐藏，1=显示）
        String auditSwitch = SPUtil.getString(getActivity(), AppApplication.AuditSwitch, "1");
        if (TextUtils.equals(auditSwitch, "0")) {
            viewNews.setVisibility(View.GONE);
        } else {
            viewNews.setVisibility(VISIBLE);
        }
        initVirus();
        initGeekAdSdk();
//        initGeekSdkCenter();
//        if (null != getActivity()) {
//            ADUtilsKt.preloadingSplashAd(getActivity(), PositionId.AD_VIRUS, getString(R.string.virus_kill));
//        }
        //创建快捷图标。有待后续优化，暂时不打开
//        Intent shortcutInfoIntent = new Intent(getActivity(), SplashADActivity.class);
//        shortcutInfoIntent.setAction(Intent.ACTION_VIEW);
//        QuickUtils.getInstant(getActivity()).addShortcut( getString(R.string.app_quick_name), AppUtils.getAppIcon(getActivity(),getActivity().getPackageName()),shortcutInfoIntent);
    }


    /**
     * 广告sdk
     */
    private void initGeekAdSdk() {
        mAdManager = GeekAdSdk.getAdsManger();
    }

    /**
     * 病毒查杀、网络加速、游戏加速轮播
     */
    private void initVirus() {
        mVirusList = new ArrayList<>();
        mVirusList.add(new VirusLlistEntity(R.drawable.icon_virus, getString(R.string.virus_kill)));
        mVirusList.add(new VirusLlistEntity(R.drawable.icon_network, getString(R.string.network_quicken)));
        mVirusList.add(new VirusLlistEntity(R.drawable.icon_game_home, getString(R.string.game_quicken)));
    }

    private void initRecyclerView() {
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecommendAdapter = new HomeRecommendAdapter(getActivity());
        mRecommendAdapter.setmOnCheckListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mRecommendAdapter);
        mNestedScrollView.smoothScrollTo(0, 0);
        mRecyclerView.setFocusable(false);
    }

    /**
     * 显示广告 position = 0 第一个 position = 1  第二个
     *
     * @param dataBean
     */
    public void showFirstAd(ImageAdEntity.DataBean dataBean, int position) {
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.BANNER);
        if (position == 0) {
            mImageFirstAd.setVisibility(VISIBLE);
            ImageUtil.display(dataBean.getImageUrl(), mImageFirstAd);
            clickDownload(mImageFirstAd, dataBean.getDownloadUrl(), position);
//            mTextBottomTitle.setVisibility(GONE);
        } else if (position == 1) {
            mImageSecondAd.setVisibility(VISIBLE);
            ImageUtil.display(dataBean.getImageUrl(), mImageSecondAd);
            clickDownload(mImageSecondAd, dataBean.getDownloadUrl(), position);
//            mTextBottomTitle.setVisibility(GONE);
        }
        StatisticsUtils.trackClickAD("ad_show", "\"广告展示曝光", AppHolder.getInstance().getSourcePageId(), "home_page_clean_up_page", String.valueOf(position));
    }

    /**
     * 获取互动式广告成功
     *
     * @param switchInfoList
     */
    public void getInteractionSwitchSuccess(InteractionSwitchList switchInfoList) {
        if (null == switchInfoList || null == switchInfoList.getData() || switchInfoList.getData().size() <= 0)
            return;
        if (switchInfoList.getData().get(0).isOpen()) {
            mInteractionIv.setVisibility(VISIBLE);
            mInteractionList = switchInfoList.getData().get(0).getSwitchActiveLineDTOList();
            Glide.with(this).load(switchInfoList.getData().get(0).getSwitchActiveLineDTOList().get(0).getImgUrl()).into(mInteractionIv);
        } else {
            mInteractionIv.setVisibility(View.GONE);
        }
    }

    /**
     * 互动式广告
     */
    @OnClick(R.id.iv_interaction)
    public void interactionClick() {

        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        StatisticsUtils.trackClick("suspended_interactive_advertising_click", "悬浮互动式广告点击", "clod_splash_page", "home_page");
        if (null != mInteractionList && mInteractionList.size() > 0) {
            if (mInteractionPoistion > mInteractionList.size()-1) {
                mInteractionPoistion = 0;
            }

            if (mInteractionList.size() == 1) {
                startActivity(new Intent(getActivity(), AgentWebViewActivity.class)
                        .putExtra(ExtraConstant.WEB_URL, mInteractionList.get(0).getLinkUrl()));
            } else {
                if (mInteractionList.size() - 1 >= mInteractionPoistion) {
                    startActivity(new Intent(getActivity(), AgentWebViewActivity.class)
                            .putExtra(ExtraConstant.WEB_URL, mInteractionList.get(mInteractionPoistion).getLinkUrl()));
                }

            }
            mInteractionPoistion++;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("home_page_view_page", "首页浏览");
        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPowerSize = new FileQueryUtils().getRunningProcess().size();

        if (null != mInteractionList && mInteractionList.size() > 0) {
            if (mInteractionPoistion > mInteractionList.size()-1) {
                mInteractionPoistion = 0;
            }
            if (mInteractionList.size() == 1) {
                GlideUtils.loadGif(getActivity(), mInteractionList.get(0).getImgUrl(), mInteractionIv, 10000);
            } else {
                if (mInteractionList.size() - 1 >= mInteractionPoistion) {
                    GlideUtils.loadGif(getActivity(), mInteractionList.get(mInteractionPoistion).getImgUrl(), mInteractionIv, 10000);
                }

            }
        }
        lineShd.setEnabled(true);
        textWjgl.setEnabled(true);
        viewPhoneThin.setEnabled(true);
        viewNews.setEnabled(true);
        viewGame.setEnabled(true);

        if (mIsClickAdTopDetail) {
            initGeekSdkTop();
        }
        if (mIsClickAdCenterDetail) {
            initGeekSdkCenter();
        }
    }

    /**
     * 顶部广告 样式---大图嵌套图片_01_跑马灯
     */
    private void initGeekSdkTop() {
        boolean isOpen = false;
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_HOME_AD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isOpen = switchInfoList.isOpen();
                }
            }
        }
        if (!isOpen) return;
        if (!mIsFristShowTopAd) {
            StatisticsUtils.customTrackEvent("ad_vue_custom", "首页头图广告vue创建", "home_page", "home_page");
            mIsFristShowTopAd = true;
        }//
        if (null == getActivity() || null == mTopAdFramelayout) return;
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "home_page", "home_page");
        AdManager adManager = GeekAdSdk.getAdsManger();
        adManager.loadNativeTemplateAd(getActivity(), PositionId.AD_HOME_TOP_MB
                , Float.valueOf(DisplayUtil.px2dp(getActivity(), DisplayUtil.getScreenWidth(getActivity())) - 28)
                , new AdListener() {
                    @Override
                    public void adSuccess(AdInfo info) {
                        if (null != info) {
                            Log.d(TAG, "adSuccess---home--top =" + info.toString());
                            StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "home_page", "home_page");
                            if (null != mTopAdFramelayout && null != info.getAdView()) {
                                mTopContentView.setVisibility(View.GONE);
                                mTopAdFramelayout.setVisibility(VISIBLE);
                                mTopAdFramelayout.removeAllViews();
                                mTopAdFramelayout.addView(info.getAdView());
                            }
                        }
                    }

                    @Override
                    public void adExposed(AdInfo info) {
                        if (null == info) return;
                        Log.d(TAG, "adExposed---home--top");
                        mIsTopAdExposed = true;
                        StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), "home_page", "home_page", info.getAdTitle());
                    }

                    @Override
                    public void adClicked(AdInfo info) {
                        Log.d(TAG, "adClicked---home--top");
                        if (null == info) return;
                        if (mIsTopAdExposed) {
                            StatisticsUtils.clickAD("ad_click", "病毒查杀激励视频结束页下载点击", "1", info.getAdId(), info.getAdSource(), "home_page", "virus_killing_video_end_page", info.getAdTitle());
                        } else {
                            StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), "home_page", "home_page", info.getAdTitle());
                        }
                        if (info.getAdClickType() == 2) { //2=详情
                            mIsClickAdTopDetail = true;
                        } else {
                            mIsClickAdTopDetail = false;
                        }
                    }

                    @Override
                    public void adClose(AdInfo info) {
                        if (null == info) return;
                        StatisticsUtils.clickAD("close_click", "病毒查杀激励视频结束页关闭点击", "1", info.getAdId(), info.getAdSource(), "home_page", "virus_killing_video_end_page", info.getAdTitle());
                    }

                    @Override
                    public void adError(AdInfo info, int errorCode, String errorMsg) {
                        if (null == info) return;
                        Log.d(TAG, "adError---home--top=" + info.toString());
                        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "fail", "home_page", "home_page");
                    }
                });
    }

    /**
     * 更多推荐上方广告 样式---大图_下载播放按钮_跑马灯
     */
    private void initGeekSdkCenter() {
        boolean isOpen = false;
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_HOME_AD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isOpen = switchInfoList.isOpen();
                }
            }
        }
        if (!isOpen) return;
        if (null == getActivity() || null == mCenterAdFramelayout) return;
        StatisticsUtils.customADRequest("ad_request", "广告请求", "2", " ", " ", "all_ad_request", "home_page", "home_page");
        AdManager adManager = GeekAdSdk.getAdsManger();
        adManager.loadNativeTemplateAd(getActivity(), PositionId.AD_HOME_BOTTOM_MB
                , Float.valueOf(DisplayUtil.px2dp(getActivity(), DisplayUtil.getScreenWidth(getActivity())) - 28)
                , new AdListener() {
                    @Override
                    public void adSuccess(AdInfo info) {
                        if (null != info) {
                            Log.d(TAG, "adSuccess--home--center =" + info.getAdSource());
                            StatisticsUtils.customADRequest("ad_request", "广告请求", "2", info.getAdId(), info.getAdSource(), "success", "home_page", "home_page");
                            if (null != mCenterAdFramelayout && null != info.getAdView()) {
                                mCenterAdFramelayout.setVisibility(VISIBLE);
                                mCenterAdFramelayout.removeAllViews();
                                mCenterAdFramelayout.addView(info.getAdView());
                            }
                        }
                    }

                    @Override
                    public void adExposed(AdInfo info) {
                        if (null == info) return;
                        Log.d(TAG, "adExposed--home--center");
                        mIsCenterAdExposed = true;
                        StatisticsUtils.customAD("ad_show", "广告展示曝光", "2", info.getAdId(), info.getAdSource(), "home_page", "home_page", " ");
                    }

                    @Override
                    public void adClicked(AdInfo info) {
                        Log.d(TAG, "adClicked--home--center");
                        if (null == info) return;
                        if (mIsCenterAdExposed) {
                            StatisticsUtils.clickAD("ad_click", "网络加速激励视频结束页下载点击", "2", info.getAdId(), info.getAdSource(), "home_page", "network_acceleration_video_end_page", info.getAdTitle());
                        } else {
                            StatisticsUtils.clickAD("ad_click", "广告点击", "2", info.getAdId(), info.getAdSource(), "home_page", "home_page", info.getAdTitle());
                        }
                        if (info.getAdClickType() == 2) { //2=详情
                            mIsClickAdCenterDetail = true;
                        } else {
                            mIsClickAdCenterDetail = false;
                        }
                    }

                    @Override
                    public void adClose(AdInfo info) {
                        LogUtils.e("AdInfo:"+new Gson().toJson(info));
                        if (null == info) return;
                        mCenterAdFramelayout.setVisibility(View.GONE);
                        StatisticsUtils.clickAD("close_click", "网络加速激励视频结束页关闭点击", "1", info.getAdId(), info.getAdSource(), "home_page", "network_acceleration_video_end_page", info.getAdTitle());
                    }

                    @Override
                    public void adError(AdInfo info, int errorCode, String errorMsg) {
                        if (null == info) return;
                        Log.d(TAG, "adError--home--center =" + errorCode + "---" + errorMsg + info.toString());
                        StatisticsUtils.customADRequest("ad_request", "广告请求", "2", info.getAdId(), info.getAdSource(), "fail", "home_page", "home_page");
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("home_page_view_page", "首页浏览");
    }

    /**
     * 清理完成回调
     *
     * @param event
     */
    @Subscribe
    public void fromHomeCleanFinishEvent(FromHomeCleanFinishEvent event) {
        if (null == event || TextUtils.isEmpty(event.getTitle())) return;
        mShowCount = 0;
        if (getString(R.string.tool_one_key_speed).contains(event.getTitle())) { //一键加速
//            mShowCount--;
            mAccFinishIv.setVisibility(View.VISIBLE);
            GlideUtils.loadDrawble(getActivity(), R.drawable.icon_yjjs, mAccIv);
            mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
            mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(15, 30)) + "%");

            //通知栏清理
            if (!NotifyUtils.isNotificationListenerEnabled()) {
                mShowCount++;
                mNotiClearFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq_o, mNotiClearIv);
                mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mNotiClearTv.setText(R.string.find_harass_notify);
            } else {
                if (!PreferenceUtil.isCleanNotifyUsed() && NotifyCleanManager.getInstance().getAllNotifications().size() > 0) {
                    mShowCount++;
                    mNotiClearFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_notify, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mNotiClearTv.setText(getString(R.string.find_harass_notify_num, NotifyCleanManager.getInstance().getAllNotifications().size() + ""));
                } else if (!PreferenceUtil.isCleanNotifyUsed() && NotifyCleanManager.getInstance().getAllNotifications().size() <= 0) {
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    mNotiClearTv.setText(R.string.tool_notification_clean);
                } else if (NotifyCleanManager.getInstance().getAllNotifications().size() <= 0) {
//                    mShowCount--;
                    mNotiClearFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    mNotiClearTv.setText(R.string.finished_clean_notify_hint);
                }
            }

            //超强省电
            if (AndroidUtil.getElectricityNum(getActivity()) <= 70) {
                if (!PreferenceUtil.isCleanPowerUsed() && PreferenceUtil.getPowerCleanTime()) {
                    mShowCount++;
                    mElectricityFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power_gif, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mElectricityTv.setText(getString(R.string.power_consumption_num, NumberUtils.mathRandom(8, 15)));
                } else {
//                    mShowCount--;
                    mElectricityFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    if (TextUtils.isEmpty(PreferenceUtil.getLengthenAwaitTime())) {
                        mElectricityTv.setText(getString(R.string.lengthen_time, "40"));
                    } else {
                        mElectricityTv.setText(getString(R.string.lengthen_time, PreferenceUtil.getLengthenAwaitTime()));
                    }
                }
            }
        } else if (getString(R.string.tool_notification_clean).contains(event.getTitle())) {//通知栏清理
//            mShowCount--;
            mNotiClearFinishIv.setVisibility(View.VISIBLE);
            GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
            mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
            mNotiClearTv.setText(R.string.finished_clean_notify_hint);

            //一键加速
            if (!PermissionUtils.isUsageAccessAllowed(getActivity())) {
                mShowCount++;
                mAccFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_yjjs_o, mAccIv);
                mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mAccTv.setText(getString(R.string.tool_one_key_speed));
            } else if (!PreferenceUtil.isCleanJiaSuUsed() && PreferenceUtil.getCleanTime()) {
                mShowCount++;
                mAccFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_quicken, mAccIv);
                mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
            }

            //超强省电
            if (!PermissionUtils.isUsageAccessAllowed(getActivity())) {
                mShowCount++;
                mElectricityFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power_o, mElectricityIv);
                mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mElectricityTv.setText(getString(R.string.tool_super_power_saving));
            } else if (AndroidUtil.getElectricityNum(getActivity()) <= 70) {
                if (!PreferenceUtil.isCleanPowerUsed() && PreferenceUtil.getPowerCleanTime()) {
                    mShowCount++;
                    mElectricityFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power_gif, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mElectricityTv.setText(getString(R.string.power_consumption_num, NumberUtils.mathRandom(8, 15)));
                } else {
//                    mShowCount--;
                    mElectricityFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power, mElectricityIv);
                    mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    if (TextUtils.isEmpty(PreferenceUtil.getLengthenAwaitTime())) {
                        mElectricityTv.setText(getString(R.string.lengthen_time, "40"));
                    } else {
                        mElectricityTv.setText(getString(R.string.lengthen_time, PreferenceUtil.getLengthenAwaitTime()));
                    }
                }
            }

        } else if (getString(R.string.tool_super_power_saving).contains(event.getTitle())) { //超强省电
//            mShowCount--;
            mElectricityFinishIv.setVisibility(View.VISIBLE);
            GlideUtils.loadDrawble(getActivity(), R.drawable.icon_power, mElectricityIv);
            mElectricityTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
            if (TextUtils.isEmpty(PreferenceUtil.getLengthenAwaitTime())) {
                mElectricityTv.setText(getString(R.string.lengthen_time, "40"));
            } else {
                mElectricityTv.setText(getString(R.string.lengthen_time, PreferenceUtil.getLengthenAwaitTime()));
            }
            //一键加速
            if (!PermissionUtils.isUsageAccessAllowed(getActivity())) {
                mShowCount++;
                mAccFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_yjjs_o, mAccIv);
                mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mAccTv.setText(getString(R.string.tool_one_key_speed));
            } else if (!PreferenceUtil.isCleanJiaSuUsed() && PreferenceUtil.getCleanTime()) {
                mShowCount++;
                mAccFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_quicken, mAccIv);
                mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
            }

            //通知栏清理
            if (!NotifyUtils.isNotificationListenerEnabled()) {
                mShowCount++;
                mNotiClearFinishIv.setVisibility(View.GONE);
                GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq_o, mNotiClearIv);
                mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
                mNotiClearTv.setText(R.string.find_harass_notify);
            } else {
                if (!PreferenceUtil.isCleanNotifyUsed() && NotifyCleanManager.getInstance().getAllNotifications().size() > 0) {
                    mShowCount++;
                    mNotiClearFinishIv.setVisibility(View.GONE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_notify, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
                    mNotiClearTv.setText(getString(R.string.find_harass_notify_num, NotifyCleanManager.getInstance().getAllNotifications().size() + ""));
                } else if (!PreferenceUtil.isCleanNotifyUsed() && NotifyCleanManager.getInstance().getAllNotifications().size() <= 0) {
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    mNotiClearTv.setText(R.string.tool_notification_clean);
                } else if (NotifyCleanManager.getInstance().getAllNotifications().size() <= 0) {
//                    mShowCount--;
                    mNotiClearFinishIv.setVisibility(View.VISIBLE);
                    GlideUtils.loadDrawble(getActivity(), R.drawable.icon_home_qq, mNotiClearIv);
                    mNotiClearTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
                    mNotiClearTv.setText(R.string.finished_clean_notify_hint);
                }
            }
        }
        if (mShowCount <= 0) {
            mTvCleanType01.setText(getString(R.string.recommend_count_hint_all));
        } else {
            mTvCleanType01.setText(getString(R.string.recommend_count_hint, String.valueOf(mShowCount)));
        }

        if (getString(R.string.virus_kill).contains(event.getTitle()) || getString(R.string.network_quicken).contains(event.getTitle())) {
            forThreeTab();
        }
        if (getString(R.string.game_quicken).contains(event.getTitle()) && isGameMain) {
            forThreeTab();
        }
        initGeekSdkTop();
    }

    /**
     * 病毒查杀、网络加速、游戏加速轮播
     */
    private void forThreeTab() {
        mVirusPoistion++;
        if (null != mVirusList && mVirusList.size() > 0) {
            if (mVirusPoistion > 2) {
                mVirusPoistion = 0;
            }
            if (mVirusList.size() - 1 >= mVirusPoistion) {
                mVirusTv.setText(mVirusList.get(mVirusPoistion).getName());
                mVirusIv.setImageResource(mVirusList.get(mVirusPoistion).getIcon());
            }
        }
    }

    /**
     * 一键加速获取权限后通知首页一键加速状态改变
     */
    @Subscribe
    public void internalStoragePremEvent(InternalStoragePremEvent event) {
        if (!PreferenceUtil.isCleanJiaSuUsed()) {
            mAccFinishIv.setVisibility(View.GONE);
            GlideUtils.loadDrawble(getActivity(), R.drawable.icon_quicken, mAccIv);
            mAccTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
            mAccTv.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
        }
    }

    /**
     * 热启动回调
     *
     * @param lifecycEvent
     */
    @Subscribe
    public void changeLifecyEvent(LifecycEvent lifecycEvent) {
        if (null == mTopAdFramelayout || null == mLottieHomeView) return;
        if (lifecycEvent.isActivity()) {
            mTopContentView.setVisibility(VISIBLE);
            mTopAdFramelayout.removeAllViews();
            mTopAdFramelayout.setVisibility(View.GONE);
            tvNowClean.setVisibility(VISIBLE);
            mTvCleanType.setVisibility(VISIBLE);
            mTvCleanType01.setVisibility(View.GONE);
            showTextView();
//            mLottieHomeView.useHardwareAcceleration(true);
            mLottieHomeView.setAnimation("clean_home_top.json");
            mLottieHomeView.setImageAssetsFolder("images_home");
            mLottieHomeView.playAnimation();
            mLottieHomeView.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    /**
     * 立即清理
     */
    @OnClick(R.id.tv_now_clean)
    public void nowClean() {
        StatisticsUtils.trackClick("home_page_clean_click", "用户在首页点击【立即清理】", "home_page", "home_page");
        //PreferenceUtil.getNowCleanTime() || TextUtils.isEmpty(Constant.APP_IS_LIVE
        ((MainActivity) getActivity()).commitJpushClickTime(1);
        if (true) {
            startActivity(NowCleanActivity.class);
        } else {
            AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
            boolean isOpen = false;
            //solve umeng error --> SwitchInfoList.getData()' on a null object reference
            if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                    && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                        isOpen = switchInfoList.isOpen();
                    }
                }
            }
            EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
            if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_suggest_clean), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_suggest_clean));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_suggest_clean));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                bundle.putString("home", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }

    /**
     * 病毒查杀、网络加速、游戏加速轮播
     */
    @OnClick(R.id.text_wjgl)
    public void wjgl() {
        textWjgl.setEnabled(false);
//        ADUtilsKt.preloadingSplashAd(getActivity(), PositionId.AD_FINISH_BEFOR);
        if (null != mVirusList && mVirusList.size() > 0) {
            switch (mVirusPoistion) {
                case 0:
                    StatisticsUtils.trackClick("virus_killing_click", "用户在首页点击【病毒查杀】按钮", "home_page", "home_page");
                    if (null == AppHolder.getInstance() || null == AppHolder.getInstance().getSwitchInfoList()
                            || null == AppHolder.getInstance().getSwitchInfoList().getData()
                            || AppHolder.getInstance().getSwitchInfoList().getData().size() <= 0) {
                        startActivity(VirusKillActivity.class);
                    } else {
                        for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                            if (PositionId.KEY_VIRUS_JILI.equals(switchInfoList.getConfigKey())) {
                                if (switchInfoList.isOpen()) {
                                    loadGeekAd();
                                } else {
                                    startActivity(VirusKillActivity.class);
                                }
                            }
                        }
                    }
                    break;
                case 1:
                    StatisticsUtils.trackClick("network_acceleration_click", "用户在首页点击【网络加速】按钮", "home_page", "home_page");
                    if (null == AppHolder.getInstance() || null == AppHolder.getInstance().getSwitchInfoList()
                            || null == AppHolder.getInstance().getSwitchInfoList().getData()
                            || AppHolder.getInstance().getSwitchInfoList().getData().size() <= 0) {
                        startActivity(NetWorkActivity.class);
                    } else {
                        for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                            if (PositionId.KEY_NET_JILI.equals(switchInfoList.getConfigKey())) {
                                if (switchInfoList.isOpen()) {
                                    loadGeekAdNet();
                                } else {
                                    startActivity(NetWorkActivity.class);
                                }
                            }
                        }
                    }
                    break;
                case 2:
                    isGameMain = true;
                    StatisticsUtils.trackClick("main_function_area_gameboost_click", "用户在首页主功能区点击【游戏加速】按钮", "home_page", "home_page");
                    if (PreferenceUtil.getGameTime()) {
                        getActivity().startActivity(new Intent(getActivity(), GameActivity.class)
                                .putExtra("main", true));
                    } else {
                        goFinishActivity();
                    }
                    break;
            }
        }
    }

    /**
     * 一键加速
     */
    @OnClick(R.id.text_acce)
    public void text_acce() {
//        ADUtilsKt.preloadingSplashAd(getActivity(), PositionId.AD_FINISH_BEFOR);
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.ONKEY);
        ((MainActivity) getActivity()).commitJpushClickTime(2);
        StatisticsUtils.trackClick("boost_click", "用户在首页点击【一键加速】按钮", "home_page", "home_page");
        //保存本次清理完成时间 保证每次清理时间间隔为3分钟
        if (!PreferenceUtil.getCleanTime()) {
            boolean isOpen = false;
            boolean mIsOpenThree = false;
            if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                    && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.KEY_FINISH_SWITCH.equals(switchInfoList.getConfigKey())) {
                        mIsOpenThree = switchInfoList.isOpen();
                    }
                    if (PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                        isOpen = switchInfoList.isOpen();
                    }

                }
            }
            EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
            if (mIsOpenThree) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_one_key_speed));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_one_key_speed), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_one_key_speed));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_one_key_speed));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_one_key_speed));
            startActivity(PhoneAccessActivity.class, bundle);
        }
    }

    /**
     * 超强省电
     */
    @OnClick(R.id.line_shd)
    public void line_shd() {
        lineShd.setEnabled(false);
//        ADUtilsKt.preloadingSplashAd(getActivity(), PositionId.AD_FINISH_BEFOR);
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        ((MainActivity) getActivity()).commitJpushClickTime(9);
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.SUPER_POWER_SAVING);
        StatisticsUtils.trackClick("powersave_click", "用户在首页点击【超强省电】按钮", "home_page", "home_page");
        if (PreferenceUtil.getPowerCleanTime()) {
            startActivity(PhoneSuperPowerActivity.class);
        } else {
            boolean isOpen = false;
            boolean mIsOpenThree = false;
            //solve umeng error --> SwitchInfoList.getData()' on a null object reference
            if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                    && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.KEY_FINISH_SWITCH.equals(switchInfoList.getConfigKey())) {
                        mIsOpenThree = switchInfoList.isOpen();
                    }
                    if (PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                        isOpen = switchInfoList.isOpen();
                    }
                }
            }

            if (mIsOpenThree) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_super_power_saving));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_super_power_saving), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_super_power_saving));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_super_power_saving));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }

    }

    /**
     * 新闻点击
     */
    @OnClick(R.id.view_news)
    public void ViewNewsClick() {
        viewNews.setEnabled(false);
        StatisticsUtils.trackClick("news_click", "用户在首页点击【头条新闻热点】按钮", "home_page", "home_page");
        startActivity(NewsActivity.class);
    }

    /**
     * 软件管理
     */
    @OnClick(R.id.view_phone_thin)
    public void ViewPhoneThinClick() {
        viewPhoneThin.setEnabled(false);
        Intent intent = new Intent(getActivity(), PhoneThinActivity.class);
        intent.putExtra(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_soft_manager));
        startActivity(intent);
        StatisticsUtils.trackClick("app_manage_click", "用户在首页点击【软件管理】按钮", "home_page", "home_page");
    }

    /**
     * 游戏加速
     */
    @OnClick(R.id.v_game_clean)
    public void ViewThinClick() {
        isGameMain = false;
        viewGame.setEnabled(false);
//        ADUtilsKt.preloadingSplashAd(getActivity(), PositionId.AD_FINISH_BEFOR);
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        Intent intent = new Intent(getActivity(), GameActivity.class);
        intent.putExtra("main", false);
        intent.putExtra(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.game_quicken));
        startActivity(intent);
        StatisticsUtils.trackClick("gameboost_click", "游戏加速点击", "home_page", "home_page");
    }

    /*    *//**
     * 权限设置
     *//*
    @OnClick(R.id.iv_permission)
    public void onClick() {
        startActivity(new Intent(getContext(), PermissionActivity.class));
        StatisticsUtils.trackClick("Triangular_yellow_mark_click", "三角黄标", AppHolder.getInstance().getSourcePageId(), "permission_page");
    }*/

    /**
     * 微信专清
     */
    @OnClick(R.id.line_wx)
    public void mClickWx() {
//        ADUtilsKt.preloadingSplashAd(getActivity(), PositionId.AD_FINISH_BEFOR);
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.WETCHAT_CLEAN);
        ((MainActivity) getActivity()).commitJpushClickTime(5);
        StatisticsUtils.trackClick("wxclean_click", "用户在首页点击【微信专清】按钮", "home_page", "home_page");
        if (!AndroidUtil.isAppInstalled(SpCacheConfig.CHAT_PACKAGE)) {
            ToastUtils.showShort(R.string.tool_no_install_chat);
            return;
        }
        if (PreferenceUtil.getWeChatCleanTime()) {
            // 每次清理间隔 至少3秒
            startActivity(WechatCleanHomeActivity.class);
        } else {
            boolean isOpen = false;
            boolean mIsOpenThree = false;
            //solve umeng error --> SwitchInfoList.getData()' on a null object reference
            if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                    && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.KEY_FINISH_SWITCH.equals(switchInfoList.getConfigKey())) {
                        mIsOpenThree = switchInfoList.isOpen();
                    }
                    if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                        isOpen = switchInfoList.isOpen();
                    }
                }
            }
            if (mIsOpenThree) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_chat_clear));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_chat_clear), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_chat_clear));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_chat_clear));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }

    /**
     * 通知栏清理
     */
    @OnClick(R.id.line_super_power_saving)
    public void mClickQq() {
//        ADUtilsKt.preloadingSplashAd(getActivity(), PositionId.AD_FINISH_BEFOR);
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        ((MainActivity) getActivity()).commitJpushClickTime(8);
        StatisticsUtils.trackClick("notification_clean_click", "用户在首页点击【通知清理】按钮", AppHolder.getInstance().getSourcePageId(), "home_page");
        if (!NotifyUtils.isNotificationListenerEnabled() || PreferenceUtil.getNotificationCleanTime() || mNotifySize > 0) {
            NotifyCleanManager.startNotificationCleanActivity(getActivity(), 0);
        } else {
            boolean isOpen = false;
            boolean mIsOpenThree = false;
            if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                    && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.KEY_FINISH_SWITCH.equals(switchInfoList.getConfigKey())) {
                        mIsOpenThree = switchInfoList.isOpen();
                    }
                    if (PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                        isOpen = switchInfoList.isOpen();
                    }
                }
            }

            if (mIsOpenThree) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_notification_clean));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_notification_clean), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_notification_clean));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_notification_clean));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }

    /**
     * 手机降温
     */
    @OnClick(R.id.line_jw)
    public void mClickJw() {
//        ADUtilsKt.preloadingSplashAd(getActivity(), PositionId.AD_FINISH_BEFOR);
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        ((MainActivity) getActivity()).commitJpushClickTime(6);
        StatisticsUtils.trackClick("cooling_click", "用户在首页点击【手机降温】按钮", AppHolder.getInstance().getSourcePageId(), "home_page");

        if (PreferenceUtil.getCoolingCleanTime()) {
            startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
        } else {
            boolean isOpen = false;
            boolean mIsOpenThree = false;
            //solve umeng error --> SwitchInfoList.getData()' on a null object reference
            if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                    && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.KEY_FINISH_SWITCH.equals(switchInfoList.getConfigKey())) {
                        mIsOpenThree = switchInfoList.isOpen();
                    }
                    if (PositionId.KEY_COOL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                        isOpen = switchInfoList.isOpen();
                    }
                }
            }
            if (mIsOpenThree) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_phone_temperature_low));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.tool_phone_temperature_low), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_phone_temperature_low));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_phone_temperature_low));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
        }
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null || listInfo.size() <= 0) return;
        mRamScale = new FileQueryUtils().computeTotalSize(listInfo);
    }

    public void onKeyBack() {
//        long currentTimeMillis = System.currentTimeMillis();
//        if (currentTimeMillis - firstTime > 1500) {
//            Toast.makeText(getActivity(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//            firstTime = currentTimeMillis;
//        } else {
//            SPUtil.setInt(getContext(), "turnask", 0);
//            AppManager.getAppManager().AppExit(getContext(), false);
//        }
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            NiuDataAPI.onPageStart("home_page_view_page", "首页浏览");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_28d1a6), true);
            } else {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_28d1a6), false);
            }
            initGeekAdSdk();
            initGeekSdkCenter();
        } else {
            NiuDataAPI.onPageEnd("home_page_view_page", "首页浏览");
        }
    }

    /**
     * EventBus 立即清理完成后，更新首页显示文案
     */
    @Subscribe
    public void onEventClean(CleanEvent cleanEvent) {
        if (cleanEvent != null) {
            if (cleanEvent.isCleanAminOver()) {
                showTextView01();
                tvNowClean.setVisibility(View.GONE);
//                mLottieHomeView.useHardwareAcceleration(true);
                mLottieHomeView.setAnimation("clean_home_top2.json");
                mLottieHomeView.setImageAssetsFolder("images_home_finish");
                mLottieHomeView.playAnimation();
                mLottieHomeView.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLottieHomeView.playAnimation();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        }
    }

    /**
     * 点击下载app
     *
     * @param view
     * @param downloadUrl
     */
    public void clickDownload(View view, String downloadUrl, int position) {
        view.setOnClickListener(v -> {
            //广告埋点
            StatisticsUtils.trackClickAD("ad_click", "\"广告点击", AppHolder.getInstance().getSourcePageId(), "home_page_clean_up_page", String.valueOf(position));
           /* Bundle bundle = new Bundle();
            bundle.putString(Constant.URL, downloadUrl);
            bundle.putBoolean(Constant.NoTitle, false);
            startActivity(UserLoadH5Activity.class, bundle);*/
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(downloadUrl);
            intent.setData(content_url);
            startActivity(intent);
        });
    }

    /**
     * 静止时动画
     */
    private void showHomeLottieView() {
        showTextView();
//        mLottieHomeView.useHardwareAcceleration(true);
        mLottieHomeView.setAnimation("clean_home_top.json");
        mLottieHomeView.setImageAssetsFolder("images_home");
        mLottieHomeView.playAnimation();
        mLottieHomeView.setVisibility(VISIBLE);
        mLottieHomeView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public void showTextView() {
        String hintText = getString(R.string.tool_home_hint);
        SpannableString msp = new SpannableString(hintText);
//        msp.setSpan(new AbsoluteSizeSpan(ScreenUtils.dpToPx(mContext, 18)), hintText.indexOf("存在大量垃圾"), hintText.indexOf("，"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), hintText.indexOf("存在大量垃圾"), hintText.indexOf("，"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mTvCleanType != null && msp != null) {
                    mTvCleanType.setText(msp);
                    mTvCleanType.animate()
                            .alpha(1f)
                            .setDuration(500)
                            .setListener(null);
                }
            }
        }, 1000);
    }

    public void showTextView01() {
        String showText = getString(R.string.tool_phone_already_clean);
        String showText01 = "";
        if (mShowCount <= 0) {
            showText01 = getString(R.string.recommend_count_hint_all);
        } else {
            showText01 = getString(R.string.recommend_count_hint, String.valueOf(mShowCount));
        }
        SpannableString msp = new SpannableString(showText);
        SpannableString msp01 = new SpannableString(showText01);
        msp01.setSpan(new AbsoluteSizeSpan(ScreenUtils.dpToPx(mContext, 17)), 0, showText01.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp01.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, showText01.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvCleanType.setText(msp);
        mTvCleanType.setVisibility(VISIBLE);
        mTvCleanType01.setText(msp01);
        mTvCleanType01.setVisibility(VISIBLE);
    }

    /**
     * 获取推荐列表成功
     *
     * @param entity
     */
    public void getRecommendListSuccess(HomeRecommendEntity entity) {
        if (null == mRecommendAdapter || null == entity || null == entity.getData() || entity.getData().size() <= 0)
            return;
        PreferenceUtil.saveFirstHomeRecommend(false);
        mRecyclerView.setVisibility(VISIBLE);
        mNoNetView.setVisibility(View.GONE);
        mRecommendAdapter.setData(entity.getData());
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.d("XiLei", "subscribe:" + Thread.currentThread().getName());
                if (null == ApplicationDelegate.getAppDatabase() || null == ApplicationDelegate.getAppDatabase().homeRecommendDao())
                    return;
                ApplicationDelegate.getAppDatabase().homeRecommendDao().deleteAll();
                ApplicationDelegate.getAppDatabase().homeRecommendDao().insertAll(entity.getData());
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    /**
     * 获取推荐列表失败 取本地数据
     */
    public void getRecommendListFail() {
        if (PreferenceUtil.isFirstHomeRecommend()) {
            mRecyclerView.setVisibility(View.GONE);
            mNoNetView.setVisibility(VISIBLE);
            return;
        }
        if (null == ApplicationDelegate.getAppDatabase() || null == ApplicationDelegate.getAppDatabase().homeRecommendDao()
                || null == ApplicationDelegate.getAppDatabase().homeRecommendDao().getAll() || ApplicationDelegate.getAppDatabase().homeRecommendDao().getAll().size() <= 0)
            return;
        Observable<List<HomeRecommendListEntity>> observable = Observable.create(new ObservableOnSubscribe<List<HomeRecommendListEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<HomeRecommendListEntity>> emitter) throws Exception {
                Log.d("XiLei", "subscribe2222:" + Thread.currentThread().getName());
                emitter.onNext(ApplicationDelegate.getAppDatabase().homeRecommendDao().getAll());
            }
        });
        Consumer<List<HomeRecommendListEntity>> consumer = new Consumer<List<HomeRecommendListEntity>>() {
            @Override
            public void accept(List<HomeRecommendListEntity> list) throws Exception {
                Log.d("XiLei", "accept:" + list.size() + ":" + Thread.currentThread().getName());
                if (null == mRecommendAdapter) return;
                mRecommendAdapter.setData(list);
            }
        };
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    @Override
    public void onCheck(List<HomeRecommendListEntity> list, int pos) {
        if (null == getActivity() || null == list || list.size() <= 0) return;
        if (list.get(pos).getLinkType().equals("1")) {
//            ADUtilsKt.preloadingSplashAd(getActivity(), PositionId.AD_FINISH_BEFOR);
            if (list.get(pos).getName().equals(getString(R.string.game_quicken))) { //游戏加速
                StatisticsUtils.trackClick("gameboost_click", "游戏加速点击", "home_page", "home_page");
                AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
                if (PreferenceUtil.getGameTime()) {
                    SchemeProxy.openScheme(getActivity(), list.get(pos).getLinkUrl());
                } else {
                    goFinishActivity();
                }
                return;
            } else if (list.get(pos).getName().equals(getString(R.string.tool_one_key_speed))) {
                StatisticsUtils.trackClick("boost_click", "用户在首页点击【一键加速】按钮", "home_page", "home_page");
            }
            SchemeProxy.openScheme(getActivity(), list.get(pos).getLinkUrl());
        } else if (list.get(pos).getLinkType().equals("2")) {
            startActivity(new Intent(getActivity(), AgentWebViewActivity.class)
                    .putExtra(ExtraConstant.WEB_URL, list.get(pos).getLinkUrl()));
        } else if (list.get(pos).getLinkType().equals("3")) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(list.get(pos).getLinkUrl());
            intent.setData(content_url);
            startActivity(intent);
        }
    }

    private void goFinishActivity() {
        boolean isOpen = false;
        boolean mIsOpenThree = false;
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_FINISH_SWITCH.equals(switchInfoList.getConfigKey())) {
                    mIsOpenThree = switchInfoList.isOpen();
                }
                if (PositionId.KEY_GAME.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isOpen = switchInfoList.isOpen();
                }
            }
        }
        if (mIsOpenThree) {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.game_quicken));
            startActivity(CleanFinishAdvertisementActivity.class, bundle);
        } else if (isOpen && PreferenceUtil.getShowCount(getActivity(), getString(R.string.game_quicken), mRamScale, mNotifySize, mPowerSize) < 3) {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.game_quicken));
            startActivity(CleanFinishAdvertisementActivity.class, bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.game_quicken));
            bundle.putString("num", PreferenceUtil.getGameCleanPer());
            startActivity(NewCleanFinishActivity.class, bundle);
        }
    }

    /**
     * 病毒查杀激励视频
     */
    private void loadGeekAd() {
        if (null == getActivity() || null == mAdManager) return;
        NiuDataAPI.onPageStart("view_page", "病毒查杀激励视频页浏览");
        NiuDataAPIUtil.onPageEnd("home_page", "virus_killing_video_page", "view_page", "病毒查杀激励视频页浏览");
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "home_page", "virus_killing_video_page");
        mAdManager.loadRewardVideoAd(getActivity(), PositionId.AD_VIRUS, "user123", 1, new VideoAdListener() {
            @Override
            public void onVideoResume(AdInfo info) {

            }

            @Override
            public void onVideoRewardVerify(AdInfo info, boolean rewardVerify, int rewardAmount, String rewardName) {

            }

            @Override
            public void onVideoComplete(AdInfo info) {
                Log.d(TAG, "-----onVideoComplete-----");
                NiuDataAPI.onPageStart("view_page", "病毒查杀激励视频结束页浏览");
            }

            @Override
            public void adSuccess(AdInfo info) {
                Log.d(TAG, "-----adSuccess-----");
                if (null == info) return;
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "home_page", "virus_killing_video_page");
            }

            @Override
            public void adExposed(AdInfo info) {
                Log.d(TAG, "-----adExposed-----");
                PreferenceUtil.saveShowAD(true);
                if (null == info) return;
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), "home_page", "virus_killing_video_page", " ");
            }

            @Override
            public void adClicked(AdInfo info) {
                Log.d(TAG, "-----adClicked-----");
                if (null == info) return;
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), "home_page", "virus_killing_video_page", " ");
            }

            @Override
            public void adClose(AdInfo info) {
                Log.d(TAG, "-----adClose-----");
                PreferenceUtil.saveShowAD(false);
                NiuDataAPIUtil.onPageEnd("home_page", "virus_killing_video_end_page", "view_page", "病毒查杀激励视频结束页浏览");
                if (null != info) {
                    StatisticsUtils.clickAD("close_click", "病毒查杀激励视频结束页关闭点击", "1", info.getAdId(), info.getAdSource(), "home_page", "virus_killing_video_end_page", " ");
                }
                startActivity(VirusKillActivity.class);
            }

            @Override
            public void adError(AdInfo info, int errorCode, String errorMsg) {
                Log.d(TAG, "-----adError-----" + errorMsg);
                if (null != info) {
                    StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "fail", "home_page", "virus_killing_video_page");
                }
                startActivity(VirusKillActivity.class);
            }
        });
    }

    /**
     * 网络加速激励视频
     */
    private void loadGeekAdNet() {
        if (null == getActivity() || null == mAdManager) return;
        NiuDataAPI.onPageStart("view_page", "网络加速激励视频页浏览");
        NiuDataAPIUtil.onPageEnd("home_page", "network_acceleration_video_page", "view_page", "网络加速激励视频页浏览");
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "home_page", "network_acceleration_video_page");
        mAdManager.loadRewardVideoAd(getActivity(), PositionId.AD_NETWORK_ACCE, "user123", 1, new VideoAdListener() {
            @Override
            public void onVideoResume(AdInfo info) {

            }

            @Override
            public void onVideoRewardVerify(AdInfo info, boolean rewardVerify, int rewardAmount, String rewardName) {

            }

            @Override
            public void onVideoComplete(AdInfo info) {
                Log.d(TAG, "-----onVideoComplete-----");
                NiuDataAPI.onPageStart("view_page", "网络加速激励视频结束页浏览");
            }

            @Override
            public void adSuccess(AdInfo info) {
                Log.d(TAG, "-----adSuccess-----");
                if (null == info) return;
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "home_page", "network_acceleration_video_page");
            }

            @Override
            public void adExposed(AdInfo info) {
                Log.d(TAG, "-----adExposed-----");
                PreferenceUtil.saveShowAD(true);
                if (null == info) return;
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), "home_page", "network_acceleration_video_page", " ");
            }

            @Override
            public void adClicked(AdInfo info) {
                Log.d(TAG, "-----adClicked-----");
                if (null == info) return;
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), "home_page", "network_acceleration_video_page", " ");
            }

            @Override
            public void adClose(AdInfo info) {
                Log.d(TAG, "-----adClose-----");
                PreferenceUtil.saveShowAD(false);
                NiuDataAPIUtil.onPageEnd("home_page", "network_acceleration_video_end_page", "view_page", "网络加速激励视频结束页浏览");
                if (null != info) {
                    StatisticsUtils.clickAD("close_click", "网络加速激励视频结束页关闭点击", "1", info.getAdId(), info.getAdSource(), "home_page", "network_acceleration_video_page", " ");
                }
                startActivity(NetWorkActivity.class);
            }

            @Override
            public void adError(AdInfo info, int errorCode, String errorMsg) {
                Log.d(TAG, "-----adError-----" + errorMsg);
                if (null != info) {
                    StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "fail", "home_page", "network_acceleration_video_page");
                }
                startActivity(NetWorkActivity.class);
            }
        });
    }
}
