package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.CleanExpandAdapter;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.FirstLevelEntity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.ThirdLevelEntity;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.NotificationEvent;
import com.xiaoniu.cleanking.ui.main.event.ScanFileEvent;
import com.xiaoniu.cleanking.ui.main.presenter.CleanBigFilePresenter;
import com.xiaoniu.cleanking.ui.main.widget.CleanAnimView;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

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
    @BindView(R.id.layout_no_file)
    LinearLayout mLayoutNoFile;
    private CleanExpandAdapter mCleanBigFileAdapter;

    private List<MultiItemEntity> mData;

    private List<ThirdLevelEntity> mAllData = new ArrayList<>();
    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //使用内存占总RAM的比例

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
        mPresenter.getAccessListBelow();
        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPowerSize = new FileQueryUtils().getRunningProcess().size();

        initAdapter();
        //大文件扫描
        mPresenter.scanBigFile();

        mCleanAnimView.setOnColorChangeListener(this::showBarColor);
        mCleanAnimView.setListener(() -> finish());
        mCleanAnimView.setAnimationEnd(() -> {


            boolean isOpen = false;
            //solve umeng error --> SwitchInfoList.getData()' on a null object reference
            if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                    && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                        isOpen = switchInfoList.isOpen();
                    }
                }
            }
            EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
            if (isOpen && PreferenceUtil.getShowCount(this, getString(R.string.tool_phone_clean), mRamScale, mNotifySize, mPowerSize) < 3) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_phone_clean));
                startActivity(CleanFinishAdvertisementActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tool_phone_clean));
                bundle.putString("num", "");
                bundle.putString("unit", "");
                startActivity(NewCleanFinishActivity.class, bundle);
            }
            finish();
        });
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
                if (mCleanAnimView != null && mCleanAnimView.getVisibility() == View.VISIBLE) {
                    StatisticsUtils.trackClick("cell_phone_clean_click", "手机清理返回按钮点击", "home_page", "");
                } else {
                    StatisticsUtils.trackClick(" mobile_cleaning_scan_clean_up_click", "清理完成返回按钮点击", "home_page", "");
                }
                break;
            case R.id.do_junk_clean:
                //垃圾清理
                if ("完成".equals(mDoJunkClean.getText().toString())) {
                    finish();
                } else {
                    mPresenter.showDeleteDialog(mAllData);
                    StatisticsUtils.trackClick("clean_click", "清理点击", "home_page", "");
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
        if (total <= 0) {
            mLayoutNoFile.setVisibility(View.VISIBLE);
            mJunkList.setVisibility(View.GONE);
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
        mCleanAnimView.setData(countEntity, CleanAnimView.page_file_clean);
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

    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("cell_phone_clean_click_view", "手机清理页面浏览");


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCleanAnimView != null && mCleanAnimView.getVisibility() == View.VISIBLE) {
            NiuDataAPI.onPageEnd("mobile_clean_up_page_view", "手机清理页浏览");
        } else {
            NiuDataAPI.onPageEnd("cell_phone_clean_click_view", "手机清理页面浏览");
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK)
            StatisticsUtils.trackClick("system_return_back", "\"手机返回\"点击", "", "one_click_acceleration_page");
        return super.onKeyDown(keyCode, event);
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null) return;
        //清理管家极速版app加入默认白名单
       /* for (FirstJunkInfo firstJunkInfo : listInfo) {
            if (SpCacheConfig.APP_ID.equals(firstJunkInfo.getAppPackageName())) {
                listInfo.remove(firstJunkInfo);
            }
        }*/
        if (listInfo.size() != 0) {
            mRamScale = new FileQueryUtils().computeTotalSize(listInfo);
        }
    }
}
