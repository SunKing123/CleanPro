package com.xiaoniu.cleanking.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.common.utils.AppActivityUtils;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.widget.LoadingDialog;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 无MVP的activity基类
 */
public abstract class SimpleActivity extends RxAppCompatActivity {

    protected Activity mContext;
    private Unbinder mUnBinder;
    protected LoadingDialog mLoadingDialog;
    /**
     * 自定义Handler对象
     */
    protected Handler mHandler;

    /**
     * 静态内部类Handler
     */
    private final static class MHandler extends Handler {
        final WeakReference<SimpleActivity> wr;

        public MHandler(SimpleActivity activity) {
            wr = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (null != wr.get()) {
                wr.get().handleMessage(msg);
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //透明activity在Android8.0上崩溃 解决方案
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && AppActivityUtils.isTranslucentOrFloating(this)) {
            AppActivityUtils.fixOrientation(this);
        }
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivityName(this);
        mHandler = new MHandler(this);

        int layoutId = getLayoutId();
        if (layoutId != 0) {
            setContentView(layoutId);
        }
        mUnBinder = ButterKnife.bind(this);
        mContext = this;
        onViewCreated();
        initView();
    }

    protected void handleMessage(Message msg) {
    }

//    public void showLoadingDialog(String contentText) {
//        if (mLoadingDialog == null)
//            mLoadingDialog = new LoadingDialog.Builder().createLoadingDialog(this).setContent(contentText).build();
//        mLoadingDialog.showDialog();
//    }

    public void showLoadingDialog() {
        if (mLoadingDialog == null)
            mLoadingDialog = new LoadingDialog.Builder().createLoadingDialog(this).build();
        mLoadingDialog.showDialog();
    }

    public void cancelLoadingDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog.cancelDialog();
    }

    protected void onViewCreated() {
        setStatusBar();
    }

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }
    }

    @Override
    protected void onDestroy() {
        mUnBinder.unbind();
        AppManager.getAppManager().finishActivity(this);
        cancelLoadingDialog();
        this.mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
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
    public void startActivity(String path,boolean...finish) {
        this.startActivityForResult(path,0,finish);
    }

    /**
     * 带返回通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, int requestCode) {
        this.startActivityForResult(cls, null, requestCode);

    }

    /**
     * 带返回通过ARouter跳转界面
     *
     * @param path        跳转地址
     * @param requestCode requestCode
     **/
    public void startActivityForResult(String path, int requestCode,boolean...finish) {
        this.startActivityForResult(path,null,requestCode,finish);
    }

    /**
     * 带返回含有Bundle通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
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
    public void startActivityForResult(String path, Bundle bundle,int requestCode,boolean...finish) {
        ARouter.getInstance().build(path).with(bundle).navigation(this, requestCode);
        if (null != finish && finish.length > 0 && finish[0]) {
            this.finish();
        }
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
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
    public void startActivity(String path, int[] flags, boolean... finish) {
        startActivity(path, flags, null, finish);
    }


    /**
     * 含有flags通过ARouter跳转界面
     *
     * @param path   跳转地址
     * @param flags  flags
     * @param bundle bundle
     **/
    public void startActivity(String path, int[] flags, Bundle bundle, boolean... finish) {
        Postcard build = ARouter.getInstance().build(path);
        if (null != bundle) {
            build.with(bundle);
        }
        if (null != flags) {
            for (int flag : flags) {
                build.withFlags(flag);
            }
        }
        build.navigation();
        if (null != finish && finish.length > 0 && finish[0]) {
            this.finish();
        }
    }


    /**
     * Activity是否已被销毁
     * @return
     */
    public boolean isActivityEnable(){
        if(this == null || isDestroyed() || isFinishing()){
            return false;
        }
        return true;
    }

}
