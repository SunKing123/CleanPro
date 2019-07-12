package com.xiaoniu.cleanking.ui.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 加速白名单添加
 * Created by lang.chen on 2019/7/2
 */
public class WhiteListAddAdapter extends RecyclerView.Adapter {


    private List<AppInfoBean> mLists = new ArrayList<>();

    private LayoutInflater mInflater;


    private Context mContext;

    private OnCheckListener onCheckListener;

    public WhiteListAddAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_white_list_add, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AppInfoBean appInfoBean = getLists().get(position);

        if (holder.getClass() == ViewHolder.class) {
            ViewHolder viewHolder = (ViewHolder) holder;
            Glide.with(mContext).load(appInfoBean.icon).into(viewHolder.mImgIcon);
            viewHolder.mTxtName.setText(appInfoBean.name);
            viewHolder.mImgCheck.setSelected(appInfoBean.isSelect);

            viewHolder.mLLCheck.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public int getItemCount() {
        return mLists.size();
    }


    public interface OnCheckListener {
        void onCheck(String id, boolean isChecked);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImgIcon;
        //名称
        private TextView mTxtName;
        private ImageButton mImgCheck;
        //
        private LinearLayout mLLCheck;
        public ViewHolder(View itemView) {
            super(itemView);
            mImgIcon = itemView.findViewById(R.id.img_icon);
            mTxtName = itemView.findViewById(R.id.txt_name);
            mImgCheck = itemView.findViewById(R.id.check_select);
            mLLCheck=itemView.findViewById(R.id.ll_check);
        }
    }

    public void setOnCheckListener(WhiteListAddAdapter.OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

}
