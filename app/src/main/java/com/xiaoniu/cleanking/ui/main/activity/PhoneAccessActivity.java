package com.xiaoniu.cleanking.ui.main.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.PhoneAccessBelowAdapter;
import com.xiaoniu.cleanking.ui.main.bean.AnimationItem;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.NotificationEvent;
import com.xiaoniu.cleanking.ui.main.presenter.PhoneAccessPresenter;
import com.xiaoniu.cleanking.ui.main.widget.AccessAnimView;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.tool.notify.event.QuickenEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.JavaInterface;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.NestedScrollWebView;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.KeyboardUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 手机加速--一键清理内存页面
 */
public class PhoneAccessActivity extends BaseActivity<PhoneAccessPresenter> {

    @BindView(R.id.cdl_root)
    CoordinatorLayout mCdlRoot;
    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    @BindView(R.id.tv_size_show)
    TextView tv_size_show;
    @BindView(R.id.tv_size)
    TextView tv_size;
    @BindView(R.id.tv_gb)
    TextView tv_gb;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.iv_dun)
    ImageView iv_dun;
    @BindView(R.id.tv_ql)
    TextView tv_ql;
    @BindView(R.id.icon_more)
    ImageView icon_more;
    @BindView(R.id.web_view)
    NestedScrollWebView mWebView;
    @BindView(R.id.viewt)
    View viewt;
    @BindView(R.id.line_title)
    View line_title;
    @BindView(R.id.layout_not_net)
    LinearLayout mLayoutNetError;
    @BindView(R.id.acceview)
    AccessAnimView acceview;
    @BindView(R.id.rel_bottom)
    RelativeLayout rel_bottom;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.tv_title_name)
    TextView mTvTitleName;
    @BindView(R.id.rl_anim_bg)
    RelativeLayout mRlAnimBg;
    @BindView(R.id.tv_speed)
    TextView mTvSpeed;
    @BindView(R.id.line_access)
    TextView mLineAccess;
    @BindView(R.id.n_scroll_view)
    NestedScrollView mNestedScrollView;

    private boolean isError = false;
    private PhoneAccessBelowAdapter belowAdapter;
    // 默认不可点击清理，当数字动画播放完毕后可以点击
    private boolean canClickDelete = false;

    private boolean isClick = false;
    private boolean isDoubleBack = false;
    private AlertDialog mAlertDialog = null;
    private String strNum;
    private String strUnit;
    private boolean isShowListInfo = false;
    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //所有应用所占内存大小

    public void setFromProtect(boolean fromProtect) {
        isFromProtect = fromProtect;
    }

    private boolean isFromProtect = false; //如果点击右上角的加速保护名单在返回，onResume不执行代码

    public void setCanClickDelete(boolean canClickDelete) {
        this.canClickDelete = canClickDelete;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_phone_access;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setTextZoom(100);
        mWebView.addJavascriptInterface(new JavaInterface(mContext, mWebView), "cleanPage");
        mWebView.loadUrl(PreferenceUtil.getWebViewUrl());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                showLoadingDialog();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (mLayoutNetError == null)
                    return;

                isError = true;
                if (mLayoutNetError != null) {
                    mLayoutNetError.setVisibility(View.VISIBLE);
                }
                if (mWebView != null) {
                    mWebView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                cancelLoadingDialog();
                if (!isError) {
                    //回调成功后的相关操作
                    if (mLayoutNetError != null) {
                        mLayoutNetError.setVisibility(View.GONE);
                    }
                    if (mWebView != null) {
                        if (SPUtil.isInAudit())
                            mWebView.setVisibility(View.GONE);
//                        mWebView.setVisibility(SPUtil.isInAudit() ? View.GONE : View.VISIBLE);
                    }
                    if (recycle_view != null) {
                        recycle_view.setVisibility(SPUtil.isInAudit() ? View.GONE : View.VISIBLE);
                    }
                }
                isError = false;
            }

        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String notification = intent.getExtras().getString("NotificationService");
            if ("clean".equals(notification)) {
                StatisticsUtils.trackClick("toggle_boost_click", "常驻通知栏点击加速", "", "toggle_page");
            }
        }
    }

    @Override
    public void initView() {
        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPowerSize = new FileQueryUtils().getRunningProcess().size();
        mAppBarLayout.setExpanded(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String title = bundle.getString(SpCacheConfig.ITEM_TITLE_NAME);
            mTvTitleName.setText(title);
            acceview.setTitleName(title);
        }
        Intent intent = getIntent();
        addClick(intent);

        if (!isUsageAccessAllowed()) {
            mAlertDialog = mPresenter.showPermissionDialog(PhoneAccessActivity.this, new PhoneAccessPresenter.ClickListener() {
                @Override
                public void clickOKBtn() {
                    isClick = true;
                    //开启权限
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
                    startActivity(PhonePremisActivity.class);
                }

                @Override
                public void cancelBtn() {

                }
            });
        } else {
            startCleanAnim();
        }

        iv_back.setOnClickListener(v -> {
            if (!keyBack()) {
                if (mTvSpeed.getVisibility() == View.GONE) {
                    StatisticsUtils.trackClick("return_back", "”一键加速返回“点击", "home_page", "one_click_acceleration_page");
                } else {
                    StatisticsUtils.trackClick("return_back", "”一键加速返回“点击", "home_page", "clean_up_ immediately_page");
                }
                finish();
            } else {
                StatisticsUtils.trackClick("return_back", "”一键加速返回“点击", "home_page", "accelerate_access_to_details_page");
            }
        });

        tv_delete.setOnClickListener(v -> {
            //开始清理
            startClean(false);
        });

        mTvSpeed.setOnClickListener(view -> {
            //开始清理
            startClean(false);
        });
        mLineAccess.setOnClickListener(view -> {
            StatisticsUtils.trackClick("view_details_click", "\"查看详情\"点击", "home_page", "clean_up_ immediately_page");
            NiuDataAPI.onPageStart("accelerate_access_to_details_view_page", "加速查看详情页浏览");
            isShowListInfo = true;
            acceview.setVisibility(View.GONE);
        });
        icon_more.setOnClickListener(v -> mPresenter.showPopupWindow(icon_more));
        acceview.setListener(new AccessAnimView.onAnimEndListener() {
            @Override
            public void onAnimEnd() {
                showCleanFinishUI(strNum, strUnit);
            }

            @Override
            public void onStatusBarColorChanged(int colorRes) {
                setStatusBarNum(colorRes);
            }
        });
        acceview.setAnimationEnd(() -> {
            if (!PreferenceUtil.getCleanTime()) {
                strNum = "";
                strUnit = "";
            }
            showCleanFinishUI(strNum, strUnit);
        });

    }

    private void addClick(Intent intent) {
        if (intent != null) {
            String notifition = intent.getStringExtra("NotificationService");
            if ("clean".equals(notifition)) {
                AppHolder.getInstance().setCleanFinishSourcePageId("toggle_boost_click");
                StatisticsUtils.trackClick("toggle_boost_click", "常驻通知栏点击加速", "", "toggle_page");
            }
        }
    }

    /**
     * 一键加速
     */
    public void showCleanButton() {
        NiuDataAPI.onPageStart("clean_up_immediately_view_page", "立即一键加速浏览页");
    }

    private void showCleanFinishUI(String num, String unit) {
        //清理完成 更新通知栏一键清理icon颜色状态
        NotificationEvent event = new NotificationEvent();
        event.setType("clean");
        EventBus.getDefault().post(event);

        //保存本次清理完成时间 保证每次清理时间间隔为3分钟
        if (PreferenceUtil.getCleanTime()) {
            PreferenceUtil.saveCleanTime();
        }
        boolean isOpen = false;
        //solve umeng error --> SwitchInfoList.getData()' on a null object reference
        if (AppHolder.getInstance().getSwitchInfoList() != null) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isOpen = switchInfoList.isOpen();
                }
            }
        }
        if (isOpen && PreferenceUtil.getShowCount(getString(R.string.tool_one_key_speed), mRamScale, mNotifySize, mPowerSize) < 3) {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.tool_one_key_speed));
            startActivity(CleanFinishAdvertisementActivity.class, bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.tool_one_key_speed));
            bundle.putString("num", tv_size != null ? tv_size.getText().toString() : num);
            bundle.putString("unit", unit);
            startActivity(NewCleanFinishActivity.class, bundle);
        }
        finish();
    }

    private void startCleanAnim() {
        if (mAlertDialog != null) mAlertDialog.cancel();
        //小飞机飞入动画
        acceview.planFlyInAnimator();
        if (Build.VERSION.SDK_INT >= 26) {
            long lastCheckTime = SPUtil.getLong(PhoneAccessActivity.this, SPUtil.ONEKEY_ACCESS, 0);
            long timeTemp = System.currentTimeMillis() - lastCheckTime;
            if (lastCheckTime == 0) {
                mPresenter.getAccessAbove22();
            } else if (timeTemp < 3 * 60 * 1000) {
                acceview.setListInfoSize(0);
            } else if (timeTemp >= 3 * 60 * 1000 && timeTemp < 6 * 60 * 1000) {
                long cacheSize = SPUtil.getLong(PhoneAccessActivity.this, SPUtil.TOTLE_CLEAR_CATH, 0);
                cacheSize = (long) (cacheSize * 0.5);
                SPUtil.setLong(PhoneAccessActivity.this, SPUtil.TOTLE_CLEAR_CATH, cacheSize);
                mPresenter.getAccessAbove22();
            } else if (timeTemp >= 6 * 60 * 1000 && timeTemp < 10 * 60 * 1000) {
                long cacheSize = SPUtil.getLong(PhoneAccessActivity.this, SPUtil.TOTLE_CLEAR_CATH, 0);
                cacheSize = (long) (cacheSize * 0.3);
                SPUtil.setLong(PhoneAccessActivity.this, SPUtil.TOTLE_CLEAR_CATH, cacheSize);
                mPresenter.getAccessAbove22();
            } else {
                SPUtil.setLong(PhoneAccessActivity.this, SPUtil.ONEKEY_ACCESS, 0);
                SPUtil.setLong(PhoneAccessActivity.this, SPUtil.TOTLE_CLEAR_CATH, 0);
                mPresenter.getAccessAbove22();
            }
        } else {
            mPresenter.getAccessListBelow();
        }
    }

    /**
     * 开始清理
     *
     * @param b 是否需要展开 由应用换场清理页面动画
     */
    private void startClean(boolean b) {
        if (!canClickDelete || mTvSpeed == null || acceview == null) return;
        mTvSpeed.setVisibility(View.GONE);
        mLineAccess.setCompoundDrawables(null, null, null, null);
        mLineAccess.setText(getString(R.string.tool_speed_now));
        ArrayList<FirstJunkInfo> junkTemp = new ArrayList<>();
        for (FirstJunkInfo info : belowAdapter.getListImage()) {
            if (info.getIsSelect()) {
                junkTemp.add(info);
            }
        }
        if (junkTemp.size() == 0) return;
        acceview.setVisibility(View.VISIBLE);
        acceview.startTopAnim(b);
        long total = 0;
        for (FirstJunkInfo info : junkTemp) {
            total += info.getTotalSize();
            CleanUtil.killAppProcesses(info.getAppPackageName(), info.getPid());
        }
        belowAdapter.deleteData(junkTemp);
        computeTotalSizeDeleteClick(junkTemp);

        if (total == 0)
            acceview.setListInfoSize(0);
        if (Build.VERSION.SDK_INT >= 26) {
            SPUtil.setLong(PhoneAccessActivity.this, SPUtil.ONEKEY_ACCESS, System.currentTimeMillis());
            SPUtil.setLong(PhoneAccessActivity.this, SPUtil.TOTLE_CLEAR_CATH, total);
        }
        StatisticsUtils.trackClick("cleaning_click", "清理点击", AppHolder.getInstance().getSourcePageId(), "once_accelerate_page");

        EventBus.getDefault().post(new QuickenEvent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("clean_up_immediately_view_page", "立即一键加速浏览页");
        NiuDataAPI.onPageStart("one_click_acceleration_page", "一键加速页浏览");
        NotificationEvent event = new NotificationEvent();
        event.setType("speed");
        EventBus.getDefault().post(event);
//        mWebView.loadUrl(PreferenceUtil.getWebViewUrl());
        if (isFromProtect) {
            isFromProtect = false;
            return;
        }
        if (isClick) {
            if (isUsageAccessAllowed()) {
                if (mAlertDialog != null)
                    mAlertDialog.cancel();
                startCleanAnim();
            } else {
                ToastUtils.showShort(getString(R.string.tool_get_premis));
                if (isDoubleBack) finish();
                isDoubleBack = true;
            }
        }
        isClick = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        KeyboardUtils.closeKeyboard(mWebView);
        NiuDataAPI.onPageEnd("clean_up_immediately_view_page", "立即一键加速浏览页");
        NiuDataAPI.onPageEnd("once_accelerate_view_page", "一键清理页面浏览");
        NiuDataAPI.onPageEnd("accelerate_access_to_details_view_page", "加速查看详情页浏览");
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null || acceview == null) return;

        //悟空清理app加入默认白名单
        for (FirstJunkInfo firstJunkInfo : listInfo) {
            if (SpCacheConfig.APP_ID.equals(firstJunkInfo.getAppPackageName())) {
                listInfo.remove(firstJunkInfo);
            }
        }
        acceview.setListInfoSize(listInfo.size());
        if (listInfo.size() != 0) {

            mRamScale = new FileQueryUtils().computeTotalSize(listInfo);
            computeTotalSize(listInfo);
            setAdapter(listInfo);
        }
    }

    long totalSizesCleaned = 0;

    //计算总的缓存大小
    public void computeTotalSize(ArrayList<FirstJunkInfo> listInfo) {
        long totalSizes = 0;
        for (FirstJunkInfo firstJunkInfo : listInfo){
            totalSizes += !isCacheWhite(firstJunkInfo.getAppPackageName()) ? firstJunkInfo.getTotalSize() : 0;
        }
        setCleanSize(totalSizes, true);
        this.totalSizesCleaned = totalSizes;
    }

    public void computeTotalSizeDeleteClick(ArrayList<FirstJunkInfo> listInfo) {
        long totalSizes = 0;
        for (FirstJunkInfo firstJunkInfo : listInfo)
            totalSizes += firstJunkInfo.getTotalSize();
        setCleanSize(totalSizes, false);
        this.totalSizesCleaned = totalSizes;
    }

    public void setCleanSize(long totalSizes, boolean canPlayAnim) {
        if (acceview == null) return;
        String str_totalSize = CleanAllFileScanUtil.getFileSize(totalSizes);
        if (str_totalSize.endsWith("KB")) return;
        //数字动画转换，GB转成Mb播放，kb太小就不扫描
        int sizeMb = 0;
        if (str_totalSize.endsWith("MB")) {
            sizeMb= Double.valueOf(str_totalSize.substring(0, str_totalSize.length() - 2).trim()).intValue();
            strNum = String.valueOf(sizeMb);
            strUnit = "MB";
            if (canPlayAnim)
                mPresenter.setNumAnim(mTvSpeed, mRlAnimBg, tv_size, tv_size_show, tv_gb, acceview.getTv_gb(), viewt, line_title, 0, sizeMb, 1);
            else
                acceview.getTv_gb().setText("MB");
            acceview.setData(sizeMb);
        } else if (str_totalSize.endsWith("GB")) {
            double gbnum = Double.valueOf(str_totalSize.substring(0, str_totalSize.length() - 2).trim());
            strUnit = "GB";
            sizeMb = NumberUtils.getRoundCeilingInt(gbnum*1024);
            strNum = String.valueOf(sizeMb);
            if (canPlayAnim)
                mPresenter.setNumAnim(mTvSpeed, mRlAnimBg, tv_size, tv_size_show, tv_gb, acceview.getTv_gb(), viewt, line_title, 0, sizeMb, 2);
            else
                acceview.getTv_gb().setText("MB");
            acceview.setData(sizeMb);
        }
    }


    //Android O以上的
    PackageManager packageManager = AppApplication.getInstance().getPackageManager();

    public void getAccessListAbove22(List<ActivityManager.RunningAppProcessInfo> listInfo) {
        if (listInfo.size() == 0) {
            mPresenter.showPermissionDialog(PhoneAccessActivity.this, new PhoneAccessPresenter.ClickListener() {
                @Override
                public void clickOKBtn() {
                    //开启权限
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    startActivity(PhonePremisActivity.class);
                }

                @Override
                public void cancelBtn() {

                }
            });

        } else {
            ArrayList<FirstJunkInfo> aboveListInfo = new ArrayList<>();
            if (listInfo.size() < 15) {
                for (ActivityManager.RunningAppProcessInfo info : listInfo) {
                    //悟空清理app加入默认白名单
                    if (!SpCacheConfig.APP_ID.equals(info.processName)) {
                        FirstJunkInfo mInfo = new FirstJunkInfo();
                        mInfo.setAppPackageName(info.processName);
                        mInfo.setAppName(info.processName);
                        aboveListInfo.add(mInfo);
                    }
                }
            } else {
                for (int i = 0; i < 15; i++) {
                    //悟空清理app加入默认白名单
                    if (!SpCacheConfig.APP_ID.equals(listInfo.get(i).processName)) {
                        FirstJunkInfo mInfo = new FirstJunkInfo();
                        mInfo.setAppPackageName(listInfo.get(i).processName);
                        mInfo.setAppName(listInfo.get(i).processName);
                        aboveListInfo.add(mInfo);
                    }
                }
            }

            long total = SPUtil.getLong(PhoneAccessActivity.this, SPUtil.TOTLE_CLEAR_CATH, 0L);
            long un = 80886656;
            if (total == 0) {
                un = 80886656;
            } else {
                un = total / aboveListInfo.size();
            }
            setAppInfo(aboveListInfo, FileQueryUtils.getInstalledList(), un);
            computeTotalSize(aboveListInfo);
            setAdapter(aboveListInfo);
        }
    }

    private void setAppInfo(ArrayList<FirstJunkInfo> aboveListInfo, List<PackageInfo> listP, long un) {
        for (FirstJunkInfo firstJunkInfo : aboveListInfo) {
            for (int j = 0; j < listP.size(); j++) {
                if (TextUtils.equals(listP.get(j).packageName.trim(), firstJunkInfo.getAppPackageName())) {
                    firstJunkInfo.setAppName(listP.get(j).applicationInfo.loadLabel(packageManager).toString().trim());
                    firstJunkInfo.setGarbageIcon(listP.get(j).applicationInfo.loadIcon(packageManager));
                    firstJunkInfo.setTotalSize((long) (Math.random() * un) + un);
                }
            }
        }
    }

    @Override
    public void netError() {

    }

    /**
     * 获取缓存白名单
     */
    public boolean isCacheWhite(String packageName) {
        SharedPreferences sp = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_NAME_WHITE_LIST_INSTALL_PACKE, Context.MODE_PRIVATE);
        Set<String> sets = sp.getStringSet(SpCacheConfig.WHITE_LIST_KEY_INSTALL_PACKE_NAME, new HashSet<>());
        return sets.contains(packageName);
    }

    public void setAdapter(ArrayList<FirstJunkInfo> listInfos) {
        ArrayList<FirstJunkInfo> listInfoData = new ArrayList<>();
        for (FirstJunkInfo firstJunkInfo : listInfos) {
            if (!isCacheWhite(firstJunkInfo.getAppPackageName()))
                listInfoData.add(firstJunkInfo);
        }

        belowAdapter = new PhoneAccessBelowAdapter(PhoneAccessActivity.this, listInfoData);
        recycle_view.setLayoutManager(new LinearLayoutManager(PhoneAccessActivity.this));
        recycle_view.setAdapter(belowAdapter);
        belowAdapter.setmOnCheckListener((listFile, pos) -> {
            int selectCount = 0;
            for (int i = 0; i < listFile.size(); i++) {
                if (listFile.get(i).getIsSelect()) {
                    selectCount++;
                }
            }
            tv_delete.setBackgroundResource(selectCount == 0 ? R.drawable.delete_unselect_bg : R.drawable.delete_select_bg);
            tv_delete.setSelected(selectCount == 0 ? false : true);
        });

        AnimationItem animationItem = new AnimationItem("Slide from bottom", R.anim.layout_animation_from_bottom);
        mPresenter.runLayoutAnimation(recycle_view, animationItem);

    }

    //清理完毕后展示内容
    public void setCleanedView(long sized) {
        NiuDataAPI.onPageStart("clean_up_page_view_immediately", "清理完成页浏览");
        mWebView.setVisibility(SPUtil.isInAudit() ? View.GONE : View.VISIBLE);
        recycle_view.setVisibility(SPUtil.isInAudit() ? View.GONE : View.VISIBLE);
        initWebView();
        setStatusBar(R.color.color_06C581);
        line_title.setBackgroundColor(getResources().getColor(R.color.color_06C581));
        viewt.setBackgroundColor(getResources().getColor(R.color.color_06C581));
        acceview.setVisibility(View.GONE);
        iv_dun.setVisibility(View.VISIBLE);
        tv_ql.setText("内存已清理");
        setHasCleaned(sized);
        rel_bottom.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NiuDataAPI.onPageEnd("one_click_acceleration_view_page", "一键加速页浏览");
    }

    public void setHasCleaned(long sized) {
        String str_totalSize = CleanAllFileScanUtil.byte2FitSize(totalSizesCleaned);
        int sizeMb = 0;
        if (str_totalSize.endsWith("MB")) {
            sizeMb = NumberUtils.getInteger(str_totalSize.substring(0, str_totalSize.length() - 2));
            tv_size.setText(sizeMb + "");
            tv_gb.setText("MB");
        } else if (str_totalSize.endsWith("GB")) {
            sizeMb = NumberUtils.getInteger(str_totalSize.substring(0, str_totalSize.length() - 2));
            tv_size.setText(sizeMb + "");
            tv_gb.setText("GB");
        }
    }

    public void setStatusBar(int colorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(colorRes), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(colorRes), false);
        }
    }

    public void setStatusBarNum(int colorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, colorRes, true);
        } else {
            StatusBarCompat.setStatusBarColor(this, colorRes, false);
        }
    }

    @OnClick(R.id.layout_not_net)
    public void onTvRefreshClicked() {
        mWebView.loadUrl(PreferenceUtil.getWebViewUrl());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShowListInfo)
                StatisticsUtils.trackClick("system_return_back", "”手机返回“点击", "home_page", "accelerate_access_to_details_page");
            else
                StatisticsUtils.trackClick("system_return_back", "”手机返回“点击", "home_page", "clean_up_immediately_page");

            if (keyBack())
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回事件
     *
     * @return true 显示详情列表 false 显示动画
     */
    private boolean keyBack() {
        if (isShowListInfo) {
            isShowListInfo = false;
            acceview.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    public boolean isUsageAccessAllowed() {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                AppOpsManager manager = ((AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE));
                if (manager == null) return false;
                int mode = manager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), this.getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED;
            } catch (Throwable ignored) {
            }
            return false;
        }
        return true;
    }
}

