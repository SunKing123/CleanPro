package com.xiaoniu.cleanking.ui.main.event;

/**
 * @author zzh
 * @date 2020/8/6 15
 * @mail：zhengzhihao@xiaoniuhy.com
 */
public class ExposureEvent {
    public ExposureEvent(int exposureTimes) {
        this.exposureTimes = exposureTimes;
    }

    int exposureTimes ;

    public int getExposureTimes() {
        return exposureTimes;
    }

    public void setExposureTimes(int exposureTimes) {
        this.exposureTimes = exposureTimes;
    }
}
