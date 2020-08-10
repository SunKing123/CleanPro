package com.xiaoniu.cleanking.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.binioter.guideview.Component;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.event.GuideViewClickEvent;
import com.xiaoniu.cleanking.utils.anim.AnimationRotateUtils;
import com.xiaoniu.cleanking.utils.anim.AnimationScaleUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 首页第二次引导
 */
public class GoldGuideComponent implements Component {
    private View.OnClickListener onmClickListener;
    public GoldGuideComponent(View.OnClickListener clickListener) {
        onmClickListener = clickListener;
    }

    @Override
    public View getView(LayoutInflater inflater) {
        RelativeLayout ll = (RelativeLayout) inflater.inflate(R.layout.layer_gold_guide, null);
        ImageView gold = (ImageView)ll.findViewById(R.id.iv_guide02_tag01);
        ImageView iv_guide02_tag04 = (ImageView)ll.findViewById(R.id.iv_guide02_tag04);
        gold.setOnClickListener(onmClickListener);
        iv_guide02_tag04.setOnClickListener(onmClickListener);
        AnimationScaleUtils.getInstance().playScaleAnimation(iv_guide02_tag04, 500);

        return ll;
    }

    @Override
    public int getAnchor() {
        return Component.ANCHOR_BOTTOM;
    }

    @Override
    public int getFitPosition() {
        return Component.FIT_END;
    }

    @Override
    public int getXOffset() {
        return 0;
    }

    @Override
    public int getYOffset() {
        return -68;
    }


}
