package com.xiaoniu.cleanking.ui.newclean.bean

enum class ScanningResultType(var type: Int, var title: String) {

    /**
     * 缓存垃圾
     */
    CACHE_JUNK(type = 1, title = "缓存垃圾"),

    /**
     * 卸载残留
     */
    UNINSTALL_JUNK(type = 2, title = "卸载残留"),

    /**
     * 广告垃圾
     */
    AD_JUNK(type = 3, title = "广告垃圾"),

    /**
     * 无用安装包
     */
    APK_JUNK(type = 4, title = "无用安装包"),

    /**
     * 内存垃圾
     */
    MEMORY_JUNK(type = 5, title = "内存加速");

    companion object {

        /**
         * 通过扫描类型id获取到扫描内容
         */
        @JvmStatic
        fun getScanningResultTypeByTypeId(typeId: Int): ScanningResultType? {
            return values().find {
                typeId == it.type
            }
        }
    }
}