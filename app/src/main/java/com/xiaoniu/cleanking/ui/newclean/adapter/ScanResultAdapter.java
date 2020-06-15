package com.xiaoniu.cleanking.ui.newclean.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.bean.JunkResultWrapper;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ScanResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<JunkResultWrapper> junkResultWrapperList = new ArrayList<>();
    private OnItemClickListener<JunkResultWrapper> mOnItemClickListener;

    public ScanResultAdapter(OnItemClickListener<JunkResultWrapper> onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == JunkResultWrapper.ITEM_TYPE_TITLE) {
            return new ScanResultTypeViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.scan_result_title_item, viewGroup, false), mOnItemClickListener);
        } else {
            return new ScanResultContentViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.scan_result_content_item, viewGroup, false), mOnItemClickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        JunkResultWrapper wrapper = junkResultWrapperList.get(position);
        if (wrapper == null) {
            return;
        }
        if (getItemViewType(position) == JunkResultWrapper.ITEM_TYPE_TITLE) {
            if (viewHolder instanceof ScanResultTypeViewHolder) {
                ScanResultTypeViewHolder typeViewHolder = (ScanResultTypeViewHolder) viewHolder;
                typeViewHolder.bind(wrapper);
            }
        } else {
            if (viewHolder instanceof ScanResultContentViewHolder) {
                ScanResultContentViewHolder contentViewHolder = (ScanResultContentViewHolder) viewHolder;
                contentViewHolder.bind(wrapper);
            }
        }
    }

    public void submitList(List<JunkResultWrapper> junkResultWrappers) {
        if (junkResultWrappers != null) {
            junkResultWrapperList.clear();
            junkResultWrapperList.addAll(junkResultWrappers);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return junkResultWrapperList.get(position).itemTye;
    }

    @Override
    public int getItemCount() {
        return junkResultWrapperList != null ? junkResultWrapperList.size() : 0;
    }

    static class ScanResultTypeViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_junk_type_title;
        private TextView tv_checked_junk_total;
        private ImageView iv_check_junk_state;
        private RelativeLayout rl_type_root;

        private OnItemClickListener<JunkResultWrapper> mOnItemClickListener;

        ScanResultTypeViewHolder(@NonNull View itemView, OnItemClickListener<JunkResultWrapper> onItemClickListener) {
            super(itemView);
            this.mOnItemClickListener = onItemClickListener;
            tv_junk_type_title = itemView.findViewById(R.id.tv_junk_type_title);
            tv_checked_junk_total = itemView.findViewById(R.id.tv_checked_junk_total);
            iv_check_junk_state = itemView.findViewById(R.id.iv_check_junk_state);
            rl_type_root = itemView.findViewById(R.id.rl_type_root);
        }

        public void bind(JunkResultWrapper wrapper) {
            JunkGroup junkGroup = wrapper.junkGroup;
            tv_junk_type_title.setText(junkGroup.mName);
            if (junkGroup.isExpand) {
                tv_junk_type_title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.icon_arrow_down, 0);
            } else {
                tv_junk_type_title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.icon_arrow_up, 0);
            }
            CountEntity mCountEntity = CleanUtil.formatShortFileSize(junkGroup.mSize);
            tv_checked_junk_total.setText(itemView.getResources().getString(R.string.scan_result_check, mCountEntity.getResultSize()));
            iv_check_junk_state.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, wrapper, getAdapterPosition());
                }
            });

            if (junkGroup.isCheckPart) {
                iv_check_junk_state.setImageResource(R.drawable.ic_scan_result_check);
            } else if (junkGroup.isChecked) {
                iv_check_junk_state.setImageResource(R.drawable.ic_scan_result_checked);
            } else {
                iv_check_junk_state.setImageResource(R.drawable.ic_scan_result_nomal);
            }
            rl_type_root.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, wrapper, getAdapterPosition());
                }
            });
        }
    }

    static class ScanResultContentViewHolder extends RecyclerView.ViewHolder {

        private View v_space;
        private ImageView iv_junk_logo;
        private TextView tv_junk_title;
        private TextView tv_junk_sub_title;
        private TextView tv_checked_total;
        private ImageView iv_check_state;

        private LinearLayout linear_uncareful,linear_careful,linear_child_view;
        private ImageView iv_check_careful_state,iv_check_uncareful_state;
        private TextView tv_checked_uncareful_total,tv_checked_careful_total;
        private RelativeLayout rel_first_level;

        private OnItemClickListener<JunkResultWrapper> mOnItemClickListener;

        ScanResultContentViewHolder(@NonNull View itemView, OnItemClickListener<JunkResultWrapper> onItemClickListener) {
            super(itemView);
            this.mOnItemClickListener = onItemClickListener;
            v_space = itemView.findViewById(R.id.v_space);
            iv_junk_logo = itemView.findViewById(R.id.iv_junk_logo);
            tv_junk_title = itemView.findViewById(R.id.tv_junk_title);
            tv_junk_sub_title = itemView.findViewById(R.id.tv_junk_sub_title);
            tv_checked_total = itemView.findViewById(R.id.tv_checked_total);
            iv_check_state = itemView.findViewById(R.id.iv_check_state);


            linear_child_view = itemView.findViewById(R.id.linear_child_view);
            linear_careful = itemView.findViewById(R.id.linear_careful);
            linear_uncareful = itemView.findViewById(R.id.linear_uncareful);
            rel_first_level = itemView.findViewById(R.id.rel_first_level);

            iv_check_careful_state = itemView.findViewById(R.id.iv_check_careful_state);
            iv_check_uncareful_state = itemView.findViewById(R.id.iv_check_uncareful_state);

            tv_checked_uncareful_total = itemView.findViewById(R.id.tv_checked_uncareful_total);
            tv_checked_careful_total = itemView.findViewById(R.id.tv_checked_careful_total);
        }

        public void bind(JunkResultWrapper wrapper) {
            FirstJunkInfo firstJunkInfo = wrapper.firstJunkInfo;
            if (firstJunkInfo.isFirstItem()) {
                v_space.setVisibility(View.GONE);
            } else {
                v_space.setVisibility(View.VISIBLE);
            }
            CountEntity mCountEntity = CleanUtil.formatShortFileSize(firstJunkInfo.getTotalSize());
            tv_checked_total.setText(mCountEntity.getResultSize());
            tv_junk_title.setText(firstJunkInfo.getAppName());
            if (firstJunkInfo.getGarbageIcon() != null) {
                iv_junk_logo.setImageDrawable(firstJunkInfo.getGarbageIcon());
            } else {
                iv_junk_logo.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.icon_other_cache));
            }


            iv_check_state.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, wrapper, getAdapterPosition());
                }
            });

            if (firstJunkInfo.isAllchecked()) {
                iv_check_state.setImageResource(R.drawable.ic_scan_result_checked);
            } else {
                iv_check_state.setImageResource(R.drawable.ic_scan_result_nomal);
            }


            tv_junk_sub_title.setVisibility(View.VISIBLE);
            if (wrapper.scanningResultType == ScanningResultType.APK_JUNK) {
                tv_junk_sub_title.setText(firstJunkInfo.getDescp() + " v" + firstJunkInfo.getVersionName());
            } else if (wrapper.scanningResultType == ScanningResultType.CACHE_JUNK) {
                tv_junk_sub_title.setText("建议清理");
            } else {
                tv_junk_sub_title.setVisibility(View.GONE);
            }


            if(firstJunkInfo.isIsthreeLevel()){
                tv_junk_sub_title.setVisibility(View.GONE);
                if (firstJunkInfo.getCareFulSize() > 0 && firstJunkInfo.getUncarefulSize() > 0) {
                    if (firstJunkInfo.isUncarefulIsChecked() && firstJunkInfo.isCarefulIsChecked()) {
                        iv_check_state.setImageResource(R.drawable.ic_scan_result_checked);
                    } else if (!firstJunkInfo.isUncarefulIsChecked() && !firstJunkInfo.isCarefulIsChecked()) {
                        iv_check_state.setImageResource(R.drawable.ic_scan_result_nomal);
                    } else {
                        iv_check_state.setImageResource(R.drawable.ic_scan_result_check);
                    }
                } else if (firstJunkInfo.getCareFulSize() > 0 && firstJunkInfo.getUncarefulSize() == 0) {
                    if (firstJunkInfo.isCarefulIsChecked()) {
                        iv_check_state.setImageResource(R.drawable.ic_scan_result_checked);
                    } else {
                        iv_check_state.setImageResource(R.drawable.ic_scan_result_nomal);
                    }
                } else if (firstJunkInfo.getCareFulSize() == 0 && firstJunkInfo.getUncarefulSize() > 0) {
                    if (firstJunkInfo.isUncarefulIsChecked()) {
                        iv_check_state.setImageResource(R.drawable.ic_scan_result_checked);
                    } else {
                        iv_check_state.setImageResource(R.drawable.ic_scan_result_nomal);
                    }
                }

                if(firstJunkInfo.isUncarefulIsChecked()){
                    iv_check_uncareful_state.setImageResource(R.drawable.ic_scan_result_checked);
                }else{
                    iv_check_uncareful_state.setImageResource(R.drawable.ic_scan_result_nomal);
                }

                if(firstJunkInfo.isCarefulIsChecked()){
                    iv_check_careful_state.setImageResource(R.drawable.ic_scan_result_checked);
                }else{
                    iv_check_careful_state.setImageResource(R.drawable.ic_scan_result_nomal);
                }
                long carefulSize = firstJunkInfo.getCareFulSize();
                long uncarefulSize = firstJunkInfo.getUncarefulSize();
                if(carefulSize>0){
                    linear_careful.setVisibility(View.VISIBLE);
                    tv_checked_careful_total.setText(CleanUtil.formatShortFileSize(firstJunkInfo.getCareFulSize()).getResultSize());
                }else{
                    linear_careful.setVisibility(View.GONE);
                }
                if(uncarefulSize>0){
                    linear_uncareful.setVisibility(View.VISIBLE);
                    tv_checked_uncareful_total.setText(CleanUtil.formatShortFileSize(firstJunkInfo.getUncarefulSize()).getResultSize());
                }else{
                    linear_uncareful.setVisibility(View.GONE);
                }




                iv_check_uncareful_state.setOnClickListener(v -> {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, wrapper, getAdapterPosition());
                    }
                });

                iv_check_careful_state.setOnClickListener(v -> {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, wrapper, getAdapterPosition());
                    }
                });

                rel_first_level.setOnClickListener(v -> {
                    linear_child_view.setVisibility(linear_child_view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                });
            }



        }
    }
}
