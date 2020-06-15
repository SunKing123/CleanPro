package com.xiaoniu.cleanking.ui.main.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.AppConfig;
import com.xiaoniu.cleanking.app.ApplicationDelegate;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.base.UmengEnum;
import com.xiaoniu.cleanking.base.UmengUtils;
import com.xiaoniu.cleanking.bean.path.AppPath;
import com.xiaoniu.cleanking.bean.path.UninstallList;
import com.xiaoniu.cleanking.bean.path.UselessApk;
import com.xiaoniu.cleanking.keeplive.KeepAliveManager;
import com.xiaoniu.cleanking.keeplive.config.ForegroundNotification;
import com.xiaoniu.cleanking.room.clean.UninstallListDao;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.ui.localpush.RomUtils;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.DeviceInfo;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.IconsEntity;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.bean.RedPacketEntity;
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
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.ui.newclean.fragment.NewCleanMainFragment;
import com.xiaoniu.cleanking.ui.news.fragment.NewsFragment;
import com.xiaoniu.cleanking.ui.notifition.NotificationService;
import com.xiaoniu.cleanking.utils.AppLifecycleUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NotificationsUtils;
import com.xiaoniu.cleanking.utils.db.RoomHelper;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.quick.QuickUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.DeviceUtil;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import cn.jzvd.Jzvd;

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
    private NewsFragment upQuotaFragment;
    private static final long DEFAULT_REFRESH_TIME = 10 * 60 * 1000L;
    /**
     * 定时扫面手机时间 1小时
     */
    private static final long SCAN_LOOP_TIME = 3 * 1000;

    /**
     * 借款页
     */
    public static int CLEAN = 0;
    /**
     * 商城页
     */
    public static int TOOL = 1;
    /**
     * 生活页
     */
    public static int LIFE = -1;
    /**
     * 提额
     */
    public static int NEWS = 2;
    /**
     * 我的页面
     */
    public static int MINE = 3;

    @Inject
    NoClearSPHelper mSPHelper;

    //判断重新启动
    boolean isFirstCreate = false;
    private int mCurrentPosition = 1;
    private boolean mIsBack; //mIsBack = true 记录当前已经进入后台
    private boolean mShowRedFirst; //红包是否已经展示
    private BottomBarTab mBottomBarTab;
    private boolean isSelectTop = false;
    private NewCleanMainFragment mainFragment;
    private final String TAG = "GeekSdk";
//    private MyHandler mHandler = new MyHandler(this);

  /*  private class MyHandler extends Handler {
        WeakReference<Activity> mActivity;

        public MyHandler(Activity con) {
            this.mActivity = new WeakReference<>(con);
        }

        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (isSelectTop)
                    return;
                if (mBottomBarTab != null) {
                    mBottomBarTab.showBadgeView("...");
                }
            }
        }
    }*/

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
//        RoomHelper.getInstance();
//        List<UninstallList> uselessApks = ApplicationDelegate.getAppPathDatabase().uninstallListDao().getAll();
//        List<AppPath> appPaths = ApplicationDelegate.getAppPathDatabase().cleanPathDao().getAll();
//        List<UselessApk> uselessApks1 = ApplicationDelegate.getAppPathDatabase().uselessApkDao().getAll();
//        LogUtils.i("zz---db---"+ uselessApks.size()+"---"+appPaths.size()+"---"+uselessApks1.size());
        PreferenceUtil.saveShowAD(false);
        getIconListFail();
        mPresenter.getIconList();
