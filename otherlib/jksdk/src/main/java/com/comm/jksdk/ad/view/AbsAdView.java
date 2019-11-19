package com.comm.jksdk.ad.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.qq.e.ads.nativ.NativeUnifiedADData;

import java.util.List;


public abstract class AbsAdView extends RelativeLayout {

    protected final String TAG = "GeekAdSdk-->";

    public Context mContext = null;

    public AbsAdView(Context context) {
        super(context);
        init(context);
    }

    public AbsAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AbsAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public AbsAdView(Context context, String style, String adPositionId) {
        super(context);
    }

    private void init(Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(getLayoutId(), this);
    }

    public abstract int getLayoutId();

    public abstract void requestAd(int requestType, int adRequestTimeOut);

    public abstract void parseYlhAd(List<NativeUnifiedADData> nativeAdList);

    public abstract void parseChjAd(List<TTFeedAd> nativeAdList);
}
