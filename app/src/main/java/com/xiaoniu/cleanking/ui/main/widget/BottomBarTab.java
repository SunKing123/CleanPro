package com.xiaoniu.cleanking.ui.main.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.common.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;


/**
 * Created on 16/6/3.
 */
public class BottomBarTab extends FrameLayout {
    private ImageView mIcon;
    private TextView mTvTitle;
    private Context mContext;
    private int mTabPosition = -1;
    private String title;

    private int[] iconsSelect = {R.drawable.clean_select, R.drawable.tool_select, R.drawable.icon_scratch_tab, R.drawable.me_select};
    private int[] icons = {R.drawable.clean_normal, R.drawable.tool_normal, R.drawable.icon_scratch_tab, R.drawable.me_normal};
    private List<String> iconsSelectNet = new ArrayList<>();
    private List<String> iconsNet = new ArrayList<>();

    private TextView mBadgeView;

    public BottomBarTab(Context context, @DrawableRes int icon, String iconString, CharSequence title, int orderNum) {
        this(context, null, icon, iconString, title, orderNum);
    }

    public BottomBarTab(Context context, AttributeSet attrs, int icon, String iconString, CharSequence title, int orderNum) {
        this(context, attrs, 0, icon, iconString, title, orderNum);
    }

    public BottomBarTab(Context context, AttributeSet attrs, int defStyleAttr, int icon, String iconString, CharSequence title, int orderNum) {
        super(context, attrs, defStyleAttr);
        init(context, icon, iconString, title, orderNum);
    }

    LinearLayout.LayoutParams params;
    protected void init(Context context, int icon, String iconString, CharSequence title, int orderNum) {
        mContext = context;
        this.title = title.toString();

        //        状态（0=隐藏，1=显示）
        String auditSwitch = MmkvUtil.getString(SpCacheConfig.AuditSwitch, "1");

        if (null == AppHolder.getInstance().getIconsEntityList()
                || null == AppHolder.getInstance().getIconsEntityList().getData()
                || AppHolder.getInstance().getIconsEntityList().getData().size() <= 0) {
            if (TextUtils.equals(auditSwitch, "0")) {
                icons = new int[]{R.drawable.clean_normal, R.drawable.me_normal};
                iconsSelect = new int[]{R.drawable.clean_select, R.drawable.me_select};
            } else {
                icons = new int[]{R.drawable.clean_normal, R.drawable.tool_normal, R.drawable.msg_normal, R.drawable.me_normal};
                iconsSelect = new int[]{R.drawable.clean_select, R.drawable.tool_select, R.drawable.msg_select, R.drawable.me_select};
            }
        } else {
            if (TextUtils.equals(auditSwitch, "0")) {
                iconsNet.add(AppHolder.getInstance().getIconsEntityList().getData().get(0).getIconImgUrl());
                iconsNet.add(AppHolder.getInstance().getIconsEntityList().getData().get(3).getIconImgUrl());
                iconsSelectNet.add(AppHolder.getInstance().getIconsEntityList().getData().get(0).getClickIconUrl());
                iconsSelectNet.add(AppHolder.getInstance().getIconsEntityList().getData().get(3).getClickIconUrl());
            } else {
                for (int i = 0; i < AppHolder.getInstance().getIconsEntityList().getData().size(); i++) {
                    iconsNet.add(AppHolder.getInstance().getIconsEntityList().getData().get(i).getIconImgUrl());
                    iconsSelectNet.add(AppHolder.getInstance().getIconsEntityList().getData().get(i).getClickIconUrl());
                    LogUtils.e("==================Tab   "+title+" url="+AppHolder.getInstance().getIconsEntityList().getData().get(i).getClickIconUrl());
                }
            }
        }

        TypedArray typedArray = context.obtainStyledAttributes(new int[]{R.attr.selectableItemBackgroundBorderless});
        Drawable drawable = typedArray.getDrawable(0);
        setBackgroundDrawable(drawable);
        typedArray.recycle();

        LinearLayout lLContainer = new LinearLayout(context);
        lLContainer.setOrientation(LinearLayout.VERTICAL);
        lLContainer.setGravity(Gravity.CENTER);
        LayoutParams paramsContainer = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsContainer.gravity = Gravity.CENTER;
        lLContainer.setLayoutParams(paramsContainer);

        mIcon = new ImageView(context);
        LogUtils.e("==================Tab  iconString="+iconString);
        if (null == mContext || TextUtils.isEmpty(iconString)) {
            params = new LinearLayout.LayoutParams(orderNum == 3 ? 110 : 70, orderNum == 3 ? 110 : 70);
            mIcon.setImageResource(icon);
            LogUtils.e("==================Tab  isEmpty: "+title+"     "+icon);
        } else if (title.equals("刮刮卡")) {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, context.getResources().getDimensionPixelOffset(R.dimen.dimen_40dp));
            mIcon.setScaleType(ImageView.ScaleType.FIT_XY);
            GlideUtils.loadImage((Activity) mContext, iconString, mIcon);
        } else {
            params = new LinearLayout.LayoutParams(orderNum == 3 ? 110 : 70, orderNum == 3 ? 110 : 70);
            GlideUtils.loadImage((Activity) mContext, iconString, mIcon);
        }

