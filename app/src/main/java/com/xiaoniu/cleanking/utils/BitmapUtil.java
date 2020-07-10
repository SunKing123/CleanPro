package com.xiaoniu.cleanking.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zzh
 * @date 2020/7/10 13
 * @mail：zhengzhihao@xiaoniuhy.com
 */
public class BitmapUtil {

    public static Bitmap convertViewToBitmap(View view) {
        if (view == null) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
//            bitmap = Bitmap.createBitmap(view.getDrawingCache());
            Bitmap tmpBmp = view.getDrawingCache();
            if (tmpBmp == null) {
                return null;
            }
            Matrix matrix = new Matrix();
            matrix.setScale(0.5f, 0.5f);
            bitmap = Bitmap.createBitmap(tmpBmp, 0, 0, tmpBmp.getWidth(),
                    tmpBmp.getHeight(), matrix, true);
            //清理绘图缓存，释放资源
            view.destroyDrawingCache();
        } catch (Exception e) {
            e.printStackTrace();
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            bitmap = Bitmap.createBitmap(view.getDrawingCache());
            //清理绘图缓存，释放资源
            view.destroyDrawingCache();
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }


    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }
}
