package com.xiaoniu.cleanking.ui.main.receiver;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;

/**
 * 提示对话框(可以自定义布局)
 * 1，两个选项，顶部是问号图标
 * 2，单个选项
 */
public class AlertDialog extends Dialog {

    public AlertDialog(Context context) {
        super(context);
    }

    public AlertDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * 建造者类
     *
     * @author Administrator
     */
    public static class Builder {
        private Context context;
        private String appName;
        private View contentView;

        private View.OnClickListener onClickListener;
        private String garbageNum;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 设置消息内容
         *
         * @param appName
         * @return
         */
        public Builder setAppName(String appName) {
            this.appName = appName;
            return this;
        }

        /**
         * 设置消息内容
         *
         * @param
         * @return
         */
        public Builder setGarbageNum(String garbageNum) {
            this.garbageNum = garbageNum;
            return this;
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }


        /**
         * 创建一个AlertDialog
         *
         * @return
         */
        public AlertDialog create() {

            LayoutInflater inflater = LayoutInflater.from(context);

            final AlertDialog dialog = new AlertDialog(context, R.style.dialog);

            View layout = null;
            if (null != contentView) {
                layout = contentView;
            } else {
                layout = inflater.inflate(R.layout.dialog_alert_layout, null);
            }


            TextView garbageNumTv = layout.findViewById(R.id.tv_garbage_num);
            TextView appNameTv = layout.findViewById(R.id.tv_app_name);
            Button cleanNowBtn = layout.findViewById(R.id.btn_clean_now);
            ImageView closeIv = layout.findViewById(R.id.iv_close);
            appNameTv.setText(appName);
            garbageNumTv.setText(garbageNum);

            closeIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            cleanNowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 清理
                    if (null != onClickListener) {
                        onClickListener.onClick(view);
                    }
                }
            });

            Window win = dialog.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT; //设置宽度
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            win.setAttributes(lp);
            dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            // 设置对话框的视图
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            dialog.setContentView(layout, params);
            return dialog;
        }
    }
}
