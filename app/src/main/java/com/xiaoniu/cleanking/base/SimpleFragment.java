package com.xiaoniu.cleanking.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiaoniu.common.widget.LoadingDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by codeest on 16/8/11.
 * 无MVP的Fragment基类
 */

public abstract class SimpleFragment extends RxFragment {

    protected View mView;
    protected Activity mActivity;
    protected Context mContext;
    private Unbinder mUnBinder;
    private LoadingDialog mLoadingDialog;
    protected boolean isBinder;

    @Override
    public void onAttach(Context context) {
        mActivity = (Activity) context;
        mContext = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(), null);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnBinder = ButterKnife.bind(this, mView);
        isBinder = true;
        initView();
    }

//    public void showLoadingDialog(String contentText) {
//        mLoadingDialog = new LoadingDialog.Builder().createLoadingDialog(getContext()).setContent(contentText).build();
//        mLoadingDialog.showDialog();
//    }

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

    @Override
    public void onDestroyView() {
        mUnBinder.unbind();
        isBinder = false;
        cancelLoadingDialog();
        super.onDestroyView();
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    /**
     * 通过Class跳转界面
     **/
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 通过ARouter跳转界面
     *
     * @param path 跳转地址
     **/
    public void startActivity(String path) {
        ARouter.getInstance().build(path).navigation();
    }

    /**
     * 带返回通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 带返回通过ARouter跳转界面
     *
     * @param path        跳转地址
     * @param requestCode requestCode
     **/
    public void startActivityForResult(String path, int requestCode) {
        ARouter.getInstance().build(path).navigation(getActivity(), requestCode);
    }

    /**
     * 带返回含有Bundle通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 带返回含有Bundle通过ARouter跳转界面
     *
     * @param path        跳转地址
     * @param requestCode requestCode
     * @param bundle      bundle
     **/
    public void startActivityForResult(String path, Bundle bundle,
                                       int requestCode) {
        ARouter.getInstance().build(path).with(bundle).navigation(getActivity(), requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        if (getActivity() != null){
            Intent intent = new Intent();
            intent.setClass(getActivity(), cls);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            startActivity(intent);
        }
    }

    /**
     * 含有Bundle通过ARouter跳转界面
     *
     * @param path   跳转地址
     * @param bundle bundle
     **/
    public void startActivity(String path, Bundle bundle) {
        ARouter.getInstance().build(path).with(bundle).navigation();
    }


    /**
     * 含有flags通过ARouter跳转界面
     *
     * @param path  跳转地址
     * @param flags flags
     **/
    public void startActivity(String path, int[] flags) {
        Postcard build = ARouter.getInstance().build(path);
        for (int i = 0; i < flags.length; i++) {
            build.withFlags(flags[i]);
        }
        build.navigation();
    }

    /**
     * 含有flags通过ARouter跳转界面
     *
     * @param path   跳转地址
     * @param flags  flags
     * @param bundle bundle
     **/
    public void startActivity(String path, int[] flags, Bundle bundle) {
        Postcard build = ARouter.getInstance().build(path).with(bundle);
        for (int i = 0; i < flags.length; i++) {
            build.withFlags(flags[i]);
        }
        build.navigation();
    }
}
