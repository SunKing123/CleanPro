package com.xiaoniu.cleanking.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Button;

import com.lxj.xpopup.core.CenterPopupView;
import com.xiaoniu.cleanking.R;

public class UserRefuseDialog extends CenterPopupView {

    private OnUserRefuseClickListener mOnUserRefuseClickListener;

    public UserRefuseDialog(@NonNull Context context, OnUserRefuseClickListener onUserRefuseClickListener) {
        super(context);
        this.mOnUserRefuseClickListener = onUserRefuseClickListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_message;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();

        Button btn_confirm = findViewById(R.id.btn_confirm);

        btn_confirm.setOnClickListener(v -> {
            dismiss();
            if (mOnUserRefuseClickListener != null) {
                mOnUserRefuseClickListener.onUserRefuse();
            }
        });
    }

    public interface OnUserRefuseClickListener {

        void onUserRefuse();
    }
}
