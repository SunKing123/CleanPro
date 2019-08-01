package com.xiaoniu.cleanking.ui.tool.wechat.util;

import android.os.Environment;
import android.text.TextUtils;

import com.xiaoniu.cleanking.ui.tool.wechat.bean.WxAndQqScanPathInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WxUtil {
    public static List<String> getReplaceNameList(File[] fileArr) {
        ArrayList arrayList = new ArrayList();
        if (fileArr != null) {
            for (int i = 0; i < fileArr.length; i++) {
                if (fileArr[i].getName().length() >= 30) {
                    arrayList.add(fileArr[i].getName());
                }
            }
        }
        return arrayList;
    }

    public static List<WxAndQqScanPathInfo> scanFilePathDb() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/.tmp"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/crash"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/Cache"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/CDNTemp"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/CheckResUpdate"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/diskcache"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/FailMsgFileCache"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/handler"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/locallog"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/spltrace"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/sns_ad_landingpages"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/vusericon"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/wallet"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/WebviewCache"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/xlog"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/ssssss/bizmsg"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/ssssss/openapi"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/ssssss/favorite"));
        arrayList.add(new WxAndQqScanPathInfo(1, "/Tencent/MicroMsg/ssssss/avatar"));
        arrayList.add(new WxAndQqScanPathInfo(3, "/Tencent/MicroMsg/ssssss/brandicon"));
        arrayList.add(new WxAndQqScanPathInfo(3, "/Tencent/MicroMsg/ssssss/image"));
        arrayList.add(new WxAndQqScanPathInfo(3, "/Tencent/MicroMsg/wxacache"));
        arrayList.add(new WxAndQqScanPathInfo(4, "/Tencent/MicroMsg/ssssss/sns"));
        arrayList.add(new WxAndQqScanPathInfo(6, "/Android/data/com.tencent.mm/files/VideoCache"));
        arrayList.add(new WxAndQqScanPathInfo(6, "/Tencent/MicroMsg/ssssss/video"));
        arrayList.add(new WxAndQqScanPathInfo(5, "/Tencent/MicroMsg/ssssss/image2"));
        arrayList.add(new WxAndQqScanPathInfo(9, "/Tencent/MicroMsg/WeiXin"));
        arrayList.add(new WxAndQqScanPathInfo(8, "/Tencent/MicroMsg/ssssss/voice2"));
        arrayList.add(new WxAndQqScanPathInfo(2, "/Tencent/MicroMsg/ssssss/emoji"));
        arrayList.add(new WxAndQqScanPathInfo(10, "/Tencent/MicroMsg/Download"));
        arrayList.add(new WxAndQqScanPathInfo(10, "/Tencent/MicroMsg/game"));
        arrayList.add(new WxAndQqScanPathInfo(10, "/Tencent/MicroMsg/music"));
        return arrayList;
    }

    public long getEasyCleanBackGround(boolean z) {
        long j = 0;
        File file = new File(Environment.getExternalStorageDirectory() + "/tencent/MicroMsg");
        if (file.exists()) {
            List<String> replaceNameList = getReplaceNameList(file.listFiles());
            List scanFilePathDb = scanFilePathDb();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= scanFilePathDb.size()) {
                    break;
                }
                if (((WxAndQqScanPathInfo) scanFilePathDb.get(i2)).getType() == 1 || ((WxAndQqScanPathInfo) scanFilePathDb.get(i2)).getType() == 3 || ((WxAndQqScanPathInfo) scanFilePathDb.get(i2)).getType() == 4 || ((WxAndQqScanPathInfo) scanFilePathDb.get(i2)).getType() == 2) {
                    String filePath = ((WxAndQqScanPathInfo) scanFilePathDb.get(i2)).getFilePath();
                    if (!TextUtils.isEmpty(filePath)) {
                        if (!filePath.contains("ssssss") || replaceNameList.size() <= 0) {
                            File file2 = new File(Environment.getExternalStorageDirectory() + filePath);
                            if (file2.exists()) {
                                j += a(file2, true, ((WxAndQqScanPathInfo) scanFilePathDb.get(i2)).getType(), z);
                            }
                        } else {
                            for (String replace : replaceNameList) {
                                File file3 = new File(Environment.getExternalStorageDirectory() + filePath.replace("ssssss", replace));
                                if (file3.exists()) {
                                    j += a(file3, true, ((WxAndQqScanPathInfo) scanFilePathDb.get(i2)).getType(), z);
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
        File file = new File(Environment.getExternalStorageDirectory() + "/tencent/MicroMsg");
        if (!file.exists()) {
            return 0;
        }
        List<String> replaceNameList = getReplaceNameList(file.listFiles());
        List scanFilePathDb = scanFilePathDb();
        long j = 0;
        for (int i = 0; i < scanFilePathDb.size(); i++) {
            if (((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getType() == 5 || ((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getType() == 6 || ((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getType() == 7 || ((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getType() == 8 || ((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getType() == 9 || ((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getType() == 10) {
                String filePath = ((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getFilePath();
                if (!TextUtils.isEmpty(filePath)) {
                    if (!filePath.contains("ssssss") || replaceNameList.size() <= 0) {
                        File file2 = new File(Environment.getExternalStorageDirectory() + filePath);
                        if (file2.exists()) {
                            j += a(file2, false, ((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getType(), false);
                        }
                    } else {
                        for (String replace : replaceNameList) {
                            File file3 = new File(Environment.getExternalStorageDirectory() + filePath.replace("ssssss", replace));
                            if (file3.exists()) {
                                j += a(file3, false, ((WxAndQqScanPathInfo) scanFilePathDb.get(i)).getType(), false);
                            }
                        }
                    }
                }
            }
        }
        return j;
    }

    private long a(File file, boolean z, int i, boolean z2) {
        long j = 0;
        File[] listFiles = file.listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {
                    j += a(file2, z, i, z2);
                } else if (i == 2) {
                    if (file2.getAbsolutePath().endsWith("_cover") || new File(file2.getAbsolutePath() + "_cover").exists()) {
                        if (!z) {
                            j += file2.length();
                        }
                    } else if (z) {
                        j += file2.length();
                        if (z2) {
                            file2.delete();
                        }
                    }
                } else if (i != 6) {
                    j += file2.length();
                    if (z && z2) {
                        file2.delete();
                    }
                } else if (file2.getName().contains(".jpg") && !new File(file2.getAbsolutePath().replace(".jpg", ".mp4")).exists()) {
                    file2.delete();
                }
            }
        }
        return j;
    }


}