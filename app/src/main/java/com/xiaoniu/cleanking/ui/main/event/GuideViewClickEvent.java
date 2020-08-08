package com.xiaoniu.cleanking.ui.main.event;

/**
 * @author zzh
 * @date 2020/8/6 15
 * @mail：zhengzhihao@xiaoniuhy.com
 */
public class GuideViewClickEvent {
    private int guideIndex;

    public GuideViewClickEvent(int guideIndex) {
        this.guideIndex = guideIndex;
    }

    public int getGuideIndex() {
        return guideIndex;
    }

    public void setGuideIndex(int guideIndex) {
        this.guideIndex = guideIndex;
    }
}
