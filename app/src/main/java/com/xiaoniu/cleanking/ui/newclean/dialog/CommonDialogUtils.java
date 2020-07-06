package com.xiaoniu.cleanking.ui.newclean.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.BaseDialog;
import com.xiaoniu.cleanking.ui.newclean.interfice.OnBtnClickListener;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/5
 * Describe: 公共的弹窗效果===几种常用的样式，可以根据xml查看适合自己的样式直接使用
 */
public class CommonDialogUtils {
    /**
     * 公共提示dialog样式一
     *
     * @param context
     * @param con
     * @param okBtn
     * @param clickListener
     * @return
     */
    public static Dialog showRemindDialogStyle01(Context context, String con, String
            okBtn, OnBtnClickListener clickListener) {
        BaseDialog dlg = new BaseDialog(context, R.style.common_dialog_style);
        dlg.setContentView(R.layout.dialog_common_style_01);
        dlg.setGravityLayout(BaseDialog.CENTER);
        dlg.setWidthDialog(BaseDialog.SCREENWIDTH);
        dlg.setHeightDialog(BaseDialog.SCREENHEIGHT);
        dlg.setCancelable(true);
        dlg.setCanceledOnTouchOutside(true);
        dlg.initOnCreate();
        dlg.show();

        TextView content = dlg.findViewById(R.id.content);
        TextView coinCancle = dlg.findViewById(R.id.btnCancle);
        TextView btnOk = dlg.findViewById(R.id.btnOk);
        content.setText(con);
        btnOk.setText(okBtn);
        coinCancle.setOnClickListener(v -> {
            dlg.dismiss();
        });
        btnOk.setOnClickListener(v -> {
            dlg.dismiss();
            clickListener.onClickView(-1);
        });
        return dlg;
    }
}
