package com.xiaoniu.cleanking.ui.news.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.bean.NewsItemInfo;
import com.xiaoniu.cleanking.ui.main.bean.NewsItemInfoRuishi;
import com.xiaoniu.cleanking.ui.main.bean.VideoItemInfo;
import com.xiaoniu.cleanking.utils.ImageUtil;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.common.base.SimpleWebActivity;
import com.xiaoniu.common.utils.DateUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.widget.xrecyclerview.CommonRecyclerAdapter;
import com.xiaoniu.common.widget.xrecyclerview.CommonViewHolder;

import java.util.Date;
import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * 头条资讯适配器
 */
public class NewsListAdapter extends CommonRecyclerAdapter<Object> {
    public NewsListAdapter(Context context) {
        super(context, new NewsItemTypeSupport());
    }

    @Override
    public void convert(RecyclerView.ViewHolder holder, Object itemData, int position) {
        CommonViewHolder commonHolder = (CommonViewHolder) holder;
        int viewType = getItemViewType(position);
        if (viewType == 0) {//视频
            VideoItemInfo itemInfo = (VideoItemInfo) itemData;
            JzvdStd jzvdStd = commonHolder.getView(R.id.videoplayer);
            jzvdStd.setUp(itemInfo.url, itemInfo.title, Jzvd.SCREEN_NORMAL);
            jzvdStd.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageUtil.display(itemInfo.coverImage, jzvdStd.thumbImageView);
        } else {
            final NewsItemInfoRuishi itemInfo = (NewsItemInfoRuishi) itemData;
            ((TextView) commonHolder.getView(R.id.tvTitle)).setText(itemInfo.getTitle());
            ((TextView) commonHolder.getView(R.id.tvDate)).setText(DateUtils.getShortTime(DateUtils.getTimeLong(itemInfo.getUpdate_time())));
            ((TextView) commonHolder.getView(R.id.tvSource)).setText(itemInfo.getSource());
            if (viewType == 1) {//一张图
                ImageUtil.display(itemInfo.getImages().get(0).getUrl(), (commonHolder.getView(R.id.ivPic1)));
            } else if (viewType == 2) {//两张图
                ImageUtil.display(itemInfo.getImages().get(0).getUrl(), (commonHolder.getView(R.id.ivPic1)));
                ImageUtil.display(itemInfo.getImages().get(1).getUrl(), (commonHolder.getView(R.id.ivPic2)));
            } else if (viewType == 3) {//三张图
                ImageUtil.display(itemInfo.getImages().get(0).getUrl(), (commonHolder.getView(R.id.ivPic1)));
                ImageUtil.display(itemInfo.getImages().get(1).getUrl(), (commonHolder.getView(R.id.ivPic2)));
                ImageUtil.display(itemInfo.getImages().get(2).getUrl(), (commonHolder.getView(R.id.ivPic3)));
            }

            commonHolder.itemView.setOnClickListener(v -> {
                        SimpleWebActivity.startActivity(mContext, itemInfo.getClk_url(), mContext.getString(R.string.app_name));
                        //埋点
                        if (position > 11)
                            return;
                        StatisticsUtils.trackClickNewsItem("information_page_news_click", "资讯页新闻点击", "selected_page", "information_page", itemInfo.getTitle(), itemInfo.getId(), position + 1);
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
            if (itemData instanceof VideoItemInfo) {
                return 0;
            } else if (itemData instanceof NewsItemInfoRuishi) {
                int size = 0;
                if(itemData!=null){
                    List<NewsItemInfoRuishi.ImagesBean> list = ((NewsItemInfoRuishi) itemData).getImages();
                    if(list!=null){
                        size= list.size();
                    }
                }
                return size <= 3 ? size : 3;
            }
            return -1;
        }

        @Override
        public int getLayoutId(int viewType) {
            if (viewType == 0) {//视频
                return R.layout.item_news_video;
            } else if (viewType == 1) {//一张图
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
}
