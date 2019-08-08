package com.xiaoniu.cleanking.ui.tool.wechat.presenter;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.MusciInfoBean;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxEasyInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxFourItemInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxHeadInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxItemInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.Constants;
import com.xiaoniu.cleanking.ui.tool.wechat.util.PrefsCleanUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.util.QueryFileUtil;
import com.xiaoniu.cleanking.ui.tool.wechat.util.WxQqUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.ThreadTaskUtil;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.VISIBLE;

/**
 * Created by z on 2017/5/15.
 */
public class WechatCleanHomePresenter extends RxPresenter<WechatCleanHomeActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;


    @Inject
    public WechatCleanHomePresenter(RxAppCompatActivity activity) {
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

    WxQqUtil f;
    public String c;
    public long e = 0;

    //扫描微信垃圾、图片】音视频等
    public void scanWxGabage() {
        PrefsCleanUtil.getInstance().init(mView, "xnpre", Context.MODE_APPEND);

        Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(ObservableEmitter<Long> emitter) throws Exception {
                f = new WxQqUtil();
                f.startScanWxGarbage(c, new WxQqUtil.a() {
                    @Override
                    public void changeHomeNum() {
                        e = WxQqUtil.d.getTotalSize() + WxQqUtil.g.getTotalSize() + WxQqUtil.f.getTotalSize() + WxQqUtil.e.getTotalSize();
                    }

                    @Override
                    public void wxEasyScanFinish() {
                        emitter.onNext(10L);
                        emitter.onComplete();
                    }
                });
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

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        mView.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mView.getScanResult();
                            }
                        });
                    }
                });



        ThreadTaskUtil.executeNormalTask("-CleanWxClearNewActivity-run-184--", new Runnable() {
            public void run() {
                SystemClock.sleep(200);
                b();
            }
        });


    }


    public void b() {
        Long oneAppCache = new QueryFileUtil().getOneAppCache(mView, "com.tencent.mm", -1);
        Log.e("asd", "" + oneAppCache);

    }

    //根据提供的bean筛选list
    public List<CleanWxItemInfo> getAllSelectList(CleanWxEasyInfo cleanWxEasyInfoFile) {
        List<CleanWxFourItemInfo> listFour = new ArrayList<>();
        List<CleanWxItemInfo> listDataTemp = new ArrayList<>();
        for (int i = 0; i < cleanWxEasyInfoFile.getList().size(); i++) {
            if (cleanWxEasyInfoFile.getList().get(i) instanceof CleanWxFourItemInfo) {
                CleanWxFourItemInfo cleanWxHeadInfo = (CleanWxFourItemInfo) cleanWxEasyInfoFile.getList().get(i);
                listFour.add(cleanWxHeadInfo);
            }
        }

        for (int j = 0; j < listFour.size(); j++) {
            listDataTemp.addAll(listFour.get(j).getFourItem());
        }
        return listDataTemp;
    }

    //清理缓存垃圾
    public void onekeyCleanDelete(boolean isTopSelect, boolean isBottomSelect) {
        CleanWxEasyInfo headCacheInfo = WxQqUtil.e;  //缓存表情   浏览聊天记录产生的表情
        CleanWxEasyInfo gabageFileInfo = WxQqUtil.d;  //垃圾文件   不含聊天记录建议清理
        CleanWxEasyInfo wxCircleInfo = WxQqUtil.g;  //朋友圈缓存

        CleanWxEasyInfo wxprogramInfo = WxQqUtil.f;  //微信小程序

        List<CleanWxItemInfo> listTemp = new ArrayList<>();
        if (isTopSelect) {
            listTemp.addAll(getAllSelectList(headCacheInfo));
            listTemp.addAll(getAllSelectList(gabageFileInfo));
            listTemp.addAll(getAllSelectList(wxCircleInfo));
        }
        if (isBottomSelect) {
            listTemp.addAll(getAllSelectList(wxprogramInfo));
        }
        //测试代码**************************************
//        List<CleanWxItemInfo> listTempTest = new ArrayList<>();
//        long tempSize = 0;
//        for (int i = 0; i < listTemp.size(); i++) {
//            if (i < 10){
//                tempSize += listTemp.get(i).getFileSize();
//                listTempTest.add(listTemp.get(i));
//            }
//        }
//        String sst = CleanAllFileScanUtil.byte2FitSizeOne(tempSize);
        Log.e("fddf", "删除大小：" + listTemp);
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


}
