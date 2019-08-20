package com.xiaoniu.common.base;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.common_lib.R;

import java.util.List;

/**
 * Created by wangbaozhong 2017/05/01
 * 主要功能包括：支持ViewPager惰加载,重新定义生命周期（模板模式），
 * 加入ToolBar,跳转activity，通用的UI操作。
 */
public abstract class BaseFragment extends Fragment {
    protected Activity mActivity;//持有Activity的引用,防止onDetach后，getActivity()== null空指针闪退
    public LayoutInflater mInflater;
    private FrameLayout layBody;
    private boolean isViewCreated = false;//标示View 是否已经创建
    private boolean isLoaded = false;//是否已经惰加载过数据
    private Toolbar mToolBar;
    private TextView tvTitle;
    private View mContentView;
    private View mEmptyView;
    private View mErrorView;
    private final int CLICK_TIME = 500;//按钮连续点击最低间隔时间 单位：毫秒
    private long lastClickTime;//记录上次点击启动activity的时间
    private String lastClassName = "";//记录上次启动activity的类名

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    /*当detach后再attach，该方法不会调用*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable(getArguments());//初始化变量，获取携带的数据
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_base, container, false);
        // 内容区
        layBody = (FrameLayout) rootView.findViewById(R.id.layBody);
        //ToolBar
        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        mToolBar = (Toolbar) rootView.findViewById(R.id.toolBar);

        int resId = getLayoutResId();
        try {
            mContentView = mInflater.inflate(resId, layBody,false);
            layBody.addView(mContentView);
        } catch (Resources.NotFoundException e) {

        }
        // 解决点击穿透问题
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(mContentView, savedInstanceState);
        setListener();
        isViewCreated = true;
        isLoaded = false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
        if (getUserVisibleHint() && !isLoaded) {
            isLoaded = true;
            lazyLoadData(); //用于第一个fragment
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && isViewCreated && !isLoaded) {
            isLoaded = true;
            lazyLoadData();
        }
    }

    /**
     * 该方法会先于onCreate和onCreateView 执行,
     * 且每次都在Fragment可见或不可见时调用一次，所以需要判断是否创建View
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isViewCreated && !isLoaded) {
            isLoaded = true;
            lazyLoadData();
        }
    }

    protected abstract int getLayoutResId();

    protected abstract void initVariable(Bundle arguments);

    protected abstract void initViews(View contentView, Bundle savedInstanceState);

    protected abstract void setListener();

    protected abstract void loadData();//和lazyLoadData选择性使用

    protected void lazyLoadData() {
    }

    ;//惰加载数据,只针对ViewPager


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
            mContentView = mInflater.inflate(resId, layBody);
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

    public void showProgressDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawable(null);
        ViewGroup decorView = (ViewGroup) dialog.getWindow().getDecorView();
        decorView.setBackgroundColor(Color.TRANSPARENT);
        decorView.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
        ProgressBar progressBar = new ProgressBar(getContext());
        dialog.setContentView(progressBar);
        dialog.show();
    }

    /***********************************************
     * ********          fragment管理      **********
     **********************************************/
    public void replaceFragment(@IdRes int resId, Fragment fragment, boolean isBackStack) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(resId, fragment);
        if (isBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    //添加fragment
    protected void addFragment(int resId, Fragment fragment, boolean isAddBackStack) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager()
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
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            for (Fragment hideFragment : hideFragments) {
                if (hideFragment != null && hideFragment.isAdded())
                    fragmentTransaction.hide(hideFragment);
            }
        }

    }
}
