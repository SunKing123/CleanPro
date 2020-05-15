package com.xiaoniu.cleanking.utils

object ScanPathExt {

    /**
     * 返回扫描扩展路径
     */
    @JvmStatic
    fun getScanExtPath(packageName: String): MutableList<String>? {
        //首先初始化路径配置
        val initScanPath = init()
        //匹配已经安装应用
        return initScanPath[packageName]
    }


    /**
     * 初始化文件路径
     */
    private fun init(): LinkedHashMap<String, MutableList<String>> {
        val scanPathMap = LinkedHashMap<String, MutableList<String>>()
        // Wps
        scanPathMap["cn.wps.moffice_eng"] = arrayListOf("KingsoftOffice/.history")
        //oppo software store
        scanPathMap["com.oppo.market"] = arrayListOf("ColorOS/Market/app")
        //moji weather
        scanPathMap["com.moji.mjweather"] = arrayListOf(
                "moji/tmp",
                "moji/ugcImgCache"
        )
        //360 clean master
        //kuwo music
        scanPathMap["com.moji.mjweather"] = arrayListOf(
                "KuwoMusic/welcome",
                "KuwoMusic/.data/net_skin",
                "KuwoMusic/.data/smallpic_cache"
        )
        //iflyime
        scanPathMap[""] = arrayListOf(
                "iFlyIME/plugin/install",
                "iFlyIME/imagecache",
                "iFlyIME/expression_download"
        )
        //360 mobile safe
        scanPathMap["com.qihoo360.mobilesafe"] = arrayListOf(
                "360/LockScreen",
                "360Download",
                "360/MobileSafe/persistence/data",
                "360/skin/com.qihoo360.mobilesafe"
        )
        //qq music
        scanPathMap["com.tencent.qqmusic"] = arrayListOf(
                "qqmusic"
        )
        //kugou music
        scanPathMap["com.kugou.android"] = arrayListOf(
                "kugou/.splash",
                "kugou/.glide",
                "kugou/.backupsv905",
                "kugou/.discovery",
                "kugou/.ads",
                "kugou/v8skin/log",
                "kugou/log",
                "kugou/.images",
                "kugou/fm",
                "kugou/lyrics/hitlayer",
                "kugou/market",
                "kugou/.feedback",
                "kugou/.recordsample",
                "kugou/list",
                "kugou/temp_lyrics",
                "kugou/down_c/radio",
                "kugou/.userinfo/friendicon"
        )
        //tencent mobile manager
        scanPathMap["com.tencent.qqpimsecure"] = arrayListOf(
                "QQSecureDownload",
                "tencent/qqimsecure/pt",
                "com.tencent.qqpimsecure",
                "tencent/QQSecure/DCMU"
        )
        //tencent news
        scanPathMap["com.tencent.news"] = arrayListOf(
                "tencent/Tencentnews/data/extended"
        )
        //yingyongbao
        scanPathMap["com.tencent.android.qqdownloader"] = arrayListOf(
                "tencent/tassistant/mediaCache",
                "tencent/tassistant/pic",
                "tencent/tassistant/log",
                "tencent/tassistant/apk",
                "tencent/tassistant/gif",
                "tencent/tassistant/video",
                "tencent/tassistant/file"
        )
        //wifi万能钥匙
        scanPathMap["com.snda.wifilocating"] = arrayListOf(
                "WifiMasterKey/apk"
        )
        //今日头条
        scanPathMap["com.ss.android.article.lite"] = arrayListOf(
                "news_article/.res"
        )
        //搜狗输入法
        scanPathMap["com.sohu.inputmethod.sogou"] = arrayListOf(
                "sougou/corelog",
                "sougou/sga/.theme_net",
                "sogou/voice",
                "sogou/download"
        )
        //百度
        scanPathMap["com.baidu.searchbox"] = arrayListOf(
                "baidu/flyflow/.video_cache",
                "baidu/searchbox/preset"
        )
        //百度地图
        scanPathMap["com.baidu.BaiduMap"] = arrayListOf(
                "BaiduMap/vmp/h/idr",
                "BaiduMap/bnav/navi/pub",
                "BaiduMap/bnav/tts"
        )
        //新浪微博
        scanPathMap["com.sina.weibo"] = arrayListOf(
                "sina/weibo/.applogs",
                "sina/weibo/.database",
                "sina/weibo/storage/weibo_detail",
                "sina/weibo/traffic",
                "sina/news/font",
                "sina/weibo/.wbadcache",
                "sina/weibo/.appinfos",
                "sina/weibo/theme_data",
                "sina/weibo/WeiyouMenuList",
                "sina/weibo/storage/pagecard_no_auto_clear",
                "sina/weibo/theme_data",
                "sina/weibo/small_page",
                "sina/weibo/.weibo_pic_new"
        )
        //高德地图
        scanPathMap["com.autonavi.minimap"] = arrayListOf(
                "autonavi/support",
                "autonavi/LocalCache",
                "autonavi/splash",
                "autonavi/tts",
                "autonavi/mini_mapv3/vmap",
                "autonavi/imagecache"
        )
        //拼多多
        scanPathMap["com.xunmeng.pinduoduo"] = arrayListOf(
                "Pindd/temp"
        )
        //优酷视频
        scanPathMap["com.youku.phone"] = arrayListOf(
                "youku/ikuacc",
                "youku/cacheData"
        )
        //支付宝
        scanPathMap["com.eg.android.AlipayGphone"] = arrayListOf(
                "alipay/com.eg.android.AlipayGphone/nebulaDownload/downloads",
                "alipay/com.eg.android.AlipayGphone/applogic",
                "alipay/com.app.shanghai.metro/applogic",
                "alipay/com.eg.android.AlipayGphone/openplatform",
                "alipay/com.eg.android.AlipayGphone/trafficLogic",
                "alipay/com.app.shanghai.metro/trafficLogic"
        )
        //手机淘宝
        scanPathMap["com.taobao.taobao"] = arrayListOf(
                "/taobao/wvache"
        )
        return scanPathMap
    }
}