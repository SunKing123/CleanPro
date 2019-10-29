package com.xiaoniu.cleanking.ui.main.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;

import java.util.List;

public class ImagePreviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<FileEntity> listImage;

    public List<FileEntity> getListImage() {
        return listImage;
    }

    public void setListImage(List<FileEntity> listImage) {
        this.listImage = listImage;
    }

    Activity mActivity;
    onCheckListener mOnCheckListener;

    int selectPosition;

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }

    public ImagePreviewAdapter(Activity mActivity, List<FileEntity> listImage, int selectPosition) {
        super();
        this.mActivity = mActivity;
        this.listImage = listImage;
        this.selectPosition = selectPosition;
    }

    public void deleteData(List<FileEntity> tempList) {
        listImage.removeAll(tempList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_preview, parent, false);
        return new ImageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
            String path = listImage.get(position).getPath();
            Glide.with(mActivity)
                    .load(path)
                    .into(((ImageViewHolder) holder).iv_photo_filelist_pic);

            ((ImageViewHolder) holder).rel_select.setVisibility(listImage.get(position).getIsSelect() ? View.VISIBLE : View.GONE);
            ((ImageViewHolder) holder).tv_select.setVisibility(selectPosition == position ? View.VISIBLE : View.GONE);
            ((ImageViewHolder) holder).iv_photo_filelist_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnCheckListener != null)
                        mOnCheckListener.onCheck(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listImage.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_photo_filelist_pic;
        public TextView tv_select;
        public RelativeLayout rel_select;

        public ImageViewHolder(View itemView) {
            super(itemView);
            iv_photo_filelist_pic = itemView.findViewById(R.id.iv_photo_filelist_pic);
            tv_select = itemView.findViewById(R.id.tv_select);
            rel_select = itemView.findViewById(R.id.rel_select);
        }
    }


    public interface onCheckListener {
        void onCheck(int pos);
    }

    public void setmOnCheckListener(onCheckListener mOnCheckListener) {
        this.mOnCheckListener = mOnCheckListener;
    }
}
