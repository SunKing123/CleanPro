package com.xiaoniu.cleanking.ui.main.fragment.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
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
        StatisticsUtils.onPageStart("reminder_view_page","温馨提示弹窗浏览");
    }

    @Override
    public void onPause() {
        super.onPause();
        StatisticsUtils.onPageEnd("reminder_view_page","温馨提示弹窗浏览","launch_page","launch_page");
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
                TextView txtTitle = view.findViewById(R.id.txt_content);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // flags
                    // FROM_HTML_MODE_COMPACT：html块元素之间使用一个换行符分隔
                    // FROM_HTML_MODE_LEGACY：html块元素之间使用两个换行符分隔
                    txtTitle.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    txtTitle.setText(Html.fromHtml(content));
                }
                txtTitle.setMovementMethod(LinkMovementMethod.getInstance());
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
                    mOnClickListener.onConfirm();
                    break;
                case R.id.btn_cancel:
                    mOnClickListener.onCancel();
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


    private void resizeDialogFragment() {
        Dialog dialog = getDialog();
        if (null != dialog) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
//            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            if (window != null) {
//                window.setLayout(lp.width, lp.height);
//            }
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

}
