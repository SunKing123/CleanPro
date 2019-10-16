package com.xiaoniu.cleanking.ui.main.fragment;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.activity.NewsActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneThinActivity;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.AutoCleanEvent;
import com.xiaoniu.cleanking.ui.main.event.HomeCleanEvent;
import com.xiaoniu.cleanking.ui.main.event.ScanFileEvent;
import com.xiaoniu.cleanking.ui.main.presenter.CleanMainPresenter;
import com.xiaoniu.cleanking.ui.main.widget.MyRelativeLayout;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.ui.tool.qq.activity.QQCleanHomeActivity;
import com.xiaoniu.cleanking.ui.tool.qq.util.QQUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.PermissionActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.ImageUtil;
import com.xiaoniu.cleanking.utils.JavaInterface;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.NestedScrollWebView;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
@Deprecated
public class CleanMainFragment extends BaseFragment<CleanMainPresenter> {

    @BindView(R.id.text_count)
    AppCompatTextView mTextCount;
    @BindView(R.id.text_unit)
    TextView mTextUnit;
    @BindView(R.id.layout_count)
    RelativeLayout mLayoutCount;
    @BindView(R.id.layout_root)
    MyRelativeLayout mLayoutRoot;
    @BindView(R.id.layout_clean_top)
    FrameLayout mLayoutCleanTop;
    @BindView(R.id.btn_ljql)
    TextView mButtonCleanNow;
    @BindView(R.id.icon_outer)
    ImageView mIconOuter;
    @BindView(R.id.icon_inner)
    ImageView mIconInner;
    @BindView(R.id.circle_outer)
    View mCircleOuter;
    @BindView(R.id.circle_outer2)
    View mCircleOuter2;
    @BindView(R.id.text_scan_trace)
    TextView mTextScanTrace;
    @BindView(R.id.layout_scan)
    LinearLayout mLayoutScan;
    @BindView(R.id.view_arrow)
    ImageView mArrowRight;
    @BindView(R.id.layout_scroll)
    ScrollView mScrollView;
    @BindView(R.id.home_nested_scroll_view)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.view_lottie)
    LottieAnimationView mAnimationView;
    @BindView(R.id.text_acce)
    LinearLayout mTextAcce;
    @BindView(R.id.line_shd)
    LinearLayout mLineShd;
    @BindView(R.id.text_wjgl)
    LinearLayout mTextWjgl;
    @BindView(R.id.iv_dun)
    ImageView mIvDun;
    @BindView(R.id.tv_size)
    TextView mTvSize;
    @BindView(R.id.tv_gb)
    TextView mTvGb;
    @BindView(R.id.web_view)
    NestedScrollWebView mWebView;
    @BindView(R.id.layout_content_clean_finish)
    LinearLayout mLaoutContentFinish;
    @BindView(R.id.image_ad_bottom_first)
    ImageView mImageFirstAd;
    @BindView(R.id.image_ad_bottom_second)
    ImageView mImageSecondAd;
    @BindView(R.id.layout_not_net)
    LinearLayout mLayoutNotNet;
    @BindView(R.id.text_bottom_title)
    TextView mTextBottomTitle;
    @BindView(R.id.view_click_first_ad)
    View mFirstViewAdClick;
    @BindView(R.id.view_click_second_ad)
    View mSecondViewAdClick;
    @BindView(R.id.animation_clean_finish)
    LottieAnimationView mFinishAnimator;
    @BindView(R.id.view_lottie_star)
    LottieAnimationView mLottieStarView;
    @BindView(R.id.view_lottie_home)
    LottieAnimationView mLottieHomeView;
    @BindView(R.id.view_news)
    ImageView mIvNews;
    @BindView(R.id.fl_anim)
    FrameLayout mFlAnim;
    /**
     * 清理的分类列表
     */
    public static HashMap<Integer, JunkGroup> mJunkGroups;
    private CountEntity mCountEntity = new CountEntity();

    /**
     * 首页是否显示
     */
    private boolean isShow = true;

    /**
     * 当前的首页的状态
     */
    private int type = 0;

    /**
     * 未扫描
     */
    private static final int TYPE_NOT_SCAN = 0;
    /**
     * 扫描完成
     */
    private static final int TYPE_SCAN_FINISH = 1;
    /**
     * 清理完成
     */
    public static final int TYPE_CLEAN_FINISH = 2;
    /**
     * 扫描中
     */
    public static final int TYPE_SCANING = 3;
    /**
     * 扫描时颜色变化是否完成
     */
    private boolean mChangeFinish;

    /**
     * 之前清理完成时间
     */
    private long preCleanTime;
    private MyHandler mHandler = new MyHandler(getActivity());

    class MyHandler extends Handler {
        WeakReference<Activity> mActivity;

        public MyHandler(Activity con) {
            this.mActivity = new WeakReference<>(con);
        }

        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (mLottieHomeView != null)
                    mLottieHomeView.playAnimation();
            }else if (msg.what == 2){
                //清理完成
                restoreLayout();
                //清理完成后通知 文件数据库同步(陈浪)
                EventBus.getDefault().post(new ScanFileEvent());
                //保存清理次数
                PreferenceUtil.saveCleanNum();
                preCleanTime = System.currentTimeMillis();
                showBottomTab();
            }
        }
    }

    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_clean_mainnew;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mPresenter.checkPermission();

        //请求广告接口
        mPresenter.requestBottomAd();
        if (mIvNews == null) return;
        //状态（0=隐藏，1=显示）
        String auditSwitch = SPUtil.getString(getActivity(), AppApplication.AuditSwitch, "1");
        if (TextUtils.equals(auditSwitch, "0")) {
            mIvNews.setVisibility(GONE);
        } else {
            mIvNews.setVisibility(VISIBLE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mLayoutCleanTop.setBackgroundColor(getResources().getColor(R.color.color_4690FD));
        } else {
            mLayoutCleanTop.setBackgroundColor(getResources().getColor(R.color.color_FD6F46));
        }
    }

    public void startScan() {
        new Handler().postDelayed(() -> {
            if (mButtonCleanNow == null) return;
            mPresenter.startScan();
            mPresenter.startCleanScanAnimation(mIconOuter, mCircleOuter, mCircleOuter2);
            type = TYPE_SCANING;
            mButtonCleanNow.setText("停止扫描");
        }, 500);
    }

    @OnClick(R.id.text_wjgl)
    public void wjgl() {
        ((MainActivity) getActivity()).commitJpushClickTime(4);
        StatisticsUtils.trackClick("file_clean_click", "用户在首页点击【文件清理】按钮", "home_page", "home_page");

        //文件管理
        startActivity(FileManagerHomeActivity.class);
    }

    @OnClick(R.id.text_acce)
    public void text_acce() {
        AppHolder.getInstance().setCleanFinishSourcePageId("boost_click");
        //一键加速
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.ONKEY);
        ((MainActivity) getActivity()).commitJpushClickTime(2);
        StatisticsUtils.trackClick("boost_click", "用户在首页点击【一键加速】按钮", "home_page", "home_page");
        //保存本次清理完成时间 保证每次清理时间间隔为3分钟
        if (PreferenceUtil.getCleanTime()) {
            PreferenceUtil.saveCleanTime();
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.tool_one_key_speed));
            bundle.putString("num", "");
            bundle.putString("unit", "");
            startActivity(NewCleanFinishActivity.class, bundle);
        }else {
            Bundle bundle = new Bundle();
            bundle.putString(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_one_key_speed));
            startActivity(PhoneAccessActivity.class, bundle);
        }
    }

    @OnClick(R.id.line_shd)
    public void line_shd() {
        //超强省电
        AppHolder.getInstance().setCleanFinishSourcePageId("powersave_click");
        ((MainActivity) getActivity()).commitJpushClickTime(3);
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.SUPER_POWER_SAVING);
        startActivity(PhoneSuperPowerActivity.class);
        StatisticsUtils.trackClick("powersave_click", "用户在首页点击【超强省电】按钮", "home_page", "home_page");
    }

    @OnClick(R.id.view_news)
    public void ViewNewsClick() {
        //新闻点击
        StatisticsUtils.trackClick("news_click", "用户在首页点击【头条新闻热点】按钮", "home_page", "home_page");
        startActivity(NewsActivity.class);
    }

    @OnClick(R.id.view_phone_thin)
    public void ViewPhoneThinClick() {
        //软件管理
        Intent intent = new Intent(getActivity(), PhoneThinActivity.class);
        intent.putExtra(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_soft_manager));
        startActivity(intent);
        StatisticsUtils.trackClick("app_manage_click", "用户在首页点击【软件管理】按钮", "home_page", "home_page");
    }

    @OnClick(R.id.view_qq_clean)
    public void ViewQQCleanClick() {
        //QQ专清
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.QQ_CLEAN);

        ((MainActivity) getActivity()).commitJpushClickTime(7);
        StatisticsUtils.trackClick("qqclean_click", "“用户在首页点击【qq专清】按钮", "home_page", "home_page");
        if (!AndroidUtil.isAppInstalled(SpCacheConfig.QQ_PACKAGE)) {
            ToastUtils.showShort(R.string.tool_no_install_qq);
            return;
        }
        if (QQUtil.audioList != null)
            QQUtil.audioList.clear();
        if (QQUtil.fileList != null)
            QQUtil.fileList.clear();
        startActivity(QQCleanHomeActivity.class);
    }

    @OnClick(R.id.iv_permission)
    public void onClick() {
        startActivity(new Intent(getContext(), PermissionActivity.class));
        StatisticsUtils.trackClick("Triangular_yellow_mark_click", "三角黄标", AppHolder.getInstance().getSourcePageId(), "permission_page");
    }

    /**
     * 开始清理
     */
    public void startCleanNow(){
        if (!"立即清理".equals(mButtonCleanNow.getText().toString()))
            return;
        ((MainActivity) getActivity()).commitJpushClickTime(1);
        mScrollView.scrollTo(mScrollView.getScrollX(), 0);
        //扫描完成点击清理
        mPresenter.showTransAnim(mLayoutCleanTop);
        mPresenter.startCleanAnimation(mIconInner, mIconOuter, mLayoutScan, mLayoutCount, mCountEntity);
        mButtonCleanNow.setVisibility(GONE);
        mTextScanTrace.setText("垃圾清理中...");
        mArrowRight.setVisibility(GONE);
        mLayoutRoot.setIntercept(true);
        initWebView();
        StatisticsUtils.trackClick("clean_click", "用户在首页点击【立即清理】按钮", "home_page", "home_page");
        showHomeLottieView(false);
    }

    @OnClick(R.id.btn_ljql)
    public void btnLjql() {
        AppHolder.getInstance().setOtherSourcePageId("");
        mLottieStarView.setVisibility(GONE);
        if (type == TYPE_SCAN_FINISH) {
            if (AppHolder.getInstance().isPush()){
                AppHolder.getInstance().setPush(false);
                AppHolder.getInstance().setCleanFinishSourcePageId("push_info_click");
            }else {
                AppHolder.getInstance().setCleanFinishSourcePageId("clean_click");
            }
           startCleanNow();
        } else if (type == TYPE_CLEAN_FINISH) {
            //清理完成点击
            mButtonCleanNow.setText("再次扫描");
            type = TYPE_NOT_SCAN;
            mLaoutContentFinish.setVisibility(GONE);
            mLayoutCount.setVisibility(VISIBLE);
            mLayoutScan.setVisibility(VISIBLE);
            mTextCount.setText("0.0");
            mTextUnit.setText("MB");
            mTextScanTrace.setText("还未扫描");
            mArrowRight.setVisibility(GONE);
        } else if (type == TYPE_NOT_SCAN) {
            long now = System.currentTimeMillis();
            long time = (now - preCleanTime) / 1000;
            if (time < 180) {
                cleanFinishSign();
            } else {
                //未扫描， 去扫描
                mCircleOuter.setVisibility(VISIBLE);
                mCircleOuter2.setVisibility(VISIBLE);
                mPresenter.startScan();
                mPresenter.startCleanScanAnimation(mIconOuter, mCircleOuter, mCircleOuter2);
                type = TYPE_SCANING;
                mButtonCleanNow.setText("停止扫描");
                mTextScanTrace.setText("扫描中");
            }

        } else if (type == TYPE_SCANING) {
            //停止扫描
            mPresenter.setIsFinish(true);
            StatisticsUtils.trackClick("stopscan_click", "用户在首页点击【停止扫描】按钮", "home_page", "home_page");
        }
    }

    @OnClick(R.id.layout_scan)
    public void mClickLayoutScan() {
        AppHolder.getInstance().setOtherSourcePageId("");
        StatisticsUtils.trackClick("cleandetail_click", "用户在首页点击【查看详情】按钮", "home_page", "home_page");
        showHomeLottieView(false);
        //查看详情
        if (type == TYPE_SCAN_FINISH) {
            startActivity(RouteConstants.JUNK_CLEAN_ACTIVITY);
        }
    }

    @OnClick(R.id.line_wx)
    public void mClickWx() {
        AppHolder.getInstance().setCleanFinishSourcePageId("wxclean_click");
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
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.tool_chat_clear));
            bundle.putString("num", "");
            bundle.putString("unit", "");
            startActivity(NewCleanFinishActivity.class,bundle);
        }
    }

    @OnClick(R.id.line_super_power_saving)
    public void mClickQq() {
        AppHolder.getInstance().setCleanFinishSourcePageId("notification_clean_click");
        //通知栏清理
        NotifyCleanManager.startNotificationCleanActivity(getActivity(), 0);
        StatisticsUtils.trackClick("notification_clean_click", "用户在首页点击【通知清理】按钮", AppHolder.getInstance().getSourcePageId(), "home_page");
    }

    @OnClick(R.id.line_jw)
    public void mClickJw() {
        AppHolder.getInstance().setCleanFinishSourcePageId("cooling_click");
        ((MainActivity) getActivity()).commitJpushClickTime(6);
        startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
        StatisticsUtils.trackClick("cooling_click", "用户在首页点击【手机降温】按钮", AppHolder.getInstance().getSourcePageId(), "home_page");
    }

    /**
     * 扫描完成
     *
     * @param junkGroups
     */
    public void scanFinish(HashMap<Integer, JunkGroup> junkGroups) {
        type = TYPE_SCAN_FINISH;
        if (mTextScanTrace == null)
            return;

        if (mCountEntity != null && mTvSize != null) {
            mTvSize.setText(mCountEntity.getTotalSize());
            mTvGb.setText(mCountEntity.getUnit());

            if (mCountEntity.getNumber() > 0) {
                //扫描到垃圾
                mLayoutCleanTop.setBackgroundResource(R.drawable.bg_home_scan_finish);
                //设置titlebar颜色
                if (getViewShow()) {
                    showBarColor(getResources().getColor(R.color.color_FD6F46));
                }

                mButtonCleanNow.setText("立即清理");
                mJunkGroups = junkGroups;
                mTextScanTrace.setText("查看垃圾详情");
                mArrowRight.setVisibility(VISIBLE);
            } else {
                //没有扫描到垃圾
                cleanFinishSign();
            }
        }

        //重置扫描状态
        mPresenter.setIsFinish(false);
        //重置颜色变化状态
        mChangeFinish = false;

        mPresenter.stopCleanScanAnimation();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 统计总数
     *
     * @param total
     */
    public void showCountNumber(long total) {
        if (getActivity() == null) {
            return;
        }
        mCountEntity = CleanUtil.formatShortFileSize(total);
        getActivity().runOnUiThread(() -> {
            CountEntity countEntity = CleanUtil.formatShortFileSize(total);
            if (mTextCount == null)
                return;
            mTextCount.setText(countEntity.getTotalSize());
            mTextUnit.setText(countEntity.getUnit());
        });
    }

    @Subscribe
    public void cleanFinish(HomeCleanEvent homeCleanEvent) {
        if (homeCleanEvent.isNowClean()) {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.app_name));
            bundle.putString("num", "");
            bundle.putString("unit", "");
            startActivity(NewCleanFinishActivity.class, bundle);
        }
        mHandler.sendEmptyMessageDelayed(2,1000);
    }

    public FrameLayout getCleanTopLayout() {
        return mLayoutCleanTop;
    }

    /**
     * 恢复布局
     */
    private void restoreLayout() {
        if (mLayoutRoot == null) return;
        showHomeLottieView(true);
        //设置可以点击
        mLayoutRoot.setIntercept(false);
        mIconInner.setVisibility(GONE);
        mIconOuter.setVisibility(VISIBLE);
        mLayoutScan.setVisibility(VISIBLE);
        mLayoutCount.setVisibility(VISIBLE);
        mAnimationView.setVisibility(VISIBLE);
        mFlAnim.setVisibility(View.INVISIBLE);
        //设置背景的高度
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLayoutCleanTop.getLayoutParams();
        layoutParams.height = DisplayUtils.dip2px(460);
        mLayoutCleanTop.setLayoutParams(layoutParams);
        //移动的页面view还原
        mIconOuter.setTranslationY(0);
        mIconInner.setTranslationY(0);
        mLayoutScan.setTranslationY(0);
        mLayoutCount.setTranslationY(0);

        cleanFinishSign();

        //恢复背景
        mLayoutCleanTop.setBackgroundResource(R.drawable.bg_big_home);
        //设置titlebar颜色
        showBarColor(getResources().getColor(R.color.color_4690FD));
    }

    /**
     * 清理很干净标识
     */
    public void cleanFinishSign() {
        mLaoutContentFinish.setVisibility(VISIBLE);
        mLayoutCount.setVisibility(GONE);
        mLayoutScan.setVisibility(GONE);
        //按钮设置
        mButtonCleanNow.setText("完成");
        mButtonCleanNow.setVisibility(VISIBLE);
        //清理完成标识
        type = TYPE_CLEAN_FINISH;
        setColorChange(false);
        //播放lottie动画
        mLottieStarView.setVisibility(VISIBLE);
        playStarAnimation();
        mPresenter.showOuterViewRotation(mIconOuter);
    }

    private void playStarAnimation() {
        mLottieStarView.setImageAssetsFolder("images");
        mLottieStarView.setAnimation("data_star.json");
        mLottieStarView.playAnimation();
    }

    public void showBottomTab() {
        if (getActivity() != null) {
            FrameLayout frameLayout = getActivity().findViewById(R.id.frame_layout);
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
            marginParams.bottomMargin = DisplayUtils.dip2px(53);
            frameLayout.setLayoutParams(marginParams);
            getActivity().findViewById(R.id.bottomBar).setVisibility(VISIBLE);
            getActivity().findViewById(R.id.bottom_shadow).setVisibility(VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void showScanFile(String p0) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(() -> {
            if (mTextScanTrace != null) {
                mTextScanTrace.setText("扫描:" + p0);
            }
        });

    }

    public void endScanAnimation() {
        mCircleOuter2.setVisibility(GONE);
        mCircleOuter.setVisibility(GONE);
        showHomeLottieView(true);
    }

    /**
     * 显示lottie动画
     */
    public void showLottieView() {
        mAnimationView.useHardwareAcceleration();
        mAnimationView.setImageAssetsFolder("images");
        mAnimationView.setAnimation("data.json");
        mAnimationView.playAnimation();
    }

    public LottieAnimationView getLottieView() {
        return mAnimationView;
    }

    /**
     * 清理完成后的页面
     *
     * @return
     */
    public View getCleanFinish() {
        return mNestedScrollView;
    }

    public RelativeLayout getCleanTextLayout() {
        return mLayoutCount;
    }

    public LinearLayout getScanLayout() {
        return mLayoutScan;
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        showBottomTab();
        if (mLayoutRoot == null) return;
        mNestedScrollView.setVisibility(GONE);
        mLayoutRoot.setVisibility(VISIBLE);
        playStarAnimation();
    }

    private long firstTime;

    public void onKeyBack() {
        if (mLayoutRoot == null) return;
        if (mNestedScrollView.getVisibility() == VISIBLE) {
            mNestedScrollView.setVisibility(GONE);
            mLayoutRoot.setVisibility(VISIBLE);
            showBottomTab();
            playStarAnimation();
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - firstTime > 1500) {
                Toast.makeText(getActivity(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                firstTime = currentTimeMillis;
            } else {
                SPUtil.setInt(getContext(), "turnask", 0);
                AppManager.getAppManager().AppExit(getContext(), false);
            }
        }
    }

    /**
     * 状态栏颜色变化
     *
     * @param animatedValue
     */
    public void showBarColor(int animatedValue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(getActivity(), animatedValue, true);
        } else {
            StatusBarCompat.setStatusBarColor(getActivity(), animatedValue, false);
        }
    }

    boolean isError = false;

    public void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setTextZoom(100);
        mWebView.addJavascriptInterface(new JavaInterface(getActivity(), mWebView), "cleanPage");
        mWebView.loadUrl(PreferenceUtil.getWebViewUrl());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mLayoutNotNet == null) return;
                if (!isError) {
                    if (mLayoutNotNet != null) {
                        mLayoutNotNet.setVisibility(View.GONE);
                    }
                    if (mWebView != null) {
                        mWebView.setVisibility(SPUtil.isInAudit() ? View.GONE : View.VISIBLE);
                    }
                }
                isError = false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isError = true;
                if (mLayoutNotNet != null) {
                    mLayoutNotNet.setVisibility(VISIBLE);
                }
                if (mWebView != null) {
                    mWebView.setVisibility(GONE);
                }
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
    }

    @OnClick(R.id.layout_not_net)
    public void onTvRefreshClicked() {
        mWebView.loadUrl(ApiModule.Base_H5_Host);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        int color = R.color.color_4690FD;
        if (type == TYPE_SCAN_FINISH || mChangeFinish) {
            //扫描完成
            color = R.color.color_FD6F46;
        }

        if (!hidden) {
            NiuDataAPI.onPageStart("home_page_view_page", "首页浏览");
            isShow = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(color), true);
            } else {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(color), false);
            }
        } else {
            isShow = false;
            NiuDataAPI.onPageEnd("home_page_view_page", "首页浏览");
        }

    }

    /**
     * 获取当前Fragment是否显示
     *
     * @return
     */
    public boolean getViewShow() {
        return isShow;
    }

    /**
     * 是否颜色变化完成
     *
     * @param b
     */
    public void setColorChange(boolean b) {
        mChangeFinish = b;
    }

    /**
     * 获取总量显示的view
     *
     * @return
     */
    public AppCompatTextView getTextCountView() {
        return mTextCount;
    }

    /**
     * 获取总量显示的view
     *
     * @return
     */
    public TextView getTextUnitView() {
        return mTextUnit;
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
            mTextBottomTitle.setVisibility(GONE);
        } else if (position == 1) {
            mImageSecondAd.setVisibility(VISIBLE);
            ImageUtil.display(dataBean.getImageUrl(), mImageSecondAd);
            clickDownload(mImageSecondAd, dataBean.getDownloadUrl(), position);
            mTextBottomTitle.setVisibility(GONE);
        }
        StatisticsUtils.trackClickAD("ad_show", "\"广告展示曝光", AppHolder.getInstance().getSourcePageId(), "home_page_clean_up_page", String.valueOf(position));
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
            Bundle bundle = new Bundle();
            bundle.putString(Constant.URL, downloadUrl);
            bundle.putBoolean(Constant.NoTitle, false);
            startActivity(UserLoadH5Activity.class, bundle);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("home_page_view_page", "首页浏览");
        NiuDataAPI.onPageStart("check_garbage_view_page", "清理垃圾浏览");
        if (isGotoSetting) {
            mPresenter.checkPermission();
            isGotoSetting = false;
        }
    }

    boolean isGotoSetting = false;

    public void setIsGotoSetting(boolean isGotoSetting) {
        this.isGotoSetting = isGotoSetting;
    }

    @Override
    public void onPause() {
        super.onPause();
        NiuDataAPI.onPageStart("home_page_view_page", "首页浏览");
        NiuDataAPI.onPageEnd("check_garbage_view_page", "清理垃圾浏览");
    }

    /**
     * 获取结束的lottieView
     *
     * @return
     */
    public LottieAnimationView getFinishAnimator() {
        return mFinishAnimator;
    }

    public FrameLayout getmFlAnim() {
        return mFlAnim;
    }

    @Subscribe
    public void pushAutoClean(AutoCleanEvent autoCleanEvent) {
        if (mButtonCleanNow != null && type == TYPE_SCAN_FINISH) {
            mButtonCleanNow.performClick();
        }
    }

    /**
     * 静止时动画
     *
     * @param isMove true转动 false 停止
     */
    private void showHomeLottieView(boolean isMove) {
        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_anim);
        if (rotate == null) {
            mIconOuter.setAnimation(rotate);
        }
        mLottieHomeView.useHardwareAcceleration();
        mLottieHomeView.setAnimation("data_home.json");
        mLottieHomeView.setImageAssetsFolder("images");
        if (isMove) {
            mIconOuter.startAnimation(rotate);
            mLottieHomeView.playAnimation();
            mLottieHomeView.setVisibility(VISIBLE);
        } else {
            mHandler.removeCallbacksAndMessages(null);
            mIconOuter.clearAnimation();
            mLottieHomeView.cancelAnimation();
            mLottieHomeView.setVisibility(GONE);
        }
        mLottieHomeView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHandler.sendEmptyMessageDelayed(1,1500);
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
