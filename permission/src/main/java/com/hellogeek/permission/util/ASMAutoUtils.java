package com.hellogeek.permission.util;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;


import com.hellogeek.permission.bean.ASBase;
import com.hellogeek.permission.bean.ASStepBean;
import com.hellogeek.permission.server.AccessibilityServiceMonitor;
import com.hellogeek.permission.vivoHelp.VivoPermissionUtils;

import java.util.List;

/**
 * Desc:
 * <p>
 *
 * @author: ZhouTao
 * Date: 2019/6/28
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update
 */
public class ASMAutoUtils {
    private static final String TAG = "ASMAutoUtils";
    static ASMAutoUtils mASMAutoUtils;
    ASMListener mASMListener;
    ASBase mBaseData;
    private final DelayedHandler mHandler;
    /**
     * 跳转界面
     */
    public static final int WHAT_JUMP = 1;
    /**
     * 查找结点
     */
    public static final int WHAT_FIND = 2;
    /**
     * 单步完成
     */
    public static final int WHAT_COMPLETE = 3;
    private final int TOTAL_COUNT = 2000;
    private int COUNT = 0;
    private boolean isFind = false;
    private boolean isDebug = true;
    private boolean isScrollableComplete = false;
    private int stepIndex = 0;
    private boolean singleStep = false;
    private boolean isPause = false;
    private Context mContext;
    public static final int PERMISSION_FLOAT = 1;
    public static final int PERMISSION_LOCK_SHOW = 2;
    public static final int PERMISSION_SYSTEM_SETTING = 3;
    public static final int PERMISSION_STARTUP = 4;
    public static final int PERMISSION_NOTIFY = 5;
    public static final int PERMISSION_ACTIVITY = 6;

    public static ASMAutoUtils getInstance() {
        if (mASMAutoUtils == null) {
            mASMAutoUtils = new ASMAutoUtils();
        }
        return mASMAutoUtils;
    }

    public ASMAutoUtils() {
        mHandler = new DelayedHandler();
    }

    public void setListener(ASMListener mASMListener) {
        this.mASMListener = mASMListener;
    }

    public void removeListener(ASMListener mASMListener) {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        this.mASMListener = null;
    }

    public void start(ASBase data) {
        start(data, false);
    }

    public void start(ASBase data, boolean singleStep) {
        Log.d(TAG, "start: " + data.describe);
        this.singleStep = singleStep;
        mBaseData = data;
        mHandler.removeMessages(WHAT_JUMP);
        mHandler.sendEmptyMessageDelayed(WHAT_JUMP, mBaseData.delay_time);
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }


    public void setContext(Context context) {
        mContext = context;
    }

