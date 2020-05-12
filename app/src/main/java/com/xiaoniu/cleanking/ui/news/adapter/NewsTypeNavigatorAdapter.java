package com.xiaoniu.cleanking.ui.news.adapter;

import android.content.Context;
import android.view.View;

import com.xiaoniu.cleanking.ui.main.bean.NewsType;
import com.xiaoniu.cleanking.ui.news.utils.NewsUtils;
import com.xiaoniu.cleanking.widget.magicIndicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import com.xiaoniu.cleanking.widget.magicIndicator.buildins.commonnavigator.abs.IPagerIndicator;
import com.xiaoniu.cleanking.widget.magicIndicator.buildins.commonnavigator.abs.IPagerTitleView;
import com.xiaoniu.cleanking.widget.magicIndicator.buildins.commonnavigator.titles.SimplePagerTitleView;

/**
 * @Description:
 * @Author: Xiaodong.He
 * @Date: 2020/5/12
 */
public class NewsTypeNavigatorAdapter extends CommonNavigatorAdapter {

    private OnClickListener mOnClickListener;

    private boolean isWhiteBg;

    private NewsType[] mNewTypes = {NewsType.TOUTIAO, NewsType.SHEHUI, NewsType.GUONEI, NewsType.GUOJI, NewsType.YULE};


    public NewsTypeNavigatorAdapter(NewsType[] mNewTypes, boolean isWhiteBg) {
        this.mNewTypes = mNewTypes;
        this.isWhiteBg = isWhiteBg;
    }

    @Override
    public int getCount() {
        return mNewTypes == null ? 0 : mNewTypes.length;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        SimplePagerTitleView simplePagerTitleView = NewsUtils.getSimplePagerTitleView(context, mNewTypes[index].getName(), isWhiteBg);
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClickTitleView(index);
                }
            }
        });
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        return NewsUtils.getPagerIndicator(context, isWhiteBg);
    }


    public void setOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public interface OnClickListener {
        void onClickTitleView(int index);
    }

}



