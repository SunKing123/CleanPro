package com.xiaoniu.cleanking.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.common.utils.AsyncTaskUtils;

import java.io.ByteArrayOutputStream;

public class DisplayImageUtils {

    private static DisplayImageUtils mInstance;
    private Handler mUIHandler;
    private LruCache<Object, Object> mLruCache;

    public static DisplayImageUtils getInstance() {
        if (mInstance == null) {
            synchronized (DisplayImageUtils.class) {
                if (mInstance == null) {
                    mInstance = new DisplayImageUtils();
                }
            }
        }
        return mInstance;
    }

    private static class ImageEntity {
        Bitmap mBitmap;
        ImageView mImageView;
        String path;

        private ImageEntity() {
        }
    }

    public void displayImage(String str, ImageView imageView) {
        if (this.mLruCache == null) {
            this.mLruCache = new LruCache<>(50);
        }
        if (TextUtils.isEmpty(str)) {
            imageView.setImageResource(R.drawable.clean_icon_apk);
            return;
        }
        imageView.setTag(str);
        if (this.mUIHandler == null) {
            this.mUIHandler = new Handler() {
                public void handleMessage(Message message) {
                    ImageEntity aVar = (ImageEntity) message.obj;
                    Bitmap bitmap = aVar.mBitmap;
                    ImageView imageView = aVar.mImageView;
                    String str = aVar.path;
                    if (imageView.getTag() != null && imageView.getTag().toString().equals(str)) {
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        } else {
                            imageView.setImageResource(R.drawable.clean_icon_apk);
                        }
                    }
                }
            };
        }
        Bitmap bitmapFromLruCache = getBitmapFromLruCache(str);
        if (bitmapFromLruCache != null) {
            imageView.setImageBitmap(bitmapFromLruCache);
        } else {
            getPicInBackground(str, imageView);
        }
    }

    private void getPicInBackground(final String str, final ImageView imageView) {
        AsyncTaskUtils.background(new Runnable() {
            @Override
            public void run() {
                Bitmap drawableToBitamp = drawableToBitamp(getAppIcon(AppApplication.getInstance(), str));
                if (drawableToBitamp == null) {
                    drawableToBitamp = BitmapFactory.decodeResource(AppApplication.getInstance().getResources(), R.mipmap.icon_apk);
                }
                setBitmapToLruCache(str, drawableToBitamp);
                refreshBitmap(str, imageView, drawableToBitamp);
            }
        });
    }

    public void setBitmapToLruCache(String str, Bitmap bitmap) {
        if (getBitmapFromLruCache(str) == null && bitmap != null) {
            this.mLruCache.put(str, bitmap);
        }
    }

    public Bitmap getBitmapFromLruCache(String str) {
        if (str == null) {
            return null;
        }
        return (Bitmap) this.mLruCache.get(str);
    }

    /* access modifiers changed from: private */
    public void refreshBitmap(String str, ImageView imageView, Bitmap bitmap) {
        Message obtain = Message.obtain();
        ImageEntity aVar = new ImageEntity();
        aVar.mBitmap = bitmap;
        aVar.path = str;
        aVar.mImageView = imageView;
        obtain.obj = aVar;
        this.mUIHandler.sendMessage(obtain);
    }

    private Bitmap drawableToBitamp(Drawable drawable) {
        if (drawable == null || !(drawable instanceof BitmapDrawable)) {
            return null;
        }
        return ((BitmapDrawable) drawable).getBitmap();
    }

    private Drawable getAppIconFromPackageName(Context context, String str) {
        try {
            PackageManager pm = AppApplication.getInstance().getPackageManager();
            return pm.getApplicationInfo(str, 0).loadIcon(pm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Drawable getAppIcon(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (str.endsWith(".apk")) {
            return getApkIconFromPath(context, str);
        }
        return getAppIconFromPackageName(context, str);
    }


    private Drawable getApkIconFromPath(Context context, String str) {
        PackageInfo packageArchiveInfo = AppApplication.getInstance().getPackageManager().getPackageArchiveInfo(str, 1);
        if (packageArchiveInfo != null) {
            ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
            applicationInfo.sourceDir = str;
            applicationInfo.publicSourceDir = str;
            try {
                return applicationInfo.loadIcon(AppApplication.getInstance().getPackageManager());
            } catch (Error e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Drawable转换成byte[]
     *
     * @param d
     * @return
     */
    public static byte[] drawableToBytes(Drawable d) {
        Bitmap bitmap = drawableToBitmap(d);
        return bitmapToBytes(bitmap);
    }

    /**
     * Drawable转换成Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap转换成byte[]
     *
     * @param bm
     * @return
     */
    public static byte[] bitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * byte[]转换成Drawable
     *
     * @param b
     * @return
     */
    public static Drawable bytesToDrawable(byte[] b) {
        Bitmap bitmap = bytesToBitmap(b);
        return bitmapToDrawable(bitmap);
    }

    /**
     * byte[]转换成Bitmap
     *
     * @param b
     * @return
     */
    public static Bitmap bytesToBitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }

    /**
     * Bitmap转换成Drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        Drawable d = (Drawable) bd;
        return d;
    }
}
