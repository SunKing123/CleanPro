package com.xiaoniu.cleanking.ui.main.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.CleanBigFileAdapter;
import com.xiaoniu.cleanking.ui.main.bean.BigFileInfoEntity;
import com.xiaoniu.cleanking.ui.main.presenter.CleanBigFilePresenter;
import com.xiaoniu.cleanking.utils.CleanUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouteConstants.CLEAN_BIG_FILE_ACTIVITY)
public class CleanBigFileActivity extends BaseActivity<CleanBigFilePresenter> {

    @BindView(R.id.img_back)
    ImageView mImgBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.text_total)
    TextView mTextTotal;
    @BindView(R.id.layout_title_bar)
    RelativeLayout mLayoutTitleBar;
    @BindView(R.id.do_junk_clean)
    TextView mDoJunkClean;
    @BindView(R.id.layout_junk_clean)
    FrameLayout mLayoutJunkClean;
    @BindView(R.id.junk_list)
    RecyclerView mJunkList;
    @BindView(R.id.layout_show_list)
    RelativeLayout mLayoutShowList;
    @BindView(R.id.text_clean_finish_title)
    TextView mTextCleanFinishTitle;
    @BindView(R.id.text_clean_number)
    TextView mTextCleanNumber;
    @BindView(R.id.layout_clean_finish)
    RelativeLayout mLayoutCleanFinish;
    private CleanBigFileAdapter mCleanBigFileAdapter;

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_big_file_clean;
    }

    @Override
    protected void initView() {

        mTvTitle.setText("手机清理");

        initAdapter();
        //大文件扫描
        mPresenter.scanBigFile();
    }

    private void initAdapter() {
        mCleanBigFileAdapter = new CleanBigFileAdapter();

        mJunkList.setLayoutManager(new LinearLayoutManager(mContext));

        mJunkList.setAdapter(mCleanBigFileAdapter);
    }

    @OnClick({R.id.img_back, R.id.do_junk_clean})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.do_junk_clean:
                break;
                default:
        }
    }

    /**
     * 大文件列表
     * @param list
     */
    public void showList(List<BigFileInfoEntity> list) {
        mCleanBigFileAdapter.setData(list);
    }

    /**
     * 总的大小
     * @param total
     */
    public void showTotal(long total) {
        mTextTotal.setText(CleanUtil.formatShortFileSize(AppApplication.getInstance(),total) + "文件");
    }
}
