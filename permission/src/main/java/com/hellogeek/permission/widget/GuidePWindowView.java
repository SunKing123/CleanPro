package com.hellogeek.permission.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hellogeek.permission.R;
import com.hellogeek.permission.util.PhoneRomUtils;

/**
 * Desc:
 * <p>
 * Author: zhoutao
 * Date: 2019/7/4
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update Comments:
 */
public class GuidePWindowView extends RelativeLayout implements View.OnClickListener {
    private OnWindowDismissListener mWindowDismissListener;
    /* renamed from: a */
    public static final String item_top = "item_top";

    /* renamed from: b */
    public static final String item_bottom = "item_bottom";

    /* renamed from: c */
    public static final String extra_action_type = "extra_action_type";
    int mTopMargin;
    int mBottomMargin;
    int action_type;
    int c;
    private TextView text_guide;
    private RelativeLayout guide_content;
    private RelativeLayout guide_bottom_re;

    public void setWindowDismissListener(OnWindowDismissListener windowDismissListener) {
        mWindowDismissListener = windowDismissListener;
    }

    public GuidePWindowView(Context context) {
        this(context, null);
    }

    public GuidePWindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.accessibility_super_vivo_guide_view, this);
        this.text_guide = (TextView) findViewById(R.id.text_guide);
        this.guide_content = (RelativeLayout) findViewById(R.id.guide_content);
        this.guide_bottom_re = (RelativeLayout) findViewById(R.id.guide_bottom_re);

        findViewById(R.id.llRoot).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWindowDismissListener != null) {
                    mWindowDismissListener.dismiss();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (mWindowDismissListener != null) {
            mWindowDismissListener.dismiss();
        }
    }

    public void setData(int top, int bottom) {
        this.mTopMargin = top;
        this.mBottomMargin = bottom;
        m13326a();
    }

    private void m13326a() {
        if (PhoneRomUtils.getPhoneModel().equalsIgnoreCase("V1914A")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("Vivo X21UD A")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("V1824BA")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("Vivo Z1")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("Vivo X21i")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("V1813A")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("V1913A")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("Vivo Z1i")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("Vivo X21")
                || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("lld-al20")
          || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("vivo x21i a")) {
            c = getResources().getDisplayMetrics().heightPixels - this.mBottomMargin + 70;
        } else if (PhoneRomUtils.getPhoneModel().equalsIgnoreCase("V1901A")) {
            c = getResources().getDisplayMetrics().heightPixels - this.mBottomMargin + 50;
        } else {
            c = getResources().getDisplayMetrics().heightPixels - this.mBottomMargin;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("permission test ActionExecutor setView -----the height ");
        sb.append(c);
        LayoutParams layoutParams = (LayoutParams) this.guide_bottom_re.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = c;
            layoutParams.topMargin = this.mBottomMargin - this.mTopMargin;
            this.guide_bottom_re.setLayoutParams(layoutParams);
        }
    }

    public interface OnWindowDismissListener {
        void dismiss();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mWindowDismissListener != null) {
            mWindowDismissListener.dismiss();
        }
        return super.onTouchEvent(event);
    }

}
