package com.hellogeek.permission.util;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hellogeek.permission.R;
import com.hellogeek.permission.widget.StateButton;

public class DialogUtil {

    public static void showChangeCallToolsDialog(Activity activity,String permission,
                                                 CallToolsDialogDismissListener listener) {
        Dialog dialog = new Dialog(activity, R.style.per_base_dialog_style);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_call_default_per, null);
        StateButton leftBtn = view.findViewById(R.id.per_left_btn);
        StateButton rightBtn = view.findViewById(R.id.per_right_btnOpen);
        TextView tipsTv = view.findViewById(R.id.per_change_call_tools_tv);
        tipsTv.setText("是否已开启"+permission+"权限?");
        rightBtn.setOnClickListener(new OnClickThrottleListener() {
            @Override
            protected void onThrottleClick(View v) {
                dialog.dismiss();
                listener.open();
                 }
        });
        leftBtn.setOnClickListener(new OnClickThrottleListener() {
            @Override
            protected void onThrottleClick(View v) {
                dialog.dismiss();
                listener.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        }


    public static void showToastToolsDialog(Activity activity,
                                                 CallToolsDialogDismissListener listener) {
        Dialog dialog = new Dialog(activity, R.style.per_base_dialog_style);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_toast_per, null);
        StateButton rightBtn = view.findViewById(R.id.per_right_btnOpen);
        TextView tipsTv = view.findViewById(R.id.per_change_call_tools_tv);
        tipsTv.setText("开启【悬浮窗】权限后才能进行一键修复喔!");
        rightBtn.setOnClickListener(new OnClickThrottleListener() {
            @Override
            protected void onThrottleClick(View v) {
                dialog.dismiss();
                listener.open();
            }
        });
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public interface CallToolsDialogDismissListener {
        void open();
        void dismiss();
    }

}
