package com.xiaoniu.cleanking.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.binioter.guideview.Component;
import com.xiaoniu.cleanking.R;

/**
 * 首页第一次引导
 */
public class FingerComponent implements Component {

  @Override public View getView(LayoutInflater inflater) {
    RelativeLayout ll = (RelativeLayout) inflater.inflate(R.layout.layer_finger_guide, null);
    return ll;
  }

  @Override public int getAnchor() {
    return Component.ANCHOR_BOTTOM;
  }

  @Override public int getFitPosition() {
    return Component.FIT_CENTER;
  }

  @Override public int getXOffset() {
    return 0;
  }

  @Override public int getYOffset() {
    return -32;
  }
}
