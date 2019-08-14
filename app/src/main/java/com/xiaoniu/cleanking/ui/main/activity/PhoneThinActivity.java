package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.presenter.PhoneThinPresenter;
import com.xiaoniu.cleanking.ui.main.widget.ViewHelper;
import com.xiaoniu.cleanking.utils.FileSizeUtils;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 手机瘦身
 * Created by lang.chen on 2019/7/9
 */
public class PhoneThinActivity extends BaseActivity<PhoneThinPresenter> {

    //扫描路径
    private String mPath = Environment.getExternalStorageDirectory().getPath();

    //可用空间大小
    public static final String PARAMS_SPACE_SIZE_AVAILABLE = "params_space_size_available";

    //占内存百分比
    @BindView(R.id.txt_space_size)
    TextView mTxtSpaceSize;
    @BindView(R.id.txt_scan_content)
    TextView mTxtScanContent;
    @BindView(R.id.img_progress)
    ImageView mImgProgress;
    @BindView(R.id.img_progress_system)
    ImageView mImgProgressSystem;

    @BindView(R.id.iv_scan_frame)
    ImageView mIvScanFrame;
    @BindView(R.id.tv_use_space)
    TextView tv_use_space;
    private long mTotalSize;
    private ObjectAnimator objectAnimatorScanIng;
    @BindView(R.id.ll_video_file)
    LinearLayout mLlVideoFile;
    @BindView(R.id.ll_system_space)
    LinearLayout mLlSystemSpace;
    //是否在扫描中状态
    private ObjectAnimator roundAnim1;
    private ObjectAnimator roundAnim3;
    private String mTitleName;
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
        return R.layout.activity_phone_thin;
    }

    @Override
    protected void initView() {
        ViewHelper.setTextViewToDDINOTF(mTxtSpaceSize);
        mTotalSize = mPresenter.queryStorageSize(mPath);
        Intent intent = getIntent();
        if(intent != null){
            mTitleName = intent.getStringExtra(SpCacheConfig.ITEM_TITLE_NAME);
            mTvTitleName.setText(mTitleName);
            if (getString(R.string.tool_phone_thin).equals(mTitleName)){
                //视频专清
                mLlVideoFile.setVisibility(View.VISIBLE);
            }else {
                mLlSystemSpace.setVisibility(View.VISIBLE);
            }
        }

        objectAnimatorScanIng = mPresenter.setScaningAnim(mIvScanFrame);
        setScanStatus(true);

        //8.0权限判断
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!hasUsageStatsPermission(this)) {
                //没有权限
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY), 0x111);
                mPresenter.scanFile(mPath);
            }else {
                //有权限
                mPresenter.scanData();

            }
        }else {
            mPresenter.scanData();

        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasUsageStatsPermission(Context context) {
        //http://stackoverflow.com/a/42390614/878126
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return false;
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        final int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        boolean granted = false;
        if (mode == AppOpsManager.MODE_DEFAULT)
            granted = (context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        else
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        return granted;
    }


    private long mCurrentTime;
    private long mSizeL;

    public void updateText(String path, long size) {

        if (!TextUtils.isEmpty(path)) {

            if (System.currentTimeMillis() - mCurrentTime > 50) {
                if (null != mTxtScanContent) {
                    mTxtScanContent.setText(String.format("正在扫描:%s", path));
                }
                if (null != mTxtSpaceSize) {
                    mCurrentTime = System.currentTimeMillis();
                    Log.i("test","size="+ FileSizeUtils.formatFileSize(size)+",totalSize="+FileSizeUtils.formatFileSize(mTotalSize));

                    mTxtSpaceSize.setText(mPresenter.accuracy(size+mSizeL, mTotalSize, 0));
                }
            }
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
                if (mTxtSpaceSize == null)
                    return;

                mSizeL=size;
                mTxtSpaceSize.setText(mPresenter.accuracy(size, mTotalSize, 0));
                mPresenter.scanFile(mPath);
            }
        });
    }



    @OnClick({R.id.img_back})
    public void onViewClick(View view) {
        int ids = view.getId();
        if (ids == R.id.img_back) {
            finish();
        }
    }

    public void setScanStatus(boolean isScaning) {
        if (mImgProgress == null) return;
        mImgProgress.setImageResource(isScaning ? R.mipmap.icon_pro : R.mipmap.icon_round);
        mImgProgressSystem.setImageResource(isScaning ? R.mipmap.icon_pro : R.mipmap.icon_round);
        if (isScaning) {
            roundAnim1 = mPresenter.playRoundAnim(mImgProgress);
            roundAnim3 = mPresenter.playRoundAnim(mImgProgressSystem);
        } else {
            roundAnim1.cancel();
            roundAnim3.cancel();
            mImgProgress.animate().rotation(0).setDuration(10).start();
            mImgProgressSystem.animate().rotation(0).setDuration(10).start();
        }
    }
    /**
     * 扫描完成
     */
    public void onComplete() {
        if (tv_use_space == null) return;
        mIvScanFrame.setVisibility(View.GONE);
        long fileTotalSize = mPresenter.getFileSize();
        String s = mPresenter.accuracy(fileTotalSize, mTotalSize, 0);
        if (Double.valueOf(s) == 0) {
            mTxtSpaceSize.setText("1");
        }else {
            mTxtSpaceSize.setText(s);
        }
        if (objectAnimatorScanIng != null) objectAnimatorScanIng.cancel();
        setScanStatus(false);
        if (null != mImgProgressSystem) {
            mImgProgressSystem.postDelayed(() -> {
                Intent intent = new Intent(PhoneThinActivity.this, PhoneThinResultActivity.class);
                intent.putExtra(PARAMS_SPACE_SIZE_AVAILABLE, mPresenter.accuracy(fileTotalSize, mTotalSize, 0));
                intent.putExtra(SpCacheConfig.ITEM_TITLE_NAME,mTitleName);
                startActivity(intent);
                finish();
            }, 500);

        }
    }
}
