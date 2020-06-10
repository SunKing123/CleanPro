package com.xiaoniu.cleanking.bean;

import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;

public class JunkResultWrapper {

    public static final int ITEM_TYPE_TITLE = 1;
    public static final int ITEM_TYPE_CONTENT = 2;

    public int itemTye;

    public JunkGroup junkGroup;

    public FirstJunkInfo firstJunkInfo;

    public ScanningResultType scanningResultType;

    public JunkResultWrapper(int itemTye, ScanningResultType scanningResultType, JunkGroup junkGroup) {
        this.itemTye = itemTye;
        this.junkGroup = junkGroup;
        this.scanningResultType = scanningResultType;
    }

    public JunkResultWrapper(int itemTye, ScanningResultType scanningResultType, FirstJunkInfo firstJunkInfo) {
        this.itemTye = itemTye;
        this.firstJunkInfo = firstJunkInfo;
        this.scanningResultType = scanningResultType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JunkResultWrapper wrapper = (JunkResultWrapper) o;

        if (itemTye != wrapper.itemTye) return false;
        if (junkGroup != null ? !junkGroup.equals(wrapper.junkGroup) : wrapper.junkGroup != null)
            return false;
        if (firstJunkInfo != null ? !firstJunkInfo.equals(wrapper.firstJunkInfo) : wrapper.firstJunkInfo != null)
            return false;
        return scanningResultType == wrapper.scanningResultType;
    }

    @Override
    public int hashCode() {
        int result = itemTye;
        result = 31 * result + (junkGroup != null ? junkGroup.hashCode() : 0);
        result = 31 * result + (firstJunkInfo != null ? firstJunkInfo.hashCode() : 0);
        result = 31 * result + (scanningResultType != null ? scanningResultType.hashCode() : 0);
        return result;
    }
}
