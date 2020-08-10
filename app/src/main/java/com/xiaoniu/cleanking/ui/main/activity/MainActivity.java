package com.xiaoniu.cleanking.ui.main.activity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppLifecyclesImpl;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.base.UmengEnum;
import com.xiaoniu.cleanking.base.UmengUtils;
import com.xiaoniu.cleanking.bean.HotStartAction;
import com.xiaoniu.cleanking.bean.PopupWindowType;
import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.constant.RouteConstants;
import com.xiaoniu.cleanking.keeplive.KeepAliveManager;
import com.xiaoniu.cleanking.keeplive.config.ForegroundNotification;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.deskpop.base.DeskPopLogger;
import com.xiaoniu.cleanking.ui.deskpop.battery.BatteryPopActivity;
import com.xiaoniu.cleanking.ui.deskpop.deviceinfo.ExternalPhoneStateActivity;
import com.xiaoniu.cleanking.ui.deskpop.wifi.ExternalSceneActivity;
import com.xiaoniu.cleanking.ui.localpush.LocalPushDispatcher;
import com.xiaoniu.cleanking.ui.main.bean.ExitRetainEntity;
import com.xiaoniu.cleanking.ui.main.bean.IconsEntity;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.RedPacketEntity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.dialog.ExitRetainDialog;
import com.xiaoniu.cleanking.ui.main.event.AutoCleanEvent;
import com.xiaoniu.cleanking.ui.main.event.FileCleanSizeEvent;
import com.xiaoniu.cleanking.ui.main.event.ScanFileEvent;
import com.xiaoniu.cleanking.ui.main.event.SwitchTabEvent;
import com.xiaoniu.cleanking.ui.main.fragment.ShoppingMallFragment;
import com.xiaoniu.cleanking.ui.main.fragment.ToolFragment;
import com.xiaoniu.cleanking.ui.main.presenter.MainPresenter;
import com.xiaoniu.cleanking.ui.main.widget.BottomBar;
import com.xiaoniu.cleanking.ui.main.widget.BottomBarTab;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.newclean.fragment.MineFragment;
import com.xiaoniu.cleanking.ui.newclean.fragment.NewPlusCleanMainFragment;
import com.xiaoniu.cleanking.ui.newclean.fragment.YuLeFragment;
import com.xiaoniu.cleanking.ui.newclean.model.PopEventModel;
import com.xiaoniu.cleanking.ui.newclean.util.ScrapingCardDataUtils;
import com.xiaoniu.cleanking.ui.notifition.NotificationService;
import com.xiaoniu.cleanking.ui.tool.notify.event.HotStartEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.WeatherInfoRequestEvent;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.AppLifecycleUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NotchUtils;
import com.xiaoniu.cleanking.utils.NotificationsUtils;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.quick.QuickUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.DateUtils;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.Points;
import com.xiaoniu.common.utils.StatisticsUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;
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
    private NewPlusCleanMainFragment mainFragment;
    private final String TAG = "GeekSdk";


    /**
     * 消息Id
     **/
    private static final String KEY_MSGID = "msg_id";
    /**
     * 该通知的下发通道
     **/
    private static final String KEY_WHICH_PUSH_SDK = "rom_type";
    /**
     * 通知标题
     **/
    private static final String KEY_TITLE = "n_title";
    /**
     * 通知内容
     **/
    private static final String KEY_CONTENT = "n_content";
    /**
     * 通知附加字段
     **/
    private static final String KEY_EXTRAS = "n_extras";

    private MyRunnable myRunnable = new MyRunnable();

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }


    private void parsePushData(Intent intent) {
        if (intent == null) {
            return;
        }
        String uriData = intent.getStringExtra("push_uri");
        if (!TextUtils.isEmpty(uriData)) {
            try {
                JSONObject jsonObject = new JSONObject(uriData);
                String msgId = jsonObject.optString(KEY_MSGID);
                byte whichPushSDK = (byte) jsonObject.optInt(KEY_WHICH_PUSH_SDK);
                String title = jsonObject.optString(KEY_TITLE);
                String content = jsonObject.optString(KEY_CONTENT);
                String extras = jsonObject.optString(KEY_EXTRAS);
                //上报点击事件
                LogUtils.e("=====点击上报成功 msgId:" + msgId + " whichPushSDK:" + whichPushSDK);
                JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK);
                jsonObject = new JSONObject(extras);
                String schema = jsonObject.getString("url");
                if (!TextUtils.isEmpty(schema)) {
                    SchemeProxy.openScheme(this, schema);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void initView() {
        parsePushData(getIntent());
        refBottomState();
        mPresenter.getIconList();
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
                    }
                }

                switch (position) {
                    case 0:
                        StatisticsUtils.trackClick(Points.Tab.CLEAN_CLICK_CODE, Points.Tab.CLEAN_CLICK_NAME, "home_page", "home_page");
                        break;
                    case 1:
                        StatisticsUtils.trackClick(Points.Tab.TOOLBOX_CLICK_CODE, Points.Tab.TOOLBOX_CLICK_NAME, "home_page", "home_page");
                        break;
                    case 2:
                        StatisticsUtils.trackClick(Points.Tab.SCRAPING_CARD_CLICK_CODE, Points.Tab.SCRAPING_CARD_CLICK_NAME, "home_page", "home_page");
                        break;
                    case 3:
                        StatisticsUtils.trackClick(Points.Tab.MINE_CLICK_CODE, Points.Tab.MINE_CLICK_NAME, "home_page", "home_page");
                        break;
                }

                RedPacketEntity.DataBean redPacketDataBean = AppHolder.getInstance().getPopupDataFromListByType(
                        AppHolder.getInstance().getPopupDataEntity(), PopupWindowType.POPUP_RED_PACKET
                );
                if (null == redPacketDataBean || null == redPacketDataBean.getImgUrls() || redPacketDataBean.getImgUrls().size() <= 0)
                    return;
                if (!mShowRedFirst && mCurrentPosition == redPacketDataBean.getLocation()) {
                    showRedPacket(redPacketDataBean);
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });

        // checkReadPermission();
        //极光推送 设备激活接口
        mPresenter.commitJPushAlias();

        //启动本地推送服务的Service(仅针对非华为手机的设备启动，因为在非华为设备在保活进程没有做适配)
      /*  if (!RomUtils.checkIsHuaWeiRom()) {
            LogUtils.e("====非华为设备，启动推送Service");
            startService(new Intent(this, LocalPushService.class));
        }*/
        //上报设备信息
//        if (!PreferenceUtil.getIsPushDeviceInfo()) {//第一次启动上报
//            getDeviceInfo();
//        }

        AndroidUtil.haveLiuhai = NotchUtils.hasNotchScreen(this);
        //游客登录
        mPresenter.visitorLogin();

        AppLifecyclesImpl.postDelay(myRunnable, 2000);

        mainFragment.setOnInteractiveClickListener(v -> {
            AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
            StatisticsUtils.trackClick("scraping_buoy_click", "首页刮刮卡浮标点击", "home_page", "home_page");
            mBottomBar.setCurrentItem(2);
        });

        mainFragment.setOnWithDrawClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
                StatisticsUtils.trackClick("withdrawal_click", "在首页点击提现", "home_page", "home_page");
                mBottomBar.setCurrentItem(3);
            }
        });
    }


    private class MyRunnable implements Runnable {

        @Override
        public void run() {
            //从服务器获取本地推送的配置信息
            mPresenter.getLocalPushConfigFromServer();
            //初始插屏广告开关
            mPresenter.getScreenSwitch();
            //弹窗信息接口
            mPresenter.getPopupData();
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        LogUtils.i("zzz-----" + hasFocus);
        if (hasFocus && isFirstCreate) {
            //检测版本更新
            mPresenter.queryAppVersion();

            isFirstCreate = false;
        }
        //fragment中传递Focus方法；
        if (mainFragment != null) {
            mainFragment.onWindowFocusChanged(hasFocus);
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
            //跳转刮刮卡
            if (ScrapingCardDataUtils.init().getCardsListSize() > 0) {
                ScrapingCardDataUtils.init().scrapingCardNextAction(this,false);
            }
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
        boolean backFromFinish = intent.getBooleanExtra("back_from_finish", false);
        LogUtils.e("============从完成页返回的:" + backFromFinish);
        if (backFromFinish) {
            StatisticsUtils.customTrackEvent("ad_request_sdk_5", "功能完成页广告位5发起请求", "", "success_page");
            InsertAdSwitchInfoList.DataBean configBean = AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_FINISH_PAGE_BACK_SCREEN);
            if (configBean != null && configBean.isOpen()) {
                AppLifecyclesImpl.postDelay(() -> {
                    mPresenter.showInsideScreenDialog(MidasConstants.MAIN_FINISH_PAGE_BACK);
                }, 1000);
            }
        }
        parsePushData(intent);
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
//                //多次重启暂时会出现ANR,已解决，是NotificationService中没有添加startForeground方法
//                Intent intent = new Intent(this, NotificationService.class);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    startForegroundService(intent);
//                } else {
//                    startService(intent);
//                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        RedPacketEntity.DataBean redPacketDataBean = AppHolder.getInstance().getPopupDataFromListByType(
                AppHolder.getInstance().getPopupDataEntity(), PopupWindowType.POPUP_RED_PACKET
        );
        if (null == redPacketDataBean || null == redPacketDataBean.getImgUrls() || redPacketDataBean.getImgUrls().size() <= 0)
            return;
        if (mIsBack && mCurrentPosition == redPacketDataBean.getLocation()) {
            showRedPacket(redPacketDataBean);
            mIsBack = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        RedPacketEntity.DataBean redPacketDataBean = AppHolder.getInstance().getPopupDataFromListByType(
                AppHolder.getInstance().getPopupDataEntity(), PopupWindowType.POPUP_RED_PACKET
        );
        if (null == redPacketDataBean || null == redPacketDataBean.getImgUrls() || redPacketDataBean.getImgUrls().size() <= 0)
            return;
        if (!AppLifecycleUtil.isAppOnForeground(this) && mCurrentPosition == redPacketDataBean.getLocation()) {
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
//        MeFragment mineFragment = MeFragment.getIntance();
        mainFragment = new NewPlusCleanMainFragment();

        ToolFragment toolFragment = new ToolFragment();
        MineFragment fourFragment = MineFragment.getInstance();
//        NewsFragment upQuotaFragment = NewsFragment.getNewsFragment("");
        YuLeFragment upQuotaFragment = YuLeFragment.getInstance();
        mFragments.add(mainFragment);
        //        状态（0=隐藏，1=显示）
        String auditSwitch = SPUtil.getString(MainActivity.this, SpCacheConfig.AuditSwitch, "1");
        if (TextUtils.equals(auditSwitch, "1")) {
            mFragments.add(toolFragment);
            mFragments.add(upQuotaFragment);
//            enableOtherComponent();
        }
        mFragments.add(fourFragment);

        mManager.beginTransaction()
                .add(R.id.frame_layout, mainFragment)
                .add(R.id.frame_layout, toolFragment)
                .add(R.id.frame_layout, upQuotaFragment)
                .add(R.id.frame_layout, fourFragment)
                .hide(mainFragment)
                .hide(toolFragment)
                .hide(upQuotaFragment)
                .hide(fourFragment)
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
        // StatisticsUtils.trackClick(eventCode, "底部icon点击", sourcePage, currentPage);
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
        if (position < mFragments.size()) {
            ft.show(mFragments.get(position));
            if (prePosition != -1 && prePosition<mFragments.size()) {
                ft.hide(mFragments.get(prePosition));
            }
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册订阅者
        EventBus.getDefault().register(this);
        StatusBarCompat.translucentStatusBarForImage(this, true, true);
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
        AppLifecyclesImpl.removeTask(myRunnable);


    }


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
            } else {
                if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                        && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                    SwitchInfoList.DataBean dataBean = null;
                    for (SwitchInfoList.DataBean switchInfo : AppHolder.getInstance().getSwitchInfoList().getData()) {
                        if (PositionId.KEY_PAGE_EXIT_RETAIN.equals(switchInfo.getConfigKey())) {
                            dataBean = switchInfo;
                        }
                    }
                    // LogUtils.e("========dataBen:"+new Gson().toJson(dataBean));
                    if (dataBean != null && dataBean.isOpen()) {
                        RedPacketEntity.DataBean data = AppHolder.getInstance().getPopupDataFromListByType(AppHolder.getInstance().getPopupDataEntity(), PopupWindowType.POPUP_RETAIN_WINDOW);
                        //LogUtils.e("=======server data:" + new Gson().toJson(data));
                        if (data != null) {
                            //判断有没有超过当日限定的次数,小于次数过时行判断，大于次数直接退出
                            ExitRetainEntity alreadyExit = PreferenceUtil.getPressBackExitAppCount();
                            // LogUtils.e("=======alreadyExit:" + new Gson().toJson(alreadyExit));
                            long currentTime = System.currentTimeMillis();
                            if (DateUtils.isSameDay(currentTime, alreadyExit.getLastTime())) {
                                //当dayLimit为0的时候不判断最大次数这个条件
                                if (data.getDailyLimit() > 0 && alreadyExit.getPopupCount() >= data.getDailyLimit()) {
                                    // LogUtils.e("=======alreadyExit:是同一天，但是已经超过了最大次数");
                                    //如果已经超过当天的次数，则应该直接退出并更新当天的次数
                                    changeBackCountAndGoHome(true);
                                } else {
                                    // LogUtils.e("=======alreadyExit:是同一天，没有超过最大次数");
                                    int serverConfig = data.getTrigger();
                                    if (serverConfig == 0) {
                                        ExitRetainDialog retainDialog = new ExitRetainDialog(this);
                                        retainDialog.show();
                                        return true;
                                    } else {
                                        if ((alreadyExit.getBackTotalCount() + 1) % serverConfig == 0) {
                                            //  LogUtils.e("=======是倍数,弹框返回");
                                            ExitRetainDialog retainDialog = new ExitRetainDialog(this);
                                            retainDialog.show();
                                            return true;
                                        } else {
                                            //   LogUtils.e("=======不是倍数，不弹，直接返回");
                                            //更新按返回键退出程序的次数
                                            changeBackCountAndGoHome(true);
                                        }
                                    }
                                }
                            } else {
                                // LogUtils.e("=======不是同一天弹，直接返回,同时重置次数");
                                //不是同一天的话重新统计次数
                                changeBackCountAndGoHome(false);
                            }

                        } else {
                            // LogUtils.e("=======服务器配置为空直接返回");
                            //服务器的配置为空
                            changeBackCountAndGoHome(true);
                        }


                    } else {
                        goHome();
                    }


                } else {
                    goHome();
                }

            }
            StatisticsUtils.trackClick("system_return_back", "\"手机返回\"点击", "", "one_click_acceleration_page");
        }
        return super.onKeyDown(keyCode, event);
    }


    private void changeBackCountAndGoHome(boolean isOneDay) {
        if (isOneDay) {
            PreferenceUtil.updatePressBackExitAppCount(false);
        } else {
            PreferenceUtil.resetPressBackExitAppCount();
        }
        goHome();
    }

    private void goHome() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
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


    //热启
    @Subscribe
    public void onEventHotStart(HotStartEvent event) {
        if (event.getAction() == HotStartAction.RED_PACKET) {
            startActivity(new Intent(this, RedPacketHotActivity.class));
        } else if (event.getAction() == HotStartAction.INSIDE_SCREEN) {
            AppLifecyclesImpl.postDelay(() -> {

                mPresenter.showInsideScreenDialog(MidasConstants.MAIN_INSIDE_SCREEN_ID);
            }, 1000);
        }
    }


    //请求天气信息
    @Subscribe
    public void onEventWeatherInfo(WeatherInfoRequestEvent infotype) {
        if (infotype.getAction() == 0) {
            mPresenter.requestWeatherVideo(Constant.DEFAULT_AREA_CODE_OF_WEATHER);
        }
    }

    @Subscribe
    public void onEventWifiConnection(PopEventModel popEventModel) {
        DeskPopLogger.log("======MainActivity before  的event bus:" + popEventModel.getAction());
        if (TextUtils.isEmpty(popEventModel.getAction()) || AppLifecycleUtil.isAppOnForeground(mContext))
            return;
        DeskPopLogger.log("======MainActivity 的event bus:" + popEventModel.getAction());
        if (popEventModel.getAction().equals("wifi")) {
            StatisticsUtils.customTrackEvent("wifi_plug_screen_meets_opportunity", "wifi插屏满足时机", "wifi_plug_screen", "wifi_plug_screen");
            startPopActivity(ExternalSceneActivity.class);
        } else if (popEventModel.getAction().equals("power")) {
            startPopActivity(BatteryPopActivity.class);
        } else if (popEventModel.getAction().equals("deviceInfo")) {
            startPopActivity(ExternalPhoneStateActivity.class);
        } else if (popEventModel.getAction().equals("localPush")) {
            LocalPushDispatcher dispatcher = new LocalPushDispatcher(this);
            dispatcher.showLocalPushDialog();
        } else if (popEventModel.getAction().equals("desktopPop")) {
            LocalPushDispatcher dispatcher = new LocalPushDispatcher(this);
            dispatcher.startDialogActivityOnLauncher();
        }
    }

    //切换tab的监听
    @Subscribe
    public void onEventTabSwitch(SwitchTabEvent tabEvent) {
        if (tabEvent != null) {
            mBottomBar.setCurrentItem(tabEvent.tabPosition);
        }
    }


    private void startPopActivity(Class<? extends AppCompatActivity> target) {
        if (ActivityCollector.hasExternalActivity()) {
            return;
        }
        Intent powerIntent = new Intent(this, target);
        powerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        powerIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        powerIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        powerIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        PendingIntent intent = PendingIntent.getActivity(this, 0, powerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            intent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        // RomUtils.onActivityResult(this, requestCode, resultCode, data);
    }

    public boolean isBadgeViewShow() {
        return mBottomBarTab == null ? false : mBottomBarTab.isBadgeViewShow();
    }

    public void hideBadgeView() {
        if (mBottomBarTab != null)
            mBottomBarTab.hideBadgeView();
    }

    /**
     * 底部tabview获取
     *
     * @return
     */
    public BottomBar getCardTabView() {
        return mBottomBar;
    }


    /**
     * 操作记录(PUSH消息)
     *
     * @param type（1-立即清理 2-一键加速 3-手机清理 4-文件清理 5-微信专清 6-手机降温 7-qq专清 8-通知栏 9- 超强省电  10-病毒查杀 11-游戏加速  12-网络加速 ）
     */
    @Deprecated
    public void commitJpushClickTime(int type) {
        mPresenter.commitJpushClickTime(type);
    }

    @Override
    protected void onPause() {
        //停止所有视频播放
        Jzvd.releaseAllVideos();
        super.onPause();
    }


    public void startKeepLive() {
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

    /**
     * 获取红包成功
     *
     * @param redPacketEntity 红包相关数据
     */
    public void getRedPacketListSuccess(RedPacketEntity.DataBean redPacketEntity) {
        if (PreferenceUtil.isHaseUpdateVersion() || null == redPacketEntity
                || null == redPacketEntity.getImgUrls()
                || redPacketEntity.getImgUrls().size() <= 0)
            return;
        if (redPacketEntity.getLocation() == 0
                || redPacketEntity.getLocation() == 1
                || redPacketEntity.getLocation() == 5) {

            showRedPacket(redPacketEntity);

        }
    }

    /**
     * 展示红包
     */
    private void showRedPacket(RedPacketEntity.DataBean data) {
        if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_3G
                || NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_2G
                || NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO)
            return;
        if (data.getTrigger() == 0
                || PreferenceUtil.getRedPacketShowCount() % data.getTrigger() == 0) {
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
        String auditSwitch = SPUtil.getString(MainActivity.this, SpCacheConfig.AuditSwitch, "1");
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
                mBottomBar
                        .addItem(new BottomBarTab(this, R.drawable.msg_normal, iconsEntity.getData().get(0).getIconImgUrl()
                                , iconsEntity.getData().get(0).getTabName()
                                , iconsEntity.getData().get(0).getOrderNum()))
                        .addItem(new BottomBarTab(this, R.drawable.msg_normal, iconsEntity.getData().get(1).getIconImgUrl()
                                , iconsEntity.getData().get(1).getTabName()
                                , iconsEntity.getData().get(1).getOrderNum()))
                        .addItem(new BottomBarTab(this, R.drawable.icon_scratch_tab, iconsEntity.getData().get(2).getIconImgUrl()
                                , iconsEntity.getData().get(2).getTabName()
                                , iconsEntity.getData().get(2).getOrderNum()))
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
    public void refBottomState() {
        //        状态（0=隐藏，1=显示）
        String auditSwitch = SPUtil.getString(MainActivity.this, SpCacheConfig.AuditSwitch, "1");
        if (TextUtils.equals(auditSwitch, "0")) {
            mBottomBar
                    .addItem(new BottomBarTab(this, R.drawable.clean_normal, "", getString(R.string.clean), 0))
                    .addItem(new BottomBarTab(this, R.drawable.me_normal, "", getString(R.string.mine), 0));
        } else {
            mBottomBar
                    .addItem(new BottomBarTab(this, R.drawable.clean_normal, "", getString(R.string.clean), 0))
                    .addItem(new BottomBarTab(this, R.drawable.tool_normal, "", getString(R.string.tool), 0))
                    .addItem(new BottomBarTab(this, R.drawable.icon_scratch_tab, "", getString(R.string.top), 0))
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
