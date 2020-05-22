package com.xiaoniu.cleanking.ui.main.fragment.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.xiaoniu.common.utils.StatisticsUtils;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ui.main.fragment.dialog
 * @ClassName: FilePermissionGuideDialogFragment
 * @Description: 读写文件获取弹窗
 * @Author: 作者名
 * @CreateDate: 2020/5/12 20:11
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/12 20:11
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class FilePermissionGuideDialogFragment extends DialogFragment {

    private Context mContext;
    private OnClickListener mOnClickListener;

    public static FilePermissionGuideDialogFragment newInstance() {
        FilePermissionGuideDialogFragment filePermissionGuideDialogFragment = new FilePermissionGuideDialogFragment();
        return filePermissionGuideDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.dialog_file_permission_guide, container, true);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        resizeDialogFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        StatisticsUtils.onPageStart("read_file_permission_popup_view_page", "读取文件权限弹窗浏览");
    }

    @Override
    public void onPause() {
        super.onPause();
        StatisticsUtils.onPageEnd("read_file_permission_popup_view_page", "读取文件权限弹窗浏览", "launch_page", "home_page");
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
                    StatisticsUtils.trackClick("read_file_permission_popup_click", "读取文件权限弹窗点击", "launch_page", "home_page");
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
//            WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
//            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            if (window != null) {
//                window.setLayout(lp.width, lp.height);
//            }
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
        }
    }

}
