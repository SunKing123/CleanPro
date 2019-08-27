package com.xiaoniu.cleanking.ui.main.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleActivity;

import java.util.List;

public class PhoneSuperPowerActivity extends SimpleActivity {

    private TextView mTvClean;
    private List<ActivityManager.RunningAppProcessInfo> listInfo;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_super_power;
    }

    @Override
    protected void initView() {
        mTvClean = findViewById(R.id.tv_clean);
        showPermissionDialog();
        mTvClean.setOnClickListener(v -> {
            startActivity(PhoneSuperPowerSavingActivity.class);
            finish();
        });
    }
    public AlertDialog showPermissionDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        if (isFinishing()) {
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
        tv_goto.setOnClickListener(v -> {
            dlg.dismiss();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        });
        dlg.setOnDismissListener(this::onDismiss);
        return dlg;
    }

    private void onDismiss(DialogInterface dialog) {
        finish();
    }
}
