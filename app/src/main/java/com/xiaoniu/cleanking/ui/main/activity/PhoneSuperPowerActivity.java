package com.xiaoniu.cleanking.ui.main.activity;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.ui.main.event.NotificationEvent;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

/**
 * 超强省电主页面
 */
public class PhoneSuperPowerActivity extends SimpleActivity {

    private TextView mTvClean;
    private boolean isClick = false;
    private boolean isDoubleBack = false;
    private AlertDialog mAlertDialog = null;

    String sourcePage = "";
    String currentPage = "";
    String sysReturnEventName = "";

    String viewPageEventCode ="";
    String viewPageEventName ="";


    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_super_power;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        addNotification(intent);
    }

    private void addNotification(Intent intent) {
        if (intent != null) {
            String notification = intent.getStringExtra("NotificationService");
            if ("clean".equals(notification)) {
                AppHolder.getInstance().setCleanFinishSourcePageId("toggle_powersave_click");
                sourcePage = "";
                StatisticsUtils.trackClick("toggle_powersave_click", "常驻通知栏点击省电", "", "toggle_page");
            }
        }
    }

    @Override
    protected void initView() {
        currentPage = "powersave_guidance_page";
        sysReturnEventName ="用户在省电引导页页浏览返回";
        String preName = AppManager.getAppManager().preActivityName();
        sourcePage = preName.contains("MainActivity") ? "home_page" : "";
        viewPageEventCode ="powersave_guidance_page_view_page";
        viewPageEventName = "用户在省电引导页浏览";

        Intent intent = getIntent();
        addNotification(intent);
        mTvClean = findViewById(R.id.tv_clean);
        mTvClean.setOnClickListener(v -> {
            StatisticsUtils.trackClick("powersave_guidance_page_clean_click", "用户在省电引导页点击【立即清理】",sourcePage, currentPage);
            if (!isUsageAccessAllowed()) {
                showPermissionDialog();
            } else {
                startActivity(PhoneSuperPowerDetailActivity.class);
                finish();
            }
        });
    }

    public void showPermissionDialog() {
        currentPage = "powersave_authorization_page";
        sourcePage = "powersave_guidance_page";
        sysReturnEventName ="用户在省电授权页返回";
        viewPageEventCode ="powersave_authorization_page_view_page";
        viewPageEventName = "用户在省电授权页浏览";
        NiuDataAPI.onPageStart(viewPageEventCode, viewPageEventName);
        NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, viewPageEventCode, viewPageEventName);

        if(null == mAlertDialog ){
            mAlertDialog = new AlertDialog.Builder(this).create();
        }

        if (isFinishing()) {
            return;
        }
        mAlertDialog.setCancelable(true);
        mAlertDialog.setCanceledOnTouchOutside(false);
        if(!mAlertDialog.isShowing()){
            mAlertDialog.show();
        }
        Window window = mAlertDialog.getWindow();
        window.setContentView(R.layout.alite_power_saving_permission_dialog);
        WindowManager.LayoutParams lp = mAlertDialog.getWindow().getAttributes();
        //这里设置居中
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.findViewById(R.id.iv_exit).setOnClickListener(v -> finish());
        TextView tv_goto = window.findViewById(R.id.tv_goto);
        tv_goto.setOnClickListener(v -> {
            isClick = true;
            try {
                StatisticsUtils.trackClick("set_up_click", "用户在省电授权页点击【前往设置】按钮", sourcePage, currentPage);
                //solve umeng error ->No Activity found to handle Intent { act=android.settings.USAGE_ACCESS_SETTINGS }
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            } catch (Exception e) {
            }
            startActivity(PhonePremisActivity.class);
        });

        mAlertDialog.setOnDismissListener(dialog -> {
            StatisticsUtils.trackClick("close_click", "用户在省电授权页点击【关闭】按钮", sourcePage, currentPage);
            finish();
        });


    }

    public boolean isUsageAccessAllowed() {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                AppOpsManager manager = ((AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE));
                if (manager == null) return false;
                int mode = manager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), this.getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED;
            } catch (Throwable ignored) {
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationEvent event = new NotificationEvent();
        event.setType("power");
        EventBus.getDefault().post(event);

        if (isClick) {
            if (isUsageAccessAllowed()) {
                if (mAlertDialog != null)
                    mAlertDialog.cancel();
                startActivity(PhoneSuperPowerDetailActivity.class);
                finish();
            } else {
                ToastUtils.showShort(getString(R.string.tool_get_premis));
                if (isDoubleBack) finish();
                isDoubleBack = true;

                currentPage = "powersave_fail_page";
                sourcePage = "powersave_authorization_page";
                viewPageEventCode ="powersave_fail_page_view_page";
                viewPageEventName = "省电授权失败页浏览";
                NiuDataAPI.onPageStart(viewPageEventCode, viewPageEventName);
                NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, viewPageEventCode, viewPageEventName);

            }
        } else {
            if (isUsageAccessAllowed()) {
                if (mAlertDialog != null)
                    mAlertDialog.cancel();
                startActivity(PhoneSuperPowerDetailActivity.class);
                finish();
            }
        }
        isClick = false;
        NiuDataAPI.onPageStart(viewPageEventCode, viewPageEventName);

    }


    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, viewPageEventCode, viewPageEventName);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StatisticsUtils.trackClick("system_return_click", sysReturnEventName, sourcePage, currentPage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(null!=mAlertDialog){
                mAlertDialog.cancel();
                mAlertDialog.dismiss();
                mAlertDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
