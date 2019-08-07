package com.xiaoniu.cleanking.ui.tool.qq.presenter;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.ui.tool.qq.activity.QQCleanHomeActivity;
import com.xiaoniu.cleanking.ui.tool.qq.bean.CleanWxClearInfo;
import com.xiaoniu.cleanking.ui.tool.qq.util.QQUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxEasyInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxItemInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.util.WxQqUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
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
//        animator.setRepeatMode(ValueAnimator.INFINITE);
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
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                tvGab.setText(NumberUtils.getFloatStr1(currentValue) + "");
            }
        });
        anim.start();
        return anim;
    }

    //字体变小动画
    public void setTextSizeAnim(TextView tvGab, int startSize, int endSize) {
        ValueAnimator anim = ValueAnimator.ofFloat(startSize, endSize);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                tvGab.setTextSize(currentValue);
            }
        });
        anim.start();
    }

    //高度变化动画
    public void setViewHeightAnim(View tvGab, RelativeLayout relSelects, View view3, View view4, int startSize, int endSize) {
        ValueAnimator anim = ValueAnimator.ofInt(startSize, endSize);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) tvGab.getLayoutParams();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();
                llp.height = currentValue;
                tvGab.setLayoutParams(llp);
            }
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
    public void onekeyCleanDelete(boolean isTopSelect, boolean isBottomSelect) {
        CleanWxEasyInfo headCacheInfo = WxQqUtil.e;  //缓存表情   浏览聊天记录产生的表情
        CleanWxEasyInfo gabageFileInfo = WxQqUtil.d;  //垃圾文件   不含聊天记录建议清理
        CleanWxEasyInfo wxCircleInfo = WxQqUtil.g;  //朋友圈缓存

        CleanWxEasyInfo wxprogramInfo = WxQqUtil.f;  //微信小程序

        List<CleanWxItemInfo> listTemp = new ArrayList<>();
        if (isTopSelect) {
            listTemp.addAll(headCacheInfo.getTempList());
            listTemp.addAll(gabageFileInfo.getTempList());
            listTemp.addAll(wxCircleInfo.getTempList());
        }
        if (isBottomSelect) {
            listTemp.addAll(wxprogramInfo.getTempList());
//            listTemp.add(wxprogramInfo.getTempList().get(0));
//            listTemp.add(wxprogramInfo.getTempList().get(1));
//            listTemp.add(wxprogramInfo.getTempList().get(2));
//            listTemp.add(wxprogramInfo.getTempList().get(3));
        }
        delFile(listTemp);
    }

    public void delFile(List<CleanWxItemInfo> list) {
        List<CleanWxItemInfo> files = list;
        Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(ObservableEmitter<Long> emitter) throws Exception {

                for (CleanWxItemInfo appInfoBean : files) {
                    File file = appInfoBean.getFile();
                    Log.e("删除路劲:", "" + file.getAbsolutePath());
                    if (null != file) {
                        file.delete();
                    }
                }
                long sizes = 0;
                for (CleanWxItemInfo cleanWxItemInfo : files)
                    sizes += cleanWxItemInfo.getFileSize();
                emitter.onNext(sizes);
                emitter.onComplete();
            }
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

}
