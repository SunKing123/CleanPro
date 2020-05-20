package com.xiaoniu.cleanking.ui.main.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.keeplive.KeepAliveManager;
import com.xiaoniu.cleanking.keeplive.config.ForegroundNotification;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.ui.main.bean.DeviceInfo;
import com.xiaoniu.cleanking.ui.main.event.AutoCleanEvent;
import com.xiaoniu.cleanking.ui.main.event.FileCleanSizeEvent;
import com.xiaoniu.cleanking.ui.main.event.ScanFileEvent;
import com.xiaoniu.cleanking.ui.main.fragment.MeFragment;
import com.xiaoniu.cleanking.ui.main.fragment.ShoppingMallFragment;
import com.xiaoniu.cleanking.ui.main.fragment.ToolFragment;
import com.xiaoniu.cleanking.ui.main.presenter.MainPresenter;
import com.xiaoniu.cleanking.ui.main.widget.BottomBar;
import com.xiaoniu.cleanking.ui.main.widget.BottomBarTab;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.newclean.fragment.NewCleanMainFragment;
import com.xiaoniu.cleanking.ui.news.fragment.NewsFragment;
import com.xiaoniu.cleanking.ui.news.utils.NewsUtils;
import com.xiaoniu.cleanking.ui.notifition.NotificationService;
import com.xiaoniu.cleanking.utils.DbHelper;
import com.xiaoniu.cleanking.utils.MainBottomBarUtil;
import com.xiaoniu.cleanking.utils.NotificationsUtils;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.DeviceUtil;
import com.xiaoniu.common.utils.StatisticsUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;

import static com.hellogeek.permission.Integrate.Permission.PACKAGEUSAGESTATS;
import static com.xiaoniu.cleanking.keeplive.config.RunMode.HIGH_POWER_CONSUMPTION;
import static com.xiaoniu.cleanking.keeplive.config.RunMode.POWER_SAVING;

/**
 * main主页面
 */
@Route(path = RouteConstants.MAIN_ACTIVITY)
public class MainActivity extends BaseActivity<MainPresenter> {
    private static final int REQUEST_STORAGE_PERMISSION = 1111;

    @BindView(R.id.bottomBar)
    BottomBar mBottomBar;
    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentManager mManager = getSupportFragmentManager();
    private NewCleanMainFragment mMainFragment;
    private NewsFragment upQuotaFragment;

    private static final long DEFAULT_REFRESH_TIME = 10 * 60 * 1000L;
    /**
     * 定时扫面手机时间 1小时
     */
    private static final long SCAN_LOOP_TIME = 3 * 1000;

    private static final int MSG_SHOW_NEWS_BADGE_VIEW = 1;


    @Inject
    NoClearSPHelper mSPHelper;

    //判断重新启动
    boolean isFirstCreate = false;

    private BottomBarTab mNewsBottomBarTab;
    private boolean isSelectTop = false;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SHOW_NEWS_BADGE_VIEW) {
                showNewsBadgeView();
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

        mHandler.sendEmptyMessageDelayed(1, DEFAULT_REFRESH_TIME);
        if (MainBottomBarUtil.isShowNewsTab()) {
            mHandler.sendEmptyMessageDelayed(MSG_SHOW_NEWS_BADGE_VIEW, DEFAULT_REFRESH_TIME);
        }
        isFirstCreate = true;
        initFragments();
        boolean isAuditMode = SPUtil.isInAudit();
        boolean isMainTabNewsOpen = NewsUtils.isMainTabNewsOpen();
        if (isAuditMode) {
            mBottomBar
                    .addItem(new BottomBarTab(this, R.drawable.ic_tab_clean_normal, getString(R.string.clean)))
                    .addItem(new BottomBarTab(this, R.drawable.ic_tab_me_normal, getString(R.string.mine)));
        } else {
            if (isMainTabNewsOpen) {
                mNewsBottomBarTab = new BottomBarTab(this, R.drawable.ic_tab_msg_normal, getString(R.string.top));
                mBottomBar.addItem(new BottomBarTab(this, R.drawable.ic_tab_clean_normal, getString(R.string.clean)))
                        .addItem(new BottomBarTab(this, R.drawable.ic_tab_tool_normal, getString(R.string.tool)))
                        .addItem(mNewsBottomBarTab)
                        .addItem(new BottomBarTab(this, R.drawable.ic_tab_me_normal, getString(R.string.mine)));

            } else {
                mBottomBar.addItem(new BottomBarTab(this, R.drawable.ic_tab_clean_normal, getString(R.string.clean)))
                        .addItem(new BottomBarTab(this, R.drawable.ic_tab_tool_normal, getString(R.string.tool)))
                        .addItem(new BottomBarTab(this, R.drawable.ic_tab_me_normal, getString(R.string.mine)));
            }
        }
        MainBottomBarUtil.initPageIndex(isAuditMode, isMainTabNewsOpen);

        mBottomBar.setCurrentItem(0);

        showHideFragment(0, -1);

        this.mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {

            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(position, prePosition);
                updateNewsBadgeView(MainActivity.this, position, true);
            }

            @Override
            public void onTabUnselected(int position) {
                updateNewsBadgeView(MainActivity.this, position, false);
            }

            @Override
            public void onTabReselected(int position) {
                if (position == MainBottomBarUtil.getCleanPageIndex() && mMainFragment != null) {
                    mMainFragment.onClickCleanTab();
                }
            }
        });
        DbHelper.copyDb();

        checkReadPermission();

        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //扫描更新系统数据库
        MediaScannerConnection.scanFile(this, new String[]{absolutePath}, null, null);

        //极光推送 设备激活接口
        mPresenter.commitJPushAlias();
        //获取本地推送配置
        mPresenter.getPushSetList();
        //上报设备信息
        if (!PreferenceUtil.getIsPushDeviceInfo()) {//第一次启动上报
            getDeviceInfo();
        }

        //开启定时扫面缓存
