package com.xiaoniu.cleanking.ui.view;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.bean.MainTableItem;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.widget.divider.GridLayoutDivider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by xinxiaolong on 2020/6/2.
 * email：xinxiaolong123@foxmail.com
 */
public class MainTableView extends RecyclerView {

    Context mContext;
    //一键清理按钮
    ItmMTView itmAccView;
    //通知清理按钮
    ItmMTView itmNotifyView;
    //电量优化按钮
    ItmMTView itmBatteryView;

    RecycleViewAdapter adapter;

    MainTableItem[] items = new MainTableItem[]{
            new MainTableItem(2, MainTableItem.TAG_ACC, R.drawable.icon_yjjs, "一键加速"),
            new MainTableItem(1, MainTableItem.TAG_COOL, R.drawable.icon_home_jw, "病毒查杀"),
            new MainTableItem(1, MainTableItem.TAG_COOL, R.drawable.icon_home_jw, "手机降温"),
            new MainTableItem(4, MainTableItem.TAG_BATTER, R.drawable.icon_power, "超强省电"),
            new MainTableItem(1, MainTableItem.TAG_CLEAN_WX, R.drawable.icon_home_wx, "微信专清"),
            new MainTableItem(3, MainTableItem.TAG_CLEAN_NOTIFY, R.drawable.icon_home_qq, "通知栏清理"),
    };

    public MainTableView(@NonNull Context context) {
        super(context);
    }

    public MainTableView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MainTableView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init() {
        itmAccView = new ItmMTView(mContext);
        itmNotifyView = new ItmMTView(mContext);
        itmBatteryView = new ItmMTView(mContext);
        adapter = new RecycleViewAdapter();
        addItemDecoration(new GridLayoutDivider(mContext, LinearLayoutManager.HORIZONTAL, R.drawable.icon_line_hor));
        setNestedScrollingEnabled(false);
        setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        setLayoutManager(gridLayoutManager);
        setAdapter(adapter);
        adapter.setData(Arrays.asList(items));
    }

    public ItmMTView getItmAccView() {
        return itmAccView;
    }

    public ItmMTView getItmNotifyView() {
        return itmNotifyView;
    }

    public ItmMTView getItmBatteryView() {
        return itmBatteryView;
    }

    public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.StyleViewHolder> {

        List<MainTableItem> mData = new ArrayList<>();

        @NonNull
        @Override
        public StyleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            StyleViewHolder holder = null;
            switch (viewType) {
                case 1:
                    ItmMTView itemView = new ItmMTView(mContext);
                    holder = new StyleViewHolder(itemView);
                    break;
                case 2:
                    holder = new ViewHolderStyle2(itmAccView);
                    break;
                case 3:
                    holder = new ViewHolderStyle3(itmNotifyView);
                    break;
                case 4:
                    holder = new ViewHolderStyle4(itmBatteryView);
                    break;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull StyleViewHolder holder, int position) {
            holder.bindData(mData.get(position));
        }

        public void setData(List<MainTableItem> data) {
            if (data == null)
                return;
            mData = new ArrayList<>(data);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return mData.get(position).styleType;
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class StyleViewHolder extends RecyclerView.ViewHolder {

            ItmMTView itmMTView;

            public StyleViewHolder(ItmMTView view) {
                super(view);
                this.itmMTView = view;
                this.itmMTView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClick.onClick(mData.get(getAdapterPosition()));
                    }
                });
            }

            public void bindData(MainTableItem item) {
                itmMTView.setText(item.text);
                itmMTView.loadDrawable(mContext, item.drawableResId);
                visibleLine();
            }

