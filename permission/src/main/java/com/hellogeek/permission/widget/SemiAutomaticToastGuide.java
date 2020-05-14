package com.hellogeek.permission.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hellogeek.permission.R;
import com.hellogeek.permission.util.UIUtil;

/**
 * Desc:
 * <p>
 *
 * @author: ZhouTao
 * Date: 2019/7/29
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update
 */
public class SemiAutomaticToastGuide {

    /* renamed from: a */
    private Toast mToast;

    /* renamed from: b */
    private View mRoot;

    /* renamed from: c */
    private TextView text;


    public void make(Rect rect,Context context) {
        if (this.mToast == null && context != null) {
            this.mToast = new Toast(context);
            this.mRoot = LayoutInflater.from(context).inflate(R.layout.per_vivo_bg_permission_tutorail_toast_layout_new, null);
            this.mToast.setView(mRoot);
            this.text = (TextView) mRoot.findViewById(R.id.show_tips);
            this.mToast.setDuration(Toast.LENGTH_LONG);
        }
        show(rect, context);
    }

    public void show(Rect rect, Context context) {
        if (rect != null && this.mToast != null) {
            this.mToast.setGravity(16, 0, -((UIUtil.getScreenHeight(context) / 2) - rect.bottom));
            this.mToast.show();
        }
    }

}
