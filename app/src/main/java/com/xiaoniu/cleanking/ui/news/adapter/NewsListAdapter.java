package com.xiaoniu.cleanking.ui.news.adapter;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.NewsItemInfo;
import com.xiaoniu.cleanking.ui.main.bean.NewsPicInfo;
import com.xiaoniu.cleanking.ui.main.bean.VideoItemInfo;
import com.xiaoniu.cleanking.ui.newclean.view.RoundProgressBar;
import com.xiaoniu.cleanking.utils.ImageUtil;
import com.xiaoniu.cleanking.widget.CountDownView;
import com.xiaoniu.common.base.SimpleWebActivity;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.widget.xrecyclerview.CommonRecyclerAdapter;
import com.xiaoniu.common.widget.xrecyclerview.CommonViewHolder;

import java.util.ArrayList;
import java.util.Map;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import kotlin.reflect.jvm.internal.impl.resolve.constants.StringValue;

/**
 * 头条资讯适配器
 */
public class NewsListAdapter extends CommonRecyclerAdapter<Object> {
    AdManager adManager;
    Activity mActivity;
    public NewsListAdapter(Context context) {
        super(context, new NewsItemTypeSupport());
    }

    public NewsListAdapter(Context context,AdManager am,Activity ac) {
        super(context, new NewsItemTypeSupport());
        adManager = am;
        mActivity = ac;
    }

    @Override
    public void convert(RecyclerView.ViewHolder holder, Object itemData, int position) {
        CommonViewHolder commonHolder = (CommonViewHolder) holder;
        int viewType = getItemViewType(position);
        if (viewType == 0) {//视频
            VideoItemInfo itemInfo = (VideoItemInfo) itemData;
            RelativeLayout linAdContainer = commonHolder.getView(R.id.lin_ad_container);//广告加载
            TextView tv_viewed_num =commonHolder.getView(R.id.tv_viewed_num);//观看数量
            TextView  tv_zan_num = commonHolder.getView(R.id.tv_zan_num);//点赞数量
            TextView tv_collection_num = commonHolder.getView(R.id.tv_collection_num);//收藏数量
            CountDownView closeBtn = commonHolder.getView(R.id.rp_close_view);

            tv_collection_num.setText(String.valueOf(itemInfo.collectTimes));
            tv_viewed_num.setText(mContext.getString(R.string.watch_times,String.valueOf(itemInfo.watchedTimes)));
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
                    insertAd(linAdContainer,"newlist_2_1",closeBtn,View.VISIBLE);
                }
            });

            int showRate = 3;   //间隔几条展示；
            if (null != AppHolder.getInstance().getInsertAdSwitchmap()) {
                Map<String, InsertAdSwitchInfoList.DataBean> map = AppHolder.getInstance().getInsertAdSwitchmap();
                showRate = null != map.get("page_video_end_screen") ? 3 : map.get("page_video_end_screen").getShowRate();
            }
            if ((position + 1) % showRate == 0) {//每间隔showRate播放
                //newlist_2_1   第二屏幕广告样式
                insertAd(linAdContainer,"newlist_2_1",closeBtn,View.GONE);
            }else{
                linAdContainer.removeAllViews();
                linAdContainer.setVisibility(View.GONE);
                closeBtn.setVisibility(View.GONE);
            }
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
                int size = 0;
                if (itemData != null) {
                    ArrayList<NewsPicInfo> list = ((NewsItemInfo) itemData).miniimg;
                    if (list != null) {
                        size = list.size();
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


    /**
     * 插入广告
     */
    public void insertAd(RelativeLayout container,String positionId,CountDownView closeBtn,int visiable){

        if (null != container && null != adManager && null != mActivity) {
            adManager.loadAd(mActivity,positionId , new AdListener() {
                @Override
                public void adSuccess(AdInfo info) {
                    if (null != container && null != closeBtn) {

                        View adView = adManager.getAdView();
                        if (adView != null) {
                            if (adView.getParent() != null) {
                                ((ViewGroup) adView.getParent()).removeView(adView);
                            }
                            container.removeAllViews();
                            container.addView(adView);
                            container.setVisibility(View.VISIBLE);
                            if (visiable == View.VISIBLE) {
                                closeBtn.setMillis(15000,1000);
                                closeBtn.setVisibility(visiable);
                                closeBtn.start();
                                closeBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(closeBtn!=null)
                                            closeBtn.setVisibility(View.GONE);
                                        if(container!=null)
                                            container.setVisibility(View.GONE);
                                    }
                                });
                                closeBtn.setOnCountDownListener(new CountDownView.OnCountDownListener() {
                                    @Override
                                    public void onCountDownFinish() {
                                        if(closeBtn!=null)
                                            closeBtn.setVisibility(View.GONE);
                                        if(container!=null)
                                            container.setVisibility(View.GONE);
                                    }
                                });

                            }
                        }
                    }
                }

                @Override
                public void adExposed(AdInfo info) {

                }

                @Override
                public void adClicked(AdInfo info) {

                }

                @Override
                public void adError(int errorCode, String errorMsg) {

                }
            });
        }
    }


}
