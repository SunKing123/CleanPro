package com.xiaoniu.cleanking.ui.tool.notify.activity;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.activity.PhonePremisActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerDetailActivity;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.tool.notify.utils.NotifyUtils;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.ToastUtils;

/**
 * 通知栏清理
 */
public class NotifyCleanGuideActivity extends BaseActivity {
    private TextView mTvClean;
    private boolean mRequestPermission;
    private AlertDialog mAlertDialog;
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

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_notifaction_clean_guide;
    }

    @Override
    protected void initVariable(Intent intent) {
        setLeftTitle("通知栏清理");
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mTvClean = findViewById(R.id.tv_clean);
    }

    @Override
    protected void setListener() {
        mTvClean.setOnClickListener(v -> {
            if (NotifyUtils.isNotificationListenerEnabled()) {
                SPUtil.setCleanNotificationEnable(true);
                NotifyCleanDetailActivity.startNotificationCleanActivity(NotifyCleanGuideActivity.this);
                finish();
            } else {
                showPermissionDialog();
            }
        });
    }

    public void showPermissionDialog() {
        mAlertDialog = new AlertDialog.Builder(this).create();
        if (isFinishing()) {
            return;
        }
        mAlertDialog.setCancelable(true);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.show();
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
            mRequestPermission = true;
           if (NotifyUtils.openNotificationListenerSettings(NotifyCleanGuideActivity.this)) {
               startActivity(new Intent(NotifyCleanGuideActivity.this, PhonePremisActivity.class));
           }
        });

        mAlertDialog.setOnDismissListener(dialog -> {
            finish();
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isClick) {
            if (isUsageAccessAllowed()) {
                if (mAlertDialog != null)
                    mAlertDialog.cancel();
                SPUtil.setCleanNotificationEnable(true);
                NotifyCleanDetailActivity.startNotificationCleanActivity(this);
                finish();
            }else {
                ToastUtils.showShort(getString(R.string.tool_get_premis));
                if (isDoubleBack) finish();
                isDoubleBack = true;
            }
        }else {
            if (isUsageAccessAllowed()) {
                if (mAlertDialog != null)
                    mAlertDialog.cancel();
                SPUtil.setCleanNotificationEnable(true);
                NotifyCleanDetailActivity.startNotificationCleanActivity(this);
                finish();
            }
        }
        isClick = false;

        if (mRequestPermission) {
            mRequestPermission = false;
            if (NotifyUtils.isNotificationListenerEnabled()) {
                SPUtil.setCleanNotificationEnable(true);
                NotifyCleanDetailActivity.startNotificationCleanActivity(this);
                finish();
            }
        }
    }
    public  boolean isUsageAccessAllowed() {
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
    protected void loadData() {

    }

}
