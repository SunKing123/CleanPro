package com.xiaoniu.common.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;
import com.xiaoniu.common.R;
import com.xiaoniu.common.widget.LoadingDialog;
import com.xiaoniu.common.widget.statusbarcompat.StatusBarCompat;

import java.util.List;


/**
 * Created by wangbaozhong 2017/05/01
 * 主要功能包括：重新定义生命周期（模板模式），
 * 加入ToolBar,跳转activity，通用的UI操作。
 */
public abstract class BaseActivity extends RxAppCompatActivity {

    public Context mContext;
    public LayoutInflater mInflater;
    private Toolbar mToolBar;
    private TextView mTvCenterTitle;
    private FrameLayout mLayBody;
    private View mContentView;
    private View mEmptyView;
    private View mErrorView;
    private final int CLICK_TIME = 500;//按钮连续点击最低间隔时间 单位：毫秒
    private long mLastClickTime = 0;//记录上次启动activity的时间
    private String mLastClassName = "";//记录上次启动activity的类名
    protected LoadingDialog mLoadingDialog;
    private TextView mTvLeftTitle;
    private ImageView mBtnLeft;
    private LinearLayout mLayRightBtn;

    /* 子类使用的时候无需再次调用onCreate(),如需要加载其他方法可重写该方法 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//去掉ActionBar
        super.setContentView(R.layout.common_activity_base);
        initBaseView();
        setContentView();
        initVariable(getIntent());
        initViews(savedInstanceState);
        setStatusBar();
        setListener();
        loadData();
    }

    private void initBaseView() {
        //常用的对象
        mContext = getApplicationContext();
        mInflater = getLayoutInflater();
        // 内容区
        mLayBody = (FrameLayout) findViewById(R.id.layBody);
        //ToolBar相关
        initToolBar();
        //网络错误显示
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View errorView = mInflater.inflate(R.layout.common_layout_web_error, mLayBody, false);
                setErrorView(errorView);
            }
        });

    }

    private void initToolBar() {
        mToolBar = (Toolbar) findViewById(R.id.toolBar);
        mTvCenterTitle = (TextView) findViewById(R.id.tvCenterTitle);
        mTvLeftTitle = (TextView) findViewById(R.id.tvLeftTitle);
        mBtnLeft = (ImageView) findViewById(R.id.btnLeft);
        mLayRightBtn = (LinearLayout) findViewById(R.id.layRightBtn);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setLeftButton(R.drawable.common_icon_back_arrow_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected abstract int getLayoutResId();

    protected abstract void initVariable(Intent intent);//包括Intent上的数据和Activity内部使用的变量

    protected abstract void initViews(Bundle savedInstanceState);//初始化View控件

    protected abstract void setListener();

    protected abstract void loadData();//请求数据

    public void doClick(View view) {
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /************************************************
     * **********       ToolBar相关设置***************
     ************************************************/
    public void hideToolBar() {
        mToolBar.setVisibility(View.GONE);
    }

    public void showToolBar() {
        mToolBar.setVisibility(View.VISIBLE);
    }

    public void setToolBarColor(int color) {
        mToolBar.setBackgroundColor(color);
    }

    /**
     * 得到导航条
     */
    public Toolbar getToolBar() {
        return mToolBar;
    }

    /**
     * 设置中间标题
     */
    public void setCenterTitle(String title) {
        mTvCenterTitle.setText(title);
    }

    /**
     * 设置左边标题
     */
    public void setLeftTitle(String title) {
        mTvLeftTitle.setText(title);
    }

