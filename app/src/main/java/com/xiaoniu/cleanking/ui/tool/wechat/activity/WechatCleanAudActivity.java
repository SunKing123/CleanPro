package com.xiaoniu.cleanking.ui.tool.wechat.activity;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.event.WxQqCleanEvent;
import com.xiaoniu.cleanking.ui.main.presenter.ImageListPresenter;
import com.xiaoniu.cleanking.ui.tool.wechat.adapter.WechatCleanAudAdapter;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxEasyInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxFourItemInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxItemInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.presenter.WechatCleanAudPresenter;
import com.xiaoniu.cleanking.ui.tool.wechat.util.WxQqUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 微信清理语音
 */
public class WechatCleanAudActivity extends BaseActivity<WechatCleanAudPresenter> {
    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;
    @BindView(R.id.cb_checkall)
    TextView cb_checkall;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    @BindView(R.id.cons_title)
    ConstraintLayout cons_title;
    @BindView(R.id.layout_not_net)
    LinearLayout layout_not_net;
    CleanWxEasyInfo cleanWxEasyInfoAud;
    ArrayList<CleanWxItemInfo> listData = new ArrayList<>();
    WechatCleanAudAdapter audAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wxclean_aud;
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
            StatisticsUtils.trackClick("wechat_voice_return_click", "微信语音返回点击", "wechat_cleaning_page", "wechat_voice_cleaning_page");
        } else if (ids == R.id.tv_delete) {
            if (!tv_delete.isSelected())
                return;
            List<CleanWxItemInfo> listF = new ArrayList<>();
            List<CleanWxItemInfo> listData = audAdapter.getListImage();
            for (int i = 0; i < listData.size(); i++) {
                if (listData.get(i).getIsSelect())
                    listF.add(audAdapter.getListImage().get(i));
            }
            StatisticsUtils.trackClick("wechat_voice_cleaning_delete_click", "删除按钮点击", "wechat_cleaning_page", "wechat_voice_cleaning_page");

            mPresenter.alertBanLiveDialog(WechatCleanAudActivity.this, listF.size(), new ImageListPresenter.ClickListener() {
                @Override
                public void clickOKBtn() {
                    //删除本地文件
                    mPresenter.delFile(listF);
                    //数据库删除选中的文件
                }

                @Override
                public void cancelBtn() {

                }
            });

            String pageName = "";
            if (AppManager.getAppManager().preActivityName().contains("FileManagerHomeActivity")) {
                pageName = "file_cleaning_page";
            }
        }
    }

    @Override
    public void initView() {

        cleanWxEasyInfoAud = WxQqUtil.k;

        List<CleanWxFourItemInfo> listFour = new ArrayList<>();
        List<CleanWxItemInfo> listDataTemp = new ArrayList<>();
        for (int i = 0; i < cleanWxEasyInfoAud.getList().size(); i++) {
            if (cleanWxEasyInfoAud.getList().get(i) instanceof CleanWxFourItemInfo) {
                CleanWxFourItemInfo cleanWxHeadInfo = (CleanWxFourItemInfo) cleanWxEasyInfoAud.getList().get(i);
                listFour.add(cleanWxHeadInfo);
            }
        }

        for (int j = 0; j < listFour.size(); j++) {
            listDataTemp.addAll(listFour.get(j).getFourItem());
        }

        for (int j = 0; j < listDataTemp.size(); j++) {
            if (listDataTemp.get(j).getFile().getAbsolutePath().endsWith("amr")) {
                listDataTemp.get(j).setIsSelect(false);
                listData.add(listDataTemp.get(j));
            }

        }

        Log.e("qweewq", "" + listData.size());
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
        audAdapter = new WechatCleanAudAdapter(WechatCleanAudActivity.this, listData);
        recycle_view.setLayoutManager(new LinearLayoutManager(WechatCleanAudActivity.this));
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
                StatisticsUtils.trackClick("wechat_voice_cleaning_all_election_click", "全选按钮点击", "wechat_cleaning_page", "wechat_voice_cleaning_page");
                cb_checkall.setSelected(!cb_checkall.isSelected());
                tv_delete.setSelected(cb_checkall.isSelected());
                audAdapter.setIsCheckAll(cb_checkall.isSelected() ? true : false);
                cb_checkall.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.icon_select : R.drawable.icon_unselect);
                tv_delete.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
                compulateDeleteSize();
            }
        });
    }

    //计算删除文件大小
    public void compulateDeleteSize() {
        List<CleanWxItemInfo> listF = new ArrayList<>();
        List<CleanWxItemInfo> listData = audAdapter.getListImage();
        for (int i = 0; i < listData.size(); i++) {
            if (listData.get(i).getIsSelect())
                listF.add(listData.get(i));
        }
        long deleteSize = 0;
        for (int i = 0; i < listF.size(); i++) {
            deleteSize += listF.get(i).getFileSize();
        }
        tv_delete.setText(deleteSize == 0 ? "删除" : "删除 " + CleanAllFileScanUtil.byte2FitSizeOne(deleteSize));
    }

    //删除成功
    public void deleteSuccess(List<CleanWxItemInfo> listF) {
        tv_delete.setSelected(false);
        tv_delete.setText("删除");
        tv_delete.setBackgroundResource(R.drawable.delete_unselect_bg);
        audAdapter.deleteData(listF);
    }

    @Override
    public void netError() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("wechat_voice_cleaning_view_page", "语音清理页面浏览");
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("wechat_voice_cleaning_view_page", "语音清理页面浏览");
    }

    @Override
    public void finish() {
        EventBus.getDefault().post(new WxQqCleanEvent(WxQqCleanEvent.WX_CLEAN_AUDIO, getAllFileSize()));
        super.finish();
    }

    //获取当前文件大小
    public long getAllFileSize() {
        if (audAdapter == null)
            return 0;
        long fileSize = 0;
        List<CleanWxItemInfo> listAll = new ArrayList<>();
        List<CleanWxItemInfo> listData = audAdapter.getListImage();
        listAll.addAll(listData);
        for (int i = 0; i < listAll.size(); i++) {
            fileSize += listAll.get(i).getFileSize();
        }
        return fileSize;
    }
}