        if (null != params) {
            mIcon.setLayoutParams(params);
        }
        lLContainer.addView(mIcon);

        mTvTitle = new TextView(context);
        if (orderNum == 3 && !TextUtils.isEmpty(iconString) && TextUtils.equals(auditSwitch, "1")) {
            mTvTitle.setVisibility(GONE);
        }

        if (title.equals("刮刮卡")) {
            mTvTitle.setVisibility(GONE);
        }

        mTvTitle.setText(title);
        LinearLayout.LayoutParams paramsTv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsTv.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        mTvTitle.setTextSize(11);
        mTvTitle.setTextColor(ContextCompat.getColor(context, R.color.color_666666));
        mTvTitle.setLayoutParams(paramsTv);
        lLContainer.addView(mTvTitle);

        addView(lLContainer);

        mBadgeView = new TextView(context);
        mBadgeView.setTextColor(Color.WHITE);
        mBadgeView.setPadding(DisplayUtils.dip2px(5), 0, DisplayUtils.dip2px(5), 0);
        mBadgeView.setTextSize(10);
        mBadgeView.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams badgeParams = new FrameLayout.LayoutParams(DisplayUtils.dip2px(17), DisplayUtils.dip2px(10));
        badgeParams.gravity = Gravity.CENTER_HORIZONTAL;
        badgeParams.rightMargin = DisplayUtils.dip2px(-12);
        badgeParams.topMargin = DisplayUtils.dip2px(5);
        mBadgeView.setBackgroundResource(R.drawable.icon_bottom_badge);
        mBadgeView.setVisibility(GONE);
        addView(mBadgeView, badgeParams);

    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (title.equals("刮刮卡")) {
            return;
        }
        if (selected) {
            LogUtils.e("==================Tab  setSelected  "+iconsSelect[mTabPosition]);
            if (null == mContext || iconsSelectNet.isEmpty()) {
                mIcon.setImageResource(iconsSelect[mTabPosition]);
            } else {
                GlideUtils.loadImage((Activity) mContext, iconsSelectNet.get(mTabPosition), mIcon);
            }
            mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.color_29D69F));
        } else {
            LogUtils.e("==================Tab  setSelected  false"+iconsSelect[mTabPosition]);
            if (null == mContext || iconsNet.isEmpty()) {
                mIcon.setImageResource(icons[mTabPosition]);
            } else {
                GlideUtils.loadImage((Activity) mContext, iconsNet.get(mTabPosition), mIcon);
            }
            mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.color_999999));
        }
    }

    public void setTabPosition(int position, int currentPosition) {
        mTabPosition = position;
        if (position == currentPosition) {
            setSelected(true);
        }
    }

    public int getTabPosition() {
        return mTabPosition;
    }

    public void showBadgeView(String content) {
        if (!TextUtils.isEmpty(content)) {
            mBadgeView.setVisibility(VISIBLE);
        }
    }

    public void hideBadgeView() {
        mBadgeView.setVisibility(GONE);
    }

    public boolean isBadgeViewShow() {
        return mBadgeView.getVisibility() == VISIBLE;
    }
}
