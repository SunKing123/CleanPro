package com.xiaoniu.cleanking.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.bean.MainTableItem;
import com.xiaoniu.cleanking.ui.main.bean.VirusLlistEntity;
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

    private Context mContext;
    //一键清理按钮
    private ItmMTView itmAcc;
    //通知清理按钮
    private ItmMTView itmNotify;
    //电量优化按钮
    private ItmMTView itmBattery;
    //病毒查杀
    private ItmMTView itmVirusKill;

    private RecycleViewAdapter adapter;

    MainTableItem[] items = new MainTableItem[]{
            new MainTableItem(2, MainTableItem.TAG_ACC, R.drawable.icon_yjjs, "一键加速"),
            new MainTableItem(5, MainTableItem.TAG_KILL_VIRUS, R.drawable.icon_virus, "病毒查杀"),
            new MainTableItem(1, MainTableItem.TAG_COOL, R.drawable.icon_home_jw, "手机降温"),
            new MainTableItem(4, MainTableItem.TAG_BATTER, R.drawable.icon_power, "超强省电"),
            new MainTableItem(1, MainTableItem.TAG_CLEAN_WX, R.drawable.icon_home_wx, "微信专清"),
            new MainTableItem(1, MainTableItem.TAG_SPEED_UP_NETWORK, R.drawable.icon_network, "网络加速"),
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
        itmAcc = new ItmMTView(mContext);
        itmNotify = new ItmMTView(mContext);
        itmBattery = new ItmMTView(mContext);
        itmVirusKill = new ItmMTView(mContext);
        adapter = new RecycleViewAdapter();
        addItemDecoration(new GridLayoutDivider(mContext, LinearLayoutManager.HORIZONTAL, R.drawable.icon_line_hor));
        setNestedScrollingEnabled(false);
        setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        setLayoutManager(gridLayoutManager);
        setAdapter(adapter);
        adapter.setData(Arrays.asList(items));
    }


    /**
     * ****************************************************************************************************************************
     * ******************************************************adapter***************************************************************
     * ****************************************************************************************************************************
     */
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
                    holder = new ViewHolderAcc(itmAcc);
                    break;
                case 3:
                    holder = new ViewHolderNotify(itmNotify);
                    break;
                case 4:
                    holder = new ViewHolderBattery(itmBattery);
                    break;
                case 5:
                    holder = new ViewHolderVirusKill(itmVirusKill);
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

        /**
         * ****************************************************************************************************************************
         * ******************************************************view holders**********************************************************
         * ****************************************************************************************************************************
         */
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
                //the gridView right line is gone。
                if (position % 3 == 0) {
                    itmMTView.setLineVisible(View.GONE);
                }
            }
        }

        class ViewHolderAcc extends StyleViewHolder {
            public ViewHolderAcc(@NonNull ItmMTView itemView) {
                super(itemView);
            }

            public void bindData(MainTableItem item) {
                //cover father doing. just do visibleLine
                super.visibleLine();
            }
        }

        class ViewHolderNotify extends StyleViewHolder {
            public ViewHolderNotify(@NonNull ItmMTView itemView) {
                super(itemView);
            }

            public void bindData(MainTableItem item) {
                //cover father doing. just do visibleLine
                super.visibleLine();
            }
        }

        class ViewHolderBattery extends StyleViewHolder {

            public ViewHolderBattery(@NonNull ItmMTView itemView) {
                super(itemView);
            }

            public void bindData(MainTableItem item) {
                //cover father doing. just do visibleLine
                super.visibleLine();
            }
        }

        class ViewHolderVirusKill extends StyleViewHolder {

            public ViewHolderVirusKill(@NonNull ItmMTView itemView) {
                super(itemView);
            }

            public void bindData(MainTableItem item) {
                //cover father doing. just do visibleLine
                super.visibleLine();
            }
        }
    }


    /**
     * ****************************************************************************************************************************
     * ******************************************************item view click listener**********************************************
     * ****************************************************************************************************************************
     */
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
        itmNotify.setVisibleTick(View.GONE);
        itmNotify.loadDrawable(context, R.drawable.icon_home_qq_o);
        itmNotify.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
        itmNotify.setText(getString(R.string.find_harass_notify));
    }

    //骚扰通知数量更新
    public void notifyNumStyle(Context context) {
        itmNotify.setVisibleTick(View.GONE);
        itmNotify.loadDrawable(context, R.drawable.icon_notify);
        itmNotify.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
        itmNotify.setText(context.getString(R.string.find_harass_notify_num, NotifyCleanManager.getInstance().getAllNotifications().size() + ""));
    }

    //通知栏清理
    public void notifyCleanStyle(Context context) {
        itmNotify.setVisibleTick(View.GONE);
        itmNotify.loadDrawable(context, R.drawable.icon_home_qq);
        itmNotify.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
        itmNotify.setText(getString(R.string.tool_notification_clean));
    }

    //清理完成
    public void notifyFinishStyle(Context context) {
        itmNotify.setVisibleTick(VISIBLE);
        itmNotify.loadDrawable(context, R.drawable.icon_home_qq);
        itmNotify.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
        itmNotify.setText(getString(R.string.finished_clean_notify_hint));
    }

    /**
     * ****************************************************************************************************************************
     * ******************************************************battery clean button styles*******************************************
     * ****************************************************************************************************************************
     */
    public void batteryNormalStyle(Context context) {
        itmBattery.setVisibleTick(GONE);
        itmBattery.loadDrawable(context, R.drawable.icon_power);
        itmBattery.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
        itmBattery.setText(getString(R.string.tool_super_power_saving));
    }

    public void batterySavingStyle(Context context) {
        itmBattery.setVisibleTick(GONE);
        itmBattery.loadDrawable(context, R.drawable.icon_power_o);
        itmBattery.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
        itmBattery.setText(getString(R.string.tool_super_power_saving));
    }

    public void batteryNumStyle(Context context) {
        itmBattery.setVisibleTick(GONE);
        itmBattery.loadDrawable(context, R.drawable.icon_power_gif);
        itmBattery.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
        itmBattery.setText(getString(R.string.power_consumption_num, NumberUtils.mathRandom(8, 15)));
    }

    public void batteryFinishStyle(Context context) {
        itmBattery.setVisibleTick(VISIBLE);
        itmBattery.loadDrawable(context, R.drawable.icon_power);
        itmBattery.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));

        if (TextUtils.isEmpty(PreferenceUtil.getLengthenAwaitTime())) {
            itmBattery.setText(getString(R.string.lengthen_time, "40"));
        } else {
            itmBattery.setText(getString(R.string.lengthen_time, PreferenceUtil.getLengthenAwaitTime()));
        }
    }

    /**
     * ****************************************************************************************************************************
     * ******************************************************one key acc button styles*********************************************
     * ****************************************************************************************************************************
     */
    public void accStorageLowStyle(Context context) {
        itmAcc.setVisibleTick(VISIBLE);
        itmAcc.loadDrawable(context, R.drawable.icon_yjjs);
        itmAcc.setTextColor(ContextCompat.getColor(getContext(), R.color.color_323232));
        itmAcc.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(15, 30)) + "%");
    }

    public void accStorageHighStyle(Context context) {
        itmAcc.setVisibleTick(GONE);
        itmAcc.loadDrawable(context, R.drawable.icon_quicken);
        itmAcc.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FF4545));
        itmAcc.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
    }

    public void accOneKeyStyle(Context context) {
        itmAcc.setVisibleTick(GONE);
        itmAcc.loadDrawable(context, R.drawable.icon_yjjs_o);
        itmAcc.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFAC01));
        itmAcc.setText(getString(R.string.tool_one_key_speed));
    }

    /**
     * ****************************************************************************************************************************
     * ******************************************************kill virus button styles**********************************************
     * ****************************************************************************************************************************
     */

    public void killVirusNormalStyle() {
        itmVirusKill.clearMark();
    }

    public void killVirusWarningStyle() {
        itmVirusKill.setMarkText("有风险");
    }

    public void killVirusCleanWarningStyle() {
        itmVirusKill.clearMark();
    }

    /**
     * ****************************************************************************************************************************
     * ******************************************************multiple style********************************************************
     * ****************************************************************************************************************************
     */
    public void setMultipleStyle(VirusLlistEntity item) {
        itmVirusKill.clearMark();
        itmVirusKill.setText(item.getName());
        itmVirusKill.loadDrawable(mContext, item.getIcon());
    }


    private CharSequence getString(@StringRes int resId) {
        return getResources().getString(resId);
    }

    @NonNull
    public final String getString(@StringRes int resId, Object... formatArgs) {
        return getResources().getString(resId, formatArgs);
    }
}
