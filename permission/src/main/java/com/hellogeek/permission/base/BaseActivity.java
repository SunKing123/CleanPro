package com.hellogeek.permission.base;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;





import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hellogeek.permission.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import butterknife.ButterKnife;


/**
 * Created on 2018/8/17.
 * @author lujinlong
 *
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * 初始化参数
     * 考虑系统恢复Activity调用onCreate时从savedInstanceState中恢复参数的情况
     *
     * @param savedInstanceState
     */
    protected abstract void initParams(Bundle savedInstanceState);

    /**
     * 由子类设置资源ID
     *
     * @return
     */
    protected abstract int getLayoutResID();

    /**
     * 初始化视图
     */
    protected abstract void initContentView();


    /**
     * 初始化数据
     */
    protected abstract void initData();


    /**
     * 处理返回事件
     * 子类返回false 则表示交给父类处理
     *
     * @return 子类是否已处理
     */
    protected abstract boolean onGoBack();



    private BroadcastReceiver logoutReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams(savedInstanceState);
        setContentView(getLayoutResID());
        ButterKnife.bind(this);

//        initMaterialStatusBar(getColorPrimary());
        initContentView();
        initData();

        IntentFilter filter = new IntentFilter("action_logout");
        logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(logoutReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 让当前活动的Activity监听网络变化
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logoutReceiver != null) {
            unregisterReceiver(logoutReceiver);
        }
    }
    /**
     * 覆盖setSupportActionBar的默认实现
     *
     * @param toolbarId
     */
    protected void setSupportActionBar(@IdRes int toolbarId) {
        Toolbar toolbar = findViewById(toolbarId);
        setSupportActionBar(toolbar);
    }

    //==========================Helper====================================//


    /**
     * 启动活动 需要处理返回结果
     * @param clazz
     * @param requestCode
     * @param bundle
     */
    protected void startActivityForResult(Class clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 弹出Toast消息
     * @param resId 消息字符串资源Id
     */
    protected void showToast(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出Toast消息
     * @param msg 消息内容
     */
    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }



    /**
     * 获取主题颜色
     *
     * @return
     */
    protected int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取深色的主题颜色
     *
     * @return
     */
    protected int getDarkColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }

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

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // edited here
            this.getWindow().setStatusBarColor(getColor(R.color.colorPrimary));
        }
//        StatusBarLightMode(this);
    }

    public static int StatusBarLightMode(Activity activity) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (MIUISetStatusBarLightMode(activity, true)) {
                result = 1;
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
                result = 2;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                result = 3;
            }
        }
        return result;
    }


    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    public static boolean MIUISetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);
                }
                result = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (dark) {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}