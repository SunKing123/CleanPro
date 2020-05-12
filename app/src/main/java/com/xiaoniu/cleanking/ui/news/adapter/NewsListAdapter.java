package com.xiaoniu.cleanking.ui.news.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.NewsItemInfo;
import com.xiaoniu.cleanking.ui.main.bean.NewsPicInfo;
import com.xiaoniu.cleanking.utils.ImageUtil;
import com.xiaoniu.common.base.SimpleWebActivity;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.widget.xrecyclerview.CommonRecyclerAdapter;
import com.xiaoniu.common.widget.xrecyclerview.CommonViewHolder;

import java.util.ArrayList;

/**
 * 头条资讯适配器
 */
public class NewsListAdapter extends CommonRecyclerAdapter<Object> {

    private OnClickItemListener mOnClickItemListener;


    public NewsListAdapter(Context context) {
        super(context, new NewsItemTypeSupport());
    }

    @Override
    public void convert(RecyclerView.ViewHolder holder, Object itemData, int position) {
        CommonViewHolder commonHolder = (CommonViewHolder) holder;
        int viewType = getItemViewType(position);
        if (viewType == 0) {//视频
        } else {
            final NewsItemInfo itemInfo = (NewsItemInfo) itemData;
            ((TextView) commonHolder.getView(R.id.tvTitle)).setText(itemInfo.topic);
            ((TextView) commonHolder.getView(R.id.tvDate)).setText(itemInfo.date);
            ((TextView) commonHolder.getView(R.id.tvSource)).setText(itemInfo.source);
            if (viewType == 1) {//一张图
                ImageUtil.display(itemInfo.miniimg.get(0).src, (commonHolder.getView(R.id.ivPic1)));
            } else if (viewType == 2) {//两张图
                ImageUtil.display(itemInfo.miniimg.get(0).src, (commonHolder.getView(R.id.ivPic1)));
                ImageUtil.display(itemInfo.miniimg.get(1).src, (commonHolder.getView(R.id.ivPic2)));
            } else if (viewType == 3) {//三张图
                ImageUtil.display(itemInfo.miniimg.get(0).src, (commonHolder.getView(R.id.ivPic1)));
                ImageUtil.display(itemInfo.miniimg.get(1).src, (commonHolder.getView(R.id.ivPic2)));
                ImageUtil.display(itemInfo.miniimg.get(2).src, (commonHolder.getView(R.id.ivPic3)));
            }

            commonHolder.itemView.setOnClickListener(v -> {
                        SimpleWebActivity.startActivity(mContext, itemInfo.url, mContext.getString(R.string.app_name));
                        if (mOnClickItemListener != null) {
                            mOnClickItemListener.onClickItem(itemInfo);
                        }
                        //埋点
                        if (position > 11)
                            return;
                        StatisticsUtils.trackClickNewsItem("information_page_news_click", "资讯页新闻点击", "selected_page", "information_page", itemInfo.topic, itemInfo.rowkey, position + 1);
                    }
            );
        }
    }

    @Override
    public RecyclerView.ViewHolder attachToViewHolder(int viewType, View itemView) {
        return new CommonViewHolder(itemView);
    }

    //多类型Item View 展示
    public static class NewsItemTypeSupport implements MultiItemTypeSupport<Object> {

        @Override
        public int getItemViewType(int position, Object itemData) {
            if (itemData instanceof NewsItemInfo) {
                int size = 0;
                ArrayList<NewsPicInfo> list = ((NewsItemInfo) itemData).miniimg;
                if (list != null) {
                    size = list.size();
                }
                return Math.min(size, 3);
            }
            return -1;
        }

        @Override
        public int getLayoutId(int viewType) {
            if (viewType == 1) {//一张图
                return R.layout.item_news_one_pic;
            } else if (viewType == 2) {//两张图
                return R.layout.item_news_two_pic;
            } else if (viewType == 3) {//三张图
                return R.layout.item_news_three_pic;
            }
            return -1;
        }

        @Override
        public boolean isFullSpan(int position) {
            return false;
        }

        @Override
        public int getSpanSize(int position, int spanCount) {
            return 0;
        }
    }

    public void setOnClickItemListener(OnClickItemListener mOnClickItemListener) {
        this.mOnClickItemListener = mOnClickItemListener;
    }

    public interface OnClickItemListener {

        void onClickItem(NewsItemInfo itemInfo);

    }
}
