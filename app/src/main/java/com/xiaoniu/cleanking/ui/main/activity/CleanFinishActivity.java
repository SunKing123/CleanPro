package com.xiaoniu.cleanking.ui.main.activity;

import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.utils.CleanUtil;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouteConstants.CLEAN_FINISH_ACTIVITY)
public class CleanFinishActivity extends SimpleActivity {

    @BindView(R.id.text_title)
    TextView mTextTitle;
    @BindView(R.id.tv_size)
    TextView mTvSize;
    @BindView(R.id.tv_gb)
    TextView mTvGb;
    @BindView(R.id.tv_ql)
    TextView mTvQl;
    @BindView(R.id.layout_right_content)
    LinearLayout mLayoutRightContent;
    @BindView(R.id.tv_number_cool)
    TextView mTvNumberCool;
    @BindView(R.id.layout_right_cooling)
    LinearLayout mLayoutRightCooling;
    private String mCleanType;

    /**
     * 一键清理
     */
    public static final String TYPE_CLEAN_CACHE = "TYPE_CLEAN_CACHE";

    /**
     * 降温
     */
    public static final String TYPE_COOLING = "TYPE_COOLING";

    /**
     * 大文件清理
     */
    public static final String TYPE_BIG_FILE = "TYPE_BIG_FILE";
    /**
     * 清理的数量
     */
    private long mCleanCount;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_clean_finish;
    }

    @Override
    protected void initView() {

        //清理类型
        mCleanType = getIntent().getStringExtra("CLEAN_TYPE");
        //清理数量
        mCleanCount = getIntent().getLongExtra("clean_count", 0);

        initData();

    }

    private void initData() {
        if (TYPE_CLEAN_CACHE.equals(mCleanType)) {
            //一键清理
            mTextTitle.setText("一键清理");
            setGarbageCleanData();
        } else if (TYPE_COOLING.equals(mCleanType)) {
            mTvSize.setVisibility(View.GONE);
            mLayoutRightContent.setVisibility(View.GONE);
            mLayoutRightCooling.setVisibility(View.VISIBLE);
            mTvNumberCool.setText("成功降温" + mCleanCount + "°C");
        } else if (TYPE_BIG_FILE.equals(mCleanType)) {
            //手机（大文件）清理
            mTextTitle.setText("手机清理");
            setGarbageCleanData();
        }
    }

    public void setGarbageCleanData() {
        CountEntity countEntity = CleanUtil.formatShortFileSize(mCleanCount);
        mTvSize.setText(countEntity.getTotalSize());
        mTvGb.setText(countEntity.getUnit());
        mTvQl.setText("垃圾已清理");
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
