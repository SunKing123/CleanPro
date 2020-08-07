package com.xiaoniu.cleanking.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.binioter.guideview.Component;
import com.comm.jksdk.utils.DisplayUtil;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.common.utils.DisplayUtils;

/**
 * 引导浮层 跳过按钮
 */
public class SkipComponent implements Component {
    private View.OnClickListener mClickListener;
    private int xoffset = 0;
    private int yoffset = 0;

    public SkipComponent(int xOff,int yOff,View.OnClickListener clickListener) {
        mClickListener = clickListener;
        xoffset = xOff;
        yoffset = yOff;
    }

    @Override
    public View getView(LayoutInflater inflater) {
        LinearLayout ll = new LinearLayout(inflater.getContext());
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(param);
        TextView textView = new TextView(inflater.getContext());
        textView.setText(inflater.getContext().getString(R.string.click_to_skip));
        textView.setTextColor(inflater.getContext().getResources().getColor(R.color.white));
        textView.setTextSize(17);
        ll.removeAllViews();
        ll.addView(textView);
        if (null != mClickListener) {
            ll.setOnClickListener(mClickListener);
        }
        return ll;
    }

    @Override
    public int getAnchor() {
        return Component.ANCHOR_TOP;
    }

    @Override
    public int getFitPosition() {
        return Component.FIT_END;
    }

    @Override
    public int getXOffset() {
        return this.xoffset;
    }

    @Override
    public int getYOffset() {
        return this.yoffset;
    }
}
