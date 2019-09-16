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
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.common.utils.ToastUtils;

public class PhoneSuperPowerActivity extends SimpleActivity {

    private TextView mTvClean;
    private boolean isClick = false;
    private boolean isDoubleBack = false;
    private AlertDialog mAlertDialog = null;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_super_power;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isUsageAccessAllowed()) {
            startActivity(PhoneSuperPowerDetailActivity.class);
            finish();
        }
    }

    @Override
    protected void initView() {
        mTvClean = findViewById(R.id.tv_clean);
        mTvClean.setOnClickListener(v -> {
            if (!isUsageAccessAllowed()){
                showPermissionDialog();
            } else {
                startActivity(PhoneSuperPowerDetailActivity.class);
                finish();
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
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);

            startActivity(PhonePremisActivity.class);
        });

        mAlertDialog.setOnDismissListener(dialog -> {
            finish();
        });
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
    protected void onResume() {
        super.onResume();
        if (isClick) {
            if (isUsageAccessAllowed()) {
                if (mAlertDialog != null)
                    mAlertDialog.cancel();
                startActivity(PhoneSuperPowerDetailActivity.class);
            }else {
                ToastUtils.showShort(getString(R.string.tool_get_premis));
                if (isDoubleBack) finish();
                isDoubleBack = true;
            }
        }
        isClick = false;
    }
}
