package com.hellogeek.permission.util;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;


import com.hellogeek.permission.bean.CustomSharedPreferences;
import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.server.AccessibilityServiceMonitor;
import com.hellogeek.permission.widget.GuidePWindowView;
import com.hellogeek.permission.widget.SemiAutomaticToastGuide;
import com.hellogeek.permission.widget.floatwindow.FloatingWindow;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class NodeInfoUtil {
    public static boolean pageContains(AccessibilityNodeInfo nodeInfo, String keyword) {
        if (nodeInfo == null) return false;
        final List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(keyword);
        recycleAll(list);
        return AccessibilitUtil.isNotEmpty(list);
    }

    private static void recycleAll(List<AccessibilityNodeInfo> list) {
        if (AccessibilitUtil.isEmpty(list)) return;
        for (AccessibilityNodeInfo nodeInfo : list) {
            recycle(nodeInfo);
        }
    }

    /**
     * Recycle nodeInfo, and the nodeInfo shouldn't be used after recycling.
     */
    public static void recycle(AccessibilityNodeInfo nodeInfo) {
        try {
            nodeInfo.recycle();
        } catch (Exception e) {
        }
    }


    public static boolean clickNodeInfo(AccessibilityNodeInfo nodeInfo, String keyword) {
        if (nodeInfo == null) return false;
        List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(keyword);
        if (nodes != null && nodes.size() > 0) {
            boolean isOpen = clickNodeIsNoChecked(nodeInfo, keyword, 3);
            recycleAll(nodes);
            return isOpen;
        } else {
            scrollableList(nodeInfo);
        }
        return false;
    }

    public static boolean clickNodeInfoAll(Context context, AccessibilityNodeInfo nodeInfo, String tagText) {
        if (CustomSharedPreferences.getBooleanValue(context, CustomSharedPreferences.isManual)) {
            CustomSharedPreferences.setValue(context, CustomSharedPreferences.isManual, false);
        }
        boolean isOpen = false;
        if (!PermissionIntegrate.getPermission().getIsManual()) {
            isOpen = NodeInfoUtil.clickNodeInfo(nodeInfo, tagText);
            if (!isOpen) {
                isOpen = NodeInfoUtil.performSwitch(nodeInfo, tagText, false);
            }
            if (!isOpen) {
                isOpen = NodeInfoUtil.clickNodeIsFor(nodeInfo, tagText);
            }
        }
        if (!isOpen) {
            List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(tagText);
            if (nodes != null && nodes.size() > 0) {
                Rect rect = new Rect(0, 0, 0, 0);
                if (nodes.get(0).getParent() != null) {
                    nodes.get(0).getParent().getBoundsInScreen(rect);
                } else if (nodes.get(0) != null) {
                    nodes.get(0).getBoundsInScreen(rect);
                }
                if (tagText.contains("悬浮窗")) {
                    floaWindowToast(context, rect);
                } else {
                    floaWindow(context, rect);
                }
                Log.e("dong", tagText);
                if (!CustomSharedPreferences.getBooleanValue(context, CustomSharedPreferences.isManual)) {
                    CustomSharedPreferences.setValue(context, CustomSharedPreferences.isManual, true);
                }
                return false;
            }
        }
        return isOpen;

    }

    public static boolean clickNodeInfoAll(Context context, AccessibilityNodeInfo nodeInfo, String tagText, int isToast) {
        if (CustomSharedPreferences.getBooleanValue(context, CustomSharedPreferences.isManual)) {
            CustomSharedPreferences.setValue(context, CustomSharedPreferences.isManual, false);
        }
        boolean isOpen = false;
        if (!PermissionIntegrate.getPermission().getIsManual()) {
            isOpen = NodeInfoUtil.clickNodeInfo(nodeInfo, tagText);
            if (!isOpen) {
                isOpen = NodeInfoUtil.performSwitch(nodeInfo, tagText, false);
            }
            if (!isOpen) {
                isOpen = NodeInfoUtil.clickNodeIsFor(nodeInfo, tagText);
            }
        }
        if (!isOpen) {
            List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(tagText);
            if (nodes != null && nodes.size() > 0) {
                Rect rect = new Rect(0, 0, 0, 0);
                nodes.get(0).getParent().getBoundsInScreen(rect);
                if (tagText.contains("悬浮窗") || isToast == 1) {
                    floaWindowToast(context, rect);
                } else {
                    floaWindow(context, rect);
                }
                Log.e("dong", tagText);
                if (!CustomSharedPreferences.getBooleanValue(context, CustomSharedPreferences.isManual)) {
                    CustomSharedPreferences.setValue(context, CustomSharedPreferences.isManual, true);
                }
                return false;
            }
        }
        return isOpen;

    }


    public static boolean clickNodeInfoAll(Context context, AccessibilityNodeInfo nodeInfo, String tagText, boolean isClose, boolean isShow) {
        if (CustomSharedPreferences.getBooleanValue(context, CustomSharedPreferences.isManual)) {
            CustomSharedPreferences.setValue(context, CustomSharedPreferences.isManual, false);
        }
        boolean isOpen = false;
        if (!PermissionIntegrate.getPermission().getIsManual()) {
            isOpen = NodeInfoUtil.performSwitch(nodeInfo, tagText, isClose);
            if (!isOpen) {
                isOpen = NodeInfoUtil.clickNodeInfo(nodeInfo, tagText);
            }
            if (!isOpen) {
                isOpen = NodeInfoUtil.clickNodeIsFor(nodeInfo, tagText);
            }
        }
        if (!isOpen) {
            List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(tagText);
            if (nodes != null && nodes.size() > 0) {
                Rect rect = new Rect(0, 0, 0, 0);
                nodes.get(0).getParent().getBoundsInScreen(rect);
                if (tagText.contains("悬浮窗")) {
                    floaWindowToast(context, rect);
                } else {
                    floaWindow(context, rect);
                }
                Log.e("dong", tagText);
                if (!CustomSharedPreferences.getBooleanValue(context, CustomSharedPreferences.isManual)) {
                    CustomSharedPreferences.setValue(context, CustomSharedPreferences.isManual, true);
                }
                return false;
            }
        }
        return isOpen;

    }


    public static boolean clickNodeInfoAll(Context context, AccessibilityNodeInfo nodeInfo, String tagText, boolean isShow) {

        boolean isOpen = false;
        if (!PermissionIntegrate.getPermission().getIsManual()) {
            isOpen = NodeInfoUtil.clickNodeInfo(nodeInfo, tagText);
            if (!isOpen) {
                isOpen = NodeInfoUtil.performSwitch(nodeInfo, tagText);
            }
            if (!isOpen) {
                isOpen = NodeInfoUtil.clickNodeIsFor(nodeInfo, tagText);
            }
        }
        if (!isOpen && !isShow) {
            List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(tagText);
            if (nodes != null && nodes.size() > 0) {
                Rect rect = new Rect(0, 0, 0, 0);
                nodes.get(0).getParent().getBoundsInScreen(rect);
                if (tagText.contains("悬浮窗")) {
                    floaWindowToast(context, rect);
                } else {
                    floaWindow(context, rect);
                }
                Log.e("dong", tagText);
                return false;
            }
        }
        return isOpen;

    }

    public static boolean performSwitch(final AccessibilityNodeInfo nodeInfo, String tagText) {
        final List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(tagText);

        boolean res = false;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                final AccessibilityNodeInfo nodeSwitch = getSwitchableNode(list.get(i));
                if (nodeSwitch != null) {
                    res = click(nodeSwitch);
                    recycle(nodeSwitch);
                    if (res) {
                        // TODO: 2018/12/24 think about it again.
                        break;
                    }
                }
            }
        }
        recycleAll(list);
        return res;
    }


    public static boolean performSwitch(final AccessibilityNodeInfo nodeInfo, String tagText, boolean isOpen) {
        final List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(tagText);

        boolean res = false;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                final AccessibilityNodeInfo nodeSwitch = getSwitchableNode(list.get(i));
                if (nodeSwitch != null) {
                    res = click(nodeSwitch, false, isOpen);
                    recycle(nodeSwitch);
                    if (res) {
                        // TODO: 2018/12/24 think about it again.
                        break;
                    }
                }
            }
        }
        recycleAll(list);
        return res;
    }

    public static AccessibilityNodeInfo getSwitchableNode(AccessibilityNodeInfo node) {
        AccessibilityNodeInfo item;
        while (node != null) {
            item = getSwitchRecursive(node);
            if (item == null) {
                final AccessibilityNodeInfo temp = node;
                node = node.getParent();
                recycle(temp);
            } else {
                return item;
            }
        }
        return null;
    }

    private static final String[] switchNames = new String[]{
            "android.widget.Switch",
            "androidx.appcompat.widget.SwitchCompat",
            "android.widget.CheckBox"
    };
    private static final Set<String> switcherSet = new HashSet<>(Arrays.asList(switchNames));

    private static AccessibilityNodeInfo getSwitchRecursive(AccessibilityNodeInfo node) {
        if (node != null && node.getClassName() != null && switcherSet.contains(node.getClassName().toString())) {
            return node;
        }

        if (node == null ? true : node.getChildCount() == 0) {
            return null;
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            final AccessibilityNodeInfo item = getSwitchRecursive(node.getChild(i));
            if (item != null) {
                return item;
            }
        }

        return null;
    }

    public static int scrollNum = 0;
    public static int direction = 0;

    /**
     * 滑动列表查找-加锁防止多次滑动
     */
    public static synchronized boolean scrollableList(AccessibilityNodeInfo rootNodeInfo) {
        boolean isScoll = false;
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scrollNum++;
        if (scrollNum == 5) {
            direction = (direction == 0 ? 1 : 0);
            scrollNum = 0;
        }
        if (!PermissionIntegrate.getPermission().getIsManual()) {
            isScoll = scroll(getScrollableNode(rootNodeInfo), direction);
            if (!isScoll) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    isScoll = scollForLast(AccessibilityServiceMonitor.getInstance().getRootInActiveWindow());
                }
            }
        }
        return isScoll;
    }


    /**
     * 遍历查找节点
     */
    private static boolean scroll(AccessibilityNodeInfo nodeInfo, int direction) {
        boolean res = false;
        final int dire = (direction == 0 ? AccessibilityNodeInfo.ACTION_SCROLL_FORWARD : AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        if (nodeInfo != null) {
            if (nodeInfo != null && nodeInfo.isScrollable()) {
                try {
                    res = nodeInfo.performAction(dire);
                } catch (Exception e) {
                } finally {
                    recycle(nodeInfo);
                }
            } else {
            }
        } else {
        }
        return res;
    }

    public static AccessibilityNodeInfo getScrollableNode(AccessibilityNodeInfo info) {
        // info is scrollable.
        if (isScrollView(info)) return info;
        final Queue<AccessibilityNodeInfo> queue = new LinkedList<>();
        queue.add(info);
        while (!queue.isEmpty()) {
            final AccessibilityNodeInfo head = queue.poll();
            if (isScrollView(head)) {
                return head;
            } else {
                for (int i = 0; head != null && i < head.getChildCount(); i++) {
                    queue.add(head.getChild(i));
                }
            }
        }
        return null;
    }

    private static boolean isScrollView(AccessibilityNodeInfo info) {
        if (info != null && info.getClassName() != null) {
            final String className = info.getClassName().toString();
            if (className.equals("android.widget.ListView")
                    || className.equals("android.widget.GridView")
                    || className.toLowerCase().contains("recyclerview")
                    || className.toLowerCase().contains("scrollview")
                    || className.toLowerCase().contains("expandablelistview")
            ) {
                return true;
            }
        }
        return false;
    }

    public static boolean clickNodeIsFor(AccessibilityNodeInfo parent, String target) {
        if (parent != null && !TextUtils.isEmpty(target)) {
            if (parent != null && !TextUtils.isEmpty(target)) {
                final List<AccessibilityNodeInfo> list = parent.findAccessibilityNodeInfosByText(target);
                if (list != null && list.size() > 0) {
                    AccessibilityNodeInfo node = null;
                    if (list.size() > 1) {
                        for (AccessibilityNodeInfo n : list) {
                            node = n;
                            if (node != null) {
                                if (click(node)) {
                                    return true;
                                }
                            }
                        }
                    } else {
                        return click(list.get(0));
                    }
                }
            }
        }
        return false;
    }

    /**
     * 点击节点
     *
     * @param parent
     * @param target
     * @param tryParentNodeLevelCount
     */
    private static boolean clickNodeIsNoChecked(AccessibilityNodeInfo parent, String target,
                                                int tryParentNodeLevelCount) {

        if (parent != null && !TextUtils.isEmpty(target)) {
            final List<AccessibilityNodeInfo> list = parent.findAccessibilityNodeInfosByText(target);
            if (list != null && list.size() > 0) {
                AccessibilityNodeInfo node = null;
                if (list.size() > 1) {
                    for (AccessibilityNodeInfo n : list) {
                        if (n.isCheckable()) {
                            node = n;
                            break;
                        }
                    }
                }
                if (node == null) {
                    node = list.get(0);
                }
                if (click(node, true)) {
                    return true;
                } else {
                    AccessibilityNodeInfo parentNode = node;
                    for (int i = 0; i < tryParentNodeLevelCount; i++) {
                        if (parentNode != null) {
                            final AccessibilityNodeInfo temp = parentNode;
                            try {
                                parentNode = parentNode.getParent();
                            } catch (Exception e) {
                            }
                            recycle(temp);
//                            if (isChecked(parentNode)) {
                            if (click(parentNode)) {
                                return true;
                            }
//                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    public static boolean isChecked(AccessibilityNodeInfo nodeInfo) {
        return nodeInfo != null && nodeInfo.isCheckable();
    }

    public static boolean click(AccessibilityNodeInfo nodeInfo) {
        return click(nodeInfo, false, false);
    }

    public static boolean click(AccessibilityNodeInfo nodeInfo,
                                boolean dontRecycleWhenActionFailed) {
        return click(nodeInfo, dontRecycleWhenActionFailed, false);
    }

    public static boolean click(AccessibilityNodeInfo nodeInfo,
                                boolean dontRecycleWhenActionFailed,
                                boolean isOpen) {
        boolean res = false;
        if (nodeInfo != null) {
            try {
                if (((!isOpen) ? !nodeInfo.isChecked() : nodeInfo.isChecked())) {
                    res = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                } else if (!TextUtils.isEmpty(nodeInfo.getText()) && nodeInfo.isClickable()) {
                    res = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                } else {
                    res = nodeInfo.isChecked();
                }
            } catch (Exception e) {
            } finally {
                if (dontRecycleWhenActionFailed) {
                    if (res) {
                        recycle(nodeInfo);
                    }
                } else {
                    recycle(nodeInfo);
                }
            }

        } else {
        }
        return res;
    }

//
//    /**
//     * @return An action result.
//     */
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    public static boolean back(AccessibilityService service) {
//        boolean res = false;
//        if (service != null) {
//            try {
//                res = service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
//            } catch (Exception e) {
//            }
//        } else {
//        }
//        return res;
//    }


    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

        }
    }

    public static boolean scollForLast(AccessibilityNodeInfo rootNode) {
        if (rootNode == null) {
            return false;
        }
        if (rootNode.getChildCount() <= 0) {
            return false;
        }
        for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {
            AccessibilityNodeInfo node = rootNode.getChild(i);
            //如果node为空则跳过该节点
            if (node == null) {
                continue;
            }
            if (node.isScrollable()) {
                //模拟点击
                return node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            } else {
                AccessibilityNodeInfo parent = node.getParent();
                while (parent != null) {
                    if (parent.isScrollable()) {
                        //模拟点击
                        return parent.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    }
                    parent = parent.getParent();
                }
            }
        }
        return false;
    }

    private static FloatingWindow mFloatWindow;

    public static synchronized void floaWindow(Context mContext, Rect rect) {
        FloatingWindow mFloatWindow = new FloatingWindow(mContext);
        GuidePWindowView callInWindowView = new GuidePWindowView(mContext);
        callInWindowView.setWindowDismissListener(() -> mFloatWindow.dismiss());
        mFloatWindow.setContentView(callInWindowView);
        if (!mFloatWindow.isShowing()) mFloatWindow.show();
        ((GuidePWindowView) mFloatWindow.getContentView()).setData(rect.top, rect.bottom);
//
    }


    public static void floaWindowToast(Context mContext, Rect rect) {
        new SemiAutomaticToastGuide().make(rect, mContext.getApplicationContext());
    }


}
