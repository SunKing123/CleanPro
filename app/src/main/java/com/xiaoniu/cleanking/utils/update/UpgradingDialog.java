package com.xiaoniu.cleanking.utils.update;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;

/**
 * 代码描述<p>
 *
 * @author anhuiqing
 * @since 2019/9/26 13:31
 */
class UpgradingDialog extends AlertDialog {
    private final String TAG = UpgradingDialog.class.getSimpleName();
    private ProgressBar pgBar;
    private TextView tvPg;

    public UpgradingDialog(Context context) {
        super(context, R.style.dialog_2_button);
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
        setContentView(R.layout.custom_download_dialog);
        initView();
    }


    private void initView() {
        pgBar = findViewById(R.id.jjdxm_update_progress_bar);
        tvPg = findViewById(R.id.jjdxm_update_progress_text);
    }

    public ProgressBar getPgBar(){
        return pgBar;
    }

    public TextView getTvPg(){
        return tvPg;
    }
}

