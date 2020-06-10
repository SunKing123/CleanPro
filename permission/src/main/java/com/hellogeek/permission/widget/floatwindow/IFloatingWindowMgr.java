package com.hellogeek.permission.widget.floatwindow;


/**
 * Desc:悬浮窗管理器接口定义
 * <p>
 * Author: AnYaBo
 * Date: 2019/7/1
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update Comments:
 * @author anyabo
 */
public interface IFloatingWindowMgr {

    /**
     * 把悬浮窗显示到Window上
     * @param window    悬浮窗
     * @return
     */
    boolean showWindow(IFloatingWindow window);

    /**
     * 从Window上移除掉悬浮窗
     * @param window    悬浮窗
     * @return
     */
    void hideWindow(IFloatingWindow window);

    /**
     * 移除掉window上的所有view
     */
    void removeAllWindow();

}
