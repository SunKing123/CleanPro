package org.song.videoplayer.media;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.Surface;

/**
 * Created by song on 2017/2/10.
 * Contact github.com/tohodog
 * 解码器父类
 */

public abstract class BaseMedia implements IMediaControl {

    protected IMediaCallback iMediaCallback;
    protected Surface surface;
    protected boolean isPrepar;
    protected Handler mainThreadHandler;
    public HandlerThread mMediaHandlerThread;
    public Handler mMediaHandler;

    public Surface getSurface() {
        return surface;
    }

    public BaseMedia(IMediaCallback iMediaCallback) {
        if (iMediaCallback == null)
            throw new IllegalArgumentException();
        this.iMediaCallback = iMediaCallback;
        mainThreadHandler = new Handler(Looper.getMainLooper());
        mMediaHandlerThread = new HandlerThread("JZVD");
        mMediaHandlerThread.start();
        mMediaHandler = new Handler(mMediaHandlerThread.getLooper());//主线程还是非主线程，就在这里
    }

    @Override
    public void destroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mMediaHandlerThread.quitSafely();
            mMediaHandler = null;
        }
    }
}
