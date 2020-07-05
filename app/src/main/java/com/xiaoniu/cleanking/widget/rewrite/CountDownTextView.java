package com.xiaoniu.cleanking.widget.rewrite;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.xiaoniu.cleanking.R;


/**
 * Created by fangmingdong on 2017/1/6.
 * 验证码倒计时
 */

public class CountDownTextView extends androidx.appcompat.widget.AppCompatTextView {
    public static final int COUNT = 60;
    private static final int DELAY = 1000;
    private int count = COUNT;
    private Handler mHandlerDialog;
    private int mWidth;
    private String mFormat = "%ss";

    public CountDownTextView(Context context) {
        super(context);
        init();
    }

    public CountDownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CountDownTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWidth > 0) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void init() {
        mHandlerDialog = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (count <= 0) {
                    return;
                }
                //setText(Html.fromHtml(String.format("<font color=\"#356BFE\">%s</font>" +"秒后重新获取", --count)));
                setText(String.format("%ss", --count));
                if (count <= 0 && !isEnabled()) {
                    setEnabled(true);
                    setTextColor(getResources().getColor(R.color.color_26DFB0));
                    setText("重新获取");
                    mHandlerDialog.removeMessages(0);
                } else {
                    mHandlerDialog.sendMessageDelayed(mHandlerDialog.obtainMessage(0), 1000);
                }
            }
        };
    }

    public void setFormat(String format) {
        mFormat = format;
    }

    public void startDialog() {
        mHandlerDialog.removeMessages(0);
        count = COUNT;
        setEnabled(false);
        setText(String.format("%ss", --count));
        setTextColor(getResources().getColor(R.color.color_c1c0c8));
        mHandlerDialog.sendMessageDelayed(mHandlerDialog.obtainMessage(0), 1000);
    }


}
