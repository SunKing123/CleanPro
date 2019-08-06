package com.xiaoniu.cleanking.ui.main.fragment.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;

/**
 * 内存已满提示
 * Created by lang.chen on 2019/7/2
 */
public class MFullDialogStyleFragment extends DialogFragment {


    private Context mContext;

    private DialogClickListener dialogClickListener;


    /**
     * @param strs strs[0] 为标题
     */
    public static MFullDialogStyleFragment newInstance(String... strs) {
        MFullDialogStyleFragment delDialogFragment = new MFullDialogStyleFragment();
        Bundle bundle = new Bundle();
        if (strs.length ==1) {
            bundle.putString("title", strs[0]);
        }else if(strs.length==2){
            bundle.putString("title",strs[0]);
            bundle.putString("content",strs[1]);
        }
        delDialogFragment.setArguments(bundle);
        return delDialogFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().getWindow().setLayout(getScreenWidth(mContext) - dip2px(mContext, 43), ViewGroup.LayoutParams.WRAP_CONTENT);


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.common_dialog_style_c);
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
        View view = inflater.inflate(R.layout.dialog_memory_full, container, true);
        initView(view);
        return view;
    }


    private void initView(View view) {


        Button btnDel = view.findViewById(R.id.btn_del);

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
                if (null != dialogClickListener) {
                    dialogClickListener.onConfirm();
                }
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
