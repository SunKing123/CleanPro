package com.xiaoniu.cleanking.ui.tool.qq.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.tool.qq.bean.CleanWxClearInfo;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.common.utils.TimeUtils;

import java.util.List;

public class QQCleanAudAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<CleanWxClearInfo> listImage;


    public void deleteData(List<CleanWxClearInfo> tempList) {
        listImage.removeAll(tempList);
        Log.e("gfd", "删除后：" + listImage.size());
        notifyDataSetChanged();
    }

    public void setIsCheckAll(boolean isCheckAll) {

        for (int i = 0; i < this.listImage.size(); i++) {
            listImage.get(i).setIsSelect(isCheckAll);
        }
        notifyDataSetChanged();
    }


    public List<CleanWxClearInfo> getListImage() {
        return listImage;
    }

    Activity mActivity;
    onCheckListener mOnCheckListener;


    public QQCleanAudAdapter(Activity mActivity, List<CleanWxClearInfo> listImage) {
        super();
        this.mActivity = mActivity;
        this.listImage = listImage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wxaud, parent, false);
        return new ImageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).tv_name.setText(listImage.get(position).getFileName());
            ((QQCleanAudAdapter.ImageViewHolder) holder).tv_time.setText(TimeUtils.getTimesByLong(listImage.get(position).getTime()));
            ((ImageViewHolder) holder).tv_size.setText(CleanAllFileScanUtil.byte2FitSizeOne(listImage.get(position).getSize()));
            ((ImageViewHolder) holder).tv_select.setBackgroundResource(listImage.get(position).getIsSelect() ? R.drawable.icon_select : R.drawable.icon_unselect);
            ((ImageViewHolder) holder).tv_select.setOnClickListener(v -> {
                listImage.get(position).setIsSelect(!listImage.get(position).getIsSelect());
                ((ImageViewHolder) holder).tv_select.setBackgroundResource(listImage.get(position).getIsSelect() ? R.drawable.icon_select : R.drawable.icon_unselect);
                if (mOnCheckListener != null)
                    mOnCheckListener.onCheck(listImage, position);
            });

        }
    }

    @Override
    public int getItemCount() {
        return listImage.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_select;
        public TextView tv_name;
        public TextView tv_size;
        public TextView tv_time;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tv_select = itemView.findViewById(R.id.tv_select);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_size = itemView.findViewById(R.id.tv_size);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }


    public interface onCheckListener {
        void onCheck(List<CleanWxClearInfo> listFile, int pos);
    }

    public void setmOnCheckListener(onCheckListener mOnCheckListener) {
        this.mOnCheckListener = mOnCheckListener;
    }
}
