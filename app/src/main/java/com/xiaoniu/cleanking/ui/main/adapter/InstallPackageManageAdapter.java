package com.xiaoniu.cleanking.ui.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.utils.DateUtils;
import com.xiaoniu.cleanking.utils.FileSizeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 安装包管理
 * Created by lang.chen on 2019/7/2
 */
public class InstallPackageManageAdapter extends RecyclerView.Adapter {


    private List<AppInfoBean> mLists = new ArrayList<>();

    private LayoutInflater mInflater;

    //已安装，未安装Tab 区分，分别未 0 ,1
    private int mTabType;

    private Context mContext;

    private OnCheckListener onCheckListener;

    //是否显示子标题 默认显示
    private boolean mIsShowSubTitle=true;

    public InstallPackageManageAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_install_package_manage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AppInfoBean appInfoBean = getLists().get(position);

        if (holder.getClass() == ViewHolder.class) {
            ViewHolder viewHolder = (ViewHolder) holder;
            Glide.with(mContext).load(appInfoBean.icon).into(viewHolder.mImgIcon);
            viewHolder.mTxtName.setText(appInfoBean.name);
            if(appInfoBean.packageSize==0){
                viewHolder.mTxtSize.setVisibility(View.INVISIBLE);
            }else {
                viewHolder.mTxtTime.setVisibility(View.VISIBLE);
                viewHolder.mTxtSize.setText(FileSizeUtils.formatFileSize(appInfoBean.packageSize));
            }
            if(mIsShowSubTitle==false){
                viewHolder.mTxtTime.setVisibility(View.GONE);
            }else {
                viewHolder.mTxtTime.setVisibility(View.VISIBLE);
                if (mTabType == 0) {
                    viewHolder.mTxtTime.setText(DateUtils.timestampToPatternTime(appInfoBean.installTime, "yyyy-MM-dd HH:mm"));
                } else {
                    viewHolder.mTxtTime.setText("版本:" + appInfoBean.versionName);
                }
            }
            viewHolder.mCheckSelect.setSelected(appInfoBean.isSelect);
            viewHolder.mCheckSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onCheckListener) {
                        onCheckListener.onCheck(appInfoBean.packageName, !appInfoBean.isSelect);
                    }
                }
            });
        }
    }


    public void clear() {
        mLists.clear();
    }

    public List<AppInfoBean> getLists() {
        return mLists;
    }

    public void modifyList(List<AppInfoBean> appInfoBeans) {
        if (null != appInfoBeans) {
            mLists.addAll(appInfoBeans);
            notifyDataSetChanged();
        }
    }

    public void setIsShowSubTitle(boolean isShowSubTitle) {
        this.mIsShowSubTitle = isShowSubTitle;
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }


    public interface  OnCheckListener{
        void onCheck(String id,boolean  isChecked);
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImgIcon;
        //名称
        private TextView mTxtName;
        /**
         * 这个地方有可能是时间，也有可能是版本号
         */
        private TextView mTxtTime;
        private TextView mTxtSize;
        private ImageButton mCheckSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            mImgIcon = itemView.findViewById(R.id.img_icon);
            mTxtTime=itemView.findViewById(R.id.txt_time);
            mTxtName = itemView.findViewById(R.id.txt_name);
            mTxtSize = itemView.findViewById(R.id.txt_size);
            mCheckSelect = itemView.findViewById(R.id.check_select);

        }
    }

    public void setOnCheckListener(InstallPackageManageAdapter.OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

    public void setTabType(int type) {
        this.mTabType = type;
    }
}