    /*设置导航条左边的按钮，一般是返回按钮*/
    public void setLeftButton(int iconResId, final View.OnClickListener onClickListener) {
        mBtnLeft.setImageResource(iconResId);
        mBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
            }
        });
    }

    /*设置导航条右边的按钮*/
    public ImageView addRightButton(final int iconResId, final View.OnClickListener onClickListener) {
        ImageView rightBtn = (ImageView) mInflater.inflate(R.layout.common_layout_right_btn, mLayRightBtn, false);
        mLayRightBtn.addView(rightBtn);
        rightBtn.setImageResource(iconResId);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
            }
        });
        return rightBtn;
    }

    public void addRightButton(final View btnView) {
        mLayRightBtn.addView(btnView);
    }

    /***
     * 设置内容区域
     */
    private void setContentView() {
        int resId = getLayoutResId();
        try {
            if (resId > 0) {
                mContentView = mInflater.inflate(resId, mLayBody, false);
                mLayBody.addView(mContentView);
            }
        } catch (Resources.NotFoundException e) {

        }
    }

    /**
     * 得到内容View,也就是用户的传入的布局，不包括ToolBar
     */
    public View getContentView() {
        return mContentView;
    }

    /*空状态下的View*/
    public void setEmptyView(View emptyView) {
        if (mEmptyView != null) {
            mLayBody.removeView(mEmptyView);
        }
        mEmptyView = emptyView;
        if (mEmptyView.getParent() != null) {
            ((ViewGroup) mEmptyView.getParent()).removeView(mEmptyView);
        }
        mEmptyView.setVisibility(View.GONE);
        mLayBody.addView(mEmptyView);
    }

    public void showEmptyView() {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    public void hideEmptyView() {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    /*加载失败的View*/
    public void setErrorView(View errorView) {
        if (mErrorView != null) {
            mLayBody.removeView(mErrorView);
        }
        mErrorView = errorView;
        if (mErrorView.getParent() != null) {
            ((ViewGroup) mErrorView.getParent()).removeView(mErrorView);
        }
        mErrorView.setVisibility(View.GONE);
        mLayBody.addView(mErrorView);
    }

    public void showErrorView() {
        if (mErrorView != null) {
            mErrorView.setVisibility(View.VISIBLE);
        }
    }

    public void hideErrorView() {
        if (mErrorView != null) {
            mErrorView.setVisibility(View.GONE);
        }
    }

    public View getErrorView() {
        return mErrorView;
    }

    /**
     * 含有flags通过ARouter跳转界面
     *
     * @param path   跳转地址
     * @param flags  flags
     * @param bundle bundle
     **/
    public void startActivity(String path, int[] flags, Bundle bundle) {
        startActivityForResult(path, flags, bundle, -1);
    }

    /**
     * 带返回含有Bundle通过ARouter跳转界面
     *
     * @param path        跳转地址
     * @param requestCode requestCode
     * @param bundle      bundle
     **/
    public void startActivityForResult(String path, int[] flags, Bundle bundle, int requestCode) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Postcard build = ARouter.getInstance().build(path);
        if (null != bundle) {
            build.with(bundle);
        }
        if (null != flags) {
            for (int flag : flags) {
                build.withFlags(flag);
            }
        }
        if (requestCode >= 0) {
            build.navigation(this, requestCode);
        } else {
            build.navigation();
        }
    }

    /*startActivity(Intent intent)最终也会调用这个方法，所以只需这一处判断*/
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (verifyClickTime(intent)) {
            super.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 验证上次点击按钮时间间隔，防止重复点击
     */
    public boolean verifyClickTime(Intent intent) {
        boolean flag = true;
        String curClassName = intent.getComponent().getClassName();
        long curClickTime = System.currentTimeMillis();
        if (curClickTime - mLastClickTime <= CLICK_TIME && curClassName.equals(mLastClassName)) {
            flag = false;
        }
        mLastClassName = curClassName;
        mLastClickTime = curClickTime;
        return flag;
    }

    /********************************************************
     * *******  通用的UI操作，例如显示Dialog，Toast等    ********
     ******************************************************/
    public void showLoadingDialog() {
        if (mLoadingDialog == null)
            mLoadingDialog = new LoadingDialog.Builder().createLoadingDialog(this).build();
        mLoadingDialog.showDialog();
    }

    public void cancelLoadingDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog.cancelDialog();
    }

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }
    }

    /***********************************************
     * ********          fragment管理      **********
     **********************************************/
    public void replaceFragment(@IdRes int resId, Fragment fragment, boolean isBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(resId, fragment);
        if (isBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    //添加fragment
    protected void addFragment(int resId, Fragment fragment, boolean isAddBackStack) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.add(resId, fragment);
            if (isAddBackStack) {
                fragmentTransaction.addToBackStack(null);
            }
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    //隐藏fragment
    protected void hideFragments(List<Fragment> hideFragments) {
        if (hideFragments != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            for (Fragment hideFragment : hideFragments) {
                if (hideFragment != null && hideFragment.isAdded())
                    fragmentTransaction.hide(hideFragment);
            }
        }

    }
}