    class DelayedHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_JUMP:
                    COUNT = 0;
                    stepIndex = 0;
                    isPause = false;
                    if (mASMListener != null) {
                        mASMListener.jumpActivity(mBaseData.intent);

                        mHandler.removeMessages(WHAT_FIND);
                        mHandler.sendEmptyMessageDelayed(WHAT_FIND, mBaseData.step.get(stepIndex).delay_time);
                    }
                    break;
                case WHAT_FIND:
                    if (isPause) {
                        mHandler.removeMessages(WHAT_FIND);
                        mHandler.sendEmptyMessageDelayed(WHAT_FIND, mBaseData.delay_time);
                        checkPermission();
                        return;
                    }
                    isFind = false;
                    List<ASStepBean> step = mBaseData.step;
                    if (step.size() > 0 && stepIndex < step.size()) {
                        ASStepBean stepBean = step.get(stepIndex);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            if (stepBean.getClick_type().equals("system")) {
                                systemAction(stepBean);
                            } else {
                                if (AccessibilityServiceMonitor.instance == null) {
                                    return;
                                }
                                AccessibilityNodeInfo rootInActiveWindow = null;
                                try{
                                    rootInActiveWindow = AccessibilityServiceMonitor.instance.getRootInActiveWindow();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                if (rootInActiveWindow != null) {
                                    CharSequence packageName = rootInActiveWindow.getPackageName();
                                    if (TextUtils.equals(packageName, AccessibilityServiceMonitor.instance.getPackageName())) {
                                        mHandler.removeMessages(WHAT_FIND);
                                        mHandler.sendEmptyMessageDelayed(WHAT_FIND, mBaseData.delay_time);
                                        return;
                                    }
                                }
                                findClickView(rootInActiveWindow, stepBean);
                                if (isFind) {
                                    stepIndex++;
                                } else {
                                    isScrollableComplete = false;
                                    findScrollableView(rootInActiveWindow, stepBean);
                                    mHandler.removeMessages(WHAT_FIND);
                                    mHandler.sendEmptyMessageDelayed(WHAT_FIND, stepBean.delay_time / 4);
                                }
                            }
                        }
                    } else {
                        mHandler.removeMessages(WHAT_COMPLETE);
                        mHandler.sendEmptyMessageDelayed(WHAT_COMPLETE, mBaseData.delay_time);
                    }
                    break;
                case WHAT_COMPLETE:
                    if (mASMListener != null) {
                        mASMListener.complete(mBaseData);
                    }
                    break;
                default:
            }
        }
    }

    private void checkPermission() {
        boolean isAllow = false;
        switch (mBaseData.type_id) {
            case PERMISSION_FLOAT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("vivo X7Plus")
                        || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("vivo X7")) {
                    isAllow = VivoPermissionUtils.getFloatPermissionStatusOld(mContext) == 0;
                } else if (PhoneRomUtils.getPhoneModel().equalsIgnoreCase("Oppo A59m") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("CUN-AL00")
                        || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("V1809T") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("vivo X21UD A")
                        || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("V1831A") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("V1813A")
                        || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("vivo x20a") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("V1732A")
                        || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("V1818A") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("V1818CA")
                        || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("V1731CA") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("Vivo Y83A")
                        || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("Oppo A59S")) {
                    isAllow = OppoUtils.isOppoNotificationOpen(mContext);
                } else {
                    isAllow = true;
                }
                break;
            case PERMISSION_LOCK_SHOW:
                isAllow = VivoPermissionUtils.getVivoLockedScreenPermissionStatus(mContext) == 0;
                break;
            case PERMISSION_SYSTEM_SETTING:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    isAllow = Settings.System.canWrite(mContext);
                } else {
                    isAllow = true;
                }
                break;
            case PERMISSION_STARTUP:
                isAllow = VivoPermissionUtils.getvivobgStartUpAppsPermissionStatus(mContext) == 0;
                break;
            case PERMISSION_NOTIFY:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("vivo X7Plus")) {
                    isAllow = VivoPermissionUtils.isNotificationListenerEnabled(mContext);
                } else {
                    isAllow = true;
                }
                break;
            case PERMISSION_ACTIVITY:
                isAllow = VivoPermissionUtils.getvivoBgStartActivityPermissionStatus(mContext) == 0;
                break;
            default:
                ;
        }
        if (isAllow) {
            setPause(!isAllow);
        }
    }

    @SuppressLint("NewApi")
    private void systemAction(ASStepBean stepBean) {
        AccessibilityNodeInfo rootWindow = AccessibilityServiceMonitor.instance.getRootInActiveWindow();
        if (rootWindow != null) {
            CharSequence packageName = rootWindow.getPackageName();
            if (TextUtils.equals(packageName, AccessibilityServiceMonitor.instance.getPackageName())) {
                mHandler.removeMessages(WHAT_FIND);
                mHandler.removeMessages(WHAT_COMPLETE);
                return;
            }
        }
        mHandler.removeMessages(WHAT_FIND);
        mHandler.sendEmptyMessageDelayed(WHAT_FIND, stepBean.delay_time);
//        AccessibilityServiceMonitor.instance.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        stepIndex++;
    }

    /**
     * 遍历查找节点
     */
    private void findClickView(AccessibilityNodeInfo rootNode, ASStepBean stepBean) {
        String findText = stepBean.getFind_text();
        COUNT++;
        if (rootNode != null) {
            if (stepBean.findMatchText) {
                String[] findMatchTexts = stepBean.getFindMatchTexts();
                if (findMatchTexts != null) {
                    for (String findMatchText : findMatchTexts) {
                        if (!isFind) {
                            List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText(findMatchText);
                            if (nodes.size() > 0) {
                                clickNodeIsNoChecked(rootNode, stepBean, nodes.get(0));
                            }
                        }
                    }
                    if (stepBean.banScrollable) {
                        markFind(stepBean.delay_time);
                    }
                }
            } else {
                //找到一级节点
                for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {
                    AccessibilityNodeInfo node = rootNode.getChild(i);
                    if (isFind) {
                        return;
                    }
                    //如果node为空则跳过该节点
                    if (node == null) {
                        continue;
                    }
                    CharSequence text = node.getText();
                    if (text != null && text.toString().contains(findText)) {
                        AccessibilityNodeInfo parent = node.getParent();
                        if (stepBean.getClick_type().equals("parent")) {
                            while (parent != null) {
                                if (parent.isClickable() || (node.isCheckable() && !node.isChecked())) {
                                    //模拟点击
                                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    markFind(stepBean.delay_time);
                                    return;
                                }
                                parent = parent.getParent();
                            }
                        } else if (stepBean.getClick_type().equals("child")) {
                            getAllChild(parent, stepBean);
                        } else if (stepBean.getClick_type().equals("self")) {
                            if (node.isClickable() || (node.isCheckable() && !node.isChecked())) {
                                //模拟点击
                                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                markFind(stepBean.delay_time);
                                return;
                            }
                        }
                    }
                    //递归需要有出口，如果找500次没找到立即停止,可能由于适配问题权限压根没在这个界面
                    if (COUNT >= TOTAL_COUNT) {
                        isFind = true;
                    }
                    if (isFind) {
                        mHandler.removeMessages(WHAT_FIND);
                        mHandler.sendEmptyMessageDelayed(WHAT_FIND, stepBean.delay_time);
                        return;
                    } else {
                        findClickView(node, stepBean);
                    }
                }
            }


        }
    }

    private void markFind(int delay_time) {
        isFind = true;
        mHandler.removeMessages(WHAT_FIND);
        mHandler.sendEmptyMessageDelayed(WHAT_FIND, delay_time);
    }


    private void getAllChild(AccessibilityNodeInfo parent, ASStepBean stepBean) {
        if (stepBean.isFindId()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = parent.findAccessibilityNodeInfosByViewId(stepBean.reality_node_id);
                if (accessibilityNodeInfosByViewId.size() > 0) {
                    AccessibilityNodeInfo child = accessibilityNodeInfosByViewId.get(0);
                    if (isDebug) {
                        Log.e(TAG, "============= findSwitchView 3 " + child.getClassName() + "==" + child.getViewIdResourceName() + "==" + child.isCheckable() + "==" + child.isClickable());
                    }
                    clickNodeIsNoChecked(parent, stepBean, child);
                }
            }
        } else {
            for (int j = 0; j < parent.getChildCount(); j++) {
                AccessibilityNodeInfo child = parent.getChild(j);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if (isDebug) {
                        if (child != null && TextUtils.isEmpty(child.getClassName()) && TextUtils.isEmpty(child.getViewIdResourceName())) {
                            Log.e(TAG, "============= findSwitchView 4 " + child.getClassName() + "==" + child.getViewIdResourceName() + "==" + child.isCheckable() + "==" + child.isClickable());
                        }
                    }
                    if (child != null && !TextUtils.isEmpty(child.getViewIdResourceName())) {
                        if (child.getViewIdResourceName().contains(stepBean.getReality_node_id())) {
                            clickNodeIsNoChecked(parent, stepBean, child);
                        }
                    }

                }
            }
        }
    }

    /**
     * 点击一个未选中的节点
     *
     * @param parent
     * @param stepBean
     * @param child
     */
    private void clickNodeIsNoChecked(AccessibilityNodeInfo parent, ASStepBean stepBean, AccessibilityNodeInfo child) {
        if (stepBean.getAlterWindowGuide() != 0) {
            isPause = true;
            if (mASMListener != null) {
                Rect outBounds = new Rect();
                child.getBoundsInScreen(outBounds);
                stepBean.outBounds = outBounds;
                mASMListener.pause(stepBean);
            }
        } else {
            boolean checked = child.isChecked();
            if (!checked) {
                boolean flag = child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                if (!child.isChecked() && !TextUtils.isEmpty(stepBean.clickFailToast)) {
                    if (mASMListener != null) {
                        mASMListener.pause(stepBean);
                    }
                }
                if (!flag) {
                    child.performAction(AccessibilityNodeInfo.ACTION_SELECT);
                    //补偿点击
                    if (!child.isChecked()) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            } else {
                //兼容小米,移除下一步
                if (stepBean.isCheckedRemoveNext()) {
                    stepIndex++;
                }
            }

        }
        markFind(stepBean.delay_time);
    }

    /**
     * 遍历查找节点
     */
    private void findScrollableView(AccessibilityNodeInfo rootNode, ASStepBean stepBean) {
        if (stepBean.isBanScrollable()) {
            return;
        }
        if (rootNode != null) {
            //如果指定了滚动控件则滚动该控件
            if (!TextUtils.isEmpty(stepBean.getReality_scrollable_node_id())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = rootNode.findAccessibilityNodeInfosByViewId(stepBean.reality_scrollable_node_id);
                    if (accessibilityNodeInfosByViewId.size() > 0) {
                        AccessibilityNodeInfo child = accessibilityNodeInfosByViewId.get(0);
                        if (child != null && child.isScrollable()) {
                            //模拟点击
                            child.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                            isScrollableComplete = true;
                        }
                    }
                }
            } else {
                //从最后一行开始找起
                for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {
                    AccessibilityNodeInfo node = rootNode.getChild(i);
                    //如果node为空则跳过该节点
                    if (node == null) {
                        continue;
                    }
                    if (isDebug) {
                        Log.d(TAG, "findScrollableView: " + node.getText());
                    }
                    if (node.isScrollable()) {
                        //模拟点击
                        node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        isScrollableComplete = true;
                    } else {
                        AccessibilityNodeInfo parent = node.getParent();
                        while (parent != null) {
                            if (parent.isScrollable()) {
                                //模拟点击
                                parent.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                                isScrollableComplete = true;
                                break;
                            }
                            parent = parent.getParent();
                        }
                    }
                    if (isScrollableComplete) {
                        break;
                    } else {
                        findScrollableView(node, stepBean);
                    }
                }
            }

        }
    }
}
