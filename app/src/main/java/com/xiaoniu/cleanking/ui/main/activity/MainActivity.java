package com.xiaoniu.cleanking.ui.main.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.base.UmengEnum;
import com.xiaoniu.cleanking.base.UmengUtils;
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
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
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
    @BindView(R.id.btn_wjgl)
    Button btn_wjgl;
    @BindView(R.id.btn_whilte_access)
    Button btn_whilte_access;
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
    public static int LOAN = 0;
    /**
     * 商城页
     */
    public static int SHOPPING = 1;
    /**
     * 生活页
     */
    public static int LIFE = -1;
    /**
     * 提额
     */
    public static int UPQUOTA = 2;
    /**
     * 我的页面
     */
    public static int MINE = 3;
    public final int[] popPositions = {Constant.LOAN, Constant.SHOPPING, Constant.UPQUOTA, Constant.MINE};

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
        if (AppApplication.isAudit) {
            mBottomBar.setCurrentItem(0);
            mBottomBar.addItem(new BottomBarTab(this, R.mipmap.mall_normal, getString(R.string.shopping_mall)))
                    .addItem(new BottomBarTab(this, R.mipmap.life_icon, getString(R.string.life)))
                    .addItem(new BottomBarTab(this, R.mipmap.me_normal, getString(R.string.mine)));
            SHOPPING = 0;
            UPQUOTA = 1;
            MINE = 2;
            showHideFragment(0, -1);
        } else {
            mBottomBar
                    .addItem(new BottomBarTab(this, 0, getString(R.string.clean)))
                    .addItem(new BottomBarTab(this, 0, "工具箱"))
                    .addItem(new BottomBarTab(this, 0, "资讯"))
                    .addItem(new BottomBarTab(this, 0, getString(R.string.mine)));
            mBottomBar.setCurrentItem(0);
            LOAN = 0;
            SHOPPING = 1;
            UPQUOTA = 2;
            MINE = 3;
            showHideFragment(0, -1);
        }

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

        btn_wjgl.setOnClickListener(v -> startActivity(FileManagerHomeActivity.class));
        btn_whilte_access.setOnClickListener(v -> startActivity(PhoneAccessActivity.class));

        DbHelper.copyDb();

        checkReadPermission();
    }

    private void checkReadPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("");
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.PACKAGE_USAGE_STATS)){
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
                    Toast.makeText(this, "打开相册失败，请允许存储权限后再试", Toast.LENGTH_SHORT).show();
                } else {
                    //TODO 请求权限弹窗 允许后回调返回的成功回调 在此写业务逻辑
                }
                break;
        }
    }

    @OnClick({R.id.btn_whilte_list_speed})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.btn_whilte_list_speed) {
            startActivity(new Intent(this, WhiteListSpeedManageActivity.class));
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
            mBottomBar.setCurrentItem(SHOPPING);
        } else if ("shenghuo".equals(type)) {
            mBottomBar.setCurrentItem(LIFE);
        } else if ("jiekuan".equals(type)) {
            mBottomBar.setCurrentItem(LOAN);
        } else if ("huodong".equals(type)) {
            mBottomBar.setCurrentItem(UPQUOTA);
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
        String url = "http://192.168.90.51/clean/home.html";

        ToolFragment toolFragment = new ToolFragment();
        ShoppingMallFragment upQuotaFragment = ShoppingMallFragment.getIntance(url);

        mFragments.add(mainFragment);
        mFragments.add(toolFragment);
        mFragments.add(upQuotaFragment);
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
        if (position == LOAN) {
            UmengUtils.event(MainActivity.this, UmengEnum.Tab_jiekuan);
            StatisticsUtils.burying(StatisticsUtils.BuryEvent.TabJiekuan);
        } else if (position == MINE) {
            UmengUtils.event(MainActivity.this, UmengEnum.Tab_wode);
            StatisticsUtils.burying(StatisticsUtils.BuryEvent.TabWode);
        } else if (position == UPQUOTA) {
            StatisticsUtils.burying(StatisticsUtils.BuryEvent.TabTie);
        } else if (position == SHOPPING) {
            StatisticsUtils.burying(StatisticsUtils.BuryEvent.TabShangcheng);
        }
        FragmentTransaction ft = mManager.beginTransaction();
        ft.show(mFragments.get(position));
        if (prePosition != -1) {
            ft.hide(mFragments.get(prePosition));
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {

        if (mUpdateAgent != null) {
            mUpdateAgent.dissmiss();
        }
//        EventBus.getDefault().unregister(this);
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
                    AppManager.getAppManager().AppExit(MainActivity.this, false);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
