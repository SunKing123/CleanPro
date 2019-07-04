package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.base.UmengEnum;
import com.xiaoniu.cleanking.base.UmengUtils;
import com.xiaoniu.cleanking.ui.main.fragment.CleanMainFragment;
import com.xiaoniu.cleanking.ui.main.fragment.MeFragment;
import com.xiaoniu.cleanking.ui.main.fragment.ShoppingMallFragment;
import com.xiaoniu.cleanking.ui.main.presenter.MainPresenter;
import com.xiaoniu.cleanking.ui.main.widget.BottomBar;
import com.xiaoniu.cleanking.ui.main.widget.BottomBarTab;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.DbHelper;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;

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
    @Inject
    NoClearSPHelper mPreferencesHelper;
    @BindView(R.id.bottomBar)
    BottomBar mBottomBar;
    @BindView(R.id.btn_wjgl)
    Button btn_wjgl;
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
                    .addItem(new BottomBarTab(this, R.mipmap.loan_normal, getString(R.string.clean)))
                    .addItem(new BottomBarTab(this, R.mipmap.mall_normal, getString(R.string.shopping_mall)))
                    .addItem(new BottomBarTab(this, R.mipmap.up_quota_normal, getString(R.string.up_quota)))
                    .addItem(new BottomBarTab(this, R.mipmap.me_normal, getString(R.string.mine)));
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

        DbHelper.copyDb();
    }


    @OnClick({R.id.btn_install_manage, R.id.btn_clean_music, R.id.btn_clean_video})
    public void onViewClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_install_manage) {
            startActivity(new Intent(this, CleanInstallPackageActivity.class));
        } else if (id == R.id.btn_clean_music) {
            startActivity(new Intent(this, CleanMusicManageActivity.class));
        } else if (id == R.id.btn_clean_video) {
            startActivity(new Intent(this, CleanVideoManageActivity.class));
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
    }

    private void initFragments() {

        MeFragment mineFragment = new MeFragment();
        CleanMainFragment mainFragment = new CleanMainFragment();
        String url = ApiModule.SHOPPING_MALL;
        if (!TextUtils.isEmpty(mSPHelper.getMineShopUrl())) {
            url = mSPHelper.getMineShopUrl();
        }
        ShoppingMallFragment shoppingMallFragment = ShoppingMallFragment.getIntance(url);
        ShoppingMallFragment upQuotaFragment = new ShoppingMallFragment();
        ShoppingMallFragment lifeFragment = ShoppingMallFragment.getIntance(ApiModule.LIFE);


        mFragments.add(mainFragment);
        mFragments.add(shoppingMallFragment);
        mFragments.add(upQuotaFragment);
        mFragments.add(mineFragment);

        mManager.beginTransaction()
                .add(R.id.frame_layout, mainFragment)
                .add(R.id.frame_layout, shoppingMallFragment)
                .add(R.id.frame_layout, upQuotaFragment)
                .add(R.id.frame_layout, mineFragment)
                .hide(mainFragment)
                .hide(shoppingMallFragment)
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