//        AlarmTimer.setRepeatingAlarmTimer(this, System.currentTimeMillis(), SCAN_LOOP_TIME, GlobalValues.TIMER_ACTION_REPEATING, AlarmManager.RTC_WAKEUP);
        SPUtil.setInt(this,"createId",SPUtil.getInt(this,"createId",0)+1);
    }

    /**
     * @param context
     * @param position
     * @author xd.he
     */
    private void updateNewsBadgeView(Context context, int position, boolean isSelect) {
        if (!MainBottomBarUtil.isShowNewsTab() || position != MainBottomBarUtil.getNewsPageIndex()) {
            return;
        }
        isSelectTop = isSelect;
        if (isSelect) {
            mHandler.removeCallbacksAndMessages(null);        //清空所有的消息
            hideBadgeView();
        } else {
            //如果没有选中头条，开始10分钟倒记时
            mHandler.removeCallbacksAndMessages(null);        //清空所有的消息
            mHandler.sendEmptyMessageDelayed(MSG_SHOW_NEWS_BADGE_VIEW, DEFAULT_REFRESH_TIME);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isFirstCreate) {
            //检查是否有补丁
//            mPresenter.queryPatch();
            //检测版本更新
            mPresenter.queryAppVersion(() -> {
            });
            //获取WebUrl
            mPresenter.getWebUrl();
            isFirstCreate = false;
        }

    }

    private void checkReadPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED) {
            System.out.println();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.PACKAGE_USAGE_STATS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.PACKAGE_USAGE_STATS}, REQUEST_STORAGE_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.PACKAGE_USAGE_STATS}, REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    /**
     * 检查权限后的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "打开相册失败，请允许存储权限后再试", Toast.LENGTH_SHORT).showShort();
                } else {
                    //TODO 请求权限弹窗 允许后回调返回的成功回调 在此写业务逻辑
                }
                break;
        }
    }

    /**
     * 接收外部跳转参数
     *
     * @param intent
     */
    private void changeTab(Bundle intent) {
        String type = intent.getString("type");
        String home = intent.getString("NotificationService");
        Log.d("MainActivity", "!--->changeTab--type:" + type + "; home:" + home);
        // TODO type 貌似没用到 !!
        if ("shangcheng".equals(type)) {  // ????
            mBottomBar.setCurrentItem(MainBottomBarUtil.getToolPageIndex());
        } else if ("shenghuo".equals(type)) {
            mBottomBar.setCurrentItem(MainBottomBarUtil.getLifePageIndex());
        } else if ("jiekuan".equals(type)) {
            mBottomBar.setCurrentItem(MainBottomBarUtil.getCleanPageIndex());
        } else if ("huodong".equals(type)) {
            mBottomBar.setCurrentItem(MainBottomBarUtil.getNewsPageIndex());
        } else if ("wode".equals(type)) {
            mBottomBar.setCurrentItem(MainBottomBarUtil.getMinePageIndex());
        }
        String tabIndex = intent.getString(SchemeConstant.EXTRA_MAIN_INDEX);
        if (!TextUtils.isEmpty(tabIndex)) {
            try {
                int tab = Integer.parseInt(tabIndex);
                if (tab <= 3) {  // TODO
                    mBottomBar.setCurrentItem(tab);
                } else {
                    if (tab == 4) {
                        mBottomBar.setCurrentItem(MainBottomBarUtil.getCleanPageIndex());
                        EventBus.getDefault().post(new AutoCleanEvent());
                    }
                }
            } catch (Exception e) {
            }
        }

        if ("home".equals(home)) {
            //默认选中主页
            mBottomBar.setCurrentItem(0);
            AppHolder.getInstance().setCleanFinishSourcePageId("toggle_home_click");
            StatisticsUtils.trackClick("toggle_home_click", "常驻通知栏点击主页", "", "toggle_page");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getExtras() != null) {
            changeTab(intent.getExtras());
        }
        super.onNewIntent(intent);
    }

    @Override
    public void netError() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //开启常驻通知栏服务
        if (NotificationsUtils.isNotificationEnabled(this) && PreferenceUtil.getIsNotificationEnabled()) {
            try {
                startService(new Intent(this, NotificationService.class));
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
        mPresenter.saveCacheFiles();
    }

    private void initFragments() {
        mMainFragment = new NewCleanMainFragment();
        ToolFragment toolFragment = new ToolFragment();
        upQuotaFragment = NewsFragment.getNewsFragment("");
        MeFragment mineFragment = MeFragment.getIntance();

        boolean isAuditMode = SPUtil.isInAudit();
        boolean isMainTabNewsOpen = NewsUtils.isMainTabNewsOpen();
        if (isAuditMode) {
            mFragments.add(mMainFragment);
            mFragments.add(mineFragment);

            mManager.beginTransaction()
                    .add(R.id.frame_layout, mMainFragment)
                    .add(R.id.frame_layout, mineFragment)
                    .hide(mMainFragment)
                    .hide(mineFragment)
                    .commitAllowingStateLoss();
        } else {
            if (isMainTabNewsOpen) {
                mFragments.add(mMainFragment);
                mFragments.add(toolFragment);
                mFragments.add(upQuotaFragment);
                mFragments.add(mineFragment);

                mManager.beginTransaction()
                        .add(R.id.frame_layout, mMainFragment)
                        .add(R.id.frame_layout, toolFragment)
                        .add(R.id.frame_layout, upQuotaFragment)
                        .add(R.id.frame_layout, mineFragment)
                        .hide(mMainFragment)
                        .hide(toolFragment)
                        .hide(upQuotaFragment)
                        .hide(mineFragment)
                        .commitAllowingStateLoss();
            } else {
                mFragments.add(mMainFragment);
                mFragments.add(toolFragment);
                mFragments.add(mineFragment);

                mManager.beginTransaction()
                        .add(R.id.frame_layout, mMainFragment)
                        .add(R.id.frame_layout, toolFragment)
                        .add(R.id.frame_layout, mineFragment)
                        .hide(mMainFragment)
                        .hide(toolFragment)
                        .hide(mineFragment)
                        .commitAllowingStateLoss();
            }
        }


    }

    private void showHideFragment(int position, int prePosition) {
        MainBottomBarUtil.onShowHideFragment(this, position, prePosition);
        if (position == MainBottomBarUtil.getMinePageIndex()) {
            source_page = "wode";
        }
        FragmentTransaction ft = mManager.beginTransaction();
        ft.show(mFragments.get(position));
        if (prePosition != -1) {
            ft.hide(mFragments.get(prePosition));
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册订阅者
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        try {
            ViewGroup layout = (ViewGroup) getWindow().getDecorView();
            layout.removeAllViews();
        } catch (Exception e) {
        }
    }

    private long firstTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mFragments.get(mBottomBar.getCurrentItemPosition()) instanceof ShoppingMallFragment) {
                ShoppingMallFragment fragment = (ShoppingMallFragment) mFragments.get(mBottomBar.getCurrentItemPosition());
                fragment.onKeyBack();
                return true;
            } else if (mFragments.get(mBottomBar.getCurrentItemPosition()) instanceof NewCleanMainFragment) {
                NewCleanMainFragment fragment = (NewCleanMainFragment) mFragments.get(mBottomBar.getCurrentItemPosition());
                fragment.onKeyBack();
                return true;
            } else {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
        }
        StatisticsUtils.trackClick("system_return_back", "\"手机返回\"点击", "", "one_click_acceleration_page");
        return super.onKeyDown(keyCode, event);
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //        super.onSaveInstanceState(outState);
    }

    /**
     * 扫描成功
     */
    public void onScanFileSuccess() {
        EventBus.getDefault().post(new FileCleanSizeEvent());
    }

    public interface OnKeyBackListener {
        void onKeyBack();
    }

    @Override
    protected void setStatusBar() {
    }

    /**
     * 重新扫描文件
     */
    @Subscribe
    public void onEventScan(ScanFileEvent scanFileEvent) {
        mPresenter.saveCacheFiles();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    public boolean isBadgeViewShow() {
        if (mNewsBottomBarTab == null) {
            return false;
        }
        return mNewsBottomBarTab.isBadgeViewShow();
    }

    private void showNewsBadgeView() {
        if (isSelectTop || mNewsBottomBarTab == null) {
            return;
        }
        mNewsBottomBarTab.showBadgeView("...");
    }

    public void hideBadgeView() {
        if (mNewsBottomBarTab == null) {
            return;
        }
        if (mNewsBottomBarTab.isBadgeViewShow()) {
            mNewsBottomBarTab.hideBadgeView();
        }
    }

    /**
     * 操作记录(PUSH消息)
     *
     * @param type（1-立即清理 2-一键加速 3-手机清理 4-文件清理 5-微信专清 6-手机降温 7-qq专清 8-通知栏 9- 超强省电）
     */
    public void commitJpushClickTime(int type) {
        mPresenter.commitJpushClickTime(type);
    }

    public void start() {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                // 启动保活服务
                KeepAliveManager.toKeepAlive(
                        getApplication()
                        , POWER_SAVING,  //不使用播放无声音乐方式保活，节省电量
                        mContext.getString(R.string.push_content_default_title),
                        mContext.getString(R.string.push_content_default_content),
                        R.mipmap.applogo,
                        new ForegroundNotification(
                                //定义前台服务的通知点击事件
                                (context, intent) -> Log.d("JOB-->", " foregroundNotificationClick"))
                );
                Map<String, Object> extParam = new HashMap<>();
                extParam.put("creation_id", "creation_0000"+SPUtil.getInt(this,"createId",1));
                StatisticsUtils.customTrackEvent("app_start_creation", "应用冷启动创建时", "app_page", "app_page",extParam);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

    //获取设备信息
    public void getDeviceInfo() {
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE);
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setAndroidId(DeviceUtil.getAndroidId(mContext));
            deviceInfo.setWlanMac(DeviceUtil.getWLANMAC(mContext) + ", " + DeviceUtil.getWLANMACShell());
            deviceInfo.setBtMac(DeviceUtil.getBTMAC());
            deviceInfo.setUdId(DeviceUtil.getPseudoID());
            deviceInfo.setBoard(Build.BOARD);
            deviceInfo.setBrand(Build.BRAND);
            deviceInfo.setCpuAbi(Build.CPU_ABI);
            deviceInfo.setDevice(Build.DEVICE);
            deviceInfo.setDisplay(Build.DISPLAY);
            deviceInfo.setHost(Build.HOST);
            deviceInfo.setPhoneId(Build.ID);
            deviceInfo.setManufacturer(Build.MANUFACTURER);
            deviceInfo.setModel(Build.MODEL);
            deviceInfo.setProduct(Build.PRODUCT);
            deviceInfo.setTags(Build.TAGS);
            deviceInfo.setType(Build.TYPE);
            deviceInfo.setSerial(Build.SERIAL);
            deviceInfo.setUser(Build.USER);
            deviceInfo.setSystemVersion(DeviceUtil.getSystemVersion());
            deviceInfo.setSystemLanguage(Locale.getDefault().getDisplayLanguage());
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                deviceInfo.setDeviceId(DeviceUtil.getDeviceId(mContext));
                deviceInfo.setDeviceId2(DeviceUtil.getDeviceId2(mContext));
            } else {
                //            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                deviceInfo.setDeviceId("");
                deviceInfo.setDeviceId2("");
            }
            mPresenter.pushDeviceInfo(deviceInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
