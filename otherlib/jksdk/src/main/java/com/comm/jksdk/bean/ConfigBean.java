package com.comm.jksdk.bean;

import java.io.Serializable;
import java.util.List;

public class ConfigBean implements Serializable {
    private List<AdListBean> adList;

    public List<AdListBean> getAdList() {
        return adList;
    }

    public void setAdList(List<AdListBean> adList) {
        this.adList = adList;
    }

    public static class AdListBean {
        /**
         * adPosition : home_page_list1
         * adRequestTimeOut : 10
         * adStyle : 大图
         * adVersion : 1
         * adsInfos : [{"adId":"6548444565561","adUnion":"优量汇","adsAppId":"jktq1103","adsAppName":"即刻天气","requestOrder":1,"requestType":0},{"adId":"4654564654644","adUnion":"穿山甲","adsAppId":"tq1103","adsAppName":"天气","requestOrder":5,"requestType":0}]
         * isChange : 0
         * productId : 1
         */

        private String adPosition;
        private int adRequestTimeOut;
        private String adStyle;
        private int adVersion;
        private int isChange;
        private int productId;
        private List<AdsInfosBean> adsInfos;

        public String getAdPosition() {
            return adPosition;
        }

        public void setAdPosition(String adPosition) {
            this.adPosition = adPosition;
        }

        public int getAdRequestTimeOut() {
            return adRequestTimeOut;
        }

        public void setAdRequestTimeOut(int adRequestTimeOut) {
            this.adRequestTimeOut = adRequestTimeOut;
        }

        public String getAdStyle() {
            return adStyle;
        }

        public void setAdStyle(String adStyle) {
            this.adStyle = adStyle;
        }

        public int getAdVersion() {
            return adVersion;
        }

        public void setAdVersion(int adVersion) {
            this.adVersion = adVersion;
        }

        public int getIsChange() {
            return isChange;
        }

        public void setIsChange(int isChange) {
            this.isChange = isChange;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public List<AdsInfosBean> getAdsInfos() {
            return adsInfos;
        }

        public void setAdsInfos(List<AdsInfosBean> adsInfos) {
            this.adsInfos = adsInfos;
        }

        public static class AdsInfosBean {
            /**
             * adId : 6548444565561
             * adUnion : 优量汇
             * adsAppId : jktq1103
             * adsAppName : 即刻天气
             * requestOrder : 1
             * requestType : 0
             */

            private String adId;
            private String adUnion;
            private String adsAppId;
            private String adsAppName;
            private int requestOrder;
            private int requestType;

            public String getAdId() {
                return adId;
            }

            public void setAdId(String adId) {
                this.adId = adId;
            }

            public String getAdUnion() {
                return adUnion;
            }

            public void setAdUnion(String adUnion) {
                this.adUnion = adUnion;
            }

            public String getAdsAppId() {
                return adsAppId;
            }

            public void setAdsAppId(String adsAppId) {
                this.adsAppId = adsAppId;
            }

            public String getAdsAppName() {
                return adsAppName;
            }

            public void setAdsAppName(String adsAppName) {
                this.adsAppName = adsAppName;
            }

            public int getRequestOrder() {
                return requestOrder;
            }

            public void setRequestOrder(int requestOrder) {
                this.requestOrder = requestOrder;
            }

            public int getRequestType() {
                return requestType;
            }

            public void setRequestType(int requestType) {
                this.requestType = requestType;
            }
        }
    }


}
