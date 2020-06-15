package com.xiaoniu.cleanking.bean.path;

import java.util.List;

/**
 * @author zhengzhihao
 * @date 2020/6/8 20
 * @mail：zhengzhihao@hellogeek.com
 */
public class RealJsondata {


    /**
     * applist : [{"cleanType":0,"filePath":".000","fileType":4,"packageName":"com.lalamove.huolala.client","ruleCategory":"applist"},{"cleanType":0,"filePath":"-百思不得姐","fileType":3,"packageName":"com.budejie.www","ruleCategory":"applist"},{"cleanType":0,"filePath":"下载/i主题/解锁样式/.cache/online","fileType":1,"packageName":"com.bbk.theme","ruleCategory":"applist"},{"cleanType":0,"filePath":"下载/i主题/静态壁纸","fileType":1,"packageName":"com.bbk.theme","ruleCategory":"applist"},{"cleanType":0,"filePath":"ZhuiShuShenQi/Chapter","fileType":4,"packageName":"com.ushaqi.zhuishushenqi","ruleCategory":"applist"},{"cleanType":0,"filePath":"Android/data/com.ushaqi.zhuishushenqi/files","fileType":4,"packageName":"com.ushaqi.zhuishushenqi","ruleCategory":"applist"},{"cleanType":1,"filePath":"Mob/com.ushaqi.zhuishushenqi/cache","packageName":"com.ushaqi.zhuishushenqi","ruleCategory":"applist"},{"cleanType":1,"filePath":"ZhuiShuShenQi/OAuth/login","packageName":"com.ushaqi.zhuishushenqi","ruleCategory":"applist"}]
     * uninstallList : [{"filePath":"-百思不得姐","nameEn":"百思不得姐","nameZh":"百思不得姐","packageName":"com.budejie.www","ruleCategory":"uninstall_list"},{"filePath":"sina/vdisk","nameEn":"微盘","nameZh":"微盘","packageName":"com.sina.VDisk","ruleCategory":"uninstall_list"},{"filePath":"smartcall","nameEn":"酷音铃声","nameZh":"酷音铃声","packageName":"com.iflytek.ringdiyclient","ruleCategory":"uninstall_list"},{"filePath":"diyring_download","nameEn":"酷音铃声","nameZh":"酷音铃声","packageName":"com.iflytek.ringdiyclient","ruleCategory":"uninstall_list"},{"filePath":"org.fungo.fungoliveallstar","nameEn":"手机电视直播大全","nameZh":"手机电视直播大全","packageName":"org.fungo.fungoliveallstar","ruleCategory":"uninstall_list"},{"filePath":"Mob/com.ushaqi.zhuishushenqi","nameEn":"追书神器","nameZh":"追书神器","packageName":"com.ushaqi.zhuishushenqi","ruleCategory":"uninstall_list"},{"filePath":"ZhuiShuShenQi","nameEn":"追书神器","nameZh":"追书神器","packageName":"com.ushaqi.zhuishushenqi","ruleCategory":"uninstall_list"}]
     * uselessApk : [{"filePath":"tencent/MidasPay/*.apk","ruleCategory":"useless_apk"},{"filePath":"Android/data/cn.ledongli.ldl/files/Download","ruleCategory":"useless_apk"},{"filePath":"Android/data/com.husor.beibei/apk","ruleCategory":"useless_apk"},{"filePath":"MiMarket/files/apk/*.apk","ruleCategory":"useless_apk"}]
     * versionInfo : {"versionId":25,"versionCode":"2.1.003"}
     */

    private VersionInfoBean versionInfo;
    private List<ApplistBean> applist;
    private List<UninstallListBean> uninstallList;
    private List<UselessApkBean> uselessApk;

    public VersionInfoBean getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(VersionInfoBean versionInfo) {
        this.versionInfo = versionInfo;
    }

    public List<ApplistBean> getApplist() {
        return applist;
    }

    public void setApplist(List<ApplistBean> applist) {
        this.applist = applist;
    }

    public List<UninstallListBean> getUninstallList() {
        return uninstallList;
    }

    public void setUninstallList(List<UninstallListBean> uninstallList) {
        this.uninstallList = uninstallList;
    }

    public List<UselessApkBean> getUselessApk() {
        return uselessApk;
    }

    public void setUselessApk(List<UselessApkBean> uselessApk) {
        this.uselessApk = uselessApk;
    }

    public static class VersionInfoBean {
        /**
         * versionId : 25
         * versionCode : 2.1.003
         */

        private int versionId;
        private String versionCode;

        public int getVersionId() {
            return versionId;
        }

        public void setVersionId(int versionId) {
            this.versionId = versionId;
        }

        public String getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(String versionCode) {
            this.versionCode = versionCode;
        }
    }

    public static class ApplistBean {
        /**
         * cleanType : 0
         * filePath : .000
         * fileType : 4
         * packageName : com.lalamove.huolala.client
         * ruleCategory : applist
         */
        private String id;
        private int cleanType;
        private String filePath;
        private int fileType;
        private String packageName;
        private String ruleCategory;

        public int getCleanType() {
            return cleanType;
        }

        public void setCleanType(int cleanType) {
            this.cleanType = cleanType;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public int getFileType() {
            return fileType;
        }

        public void setFileType(int fileType) {
            this.fileType = fileType;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getRuleCategory() {
            return ruleCategory;
        }

        public void setRuleCategory(String ruleCategory) {
            this.ruleCategory = ruleCategory;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class UninstallListBean {
        /**
         * filePath : -百思不得姐
         * nameEn : 百思不得姐
         * nameZh : 百思不得姐
         * packageName : com.budejie.www
         * ruleCategory : uninstall_list
         */
        private String id;
        private String filePath;
        private String nameEn;
        private String nameZh;
        private String packageName;
        private String ruleCategory;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getNameEn() {
            return nameEn;
        }

        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }

        public String getNameZh() {
            return nameZh;
        }

        public void setNameZh(String nameZh) {
            this.nameZh = nameZh;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getRuleCategory() {
            return ruleCategory;
        }

        public void setRuleCategory(String ruleCategory) {
            this.ruleCategory = ruleCategory;
        }
    }

    public static class UselessApkBean {
        /**
         * filePath : tencent/MidasPay/*.apk
         * ruleCategory : useless_apk
         */
        private String id;
        private String filePath;
        private String ruleCategory;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getRuleCategory() {
            return ruleCategory;
        }

        public void setRuleCategory(String ruleCategory) {
            this.ruleCategory = ruleCategory;
        }
    }
}
