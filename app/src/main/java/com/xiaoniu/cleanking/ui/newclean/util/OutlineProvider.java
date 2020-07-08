package com.xiaoniu.cleanking.ui.newclean.util;

import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.RequiresApi;

/**
 * author : zyt
 * e-mail : 632105276@qq.com
 * date   : 2019/1/14
 * desc   :View都支持该种方式设置圆角。。。 api21以上===只能全圆角
 *
 * 调用： if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
 *             view.setOutlineProvider(new OutlineProvider(TextViewUtils.init().getDpValue(this, R.dimen.margin_015)));
 *             view.setClipToOutline(true);
 *         }
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class OutlineProvider extends ViewOutlineProvider {
    private float mRadius;

    public OutlineProvider(float radius) {
        this.mRadius = radius;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        Rect selfRect  = new Rect(0, 0,
                rect.right - rect.left, rect.bottom - rect.top);
        outline.setRoundRect(selfRect, mRadius);
    }
}
