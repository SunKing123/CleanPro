package com.xiaoniu.cleanking.ui.tool.qq.util;

import android.os.Environment;
import android.text.TextUtils;

import com.xiaoniu.cleanking.ui.tool.qq.bean.CleanWxClearInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.Logger;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.WxAndQqScanPathInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class QQUtil {
    public static  List<CleanWxClearInfo> fileList = null;
    public static  List<CleanWxClearInfo> audioList = null;

    public long getEasyCleanBackGround(boolean z) {
        long j = 0;
        File file = new File(Environment.getExternalStorageDirectory() + "/Tencent/MobileQQ");
        if (file.exists()) {
            List<String> replaceNameList = getReplaceNameList(file.listFiles());
            List scanFilePathDb = scanFilePathDb();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= scanFilePathDb.size()) {
                    break;
                }
                if (((WxAndQqScanPathInfo) scanFilePathDb.get(i2)).getType() <= 104) {
                    String filePath = ((WxAndQqScanPathInfo) scanFilePathDb.get(i2)).getFilePath();
                    if (!TextUtils.isEmpty(filePath)) {
                        if (!filePath.contains("ssssss") || replaceNameList.size() <= 0) {
                            File file2 = new File(Environment.getExternalStorageDirectory() + filePath);
                            if (file2.exists()) {
                                j += a(file2, z);
                            }
                        } else {
                            for (String replace : replaceNameList) {
                                File file3 = new File(Environment.getExternalStorageDirectory() + filePath.replace("ssssss", replace));
                                if (file3.exists()) {
                                    j += a(file3, z);
                                }
                            }
                        }
                    }
                }
                i = i2 + 1;
            }
        }
        return j;
    }

    public long getDeepCleanBackGround() {
        File file = new File(Environment.getExternalStorageDirectory() + "/Tencent/MobileQQ");
        if (!file.exists()) {
            return 0;
        }
        List<String> replaceNameList = getReplaceNameList(file.listFiles());
        List scanFilePathDb = scanFilePathDb();
        long j = 0;
        for (int i = 0; i < scanFilePathDb.size(); i++) {
            if (((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getType() > 104) {
                String filePath = ((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getFilePath();
                if (!TextUtils.isEmpty(filePath)) {
                    if (!filePath.contains("ssssss") || replaceNameList.size() <= 0) {
                        File file2 = new File(Environment.getExternalStorageDirectory() + filePath);
                        if (file2.exists()) {
                            j += a(file2, false);
                        }
                    } else {
                        for (String replace : replaceNameList) {
                            File file3 = new File(Environment.getExternalStorageDirectory() + filePath.replace("ssssss", replace));
                            if (file3.exists()) {
                                j += a(file3, false);
                            }
                        }
                    }
                }
            }
        }
        return j;
    }

    private long a(File file, boolean z) {
        long j = 0;
        File[] listFiles = file.listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {
                    j += a(file2, z);
                } else {
                    j += file2.length();
                    if (z) {
                        file2.delete();
                    }
                }
            }
        }
        return j;
    }

    public static List<String> getReplaceNameList(File[] fileArr) {
        ArrayList arrayList = new ArrayList();
        if (fileArr != null) {
            Pattern compile = Pattern.compile("[0-9]*");
            for (int i = 0; i < fileArr.length; i++) {
                if (!TextUtils.isEmpty(fileArr[i].getName()) && fileArr[i].getName().length() >= 5 && fileArr[i].getName().length() <= 16 && compile.matcher(fileArr[i].getName()).matches()) {
                    arrayList.add(fileArr[i].getName());
                }
            }
        }
        return arrayList;
    }

    public static boolean checkEasyGarbage() {
        boolean z;
        Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqScanUtil-checkEasyGarbage-137--检查有没有qq垃圾");
        File file = new File(Environment.getExternalStorageDirectory() + "/Tencent/MobileQQ");
        if (file.exists()) {
            List<String> replaceNameList = getReplaceNameList(file.listFiles());
            List scanFilePathDb = scanFilePathDb();
            z = false;
            for (int i = 0; i < scanFilePathDb.size(); i++) {
                if (((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getType() <= 104) {
                    String filePath = ((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getFilePath();
                    if (TextUtils.isEmpty(filePath)) {
                        continue;
                    } else if (!filePath.contains("ssssss") || replaceNameList.size() <= 0) {
                        File file2 = new File(Environment.getExternalStorageDirectory() + filePath);
                        if (file2.exists()) {
                            z = a(file2);
                            if (z) {
                                Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqScanUtil-checkEasyGarbage-164--查了有");
                                return true;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        for (String replace : replaceNameList) {
                            File file3 = new File(Environment.getExternalStorageDirectory() + filePath.replace("ssssss", replace));
                            if (file3.exists()) {
                                z = a(file3);
                                if (z) {
                                    Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqScanUtil-checkEasyGarbage-154--查了有");
                                    return true;
                                }
                            }
                        }
                        continue;
                    }
                }
            }
        } else {
            z = false;
        }
        Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqScanUtil-checkEasyGarbage-173--查了没有");
        return z;
    }

    private static boolean a(File file) {
        boolean z = false;
        File[] listFiles = file.listFiles();
        if (listFiles == null || listFiles.length <= 0) {
            return false;
        }
        for (File file2 : listFiles) {
            if (file2.isDirectory()) {
                z = a(file2);
                if (z) {
                    return true;
                }
            } else if (file2.length() > 10) {
                Logger.i(Logger.TAG, Logger.ZYTAG, "CleanQqScanUtil-checkFiles-186--" + file2.getAbsolutePath());
                return true;
            }
        }
        return z;
    }

    public static List<WxAndQqScanPathInfo> scanFilePathDb() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/tassistant/cache"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/tassistant/log"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/Midas/log"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/MobileQQ/msgpushnotify"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/MobileQQ/ar_model"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/MobileQQ/iar"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/MobileQQ/.gift"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/.qmt"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/MobileQQ/qqconnect"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/MobileQQ/appicon"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/Qqfile_recv/.thumbnails"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Android/data/com.tencent.mobileqq/qzone/video_cache"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/tencentmapsdk"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/tassistant/pic"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/QQSecureDownload/.CacheADImage"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/MobileQQ/WebViewCheck"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/qzone/.AppCenterImgCache"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/tbs/com.tencent.mobileqq"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/wtlogin/com.tencent.mobileqq"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Qmap/RasterMap/Grid"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/MobileQQ/kata"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/MobileQQ/profilecard"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/MobileQQ/signaturetemplate"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Tencent/MobileQQ/RedPacket"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/.AppCenterWebBuffer_QQ"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Android/data/com.tencent.mobileqq/files/tbslog"));
        arrayList.add(new WxAndQqScanPathInfo(101, "/Android/data/com.tencent.mobileqq/cache/file"));
        arrayList.add(new WxAndQqScanPathInfo(102, "/Tencent/MobileQQ/shortvideo/thumbs"));
        arrayList.add(new WxAndQqScanPathInfo(102, "/Tencent/MobileQQ/status_ic"));
        arrayList.add(new WxAndQqScanPathInfo(102, "/Tencent/MobileQQ/thumb"));
        arrayList.add(new WxAndQqScanPathInfo(102, "/Tencent/QQfile_recv/.trooptmp"));
        arrayList.add(new WxAndQqScanPathInfo(102, "/Tencent/MobileQQ/photoplus"));
        arrayList.add(new WxAndQqScanPathInfo(102, "/Tencent/MobileQQ/.pendant"));
        arrayList.add(new WxAndQqScanPathInfo(102, "/Tencent/MobileQQ/voicechange"));
        arrayList.add(new WxAndQqScanPathInfo(102, "/Tencent/MobileQQ/early"));
        arrayList.add(new WxAndQqScanPathInfo(102, "/Tencent/TMAssistantSDK/Download/com.tencent.mobileqq"));
        arrayList.add(new WxAndQqScanPathInfo(102, "/Tencent/MobileQQ/portrait"));
        arrayList.add(new WxAndQqScanPathInfo(102, "/Android/data/com.tencent.mobileqq/files/.info"));
        arrayList.add(new WxAndQqScanPathInfo(102, "/Tencent/MobileQQ/.apollo"));
        arrayList.add(new WxAndQqScanPathInfo(102, "/Tencent/MobileQQ/.emojiSticker_v2.1"));
        arrayList.add(new WxAndQqScanPathInfo(102, "/Tencent/MobileQQ/babyQIconRes"));
        arrayList.add(new WxAndQqScanPathInfo(102, "/Tencent/MobileQQ/DoutuRes"));
        arrayList.add(new WxAndQqScanPathInfo(103, "/Tencent/MobileQQ/head"));
        arrayList.add(new WxAndQqScanPathInfo(104, "/Android/data/com.qzone"));
        arrayList.add(new WxAndQqScanPathInfo(104, "/Tencent/blob/mqq"));
        arrayList.add(new WxAndQqScanPathInfo(104, "/Tencent/MobileQQ/emoji"));
        arrayList.add(new WxAndQqScanPathInfo(104, "/Tencent/MobileQQ/card"));
        arrayList.add(new WxAndQqScanPathInfo(104, "/Android/data/com.tencent.mobileqq/qzone/zip_cache"));
        arrayList.add(new WxAndQqScanPathInfo(104, "/Android/data/com.tencent.mobileqq/qzone/rapid_comment"));
        arrayList.add(new WxAndQqScanPathInfo(104, "/Android/data/com.tencent.mobileqq/qzone/qzone_live_video_like_em_res"));
        arrayList.add(new WxAndQqScanPathInfo(104, "/Android/data/com.tencent.mobileqq/cache/tencent_sdk_download"));
        arrayList.add(new WxAndQqScanPathInfo(107, "/Tencent/MobileQQ/shortvideo"));
        arrayList.add(new WxAndQqScanPathInfo(105, "/Tencent/MobileQQ/diskcache"));
        arrayList.add(new WxAndQqScanPathInfo(106, "/Tencent/QQ_Images"));
        arrayList.add(new WxAndQqScanPathInfo(109, "/Tencent/QQ_Favorite"));
        arrayList.add(new WxAndQqScanPathInfo(110, "/Tencent/MobileQQ/.emotionsm"));
        arrayList.add(new WxAndQqScanPathInfo(111, "/Tencent/MobileQQ/ssssss/ptt"));
        arrayList.add(new WxAndQqScanPathInfo(108, "/Tencent/QQfile_recv"));
        return arrayList;
    }
}