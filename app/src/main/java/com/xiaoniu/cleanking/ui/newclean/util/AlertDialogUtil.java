package com.xiaoniu.cleanking.ui.newclean.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.newclean.interfice.ClickListener;

public class AlertDialogUtil {

    /**
     * 1.2.1版本更新 扫描终止提示框
     * @param context
     * @param tipTxtStr
     * @param contentStr
     * @param okListener
     * @return
     */
    public static AlertDialog alertBanLiveDialog(Context context, String tipTxtStr, String contentStr,String btnOkStr,String btnCancelStr, ClickListener okListener) {

        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        if (((Activity) context).isFinishing()) {
            return dlg;
        }
        dlg.show();
        dlg.setCanceledOnTouchOutside(false);
        Window window = dlg.getWindow();
        window.setContentView(R.layout.alite_redp_send_dialog);
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        //这里设置居中
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView btnOk = window.findViewById(R.id.btnOk);

        TextView btnCancel = window.findViewById(R.id.btnCancle);
        TextView tipTxt = window.findViewById(R.id.tipTxt);
        TextView content = window.findViewById(R.id.content);
        tipTxt.setText(tipTxtStr);
        content.setText(contentStr);
        btnOk.setText(btnOkStr);
        btnCancel.setText(btnCancelStr);
        btnOk.setOnClickListener(v -> {
            dlg.dismiss();
            okListener.clickOKBtn();
        });
        btnCancel.setOnClickListener(v -> {
            dlg.dismiss();
            okListener.cancelBtn();
        });
        return dlg;
    }

}
