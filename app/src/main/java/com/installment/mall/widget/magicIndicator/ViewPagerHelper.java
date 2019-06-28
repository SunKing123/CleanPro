package com.installment.mall.widget.magicIndicator;

import android.support.v4.view.ViewPager;

/**
 * 简化和ViewPager绑定
 * Created by hackware on 2016/8/17.
 */

public class ViewPagerHelper {
    public static void bind(final MagicIndicator magicIndicator, ViewPager viewPager) {

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(magicIndicator != null){
                    magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

            }

            @Override
            public void onPageSelected(int position) {
                if(magicIndicator != null){
                    magicIndicator.onPageSelected(position);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(magicIndicator != null){
                    magicIndicator.onPageScrollStateChanged(state);
                }

            }
        });
    }


    /**
     * add by wuchangbin 将viewpager的3个回调对外开放
     * @param magicIndicator
     * @param viewPager
     * @param pageChangeListener
     */
    public static void bind(final MagicIndicator magicIndicator, ViewPager viewPager, final ViewPager.OnPageChangeListener pageChangeListener) {

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(magicIndicator != null){
                    magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
                if(pageChangeListener != null){
                    pageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if(magicIndicator != null){
                    magicIndicator.onPageSelected(position);
                }
                if(pageChangeListener != null){
                    pageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(magicIndicator != null){
                    magicIndicator.onPageScrollStateChanged(state);
                }
                if(pageChangeListener != null){
                    pageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
    }
}
