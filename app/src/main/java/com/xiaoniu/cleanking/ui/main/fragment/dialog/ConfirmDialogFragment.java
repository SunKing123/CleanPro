package com.xiaoniu.cleanking.ui.main.fragment.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.activity.WebActivity;
import com.xiaoniu.common.utils.StatisticsUtils;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ui.main.fragment.dialog
 * @ClassName: ConfirmDialogFragment
 * @Description: 确认类型弹窗
 * @Author: LiDing
 * @CreateDate: 2020/5/9 11:46
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/9 11:46
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ConfirmDialogFragment extends DialogFragment {

    private Context mContext;

    private OnClickListener mOnClickListener;

    private boolean isCancelable = true;  // XD added

    public static ConfirmDialogFragment newInstance() {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        return confirmDialogFragment;
    }


    @Override
    public void onStart() {
        super.onStart();
        resizeDialogFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        StatisticsUtils.onPageStart("reminder_view_page", "温馨提示弹窗浏览");
    }

    @Override
    public void onPause() {
        super.onPause();
        StatisticsUtils.onPageEnd("reminder_view_page", "温馨提示弹窗浏览", "launch_page", "launch_page");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.dialog_confirm, container, true);
        initView(view);
        return view;
    }

    private void initView(View view) {
        if (null != getArguments()) {
            String title = getArguments().getString("title", "");
            String content = getArguments().getString("content", "");
            if (!TextUtils.isEmpty(title)) {
                TextView txtTitle = view.findViewById(R.id.txt_title);
                txtTitle.setText(title);
            }
            if (!TextUtils.isEmpty(content)) {
                TextView txtContent = view.findViewById(R.id.txt_content);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // flags
                    // FROM_HTML_MODE_COMPACT：html块元素之间使用一个换行符分隔
                    // FROM_HTML_MODE_LEGACY：html块元素之间使用两个换行符分隔
                    txtContent.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    txtContent.setText(Html.fromHtml(content));
                }
                txtContent.setMovementMethod(LinkMovementMethod.getInstance());

                CharSequence text = txtContent.getText();

                if (text instanceof Spannable) {
                    int end = text.length();
                    Spannable sp = (Spannable) txtContent.getText();
                    URLSpan[] spans = sp.getSpans(0, end, URLSpan.class);
                    SpannableStringBuilder style = new SpannableStringBuilder(text);
                    style.clearSpans();// should clear old spans
                    for (URLSpan span : spans) {
                        JayceSpan mySpan = new JayceSpan(span.getURL());
                        style.setSpan(mySpan, sp.getSpanStart(span), sp.getSpanEnd(span), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    txtContent.setText(style);
                }
                txtContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                         *这里是对所有点击消息的监听
                         * 可以把链接的做标记剔除后监听非链接的点击
                         * 比如jaycetest区域的点击
                         */
                    }
                });

            }
        }
        Button confirmBtn = view.findViewById(R.id.btn_confirm);
        Button cancelBtn = view.findViewById(R.id.btn_cancel);
        confirmBtn.setOnClickListener(onClickListener);
        cancelBtn.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (null == mOnClickListener) {
                return;
            }
            switch (view.getId()) {
                case R.id.btn_confirm:
                    dismiss();
                    mOnClickListener.onConfirm();
                    StatisticsUtils.trackClick("reminder_agree_click", "温馨提示同意点击", "launch_page", "launch_page");
                    break;
                case R.id.btn_cancel:
                    dismiss();
                    mOnClickListener.onCancel();
                    StatisticsUtils.trackClick("reminder_no_agree_click", "温馨提示不同意点击", "launch_page", "launch_page");
                    break;
            }
        }
    };

    public OnClickListener getOnClickListener() {
        return mOnClickListener;
    }

    public void setOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }


    public interface OnClickListener {
        void onConfirm();

        void onCancel();
    }

    /**
     * @author xd.he
     */
    public void setCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }

    private void resizeDialogFragment() {
        Dialog dialog = getDialog();
        if (null != dialog) {
            Window window = dialog.getWindow();
            //     WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
//            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            if (window != null) {
//                window.setLayout(lp.width, lp.height);
//            }
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // xd added
            dialog.setCancelable(isCancelable);
            dialog.setCanceledOnTouchOutside(isCancelable);
        }
    }


    private class JayceSpan extends ClickableSpan {

        private String mSpan;

        JayceSpan(String span) {
            mSpan = span;
        }

        @Override
        public void onClick(View widget) {
            Log.e("jayce", "span:" + mSpan);
            /* 链接被点击
             * 这里可以做一些自己定义的操作
             */
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.setData(Uri.parse(mSpan));
            startActivity(intent);
        }
    }


}
