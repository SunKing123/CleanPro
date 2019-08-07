package com.xiaoniu.cleanking.ui.tool.qq.adapter;

import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.tool.qq.bean.CleanWxClearInfo;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;

import java.util.ArrayList;
import java.util.List;

public class QQCleanFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<CleanWxClearInfo> listImage = new ArrayList<>();


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


    public ArrayList<CleanWxClearInfo> getListImage() {
        return listImage;
    }

    Activity mActivity;
    onCheckListener mOnCheckListener;


    public QQCleanFileAdapter(Activity mActivity, ArrayList<CleanWxClearInfo> listImage) {
        super();
        this.mActivity = mActivity;
        this.listImage = listImage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wxfile, parent, false);
        return new ImageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
//            String path = listImage.get(position).getPath();
//            Glide.with(mActivity)
//                    .load(path)
//                    .into(((ImageViewHolder) holder).iv_photo_filelist_pic);
            ((ImageViewHolder) holder).tv_name.setText(listImage.get(position).getFileName());
            ((ImageViewHolder) holder).tv_time.setText(com.xiaoniu.cleanking.utils.TimeUtil.getTimesByLong(listImage.get(position).getTime()));
            ((ImageViewHolder) holder).tv_size.setText(CleanAllFileScanUtil.byte2FitSizeOne(listImage.get(position).getSize()));
            ((ImageViewHolder) holder).tv_select.setBackgroundResource(listImage.get(position).getIsSelect() ? R.drawable.icon_select : R.drawable.icon_unselect);
            ((ImageViewHolder) holder).tv_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listImage.get(position).setIsSelect(!listImage.get(position).getIsSelect());
                    ((ImageViewHolder) holder).tv_select.setBackgroundResource(listImage.get(position).getIsSelect() ? R.drawable.icon_select : R.drawable.icon_unselect);
                    if (mOnCheckListener != null)
                        mOnCheckListener.onCheck(listImage, position);
                }
            });
            ((ImageViewHolder) holder).conslayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AndroidUtil.openFileSafe( mActivity, String.valueOf(listImage.get(position).getFileName()));
                }
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
        public ConstraintLayout conslayout;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tv_select = (TextView) itemView.findViewById(R.id.tv_select);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            conslayout = (ConstraintLayout) itemView.findViewById(R.id.conslayout);
        }
    }


    public interface onCheckListener {
        public void onCheck(List<CleanWxClearInfo> listFile, int pos);
    }

    public void setmOnCheckListener(onCheckListener mOnCheckListener) {
        this.mOnCheckListener = mOnCheckListener;
    }
}
