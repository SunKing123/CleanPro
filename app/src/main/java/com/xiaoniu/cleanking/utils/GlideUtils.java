package com.xiaoniu.cleanking.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Util;

/**
 * @author XiLei
 * @date 2019/10/18.
 * description：
 */
public class GlideUtils {

    public static void loadImage(Activity context, String url, ImageView imageView) {
        if (null != context && !context.isDestroyed()) {
            try {
                Glide.with(context).load(url).into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadImage(Context context, String url, ImageView imageView) {
        if (null != context) {
            try {
                Glide.with(context).load(url).centerCrop().into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadRoundImage(Activity context, String url, ImageView imageView, int round) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (null != context && !context.isDestroyed()) {
                try {
                    Glide.with(context).load(url)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(round)))
                            .into(imageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void loadGif(Context context, String url, ImageView imageView, int count) {
        if (null == context) return;
        Glide.with(context).load(url).listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (resource instanceof GifDrawable) {
                    //加载次数
                    ((GifDrawable) resource).setLoopCount(count);
                }
                return false;
            }
        }).into(imageView);
    }

    /**
     * 加载本地Gif
     *
     * @param context
     * @param resourceId
     * @param imageView
     */
    public static void loadDrawble(Activity context, int resourceId, ImageView imageView) {
        //Umeng --- Caused by: java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity
        if (Util.isOnMainThread() && null != context && !context.isDestroyed()) {
            try {
                Glide.with(context).load(resourceId).into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadDrawble(Context context, int resourceId, ImageView imageView) {
        try {
            Glide.with(context).load(resourceId).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
