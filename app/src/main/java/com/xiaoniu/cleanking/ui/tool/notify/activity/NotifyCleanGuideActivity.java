package com.xiaoniu.cleanking.ui.tool.notify.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.activity.PhonePremisActivity;
import com.xiaoniu.cleanking.ui.main.event.NotificationEvent;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.tool.notify.utils.NotifyUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

/**
 * 通知栏清理引导页面
 */
public class NotifyCleanGuideActivity extends BaseActivity {
    private TextView mTvClean;
    //    private boolean mRequestPermission;
    private AlertDialog mAlertDialog;//权限引导弹框
    private boolean isClick = false;
    private boolean isDoubleBack = false;

    public static void startNotificationGuideActivity(Context context) {
        if (context != null) {
            if (!NotifyUtils.isNotificationListenerEnabled() || !SPUtil.isCleanNotificationEnable()) {
                Intent intent = new Intent(context, NotifyCleanGuideActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                NotifyCleanDetailActivity.startNotificationCleanActivity(context);
            }
        }
    }

    String sourcePage = "";
    String currentPage = "";
    String pageviewEventCode = "";
    String pageviewEventName = "";
    String returnEventName = "";
    String sysReturnEventName = "";

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_notifaction_clean_guide;
    }

    @Override
    protected void initVariable(Intent intent) {
        setLeftTitle("通知栏清理");
        mToolBar.setBackgroundColor(getResources().getColor(R.color.color_06C581));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        currentPage = "notification_clean_guidance_page";
        pageviewEventName = "用户在通知引导页浏览";
        pageviewEventCode = "notification_clean_guidance_page_view_page";
        returnEventName = "用户在通知引导页浏览返回";
        sysReturnEventName = "用户在通知引导页浏览返回";
        sourcePage = AppManager.getAppManager().preActivityName().contains("MainActivity") ? "home_page" : "";

        Intent intent = getIntent();
        if (intent != null) {
            String notification = intent.getStringExtra("NotificationService");
            if ("clean".equals(notification)) {
                AppHolder.getInstance().setCleanFinishSourcePageId("toggle_noti_clean_click");
                StatisticsUtils.trackClick("toggle_noti_clean_click", "常驻通知栏点击通知清理", "", "toggle_page");
            }
        }

        mTvClean = findViewById(R.id.tv_clean);
    }

    @Override
    protected void setListener() {
        mTvClean.setOnClickListener(v -> {
            if (NotifyUtils.isNotificationListenerEnabled()) {
                startNodifyDetail();
            } else {
                NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, pageviewEventCode, pageviewEventName);
                currentPage = "notification_clean_authorization_page";
                pageviewEventName = "用户在通知授权页浏览";
                pageviewEventCode = "notification_clean_authorization_page_view_page";
                returnEventName = "用户在通知授权页返回";
                sysReturnEventName = "用户在通知授权页返回";

                NiuDataAPI.onPageStart(pageviewEventCode, pageviewEventName);
                NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, pageviewEventCode, pageviewEventName);


                mAlertDialog = showPermissionDialog(new ClickListener() {
                    @Override
                    public void clickOKBtn() {
                        StatisticsUtils.trackClick("return_back", returnEventName, sourcePage, currentPage);
                        isClick = true;
                        //开启权限
                        try {
                            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                            NotifyCleanGuideActivity.this.startActivity(intent);
                            NotifyCleanGuideActivity.this.startActivity(PhonePremisActivity.class);
                        } catch (ActivityNotFoundException exception) {
                            exception.printStackTrace();
                            //umeng --  android.content.ActivityNotFoundException: No Activity found to handle Intent { }
//                            Intent intent = new Intent();
//                            NotifyCleanGuideActivity.this.startActivity(intent);
                            NotifyCleanGuideActivity.this.startActivity(PhonePremisActivity.class);
                        }
                    }

                    @Override
                    public void cancelBtn() {

                    }
                });


            }
        });
    }

    //进入通知栏详情页
    public void startNodifyDetail() {
        SPUtil.setCleanNotificationEnable(true);
        NotifyCleanDetailActivity.startNotificationCleanActivity(NotifyCleanGuideActivity.this);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationEvent event = new NotificationEvent();
        event.setType("notification");
        EventBus.getDefault().post(event);

        if (isClick) {
            if (NotifyUtils.isNotificationListenerEnabled()) {
                if (mAlertDialog != null)
                    mAlertDialog.cancel();
                startNodifyDetail();
            } else {
                ToastUtils.showShort(getString(R.string.tool_get_premis));
                if (isDoubleBack) finish();

                isDoubleBack = true;
                sourcePage = "notification_clean_authorization_page";
                currentPage = "notification_clean_fail_page";
                pageviewEventName = "通知授权失败页浏览";
                pageviewEventCode = "notification_clean_fail_page_view_page";
                returnEventName = "用户在通知授权页返回";
                sysReturnEventName = "用户在通知授权页返回";

                NiuDataAPI.onPageStart(pageviewEventCode, pageviewEventName);
                NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, pageviewEventCode, pageviewEventName);
            }
        }
        isClick = false;
        NiuDataAPI.onPageStart(pageviewEventCode, pageviewEventName);

    }

    @Override
    public void onBackPressed() {
        StatisticsUtils.trackClick("system_return_click", returnEventName, sourcePage, currentPage);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, pageviewEventCode, pageviewEventName);
        super.onPause();
    }

    @Override
    protected void loadData() {

    }


    boolean isFromClick = false;

    public AlertDialog showPermissionDialog(final ClickListener okListener) {
        isFromClick = false;
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        if (this.isFinishing()) {
            return dlg;
        }
        dlg.setCancelable(true);
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.alite_permission_dialog);
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        //这里设置居中
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv_goto = window.findViewById(R.id.tv_goto);
        window.findViewById(R.id.iv_exit).setOnClickListener(v -> dlg.cancel());
        tv_goto.setOnClickListener(v -> {
            isFromClick = true;
            okListener.clickOKBtn();
        });
        dlg.setOnDismissListener(dialog -> {
            if (!isFromClick)
                NotifyCleanGuideActivity.this.finish();
        });
        return dlg;
    }

    public interface ClickListener {
        void clickOKBtn();

        void cancelBtn();
    }

}
