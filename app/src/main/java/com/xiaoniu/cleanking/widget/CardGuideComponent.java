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
public class CardGuideComponent implements Component {


    private View.OnClickListener onmClickListener;
    public CardGuideComponent(View.OnClickListener clickListener) {
        onmClickListener = clickListener;
    }

    @Override
    public View getView(LayoutInflater inflater) {
        RelativeLayout ll = (RelativeLayout) inflater.inflate(R.layout.layer_card_guide, null);
        ImageView iv_guide03_tag04 = (ImageView)ll.findViewById(R.id.iv_guide03_tag04);
        ImageView iv_guide03_tag02 = (ImageView)ll.findViewById(R.id.iv_guide03_tag02);
        iv_guide03_tag02.setOnClickListener(onmClickListener);
        iv_guide03_tag04.setOnClickListener(onmClickListener);
        AnimationScaleUtils.getInstance().playScaleAnimation(iv_guide03_tag02, 1000);
        return ll;
    }

    @Override
    public int getAnchor() {
        return Component.ANCHOR_TOP;
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
        return 52;
    }


}
