package com.xiaoniu.cleanking.ui.main.adapter;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.callback.OnItemCheckedListener;
import com.xiaoniu.cleanking.ui.main.bean.FirstLevelEntity;
import com.xiaoniu.cleanking.ui.main.bean.SecondLevelEntity;
import com.xiaoniu.cleanking.ui.main.bean.ThirdLevelEntity;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileUtils;

import java.util.List;

public class CleanExpandAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * 第一级标题
     */
    public static final int TYPE_LEVEL_1 = 1;
    /**
     * 第二级
     */
    public static final int TYPE_LEVEL_2 = 2;
    /**
     * 第三级
     */
    public static final int TYPE_LEVEL_3 = 3;

    private OnItemCheckedListener mOnItemSelectListener;

    public void setOnItemSelectListener(OnItemCheckedListener onItemSelectListener) {
        mOnItemSelectListener = onItemSelectListener;
    }

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public CleanExpandAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_1, R.layout.item_big_file_level_1);
        addItemType(TYPE_LEVEL_2, R.layout.item_big_file_level_2);
        addItemType(TYPE_LEVEL_3, R.layout.item_big_file_level_3);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case TYPE_LEVEL_1:
                //第一级目录
                FirstLevelEntity entity = (FirstLevelEntity) item;
                helper.setText(R.id.junk_size, CleanUtil.formatShortFileSize(AppApplication.getInstance(), entity.getTotal()));
                helper.itemView.setOnClickListener(v -> {
                    int pos = helper.getAdapterPosition();
                    if (entity.isExpanded()) {
                        collapse(pos);
                    } else {
                        expand(pos);
                    }
                });
                helper.setImageResource(R.id.image_arrow, entity.isExpanded() ? R.mipmap.arrow_up : R.mipmap.arrow_down);
                break;
            case TYPE_LEVEL_2:
                //第二级目录
                final SecondLevelEntity entity2 = (SecondLevelEntity) item;
                helper.setText(R.id.text_app_name, entity2.getName())
                        .setText(R.id.junk_size, CleanUtil.formatShortFileSize(AppApplication.getInstance(), entity2.getTotalSize()));
                //设置二级目录图标
                switch (entity2.getType()) {
                    case SecondLevelEntity.TYPE_IMAGE:
                        helper.setImageResource(R.id.app_logo, R.mipmap.icon_clean_image);
                        break;
                    case SecondLevelEntity.TYPE_MUSIC:
                        helper.setImageResource(R.id.app_logo, R.mipmap.icon_clean_music);
                        break;
                    case SecondLevelEntity.TYPE_VIDEO:
                        helper.setImageResource(R.id.app_logo, R.mipmap.icon_clean_video);
                        break;
                    case SecondLevelEntity.TYPE_WORD:
                        helper.setImageResource(R.id.app_logo, R.mipmap.icon_clean_word);
                        break;
                    case SecondLevelEntity.TYPE_ZIP:
                        helper.setImageResource(R.id.app_logo, R.mipmap.icon_clean_zip);
                        break;
                    default:
                }
                helper.itemView.setOnClickListener(v -> {
                    int pos = helper.getAdapterPosition();
                    if (entity2.isExpanded()) {
                        collapse(pos);
                    } else {
                        expand(pos);
                    }
                });
                break;
            case TYPE_LEVEL_3:
                final ThirdLevelEntity entity3 = (ThirdLevelEntity) item;
                helper.setText(R.id.text_app_name, entity3.getFile().getName())
                        .setText(R.id.junk_size, CleanUtil.formatShortFileSize(AppApplication.getInstance(), entity3.getFile().length()))
                        .setText(R.id.text_version, entity3.getContent());
                helper.getView(R.id.icon_check).setSelected(entity3.isChecked());
                FileUtils.showIconByFile(helper.getView(R.id.app_logo),entity3.getFile());
                helper.setOnClickListener(R.id.icon_check, v -> {
                    boolean checked = entity3.isChecked();
                    if (checked) {
                        //取消勾选
                        entity3.setChecked(false);
                        helper.getView(R.id.icon_check).setSelected(false);
                        if (mOnItemSelectListener != null) {
                            mOnItemSelectListener.onItemChecked(false, entity3);
                        }
                    } else {
                        showConfirmDialog(entity3);
                    }
                });
                break;
            default:
        }
    }

    /**
     * 确认勾选弹窗
     */
    public void showConfirmDialog(ThirdLevelEntity entity) {
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
        mTextConfirm.setOnClickListener(v -> {
            entity.setChecked(true);
            dialog.dismiss();
            notifyDataSetChanged();
            if (mOnItemSelectListener != null) {
                mOnItemSelectListener.onItemChecked(true, entity);
            }
        });
        dialog.setContentView(view);
        if (dialog.isShowing()) {
            return;
        }
        dialog.show();
    }
}
