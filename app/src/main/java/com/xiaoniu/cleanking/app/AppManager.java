package com.xiaoniu.cleanking.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by fengpeihao on 2017/3/16.
 */

public class AppManager {
    private static Stack<Activity> activityStack;
    private static List<String> activityName = new ArrayList<>();
    private volatile static AppManager instance;

    private AppManager() {

    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            synchronized (AppManager.class) {
                if (instance == null) {
                    instance = new AppManager();
                    activityStack = new Stack();
                    activityName = new ArrayList<>();
                }
            }

        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        Activity cAc = null;
        for (Activity activityItem : activityStack) {
            if (TextUtils.equals(activityItem.getClass().getName(), activity.getClass().getName())) {
                cAc = activityItem;
            }
        }
        if (cAc != null) activityStack.remove(activity);
        activityStack.add(activity);
    }

    public void addActivityName(Activity activity) {
        if (activityName == null) {
            activityName = new ArrayList<>();
        }
        List<String> listTemp=new ArrayList<>();
        if(activityName.contains(activity.getClass().getName())){
            listTemp.add(activity.getClass().getName());
        }
        activityName.removeAll(listTemp);
        activityName.add(activity.getClass().getName());
    }

    public String preActivityName() {
        int index = activityName.size() - 2;
        if (index < 0) {
            return "";
        }
        String activityNames = activityName.get(index);
        return activityNames;
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        try {
            Activity activity = activityStack.lastElement();
            return activity;
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前Activity的前一个Activity
     */
    public Activity preActivity() {
        int index = activityStack.size() - 2;
        if (index < 0) {
            return null;
        }
        Activity activity = activityStack.get(index);
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 移除指定的Activity
     */
    public void removeActivity(Activity activitys) {


        try {
            for (Activity activity : activityStack) {
                String name1 = activity.getClass().getName();
                String currentName = activitys.getClass().getName();
                if (name1.equals(currentName)) {
                    if (activitys != null) {
                        activityStack.remove(activitys);
                        activitys = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        try {
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    finishActivity(activity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 返回到指定的activity
     *
     * @param cls
     */
    public void returnToActivity(Class<?> cls) {
        while (activityStack.size() != 0)
            if (activityStack.peek().getClass() == cls) {
                break;
            } else {
                finishActivity(activityStack.peek());
            }
    }


    /**
     * 是否已经打开指定的activity
     *
     * @param cls
     * @return
     */
    public boolean isOpenActivity(Class<?> cls) {
        if (activityStack != null) {
            for (int i = 0, size = activityStack.size(); i < size; i++) {
                if (cls == activityStack.peek().getClass()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 退出应用程序
     *
     * @param context      上下文
     * @param isBackground 是否开开启后台运行
     */
    public void AppExit(Context context, Boolean isBackground) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
        } catch (Exception e) {

        } finally {
            // 注意，如果您有后台程序运行，请不要支持此句子
            if (!isBackground) {
                Process.killProcess(Process.myPid());
            }
        }
    }

    public void clearStack() {
        if (activityStack != null) {
            activityStack.clear();
            activityName.clear();
        }

    }
}
