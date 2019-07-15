package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.CleanExpandAdapter;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstLevelEntity;
import com.xiaoniu.cleanking.ui.main.bean.ThirdLevelEntity;
import com.xiaoniu.cleanking.ui.main.event.ScanFileEvent;
import com.xiaoniu.cleanking.ui.main.presenter.CleanBigFilePresenter;
import com.xiaoniu.cleanking.ui.main.widget.CleanAnimView;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
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
    @BindView(R.id.layout_current_select)
    LinearLayout mLayoutCurrentSelect;
    @BindView(R.id.layout_wait_select)
    LinearLayout mLayoutWaitSelect;
    @BindView(R.id.tv_size)
    TextView mTvSize;
    @BindView(R.id.tv_gb)
    TextView mTvGb;
    @BindView(R.id.acceview)
    CleanAnimView mCleanAnimView;
    @BindView(R.id.web_view)
    WebView mWebView;
    private CleanExpandAdapter mCleanBigFileAdapter;

    private List<MultiItemEntity> mData;

    private List<ThirdLevelEntity> mAllData = new ArrayList<>();

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

        mCleanAnimView.setOnColorChangeListener(this::showBarColor);
    }

    private void initAdapter() {

        mData = new ArrayList<>();

        mCleanBigFileAdapter = new CleanExpandAdapter(mData);

        mJunkList.setLayoutManager(new LinearLayoutManager(mContext));

        mJunkList.setAdapter(mCleanBigFileAdapter);

        //条目选择监听
        mCleanBigFileAdapter.setOnItemSelectListener((isCheck, entity) -> {
            if (isCheck) {
                mAllData.add(entity);
            } else {
                mAllData.remove(entity);
            }
            updateSelectCount();
        });
    }

    /**
     * 更新选择的数据
     */
    private void updateSelectCount() {
        long total = 0;
        for (ThirdLevelEntity entity : mAllData) {
            if (entity.isChecked()) {
                total += entity.getFile().length();
            }
        }
        if (total > 0) {
            CountEntity countEntity = CleanUtil.formatShortFileSize(total);
            mDoJunkClean.setText("清理 " + countEntity.getTotalSize() + countEntity.getUnit());
            mTvSize.setText(countEntity.getTotalSize());
            mTvGb.setText(countEntity.getUnit());
            mLayoutWaitSelect.setVisibility(View.GONE);
            mLayoutCurrentSelect.setVisibility(View.VISIBLE);
        } else {
            mDoJunkClean.setText("完成");
            mLayoutWaitSelect.setVisibility(View.VISIBLE);
            mLayoutCurrentSelect.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.img_back, R.id.do_junk_clean})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.do_junk_clean:
                //垃圾清理
                if("完成".equals(mDoJunkClean.getText().toString())){
                    finish();
                }else {
                    mPresenter.showDeleteDialog(mAllData);
                }
                break;
            default:
        }
    }

    /**
     * 大文件列表
     *
     * @param list
     */
    public void showList(FirstLevelEntity list) {
//        mData.addAll()
        mData.add(list);
        mCleanBigFileAdapter.notifyDataSetChanged();
        mCleanBigFileAdapter.expandAll();
    }

    /**
     * 总的大小
     *
     * @param total
     */
    public void showTotal(long total) {
        if (mTextTotal != null) {
            mTextTotal.setText("共发现" + CleanUtil.formatShortFileSize(AppApplication.getInstance(), total));
        }
    }

    /**
     * 清理完成
     *
     * @param total
     */
    public void cleanFinish(long total) {
        //清理完成后通知 文件数据库同步(陈浪)
        EventBus.getDefault().post(new ScanFileEvent());
    }

    /**
     * 开始清理动画
     *
     * @param countEntity
     */
    public void startCleanAnim(CountEntity countEntity) {
        mCleanAnimView.setData(countEntity);
        mCleanAnimView.setVisibility(View.VISIBLE);
        mCleanAnimView.startTopAnim(true);
    }

    /**
     * 状态栏颜色变化
     *
     * @param animatedValue
     */
    public void showBarColor(int animatedValue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, animatedValue, true);
        } else {
            StatusBarCompat.setStatusBarColor(this, animatedValue, false);
        }
    }

}
