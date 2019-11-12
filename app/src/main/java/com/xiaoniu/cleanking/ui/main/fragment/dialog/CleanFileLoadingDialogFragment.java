package com.xiaoniu.cleanking.ui.main.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.xiaoniu.cleanking.R;

/**
 * Created by lang.chen on 2019/7/11
 */
public class CleanFileLoadingDialogFragment extends DialogFragment {


    private ProgressBar mProgressBar;
    private TextView mTxtContent;
    private ImageView mImgSuccessful;

    public static CleanFileLoadingDialogFragment newInstance() {

        CleanFileLoadingDialogFragment cleanFileLoadingDialogFragment = new CleanFileLoadingDialogFragment();

        return cleanFileLoadingDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_clean_file_loading, null);
        getDialog().setCanceledOnTouchOutside(false);
        initView(view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.common_dialog_style);
    }

    /**
     * 获取屏幕宽度
     */
    protected int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private void initView(View view) {
        mTxtContent = view.findViewById(R.id.txt_content);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mImgSuccessful = view.findViewById(R.id.img_upload_successful);
    }


    public void setReportSuccess(int status, String title) {
        if (status == 0) {
            if (null != mProgressBar) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            if (null != mImgSuccessful) {
                mImgSuccessful.setVisibility(View.GONE);
            }
            if (null != mTxtContent) {
                mTxtContent.setText("删除中...");
            }

        } else {
            if (null != mProgressBar) {
                mProgressBar.setVisibility(View.GONE);
            }
            if (null != mImgSuccessful) {
                mImgSuccessful.setVisibility(View.VISIBLE);
            }
            if (null != mTxtContent) {
                mTxtContent.setText(title);
            }

        }
    }
}
