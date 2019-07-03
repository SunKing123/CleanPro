package com.xiaoniu.cleanking.ui.main.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.activity.PreviewImageActivity;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.bean.Image;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.NumberUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageShowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<FileEntity> listImage = new ArrayList<>();
    List<Integer> listCheck = new ArrayList<>();

    public List<Integer> getListCheck() {
        return listCheck;
    }

    public void setListCheck(List<Integer> listCheck) {
        this.listCheck.clear();
        this.listCheck.addAll(listCheck);
        notifyDataSetChanged();
    }

    public void setIsCheckAll(boolean isCheckAll) {
        listCheck.clear();
        if (isCheckAll) {
            for (int i = 0; i < this.listImage.size(); i++) {
                listCheck.add(i);
            }
        }
        notifyDataSetChanged();
    }

    public void deleteData(List<FileEntity> tempList) {
        listImage.removeAll(tempList);
        Log.e("gfd", "删除后：" + listImage.size());
        notifyDataSetChanged();
    }

    public List<FileEntity> getListImage() {
        return listImage;
    }

    Activity mActivity;
    onCheckListener mOnCheckListener;


    public ImageShowAdapter(Activity mActivity, List<FileEntity> listImage, List<Integer> listCheck) {
        super();
        this.mActivity = mActivity;
        this.listCheck = listCheck;
        this.listImage = listImage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_list, parent, false);
        return new ImageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
            String path = listImage.get(position).getPath();
            Glide.with(mActivity)
                    .load(path)
                    .into(((ImageViewHolder) holder).iv_photo_filelist_pic);
            ConstraintLayout.LayoutParams llp = (ConstraintLayout.LayoutParams) ((ImageViewHolder) holder).iv_photo_filelist_pic.getLayoutParams();
            llp.width = (DeviceUtils.getScreenWidth() - DeviceUtils.dip2px(48)) / 3;
            llp.height = llp.width;
            ((ImageViewHolder) holder).iv_photo_filelist_pic.setLayoutParams(llp);
            ((ImageViewHolder) holder).iv_photo_filelist_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, PreviewImageActivity.class);
                    List<Image> list = new ArrayList<>();
                    for (int i = 0; i < listImage.size(); i++) {
                        Image image = new Image();
                        image.setPath(listImage.get(i).getPath());
                        image.setSize(NumberUtils.getLong(listImage.get(i).getSize()));
                        list.add(image);
                    }
                    intent.putExtra(ExtraConstant.PREVIEW_IMAGE_POSITION, position);
                    CleanAllFileScanUtil.preview_image_list=list;
//                    intent.putParcelableArrayListExtra(ExtraConstant.PREVIEW_IMAGE_DATA, (ArrayList<? extends Parcelable>) list);
                    intent.putExtra(ExtraConstant.PREVIEW_IMAGE_SELECT, (Serializable) listCheck);
                    mActivity.startActivityForResult(intent, 209);
                }
            });
            ((ImageViewHolder) holder).cb_choose.setBackgroundResource(listCheck.contains(position) ? R.drawable.icon_select : R.drawable.icon_unselect);
            ((ImageViewHolder) holder).rel_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!listCheck.contains(position)) {
                        listCheck.add(position);
                    } else {
                        listCheck.remove(listCheck.indexOf(position));
                    }
                    ((ImageViewHolder) holder).cb_choose.setBackgroundResource(listCheck.contains(position) ? R.drawable.icon_select : R.drawable.icon_unselect);
                    if (mOnCheckListener != null)
                        mOnCheckListener.onCheck(listImage, position);
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
        public TextView cb_choose;
        public RelativeLayout rel_select;

        public ImageViewHolder(View itemView) {
            super(itemView);
            iv_photo_filelist_pic = (ImageView) itemView.findViewById(R.id.iv_photo_filelist_pic);
            cb_choose = (TextView) itemView.findViewById(R.id.cb_choose);
            rel_select = (RelativeLayout) itemView.findViewById(R.id.rel_select);
        }
    }


    public interface onCheckListener {
        public void onCheck(List<FileEntity> listFile, int pos);
    }

    public void setmOnCheckListener(onCheckListener mOnCheckListener) {
        this.mOnCheckListener = mOnCheckListener;
    }
}
