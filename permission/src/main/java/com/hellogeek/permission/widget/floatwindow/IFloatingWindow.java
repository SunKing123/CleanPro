package com.hellogeek.permission.widget.floatwindow;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.WindowManager;




/**
 * Desc:悬浮窗基类接口定义
 * <p>
 * Author: AnYaBo
 * Date: 2019/7/1
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update Comments:
 * @author anyabo
 */
public interface IFloatingWindow {

    Context getContext();

    View getContentView();

    void setContentView(@LayoutRes int layoutRes);

    void setContentView(View view);

    <T extends View> T findViewById(@IdRes int viewId);

    void setParams(WindowManager.LayoutParams params);

    void show();

    void dismiss();

    boolean isShowing();

    WindowManager.LayoutParams  getDefaultLayoutParams();

    WindowManager.LayoutParams  getLayoutParams();

}
