package com.xiaoniu.cleanking.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.BubbleConfig;
import com.xiaoniu.cleanking.utils.CollectionUtils;

/**
 * @author zhengzhihao
 * @date 2020/6/11 09
 * @mail：zhengzhihao@hellogeek.com
 * @des 限时奖励
 */
public class RewardLimiteView extends RelativeLayout {

    private Context mContext;
    private LuckBubbleView luckbub06, luckbub07, luckbub08, luckbub09;

    public RewardLimiteView(Context context) {
        super(context);
        initView(context);
    }

    public RewardLimiteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RewardLimiteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

    }

    /**
     * 初始化布局
     *
     * @return
     */
    public void initView(Context context) {
        mContext = context;
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_reward_limit, this, true);
        luckbub06 = v.findViewById(R.id.iv_golde_06);
        luckbub07 = v.findViewById(R.id.iv_golde_07);
        luckbub08 = v.findViewById(R.id.iv_golde_08);
        luckbub09 = v.findViewById(R.id.iv_golde_09);
        setViewLayoutParms();
    }

    public void setViewLayoutParms() {

    }


    /**
     * 头部金币刷新
     *
     * @param dataBean
     */
    public void refBubbleView(BubbleConfig dataBean) {
        luckbub06.setDataCheckToShow(null);
        luckbub07.setDataCheckToShow(null);
        luckbub08.setDataCheckToShow(null);
        luckbub09.setDataCheckToShow(null);
        if (null != dataBean && !CollectionUtils.isEmpty(dataBean.getData())) {
            for (BubbleConfig.DataBean dataBean1 : dataBean.getData()) {
                if (dataBean1 == null) {
                    return;
                }
                switch (dataBean1.getLocationNum()) {
                    case 6://金币位1
                        luckbub06.setDataCheckToShow(dataBean1);
                        break;
                    case 7://金币位2
                        luckbub07.setDataCheckToShow(dataBean1);
                        break;
                    case 8://金币位3
                        luckbub08.setDataCheckToShow(dataBean1);
                        break;
                    case 9://金币位4
                        luckbub09.setDataCheckToShow(dataBean1);
                        break;

                }
            }
        } else {
            setVisibility(GONE);
        }

    }


}
