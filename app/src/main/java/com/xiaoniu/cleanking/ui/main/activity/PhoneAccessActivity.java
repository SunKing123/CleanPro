package com.xiaoniu.cleanking.ui.main.activity;

import android.app.ActivityManager;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.ImageShowAdapter;
import com.xiaoniu.cleanking.ui.main.adapter.PhoneAccessAdapter;
import com.xiaoniu.cleanking.ui.main.adapter.PhoneAccessBelowAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.presenter.ImageListPresenter;
import com.xiaoniu.cleanking.ui.main.presenter.PhoneAccessPresenter;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 手机加速--一键清理内存页面
 */
public class PhoneAccessActivity extends BaseActivity<PhoneAccessPresenter> {

    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    @BindView(R.id.tv_size)
    TextView tv_size;
    @BindView(R.id.tv_gb)
    TextView tv_gb;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    PhoneAccessAdapter imageAdapter;
    PhoneAccessBelowAdapter belowAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_phone_access;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }


    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= 26) {

        } else {
            mPresenter.getAccessListBelow();
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        computeTotalSize(listInfo);
        belowAdapter = new PhoneAccessBelowAdapter(PhoneAccessActivity.this, listInfo);
        recycle_view.setLayoutManager(new LinearLayoutManager(PhoneAccessActivity.this));
        recycle_view.setAdapter(belowAdapter);
        belowAdapter.setmOnCheckListener(new PhoneAccessBelowAdapter.onCheckListener() {
            @Override
            public void onCheck(List<FirstJunkInfo> listFile, int pos) {
                int selectCount = 0;
                for (int i = 0; i < listFile.size(); i++) {
                    if (listFile.get(i).getIsSelect()) {
                        selectCount++;
                    }
                }
//                cb_checkall.setBackgroundResource(selectCount == listFile.size() ? R.drawable.icon_select : R.drawable.icon_unselect);
                tv_delete.setBackgroundResource(selectCount == 0 ? R.drawable.delete_unselect_bg : R.drawable.delete_select_bg);
                tv_delete.setSelected(selectCount == 0 ? false : true);
//                compulateDeleteSize();
            }
        });

        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<FirstJunkInfo> junkTemp = new ArrayList<>();
                for (FirstJunkInfo info : listInfo) {
                    if (info.getIsSelect()) {
                        junkTemp.add(info);
                    }
                }

                mPresenter.alertBanLiveDialog(PhoneAccessActivity.this, junkTemp.size(), new PhoneAccessPresenter.ClickListener() {
                    @Override
                    public void clickOKBtn() {
                        long total = 0;
                        for (FirstJunkInfo info : junkTemp) {
                            total += info.getTotalSize();
                            CleanUtil.killAppProcesses(info.getAppPackageName(), info.getPid());
                        }
                        belowAdapter.deleteData(junkTemp);
                        computeTotalSize(belowAdapter.getListImage());
                    }

                    @Override
                    public void cancelBtn() {

                    }
                });

            }
        });
    }


    //计算总的缓存大小
    public void computeTotalSize(ArrayList<FirstJunkInfo> listInfo){
        long totalSizes = 0;
        for (FirstJunkInfo firstJunkInfo : listInfo)
            totalSizes += firstJunkInfo.getTotalSize();
        String str_totalSize = CleanAllFileScanUtil.byte2FitSize(totalSizes);
        Log.e("sdfg", "" + str_totalSize);
        if (str_totalSize.endsWith("MB") || str_totalSize.endsWith("GB") || str_totalSize.endsWith("KB")) {
            tv_size.setText(str_totalSize.substring(0, str_totalSize.length() - 2));
            tv_gb.setText(str_totalSize.substring(str_totalSize.length() - 2, str_totalSize.length()));
        } else {
            tv_size.setText(str_totalSize.substring(0, str_totalSize.length() - 1));
            tv_gb.setText(str_totalSize.substring(str_totalSize.length() - 1, str_totalSize.length()));
        }
    }
    public void getAccessListAbove22(List<ActivityManager.RunningAppProcessInfo> listInfo) {

    }

    @Override
    public void netError() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
