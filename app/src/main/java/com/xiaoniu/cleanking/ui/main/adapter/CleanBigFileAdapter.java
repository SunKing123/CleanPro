package com.xiaoniu.cleanking.ui.main.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.callback.OnItemSelectListener;
import com.xiaoniu.cleanking.ui.main.bean.BigFileInfoEntity;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 清理大文件实体类
 */
public class CleanBigFileAdapter extends RecyclerView.Adapter<CleanBigFileAdapter.ViewHolder> {

    private List<BigFileInfoEntity> mList;

    private OnItemSelectListener mOnItemSelectListener;

    private Context mContext;

    public CleanBigFileAdapter(Context context) {
        mContext = context;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        mOnItemSelectListener = onItemSelectListener;
    }

    public void setData(List<BigFileInfoEntity> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_big_file, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BigFileInfoEntity infoEntity = mList.get(position);

        //头像
        FileUtils.showIconByFile(holder.mAppLogo,infoEntity.getFile());
        //名称
        holder.mTextAppName.setText(infoEntity.getFile().getName());
        //大小
        holder.mJunkSize.setText(CleanUtil.formatShortFileSize(AppApplication.getInstance(),infoEntity.getFile().length()));
        //描述
        holder.mTextVersion.setText(infoEntity.getContent());

        holder.mIconCheck.setSelected(infoEntity.isChecked());

        holder.mIconCheck.setOnClickListener(v->{
            boolean checked = infoEntity.isChecked();
            if (checked) {
                //取消勾选
                infoEntity.setChecked(false);
                holder.mIconCheck.setSelected(false);
                if (mOnItemSelectListener != null) {
                    mOnItemSelectListener.onCount();
                }
            }else {
                showConfirmDialog(infoEntity);
            }

        });
    }

    /**
     * 确认勾选弹窗
     */
    public void showConfirmDialog(BigFileInfoEntity entity) {
            //提示对话框
            final Dialog dialog = new Dialog(mContext, R.style.custom_dialog);
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_select_confirm, null);

            TextView mTextSize = view.findViewById(R.id.text_size);
            TextView mTextTrace = view.findViewById(R.id.text_trace);
            TextView mTextConfirm = view.findViewById(R.id.text_confirm);
            TextView mTextCancel = view.findViewById(R.id.text_cancel);

            //大小
            mTextSize.setText("大小：" + CleanUtil.formatShortFileSize(mContext, entity.getFile().length()));
            //路径
            mTextTrace.setText("来自：" + entity.getContent());

            mTextCancel.setOnClickListener(v -> dialog.dismiss());
            mTextConfirm.setOnClickListener(v->{
                entity.setChecked(true);
                dialog.dismiss();
                notifyDataSetChanged();
                if (mOnItemSelectListener != null) {
                    mOnItemSelectListener.onCount();
                }
            });
            dialog.setContentView(view);
            if (dialog.isShowing()) {
                return;
            }
            dialog.show();
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.app_logo)
        ImageView mAppLogo;
        @BindView(R.id.text_app_name)
        TextView mTextAppName;
        @BindView(R.id.text_version)
        TextView mTextVersion;
        @BindView(R.id.junk_size)
        TextView mJunkSize;
        @BindView(R.id.icon_check)
        ImageView mIconCheck;
        @BindView(R.id.layout_root)
        LinearLayout mLayoutRoot;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
