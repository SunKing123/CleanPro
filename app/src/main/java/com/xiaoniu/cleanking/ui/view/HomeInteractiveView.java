package com.xiaoniu.cleanking.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.bean.InteractionSwitchList;
import com.xiaoniu.cleanking.utils.GlideUtils;

import java.util.List;

/**
 * Created by xinxiaolong on 2020/7/1.
 * email：xinxiaolong123@foxmail.com
 */
public class HomeInteractiveView extends androidx.appcompat.widget.AppCompatImageView {

    List<InteractionSwitchList.DataBean.SwitchActiveLineDTOList> dataList;

    int index = -1;

    public HomeInteractiveView(Context context) {
        super(context);
    }

    public HomeInteractiveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeInteractiveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDataList(List<InteractionSwitchList.DataBean.SwitchActiveLineDTOList> dataList) {
        this.dataList = dataList;
    }

    public void loadNextDrawable() {
        index = getNextIndex();
        if (index == -1) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);
        GlideUtils.loadGif(getContext(), dataList.get(index).getImgUrl(), this, 10000);
    }

    private int getNextIndex() {
        if (dataList == null || dataList.size() == 0) {
            return -1;
        }
        index++;
        return index >= dataList.size() ? 0 : index;
    }


    public void setClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index != -1 && dataList != null)
                    onClickListener.onClick(dataList.get(index));
            }
        });
    }

    OnClickListener onClickListener;

    public interface OnClickListener {
        void onClick(InteractionSwitchList.DataBean.SwitchActiveLineDTOList data);
    }
}
