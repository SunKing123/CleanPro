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

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ui.main.fragment.dialog
 * @ClassName: OpenJurisdictionDialogFragment
 * @Description: 开启权限
 * @Author: LiDing
 * @CreateDate: 2020/5/9 11:46
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/9 11:46
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class PermissionGuideDialogFragment extends DialogFragment {

    private Context mContext;
    private OnClickListener mOnClickListener;
    private TextView TitleTv;
    private String riskTipsNum;
    private static final String RISK_TIPS_NUM = "riskTipsNum";

    public static PermissionGuideDialogFragment newInstance(String riskTipsNum) {
        PermissionGuideDialogFragment permissionGuideDialogFragment = new PermissionGuideDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RISK_TIPS_NUM, riskTipsNum);
        permissionGuideDialogFragment.setArguments(bundle);
        return permissionGuideDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.dialog_open_jurisdiction, container, true);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        resizeDialogFragment();
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

            }
        }
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button confirmBtn = view.findViewById(R.id.btn_confirm);
        confirmBtn.setOnClickListener(onClickListener);
        TitleTv = view.findViewById(R.id.txt_title);
        this.riskTipsNum = getArguments().getString(RISK_TIPS_NUM);
        if (null != this.riskTipsNum) {
            String html = "已发现手机<font color='#06C581'> " + riskTipsNum + " </font> 项风险，请立即修复！";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                TitleTv.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
            } else {
                TitleTv.setText(Html.fromHtml(html));
            }
            TitleTv.setMovementMethod(LinkMovementMethod.getInstance());
        }
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
