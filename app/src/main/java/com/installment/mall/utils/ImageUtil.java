package com.installment.mall.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.installment.mall.R;

import java.io.File;

public class ImageUtil {

    public static void display(String imageUrl, ImageView imageView) {
        if (TextUtils.isEmpty(imageUrl)) {
            imageUrl = "http://xiaoniutaojin.jpg";
        }

        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.other_empty)// 正在加载中的图片
                .error(R.mipmap.other_empty) // 加载失败的图片
                .diskCacheStrategy(DiskCacheStrategy.ALL); // 磁盘缓存策略
        Glide.with(imageView.getContext()).load(imageUrl)
                .apply(options).into(imageView);
    }

    public static void display(String imageUrl, ImageView imageView, Integer errorImgResouce) {
        if (TextUtils.isEmpty(imageUrl)) {
            imageUrl = "http://xiaoniutaojin.jpg";
        }
        if (errorImgResouce == null) {
            errorImgResouce = R.mipmap.other_empty;
        }
        RequestOptions options = new RequestOptions()
                .placeholder(errorImgResouce)// 正在加载中的图片
                .error(errorImgResouce) // 加载失败的图片
                .diskCacheStrategy(DiskCacheStrategy.ALL); // 磁盘缓存策略
        Glide.with(imageView.getContext()).load(imageUrl)
                .apply(options).into(imageView);
    }

    public static void display(Fragment context, String imageUrl, ImageView imageView, Integer errorImgResouce) {
        if (TextUtils.isEmpty(imageUrl)) {
            imageUrl = "http://xiaoniutaojin.jpg";
        }
        if (errorImgResouce == null) {
            errorImgResouce = R.mipmap.other_empty;
        }
        RequestOptions options = new RequestOptions()
                .placeholder(errorImgResouce)// 正在加载中的图片
                .error(errorImgResouce) // 加载失败的图片
                .diskCacheStrategy(DiskCacheStrategy.ALL); // 磁盘缓存策略
        Glide.with(context).load(imageUrl)
                .apply(options).into(imageView);
    }

    public static void display(Activity context, String imageUrl, ImageView imageView, Integer errorImgResouce) {
        if (TextUtils.isEmpty(imageUrl)) {
            imageUrl = "http://xiaoniutaojin.jpg";
        }
        if (errorImgResouce == null) {
            errorImgResouce = R.mipmap.other_empty;
        }
        RequestOptions options = new RequestOptions()
                .placeholder(errorImgResouce)// 正在加载中的图片
                .error(errorImgResouce) // 加载失败的图片
                .diskCacheStrategy(DiskCacheStrategy.ALL); // 磁盘缓存策略
        Glide.with(context).load(imageUrl)
                .apply(options).into(imageView);
    }


    public static void display(File file, ImageView imageView, Integer errorImgResouce) {
        if (errorImgResouce == null) {
            errorImgResouce = R.mipmap.other_empty;
        }
        RequestOptions options = new RequestOptions()
                .placeholder(errorImgResouce)// 正在加载中的图片
                .error(errorImgResouce) // 加载失败的图片
                .diskCacheStrategy(DiskCacheStrategy.ALL); // 磁盘缓存策略
        Glide.with(imageView.getContext()).load(file)
                .apply(options).into(imageView);
    }

    public static void displayRound(String imageUrl, ImageView imageView, Integer errorImgResouce) {
        if (TextUtils.isEmpty(imageUrl)) {
            imageUrl = "http://xiaoniutaojin.jpg";
        }
        if (errorImgResouce == null) {
            errorImgResouce = R.mipmap.other_empty;
        }
        RequestOptions options = new RequestOptions()
                .placeholder(errorImgResouce)// 正在加载中的图片
                .error(errorImgResouce) // 加载失败的图片
                .bitmapTransform(new RoundedCorners(15))
                .diskCacheStrategy(DiskCacheStrategy.ALL); // 磁盘缓存策略
        Glide.with(imageView.getContext()).load(imageUrl).
                apply(options).into(imageView);
    }

    public static void displayBackgroun(String url, final View view, Integer errorImgResouce) {
        if (errorImgResouce == null) {
            errorImgResouce = R.mipmap.other_empty;
        }
        RequestOptions options = new RequestOptions()
                .placeholder(errorImgResouce)// 正在加载中的图片
                .error(errorImgResouce) // 加载失败的图片
                .bitmapTransform(new RoundedCorners(5))
                .diskCacheStrategy(DiskCacheStrategy.ALL); // 磁盘缓存策略
        SimpleTarget<Drawable> simpleTarget = new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                view.setBackground(resource);
            }
        };
        Glide.with(view.getContext()).load(url).apply(options).into(simpleTarget);
    }

    public static Bitmap getRoundCornerImage(Bitmap bitmap, int roundPixels) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap roundConcerImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);//创建一个和原始图片一样大小的位图
        Canvas canvas = new Canvas(roundConcerImage);//创建位图画布
        Paint paint = new Paint();//创建画笔

        Rect rect = new Rect(0, 0, width, height);//创建一个和原始图片一样大小的矩形
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);// 抗锯齿

        canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);//画一个基于前面创建的矩形大小的圆角矩形
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//设置相交模式
        canvas.drawBitmap(bitmap, null, rect, paint);//把图片画到矩形去
        return Bitmap.createBitmap(roundConcerImage, 0, 0, width, height - roundPixels);
    }

}
