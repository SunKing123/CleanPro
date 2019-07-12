package com.xiaoniu.cleanking.ui.main.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.PhoneAccessBelowAdapter;
import com.xiaoniu.cleanking.ui.main.bean.AnimationItem;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.presenter.PhoneAccessPresenter;
import com.xiaoniu.cleanking.ui.main.widget.AccessAnimView;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

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
    WebView mWebView;
    @BindView(R.id.viewt)
    View viewt;
    @BindView(R.id.line_title)
    View line_title;
    @BindView(R.id.acceview)
    AccessAnimView acceview;
    //    PhoneAccessAdapter imageAdapter;
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
        String url = ApiModule.Base_H5_Host;
        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoadingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                cancelLoadingDialog();
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
        initWebView();


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                StatisticsUtils.trackClick("One_click_acceleration_Clean_click", "返回按钮", "home_page", "One_click_acceleration_Clean_up_pag");
            }
        });

        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClickDelete) return;
                acceview.setVisibility(View.VISIBLE);
                acceview.startTopAnim();
                ArrayList<FirstJunkInfo> junkTemp = new ArrayList<>();
                for (FirstJunkInfo info : belowAdapter.getListImage()) {
                    if (info.getIsSelect()) {
                        junkTemp.add(info);
                    }
                }

                long total = 0;
                for (FirstJunkInfo info : junkTemp) {
                    total += info.getTotalSize();
                    CleanUtil.killAppProcesses(info.getAppPackageName(), info.getPid());
                }
                belowAdapter.deleteData(junkTemp);
                computeTotalSize(junkTemp);
                setCleanedView(total);
                if (Build.VERSION.SDK_INT >= 26)
                    SPUtil.setLong(PhoneAccessActivity.this, SPUtil.ONEKEY_ACCESS, System.currentTimeMillis());

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
            }

            @Override
            public void onStatusBarColorChanged(int colorRes) {
                setStatusBar(colorRes);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("clean_up_page_view_immediately", "清理完成页浏览");
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        computeTotalSize(listInfo);
        setAdapter(listInfo);
    }

    long totalSizesCleaned = 0;

    //计算总的缓存大小
    public void computeTotalSize(ArrayList<FirstJunkInfo> listInfo) {
        long totalSizes = 0;
        for (FirstJunkInfo firstJunkInfo : listInfo)
            totalSizes += firstJunkInfo.getTotalSize();
        setCleanSize(totalSizes);
        this.totalSizesCleaned = totalSizes;
    }

    public void setCleanSize(long totalSizes) {
        String str_totalSize = CleanAllFileScanUtil.byte2FitSize(totalSizes);
        if (str_totalSize.endsWith("KB")) return;
        //数字动画转换，GB转成Mb播放，kb太小就不扫描
        int sizeMb = 0;
        if (str_totalSize.endsWith("MB")) {
            sizeMb = NumberUtils.getInteger(str_totalSize.substring(0, str_totalSize.length() - 2));
            mPresenter.setNumAnim(tv_size, tv_gb, viewt, line_title, 0, sizeMb, 1);
            acceview.setData(sizeMb, "MB");
        } else if (str_totalSize.endsWith("GB")) {
            sizeMb = NumberUtils.getInteger(str_totalSize.substring(0, str_totalSize.length() - 2));
            sizeMb *= 1024;
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
            for (FirstJunkInfo firstJunkInfo : aboveListInfo) {
                for (int j = 0; j < listP.size(); j++) {
                    if (TextUtils.equals(listP.get(j).packageName.trim(), firstJunkInfo.getAppPackageName())) {
                        firstJunkInfo.setAppName(listP.get(j).applicationInfo.loadLabel(packageManager).toString().trim());
                        firstJunkInfo.setGarbageIcon(listP.get(j).applicationInfo.loadIcon(packageManager));
                        firstJunkInfo.setTotalSize((int) (Math.random() * 80886656) + 80886656);
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


    public void setAdapter(ArrayList<FirstJunkInfo> listInfo) {
        belowAdapter = new PhoneAccessBelowAdapter(PhoneAccessActivity.this, listInfo);
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
        mWebView.setVisibility(View.VISIBLE);
        iv_dun.setVisibility(View.VISIBLE);
        tv_ql.setText("内存已清理");
        setHasCleaned(sized);
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
