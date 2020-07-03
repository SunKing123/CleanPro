package com.xiaoniu.cleanking.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaoniu.cleanking.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by zhaoyingtao
 * Date: 2020/7/2
 * Describe:公共的title
 */
public class CommonTitleLayout extends FrameLayout {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.root_layout)
    LinearLayout rootLayout;
    private Context mContext;
    private boolean leftFinish = true;


    public CommonTitleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.common_layout_titlebar, this, true);
        ButterKnife.bind(view, this);
        imgBack.setOnClickListener(v -> {
            if (leftFinish) {
                ((Activity) mContext).finish();
            } else {
                if (specialLeftFinish != null) {
                    specialLeftFinish.specialLeftOption();
                }
            }
        });
        tvMiddleTitle.setVisibility(GONE);
        tvTitle.setVisibility(GONE);
    }

    public CommonTitleLayout setLeftTitle(String title) {
        tvTitle.setVisibility(VISIBLE);
        tvTitle.setText(title);
        return this;
    }

    public CommonTitleLayout setMiddleTitle(String title) {
        tvMiddleTitle.setVisibility(VISIBLE);
        tvMiddleTitle.setText(title);
        return this;
    }

    public CommonTitleLayout setRightMsg(String msg) {
        tvRight.setText(msg);
        return this;
    }

    /**
     * 设置背景颜色
     *
     * @param color R.color.xxxx
     * @return
     */
    public CommonTitleLayout setBgColor(int color) {
        rootLayout.setBackgroundColor(getResources().getColor(color));
        return this;
    }

    private SpecialLeftOptionListener specialLeftFinish;

    public void setSpecialLeftFinish(SpecialLeftOptionListener specialLeftFinish) {
        this.leftFinish = false;
        this.specialLeftFinish = specialLeftFinish;
    }

    public interface SpecialLeftOptionListener {
        void specialLeftOption();
    }

    /**
     * 设置是否按返回键关闭页面
     *
     * @param isFinish
     * @return
     */
    public CommonTitleLayout leftFinish(boolean isFinish) {
        this.leftFinish = isFinish;
        return this;
    }

}