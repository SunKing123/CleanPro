package com.xiaoniu.cleanking.ui.lockscreen;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;

import androidx.appcompat.app.AlertDialog;

/**
 * 锁屏退出dialog<p>
 *
 */
public class LockExitDialog extends AlertDialog implements View.OnClickListener {
    private TextView exit;
    private TextView cancel;
    private Activity activity;

    public LockExitDialog(Activity context) {
        super(context, R.style.Dialog);
        this.activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        window.setGravity(Gravity.CENTER);
        setContentView(R.layout.dialog_lock_exit);
        initView();
    }

    private void initView() {
        exit = findViewById(R.id.lock_exit);
        cancel = findViewById(R.id.lock_cancel);

        exit.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lock_exit:
                //关闭锁屏
              /*  PreferenceUtil.getInstants().saveInt("openLock",0);
                LockerService.stopLockScreenFlowAd(getContext().getApplicationContext());
                dismiss();*/
                if (activity != null) {
                    activity.finish();
                }
                break;
            case R.id.lock_cancel:
                dismiss();
                break;
        }
    }
}
