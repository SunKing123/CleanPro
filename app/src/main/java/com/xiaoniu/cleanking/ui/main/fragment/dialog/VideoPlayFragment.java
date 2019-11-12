package com.xiaoniu.cleanking.ui.main.fragment.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xiaoniu.cleanking.R;

/**
 * 音频播放dialog
 * Created by lang.chen on 2019/7/2
 */
public class VideoPlayFragment extends DialogFragment {


    private Context mContext;

    private DialogClickListener dialogClickListener;


    /**
     * @param title    标题
     * @param size     文件大小
     * @param duration 时长
     * @param from     来源
     * @return
     */
    public static VideoPlayFragment newInstance(String title, String size, String duration, String from) {
        VideoPlayFragment delDialogFragment = new VideoPlayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("size", size);
        bundle.putString("duration", duration);
        bundle.putString("from", from);
        delDialogFragment.setArguments(bundle);
        return delDialogFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().getWindow().setLayout(getScreenWidth(mContext) - dip2px(mContext, 43), ViewGroup.LayoutParams.WRAP_CONTENT);


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.common_dialog_style);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.dialog_video_play, container, true);
        initView(view);
        return view;
    }


    private void initView(View view) {
        TextView txtTitle = view.findViewById(R.id.txt_title);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnDel = view.findViewById(R.id.btn_del);
        TextView txtSize = view.findViewById(R.id.txt_size);
        TextView txtDuration = view.findViewById(R.id.txt_duration);
        TextView txtFrom = view.findViewById(R.id.txt_from);

        if (null != getArguments()) {
            String title = getArguments().getString("title", "");
            String size = getArguments().getString("size", "");
            String duration = getArguments().getString("duration", "");
            String from = getArguments().getString("from", "");
            if (!TextUtils.isEmpty(title)) {
                txtTitle.setText(title);
            }
            txtSize.setText(String.format("大小: %s", size));
            txtDuration.setText(duration);
            txtFrom.setText(String.format("来自: %s", from));
        }

        btnCancel.setOnClickListener(v -> dismissAllowingStateLoss());
        btnDel.setOnClickListener(v -> {
            dismissAllowingStateLoss();
            if (null != dialogClickListener) {
                dialogClickListener.onConfirm();
            }
        });
    }


    public void setDialogClickListener(DialogClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;
    }

    /**
     * dialog listener
     */
    public interface DialogClickListener {

        //取消
        void onCancel();

        //确认
        void onConfirm();
    }

}