//        mHandler.sendEmptyMessageDelayed(1, DEFAULT_REFRESH_TIME);
        isFirstCreate = true;
        initFragments();
        CLEAN = 0;
        TOOL = 1;
        NEWS = 2;
        MINE = 3;
        showHideFragment(0, -1);

        this.mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {

            @Override
            public void onTabSelected(int position, int prePosition) {
                mCurrentPosition = position + 1;
                showHideFragment(position, prePosition);
                //如果没有选中头条，开始10分钟记时
                if (position == 2) {
                    isSelectTop = true;
                    hideBadgeView();
                } else {
                    if (isSelectTop) {
                        isSelectTop = false;
                        //清空所有的消息
//                        mHandler.removeCallbacksAndMessages(null);
//                        mHandler.sendEmptyMessageDelayed(1, DEFAULT_REFRESH_TIME);
                    }
                }

                if (null == AppHolder.getInstance().getRedPacketEntityList()
                        || null == AppHolder.getInstance().getRedPacketEntityList().getData()
                        || AppHolder.getInstance().getRedPacketEntityList().getData().size() <= 0
                        || null == AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getImgUrls()
                        || AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getImgUrls().size() <= 0)
                    return;
                if (!mShowRedFirst && mCurrentPosition == AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getLocation()) {
                    showRedPacket(AppHolder.getInstance().getRedPacketEntityList());
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });

        checkReadPermission();

       /* String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //扫描更新系统数据库
        MediaScannerConnection.scanFile(this, new String[]{absolutePath}, null, null);*/

        //极光推送 设备激活接口
        mPresenter.commitJPushAlias();
        //获取本地推送配置
     //   mPresenter.getPushSetList();
        //从服务器获取本地推送的配置信息
        mPresenter.getLocalPushConfigFromServer();
        //上报设备信息
        if (!PreferenceUtil.getIsPushDeviceInfo()) {//第一次启动上报
            getDeviceInfo();
        }
        //初始插屏广告开关
        mPresenter.getScreentSwitch();
        //获取定位权限
        mPresenter.requestPhoneStatePermission();
        //测试入口
//        if (BuildConfig.DEBUG) {
//            AppConfig.showDebugWindow(mContext);
//        }
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
        if ("shangcheng".equals(type)) {
            mBottomBar.setCurrentItem(TOOL);
        } else if ("shenghuo".equals(type)) {
            mBottomBar.setCurrentItem(LIFE);
        } else if ("jiekuan".equals(type)) {
            mBottomBar.setCurrentItem(CLEAN);
        } else if ("huodong".equals(type)) {
            mBottomBar.setCurrentItem(NEWS);
        } else if ("wode".equals(type)) {
            mBottomBar.setCurrentItem(MINE);
        }
        String tabIndex = intent.getString(SchemeConstant.EXTRA_MAIN_INDEX);
        if (!TextUtils.isEmpty(tabIndex)) {
            try {
                int tab = Integer.parseInt(tabIndex);
                if (tab <= 3) {
                    mBottomBar.setCurrentItem(tab);
                } else {
                    if (tab == 4) {
                        mBottomBar.setCurrentItem(CLEAN);
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
    protected void onRestart() {
        super.onRestart();
        if (null == AppHolder.getInstance().getRedPacketEntityList()
                || null == AppHolder.getInstance().getRedPacketEntityList().getData()
                || AppHolder.getInstance().getRedPacketEntityList().getData().size() <= 0
                || null == AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getImgUrls()
                || AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getImgUrls().size() <= 0)
            return;
        if (mIsBack && mCurrentPosition == AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getLocation()) {
            showRedPacket(AppHolder.getInstance().getRedPacketEntityList());
            mIsBack = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null == AppHolder.getInstance().getRedPacketEntityList()
                || null == AppHolder.getInstance().getRedPacketEntityList().getData()
                || AppHolder.getInstance().getRedPacketEntityList().getData().size() <= 0
                || null == AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getImgUrls()
                || AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getImgUrls().size() <= 0)
            return;
        if (!AppLifecycleUtil.isAppOnForeground(this)
                && mCurrentPosition == AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getLocation()) {
            //app 进入后台
            mIsBack = true;
        }
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
        mPresenter.saveCacheFiles();
    }

    private void initFragments() {

        MeFragment mineFragment = MeFragment.getIntance();
        mainFragment = new NewCleanMainFragment();
        String url = ApiModule.SHOPPING_MALL;

        ToolFragment toolFragment = new ToolFragment();
        upQuotaFragment = NewsFragment.getNewsFragment("");
        mFragments.add(mainFragment);
        //        状态（0=隐藏，1=显示）
        String auditSwitch = SPUtil.getString(MainActivity.this, AppApplication.AuditSwitch, "1");
        if (TextUtils.equals(auditSwitch, "1")) {
            mFragments.add(toolFragment);
            mFragments.add(upQuotaFragment);
//            enableOtherComponent();
        }
        mFragments.add(mineFragment);

        mManager.beginTransaction()
                .add(R.id.frame_layout, mainFragment)
                .add(R.id.frame_layout, toolFragment)
                .add(R.id.frame_layout, upQuotaFragment)
                .add(R.id.frame_layout, mineFragment)
                .hide(mainFragment)
                .hide(toolFragment)
                .hide(upQuotaFragment)
                .hide(mineFragment)
                .commitAllowingStateLoss();

    }

    private void showHideFragment(int position, int prePosition) {
        String eventCode = "home_click";
        String currentPage = "";
        String sourcePage = "";
        if (position == 0) {
            eventCode = "home_click";
            currentPage = "home_page";
        } else if (position == 1) {
            eventCode = "tool_click";
            currentPage = "tool_page";
        } else if (position == 2) {
            eventCode = "selected_click";
            currentPage = "selected_page";
        } else if (position == 3) {
            eventCode = "mine_click";
            currentPage = "mine_page";
        }
        if (prePosition == 0) {
            sourcePage = "home_page";
        } else if (prePosition == 1) {
            sourcePage = "tool_page";
        } else if (prePosition == 2) {
            sourcePage = "selected_page";
        } else if (prePosition == 3) {
            sourcePage = "mine_page";
        }
        //保存选中的currentPage 为 上级页面id
        AppHolder.getInstance().setSourcePageId(currentPage);
        //默认二级选中页面为当前页面
        AppHolder.getInstance().setOtherSourcePageId(currentPage);
        StatisticsUtils.trackClick(eventCode, "底部icon点击", sourcePage, currentPage);
        if (position == MINE)
            source_page = "wode";
        if (position == CLEAN) {
            UmengUtils.event(MainActivity.this, UmengEnum.Tab_jiekuan);
        } else if (position == MINE) {
            UmengUtils.event(MainActivity.this, UmengEnum.Tab_wode);
        } else if (position == NEWS) {
        } else if (position == TOOL) {
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
            if (Jzvd.backPress()) {
                return true;
            }
            if (mFragments.get(mBottomBar.getCurrentItemPosition()) instanceof ShoppingMallFragment) {
                ShoppingMallFragment fragment = (ShoppingMallFragment) mFragments.get(mBottomBar.getCurrentItemPosition());
                fragment.onKeyBack();
                return true;
            } else if (mFragments.get(mBottomBar.getCurrentItemPosition()) instanceof NewCleanMainFragment) {
                NewCleanMainFragment fragment = (NewCleanMainFragment) mFragments.get(mBottomBar.getCurrentItemPosition());
                fragment.onKeyBack();
                return true;
            } else {
//                long secondTime = System.currentTimeMillis();
//                if (secondTime - firstTime > 1500) {
//                    // 如果两次按键时间间隔大于800毫秒，则不退出
//                    Toast.makeText(getApplicationContext(), R.string.press_exit_again, Toast.LENGTH_SHORT).show();
//                    // 更新firstTime
//                    firstTime = secondTime;
//                    Intent intent = new Intent();
//                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//                    return true;
//                } else {
//
//                    //如果审核满足答题条件时自动跳转答题页面，返回则不跳
//                    SPUtil.setInt(MainActivity.this, "turnask", 0);
//                    SPUtil.setBoolean(MainActivity.this, "firstShowHome", false);
//                    AppManager.getAppManager().clearStack();
//                }
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
        RomUtils.onActivityResult(this, requestCode, resultCode, data);
    }

    public boolean isBadgeViewShow() {
        return mBottomBarTab.isBadgeViewShow();
    }

    public void hideBadgeView() {
        mBottomBarTab.hideBadgeView();
    }

    /**
     * 操作记录(PUSH消息)
     *
     * @param type（1-立即清理 2-一键加速 3-手机清理 4-文件清理 5-微信专清 6-手机降温 7-qq专清 8-通知栏 9- 超强省电  10-病毒查杀 11-游戏加速  12-网络加速 ）
     */
    public void commitJpushClickTime(int type) {
        mPresenter.commitJpushClickTime(type);
    }

    @Override
    protected void onPause() {
        //停止所有视频播放
        Jzvd.releaseAllVideos();
        super.onPause();
    }


    public void start() {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                //启动保活服务
                KeepAliveManager.toKeepAlive(
                        getApplication()
                        , POWER_SAVING,
                        mContext.getString(R.string.push_content_default_title),
                        mContext.getString(R.string.push_content_default_content),
                        R.mipmap.applogo,
                        new ForegroundNotification(
                                //定义前台服务的通知点击事件
                                (context, intent) -> Log.d("JOB-->", " foregroundNotificationClick"))
                );
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

    /**
     * 获取红包成功
     *
     * @param redPacketEntity
     */
    public void getRedPacketListSuccess(RedPacketEntity redPacketEntity) {
        if (PreferenceUtil.isHaseUpdateVersion() || null == redPacketEntity || null == redPacketEntity.getData()
                || redPacketEntity.getData().size() <= 0 || null == redPacketEntity.getData().get(0).getImgUrls()
                || redPacketEntity.getData().get(0).getImgUrls().size() <= 0)
            return;
        if (redPacketEntity.getData().get(0).getLocation() == 0
                || redPacketEntity.getData().get(0).getLocation() == 1
                || redPacketEntity.getData().get(0).getLocation() == 5) {
            showRedPacket(redPacketEntity);
        }
    }

    /**
     * 展示红包
     */
    private void showRedPacket(RedPacketEntity redPacketEntity) {
        if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_3G
                || NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_2G
                || NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO)
            return;
        if (redPacketEntity.getData().get(0).getTrigger() == 0
                || PreferenceUtil.getRedPacketShowCount() % redPacketEntity.getData().get(0).getTrigger() == 0) {
            mShowRedFirst = true;
            if (!isFinishing()) {
                startActivity(new Intent(this, RedPacketHotActivity.class));
            }
        }
    }

    /**
     * 获取底部icon成功
     *
     * @param iconsEntity
     */
    public void getIconListSuccess(IconsEntity iconsEntity) {
        if (null == iconsEntity || null == iconsEntity.getData() || iconsEntity.getData().size() <= 0)
            return;
        mBottomBar.removeAllTabs();
        String auditSwitch = SPUtil.getString(MainActivity.this, AppApplication.AuditSwitch, "1");
        if (TextUtils.equals(auditSwitch, "0")) {
            mBottomBar
                    .addItem(new BottomBarTab(this, R.drawable.msg_normal, iconsEntity.getData().get(0).getIconImgUrl()
                            , iconsEntity.getData().get(0).getTabName()
                            , iconsEntity.getData().get(0).getOrderNum()))
                    .addItem(new BottomBarTab(this, R.drawable.msg_normal, iconsEntity.getData().get(3).getIconImgUrl()
                            , iconsEntity.getData().get(3).getTabName()
                            , iconsEntity.getData().get(3).getOrderNum()));
            mBottomBar.setCurrentItem(0);
        } else {
            if (iconsEntity.getData().size() >= 4) {
                mBottomBarTab = new BottomBarTab(this, R.drawable.msg_normal, iconsEntity.getData().get(2).getIconImgUrl()
                        , iconsEntity.getData().get(2).getTabName()
                        , iconsEntity.getData().get(2).getOrderNum());
                mBottomBar
                        .addItem(new BottomBarTab(this, R.drawable.msg_normal, iconsEntity.getData().get(0).getIconImgUrl()
                                , iconsEntity.getData().get(0).getTabName()
                                , iconsEntity.getData().get(0).getOrderNum()))
                        .addItem(new BottomBarTab(this, R.drawable.msg_normal, iconsEntity.getData().get(1).getIconImgUrl()
                                , iconsEntity.getData().get(1).getTabName()
                                , iconsEntity.getData().get(1).getOrderNum()))
                        .addItem(mBottomBarTab)
                        .addItem(new BottomBarTab(this, R.drawable.msg_normal, iconsEntity.getData().get(3).getIconImgUrl()
                                , iconsEntity.getData().get(3).getTabName()
                                , iconsEntity.getData().get(3).getOrderNum()));
                mBottomBar.setCurrentItem(0);
            }
        }
    }

    /**
     * 获取底部icon失败
     */
    public void getIconListFail() {
        //        状态（0=隐藏，1=显示）
        String auditSwitch = SPUtil.getString(MainActivity.this, AppApplication.AuditSwitch, "1");
        if (TextUtils.equals(auditSwitch, "0")) {
            mBottomBar
                    .addItem(new BottomBarTab(this, R.drawable.clean_normal, "", getString(R.string.clean), 0))
                    .addItem(new BottomBarTab(this, R.drawable.me_normal, "", getString(R.string.mine), 0));
        } else {
            mBottomBarTab = new BottomBarTab(this, R.drawable.msg_normal, "", getString(R.string.top), 0);
            mBottomBar
                    .addItem(new BottomBarTab(this, R.drawable.clean_normal, "", getString(R.string.clean), 0))
                    .addItem(new BottomBarTab(this, R.drawable.tool_normal, "", getString(R.string.tool), 0))
                    .addItem(mBottomBarTab)
                    .addItem(new BottomBarTab(this, R.drawable.me_normal, "", getString(R.string.mine), 0));
        }
        mBottomBar.setCurrentItem(0);
    }


    //桌面创建图标
    private void enableOtherComponent() {
        ComponentName apple = new ComponentName(getApplication(), "com.xiaoniu.cleanking.wx");
        QuickUtils.getInstant(this).enableComponent(apple);
    }


}
