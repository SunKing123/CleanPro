package com.installment.mall.utils;

import android.app.Activity;
import android.widget.ImageView;

/**
 * Created by Admin on 2017/6/20.
 */

public class SceneAnimation {
    //传入的关联动画的imageView对象(这里不一定是ImageView也可以是其他)
    private ImageView mImageView;

    //图片数组对象
    private int[] mFrameRess;

    //每张图片的动画时长(其长度与图片数组对应,用于设置不同时间间隔)
    private int[] mDurations;

    //每张图片相同的时长
    private int mDuration;

    //播放至最后一张图片的下标
    private int mLastFrameNo;

    private long mBreakDelay;

    private int mCode;

    public SceneAnimation(Activity activity, ImageView pImageView, int[] pFrameRess, int[] pDurations)
    {
        mImageView = pImageView;
        mFrameRess = pFrameRess;
        mDurations = pDurations;
        mLastFrameNo = pFrameRess.length - 1;

        mImageView.setBackgroundResource(mFrameRess[0]);
        play(1);
    }

    public SceneAnimation(ImageView pImageView, int[] pFrameRess, int pDuration)
    {
        mImageView = pImageView;
        mFrameRess = pFrameRess;
        mDuration = pDuration;
        mLastFrameNo = pFrameRess.length - 1;

        mImageView.setBackgroundResource(mFrameRess[0]);
        playConstant(1);
    }

    public SceneAnimation(ImageView pImageView, int[] pFrameRess, int pDuration, int code)
    {
        mImageView = pImageView;
        mFrameRess = pFrameRess;
        mDuration = pDuration;
        mLastFrameNo = pFrameRess.length - 1;
        mCode = code;
        mImageView.setBackgroundResource(mFrameRess[0]);
        playConstantCode(1);
    }

    public SceneAnimation(ImageView pImageView, int[] pFrameRess, int pDuration, long pBreakDelay)
    {
        mImageView = pImageView;
        mFrameRess = pFrameRess;
        mDuration = pDuration;
        mLastFrameNo = pFrameRess.length - 1;
        mBreakDelay = pBreakDelay;

        mImageView.setBackgroundResource(mFrameRess[0]);
        playConstant(1);
    }

    private void play(final int pFrameNo)
    {
        mImageView.postDelayed(new Runnable()
        {
            public void run()
            {
                mImageView.setBackgroundResource(mFrameRess[pFrameNo]);
                // 当播放完成后
                if (pFrameNo == mLastFrameNo)
                {
                    // play(0);


                }
                else
                {

                    play(pFrameNo + 1);
                }
            }
        }, mDurations[pFrameNo]);
    }

    private void playConstant(final int pFrameNo)
    {
        mImageView.postDelayed(new Runnable()
        {
            public void run()
            {
                mImageView.setBackgroundResource(mFrameRess[pFrameNo]);

                if (pFrameNo == mLastFrameNo)
                {
                    // 当动画执行完成后设置imageView可点击
                    mImageView.setClickable(true);

                    //播放完成后显示第一张图片
                    mImageView.setBackgroundResource(mFrameRess[0]);

                }
                else
                {
                    // 在动画执行时imageView不可点击
                    mImageView.setClickable(false);
                    playConstant(pFrameNo + 1);
                }
            }
        }, pFrameNo == mLastFrameNo && mBreakDelay > 0 ? mBreakDelay : mDuration);
    }

    private void playConstantCode(final int pFrameNo)
    {
        mImageView.postDelayed(new Runnable()
        {
            public void run()
            {
                mImageView.setBackgroundResource(mFrameRess[pFrameNo]);

                if (pFrameNo == mLastFrameNo)
                {
                    // 当动画执行完成后设置imageView可点击
                    mImageView.setClickable(true);
                    mImageView.setBackgroundResource(mFrameRess[0]);
                }
                else
                {
                    // 在动画执行时imageView不可点击
                    mImageView.setClickable(false);
                    playConstantCode(pFrameNo + 1);
                }
            }
        }, pFrameNo == mLastFrameNo && mBreakDelay > 0 ? mBreakDelay : mDuration);
    }
}
