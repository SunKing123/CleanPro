package com.xiaoniu.cleanking.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.comm.jksdk.utils.DisplayUtil;

import java.lang.reflect.Field;

/**
 * 爱步行的baseDialog
 */
public class BaseDialog extends Dialog {

    //数据的一些初始化
    public static final int BOTTOM = 0;
    public static final int TOP = 1;
    public static final int CENTER = 2;
    public static final float SCREENWIDTH = 0;
    public static final float SCREENHEIGHT = 0;
    public static final float WARPWIDTH = -2;
    public static final float WARPHEIGHT = -2;
    public Context context;
    //设置更改的宽度比例
    public double widthDialog = 0;
    //设置更改的高度比例
    public double heightDialog = 0;
    public float heightDialogdp = 0;//固定高度
    public float widthDialogdp = 0;//固定宽度
    //设置位置
    public int gravityLayout = 0;

    public WindowManager.LayoutParams lp;
    private Display d;
    public DisplayMetrics dm;
    private Window mWindow;
    //是否根据宽来算高度
    private boolean widthTag;

    public BaseDialog(Context context) {
        super(context);
        this.context = context;
        initWindowState();
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        initWindowState();
    }

    protected BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
        initWindowState();
    }

    private void initWindowState() {
        mWindow = this.getWindow();
        lp = mWindow.getAttributes();
        WindowManager windowManager = ((Activity) context).getWindowManager();
        d = windowManager.getDefaultDisplay();
        dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
    }


    public void setFullScreen() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//        }
    }

    public void setFullScreenNoStatusBar() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 第4步
     * 初始化dialog完成4步
     */
    public void initOnCreate() {
        lp.gravity = gravityLayout;
        if (widthDialog > 0) {  //设置宽度
            lp.width = (int) (d.getWidth() * widthDialog);
        } else {     //这个是横向全屏占满
            if (widthDialogdp > 0) {
                lp.width = DisplayUtil.dp2px(context, widthDialogdp);
            } else if (widthDialogdp == WARPWIDTH) {
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                lp.width = d.getWidth();
            }
        }
        if (heightDialog > 0) {   //设置高度
            if (widthTag) {
                lp.height = (int) (lp.width * heightDialog);
            } else {
                lp.height = (int) (d.getHeight() * heightDialog);
            }
        } else {                //设置高度 去掉状态栏
            if (heightDialogdp > 0) {//是不是固定的呢
                lp.height = DisplayUtil.dp2px(context, heightDialogdp);
            } else if (heightDialogdp == WARPHEIGHT) {
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                lp.height = d.getHeight() - getStatusBarHeight();
            }
        }   //绑定
        mWindow.setAttributes(lp);
    }

    //第一步设置宽
    public void setWidthDialog(double widthDialog) {
        this.widthDialog = widthDialog;
    }

    //第二步设置高
    public void setHeightDialog(double heightDialog) {
        this.heightDialog = heightDialog;
    }

    public void setHeightDialogdp(float heightDialogdp) {
        this.heightDialogdp = heightDialogdp;
    }

    //第三步设置位置
    public void setGravityLayout(int gravityLay) {
        if (BOTTOM == gravityLay) {
            gravityLayout = Gravity.BOTTOM;
        }
        if (TOP == gravityLay) {
            gravityLayout = Gravity.TOP;
        }
        if (CENTER == gravityLay) {
            gravityLayout = Gravity.CENTER;
        }
    }

    //有的时候 高度是根据宽度来算的
    public void setWidthTag(boolean widthTag) {
        this.widthTag = widthTag;
    }

    public void setWidthDialogdp(float widthDialogdp) {
        this.widthDialogdp = widthDialogdp;
    }


    public <T extends View> T getViewById(int viewId) {
        View view = findViewById(viewId);
        return (T) view;
    }

    /**
     * 状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
}
