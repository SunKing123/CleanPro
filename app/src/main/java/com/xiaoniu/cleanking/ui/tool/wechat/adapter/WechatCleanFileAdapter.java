package com.xiaoniu.cleanking.ui.tool.wechat.adapter;

import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.tool.qq.adapter.QQCleanFileAdapter;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxItemInfo;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WechatCleanFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<CleanWxItemInfo> listImage = new ArrayList<>();


    public void deleteData(List<CleanWxItemInfo> tempList) {
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


    public ArrayList<CleanWxItemInfo> getListImage() {
        return listImage;
    }

    Activity mActivity;
    onCheckListener mOnCheckListener;


    public WechatCleanFileAdapter(Activity mActivity, ArrayList<CleanWxItemInfo> listImage) {
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
            ((ImageViewHolder) holder).iv_photo_filelist_pic.setImageResource(getImgRes(listImage.get(position).getFile().getName()));
            ((ImageViewHolder) holder).tv_name.setText(listImage.get(position).getFile().getName());
            ((ImageViewHolder) holder).tv_time.setText(listImage.get(position).getStringDay());
            ((ImageViewHolder) holder).tv_size.setText(CleanAllFileScanUtil.byte2FitSizeOne(listImage.get(position).getFileSize()));
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
                    AndroidUtil.openFileSafe(mActivity, String.valueOf(listImage.get(position).getFile()));
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return listImage.size();
    }

    //zip rar txt xlsx pdf docx pptx
    public int getImgRes(String filename) {
        int resImg = R.mipmap.icon_file;
        if (filename.contains("zip")) {
            resImg = R.mipmap.icon_zip;
        } else if (filename.contains("rar")) {
            resImg = R.mipmap.icon_zip;
        } else if (filename.contains("txt")) {
            resImg = R.mipmap.icon_txt;
        } else if (filename.contains("xls")) {
            resImg = R.mipmap.icon_excel;
        } else if (filename.contains("pdf")) {
            resImg = R.mipmap.icon_pdf;
        } else if (filename.contains("docx")) {
            resImg = R.mipmap.icon_file;
        } else if (filename.contains("ppt")) {
            resImg = R.mipmap.icon_ppt;
        }
        return resImg;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_photo_filelist_pic;
        public TextView tv_select;
        public TextView tv_name;
        public TextView tv_size;
        public TextView tv_time;
        public ConstraintLayout conslayout;

        public ImageViewHolder(View itemView) {
            super(itemView);
            iv_photo_filelist_pic = (ImageView) itemView.findViewById(R.id.iv_photo_filelist_pic);
            tv_select = (TextView) itemView.findViewById(R.id.tv_select);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            conslayout = (ConstraintLayout) itemView.findViewById(R.id.conslayout);
        }
    }


    public interface onCheckListener {
        public void onCheck(List<CleanWxItemInfo> listFile, int pos);
    }

    public void setmOnCheckListener(onCheckListener mOnCheckListener) {
        this.mOnCheckListener = mOnCheckListener;
    }
}
