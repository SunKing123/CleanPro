package com.xiaoniu.cleanking.ui.main.fragment;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
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
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.event.ScanFileEvent;
import com.xiaoniu.cleanking.ui.main.presenter.CleanMainPresenter;
import com.xiaoniu.cleanking.ui.main.widget.MyRelativeLayout;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.ImageUtil;
import com.xiaoniu.cleanking.utils.JavaInterface;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.utils.ToastUtils;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
    @BindView(R.id.view_lottie)
    LottieAnimationView mAnimationView;
    @BindView(R.id.text_acce)
    LinearLayout mTextAcce;
    @BindView(R.id.line_ql)
    LinearLayout mLineQl;
    @BindView(R.id.text_wjgl)
    LinearLayout mTextWjgl;
    @BindView(R.id.iv_dun)
    ImageView mIvDun;
    @BindView(R.id.tv_size)
    TextView mTvSize;
    @BindView(R.id.tv_gb)
    TextView mTvGb;
    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.layout_content_clean_finish)
    LinearLayout mLaoutContentFinish;
    @BindView(R.id.layout_clean_finish)
    ConstraintLayout mLayoutCleanFinish;
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

    /**
     * 清理的分类列表
     */
    public static HashMap<Integer, JunkGroup> mJunkGroups;
    private CountEntity mCountEntity = new CountEntity();
    private List<ImageView> mTopViews;
    private Handler mHandler;

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

        ViewGroup.LayoutParams layoutParams = mLayoutCleanFinish.getLayoutParams();
        layoutParams.height = ScreenUtils.getScreenHeight(AppApplication.getInstance());
        mLayoutCleanFinish.setLayoutParams(layoutParams);

        new Handler().postDelayed(() -> {
            mPresenter.startScan();
            mPresenter.startCleanScanAnimation(mIconOuter, mCircleOuter, mCircleOuter2);
            type = TYPE_SCANING;
            mButtonCleanNow.setText("停止扫描");
        }, 500);

        //请求广告接口
        mPresenter.requestBottomAd();


    }


    //    @OnClick(R.id.text_cooling)
