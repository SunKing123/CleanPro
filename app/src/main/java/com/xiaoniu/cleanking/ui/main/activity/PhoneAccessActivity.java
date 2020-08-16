package com.xiaoniu.cleanking.ui.main.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.localpush.LocalPushUtils;
import com.xiaoniu.cleanking.ui.main.adapter.PhoneAccessBelowAdapter;
import com.xiaoniu.cleanking.ui.main.bean.AnimationItem;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.LockScreenBtnInfo;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.NotificationEvent;
import com.xiaoniu.cleanking.ui.main.presenter.PhoneAccessPresenter;
import com.xiaoniu.cleanking.ui.main.widget.AccessAnimView;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.newclean.activity.SpeedUpResultActivity;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.FunctionCompleteEvent;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
    private boolean isShowListInfo = false; //是否扫描结束
    private boolean isStartClean = false; //是否开始加速
    private long mTotalSizesCleaned = 0; //扫描的总大小
    private boolean mIsFinish; //是否点击了返回键

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getExtras() != null) {
            //solve umeng error -> 'java.lang.String android.os.BaseBundle.getString(java.lang.String)' on a null object reference
            String notification = intent.getExtras().getString("NotificationService");
            if ("clean".equals(notification)) {
                StatisticsUtils.trackClick("toggle_boost_click", "常驻通知栏点击加速", AppHolder.getInstance().getCleanFinishSourcePageId(), "boost_scan_page_view_page");
            }
        }
    }

    @Override
    public void initView() {
      //  StatusBarUtil.setTransparentForWindow(this);
        StatisticsUtils.onPageStart("boost_scan_page_view_page", "用户在加速扫描页浏览");
        tv_size.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/FuturaRound-Medium.ttf"));
        mAppBarLayout.setExpanded(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String title = bundle.getString(SpCacheConfig.ITEM_TITLE_NAME);
            mTvTitleName.setText(title);
            acceview.setTitleName(title);
        }
        Intent intent = getIntent();
        addClick(intent);

        startCleanAnim();

        acceview.getLineTitle().setOnClickListener(v -> {
            mIsFinish = true;
            StatisticsUtils.trackClick("return_click", "用户在加速扫描页点击返回", "boost_scan_page", "boost_scan_page");
            finish();
        });

        line_title.setOnClickListener(v -> {
            mIsFinish = true;
           /* if (!isShowListInfo) {
                StatisticsUtils.trackClick("return_click", "用户在加速扫描页点击返回", AppHolder.getInstance().getCleanFinishSourcePageId(), "boost_scan_page");
            } else if (isStartClean) {
                StatisticsUtils.trackClick("return_click", "加速动画页返回", "boost_scan_result_page", "boost_animation_page");
            } else {
                StatisticsUtils.trackClick("return_click", "用户在加速诊断页返回", "boost_scan_page", "boost_scan_result_page");
            }*/
            StatisticsUtils.trackClick("return_click", "用户在加速扫描页点击返回", "boost_scan_page", "boost_scan_page");
            finish();
        });

        tv_delete.setOnClickListener(v -> {
            //开始清理
            startClean(false);
        });

        /*mTvSpeed.setOnClickListener(view -> {
            //开始清理
            startClean(false);
        });*/
       /* mLineAccess.setOnClickListener(view -> {
            StatisticsUtils.trackClick("view_details_click", "\"查看详情\"点击", AppHolder.getInstance().getCleanFinishSourcePageId(), "clean_up_ immediately_page");
            NiuDataAPI.onPageStart("accelerate_access_to_details_view_page", "加速查看详情页浏览");
            isShowListInfo = true;
            acceview.setVisibility(View.GONE);
        });*/
        icon_more.setOnClickListener(v -> {
            StatisticsUtils.trackClick("whitelist_click", "用户在加速诊断页点击白名单", "boost_scan_page", "boost_scan_result_page");
            mPresenter.showPopupWindow(icon_more);
        });
        acceview.setListener(new AccessAnimView.onAnimEndListener() {
            @Override
            public void onAnimEnd() {
                if (!mIsFinish) {
                    showCleanFinishUI(strNum, strUnit);
                }
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
            if (!mIsFinish) {
                //showCleanFinishUI(strNum, strUnit);
            }
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
     /*   if (null != acceview) {
            acceview.setVisibility(View.GONE);
        }
        NiuDataAPI.onPageStart("clean_up_immediately_view_page", "立即一键加速浏览页");*/
        if (acceview != null) {
            acceview.cancelYuAnims();
        }
        startClean(false);
    }

    private void showCleanFinishUI(String num, String unit) {
        //加速完成 更新通知栏状态
        NotificationEvent event = new NotificationEvent();
        event.setType("speed");
        event.setAppendValue(2);
        EventBus.getDefault().post(event);

        LocalPushUtils.getInstance().updateLastUsedFunctionTime(SpCacheConfig.KEY_FUNCTION_SPEED_UP);
        //设置锁屏数据
        LockScreenBtnInfo btnInfo = new LockScreenBtnInfo(1);
        btnInfo.setNormal(true);
        btnInfo.setCheckResult("550");
        PreferenceUtil.getInstants().save("lock_pos02", new Gson().toJson(btnInfo));
        EventBus.getDefault().post(btnInfo);

        PreferenceUtil.saveCleanJiaSuUsed(true);
        EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
        AppHolder.getInstance().setCleanFinishSourcePageId("boost_animation_page");


        Intent in = new Intent(this, SpeedUpResultActivity.class);
        int size = mPresenter.getRandomSize();
        in.putExtra(SpeedUpResultActivity.SPEEDUP_APP_SIZE, size);
        startActivity(in);
        finish();
    }

    private void startCleanAnim() {
        if (mAlertDialog != null) mAlertDialog.cancel();
        //小飞机飞入动画
        acceview.planFlyInAnimator();

        if (Build.VERSION.SDK_INT >= 26) {
            mPresenter.getAccessAbove22();
            /*long lastCheckTime = SPUtil.getLong(PhoneAccessActivity.this, SPUtil.ONEKEY_ACCESS, 0);
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
            }*/
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
        if (!canClickDelete || acceview == null || null == belowAdapter) {
            //设置默认值
            strNum = "521";
            strUnit = "MB";
            showCleanFinishUI(strNum, strUnit);
        } else {
            //        mTvSpeed.setVisibility(View.GONE);
//        mLineAccess.setCompoundDrawables(null, null, null, null);
//        mLineAccess.setText(getString(R.string.tool_speed_now));
            StatisticsUtils.trackClick("accelerate_clean_button_click", "用户在加速诊断页点击【一键清理】按钮", "boost_scan_page", "boost_scan_result_page");
            ArrayList<FirstJunkInfo> junkTemp = new ArrayList<>();
            ArrayList<FirstJunkInfo> listdata = belowAdapter.getListImage();
            if (null != listdata && listdata.size() > 0) {
                for (FirstJunkInfo info : listdata) {
                    if (info.isSelect()) {
                        junkTemp.add(info);
                    }
                }
                if (junkTemp.size() == 0) return;
                acceview.setVisibility(View.VISIBLE);
                acceview.startTopAnim(b);
                isStartClean = true;
                NiuDataAPI.onPageStart("boost_animation_page_view_page", "加速动画页浏览");
                NiuDataAPIUtil.onPageEnd("boost_scan_result_page", "boost_animation_page", "boost_animation_page_view_page", "加速动画页浏览");

                long total = 0;
                for (FirstJunkInfo info : junkTemp) {
                    total += info.getTotalSize();
                    CleanUtil.killAppProcesses(info.getAppPackageName(), info.getPid());
                }
                belowAdapter.deleteData(junkTemp);
                if (total == 0) {
                    acceview.setListInfoSize(0);
                }
                if (Build.VERSION.SDK_INT >= 26) {
                    SPUtil.setLong(PhoneAccessActivity.this, SPUtil.ONEKEY_ACCESS, System.currentTimeMillis());
                    SPUtil.setLong(PhoneAccessActivity.this, SPUtil.TOTLE_CLEAR_CATH, mTotalSizesCleaned);
                }
                computeTotalSizeDeleteClick(junkTemp);
            } else {
                //设置默认值
                strNum = "521";
                strUnit = "MB";
                showCleanFinishUI(strNum, strUnit);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("clean_up_immediately_view_page", "立即一键加速浏览页");
        NiuDataAPI.onPageStart("one_click_acceleration_page", "一键加速页浏览");
//        mWebView.loadUrl(PreferenceUtil.getWebViewUrl());
        if (isFromProtect) {
            isFromProtect = false;
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("clean_up_immediately_view_page", "立即一键加速浏览页");
        NiuDataAPI.onPageEnd("once_accelerate_view_page", "一键清理页面浏览");
        NiuDataAPI.onPageEnd("accelerate_access_to_details_view_page", "加速查看详情页浏览");
    }


    //计算总的缓存大小
    public void computeTotalSize(ArrayList<FirstJunkInfo> listInfo) {
        long totalSizes = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            for (FirstJunkInfo firstJunkInfo : listInfo) {
                totalSizes += !isCacheWhite(firstJunkInfo.getAppPackageName()) ? firstJunkInfo.getTotalSize() : 0;
            }
        } else { //8.0以上内存[200M,2G]随机数
            long lastCheckTime = SPUtil.getLong(PhoneAccessActivity.this, SPUtil.ONEKEY_ACCESS, 0);
            long timeTemp = System.currentTimeMillis() - lastCheckTime;
            if (timeTemp >= 3 * 60 * 1000 && timeTemp < 6 * 60 * 1000) {
                long cacheSize = SPUtil.getLong(PhoneAccessActivity.this, SPUtil.TOTLE_CLEAR_CATH, 0);
                totalSizes = (long) (cacheSize * 0.3);
                SPUtil.setLong(PhoneAccessActivity.this, SPUtil.TOTLE_CLEAR_CATH, cacheSize);
            } else if (timeTemp >= 6 * 60 * 1000 && timeTemp < 10 * 60 * 1000) {
                long cacheSize = SPUtil.getLong(PhoneAccessActivity.this, SPUtil.TOTLE_CLEAR_CATH, 0);
                totalSizes = (long) (cacheSize * 0.6);
                SPUtil.setLong(PhoneAccessActivity.this, SPUtil.TOTLE_CLEAR_CATH, cacheSize);
            } else {
                SPUtil.setLong(PhoneAccessActivity.this, SPUtil.ONEKEY_ACCESS, 0);
                SPUtil.setLong(PhoneAccessActivity.this, SPUtil.TOTLE_CLEAR_CATH, 0);
                totalSizes = Long.parseLong(NumberUtils.mathRandom(200 * 1024 * 1024, 2 * 1024 * 1024 * 1024));
            }
        }

        double kb = totalSizes / 1024 / 1024;

        if (kb <= 1) {
            totalSizes = 1024 * 1024 * NumberUtils.mathRandomInt(1, 20);
        }
        setCleanSize(totalSizes, true);
        this.mTotalSizesCleaned = totalSizes;
    }

    /**
     * 清理
     *
     * @param listInfo
     */
    public void computeTotalSizeDeleteClick(ArrayList<FirstJunkInfo> listInfo) {
        long totalSizes = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            for (FirstJunkInfo firstJunkInfo : listInfo)
                totalSizes += firstJunkInfo.getTotalSize();
        } else {
            totalSizes = SPUtil.getLong(PhoneAccessActivity.this, SPUtil.TOTLE_CLEAR_CATH, 0);
        }
        setCleanSize(totalSizes, false);
        this.mTotalSizesCleaned = totalSizes;
    }

    public void setCleanSize(long totalSizes, boolean canPlayAnim) {
        if (acceview == null) return;
        String str_totalSize = CleanAllFileScanUtil.getFileSize(totalSizes);
        if (str_totalSize.endsWith("KB")) return;
        //数字动画转换，GB转成Mb播放，kb太小就不扫描
        int sizeMb = 0;
        if (str_totalSize.endsWith("MB")) {
            if (str_totalSize.contains(",")) {
                str_totalSize = str_totalSize.replace(",", "");
            }
            sizeMb = Double.valueOf(str_totalSize.substring(0, str_totalSize.length() - 2).trim()).intValue();
            strNum = String.valueOf(sizeMb);
            strUnit = "MB";
            if (canPlayAnim) {
                mPresenter.setNumAnim(mRlAnimBg, tv_size, tv_size_show, tv_delete, tv_gb, acceview.getTv_gb(), viewt, line_title, 0, sizeMb, 1);
            } else
                acceview.getTv_gb().setText("MB");
            acceview.setData(sizeMb);
        } else if (str_totalSize.endsWith("GB")) {
            double gbnum = Double.valueOf(str_totalSize.substring(0, str_totalSize.length() - 2).trim());
            strUnit = "GB";
            sizeMb = NumberUtils.getRoundCeilingInt(gbnum * 1024);
            strNum = String.valueOf(sizeMb);
            if (canPlayAnim) {
                mPresenter.setNumAnim(mRlAnimBg, tv_size, tv_size_show, tv_delete, tv_gb, acceview.getTv_gb(), viewt, line_title, 0, sizeMb, 2);
            } else
                acceview.getTv_gb().setText("MB");
            acceview.setData(sizeMb);
        }
    }


    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null || acceview == null) return;
        acceview.setListInfoSize(listInfo.size());
        if (listInfo.size() != 0) {
            computeTotalSize(listInfo);
            // setAdapter(listInfo);
        }
    }

    //Android O以上的
    PackageManager packageManager = AppApplication.getInstance().getPackageManager();

    public void getAccessListAbove22(ArrayList<FirstJunkInfo> aboveListInfo) {
        long total = SPUtil.getLong(PhoneAccessActivity.this, SPUtil.TOTLE_CLEAR_CATH, 0L);
        long un = 80886656;
        if (total == 0) {
            un = 80886656;
        } else {
            un = total / aboveListInfo.size() == 0 ? 1 : aboveListInfo.size();
        }

        setAppInfo(aboveListInfo, FileQueryUtils.getInstalledList(), un);
        computeTotalSize(aboveListInfo);
        //setAdapter(aboveListInfo);
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
//        Set<String> sets = sp.getStringSet(SpCacheConfig.WHITE_LIST_KEY_INSTALL_PACKE_NAME, new HashSet<>());
        Set<String> sets = sp.getStringSet(SpCacheConfig.WHITE_LIST_SOFT_KEY_INSTALL_PACKE_NAME, new HashSet<>());
        if (null != sets && sets.size() > 0) {
            Iterator<String> it = sets.iterator();
            while (it.hasNext()) {
                String str = it.next();
            }
        }
        return sets.contains(packageName);
    }

    public void setAdapter(ArrayList<FirstJunkInfo> listInfos) {
        if (null == recycle_view)
            return;
        LogUtils.e("=======setAdapter被调用");
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
            long selectTotalSize = 0;
            for (int i = 0; i < listFile.size(); i++) {
                if (listFile.get(i).isSelect()) {
                    selectCount++;
                    selectTotalSize += listFile.get(i).getTotalSize();
                }
            }
//            tv_delete.setBackgroundResource(selectCount == 0 ? R.drawable.delete_unselect_bg : R.drawable.icon_clean_btn_bg);
            tv_delete.setSelected(selectCount == 0 ? false : true);
            if (selectCount <= 0 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                tv_delete.getBackground().setAlpha(75);
                tv_delete.setText(getString(R.string.tool_one_key_speed));
            } else if (selectCount == listFile.size()) {
                tv_delete.getBackground().setAlpha(255);
                tv_delete.setText(getString(R.string.tool_one_key_speed) + " " + tv_size.getText().toString() + tv_gb.getText().toString());
            } else {
                tv_delete.getBackground().setAlpha(255);
                tv_delete.setText(getString(R.string.tool_one_key_speed) + " " + CleanAllFileScanUtil.byte2FitSize(selectTotalSize));
            }
        });

        AnimationItem animationItem = new AnimationItem("Slide from bottom", R.anim.layout_animation_from_bottom);
        mPresenter.runLayoutAnimation(recycle_view, animationItem);

        NiuDataAPI.onPageStart("boost_scan_result_page_view_page", "用户在加速诊断页浏览");
        NiuDataAPIUtil.onPageEnd("boost_scan_page", "boost_scan_result_page", "boost_scan_result_page_view_page", "用户在加速诊断页浏览");
        isShowListInfo = true;
    }

    //清理完毕后展示内容
 /*   public void setCleanedView(long sized) {
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
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatisticsUtils.onPageEnd("boost_scan_page_view_page", "用户在加速扫描页浏览", "boost_scan_page", "boost_scan_page");
    }

    public void setHasCleaned(long sized) {
        String str_totalSize = CleanAllFileScanUtil.byte2FitSize(mTotalSizesCleaned);
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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mIsFinish = true;
          /*  if (!isShowListInfo) {
                StatisticsUtils.trackClick("system_return_click", "用户在加速扫描页点击返回", AppHolder.getInstance().getCleanFinishSourcePageId(), "boost_scan_page");
            } else if (isStartClean) {
                StatisticsUtils.trackClick("system_return_click", "加速动画页返回", "boost_scan_result_page", "boost_animation_page");
            } else {
                StatisticsUtils.trackClick("system_return_back_click", "用户在加速诊断页返回", "boost_scan_page", "boost_scan_result_page");
            }*/
            StatisticsUtils.trackClick("system_return_click", "用户在加速扫描页点击返回", "boost_scan_page", "boost_scan_page");
           /* if (keyBack())
                return true;*/
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回事件
     *
     * @return true 显示详情列表 false 显示动画
     */
    /*private boolean keyBack() {
        if (isShowListInfo) {
            isShowListInfo = false;
            acceview.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

}

