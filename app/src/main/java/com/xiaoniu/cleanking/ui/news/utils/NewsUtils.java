package com.xiaoniu.cleanking.ui.news.utils;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.NewsType;
import com.xiaoniu.cleanking.utils.ScreenUtil;
import com.xiaoniu.cleanking.widget.ScaleTransitionPagerTitleView;
import com.xiaoniu.cleanking.widget.magicIndicator.buildins.commonnavigator.abs.IPagerIndicator;
import com.xiaoniu.cleanking.widget.magicIndicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import com.xiaoniu.cleanking.widget.magicIndicator.buildins.commonnavigator.titles.SimplePagerTitleView;

/**
 * @Description:
 * @Author: Xiaodong.He
 * @Date: 2020/5/12
 */
public class NewsUtils {

    public static final NewsType[] sNewTypes = {NewsType.TOUTIAO, NewsType.SHEHUI, NewsType.GUOJI, NewsType.YUN_SHI, NewsType.JIAN_KANG, NewsType.REN_WEN};


    /**
     * 信息流是否关闭  TODO
     *
     * @return
     */
    public static boolean isFeedClosed() {
        return false;
    }



    public static SimplePagerTitleView getSimplePagerTitleView(Context context, String text) {
        SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
        simplePagerTitleView.setNormalColor(Color.parseColor("#000000"));    // black
        simplePagerTitleView.setSelectedColor(Color.parseColor("#06C581"));  // colorPrimary
        simplePagerTitleView.setTextSize(20);
        simplePagerTitleView.setText(text);
        int padding = ScreenUtil.dp2px(context, 3);
        simplePagerTitleView.setPadding(simplePagerTitleView.getPaddingLeft() - padding, simplePagerTitleView.getPaddingTop()
                , simplePagerTitleView.getPaddingRight() - padding, simplePagerTitleView.getPaddingBottom());
        return  simplePagerTitleView;
    }


    public static LinePagerIndicator getPagerIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        //设置宽度
        indicator.setLineWidth(ScreenUtil.dp2px(context,12));
        //设置高度
        indicator.setLineHeight(ScreenUtil.dp2px(context, 2));
        //设置颜色
        indicator.setColors(context.getResources().getColor(R.color.colorPrimary));
        //设置圆角
        indicator.setRoundRadius(1);
        //设置模式
        indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
        return indicator;
    }


}