//    public void onCoolingViewClicked() {
//        //手机降温
//        startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
//    }
    @OnClick(R.id.text_wjgl)
    public void wjgl() {
        StatisticsUtils.trackClick("file_clean_click", "\"文件清理\"点击", "home_page", "home_page");

        //文件管理
        startActivity(FileManagerHomeActivity.class);
    }

    @OnClick(R.id.text_acce)
    public void text_acce() {
        StatisticsUtils.trackClick("once_accelerate_click", "\"一键加速\"点击", "home_page", "home_page");
        //一键加速
        startActivity(PhoneAccessActivity.class);
    }

    @OnClick(R.id.line_ql)
    public void line_ql() {
        StatisticsUtils.trackClick("cell_phone_clean_click", "\"手机清理\"点击", "home_page", "home_page");

        //手机清理
        startActivity(RouteConstants.CLEAN_BIG_FILE_ACTIVITY);
    }

    @OnClick(R.id.btn_ljql)
    public void btnLjql() {
        if (type == TYPE_SCAN_FINISH) {
            mScrollView.scrollTo(mScrollView.getScrollX(), 0);
            //扫描完成点击清理
            mPresenter.showTransAnim(mLayoutCleanTop);
            mPresenter.startCleanAnimation(mIconInner, mIconOuter, mLayoutScan, mLayoutCount, mCountEntity);
            mButtonCleanNow.setVisibility(GONE);
            mTextScanTrace.setText("垃圾清理中...");
            mArrowRight.setVisibility(GONE);
            mLayoutRoot.setIntercept(true);
            initWebView();
            StatisticsUtils.trackClick("cleaning_page_click", "\"立即清理\"点击", "home_page", "check_garbage_details");

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
            if (time < 30) {
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
            StatisticsUtils.trackClick("stop_scanning_click", "\"停止扫描\"点击", "home_page", "home_page");
        }
    }

    @OnClick(R.id.layout_scan)
    public void mClickLayoutScan() {
        StatisticsUtils.trackClick("view_spam_details_page_click", "\"查看垃圾详情\"点击", "home_page", "check_garbage_details");

        //查看详情
        if (type == TYPE_SCAN_FINISH) {
            startActivity(RouteConstants.JUNK_CLEAN_ACTIVITY);
        }
    }

    /**
     * 扫描完成
     *
     * @param junkGroups
     */
    public void scanFinish(HashMap<Integer, JunkGroup> junkGroups) {
        type = TYPE_SCAN_FINISH;

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
        StatisticsUtils.trackClick("home_page_view_page", "首页浏览", "home_page", "home_page");
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
            mTextCount.setText(countEntity.getTotalSize());
            mTextUnit.setText(countEntity.getUnit());
        });
    }


    @Subscribe
    public void cleanFinish(String string) {
        if ("clean_finish".equals(string)) {
            //清理完成
            restoreLayout();
            //清理完成后通知 文件数据库同步(陈浪)
            EventBus.getDefault().post(new ScanFileEvent());

            preCleanTime = System.currentTimeMillis();
        }
    }

    public FrameLayout getCleanTopLayout() {
        return mLayoutCleanTop;
    }

    /**
     * 恢复布局
     */
    private void restoreLayout() {
        //设置可以点击
        mLayoutRoot.setIntercept(false);
        mIconInner.setVisibility(GONE);
        //设置背景的高度
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLayoutCleanTop.getLayoutParams();
        layoutParams.height = DeviceUtils.dip2px(460);
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
    }

    public void showBottomTab() {
        if (getActivity() != null) {
            FrameLayout frameLayout = getActivity().findViewById(R.id.frame_layout);
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
            marginParams.bottomMargin = DeviceUtils.dip2px(53);
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

    /**
     * 清理完成后的页面
     *
     * @return
     */
    public View getCleanFinish() {
        return mLayoutCleanFinish;
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {

        //
        showBottomTab();
        mLayoutCleanFinish.setVisibility(GONE);
    }

    private long firstTime;

    public void onKeyBack() {
        if (mLayoutCleanFinish.getVisibility() == VISIBLE) {
            mLayoutCleanFinish.setVisibility(GONE);
            showBottomTab();
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
        String url = ApiModule.Base_H5_Host;
        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JavaInterface(), "cleanPage");
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                showLoadingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                cancelLoadingDialog();
                if (!isError) {
                    if (mLayoutNotNet != null) {
                        mLayoutNotNet.setVisibility(View.GONE);
                    }
                    if (mWebView != null) {
                        mWebView.setVisibility(View.VISIBLE);
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
            isShow = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(color), true);
            } else {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(color), false);
            }
        } else {
            isShow = false;
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
        if (position == 0) {
            mImageFirstAd.setVisibility(VISIBLE);
            ImageUtil.display(dataBean.getImageUrl(), mImageFirstAd);
            clickDownload(mFirstViewAdClick, dataBean.getDownloadUrl(), position);
            mTextBottomTitle.setVisibility(VISIBLE);
        } else if (position == 1) {
            mImageSecondAd.setVisibility(VISIBLE);
            ImageUtil.display(dataBean.getImageUrl(), mImageSecondAd);
            clickDownload(mSecondViewAdClick, dataBean.getDownloadUrl(), position);
            mTextBottomTitle.setVisibility(VISIBLE);
        }
        StatisticsUtils.trackClickHolder("clean_up_ad_show", "\"广告展示曝光", "home_page"
                , "home_page_clean_up_page", String.valueOf(position));

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
            StatisticsUtils.trackClickHolder("clean_up_ad_click", "\"广告点击", "home_page"
                    , "home_page_clean_up_page", String.valueOf(position));
            mPresenter.startDownload(downloadUrl);
            ToastUtils.show("已开始下载");
        });
    }

}
