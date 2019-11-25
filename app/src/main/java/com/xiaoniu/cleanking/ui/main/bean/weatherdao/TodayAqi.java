package com.xiaoniu.cleanking.ui.main.bean.weatherdao;

/**
 * @author xiangzhenbiao
 * @since 2019/5/7 18:19
 */
public class TodayAqi {

    /**
     * "2019-05-08T00:00+08:00"
     */
    private String date;

//    private List<TemperaEntity> avg;
    private TemperaEntity avg;

//    private List<TemperaEntity> min;
    private TemperaEntity min;

//    private List<TemperaEntity> max;
    private TemperaEntity max;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public TemperaEntity getAvg() {
        return avg;
    }

    public void setAvg(TemperaEntity avg) {
        this.avg = avg;
    }

    public TemperaEntity getMin() {
        return min;
    }

    public void setMin(TemperaEntity min) {
        this.min = min;
    }

    public TemperaEntity getMax() {
        return max;
    }

    public void setMax(TemperaEntity max) {
        this.max = max;
    }

    public static class TemperaEntity{

        /**
         * 24.96
         */
        private String usa;

        private String chn;

        public String getUsa() {
            return usa;
        }

        public void setUsa(String usa) {
            this.usa = usa;
        }

        public String getChn() {
            return chn;
        }

        public void setChn(String chn) {
            this.chn = chn;
        }
    }
}
