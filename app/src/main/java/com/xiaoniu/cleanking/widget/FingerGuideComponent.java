package com.xiaoniu.cleanking.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.binioter.guideview.Component;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.ui.main.event.GuideViewClickEvent;
import com.xiaoniu.cleanking.utils.anim.AnimationScaleUtils;
import com.xiaoniu.common.utils.DisplayUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 首页第一次引导
 */
public class FingerGuideComponent implements Component {
    private View.OnClickListener onmClickListener;
    public FingerGuideComponent(View.OnClickListener clickListener) {
        onmClickListener = clickListener;
    }

    @Override
    public View getView(LayoutInflater inflater) {
        RelativeLayout ll = (RelativeLayout) inflater.inflate(R.layout.layer_finger_guide, null);
        TextView iv_guide_tag00 = ll.findViewById(R.id.iv_guide_tag00);
        ImageView iv_guide_tag03 = ll.findViewById(R.id.iv_guide_tag03);
        RelativeLayout.LayoutParams relP = (RelativeLayout.LayoutParams) iv_guide_tag00.getLayoutParams();
        int clickAreaTag = Float.valueOf((DisplayUtils.getScreenWidth() / 1.5f)).intValue();
        relP.height = clickAreaTag;
        relP.width = clickAreaTag;
        iv_guide_tag00.setLayoutParams(relP);
        iv_guide_tag00.setOnClickListener(onmClickListener);
        iv_guide_tag03.setOnClickListener(onmClickListener);
//        AnimationScaleUtils.getInstance().playScaleAnimation(iv_guide_tag03, 1000);
        return ll;
    }

    @Override
    public int getAnchor() {
        return Component.ANCHOR_BOTTOM;
    }

    @Override
    public int getFitPosition() {
        return Component.FIT_CENTER;
    }

    @Override
    public int getXOffset() {
        return 0;
    }

    @Override
    public int getYOffset() {
        return -DisplayUtils.px2dip(AppApplication.getInstance(), Float.valueOf((DisplayUtils.getScreenWidth() / 1.7f)).intValue());
    }


}
