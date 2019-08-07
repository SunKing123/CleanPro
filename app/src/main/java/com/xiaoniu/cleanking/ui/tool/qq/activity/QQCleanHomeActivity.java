package com.xiaoniu.cleanking.ui.tool.qq.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.activity.QQCleanImgActivity;
import com.xiaoniu.cleanking.ui.main.activity.QQCleanVideoActivity;
import com.xiaoniu.cleanking.ui.main.bean.FileChildEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.widget.ViewHelper;
import com.xiaoniu.cleanking.ui.tool.qq.bean.CleanWxClearInfo;
import com.xiaoniu.cleanking.ui.tool.qq.presenter.QQCleanHomePresenter;
import com.xiaoniu.cleanking.ui.tool.qq.util.QQUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanAudActivity;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanFileActivity;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanResultActivity;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxEasyInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.Constants;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.Logger;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.WxAndQqScanPathInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.util.PrefsCleanUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.util.QueryFileUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.util.WxQqUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.utils.ThreadTaskUtil;
import com.xiaoniu.statistic.NiuDataAPI;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.http.POST;

/**
 * 微信清理首页
 */
public class QQCleanHomeActivity extends BaseActivity<QQCleanHomePresenter> {

    //qq图片请求编码
    private static final int REQUEST_CODE_QQ_IMG = 0x3301;
    //qq视频请求编码
    private static final int REQUEST_CODE_QQ_VIDEO = 0x3302;
    @BindView(R.id.tv_gabsize)
    TextView tvGabsize;
    @BindView(R.id.tv_gb)
    TextView tvGb;
    @BindView(R.id.rel_gasize)
    RelativeLayout relGasize;
    @BindView(R.id.rel_selects)
    RelativeLayout relSelects;
    @BindView(R.id.tv_ql)
    TextView tv_ql;
    @BindView(R.id.tv1_top)
    TextView tv1Top;
    @BindView(R.id.tv_video_size)
    TextView tvVideoSize;
    @BindView(R.id.tv_pic_size)
    TextView tvPicSize;
    @BindView(R.id.tv_file_size)
    TextView tvFileSize;
    @BindView(R.id.tv_aud_size)
    TextView tvAudSize;
    @BindView(R.id.tv_wxgabage_size)
    TextView tvWxgabageSize;
    @BindView(R.id.tv1_file)
    TextView tv1File;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.tv_select1)
    TextView tvSelect1;
    @BindView(R.id.tv_select_size)
    TextView tvSelectSize;
    @BindView(R.id.iv_dun)
    ImageView iv_dun;
    @BindView(R.id.iv_gabcache)
    ImageView ivGabcache;
    @BindView(R.id.iv_scan_frame)
    ImageView ivScanFrame;
    @BindView(R.id.iv_chatfile)
    ImageView ivChatfile;
    @BindView(R.id.iv_hua3)
    ImageView ivHua3;
    @BindView(R.id.tv_selectaud)
    TextView tvSelectAud;
    @BindView(R.id.tv_selectfile)
    TextView tvSelectFile;
    @BindView(R.id.iv_hua1)
    ImageView ivHua1;
    @BindView(R.id.cons_gabcache)
    ConstraintLayout consGabcache;
    @BindView(R.id.cons_allfiles)
    ConstraintLayout consAllfiles;
    @BindView(R.id.line_sming)
    LinearLayout lineSming;
    @BindView(R.id.line_smed)
    LinearLayout lineSmed;
    ObjectAnimator objectAnimatorScanIng;
    boolean scanImgOver = false; //图片是否扫描完毕
    boolean scanVideoOver = false; //视频是否扫描完毕
    boolean scanGarbageOver = false; //垃圾是否扫描完毕
    //qq图片
    private ArrayList<FileTitleEntity> mListImg = new ArrayList<>();
    //qq视频
    private ArrayList<FileTitleEntity> mListVideo = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_qqclean_home;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }


    @Override
    public void initView() {
        this.i = new a(QQCleanHomeActivity.this);
        ViewHelper.setTextViewToDDINOTF(tvGabsize);
        ViewHelper.setTextViewToDDINOTF(tvGb);
        tvSelect1.setSelected(true);
        lineSming.setVisibility(View.VISIBLE);
        lineSmed.setVisibility(View.GONE);
        setScanStatus(true);
        getWindow().getDecorView().post(new Runnable() {
            public void run() {
                j();
                i();
            }
        });
        mPresenter.getImgQQ();
        mPresenter.getVideoFiles();

        objectAnimatorScanIng = mPresenter.setScaningAnim(ivScanFrame);
    }

    @OnClick({R.id.cons_aud, R.id.iv_gabcache, R.id.tv1_top, R.id.tv1_file, R.id.iv_chatfile, R.id.iv_back, R.id.tv_delete, R.id.tv_select1, R.id.cons_file
            , R.id.cons_pic, R.id.cons_wxsp})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.iv_back) {
            finish();
            StatisticsUtils.trackClick("qq_cleaning_return_click", "qq清理返回点击", "home_page", "qq_cleaning_page");
        } else if (ids == R.id.iv_gabcache) {
            consGabcache.setVisibility(consGabcache.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            ivGabcache.setImageResource(consGabcache.getVisibility() == View.VISIBLE ? R.mipmap.arrow_up : R.mipmap.arrow_down);
        } else if (ids == R.id.tv1_top) {
            consGabcache.setVisibility(consGabcache.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            ivGabcache.setImageResource(consGabcache.getVisibility() == View.VISIBLE ? R.mipmap.arrow_up : R.mipmap.arrow_down);
        } else if (ids == R.id.tv1_file) {
            consAllfiles.setVisibility(consAllfiles.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            ivChatfile.setImageResource(consAllfiles.getVisibility() == View.VISIBLE ? R.mipmap.arrow_up : R.mipmap.arrow_down);
        } else if (ids == R.id.iv_chatfile) {
            consAllfiles.setVisibility(consAllfiles.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            ivChatfile.setImageResource(consAllfiles.getVisibility() == View.VISIBLE ? R.mipmap.arrow_up : R.mipmap.arrow_down);
        } else if (ids == R.id.tv_delete) {
//            if (!tvSelect.isSelected() && !tvSelect1.isSelected()) return;
            mPresenter.onekeyCleanDelete(getCacheList(), tvSelect1.isSelected());
            StatisticsUtils.trackClick("cleaning_click", "清理点击", "home_page", "qq_cleaning_page");
        } else if (ids == R.id.tv_select1) {
            tvSelect1.setSelected(tvSelect1.isSelected() ? false : true);
            getSelectCacheSize();
            StatisticsUtils.trackClick("Spam_files_click", "垃圾文件点击", "home_page", "qq_cleaning_page");
        } else if (ids == R.id.cons_aud) {
            QQUtil.audioList = az;
            Intent intent = new Intent(QQCleanHomeActivity.this, QQCleanAudActivity.class);
            startActivity(intent);
            StatisticsUtils.trackClick("qq_voice_click", "qq语音点击", "home_page", "qq_cleaning_page");
        } else if (ids == R.id.cons_file) {
            QQUtil.fileList = aB;
            Intent intent = new Intent(QQCleanHomeActivity.this, QQCleanFileActivity.class);
            startActivity(intent);
            StatisticsUtils.trackClick("receive_files_click", "接收文件点击", "home_page", "qq_cleaning_page");
        } else if (ids == R.id.cons_pic) {
            //聊天图片
            Intent intent = new Intent(this, QQCleanImgActivity.class);
            startActivityForResult(intent, REQUEST_CODE_QQ_IMG);
            StatisticsUtils.trackClick("Chat_pictures_click", "聊天图片点击", "home_page", "qq_cleaning_page");
        } else if (ids == R.id.cons_wxsp) {
            //视频
            Intent intent = new Intent(this, QQCleanVideoActivity.class);
            startActivityForResult(intent, REQUEST_CODE_QQ_VIDEO);
            StatisticsUtils.trackClick("qq_video_click", "QQ视频点击", "home_page", "qq_cleaning_page");
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("qq_ceaning_view_page", "qq清理页面浏览");
        tvSelectFile.setText("已选择" + CleanAllFileScanUtil.byte2FitSizeOne(mPresenter.getSelectFileSize()));
        tvSelectAud.setText("已选择" + CleanAllFileScanUtil.byte2FitSizeOne(mPresenter.getSelectAudioSize()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("qq_ceaning_view_page", "qq清理页面浏览");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //获取扫描结果
    boolean isFirst = true;

    //获取扫描的所有垃圾文件
    public List<CleanWxClearInfo> getCacheList() {
        List<CleanWxClearInfo> listAllCache = new ArrayList<>();
        listAllCache.addAll(al);
        listAllCache.addAll(an);
        listAllCache.addAll(ah);
        listAllCache.addAll(ag);
        return listAllCache;
    }

    //扫描到图片文件
    public void updateQQImgSize(String size) {
        if (TextUtils.isEmpty(size) || "0".equals(size)) {
            tvPicSize.setText("");
        } else {
            tvPicSize.setText(size);
        }
        scanImgOver = true;
        updateScanResult();
    }

    //扫描到视频文件
    public void updateVideoSize(String size) {
        if (TextUtils.isEmpty(size) || "0".equals(size)) {
            tvVideoSize.setText("");
        } else {
            tvVideoSize.setText(size);
        }
        scanVideoOver = true;
        updateScanResult();
    }

    //扫描到缓存垃圾、语音、文件
    public void getScanGabageResult() {
        if (!isFirst) return;
        isFirst = false;
        scanGarbageOver = true;
        updateScanResult();
    }

    //开始播放动画显示扫描结果等
    public void updateScanResult() {
        if (!scanImgOver || !scanVideoOver || !scanGarbageOver) return;
        setScanStatus(false);
        tvWxgabageSize.setText("已选" + CleanAllFileScanUtil.byte2FitSizeOne(getSize(al) + getSize(an) + getSize(ah) + getSize(ag)));
        getSelectCacheSize();
        tvAudSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(getSize(az)));
        tvFileSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(getSize(aB)));
        String str_totalSize = CleanAllFileScanUtil.byte2FitSizeOne(getSize(al) + getSize(an) + getSize(ah) + getSize(ag));
        if (str_totalSize.endsWith("KB")) return;
        //数字动画转换，GB转成Mb播放，kb太小就不扫描
        float sizeMb = 0;
        if (str_totalSize.endsWith("MB")) {
            sizeMb = NumberUtils.getFloat(str_totalSize.substring(0, str_totalSize.length() - 2));

        } else if (str_totalSize.endsWith("GB")) {
            sizeMb = NumberUtils.getFloat(str_totalSize.substring(0, str_totalSize.length() - 2));
            sizeMb *= 1024;
        }

        ValueAnimator valueAnimator = mPresenter.setTextAnim(tvGabsize, 0, sizeMb);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ivScanFrame.setVisibility(View.GONE);
                if (objectAnimatorScanIng != null) objectAnimatorScanIng.cancel();
                lineSming.setVisibility(View.GONE);
                lineSmed.setVisibility(View.VISIBLE);
                mPresenter.setTextSizeAnim(tvGabsize, 110, 55);
                mPresenter.setTextSizeAnim(tvGb, 24, 12);
                mPresenter.setViewHeightAnim(relGasize, relSelects, tv_ql, iv_dun, DeviceUtils.dip2px(263), DeviceUtils.dip2px(123));
            }
        });
    }

    //是否在扫描中状态
    public void setScanStatus(boolean isScaning) {
        ivHua1.setImageResource(isScaning ? R.mipmap.icon_pro : R.drawable.icon_select);
        ivHua3.setImageResource(isScaning ? R.mipmap.icon_pro : R.drawable.icon_select);
    }

    //垃圾选中的大小
    public void getSelectCacheSize() {
        long selectSize = 0;
        if (tvSelect1.isSelected())
            selectSize += getSize(al) + getSize(an) + getSize(ah) + getSize(ag);
        tvSelectSize.setText("已经选择：" + CleanAllFileScanUtil.byte2FitSizeOne(selectSize));
        tvDelete.setText("清理 " + CleanAllFileScanUtil.byte2FitSizeOne(selectSize));
        tvDelete.setBackgroundResource(tvSelect1.isSelected() ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_NAME_WXQQ_CACHE, Context.MODE_PRIVATE);
        sp.edit().putLong(SpCacheConfig.QQ_CACHE_SIZE, selectSize).commit();
    }

    public void deleteResult(long result) {
        Intent intent = new Intent(QQCleanHomeActivity.this, WechatCleanResultActivity.class);
        intent.putExtra("data", result);
        startActivity(intent);
        finish();
    }

    @Override
    public void netError() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //图片请求返回
        if (requestCode == REQUEST_CODE_QQ_IMG && null != data && null != data.getExtras()) {
            ArrayList<FileTitleEntity> lists = (ArrayList<FileTitleEntity>) data.getExtras().getSerializable(Constant.PARAMS_QQ_IMG_LIST);
            if (null != lists && lists.size() > 0) {
                mListImg.clear();
                mListImg.addAll(lists);
            }

        } else if (requestCode == REQUEST_CODE_QQ_VIDEO && null != data && null != data.getExtras()) {
            //视频列表选中返回更新
            ArrayList<FileTitleEntity> lists = (ArrayList<FileTitleEntity>) data.getExtras().getSerializable(Constant.PARAMS_QQ_VIDEO_LIST);
            if (null != lists && lists.size() > 0) {
                mListVideo.clear();
                mListVideo.addAll(lists);
            }

        }
    }

    /**
     * 获取选中删除的元素
     *
     * @return
     */
    public List<FileChildEntity> getDelFiles(List<FileTitleEntity> fileTitleEntities) {
        List<FileChildEntity> files = new ArrayList<>();
        List<FileTitleEntity> lists = fileTitleEntities;
        for (FileTitleEntity fileTitleEntity : lists) {
            for (FileChildEntity file : fileTitleEntity.lists) {
                if (file.isSelect) {
                    files.add(file);
                }
            }
        }
        return files;
    }


    private void c() {
        this.e = true;
        if (this.f && this.e) {
            if (this.n > 0) {
//                PrefsCleanUtil.getInstance().putLong(Constants.CLEAN_QQ_TOTAL_SIZE, this.n + this.bT + this.T + this.Q + this.ac + this.W + this.Z + this.af);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getScanGabageResult();
                    }
                }, 300);
                Log.e("eeee", "aj大小：" + getSize(aj) + "  aj数量：" + aj.size());
                Log.e("eeee", "al大小：" + getSize(al) + "  aj数量：" + al.size());  //头像缓存(联网可重新下载)
                Log.e("eeee", "an大小：" + getSize(an) + "  an数量：" + an.size());  //空间缓存(浏览空间产生的图片缓存)
                Log.e("eeee", "ap大小：" + getSize(ap) + "  ap数量：" + ap.size());
                Log.e("eeee", "ar大小：" + getSize(ar) + "  ar数量：" + ar.size());

                Log.e("eeee", "at大小：" + getSize(at) + "  aj数量：" + at.size());
                Log.e("eeee", "av大小：" + getSize(av) + "  av数量：" + av.size());
                Log.e("eeee", "ax大小：" + getSize(ax) + "  ax数量：" + ax.size());
                Log.e("eeee", "az大小：" + getSize(az) + "  ap数量：" + az.size());  //语音

                Log.e("eeee", "ah大小：" + getSize(ah) + "  ah数量：" + ah.size());   //临时缓存(浏览朋友圈产生的缓存垃圾)
                Log.e("eeee", "ag大小：" + getSize(ag) + "  ah数量：" + ag.size());  //垃圾文件(不含聊天信息，不影响QQ使用)
                Log.e("eeee", "aB大小：" + getSize(aB) + "  aB数量：" + aB.size());  //文件
            } else {
                PrefsCleanUtil.getInstance().putLong(Constants.CLEAN_QQ_TOTAL_SIZE, 0);
            }
        }
        if (this.n <= 0 && !"finishActivity".equals(this.bu) && !"bigGarbageFragment".equals(this.bu)) {
            this.i.sendEmptyMessageDelayed(4, 500);
        }

    }

    boolean v;
    boolean A;
    boolean F;
    boolean K;
    List<String> bb;
    public String bu = "d";
    public boolean aP = false;
    public boolean aQ = false;
    public boolean aR = false;
    public boolean aS = false;
    public boolean aT = false;
    public boolean aU = false;
    public boolean aV = false;
    public boolean aW = false;
    public boolean aX = false;
    public boolean aY = false;
    public boolean aZ = false;
    boolean j = true;

    public void j() {
        ThreadTaskUtil.executeNormalTask("-CleanQqClearActivity-createScanPath-1419--", new Runnable() {
            public void run() {
                q = PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_QQ_GARBAGE_FILES_CHECKED, true);
                v = PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_QQ_TEMP_CACHE_CHECKED, true);
                A = PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_QQ_SHORT_VIDEO_CHECKED, true);
                F = PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_QQ_HEAD_CACHE_CHECKED, true);
                K = PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_QQ_SPACE_CACHE_CHECKED, true);
                File file = new File(Environment.getExternalStorageDirectory() + "/Tencent/MobileQQ");
                if (file.exists()) {
                    if (bb == null) {
                        bb = new ArrayList();
                    } else {
                        bb.clear();
                    }
                    List scanFilePathDb = QQUtil.scanFilePathDb();
                    if ("finishActivity".equals(bu) || "bigGarbageFragment".equals(bu)) {
                        aP = true;
                        aT = true;
                        aS = true;
                        aQ = true;
                        int i = 0;
                        while (i < scanFilePathDb.size()) {
                            if (((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getType() == 101 || ((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getType() == 102 || ((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getType() == 103 || ((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getType() == 104 || ((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getType() == 112) {
                                scanFilePathDb.remove(i);
                                i--;
                            }
                            i++;
                        }
                    }
                    Pattern compile = Pattern.compile("[0-9]*");
                    File[] listFiles = file.listFiles();
                    if (listFiles != null) {
                        for (int i2 = 0; i2 < listFiles.length; i2++) {
                            if (!TextUtils.isEmpty(listFiles[i2].getName()) && listFiles[i2].getName().length() >= 5 && listFiles[i2].getName().length() <= 16 && compile.matcher(listFiles[i2].getName()).matches()) {
                                bb.add(listFiles[i2].getName());
                            }
                        }
                        int i3 = 0;
                        while (i3 < scanFilePathDb.size()) {
                            if (((WxAndQqScanPathInfo) scanFilePathDb.get(i3)).getFilePath().contains("ssssss") && bb != null && bb.size() > 0) {
                                for (String replace : bb) {
                                    scanFilePathDb.add(new WxAndQqScanPathInfo(((WxAndQqScanPathInfo) scanFilePathDb.get(i3)).getType(), ((WxAndQqScanPathInfo) scanFilePathDb.get(i3)).getFilePath().replace("ssssss", replace)));
                                }
                                scanFilePathDb.remove(i3);
                                i3--;
                            }
                            i3++;
                        }
                        if (scanFilePathDb.size() > 0) {
                            while (scanFilePathDb.size() > 0) {
                                ArrayList arrayList = new ArrayList();
                                arrayList.add(scanFilePathDb.get(0));
                                scanFilePathDb.remove(0);
                                if (scanFilePathDb.size() > 0) {
                                    int i4 = 0;
                                    while (i4 < scanFilePathDb.size()) {
                                        if (((WxAndQqScanPathInfo) scanFilePathDb.get(i4)).getType() == ((WxAndQqScanPathInfo) arrayList.get(0)).getType()) {
                                            arrayList.add(scanFilePathDb.get(i4));
                                            scanFilePathDb.remove(i4);
                                            i4--;
                                        }
                                        i4++;
                                    }
                                }
                                if (arrayList.size() > 0) {
                                    if (j) {
                                        a((List<WxAndQqScanPathInfo>) arrayList);
                                    } else {
                                        return;
                                    }
                                }
                            }
                            return;
                        }
                    }
                }
                Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---run --无数据-- ");
                i.sendEmptyMessage(2);
            }
        });
    }

    /* access modifiers changed from: private */
    public void i() {
        ThreadTaskUtil.executeNormalTask("-CleanQqClearActivity-getQqCacheInSystem-962--", new Runnable() {
            public void run() {
                Long oneAppCache = new QueryFileUtil().getOneAppCache(QQCleanHomeActivity.this, "com.tencent.mobileqq", -1);
                Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---getQqCacheInSystem ---- " + oneAppCache);
                Message obtainMessage = i.obtainMessage();
                obtainMessage.obj = oneAppCache;
                obtainMessage.what = 5;
                i.sendMessage(obtainMessage);
            }
        });
    }

    a i;
    boolean q;

    public static class a extends Handler {
        WeakReference<QQCleanHomeActivity> a;

        private a(QQCleanHomeActivity cleanQqClearActivity) {
            this.a = new WeakReference<>(cleanQqClearActivity);
        }

        public void handleMessage(Message message) {
            if (this.a != null && this.a.get() != null) {
                ((QQCleanHomeActivity) this.a.get()).a(message);
            }
        }
    }

    public void a(Message message) {
        switch (message.what) {
            case 1:
                d();
                return;
            case 2:
                c();
                return;
            case 3:
//                b();
                return;
            case 4:
//                if (!PermissionSDK23Utils.isGrantedStoragePermission() || !NetworkUtil.hasNetWork()) {
//                    this.bh.setText("完成");
//                    return;
//                } else {
//                    a();
//                    return;
//                }
            case 5:
//                a(message.obj);
                return;
            case 10:
                b(message);
                return;
            case 11:
                c(message);
                return;
            default:
                return;
        }
    }

    public long getSize(List<CleanWxClearInfo> aaa) {
        long tempSize = 0;
        for (CleanWxClearInfo cleanWxClearInfo : aaa) {
            tempSize += cleanWxClearInfo.getSize();
        }
        return tempSize;
    }

    public boolean e;
    public boolean f;
    List<CleanWxClearInfo> ai = new ArrayList();
    List<CleanWxClearInfo> aj = new ArrayList();
    List<CleanWxClearInfo> ak = new ArrayList();
    List<CleanWxClearInfo> al = new ArrayList();
    List<CleanWxClearInfo> am = new ArrayList();
    List<CleanWxClearInfo> an = new ArrayList();
    List<CleanWxClearInfo> ao = new ArrayList();
    List<CleanWxClearInfo> ap = new ArrayList();
    List<CleanWxClearInfo> aq = new ArrayList();
    List<CleanWxClearInfo> ar = new ArrayList();
    List<CleanWxClearInfo> as = new ArrayList();
    List<CleanWxClearInfo> at = new ArrayList();
    List<CleanWxClearInfo> au = new ArrayList();
    List<CleanWxClearInfo> av = new ArrayList();
    List<CleanWxClearInfo> aw = new ArrayList();
    List<CleanWxClearInfo> ax = new ArrayList();
    List<CleanWxClearInfo> ay = new ArrayList();
    List<CleanWxClearInfo> az = new ArrayList();

    List<CleanWxClearInfo> ag = new ArrayList();
    List<CleanWxClearInfo> ah = new ArrayList();
    List<CleanWxClearInfo> aB = new ArrayList();
    List<CleanWxClearInfo> aC = new ArrayList();


    private void d() {
        this.f = true;
        if (this.f && this.e && this.n > 0) {
//            PrefsCleanUtil.getInstance().putLong(Constants.CLEAN_QQ_TOTAL_SIZE, this.n + this.bT + this.T + this.Q + this.ac + this.Z + this.af);
        }
    }

    public void a(final List<WxAndQqScanPathInfo> list) {
        ThreadTaskUtil.executeNormalTask("-CleanQqClearActivity-startScanAllOneType-1515--", new Runnable() {
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    File file = new File(Environment.getExternalStorageDirectory() + ((WxAndQqScanPathInfo) list.get(i)).getFilePath());
                    if (file.exists()) {
                        a(file, ((WxAndQqScanPathInfo) list.get(i)).getType(), 10);
                    }
                }
                Message obtainMessage = i.obtainMessage();
                obtainMessage.what = 11;
                obtainMessage.obj = Integer.valueOf(((WxAndQqScanPathInfo) list.get(0)).getType());
                i.sendMessage(obtainMessage);
            }
        });
    }

    public boolean ba = false;

    private void c(Message message) {
        if (message != null) {
            int intValue = ((Integer) message.obj).intValue();
            switch (intValue) {
                case 101:
                    this.aP = true;
                    Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---doOneTypeFinihed --QQ_GARBAGE_CACHE_FINISH-- ");
                    if (this.aP && this.aT && this.aS && this.aQ) {
                        Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---doOneTypeFinihed --QQ_GARBAGE_CACHE_FINISH--1== ");
                        this.i.sendEmptyMessage(2);
                        break;
                    }
                case 102:
                    this.aQ = true;
                    Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---doOneTypeFinihed --QQ_TEMP_CACHE_FINISH-- ");
                    if (this.aP && this.aT && this.aS && this.aQ) {
                        Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---doOneTypeFinihed --QQ_TEMP_CACHE_FINISH--4== ");
                        this.i.sendEmptyMessage(2);
                        break;
                    }
                case 103:
                    this.aS = true;
                    b(this.al);
                    Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---doOneTypeFinihed --QQ_HEAD_CACHE_FINISH-- ");
                    if (this.aP && this.aT && this.aS && this.aQ) {
                        Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---doOneTypeFinihed --QQ_HEAD_CACHE_FINISH--3== ");
                        this.i.sendEmptyMessage(2);
                        break;
                    }
                case 104:
                    this.aT = true;
                    b(this.an);
                    b(this.aj);
                    Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---doOneTypeFinihed --QQ_SPACE_CACHE_FINISH-- ");
                    if (this.aP && this.aT && this.aS && this.aQ) {
                        Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---doOneTypeFinihed --QQ_SPACE_CACHE_FINISH--2== ");
                        this.i.sendEmptyMessage(2);
                        break;
                    }
                case 105:
                    this.aV = true;
                    b(this.ar);
                    Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---doOneTypeFinihed --QQ_CHAT_PIC_FINISH-- ");
                    if (this.aU && this.aX && this.aY && this.ba && this.aZ && this.aV && this.aW) {
                        this.i.sendEmptyMessage(1);
                        break;
                    }
                case 106:
                    this.aW = true;
                    b(this.at);
                    Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---doOneTypeFinihed --QQ_CAMERA_SAVE-- ");
                    if (this.aU && this.aX && this.aY && this.ba && this.aZ && this.aV && this.aW) {
                        this.i.sendEmptyMessage(1);
                        break;
                    }
                case 107:
                    this.aU = true;
                    b(this.ap);
                    Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---doOneTypeFinihed --QQ_VIDEO_FINISH-- ");
                    if (this.aU && this.aX && this.aY && this.ba && this.aZ && this.aV && this.aW) {
                        this.i.sendEmptyMessage(1);
                        break;
                    }
                case 108:
                    this.ba = true;
                    b(this.aB);
                    Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---doOneTypeFinihed --QQ_DOWNLOAD_FILE_FINISH-- ");
                    if (this.aU && this.aX && this.aY && this.ba && this.aZ && this.aV && this.aW) {
                        this.i.sendEmptyMessage(1);
                        break;
                    }
                case 109:
                    this.aX = true;
                    b(this.av);
                    Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---doOneTypeFinihed --QQ_EMOJI_SELF_FINISH-- ");
                    if (this.aU && this.aX && this.aY && this.ba && this.aZ && this.aV && this.aW) {
                        this.i.sendEmptyMessage(1);
                        break;
                    }
                case 110:
                    this.aY = true;
                    b(this.ax);
                    Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---doOneTypeFinihed --QQ_EMOJI_DOWNLOAD_FINISH-- ");
                    if (this.aU && this.aX && this.aY && this.ba && this.aZ && this.aV && this.aW) {
                        this.i.sendEmptyMessage(1);
                        break;
                    }
                case 111:
                    this.aZ = true;
                    b(this.az);
                    Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqClearActivity---doOneTypeFinihed --QQ_CHAT_TALK_FINISH-- ");
                    if (this.aU && this.aX && this.aY && this.ba && this.aZ && this.aV && this.aW) {
                        this.i.sendEmptyMessage(1);
                        break;
                    }


            }

        }
    }

    long bc = Long.MAX_VALUE;

    private void b(List<CleanWxClearInfo> list) {
        try {
            Collections.sort(list, new Comparator<CleanWxClearInfo>() {
                public int compare(CleanWxClearInfo cleanWxClearInfo, CleanWxClearInfo cleanWxClearInfo2) {
                    long time = cleanWxClearInfo.getTime();
                    long time2 = cleanWxClearInfo2.getTime();
                    if (cleanWxClearInfo2.isChecked()) {
                        time2 = bc;
                        bc--;
                    }
                    if (cleanWxClearInfo.isChecked()) {
                        time = bc;
                        bc--;
                    }
                    if (time < time2) {
                        return 1;
                    }
                    if (time == time2) {
                        return 0;
                    }
                    return -1;
                }
            });
        } catch (Exception e2) {
            Logger.i(Logger.TAG, Logger.ZYTAG, "sortList---sort  " + e2.toString());
        }
    }


    public void a(File file, int i2, int i3) {
        if (!this.j || file == null) {
            return;
        }
        if (!file.getAbsolutePath().contains("/MobileQQ/shortvideo/thumbs") || i3 == 102) {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                int length = listFiles.length;
                int i4 = 0;
                while (i4 < length) {
                    File file2 = listFiles[i4];
                    if (this.j) {
                        if (file2 != null) {
                            if (file2.isDirectory()) {
                                try {
                                    if (file2.listFiles() == null || file2.listFiles().length == 0) {
                                        file2.delete();
                                    }
                                } catch (Exception e2) {
                                    Logger.iCatch(Logger.TAG, Logger.ZYTAG, "CleanQqClearNewActivity---qqFileScan ---- ", e2);
                                }
                                a(file2, i2, i3);
                            } else if (!".nomedia".equals(file2.getName()) && file2.length() >= 5) {
                                CleanWxClearInfo cleanWxClearInfo = new CleanWxClearInfo();
                                cleanWxClearInfo.setFilePath(file2.getAbsolutePath());
                                cleanWxClearInfo.setSize(file2.length());
                                cleanWxClearInfo.setDate(new Date(file2.lastModified()));
                                cleanWxClearInfo.setTime(file2.lastModified());
                                cleanWxClearInfo.setFileName(file2.getName());
                                cleanWxClearInfo.setType(i2);
                                if (i2 == 104 && file2.getAbsolutePath().contains("/Android/data/com.tencent.mobileqq/cache/tencent_sdk_download")) {
                                    if (file2.getAbsolutePath().contains("mp4")) {
                                        cleanWxClearInfo.setType(112);
                                    } else if (file2.getAbsolutePath().endsWith("filedesc")) {
                                        cleanWxClearInfo.setType(101);
                                    }
                                }
                                Message obtainMessage = this.i.obtainMessage();
                                obtainMessage.what = i3;
                                obtainMessage.obj = cleanWxClearInfo;
                                this.i.sendMessage(obtainMessage);
                            }
                        }
                        i4++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    long n = 0;
    long p = 0;
    long o = 0;

    public long u;
    public long t;
    public long C;
    public long D;
    long E = 0;

    public long H;
    public long I;
    public long T;
    public long Q;
    long J = 0;
    long af = 0;
    long W = 0;
    long Z = 0;

    private long bT = 0;
    long ac = 0;
    long s = 0;

    private void b(Message message) {
        if (message != null && this.j) {
            CleanWxClearInfo cleanWxClearInfo = (CleanWxClearInfo) message.obj;
            if (cleanWxClearInfo != null) {
                switch (cleanWxClearInfo.getType()) {
                    case 101:
                        this.n += cleanWxClearInfo.getSize();
                        this.i.sendEmptyMessage(3);
                        if (this.q) {
                            cleanWxClearInfo.setChecked(true);
                            this.p += cleanWxClearInfo.getSize();
                        }
                        this.o += cleanWxClearInfo.getSize();
                        this.ag.add(cleanWxClearInfo);
                        break;
                    case 102:
                        this.n += cleanWxClearInfo.getSize();
                        this.i.sendEmptyMessage(3);
                        if (this.v) {
                            cleanWxClearInfo.setChecked(true);
                            this.u++;
                            this.t += cleanWxClearInfo.getSize();
                        }
                        this.s += cleanWxClearInfo.getSize();
                        this.ah.add(cleanWxClearInfo);
                        break;
                    case 103:
                        cleanWxClearInfo.setType(103);
                        this.n += cleanWxClearInfo.getSize();
                        this.i.sendEmptyMessage(3);
                        if (this.F) {
                            cleanWxClearInfo.setChecked(true);
                            this.D++;
                            this.C += cleanWxClearInfo.getSize();
                        }
                        this.E += cleanWxClearInfo.getSize();
                        this.al.add(cleanWxClearInfo);
                        break;
                    case 104:
                        this.n += cleanWxClearInfo.getSize();
                        this.i.sendEmptyMessage(3);
                        if (this.K) {
                            cleanWxClearInfo.setChecked(true);
                            this.I++;
                            this.H += cleanWxClearInfo.getSize();
                        }
                        this.J += cleanWxClearInfo.getSize();
                        this.an.add(cleanWxClearInfo);
                        break;
                    case 105:
                        cleanWxClearInfo.setType(105);
                        this.Q += cleanWxClearInfo.getSize();
                        this.ar.add(cleanWxClearInfo);
                        break;
                    case 106:
                        this.T += cleanWxClearInfo.getSize();
                        this.at.add(cleanWxClearInfo);
                        break;
                    case 107:
                        this.bT += cleanWxClearInfo.getSize();
                        this.ap.add(cleanWxClearInfo);
                        break;
                    case 108:
                        this.af += cleanWxClearInfo.getSize();
                        this.aB.add(cleanWxClearInfo);
                        break;
                    case 109:
                        this.W += cleanWxClearInfo.getSize();
                        this.av.add(cleanWxClearInfo);
                        break;
                    case 110:
                        this.Z += cleanWxClearInfo.getSize();
                        this.ax.add(cleanWxClearInfo);
                        break;
                    case 111:
                        this.ac += cleanWxClearInfo.getSize();
                        this.az.add(cleanWxClearInfo);
                        break;
                    case 112:
//                        this.n += cleanWxClearInfo.getSize();
//                        this.i.sendEmptyMessage(3);
//                        if (this.A) {
//                            cleanWxClearInfo.setChecked(true);
//                            this.y++;
//                            this.x += cleanWxClearInfo.getSize();
//                        }
//                        this.z += cleanWxClearInfo.getSize();
//                        this.aj.add(cleanWxClearInfo);
                        break;
                }
//                if (this.g != null && this.g.isAdded()) {
//                    this.g.refreshChildFragment(cleanWxClearInfo.getType(), false);
//                }
//                if (this.h != null && this.h.isAdded()) {
//                    this.h.refreshChildFragment(cleanWxClearInfo.getType(), false);
//                }
            }
        }
    }


}
