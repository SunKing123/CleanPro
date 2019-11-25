package com.xiaoniu.cleanking.ui.main.bean.weatherdao;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhengzhihao
 * @date 2019/11/25 17
 * @mail：zhengzhihao@hellogeek.com
 */
public class Weather72HEntity {


    private List<WindInfoBean> windInfo;
    private List<TemperatureBean> temperature ;
    private List<SkyconBean> skycon;
    private List<AqiBean> aqi;
    private List<WindBean> wind;

    public List<WindInfoBean> getWindInfo() {
        return windInfo;
    }

    public void setWindInfo(List<WindInfoBean> windInfo) {
        this.windInfo = windInfo;
    }

    public List<TemperatureBean> getTemperature() {
        return temperature;
    }

    public void setTemperature(List<TemperatureBean> temperature) {
        this.temperature = temperature;
    }

    public List<SkyconBean> getSkycon() {
        return skycon;
    }

    public void setSkycon(List<SkyconBean> skycon) {
        this.skycon = skycon;
    }

    public List<AqiBean> getAqi() {
        return aqi;
    }

    public void setAqi(List<AqiBean> aqi) {
        this.aqi = aqi;
    }

    public List<WindBean> getWind() {
        return wind;
    }

    public void setWind(List<WindBean> wind) {
        this.wind = wind;
    }

    public static class WindInfoBean {
        /**
         * datetime : 2019-07-05T12:00+08:00
         * rank : 2级
         */

        private String datetime;
        private String rank;

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }
    }

    public static class TemperatureBean {
        /**
         * datetime : 2019-07-05T12:00+08:00
         * value : 28
         */

        private String datetime;
        private String  value;

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class SkyconBean {
        /**
         * datetime : 2019-07-05T12:00+08:00
         * value : PARTLY_CLOUDY_DAY
         */

        private String datetime;
        private String value;

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class AqiBean {
        /**
         * datetime : 2019-07-05T12:00+08:00
         * value : {"usa":39,"aqiDesc":"优","chn":39}
         */

        private String datetime;
        private ValueBean value;

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public ValueBean getValue() {
            return value;
        }

        public void setValue(ValueBean value) {
            this.value = value;
        }

        public static class ValueBean {
            /**
             * usa : 39
             * aqiDesc : 优
             * chn : 39
             */

            private int usa;
            private String aqiDesc;
            private int chn;

            public int getUsa() {
                return usa;
            }

            public void setUsa(int usa) {
                this.usa = usa;
            }

            public String getAqiDesc() {
                return aqiDesc;
            }

            public void setAqiDesc(String aqiDesc) {
                this.aqiDesc = aqiDesc;
            }

            public int getChn() {
                return chn;
            }

            public void setChn(int chn) {
                this.chn = chn;
            }
        }
    }

    public static class WindBean {
        /**
         * datetime : 2019-07-05T12:00+08:00
         * speed : 10.87
         * direction : 38.46
         */

        private String datetime;
        private double speed;
        private double direction;

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public double getSpeed() {
            return speed;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        public double getDirection() {
            return direction;
        }

        public void setDirection(double direction) {
            this.direction = direction;
        }
    }
}
