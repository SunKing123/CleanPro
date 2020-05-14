package com.xiaoniu.cleanking.ui.main.fragment.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ui.main.fragment.dialog
 * @ClassName: MessageDialogFragment
 * @Description: 消息类型弹窗
 * @Author: LiDing
 * @CreateDate: 2020/5/9 11:46
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/9 11:46
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MessageDialogFragment extends DialogFragment {

    private Context mContext;
    private OnClickListener mOnClickListener;

    public static MessageDialogFragment newInstance() {
        MessageDialogFragment messageDialogFragment = new MessageDialogFragment();
        return messageDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.dialog_message, container, true);
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
                txtTitle.setText(content);
            }
        }
        Button confirmBtn = view.findViewById(R.id.btn_confirm);
        confirmBtn.setOnClickListener(onClickListener);
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


    @Override
    public void onStart() {
        super.onStart();
        resizeDialogFragment();
    }


    private void resizeDialogFragment() {
        Dialog dialog = getDialog();
        if (null != dialog) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            if (window != null) {
                window.setLayout(lp.width, lp.height);
            }
        }
    }

}
