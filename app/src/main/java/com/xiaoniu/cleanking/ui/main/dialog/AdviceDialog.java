package com.xiaoniu.cleanking.ui.main.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;

import com.xiaoniu.cleanking.R;

/**
 * 广告Dialog<p>
 */
public class AdviceDialog extends AlertDialog implements View.OnClickListener {
    private FrameLayout container;
    private Activity activity;

    public AdviceDialog(Activity context) {
        super(context, R.style.Dialog);
        this.activity = context;
    }

    public AdviceDialog(Activity context, int type) {
        super(context, R.style.Dialog);
        this.activity = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
            window.setGravity(Gravity.CENTER);
        }
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_advice_layout);
        initView();

    }

    private void initView() {
    }


    public void initAdv() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
