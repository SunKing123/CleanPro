package com.xiaoniu.cleanking.ui.tool.qq.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.FileChildEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileDeleteEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.ui.tool.qq.activity.QQCleanHomeActivity;
import com.xiaoniu.cleanking.ui.tool.qq.bean.CleanWxClearInfo;
import com.xiaoniu.cleanking.ui.tool.qq.util.QQUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.FileSizeUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.VISIBLE;

/**
 * Created by z on 2017/5/15.
 */
public class QQCleanHomePresenter extends RxPresenter<QQCleanHomeActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;


    @Inject
    public QQCleanHomePresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }


    //扫描中动画
    public ObjectAnimator setScaningAnim(View viewY) {
        PropertyValuesHolder translationX = PropertyValuesHolder.ofFloat("translationX", -1 * DeviceUtils.dip2px(99), DeviceUtils.getScreenWidth());
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(viewY, translationX);
        animator.setDuration(600);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                viewY.setVisibility(VISIBLE);
            }
        });
        animator.start();
        return animator;
    }

    //数字动画先播
    public ValueAnimator setTextAnim(TextView tvGab, float startNum, float endNum) {
        ValueAnimator anim = ValueAnimator.ofFloat(startNum, endNum);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            tvGab.setText(NumberUtils.getFloatStr1(currentValue) + "");
        });
        anim.start();
        return anim;
    }

    //字体变小动画
    public void setTextSizeAnim(TextView tvGab, int startSize, int endSize) {
        ValueAnimator anim = ValueAnimator.ofFloat(startSize, endSize);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            tvGab.setTextSize(currentValue);
        });
        anim.start();
    }

    //高度变化动画
    public void setViewHeightAnim(View tvGab, RelativeLayout relSelects, View view3, View view4, int startSize, int endSize) {
        ValueAnimator anim = ValueAnimator.ofInt(startSize, endSize);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) tvGab.getLayoutParams();
        anim.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            llp.height = currentValue;
            tvGab.setLayoutParams(llp);
        });
        anim.start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                relSelects.setVisibility(VISIBLE);
                view3.setVisibility(VISIBLE);
                view4.setVisibility(VISIBLE);
            }
        });
    }


    //清理缓存垃圾
    public void onekeyCleanDelete(List<CleanWxClearInfo> cacheList, boolean isTopSelect, ArrayList<FileTitleEntity> mListImg, ArrayList<FileTitleEntity> mListVideo) {
        List<CleanWxClearInfo> listTemp = new ArrayList<>();
        List<CleanWxClearInfo> selectAudList = getSelectAudioList();
        List<CleanWxClearInfo> selectFileList = getSelectFileList();
        if (selectFileList != null)
            listTemp.addAll(selectFileList);
        if (selectAudList != null)
            listTemp.addAll(selectAudList);
        if (isTopSelect)
            listTemp.addAll(cacheList);
        Log.e("ss", "" + listTemp.size());

        List<FileDeleteEntity> listFileDelete = new ArrayList<>();
        for (int i = 0; i < listTemp.size(); i++) {
            FileDeleteEntity fileDeleteEntity = new FileDeleteEntity();
            fileDeleteEntity.setPath(listTemp.get(i).getFilePath());
            fileDeleteEntity.setSize(listTemp.get(i).getSize());
            listFileDelete.add(fileDeleteEntity);
        }

        for (int i = 0; i < getSelectImgOrVideoList(mListImg).size(); i++) {
            FileDeleteEntity fileDeleteEntity = new FileDeleteEntity();
            fileDeleteEntity.setPath(getSelectImgOrVideoList(mListImg).get(i).path);
            fileDeleteEntity.setSize(getSelectImgOrVideoList(mListImg).get(i).size);
            listFileDelete.add(fileDeleteEntity);
        }
        for (int i = 0; i < getSelectImgOrVideoList(mListVideo).size(); i++) {
            FileDeleteEntity fileDeleteEntity = new FileDeleteEntity();
            fileDeleteEntity.setPath(getSelectImgOrVideoList(mListVideo).get(i).path);
            fileDeleteEntity.setSize(getSelectImgOrVideoList(mListVideo).get(i).size);
            listFileDelete.add(fileDeleteEntity);
        }
        delFile(listFileDelete);
    }

    public void delFile(List<FileDeleteEntity> list) {
        List<FileDeleteEntity> files = list;
        Observable.create((ObservableOnSubscribe<Long>) emitter -> {

            for (FileDeleteEntity appInfoBean : files) {
                File file = new File(appInfoBean.getPath());
                Log.e("删除路劲:", "" + file.getAbsolutePath());
                if (null != file) {
                    file.delete();
                }
            }
            long sizes = 0;
            for (FileDeleteEntity cleanWxItemInfo : files)
                sizes += cleanWxItemInfo.getSize();
            emitter.onNext(sizes);
            emitter.onComplete();
        })
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io())//执行在io线程
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Long value) {
                        mView.deleteResult(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

    //获取选中的语音大小
    public long getSelectAudioSize() {
        long selectAudSize = 0;
        List<CleanWxClearInfo> listTemp = new ArrayList<>();
        if (QQUtil.audioList == null) return 0;
        for (int i = 0; i < QQUtil.audioList.size(); i++) {
            if (QQUtil.audioList.get(i).getIsSelect())
                listTemp.add(QQUtil.audioList.get(i));
        }
        for (int i = 0; i < listTemp.size(); i++) {
            selectAudSize += listTemp.get(i).getSize();
        }
        return selectAudSize;
    }

    //获取选中的语音列表
    public List<CleanWxClearInfo> getSelectAudioList() {
        List<CleanWxClearInfo> listTemp = new ArrayList<>();
        if (QQUtil.audioList == null) return null;
        for (int i = 0; i < QQUtil.audioList.size(); i++) {
            if (QQUtil.audioList.get(i).getIsSelect())
                listTemp.add(QQUtil.audioList.get(i));
        }
        return listTemp;
    }

    //选中的图片或者视频大小
    public long getSelectImgOrVideoSize(ArrayList<FileTitleEntity> listImg) {
        long selectAudSize = 0;
        if (listImg.size() == 0) return 0;
        for (int i = 0; i < listImg.size(); i++) {
            for (int k = 0; k < listImg.get(i).lists.size(); k++) {
                if (listImg.get(i).lists.get(k).isSelect)
                    selectAudSize += listImg.get(i).lists.get(k).size;
            }
        }
        return selectAudSize;
    }

    public List<FileChildEntity> getSelectImgOrVideoList(ArrayList<FileTitleEntity> listImg) {
        List<FileChildEntity> selectImgList = new ArrayList<>();
        for (int i = 0; i < listImg.size(); i++) {
            for (int k = 0; k < listImg.get(i).lists.size(); k++) {
                if (listImg.get(i).lists.get(k).isSelect)
                    selectImgList.add(listImg.get(i).lists.get(k));
            }
        }
        return selectImgList;
    }

    //获取选中的文件大小
    public long getSelectFileSize() {
        long selectAudSize = 0;
        List<CleanWxClearInfo> listTemp = new ArrayList<>();
        if (QQUtil.fileList == null) return 0;
        for (int i = 0; i < QQUtil.fileList.size(); i++) {
            if (QQUtil.fileList.get(i).getIsSelect())
                listTemp.add(QQUtil.fileList.get(i));
        }
        for (int i = 0; i < listTemp.size(); i++) {
            selectAudSize += listTemp.get(i).getSize();
        }
        return selectAudSize;
    }

    //获取选中的文件列表
    public List<CleanWxClearInfo> getSelectFileList() {
        List<CleanWxClearInfo> listTemp = new ArrayList<>();
        if (QQUtil.fileList == null) return null;
        for (int i = 0; i < QQUtil.fileList.size(); i++) {
            if (QQUtil.fileList.get(i).getIsSelect())
                listTemp.add(QQUtil.fileList.get(i));
        }
        return listTemp;
    }


    private long mQQImgFileSize = 0;
    private long mQQVideoFileSize = 0L;


    /**
     * 获取QQ聊天图片
     */
    public void getImgQQ() {
        mQQImgFileSize = 0L;
        String wxRootPath = Environment.getExternalStorageDirectory().getPath() + "/tencent/MobileQQ";
        String pathLocal = wxRootPath + "/diskcache";
        String pathLocal2 = wxRootPath + "/photo";
        String pathLocal3 = wxRootPath + "/thumb";
        Observable.create((ObservableOnSubscribe<String>) emitter -> {

            scanAllImgCamera(pathLocal);
            scanAllImgCamera(pathLocal2);
            scanAllImgCamera(pathLocal3);
            emitter.onNext("");
            emitter.onComplete();
        })
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io())//执行在io线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String value) {
                        mView.updateQQImgSize(FileSizeUtils.formatFileSize(mQQImgFileSize), mQQImgFileSize);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        mView.cancelLoadingDialog();
                    }
                });

    }

    /**
     * 获取相机视频
     */
    public void getVideoFiles() {
        mQQVideoFileSize = 0L;
        String wxRootPath = Environment.getExternalStorageDirectory().getPath() + "/tencent/MobileQQ";
        String pathLocal = wxRootPath + "/shortvideo";
        Observable.create((ObservableOnSubscribe<String>) emitter -> {

            scanAllVideoCamera(pathLocal);
            emitter.onNext("");
            emitter.onComplete();
        })
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io())//执行在io线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String value) {
                        mView.updateVideoSize(FileSizeUtils.formatFileSize(mQQVideoFileSize), mQQVideoFileSize);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        mView.cancelLoadingDialog();
                    }
                });

    }

    /**
     * 扫描聊天中的视频
     *
     * @param path
     */
    private void scanAllVideoCamera(String path) {
        File fileRoot = new File(path);
        if (fileRoot.isDirectory()) {
            File[] files = fileRoot.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        scanAllVideoCamera(path + "/" + file.getName());
                    } else if (file.getName().endsWith(".mp4")) {
                        FileChildEntity fileChildEntity = new FileChildEntity();
                        fileChildEntity.name = file.getName();
                        fileChildEntity.path = file.getPath();
                        fileChildEntity.size = file.length();
                        fileChildEntity.fileType = 1;
                        mQQVideoFileSize += file.length();

                    }
                }
            }
        }
    }

    /**
     * 扫描聊天中的图片，包括缩略图
     *
     * @param path
     */
    private void scanAllImgCamera(String path) {
        File fileRoot = new File(path);
        if (fileRoot.isDirectory()) {
            File[] files = fileRoot.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        scanAllImgCamera(path + "/" + file.getName());
                    } else {
                        FileChildEntity fileChildEntity = new FileChildEntity();
                        fileChildEntity.name = file.getName();
                        fileChildEntity.path = file.getPath();
                        fileChildEntity.size = file.length();
                        mQQImgFileSize += file.length();

                    }
                }
            }
        }
    }

    public ObjectAnimator playRoundAnim(ImageView iconOuter) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360);
        rotation.setDuration(500);
        rotation.setRepeatCount(ValueAnimator.INFINITE);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.start();
        return rotation;
    }

}
