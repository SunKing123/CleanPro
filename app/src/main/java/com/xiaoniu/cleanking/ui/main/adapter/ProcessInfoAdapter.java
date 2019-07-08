package com.xiaoniu.cleanking.ui.main.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.utils.CleanUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProcessInfoAdapter extends RecyclerView.Adapter<ProcessInfoAdapter.ViewHolder> {

    private List<FirstJunkInfo> mList;

    public void setData(List<FirstJunkInfo> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_process_info, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirstJunkInfo junkInfo = mList.get(position);
        holder.mIcon.setImageDrawable(junkInfo.getGarbageIcon());
        holder.mTextName.setText(junkInfo.getAppName());

        holder.mTextStop.setOnClickListener(v->{
            CleanUtil.killAppProcesses(junkInfo.getAppPackageName(),junkInfo.getPid());
            mList.remove(junkInfo);
            notifyDataSetChanged();
        });

        if (position == mList.size() - 1) {
            holder.mViewDivider.setVisibility(View.GONE);
        }else {
            holder.mViewDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon)
        ImageView mIcon;
        @BindView(R.id.text_name)
        TextView mTextName;
        @BindView(R.id.text_stop)
        TextView mTextStop;
        @BindView(R.id.view_divider)
        View mViewDivider;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
