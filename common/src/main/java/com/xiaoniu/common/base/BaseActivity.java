package com.xiaoniu.common.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.common_lib.R;
import java.util.List;


/**
 * Created by wangbaozhong 2017/05/01
 * 主要功能包括：重新定义生命周期（模板模式），
 * 加入ToolBar,跳转activity，通用的UI操作。
 */
public abstract class BaseActivity extends AppCompatActivity {

    public Context mContext;
    public LayoutInflater mInflater;
    private Toolbar mToolBar;
    private TextView tvTitle;
    private FrameLayout layBody;
    private View mContentView;
    private View mEmptyView;
    private View mErrorView;
    private final int CLICK_TIME = 500;//按钮连续点击最低间隔时间 单位：毫秒
    private long lastClickTime = 0;//记录上次启动activity的时间
    private String lastClassName = "";//记录上次启动activity的类名
    private Dialog mProgressDialog;

    /* 子类使用的时候无需再次调用onCreate(),如需要加载其他方法可重写该方法 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//去掉ActionBar
        super.setContentView(R.layout.activity_base);
        initBaseView();
        setContentView();
        initVariable();
        initViews(savedInstanceState);
        setListener();
        loadData();
    }

    private void initBaseView() {
        //常用的对象
        mContext = getApplicationContext();
        mInflater = getLayoutInflater();

        //ToolBar相关
        mToolBar = (Toolbar) findViewById(R.id.toolBar);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 内容区
        layBody = (FrameLayout) findViewById(R.id.layBody);
    }

    protected abstract int getLayoutResId();

    protected abstract void initVariable();//包括Intent上的数据和Activity内部使用的变量

    protected abstract void initViews(Bundle savedInstanceState);//初始化View控件

    protected abstract void setListener();

    protected abstract void loadData();//请求数据

    public void doClick(View view) {
    }

    ;

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
     * 设置标题
     */
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    /**
     * 获取标题
     */
    public TextView getTitleView() {
        return tvTitle;
    }

    /*设置导航条左边的按钮，一般是返回按钮*/
    public void setLeftButton(int iconResId, final View.OnClickListener onClickListener) {
        mToolBar.setNavigationIcon(iconResId);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
            }
        });
    }

    /*设置导航条右边的按钮*/
    public void addRightButton(final int iconResId, final Toolbar.OnMenuItemClickListener OnMenuItemClickListener) {
        mToolBar.post(new Runnable() {
            @Override
            public void run() {
                mToolBar.inflateMenu(R.menu.menu_tool_bar_item);//设置右上角的填充菜单
                Menu menu = mToolBar.getMenu();
                MenuItem item = menu.getItem(menu.size() - 1);
                item.setIcon(iconResId);
                mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        OnMenuItemClickListener.onMenuItemClick(item);
                        return true;
                    }
                });
            }
        });

    }

    public void addRightButton(final View btnView) {
        mToolBar.post(new Runnable() {
            @Override
            public void run() {
                mToolBar.inflateMenu(R.menu.menu_tool_bar_item);//设置右上角的填充菜单
                Menu menu = mToolBar.getMenu();
                MenuItem item = menu.getItem(menu.size() - 1);
                item.setActionView(btnView);
            }
        });

    }

    /***
     * 设置内容区域
     */
    private void setContentView() {
        int resId = getLayoutResId();
        try {
            if (resId > 0) {
                mContentView = mInflater.inflate(resId, layBody, false);
                layBody.addView(mContentView);
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
        mEmptyView = emptyView;
        if (mEmptyView.getParent() != null) {
            ((ViewGroup) mEmptyView.getParent()).removeView(mEmptyView);
        }
        mEmptyView.setVisibility(View.GONE);
        layBody.addView(mEmptyView);
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
        mErrorView = errorView;
        if (mErrorView.getParent() != null) {
            ((ViewGroup) mErrorView.getParent()).removeView(mErrorView);
        }
        mErrorView.setVisibility(View.GONE);
        layBody.addView(mErrorView);
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
        if (curClickTime - lastClickTime <= CLICK_TIME && curClassName.equals(lastClassName)) {
            flag = false;
        }
        lastClassName = curClassName;
        lastClickTime = curClickTime;
        return flag;
    }

    /********************************************************
     * *******  通用的UI操作，例如显示Dialog，Toast等    ********
     ******************************************************/
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new Dialog(this);
            mProgressDialog.getWindow().setBackgroundDrawable(null);
            ViewGroup decorView = (ViewGroup) mProgressDialog.getWindow().getDecorView();
            decorView.setBackgroundColor(Color.TRANSPARENT);
            decorView.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
            ProgressBar progressBar = new ProgressBar(mContext);
            mProgressDialog.setContentView(progressBar);
        }
        mProgressDialog.show();
    }

    public void cancelProgressDialog() {
        mProgressDialog.cancel();
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
