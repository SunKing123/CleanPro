package com.xiaoniu.cleanking.ui.news.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.qq.e.ads.nativ.NativeExpressADView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.enums.AdType;
import com.xiaoniu.cleanking.ad.interfaces.AdShowCallBack;
import com.xiaoniu.cleanking.ad.mvp.presenter.AdPresenter;
import com.xiaoniu.cleanking.ui.main.bean.NewsItemInfo;
import com.xiaoniu.cleanking.ui.main.bean.NewsPicInfo;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.ui.news.listener.OnClickNewsItemListener;
import com.xiaoniu.cleanking.utils.ImageUtil;
import com.xiaoniu.common.base.SimpleWebActivity;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.widget.xrecyclerview.CommonRecyclerAdapter;
import com.xiaoniu.common.widget.xrecyclerview.CommonViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 头条资讯适配器
 */
public class NewsListAdapter extends CommonRecyclerAdapter<Object> {

    private OnClickNewsItemListener mOnClickItemListener;

    public static final String adType = "AD";
    private AdPresenter presenter;

    //广告结果缓存
    private LinkedHashMap<Integer, View> adCache = new LinkedHashMap<>();
//    LinkedHashMap<Integer, WeakReference<View>> adCache = new LinkedHashMap<>();


    public NewsListAdapter(Context context) {
        super(context, new NewsItemTypeSupport());
        presenter = new AdPresenter();
    }

    public void setData(ArrayList<NewsItemInfo> datas) {
        if (datas != null && datas.size() > 0) {
            mDatas.clear();
            adCache.clear();
            mDatas.addAll(setAdList(datas));
            notifyDataSetChanged();
        }
    }

    public void addData(ArrayList<NewsItemInfo> datas) {
        if (datas != null && datas.size() > 0) {
            mDatas.addAll(setAdList(datas));
            notifyDataSetChanged();
        }
    }

    /**
     * 拼装广告数据
     *
     * @param datas
     * @return
     */
    private ArrayList<NewsItemInfo> setAdList(ArrayList<NewsItemInfo> datas) {
        ArrayList<NewsItemInfo> tempDatas = new ArrayList<>();
        for (int i = 1; i <= datas.size(); i++) {
            tempDatas.add(datas.get(i - 1));
            if (i % 3 == 0) {
                NewsItemInfo newsItemInfo = new NewsItemInfo();
                newsItemInfo.type = adType;
                tempDatas.add(newsItemInfo);
                getAd(mDatas.size() + (tempDatas.size() - 1));
            }
        }
        return tempDatas;
    }

    /**
     * 请求广告
     *
     * @param postion 当前页广告下标
     */
    private void getAd(int postion) {

        AdRequestParamentersBean adRequestParamentersBean = new AdRequestParamentersBean(PositionId.KEY_HOME_NEWS,
                PositionId.DRAW_ONE_CODE,
                mContext,
                AdType.Template,
                (int) ScreenUtils.getScreenWidthDp(mContext) - 10,
                0,
                postion);
        presenter.requestAd(adRequestParamentersBean, new AdShowCallBack() {

            @Override
            public void onAdListShowCallBack(int index, View view) {
                //ylh
                if (view instanceof NativeExpressADView) {
                    adCache.put(index, (NativeExpressADView) view);
                    notifyItemChanged(index + 1);
                }
                //csj
                else {
                    adCache.put(index, view);
                    notifyItemChanged(index + 1);
                }
            }

            @Override
            public void onCloseCallback(int index) {
                try {
                    if (adCache.get(index) == null) {
                        return;
                    }
                    adCache.remove(index);
                    notifyItemRemoved(index);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void convert(RecyclerView.ViewHolder holder, Object itemData, int position) {

        CommonViewHolder commonHolder = (CommonViewHolder) holder;
        int viewType = getItemViewType(position);
        if (viewType == 0) {//视频
        } else {
            final NewsItemInfo itemInfo = (NewsItemInfo) itemData;
            if (viewType == 1 || viewType == 2 || viewType == 3) {
                ((TextView) commonHolder.getView(R.id.tvTitle)).setText(itemInfo.topic);
                ((TextView) commonHolder.getView(R.id.tvDate)).setText(itemInfo.date);
                ((TextView) commonHolder.getView(R.id.tvSource)).setText(itemInfo.source);
            }
            if (viewType == 1) {//一张图
                ImageUtil.display(itemInfo.miniimg.get(0).src, (commonHolder.getView(R.id.ivPic1)));
            } else if (viewType == 2) {//两张图
                ImageUtil.display(itemInfo.miniimg.get(0).src, (commonHolder.getView(R.id.ivPic1)));
                ImageUtil.display(itemInfo.miniimg.get(1).src, (commonHolder.getView(R.id.ivPic2)));
            } else if (viewType == 3) {//三张图
                ImageUtil.display(itemInfo.miniimg.get(0).src, (commonHolder.getView(R.id.ivPic1)));
                ImageUtil.display(itemInfo.miniimg.get(1).src, (commonHolder.getView(R.id.ivPic2)));
                ImageUtil.display(itemInfo.miniimg.get(2).src, (commonHolder.getView(R.id.ivPic3)));
            } else if (viewType == 4) {
                FrameLayout adLayout = commonHolder.getView(R.id.ad_layout);

                if (adCache.get(position) != null && adLayout != null) {
                    adLayout.removeAllViews();
                    View adView = adCache.get(position);
                    if (adView.getParent() != null) {
                        ((ViewGroup) adView.getParent()).removeAllViews();
                    }
                    adLayout.addView(adView);
                }
            }

            commonHolder.itemView.setOnClickListener(v -> {
                        SimpleWebActivity.startActivity(mContext, itemInfo.url, mContext.getString(R.string.app_name));
                        if (mOnClickItemListener != null) {
                            mOnClickItemListener.onClickItem(position, itemInfo);
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
                //广告类型定义为4 start
                NewsItemInfo newsItemInfo = ((NewsItemInfo) itemData);
                if (NewsListAdapter.adType.equals(newsItemInfo.type)) {
                    return 4;
                }
                //广告类型定义为4 end
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
            } else if (viewType == 4) {
                return R.layout.item_news_ad_pic;
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

    public void setOnClickItemListener(OnClickNewsItemListener onClickItemListener) {
        this.mOnClickItemListener = onClickItemListener;
    }


}
