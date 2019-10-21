package com.xiaoniu.cleanking.utils;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by banketree
 * on 2019/10/21 20:12
 * des:
 */
public class ViewUtil {

    /**
     * description: 删除自身 不绑定父视图
     * You must call removeView() on the child's parent first
     *
     * @param view
     */
    public static void removeViewByParent(View view) {
        if (view == null) {
            return;
        }
        if (view.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
    }
}