            protected void visibleLine() {
                int position = getAdapterPosition();
                if (position % 3 == 0) {
                    itmMTView.setLineVisible(View.GONE);
                }
            }
        }

        class ViewHolderStyle2 extends StyleViewHolder {
            public ViewHolderStyle2(@NonNull ItmMTView itemView) {
                super(itemView);
            }

            public void bindData(MainTableItem item) {
                //cover father doing. just do visibleLine
                super.visibleLine();
            }
        }

        class ViewHolderStyle3 extends StyleViewHolder {
            public ViewHolderStyle3(@NonNull ItmMTView itemView) {
                super(itemView);
            }

            public void bindData(MainTableItem item) {
                //cover father doing. just do visibleLine
                super.visibleLine();
            }
        }

        class ViewHolderStyle4 extends StyleViewHolder {

            public ViewHolderStyle4(@NonNull ItmMTView itemView) {
                super(itemView);
            }

            public void bindData(MainTableItem item) {
                //cover father doing. just do visibleLine
                super.visibleLine();
            }
        }
    }

    OnItemClick onItemClick;

    public interface OnItemClick {
        void onClick(MainTableItem item);
    }

    public void setOnItemClickListener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    /**
     * ****************************************************************************************************************************
     * ******************************************************notify clean button styles********************************************
     * ****************************************************************************************************************************
     */
    //发现骚扰通知
    public void notifyHasStyle(Context context) {
        getItmNotifyView().setVisibleTick(View.GONE);
        getItmNotifyView().loadDrawable(context, R.drawable.icon_home_qq_o);
        getItmNotifyView().setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
        getItmNotifyView().setText(getString(R.string.find_harass_notify));
    }

    //骚扰通知数量更新
    public void notifyNumStyle(Context context) {
        getItmNotifyView().setVisibleTick(View.GONE);
        getItmNotifyView().loadDrawable(context, R.drawable.icon_notify);
        getItmNotifyView().setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
        getItmNotifyView().setText(context.getString(R.string.find_harass_notify_num, NotifyCleanManager.getInstance().getAllNotifications().size() + ""));
    }

    //通知栏清理
    public void notifyCleanStyle(Context context) {
        getItmNotifyView().setVisibleTick(View.GONE);
        getItmNotifyView().loadDrawable(context, R.drawable.icon_home_qq);
        getItmNotifyView().setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
        getItmNotifyView().setText(getString(R.string.tool_notification_clean));
    }

    //清理完成
    public void notifyFinishStyle(Context context) {
        getItmNotifyView().setVisibleTick(VISIBLE);
        getItmNotifyView().loadDrawable(context, R.drawable.icon_home_qq);
        getItmNotifyView().setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
        getItmNotifyView().setText(getString(R.string.finished_clean_notify_hint));
    }

    /**
     * ****************************************************************************************************************************
     * ******************************************************battery clean button styles*******************************************
     * ****************************************************************************************************************************
     */
    public void batteryNormalStyle(Context context) {
        getItmBatteryView().setVisibleTick(GONE);
        getItmBatteryView().loadDrawable(context, R.drawable.icon_power);
        getItmBatteryView().setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
        getItmBatteryView().setText(getString(R.string.tool_super_power_saving));
    }

    public void batterySavingStyle(Context context) {
        getItmBatteryView().setVisibleTick(GONE);
        getItmBatteryView().loadDrawable(context, R.drawable.icon_power_o);
        getItmBatteryView().setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
        getItmBatteryView().setText(getString(R.string.tool_super_power_saving));
    }

    public void batteryNumStyle(Context context) {
        getItmBatteryView().setVisibleTick(GONE);
        getItmBatteryView().loadDrawable(context, R.drawable.icon_power_gif);
        getItmBatteryView().setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
        getItmBatteryView().setText(getString(R.string.power_consumption_num, NumberUtils.mathRandom(8, 15)));
    }

    public void batteryFinishStyle(Context context) {
        getItmBatteryView().setVisibleTick(VISIBLE);
        getItmBatteryView().loadDrawable(context, R.drawable.icon_power);
        getItmBatteryView().setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));

        if (TextUtils.isEmpty(PreferenceUtil.getLengthenAwaitTime())) {
            getItmBatteryView().setText(getString(R.string.lengthen_time, "40"));
        } else {
            getItmBatteryView().setText(getString(R.string.lengthen_time, PreferenceUtil.getLengthenAwaitTime()));
        }
    }

    /**
     * ****************************************************************************************************************************
     * ******************************************************one key acc button styles*********************************************
     * ****************************************************************************************************************************
     */
    public void accStorageLowStyle(Context context) {
        getItmAccView().setVisibleTick(VISIBLE);
        getItmAccView().loadDrawable(context, R.drawable.icon_yjjs);
        getItmAccView().setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
        getItmAccView().setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(15, 30)) + "%");
    }

    public void accStorageHighStyle(Context context) {
        getItmAccView().setVisibleTick(GONE);
        getItmAccView().loadDrawable(context, R.drawable.icon_quicken);
        getItmAccView().setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
        getItmAccView().setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
    }

    public void accOneKeyStyle(Context context) {
        getItmAccView().setVisibleTick(GONE);
        getItmAccView().loadDrawable(context, R.drawable.icon_yjjs_o);
        getItmAccView().setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
        getItmAccView().setText(getString(R.string.tool_one_key_speed));
    }

    private CharSequence getString(@StringRes int resId) {
        return getResources().getString(resId);
    }

    @NonNull
    public final String getString(@StringRes int resId, Object... formatArgs) {
        return getResources().getString(resId, formatArgs);
    }
}
