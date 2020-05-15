package com.xiaoniu.cleanking.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.AppHolder;
//import com.xiaoniu.cleanking.base.UmengEnum;
//import com.xiaoniu.cleanking.base.UmengUtils;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.news.utils.NewsUtils;
import com.xiaoniu.common.utils.StatisticsUtils;

/**
 * @Description:
 * @Author: Xiaodong.He
 * @Date: 2020/5/13
 */
public class MainBottomBarUtil {

    private static int PAGE_INDEX_LIFE = -1;  // 生活页
    private static int PAGE_INDEX_CLEAN = 0;  // 清理页
    private static int PAGE_INDEX_TOOL = 1;   // 工具页
    private static int PAGE_INDEX_NEWS = 2;   // 资讯
    private static int PAGE_INDEX_MINE = 3;   // 我的页面

    private static final String PAGE_HOME = "home_page";
    private static final String PAGE_TOOL = "tool_page";
    private static final String PAGE_NEWS = "selected_page";
    private static final String PAGE_MINE = "mine_page";

    private static final String EVENT_CODE_HOME_CLICK = "home_click";
    private static final String EVENT_CODE_TOOL_CLICK = "tool_click";
    private static final String EVENT_CODE_NEWS_CLICK = "selected_click";
    private static final String EVENT_CODE_MINE_CLICK = "mine_click";

    private static String sEventCode = "home_click";
    private static String sCurrentPage = "";
    private static String sSourcePage = "";

    public static int getCleanPageIndex() {
        return PAGE_INDEX_CLEAN;
    }

    public static int getToolPageIndex() {
        return PAGE_INDEX_TOOL;
    }

    public static int getLifePageIndex() {
        return PAGE_INDEX_LIFE;
    }

    public static int getNewsPageIndex() {
        return PAGE_INDEX_NEWS;
    }

    public static int getMinePageIndex() {
        return PAGE_INDEX_MINE;
    }

    public static void initPageIndex(boolean isAuditMode, boolean isMainTabNewsOpen) {
        if (isAuditMode) {
            PAGE_INDEX_CLEAN = 0;
            PAGE_INDEX_MINE = 1;
        } else {
            if (isMainTabNewsOpen) {
                PAGE_INDEX_CLEAN = 0;
                PAGE_INDEX_TOOL = 1;
                PAGE_INDEX_NEWS = 2;
                PAGE_INDEX_MINE = 3;
            } else {
                PAGE_INDEX_CLEAN = 0;
                PAGE_INDEX_TOOL = 1;
                PAGE_INDEX_MINE = 3;
            }
        }
    }

    public static boolean isShowNewsTab() {
        return !SPUtil.isInAudit() & NewsUtils.isMainTabNewsOpen();
    }

    /**
     *
     * @param context
     * @param position
     * @param prePosition
     */
    public static void onShowHideFragment(Context context, int position, int prePosition) {
        updatePageIndex(context, position, prePosition);
        Log.d("MainTabConfigUtil", "!--->onShowHideFragment--position:"+position+"; prePosition:"+prePosition+"; sourcePage:"+ sSourcePage +"; currentPage:"+ sCurrentPage);
        //保存选中的currentPage 为 上级页面id
        AppHolder.getInstance().setSourcePageId(sCurrentPage);
        //默认二级选中页面为当前页面
        AppHolder.getInstance().setOtherSourcePageId(sCurrentPage);
        StatisticsUtils.trackClick(sEventCode, "底部icon点击", sSourcePage, sCurrentPage);

        // TODO: Umeng event is no used
//        if (position == PAGE_INDEX_CLEAN) {
//            UmengUtils.event(context, UmengEnum.Tab_jiekuan); // ???
//        } else if (position == PAGE_INDEX_MINE) {
//            UmengUtils.event(context, UmengEnum.Tab_wode);   //  ???
//        } else if (position == PAGE_INDEX_NEWS) {
//        } else if (position == PAGE_INDEX_TOOL) {
//        }
    }


    private static void updatePageIndex(Context context, int position, int prePosition) {
        boolean isAuditMode = SPUtil.isInAudit();
        boolean isMainTabNewsOpen = NewsUtils.isMainTabNewsOpen();
        if (isAuditMode) {
            updatePageAuditMode(position, prePosition);    // clean  mine
        } else {
            if (isMainTabNewsOpen) {
                updatePageIndexAll(position, prePosition); // all
            } else {
                updatePageNoNews(position, prePosition);   // clean  tool  mine
            }
        }
    }

    private static void updatePageAuditMode(int position, int prePosition) {
        if (position == 0) {
            sEventCode = EVENT_CODE_HOME_CLICK;
            sCurrentPage = PAGE_HOME;
        } else if (position == 1) {
            sEventCode = EVENT_CODE_MINE_CLICK;
            sCurrentPage = PAGE_MINE;
        }
        if (prePosition == 0) {
            sSourcePage = PAGE_HOME;
        } else if (prePosition == 1) {
            sSourcePage = PAGE_MINE;
        }
    }

    private static void updatePageNoNews(int position, int prePosition) {
        if (position == 0) {
            sEventCode = EVENT_CODE_HOME_CLICK;
            sCurrentPage = PAGE_HOME;
        } else if (position == 1) {
            sEventCode = EVENT_CODE_TOOL_CLICK;
            sCurrentPage = PAGE_TOOL;
        } else if (position == 2) {
            sEventCode = EVENT_CODE_MINE_CLICK;
            sCurrentPage = PAGE_MINE;
        }
        if (prePosition == 0) {
            sSourcePage = PAGE_HOME;
        } else if (prePosition == 1) {
            sSourcePage = PAGE_TOOL;
        } else if (prePosition == 2) {
            sSourcePage = PAGE_MINE;
        }
    }

    private static void updatePageIndexAll(int position, int prePosition) {
        if (position == 0) {
            sEventCode = EVENT_CODE_HOME_CLICK;
            sCurrentPage = PAGE_HOME;
        } else if (position == 1) {
            sEventCode = EVENT_CODE_TOOL_CLICK;
            sCurrentPage = PAGE_TOOL;
        } else if (position == 2) {
            sEventCode = EVENT_CODE_NEWS_CLICK;
            sCurrentPage = PAGE_NEWS;
        } else if (position == 3) {
            sEventCode = EVENT_CODE_MINE_CLICK;
            sCurrentPage = PAGE_MINE;
        }
        if (prePosition == 0) {
            sSourcePage = PAGE_HOME;
        } else if (prePosition == 1) {
            sSourcePage = PAGE_TOOL;
        } else if (prePosition == 2) {
            sSourcePage = PAGE_NEWS;
        } else if (prePosition == 3) {
            sSourcePage = PAGE_MINE;
        }
    }

}
