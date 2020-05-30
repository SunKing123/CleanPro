package com.xiaoniu.cleanking.ui.tool.qq.activity;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.tool.qq.adapter.QQCleanAudAdapter;
import com.xiaoniu.cleanking.ui.tool.qq.bean.CleanWxClearInfo;
import com.xiaoniu.cleanking.ui.tool.qq.presenter.QQCleanAudPresenter;
import com.xiaoniu.cleanking.ui.tool.qq.util.QQUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.widget.CustomLinearLayoutManger;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * QQ清理语音
 */
public class QQCleanAudActivity extends BaseActivity<QQCleanAudPresenter> {
    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;
    @BindView(R.id.cons_title)
    ConstraintLayout cons_title;
    @BindView(R.id.layout_not_net)
    LinearLayout layout_not_net;
    @BindView(R.id.cb_checkall)
    TextView cb_checkall;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    List<CleanWxClearInfo> listData = new ArrayList<>();
    QQCleanAudAdapter audAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_qqclean_aud;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }


    @OnClick({R.id.iv_back, R.id.tv_delete})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.iv_back) {
            finish();
            StatisticsUtils.trackClick("qq_voice_return_click", "qq语音返回点击", "qq_cleaning_page", "qq_voice_cleaning_page");
        } else if (ids == R.id.tv_delete) {
            if (!tv_delete.isSelected())
                return;
            finish();
            StatisticsUtils.trackClick("qq_voice_confirm_the_selection_click", "\"确认选中\"点击", "qq_cleaning_page", "qq_voice_cleaning_page");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("qq_voice_cleaning_view_page", "语音清理页面浏览");
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("qq_voice_cleaning_view_page", "语音清理页面浏览");
    }

    @Override
    public void initView() {

        if (listData == null || QQUtil.audioList == null)
            return;
        listData = QQUtil.audioList;

        if (listData.size() == 0) {
            cons_title.setVisibility(View.GONE);
            recycle_view.setVisibility(View.GONE);
            layout_not_net.setVisibility(View.VISIBLE);
            tv_delete.setBackgroundResource(R.drawable.delete_unselect_bg);
            return;
        } else {
            cons_title.setVisibility(View.VISIBLE);
            recycle_view.setVisibility(View.VISIBLE);
            layout_not_net.setVisibility(View.GONE);
        }

        audAdapter = new QQCleanAudAdapter(QQCleanAudActivity.this, listData);
        recycle_view.setLayoutManager(new CustomLinearLayoutManger(QQCleanAudActivity.this));
        recycle_view.setAdapter(audAdapter);
        audAdapter.setmOnCheckListener((listFile, pos) -> {
            int selectCount = 0;
            for (int i = 0; i < listFile.size(); i++) {
                if (listFile.get(i).getIsSelect()) {
                    selectCount++;
                }
            }
            cb_checkall.setBackgroundResource(selectCount == listFile.size() ? R.drawable.icon_select : R.drawable.icon_unselect);
            tv_delete.setBackgroundResource(selectCount == 0 ? R.drawable.delete_unselect_bg : R.drawable.delete_select_bg);
            tv_delete.setSelected(selectCount == 0 ? false : true);
            compulateDeleteSize();
        });

        tv_delete.setSelected(false);
        cb_checkall.setSelected(false);
        cb_checkall.setOnClickListener(v -> {
            if (listData.size() == 0) return;
            if (!recycle_view.isComputingLayout()) {
                cb_checkall.setSelected(!cb_checkall.isSelected());
                tv_delete.setSelected(cb_checkall.isSelected());
                audAdapter.setIsCheckAll(cb_checkall.isSelected() ? true : false);
                cb_checkall.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.icon_select : R.drawable.icon_unselect);
                tv_delete.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
                compulateDeleteSize();
                StatisticsUtils.trackClick("qq_voice_cleaning_all_election_click", "全选按钮点击", "qq_cleaning_page", "qq_voice_cleaning_page");
            }
        });
        compulateDeleteSize();

    }

    //计算删除文件大小
    public void compulateDeleteSize() {
        List<CleanWxClearInfo> listF = new ArrayList<>();
        List<CleanWxClearInfo> listData = audAdapter.getListImage();
        for (int i = 0; i < listData.size(); i++) {
            if (listData.get(i).getIsSelect())
                listF.add(listData.get(i));
        }
        long deleteSize = 0;
        for (int i = 0; i < listF.size(); i++) {
            deleteSize += listF.get(i).getSize();
        }
        tv_delete.setText(deleteSize == 0 ? "未选中" : "确认选中 " + CleanAllFileScanUtil.byte2FitSizeOne(deleteSize));
        tv_delete.setBackgroundResource(deleteSize != 0 ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
        cb_checkall.setBackgroundResource(listData.size() == listF.size() ? R.drawable.icon_select : R.drawable.icon_unselect);
    }

    //删除成功
    public void deleteSuccess(List<CleanWxClearInfo> listF) {
        tv_delete.setSelected(false);
        tv_delete.setText("未选中");
        audAdapter.deleteData(listF);
    }

    @Override
    public void netError() {

    }
}
