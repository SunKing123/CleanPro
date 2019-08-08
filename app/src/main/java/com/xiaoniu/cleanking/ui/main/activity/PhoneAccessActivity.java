package com.xiaoniu.cleanking.ui.main.activity;

import android.app.Activity;
import android.app.ActivityManager;
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
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.PhoneAccessBelowAdapter;
import com.xiaoniu.cleanking.ui.main.bean.AnimationItem;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.presenter.PhoneAccessPresenter;
import com.xiaoniu.cleanking.ui.main.widget.AccessAnimView;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.JavaInterface;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.widget.NestedScrollWebView;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.statistic.NiuDataAPI;

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

    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
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
    //    PhoneAccessAdapter imageAdapter;
    private boolean isSuccess = false;
    private boolean isError = false;
    PhoneAccessBelowAdapter belowAdapter;
    boolean canClickDelete = false; //默认不可点击清理，当数字动画播放完毕后可以点击

    public void setFromProtect(boolean fromProtect) {
        isFromProtect = fromProtect;
    }

    boolean isFromProtect = false; //如果点击右上角的加速保护名单在返回，onResume不执行代码

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

    public void initWebView() {
        String url = ApiModule.Base_H5_Host + "/activity_page.html?deviceId=" + AndroidUtil.getUdid();
        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JavaInterface((Activity) mContext, mWebView), "cleanPage");
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                showLoadingDialog();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                isError = true;
                isSuccess = false;
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
                cancelLoadingDialog();
                if (!isError) {
                    isSuccess = true;
                    //回调成功后的相关操作
                    if (mLayoutNetError != null) {
                        mLayoutNetError.setVisibility(View.GONE);
                    }
                    if (mWebView != null) {
                        mWebView.setVisibility(AndroidUtil.isInAudit() ? View.GONE : View.VISIBLE);
                    }
                    if (recycle_view != null) {
                        recycle_view.setVisibility(AndroidUtil.isInAudit() ? View.GONE : View.VISIBLE);
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
    public void initView() {

        if (Build.VERSION.SDK_INT >= 26) {
            long lastCheckTime = SPUtil.getLong(PhoneAccessActivity.this, SPUtil.ONEKEY_ACCESS, 0);
            long timeTemp = System.currentTimeMillis() - lastCheckTime;
            if (lastCheckTime == 0 || timeTemp > 20 * 1000)
                mPresenter.getAccessAbove22();
            else
                setCleanedView(0);
        } else {
            mPresenter.getAccessListBelow();
        }
        NiuDataAPI.onPageStart("clean_up_page_view_immediately", "清理完成页浏览");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                StatisticsUtils.trackClick("One_click_Accelerated_Return_click", "返回按钮", "home_page", "once_accelerate_page");
            }
        });

        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClickDelete) return;
                ArrayList<FirstJunkInfo> junkTemp = new ArrayList<>();
                for (FirstJunkInfo info : belowAdapter.getListImage()) {
                    if (info.getIsSelect()) {
                        junkTemp.add(info);
                    }
                }
                if (junkTemp.size() == 0) return;
                acceview.setVisibility(View.VISIBLE);
                acceview.startTopAnim();
                long total = 0;
                for (FirstJunkInfo info : junkTemp) {
                    total += info.getTotalSize();
                    CleanUtil.killAppProcesses(info.getAppPackageName(), info.getPid());
                }
                belowAdapter.deleteData(junkTemp);
                computeTotalSizeDeleteClick(junkTemp);
                setCleanedView(total);
                if (Build.VERSION.SDK_INT >= 26)
                    SPUtil.setLong(PhoneAccessActivity.this, SPUtil.ONEKEY_ACCESS, System.currentTimeMillis());
                StatisticsUtils.trackClick("cleaning_click", "清理点击", "home_page", "once_accelerate_page");
            }
        });

        icon_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.showPopupWindow(icon_more);
            }
        });
        acceview.setListener(new AccessAnimView.onAnimEndListener() {
            @Override
            public void onAnimEnd() {
                //动画结束时
                setStatusBar(R.color.color_06C581);
                if (viewt == null || line_title == null) return;
                line_title.setBackgroundColor(getResources().getColor(R.color.color_06C581));
                viewt.setBackgroundColor(getResources().getColor(R.color.color_06C581));
                setCleanedView(0);
                rel_bottom.setVisibility(View.GONE);
                mAppBarLayout.setExpanded(true);
            }

            @Override
            public void onStatusBarColorChanged(int colorRes) {
                setStatusBarNum(colorRes);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFromProtect) {
            isFromProtect = false;
            return;
        }
        NiuDataAPI.onPageStart("once_accelerate_view_page", "一键清理页面浏览");
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("once_accelerate_view_page", "一键清理页面浏览");
        NiuDataAPI.onPageEnd("clean_up_page_view_immediately", "清理完成页浏览");
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo.size() == 0) {
            setCleanedView(0);
        } else {
            computeTotalSize(listInfo);
            setAdapter(listInfo);
        }
    }

    long totalSizesCleaned = 0;

    //计算总的缓存大小
    public void computeTotalSize(ArrayList<FirstJunkInfo> listInfo) {
        long totalSizes = 0;
        for (FirstJunkInfo firstJunkInfo : listInfo)
            totalSizes += !isCacheWhite(firstJunkInfo.getAppPackageName()) ? firstJunkInfo.getTotalSize() : 0;
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
        String str_totalSize = CleanAllFileScanUtil.byte2FitSize(totalSizes);
        if (str_totalSize.endsWith("KB")) return;
        //数字动画转换，GB转成Mb播放，kb太小就不扫描
        int sizeMb = 0;
        if (str_totalSize.endsWith("MB")) {
            sizeMb = NumberUtils.getInteger(str_totalSize.substring(0, str_totalSize.length() - 2));
            if (canPlayAnim)
                mPresenter.setNumAnim(tv_size, tv_gb, viewt, line_title, 0, sizeMb, 1);
            acceview.setData(sizeMb, "MB");
        } else if (str_totalSize.endsWith("GB")) {
            sizeMb = NumberUtils.getInteger(str_totalSize.substring(0, str_totalSize.length() - 2));
            sizeMb *= 1024;
            if (canPlayAnim)
                mPresenter.setNumAnim(tv_size, tv_gb, viewt, line_title, 0, sizeMb, 2);
            acceview.setData(sizeMb, "GB");
        }
    }

    //Android O以上的
    PackageManager packageManager = AppApplication.getInstance().getPackageManager();

    public void getAccessListAbove22(List<ActivityManager.RunningAppProcessInfo> listInfo) {
        if (listInfo.size() == 0) {
            mPresenter.showPermissionDialog(PhoneAccessActivity.this, new PhoneAccessPresenter.ClickListener() {
                @Override
                public void clickOKBtn() {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
                }

                @Override
                public void cancelBtn() {

                }
            });

        } else {
            ArrayList<FirstJunkInfo> aboveListInfo = new ArrayList<>();
            for (ActivityManager.RunningAppProcessInfo info : listInfo) {
                FirstJunkInfo mInfo = new FirstJunkInfo();
                mInfo.setAppPackageName(info.processName);
                mInfo.setAppName(info.processName);
                aboveListInfo.add(mInfo);
            }
            List<PackageInfo> listP = FileQueryUtils.getInstalledList();
//            long oneG = (1024 * 1024 * 1024) / aboveListInfo.size();
            long un = 80886656;
            if (aboveListInfo.size() < 10 && aboveListInfo.size() > 0) {
                un = 80886656;
            } else if (aboveListInfo.size() < 20 && aboveListInfo.size() >= 10) {
                un = 40886656;
            } else {
                un = 20886656;
            }
            for (FirstJunkInfo firstJunkInfo : aboveListInfo) {
                for (int j = 0; j < listP.size(); j++) {
                    if (TextUtils.equals(listP.get(j).packageName.trim(), firstJunkInfo.getAppPackageName())) {
                        firstJunkInfo.setAppName(listP.get(j).applicationInfo.loadLabel(packageManager).toString().trim());
                        firstJunkInfo.setGarbageIcon(listP.get(j).applicationInfo.loadIcon(packageManager));
                        firstJunkInfo.setTotalSize((int) (Math.random() * un) + un);
                    }
                }
            }
            computeTotalSize(aboveListInfo);
            setAdapter(aboveListInfo);
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
        belowAdapter.setmOnCheckListener(new PhoneAccessBelowAdapter.onCheckListener() {
            @Override
            public void onCheck(List<FirstJunkInfo> listFile, int pos) {
                int selectCount = 0;
                for (int i = 0; i < listFile.size(); i++) {
                    if (listFile.get(i).getIsSelect()) {
                        selectCount++;
                    }
                }
//                cb_checkall.setBackgroundResource(selectCount == listFile.size() ? R.drawable.icon_select : R.drawable.icon_unselect);
                tv_delete.setBackgroundResource(selectCount == 0 ? R.drawable.delete_unselect_bg : R.drawable.delete_select_bg);
                tv_delete.setSelected(selectCount == 0 ? false : true);
//                compulateDeleteSize();
            }
        });

        AnimationItem animationItem = new AnimationItem("Slide from bottom", R.anim.layout_animation_from_bottom);
        mPresenter.runLayoutAnimation(recycle_view, animationItem);

    }

    //清理完毕后展示内容
    public void setCleanedView(long sized) {
        NiuDataAPI.onPageStart("clean_up_page_view_immediately", "清理完成页浏览");
        mWebView.setVisibility(AndroidUtil.isInAudit() ? View.GONE : View.VISIBLE);
        recycle_view.setVisibility(AndroidUtil.isInAudit() ? View.GONE : View.VISIBLE);
        initWebView();
        iv_dun.setVisibility(View.VISIBLE);
        tv_ql.setText("内存已清理");
        setHasCleaned(sized);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NiuDataAPI.onPageEnd("clean_up_page_view_immediately", "清理完成页浏览");
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
        mWebView.loadUrl(ApiModule.Base_H5_Host + "/activity_page.html?deviceId=" + AndroidUtil.getUdid());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.getVisibility() == View.VISIBLE && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


}
