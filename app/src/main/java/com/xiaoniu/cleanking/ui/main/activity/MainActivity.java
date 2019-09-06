package com.xiaoniu.cleanking.ui.main.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.base.UmengEnum;
import com.xiaoniu.cleanking.base.UmengUtils;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.ui.main.event.AutoCleanEvent;
import com.xiaoniu.cleanking.ui.main.event.FileCleanSizeEvent;
import com.xiaoniu.cleanking.ui.main.event.ScanFileEvent;
import com.xiaoniu.cleanking.ui.main.fragment.CleanMainFragment;
import com.xiaoniu.cleanking.ui.main.fragment.MeFragment;
import com.xiaoniu.cleanking.ui.main.fragment.ShoppingMallFragment;
import com.xiaoniu.cleanking.ui.main.fragment.ToolFragment;
import com.xiaoniu.cleanking.ui.main.presenter.MainPresenter;
import com.xiaoniu.cleanking.ui.main.widget.BottomBar;
import com.xiaoniu.cleanking.ui.main.widget.BottomBarTab;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.news.fragment.NewsFragment;
import com.xiaoniu.cleanking.utils.DbHelper;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.common.utils.StatisticsUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

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
    private static final long DEFAULT_REFRESH_TIME = 10*60*1000L;

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
    private BottomBarTab mBottomBarTab;
    private boolean isSelectTop = false;
    MyHandler mHandler = new MyHandler(this);

    class MyHandler extends Handler{
        WeakReference<Activity> mActivity;
        public MyHandler(Activity con){
            this.mActivity = new WeakReference<>(con);
        }
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 1 ){
                if (isSelectTop)
                    return;
                if (mBottomBarTab != null){
                    mBottomBarTab.showBadgeView("...");
                }
            }
        }
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mHandler.sendEmptyMessageDelayed(1, DEFAULT_REFRESH_TIME);
        //检查是否有补丁
        mPresenter.queryPatch();
        //检测版本更新
        mPresenter.queryAppVersion(() -> {
        });

        //获取WebUrl
        mPresenter.getWebUrl();
        initFragments();
//        状态（0=隐藏，1=显示）
        String auditSwitch = SPUtil.getString(MainActivity.this, AppApplication.AuditSwitch, "1");
        if (TextUtils.equals(auditSwitch, "0")) {
            mBottomBar
                    .addItem(new BottomBarTab(this, R.mipmap.clean_normal, getString(R.string.clean)))
//                    .addItem(new BottomBarTab(this, R.mipmap.msg_normal, "资讯"))
                    .addItem(new BottomBarTab(this, R.mipmap.me_normal, getString(R.string.mine)));
        } else {
            mBottomBarTab = new BottomBarTab(this, R.mipmap.msg_normal, getString(R.string.top));
            mBottomBar
                    .addItem(new BottomBarTab(this, R.mipmap.clean_normal, getString(R.string.clean)))
                    .addItem(new BottomBarTab(this, R.mipmap.tool_normal, getString(R.string.tool)))
                    .addItem(mBottomBarTab)
                    .addItem(new BottomBarTab(this, R.mipmap.me_normal, getString(R.string.mine)));
        }

        mBottomBar.setCurrentItem(0);
        CLEAN = 0;
        TOOL = 1;
        NEWS = 2;
        MINE = 3;
        showHideFragment(0, -1);

        this.mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {

            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(position, prePosition);
                //如果没有选中头条，开始10分钟记时
                if (position == 2){
                    isSelectTop = true;
                }else {
                    if (isSelectTop) {
                        isSelectTop = false;
                        //清空所有的消息
                        mHandler.removeCallbacksAndMessages(null);
                        mHandler.sendEmptyMessageDelayed(1, DEFAULT_REFRESH_TIME);
                    }
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
        DbHelper.copyDb();

        checkReadPermission();

        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //扫描更新系统数据库
        MediaScannerConnection.scanFile(this, new String[]{absolutePath}, null, null);

        //极光推送 设备激活接口
        mPresenter.commitJPushAlias();
    }

    private void checkReadPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("");
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.PACKAGE_USAGE_STATS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.PACKAGE_USAGE_STATS},
                        REQUEST_STORAGE_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.PACKAGE_USAGE_STATS},
                        REQUEST_STORAGE_PERMISSION);
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
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
        mPresenter.saveCacheFiles();
    }

    private void initFragments() {

        MeFragment mineFragment = MeFragment.getIntance();
        CleanMainFragment mainFragment = new CleanMainFragment();
        String url = ApiModule.SHOPPING_MALL;

        ToolFragment toolFragment = new ToolFragment();
        upQuotaFragment = NewsFragment.getNewsFragment("");
        mFragments.add(mainFragment);

        mFragments.add(toolFragment);

        //        状态（0=隐藏，1=显示）
        String auditSwitch = SPUtil.getString(MainActivity.this, AppApplication.AuditSwitch, "1");
        if (TextUtils.equals(auditSwitch, "1")) {
            mFragments.add(upQuotaFragment);
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
//        AppConfig.showDebugWindow(this);
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

    long firstTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mFragments.get(mBottomBar.getCurrentItemPosition()) instanceof ShoppingMallFragment) {
                ShoppingMallFragment fragment = (ShoppingMallFragment) mFragments.get(mBottomBar.getCurrentItemPosition());
                fragment.onKeyBack();
                return true;
            } else if (mFragments.get(mBottomBar.getCurrentItemPosition()) instanceof CleanMainFragment) {
                CleanMainFragment fragment = (CleanMainFragment) mFragments.get(mBottomBar.getCurrentItemPosition());
                fragment.onKeyBack();
                return true;
            } else {
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 1500) {
                    // 如果两次按键时间间隔大于800毫秒，则不退出
                    Toast.makeText(getApplicationContext(), R.string.press_exit_again, Toast.LENGTH_SHORT).show();
                    // 更新firstTime
                    firstTime = secondTime;
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    return true;
                } else {
                    //如果审核满足答题条件时自动跳转答题页面，返回则不跳
                    SPUtil.setInt(MainActivity.this, "turnask", 0);
                    SPUtil.setBoolean(MainActivity.this, "firstShowHome", false);
                    AppManager.getAppManager().clearStack();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
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
        return mBottomBarTab.isBadgeViewShow();
    }

    public void hideBadgeView() {
        mBottomBarTab.hideBadgeView();
    }

    /**
     * 操作记录(PUSH消息)
     * @param type（1-立即清理 2-一键加速 3-手机清理 4-文件清理 5-微信专清 6-手机降温 7-qq专清）
     */
    public void commitJpushClickTime(int type){
        mPresenter.commitJpushClickTime(type);
    }
}
