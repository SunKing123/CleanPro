package com.xiaoniu.cleanking.ui.news.utils;

import android.content.Context;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.NewsType;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.utils.ScreenUtil;
import com.xiaoniu.cleanking.widget.ScaleTransitionPagerTitleView;
import com.xiaoniu.cleanking.widget.magicIndicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import com.xiaoniu.cleanking.widget.magicIndicator.buildins.commonnavigator.titles.SimplePagerTitleView;

/**
 * @Description:
 * @Author: Xiaodong.He
 * @Date: 2020/5/12
 */
public class NewsUtils {

    public static final NewsType[] sNewTypes = {NewsType.TOUTIAO, NewsType.SHEHUI, NewsType.GUOJI, NewsType.YUN_SHI, NewsType.JIAN_KANG, NewsType.REN_WEN};

    private static boolean isHomeFeedOpen = true;
    private static boolean isMainTabNewsOpen = true;
    private static boolean isCleanFinishFeedOpen = true;

    public static void setIsHomeFeedOpen(boolean isHomeFeedOpen) {
        NewsUtils.isHomeFeedOpen = isHomeFeedOpen;
    }

    public static void setIsMainTabNewsOpen(boolean isMainTabNewsOpen) {
        NewsUtils.isMainTabNewsOpen = isMainTabNewsOpen;
    }

    public static void setIsCleanFinishFeedOpen(boolean isCleanFinishFeedOpen) {
        NewsUtils.isCleanFinishFeedOpen = isCleanFinishFeedOpen;
    }

    private static boolean isHomeFeedOpen() {
        return isHomeFeedOpen;
    }

    public static boolean isMainTabNewsOpen() {
        return isMainTabNewsOpen;
        // return AppHolder.getInstance().isOpen(PositionId.KEY_MAIN_TAB_NEWS, PositionId.AD_POSITION_MAIN_TAB_NEWS);
    }

    private static boolean isCleanFinishFeedOpen() {
        return isCleanFinishFeedOpen;
    }

    public static boolean isShowHomeFeed() {
        return !SPUtil.isInAudit() && isHomeFeedOpen();
    }

    public static boolean isShowCleanFinishFeed() {
        return !SPUtil.isInAudit() && isCleanFinishFeedOpen();
    }

    /**
     *
     * @param context
     * @param text
     * @param isWhiteBg
     * @return
     */
    public static SimplePagerTitleView getSimplePagerTitleView(Context context, String text, boolean isWhiteBg) {

        SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
        if (isWhiteBg) {
            simplePagerTitleView.setNormalColor(context.getResources().getColor(R.color.black));    // black
            simplePagerTitleView.setSelectedColor(context.getResources().getColor(R.color.colorPrimary));  // colorPrimary
        } else {
            simplePagerTitleView.setNormalColor(context.getResources().getColor(R.color.white));
            simplePagerTitleView.setSelectedColor(context.getResources().getColor(R.color.white));
        }
        simplePagerTitleView.setTextSize(20);
        simplePagerTitleView.setText(text);
        int padding = ScreenUtil.dp2px(context, 3);
        simplePagerTitleView.setPadding(simplePagerTitleView.getPaddingLeft() - padding, simplePagerTitleView.getPaddingTop()
                , simplePagerTitleView.getPaddingRight() - padding, simplePagerTitleView.getPaddingBottom());
        return  simplePagerTitleView;
    }


    public static LinePagerIndicator getPagerIndicator(Context context, boolean isWhiteBg) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        //设置宽度
        indicator.setLineWidth(ScreenUtil.dp2px(context,12));
        //设置高度
        indicator.setLineHeight(ScreenUtil.dp2px(context, 2));
        //设置颜色
        if (isWhiteBg) {
            indicator.setColors(context.getResources().getColor(R.color.colorPrimary));
        } else {
            indicator.setColors(context.getResources().getColor(R.color.white));
        }
        //设置圆角
        indicator.setRoundRadius(1);
        //设置模式
        indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
        return indicator;
    }


}
