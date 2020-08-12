package com.xiaoniu.cleanking.ui.main.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.midas.AdRequestParams;

import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.midas.abs.SimpleViewCallBack;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.StatisticsUtils;

/**
 * 锁屏退出dialog<p>
 */
public class ExitRetainDialog extends AlertDialog implements View.OnClickListener {
    private FrameLayout container;
    private Activity activity;

    public ExitRetainDialog(Activity context) {
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
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_retain_layout);
        initView();
        //只要弹出这个窗口带表当日的弹框次数一定没有超过限制，因此只用更新当日次数即可，不用判断别的逻辑
        PreferenceUtil.updatePressBackExitAppCount(true);

        StatisticsUtils.customTrackEvent("retain_the_pop_up_window_custom", "挽留弹窗创建时", "", "retain_the_pop_up_window");
        StatisticsUtils.customTrackEvent("ad_request_sdk", "挽留弹窗上广告发起请求", "", "retain_the_pop_up_window");
    }

    private void initView() {
        AppCompatTextView exit = findViewById(R.id.btn_exit);
        AppCompatTextView cancel = findViewById(R.id.btn_cancel);
        //此container即为显示广告的容器
        container = findViewById(R.id.container);
        if (exit != null) {
            exit.setOnClickListener(this);
        }
        if (cancel != null) {
            cancel.setOnClickListener(this);
        }
        initAdv();
    }


    public void initAdv() {
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", "", "", "all_ad_request", "retain_the_pop_up_window", "retain_the_pop_up_window");
        MidasRequesCenter.requestAndShowAd(activity, AppHolder.getInstance().getMidasAdId(PositionId.KEY_PAGE_EXIT_RETAIN,PositionId.DRAW_ONE_CODE),new SimpleViewCallBack(container));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exit:
                StatisticsUtils.trackClick("confirm_exit_button_click", "确认退出按钮点击", "", "retain_the_pop_up_window");
                //更新按返回键退出程序的次数
                dismiss();
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                activity.startActivity(home);
                break;
            case R.id.btn_cancel:
                StatisticsUtils.trackClick("continue_to_browse_button_click", "继续浏览按钮点击", "", "retain_the_pop_up_window");
                dismiss();
                break;
        }
    }
}
