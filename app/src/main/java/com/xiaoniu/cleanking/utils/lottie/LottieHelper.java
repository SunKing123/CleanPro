package com.geek.jk.weather.main.helper;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;

import com.agile.frame.utils.LogUtils;
import com.airbnb.lottie.Cancellable;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;

public class LottieHelper {

    private LottieAnimationView mLottieView = null;

    public LottieHelper(LottieAnimationView lottieView) {
        this.mLottieView = lottieView;
        mLottieView.useHardwareAcceleration();
    }

    public void playAnimation() {
        if(null != mLottieView && !mLottieView.isAnimating()) {
            mLottieView.playAnimation();
            setAnimationAlpha(255);
        }
    }

    public void resumeAnimation() {
        if (!isAnimating())
            mLottieView.resumeAnimation();
    }

    public void pauseAnimation() {
        if(isAnimating()) {
            mLottieView.pauseAnimation();
        }
    }

    public void cancelAnimation() {
        if(isAnimating()) {
            mLottieView.cancelAnimation();
            setAnimationAlpha(0);
        }
    }

    /**
     * 0 - 255
     * @param alpha 0:完全不可见，255：完全可见
     */
    public void setAnimationAlpha(int alpha) {
        if(mLottieView != null){
            mLottieView.setImageAlpha(alpha);
        }
    }

    public void start(Context context,@Nullable int[] marginPixs, String assetName ) {
        if(mLottieView == null){
            return;
        }
        setAnimationAlpha(255);
        if (marginPixs != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(marginPixs[0], marginPixs[1], marginPixs[2], marginPixs[3]);
            mLottieView.setLayoutParams(params);
        }
        LottieComposition.Factory.fromAssetFileName(context, assetName,
                new OnCompositionLoadedListener() {
                    @Override
                    public void onCompositionLoaded(@Nullable LottieComposition composition) {
                        if (composition == null || mLottieView == null) {
                            return;
                        }

                        try {
                            mLottieView.clearAnimation();
                            mLottieView.setComposition(composition);
                            mLottieView.setProgress(0f);
                            mLottieView.playAnimation();
                            LogUtils.e("dkk", assetName + " 播放动画....");
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.e("dkk", "LottieHelper error " + e.getMessage());
                        }
                    }
                });
    }

    private boolean isAnimating() {
        return null != mLottieView && mLottieView.isAnimating();
    }

}
