package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

import java.util.List;

/**
 * 互动式广告
 */
public class InteractionSwitchList extends BaseEntity {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {

        private boolean isOpen;
        private String configKey;
        private String switcherName;
        private String advertPosition;
        private String versions;
        private String advertId;
        private List<SwitchActiveLineDTOList> switchActiveLineDTOList;

        public boolean isOpen() {
            return isOpen;
        }

        public String getConfigKey() {
            return configKey;
        }

        public String getSwitcherName() {
            return switcherName;
        }

        public String getAdvertPosition() {
            return advertPosition;
        }

        public String getVersions() {
            return versions;
        }

        public String getAdvertId() {
            return advertId;
        }

        public List<SwitchActiveLineDTOList> getSwitchActiveLineDTOList() {
            return switchActiveLineDTOList;
        }

        public static class SwitchActiveLineDTOList {

            private String activeId;
            private String imgUrl;
            private String linkUrl;

            public String getActiveId() {
                return activeId;
            }

            public String getImgUrl() {
                return imgUrl;
            }

            public String getLinkUrl() {
                return linkUrl;
            }
        }
    }
}
