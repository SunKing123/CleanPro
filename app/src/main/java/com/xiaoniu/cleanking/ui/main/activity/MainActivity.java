package com.xiaoniu.cleanking.ui.main.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.base.UmengEnum;
import com.xiaoniu.cleanking.base.UmengUtils;
import com.xiaoniu.cleanking.ui.main.event.ScanFileEvent;
import com.xiaoniu.cleanking.ui.main.fragment.CleanMainFragment;
import com.xiaoniu.cleanking.ui.main.fragment.MeFragment;
import com.xiaoniu.cleanking.ui.main.fragment.ShoppingMallFragment;
import com.xiaoniu.cleanking.ui.main.fragment.ToolFragment;
import com.xiaoniu.cleanking.ui.main.presenter.MainPresenter;
import com.xiaoniu.cleanking.ui.main.widget.BottomBar;
import com.xiaoniu.cleanking.ui.main.widget.BottomBarTab;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.DbHelper;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;

/**
 * main主页面
 */
@Route(path = RouteConstants.MAIN_ACTIVITY)
public class MainActivity extends BaseActivity<MainPresenter> {

    private static final String TAG = "MainActivity.class";
    private static final int REQUEST_STORAGE_PERMISSION = 1111;
    @Inject
    NoClearSPHelper mPreferencesHelper;
    @BindView(R.id.bottomBar)
    BottomBar mBottomBar;
    @BindView(R.id.bottom_shadow)
    ImageView mBottomShadow;
    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentManager mManager = getSupportFragmentManager();
    private String mUrl;
    private String mTitle;
    /**
     * 版本更新代理
     */
    private UpdateAgent mUpdateAgent;
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

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
//        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        //检查是否有补丁
        mPresenter.queryPatch();
        //检测版本更新
        mPresenter.queryAppVersion(() -> {
        });

        initFragments();
        //初始化sdk 正式版的时候设置false，关闭调试
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
        JPushInterface.setAlias(this, AndroidUtil.getPhoneNum(), null);

//        ShadowDrawable.setShadowDrawable(mBottomBar, Color.parseColor("#ffffff"),
//                DeviceUtils.dip2px(6),
//                Color.parseColor("#1A000000"), DeviceUtils.dip2px(8), 0, 5);
        mBottomBar
                .addItem(new BottomBarTab(this, R.mipmap.clean_normal, getString(R.string.clean)))
//                .addItem(new BottomBarTab(this, R.mipmap.tool_normal, "工具箱"))
                .addItem(new BottomBarTab(this, R.mipmap.msg_normal, "资讯"))
                .addItem(new BottomBarTab(this, R.mipmap.me_normal, getString(R.string.mine)));
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
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
//        EventBus.getDefault().register(this);


        DbHelper.copyDb();

        checkReadPermission();

        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //扫描更新系统数据库
        MediaScannerConnection.scanFile(this, new String[]{absolutePath}, null,null);
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
//                    Toast.makeText(this, "打开相册失败，请允许存储权限后再试", Toast.LENGTH_SHORT).show();
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

        MeFragment mineFragment = new MeFragment();
        CleanMainFragment mainFragment = new CleanMainFragment();
//        String url = ApiModule.SHOPPING_MALL;
        String url = ApiModule.Base_H5_Host;

        ToolFragment toolFragment = new ToolFragment();
        ShoppingMallFragment upQuotaFragment = ShoppingMallFragment.getIntance(url);
//        BaseBrowserFragment baseBrowserFragment=BaseBrowserFragment.newInstance(url);
        mFragments.add(mainFragment);
//        mFragments.add(toolFragment);
        mFragments.add(upQuotaFragment);
        mFragments.add(mineFragment);

        mManager.beginTransaction()
                .add(R.id.frame_layout, mainFragment)
//                .add(R.id.frame_layout, toolFragment)
                .add(R.id.frame_layout, upQuotaFragment)
                .add(R.id.frame_layout, mineFragment)
                .hide(mainFragment)
//                .hide(toolFragment)
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
        }  else if (position == 1) {
            eventCode = "selected_click";
            currentPage = "selected_page";
        } else if (position == 2) {
            eventCode = "mine_click";
            currentPage = "mine_page";
        }
        if (prePosition == 0) {
            sourcePage = "home_page";
        } else if (prePosition == 1) {
            sourcePage = "selected_page";
        } else if (prePosition == 2) {
            sourcePage = "mine_page";
        }
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

        if (mUpdateAgent != null) {
            mUpdateAgent.dissmiss();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
            }else {
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


}
