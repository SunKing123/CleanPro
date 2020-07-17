package com.xiaoniu.cleanking.ui.news.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.comm.jksdk.ad.listener.AdManager;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.NewsItemInfo;
import com.xiaoniu.cleanking.ui.main.bean.NewsPicInfo;
import com.xiaoniu.cleanking.ui.main.bean.NewsType;
import com.xiaoniu.cleanking.ui.main.bean.VideoItemInfo;
import com.xiaoniu.cleanking.utils.ImageUtil;
import com.xiaoniu.cleanking.widget.CountDownView;
import com.xiaoniu.common.base.SimpleWebActivity;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.widget.xrecyclerview.CommonRecyclerAdapter;
import com.xiaoniu.common.widget.xrecyclerview.CommonViewHolder;

import java.util.ArrayList;
import java.util.Map;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * 头条资讯适配器
 */
public class NewsListAdapter extends CommonRecyclerAdapter<Object> {
    Activity mActivity;
    String pageName = "";

    public NewsListAdapter(Context context) {
        super(context, new NewsItemTypeSupport());
    }

    public NewsListAdapter(Context context,Activity ac, String name) {
        super(context, new NewsItemTypeSupport());
        mActivity = ac;
        pageName = name;
    }

    @Override
    public void convert(RecyclerView.ViewHolder holder, Object itemData, int position) {
        CommonViewHolder commonHolder = (CommonViewHolder) holder;
        int viewType = getItemViewType(position);
        if (viewType == 0) {//视频
            VideoItemInfo itemInfo = (VideoItemInfo) itemData;
            RelativeLayout linAdContainer = commonHolder.getView(R.id.lin_ad_container);//广告加载
            TextView tv_viewed_num = commonHolder.getView(R.id.tv_viewed_num);//观看数量
            TextView tv_zan_num = commonHolder.getView(R.id.tv_zan_num);//点赞数量
            TextView tv_collection_num = commonHolder.getView(R.id.tv_collection_num);//收藏数量
            CountDownView closeBtn = commonHolder.getView(R.id.rp_close_view);

            tv_collection_num.setText(String.valueOf(itemInfo.collectTimes));
            tv_viewed_num.setText(mContext.getString(R.string.watch_times, itemInfo.watchTimesStr));
            tv_zan_num.setText(String.valueOf(itemInfo.starTimes));

            JzvdStd jzvdStd = commonHolder.getView(R.id.videoplayer);
            jzvdStd.setUp(itemInfo.url, itemInfo.title, Jzvd.SCREEN_NORMAL);
            jzvdStd.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageUtil.display(itemInfo.coverImage, jzvdStd.thumbImageView);
            jzvdStd.setCallBack(new JzvdStd.ThumbImageClickCallBack() {
                @Override
                public void clickCall() {
                    StatisticsUtils.trackClickNewsItem("information_page_video_click", "资讯页视频点击", "selected_page", "information_page", itemInfo.title, itemInfo.videoId, position + 1);
                }

                @Override
                public void completed() {
                    //newlist_2_1   第二屏幕广告样式
                    if (NetworkUtils.isNetConnected()) {
                    }

                }
            });

            int showRate = 3;   //间隔几条展示；
            if (null != AppHolder.getInstance().getInsertAdSwitchMap()) {
                Map<String, InsertAdSwitchInfoList.DataBean> map = AppHolder.getInstance().getInsertAdSwitchMap();
                if (null != map.get("page_video_end_screen")) {
                    InsertAdSwitchInfoList.DataBean dataBean = map.get("page_video_end_screen");
                    if (dataBean.isOpen()) {
                        showRate = map.get("page_video_end_screen").getShowRate();
                        if ((position + 1) % (showRate + 1) == 0) {//每间隔showRate播放
                            //newlist_2_1   第二屏幕广告样式
                        } else {
                            linAdContainer.removeAllViews();
                            linAdContainer.setVisibility(View.GONE);
                            closeBtn.setVisibility(View.GONE);
                        }
                    } else {  //广告关闭
                        linAdContainer.removeAllViews();
                        linAdContainer.setVisibility(View.GONE);
                        closeBtn.setVisibility(View.GONE);
                    }
                }

            }

        } else {
            final NewsItemInfo itemInfo = (NewsItemInfo) itemData;

            if (viewType == 1) {//一张图
                ImageUtil.display(itemInfo.miniimg.get(0).src, (commonHolder.getView(R.id.ivPic1)));
                ((TextView) commonHolder.getView(R.id.tvTitle)).setText(itemInfo.topic);
                ((TextView) commonHolder.getView(R.id.tvDate)).setText(itemInfo.date);
                ((TextView) commonHolder.getView(R.id.tvSource)).setText(itemInfo.source);
            } else if (viewType == 2) {//两张图
                ImageUtil.display(itemInfo.miniimg.get(0).src, (commonHolder.getView(R.id.ivPic1)));
                ImageUtil.display(itemInfo.miniimg.get(1).src, (commonHolder.getView(R.id.ivPic2)));
                ((TextView) commonHolder.getView(R.id.tvTitle)).setText(itemInfo.topic);
                ((TextView) commonHolder.getView(R.id.tvDate)).setText(itemInfo.date);
                ((TextView) commonHolder.getView(R.id.tvSource)).setText(itemInfo.source);
            } else if (viewType == 3) {//三张图
                ImageUtil.display(itemInfo.miniimg.get(0).src, (commonHolder.getView(R.id.ivPic1)));
                ImageUtil.display(itemInfo.miniimg.get(1).src, (commonHolder.getView(R.id.ivPic2)));
                ImageUtil.display(itemInfo.miniimg.get(2).src, (commonHolder.getView(R.id.ivPic3)));
                ((TextView) commonHolder.getView(R.id.tvTitle)).setText(itemInfo.topic);
                ((TextView) commonHolder.getView(R.id.tvDate)).setText(itemInfo.date);
                ((TextView) commonHolder.getView(R.id.tvSource)).setText(itemInfo.source);
            } else if (viewType == 4) { //资讯广告
                RelativeLayout linAdContainer = commonHolder.getView(R.id.lin_ad_container);//广告加载
                //newlist_2_1   第二屏幕广告样式
                String positionId = pageName.equals(NewsType.TOUTIAO.getName()) ? "newlist_1_1" : "newlist_other_1";
//                LogUtils.i("---zzh--" + positionId);
            }

            commonHolder.itemView.setOnClickListener(v -> {
                        SimpleWebActivity.startActivity(mContext, itemInfo.url, mContext.getString(R.string.app_name));
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
            if (itemData instanceof VideoItemInfo) {
                return 0;
            } else if (itemData instanceof NewsItemInfo) {
                if (((NewsItemInfo) itemData).isAd) {
                    return 4;
                } else {
                    int size = 0;
                    if (itemData != null) {
                        ArrayList<NewsPicInfo> list = ((NewsItemInfo) itemData).miniimg;
                        if (list != null) {
                            size = list.size();
                        }
                    }
                    return size <= 3 ? size : 3;
                }

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
            } else if (viewType == 4) { //广告显示
                return R.layout.item_news_insert_ad;
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
