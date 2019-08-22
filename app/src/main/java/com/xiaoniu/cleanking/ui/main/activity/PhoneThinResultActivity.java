package com.xiaoniu.cleanking.ui.main.activity;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.presenter.PhoneThinResultPresenter;
import com.xiaoniu.cleanking.ui.main.widget.ViewHelper;
import com.xiaoniu.cleanking.utils.FileSizeUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 手机瘦身结果页
 * Created by lang.chen on 2019/7/9
 */
public class PhoneThinResultActivity extends BaseActivity<PhoneThinResultPresenter> {

    @BindView(R.id.txt_video_size)
    TextView mTxtVideoSize;
    @BindView(R.id.txt_space_size)
    TextView mTxtSpaceSize;

    @BindView(R.id.txt_install_size)
    TextView mTxtInstallSize;//安装应用大小
    @BindView(R.id.txt_soft_size)
    TextView mTxtSoftSize;//软件大小
    private String mSize;
    private String mTitleName;
    @BindView(R.id.ll_video_file)
    LinearLayout mLlVideoFile;
    @BindView(R.id.ll_video)
    LinearLayout mLlVideo;
    @BindView(R.id.ll_soft_title)
    LinearLayout mLlSoftTitle;
    @BindView(R.id.ll_soft)
    LinearLayout mLlSoft;
    @BindView(R.id.tv_title_name)
    TextView mTvTitleName;
    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_thin_result;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        if(intent != null){
            mSize = intent.getStringExtra(PhoneThinActivity.PARAMS_SPACE_SIZE_AVAILABLE);
            mTitleName = intent.getStringExtra(SpCacheConfig.ITEM_TITLE_NAME);
            mTvTitleName.setText(mTitleName);
        }

        if (getString(R.string.tool_phone_thin).equals(mTitleName)){
            //视频专清
            mLlVideoFile.setVisibility(View.VISIBLE);
            mLlVideo.setVisibility(View.VISIBLE);
        }else {
            //软件管理
            mLlSoftTitle.setVisibility(View.VISIBLE);
            mLlSoft.setVisibility(View.VISIBLE);
        }
       setData();
        ViewHelper.setTextViewToDDINOTF(mTxtSpaceSize);

        //8.0权限判断
        if (android.os.Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {
            if (!hasUsageStatsPermission(this)) {
                //没有权限
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY), 0x111);
            }else {
                //有权限
                mPresenter.scanData();

            }
        }else {
            mPresenter.scanData();

        }
    }

    private void setData() {
        mTxtVideoSize.setText(FileSizeUtils.formatFileSize(mPresenter.getVideoTotalSize()));
        if (Double.valueOf(mSize) == 0) {
            mTxtSpaceSize.setText("1");
        }else {
            mTxtSpaceSize.setText(mSize);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasUsageStatsPermission(Context context) {
        //http://stackoverflow.com/a/42390614/878126
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return false;
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        final int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        boolean granted;
        if (mode == AppOpsManager.MODE_DEFAULT)
            granted = (context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        else
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        return granted;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StatisticsUtils.trackClick("cell_phone_slimming_return_click","\"手机瘦身返回\"点击"
                ,"clean_up_toolbox_page","cell_phone_slimming_page");
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.scanData();
        setData();
        NiuDataAPI.onPageStart("cell_phone_slimming_view_page","手机瘦身页面浏览");
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("cell_phone_slimming_view_page","手机瘦身页面浏览");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0x111
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED){
            //
            mPresenter.scanData();
        }
    }

    @OnClick({R.id.img_back, R.id.ll_video, R.id.ll_soft})
    public void onViewClick(View view) {
        int ids = view.getId();
        if (ids == R.id.img_back) {
            StatisticsUtils.trackClick("cell_phone_slimming_return_click","\"手机瘦身返回\"点击"
                    ,"clean_up_toolbox_page","cell_phone_slimming_page");
            finish();
        } else if (ids == R.id.ll_video) {
            StatisticsUtils.trackClick("cleanable_video_file_click","\"可清理的视频文件\"点击"
                    ,"clean_up_toolbox_page","cell_phone_slimming_page");
            startActivity(new Intent(this, CleanVideoManageActivity.class));
        } else if (ids == R.id.ll_soft) {
            StatisticsUtils.trackClick("Software_uninstaller","\"软件卸载\"点击"
                    ,"clean_up_toolbox_page","cell_phone_slimming_page");
            startActivity(new Intent(this,SoftManageActivity.class));
        }
    }

    /**
     * 更新软件数据
     * @param size 安装应用大小
     * @param totalSize  应用总大小
     */
    public void updateData(int size,long totalSize){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(null!=mTxtInstallSize){
                    mTxtInstallSize.setText(String.format("已安装%s款",size));
                }
                if(null!=mTxtSoftSize){
                    mTxtSoftSize.setText(FileSizeUtils.formatFileSize(totalSize));
                }

            }
        });
    }


}
