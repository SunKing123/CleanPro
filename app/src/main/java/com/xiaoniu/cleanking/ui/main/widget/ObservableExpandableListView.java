package com.xiaoniu.cleanking.ui.main.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by lang.chen on 2019/8/8
 */
public class ObservableExpandableListView extends ExpandableListView {

    private ObservableScrollView.ScrollViewListener scrollViewListener;
    public ObservableExpandableListView(Context context) {
        super(context);
    }

    public ObservableExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if(null!=scrollViewListener){
            scrollViewListener.onScrollChanged(l,t,oldl,oldt);
        }
    }



    public void setViewListener(ObservableScrollView.ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }
}
