package com.xiaoniu.common.base;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiaoniu.common.R;
import com.xiaoniu.common.widget.LoadingDialog;

import java.util.List;

import cn.jzvd.Jzvd;

/**
 * Created by wangbaozhong 2017/05/01
 * 主要功能包括：支持ViewPager惰加载,重新定义生命周期（模板模式），
 * 加入ToolBar,跳转activity，通用的UI操作。
 */
public abstract class BaseFragment extends RxFragment {
    protected Activity mActivity;//持有Activity的引用,防止onDetach后，getActivity()== null空指针闪退
    public LayoutInflater mInflater;
    private FrameLayout mLayBody;
    private boolean mViewCreated = false;//标示View 是否已经创建
    private boolean mIsLoaded = false;//是否已经惰加载过数据
    private Toolbar mToolBar;
    private TextView mTvCenterTitle;
    private TextView mTvLeftTitle;
    private ImageView mBtnLeft;
    private LinearLayout mLayRightBtn;

    private View mContentView;
    private View mEmptyView;
    private View mErrorView;
    private final int CLICK_TIME = 500;//按钮连续点击最低间隔时间 单位：毫秒
    private long lastClickTime;//记录上次点击启动activity的时间
    private String lastClassName = "";//记录上次启动activity的类名
    private LoadingDialog mLoadingDialog;
    private boolean mVisibleToUser = false;
    private boolean mSupportLazy = false; //默认不支持惰加载

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
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
        View rootView = inflater.inflate(R.layout.common_fragment_base, container, false);
        // 内容区
        mLayBody = (FrameLayout) rootView.findViewById(R.id.layBody);

        //ToolBar相关
        initToolBar(rootView);

        int resId = getLayoutResId();
        try {
            mContentView = mInflater.inflate(resId, mLayBody, false);
            mLayBody.addView(mContentView);
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

    private void initToolBar(View rootView) {
        mToolBar = (Toolbar) rootView.findViewById(R.id.toolBar);
        mTvCenterTitle = (TextView) rootView.findViewById(R.id.tvCenterTitle);
        mTvLeftTitle = (TextView) rootView.findViewById(R.id.tvLeftTitle);
        mBtnLeft = (ImageView) rootView.findViewById(R.id.btnLeft);
        mLayRightBtn = (LinearLayout) rootView.findViewById(R.id.layRightBtn);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(mContentView, savedInstanceState);
        setListener();
        mViewCreated = true;
        mIsLoaded = false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mSupportLazy) {
            if (mVisibleToUser && !mIsLoaded) {
                mIsLoaded = true;
                loadData(); //用于第一个fragment
            }
        } else {
            mIsLoaded = true;
            loadData();
        }
    }

    /**
     * 该方法会先于onCreate和onCreateView 执行,适合ViewPager切换情况
     * 且每次都在Fragment可见或不可见时调用一次，所以需要判断是否创建View
     * 该方案与onHiddenChanged方案同时只有一个生效，不会有冲突
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            onVisibleToUser();
        } else {
            Jzvd.releaseAllVideos();
        }
        if (mSupportLazy && isVisibleToUser && mViewCreated && !mIsLoaded) {
            mIsLoaded = true;
            loadData();
        }
    }

    /**
     * 适合自己控制Fragment显示隐藏情况，要想实现懒加载，必须先hide所有Fragment，然后再show需要的显示的fragment，
     * 如果使用该方案实现懒加载，必须按要求实现逻辑，否则loadData方法不会调用
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onVisibleToUser();
        }
        if (mSupportLazy) {
            if (!hidden && mViewCreated && !mIsLoaded) {
                mIsLoaded = true;
                loadData();
            }
        }
    }

    /*每次Fragment可见都会调用*/
    protected void onVisibleToUser() {
    }

    protected void setSupportLazy(boolean supportLazy) {
        mSupportLazy = supportLazy;
    }


    protected abstract int getLayoutResId();

    protected abstract void initVariable(Bundle arguments);

    protected abstract void initViews(View contentView, Bundle savedInstanceState);

    protected abstract void setListener();

    protected abstract void loadData();//和lazyLoadData选择性使用

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
                mToolBar.inflateMenu(R.menu.common_menu_tool_bar_item);//设置右上角的填充菜单
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
                mToolBar.inflateMenu(R.menu.common_menu_tool_bar_item);//设置右上角的填充菜单
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
            mContentView = mInflater.inflate(resId, mLayBody);
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
            build.navigation(mActivity, requestCode);
        } else {
            build.navigation();
        }
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

    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog.Builder().createLoadingDialog(getActivity()).build();
        }
        mLoadingDialog.showDialog();
    }

    public void cancelLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancelDialog();
        }
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
