package com.xiaoniu.cleanking.ui.tool.wechat.util;

import android.content.Intent;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanSwitch;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxEasyInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxFourItemInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxHeadInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxItemInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.Constants;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.Logger;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.WxAndQqScanPathInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.WxNotifyRefrshReceiver;
import com.xiaoniu.cleanking.utils.FileUtils;
import com.xiaoniu.cleanking.utils.ThreadTaskUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 微信扫描文件工具类
 */
public class WxQqUtil {
    public static boolean a = false;
    public static CleanWxEasyInfo d = new CleanWxEasyInfo(1, PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_WX_GARBAGE_FILES_CHECKED, true));   //垃圾文件   不含聊天记录建议清理
    public static CleanWxEasyInfo e = new CleanWxEasyInfo(2, PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_WX_FACE_CACHE_CHECKED, true));      //缓存表情   浏览聊天记录产生的表情
    public static CleanWxEasyInfo f = new CleanWxEasyInfo(3, PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_WX_OTHERS_CACHE_CHECKED, true));    //其他缓存   浏览公众号小程序产生
    public static CleanWxEasyInfo g = new CleanWxEasyInfo(4, PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_WX_FRIENDS_CACHE_CHECKED, true));   //朋友圈缓存
    public static CleanWxEasyInfo h = new CleanWxEasyInfo(5, false);   //图片 1911张209M
    public static CleanWxEasyInfo i = new CleanWxEasyInfo(6, false);   //视频（确认）(可以视频跳转)
    public static CleanWxEasyInfo j = new CleanWxEasyInfo(7, false);   //我的--收藏的表情（确认）
    public static CleanWxEasyInfo k = new CleanWxEasyInfo(8, false);   // 语音                               貌似是所有的微信视频的第一帧图  361张图7.8M
    public static CleanWxEasyInfo l = new CleanWxEasyInfo(9, false);   //拍摄及保存的图片（确认） 650张237M
    public static CleanWxEasyInfo m = new CleanWxEasyInfo(11, false);  //拍摄以及保存的视频（确认）
    public static CleanWxEasyInfo n = new CleanWxEasyInfo(10, false);  //接收的文件（确认）
    public static long o;
    public boolean b;
    public boolean c;
    /* access modifiers changed from: private */
    public boolean p = false;
    /* access modifiers changed from: private */
    public a q;
    private String r;

    public interface a {
        void changeHomeNum();

        void wxEasyScanFinish();
    }

    public void startScanWxGarbage(String str, a aVar) {
        this.r = str;
        this.q = aVar;
        this.p = false;
        a = true;
        o = 0;
        this.b = false;
        this.c = false;
        d.reDataInfo();
        e.reDataInfo();
        f.reDataInfo();
        g.reDataInfo();
        h.reDataInfo();
        i.reDataInfo();
        j.reDataInfo();
        k.reDataInfo();
        l.reDataInfo();
        m.reDataInfo();
        n.reDataInfo();
        ArrayList<String> arrayList = new ArrayList<>();
        File file = new File(Environment.getExternalStorageDirectory() + "/Tencent/MicroMsg");
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (int i2 = 0; i2 < listFiles.length; i2++) {
                    if (listFiles[i2].getName().length() == 32) {
                        arrayList.add(listFiles[i2].getName());
                    }
                }
            }
            List scanFilePathDb = WxUtil.scanFilePathDb();
            if ("finishActivity".equals(str) || "bigGarbageFragment".equals(str)) {
                d.setFinished(true);
                g.setFinished(true);
                f.setFinished(true);
                e.setFinished(true);
                int i3 = 0;
                while (i3 < scanFilePathDb.size()) {
                    if (((WxAndQqScanPathInfo) scanFilePathDb.get(i3)).getType() == 1 || ((WxAndQqScanPathInfo) scanFilePathDb.get(i3)).getType() == 3 || ((WxAndQqScanPathInfo) scanFilePathDb.get(i3)).getType() == 4) {
                        scanFilePathDb.remove(i3);
                        i3--;
                    }
                    i3++;
                }
            }
            int i4 = 0;
            while (i4 < scanFilePathDb.size()) {
                if (((WxAndQqScanPathInfo) scanFilePathDb.get(i4)).getFilePath().contains("ssssss") && arrayList != null && arrayList.size() > 0) {
                    for (String replace : arrayList) {
                        scanFilePathDb.add(new WxAndQqScanPathInfo(((WxAndQqScanPathInfo) scanFilePathDb.get(i4)).getType(), ((WxAndQqScanPathInfo) scanFilePathDb.get(i4)).getFilePath().replace("ssssss", replace)));
                    }
                    scanFilePathDb.remove(i4);
                    i4--;
                }
                i4++;
            }
            if (scanFilePathDb.size() > 0) {
                if (!this.p) {
                    c();
                    d();
                }
                while (scanFilePathDb.size() > 0 && !this.p) {
                    ArrayList arrayList2 = new ArrayList();
                    arrayList2.add(scanFilePathDb.get(0));
                    scanFilePathDb.remove(0);
                    if (scanFilePathDb.size() > 0) {
                        int i5 = 0;
                        while (i5 < scanFilePathDb.size()) {
                            if (((WxAndQqScanPathInfo) scanFilePathDb.get(i5)).getType() == ((WxAndQqScanPathInfo) arrayList2.get(0)).getType()) {
                                arrayList2.add(scanFilePathDb.get(i5));
                                scanFilePathDb.remove(i5);
                                i5--;
                            }
                            i5++;
                        }
                    }
                    if (arrayList2.size() > 0) {
                        a((List<WxAndQqScanPathInfo>) arrayList2);
                    }
                }
                return;
            }
            Logger.i(Logger.TAG, Logger.ZYTAG, "CleanWxClearNewActivity---run --pathList列表null-- ");
            e();
            if (aVar != null) {
                aVar.wxEasyScanFinish();
                return;
            }
            return;
        }
        e();
        if (aVar != null) {
            aVar.wxEasyScanFinish();
        }
        Logger.i(Logger.TAG, Logger.ZYTAG, "CleanWxClearNewActivity---run --微信文件夹不存在-- ");
    }

    private void a(final List<WxAndQqScanPathInfo> list) {
        ThreadTaskUtil.executeNormalTask("-CleanWxClearNewActivity-startScanAllOneType-468--", new Runnable() {
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    File file = new File(Environment.getExternalStorageDirectory() + ((WxAndQqScanPathInfo) list.get(i)).getFilePath());
                    if (file.exists()) {
                        WxQqUtil.this.a(file, list);
                    }
                }
                switch (((WxAndQqScanPathInfo) list.get(0)).getType()) {
                    case 1:
                        WxQqUtil.this.a(d);
                        d.setFinished(true);
                        WxQqUtil.this.a();
                        break;
                    case 2:
                        WxQqUtil.this.a(j);
                        WxQqUtil.this.a(e);
                        j.setFinished(true);
                        e.setFinished(true);
                        WxQqUtil.this.a();
                        WxQqUtil.this.b();
                        break;
                    case 3:
                        WxQqUtil.this.a(f);
                        f.setFinished(true);
                        WxQqUtil.this.a();
                        break;
                    case 4:
                        WxQqUtil.this.a(g);
                        g.setFinished(true);
                        WxQqUtil.this.a();
                        break;
                    case 5:
                        WxQqUtil.this.a(h);
                        h.setFinished(true);
                        WxQqUtil.this.b();
                        break;
                    case 6:
                        WxQqUtil.this.a(i);
                        i.setFinished(true);
                        WxQqUtil.this.b();
                        break;
                    case 8:
                        WxQqUtil.this.a(k);
                        k.setFinished(true);
                        WxQqUtil.this.b();
                        break;
                    case 9:
                        WxQqUtil.this.a(l);
                        WxQqUtil.this.a(m);
                        l.setFinished(true);
                        m.setFinished(true);
                        WxQqUtil.this.b();
                        break;
                    case 10:
                        WxQqUtil.this.a(n);
                        n.setFinished(true);
                        WxQqUtil.this.b();
                        break;
                }
                LocalBroadcastManager.getInstance(AppApplication.getInstance()).sendBroadcast(new Intent().setAction(WxNotifyRefrshReceiver.b).putExtra(CleanSwitch.CLEAN_DATA, ((WxAndQqScanPathInfo) list.get(0)).getType()));
            }
        });
    }

    /* access modifiers changed from: private */
    public void a(File file, List<WxAndQqScanPathInfo> list) {
        if (file != null && !this.p) {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                int length = listFiles.length;
                int i2 = 0;
                while (i2 < length) {
                    File file2 = listFiles[i2];
                    if (!this.p) {
                        if (file2 != null) {
                            if (!file2.isDirectory()) {
                                if (!".nomedia".equals(file2.getName()) && file2.length() >= 5 && file2 != null && file2.exists()) {
                                    CleanWxItemInfo cleanWxItemInfo = new CleanWxItemInfo();
                                    cleanWxItemInfo.setFileType(((WxAndQqScanPathInfo) list.get(0)).getType());
                                    cleanWxItemInfo.setFile(file2);
                                    cleanWxItemInfo.setDays(TimeUtil.changeTimeToDay(cleanWxItemInfo.getFile().lastModified()));
                                    cleanWxItemInfo.setFileSize(file2.length());
                                    switch (((WxAndQqScanPathInfo) list.get(0)).getType()) {
                                        case 1:
                                            o += cleanWxItemInfo.getFileSize();
                                            d.setTotalSize(d.getTotalSize() + cleanWxItemInfo.getFileSize());
                                            d.setTotalNum(d.getTotalNum() + 1);
                                            if (d.isChecked()) {
                                                cleanWxItemInfo.setChecked(true);
                                                d.setSelectSize(d.getSelectSize() + cleanWxItemInfo.getFileSize());
                                                d.setSelectNum(d.getSelectNum() + 1);
                                            }
                                            cleanWxItemInfo.setDays(TimeUtil.changeTimeToDay(0));
                                            a(d, cleanWxItemInfo);
                                            break;
                                        case 2:
                                            if (!file2.getAbsolutePath().endsWith("_cover") && !new File(file2.getAbsolutePath() + "_cover").exists()) {
                                                if (!"finishActivity".equals(this.r) && !"bigGarbageFragment".equals(this.r)) {
                                                    if (e.isFinished()) {
                                                        j.setTotalNum(j.getTotalNum() + 1);
                                                        j.setTotalSize(j.getTotalSize() + cleanWxItemInfo.getFileSize());
                                                        a(j, cleanWxItemInfo);
                                                        break;
                                                    } else {
                                                        o += cleanWxItemInfo.getFileSize();
                                                        e.setTotalNum(e.getTotalNum() + 1);
                                                        e.setTotalSize(e.getTotalSize() + cleanWxItemInfo.getFileSize());
                                                        if (e.isChecked()) {
                                                            cleanWxItemInfo.setChecked(true);
                                                            e.setSelectNum(e.getSelectNum() + 1);
                                                            e.setSelectSize(e.getSelectSize() + cleanWxItemInfo.getFileSize());
                                                        }
                                                        a(e, cleanWxItemInfo);
                                                        break;
                                                    }
                                                }
                                            } else {
                                                j.setTotalSize(j.getTotalSize() + cleanWxItemInfo.getFileSize());
                                                j.setTotalNum(j.getTotalNum() + 1);
                                                a(j, cleanWxItemInfo);
                                                break;
                                            }
                                            break;
                                        case 3:
                                            o += cleanWxItemInfo.getFileSize();
                                            f.setTotalSize(f.getTotalSize() + cleanWxItemInfo.getFileSize());
                                            f.setTotalNum(f.getTotalNum() + 1);
                                            if (f.isChecked()) {
                                                cleanWxItemInfo.setChecked(true);
                                                f.setSelectSize(f.getSelectSize() + cleanWxItemInfo.getFileSize());
                                                f.setSelectNum(f.getSelectNum() + 1);
                                            }
                                            a(f, cleanWxItemInfo);
                                            break;
                                        case 4:
                                            if (file2.getName().startsWith("snstblur_src_")) {
                                                if (Constants.PRIVATE_LOG_CONTROLER) {
                                                    break;
                                                } else {
                                                    file2.delete();
                                                    break;
                                                }
                                            } else {
                                                if (!file2.getName().startsWith("snst_")) {
                                                    if (file2.getName().startsWith("snsu_") && new File(file2.getAbsolutePath().replace("snsu_", "snsb_")).exists()) {
                                                        break;
                                                    }
                                                } else if (!new File(file2.getAbsolutePath().replace("snst_", "snsu_")).exists()) {
                                                    if (new File(file2.getAbsolutePath().replace("snst_", "snsb_")).exists()) {
                                                        break;
                                                    }
                                                } else {
                                                    break;
                                                }
                                                o += cleanWxItemInfo.getFileSize();
                                                g.setTotalSize(g.getTotalSize() + cleanWxItemInfo.getFileSize());
                                                g.setTotalNum(g.getTotalNum() + 1);
                                                if (g.isChecked()) {
                                                    cleanWxItemInfo.setChecked(true);
                                                    g.setSelectSize(g.getSelectSize() + cleanWxItemInfo.getFileSize());
                                                    g.setSelectNum(g.getSelectNum() + 1);
                                                }
                                                a(g, cleanWxItemInfo);
                                                break;
                                            }
                                        case 5:
                                            if (file2.getName().endsWith(".jpg_hevc")) {
                                                if (Constants.PRIVATE_LOG_CONTROLER) {
                                                    break;
                                                } else {
                                                    file2.delete();
                                                    break;
                                                }
                                            } else {
                                                if (file2.getName().startsWith("th_")) {
                                                    if (!file2.getName().endsWith("hd")) {
                                                        break;
                                                    } else {
                                                        break;
                                                    }
                                                }
                                                h.setTotalSize(h.getTotalSize() + cleanWxItemInfo.getFileSize());
                                                h.setTotalNum(h.getTotalNum() + 1);
                                                a(h, cleanWxItemInfo);
                                                break;
                                            }
                                        case 6:
                                            if (!file2.getAbsolutePath().contains("download/appbrand")) {
                                                if (file2.getName().endsWith(".jpg")) {
                                                    if (!new File(file2.getAbsolutePath().replace(".jpg", ".mp4")).exists() && !Constants.PRIVATE_LOG_CONTROLER) {
                                                        file2.delete();
                                                        break;
                                                    }
                                                } else {
                                                    i.setTotalSize(i.getTotalSize() + cleanWxItemInfo.getFileSize());
                                                    i.setTotalNum(i.getTotalNum() + 1);
                                                    a(i, cleanWxItemInfo);
                                                    break;
                                                }
                                            } else {
                                                break;
                                            }
                                        case 8:
                                            k.setTotalSize(k.getTotalSize() + cleanWxItemInfo.getFileSize());
                                            k.setTotalNum(k.getTotalNum() + 1);
                                            a(k, cleanWxItemInfo);
                                            break;
                                        case 9:
                                            if (!file2.getName().endsWith(".mp4")) {
                                                l.setTotalSize(l.getTotalSize() + cleanWxItemInfo.getFileSize());
                                                l.setTotalNum(l.getTotalNum() + 1);
                                                l.getMineList().add(cleanWxItemInfo);
                                                a(l, cleanWxItemInfo);
                                                break;
                                            } else {
                                                m.setTotalSize(m.getTotalSize() + cleanWxItemInfo.getFileSize());
                                                m.setTotalNum(m.getTotalNum() + 1);
                                                m.getMineList().add(cleanWxItemInfo);
                                                a(m, cleanWxItemInfo);
                                                break;
                                            }
                                        case 10:
                                            n.setTotalSize(n.getTotalSize() + cleanWxItemInfo.getFileSize());
                                            n.setTotalNum(n.getTotalNum() + 1);
                                            a(n, cleanWxItemInfo);
                                            break;
                                    }
                                }
                            } else {
                                try {
                                    if (file2.listFiles() == null || file2.listFiles().length == 0) {
                                        FileUtils.deleteFileAndFolder(file2);
                                    }
                                } catch (Exception e2) {
                                    Logger.iCatch(Logger.TAG, Logger.ZYTAG, "CleanWxClearNewActivity---wxFileScan ---- ", e2);
                                }
                                a(file2, list);
                            }
                        }
                        i2++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public synchronized void a() {
        if (!this.b && d.isFinished() && g.isFinished() && f.isFinished()) {
            e.setFinished(true);
            this.b = true;
            if (this.q != null) {
                this.q.wxEasyScanFinish();
            }
        }
        if (this.c && this.b) {
            LocalBroadcastManager.getInstance(AppApplication.getInstance()).sendBroadcast(new Intent().setAction(WxNotifyRefrshReceiver.b).putExtra(CleanSwitch.CLEAN_DATA, WxNotifyRefrshReceiver.c));
        }
    }

    /* access modifiers changed from: private */
    public synchronized void b() {
        if (!this.c && n.isFinished() && l.isFinished() && m.isFinished() && k.isFinished() && j.isFinished() && i.isFinished() && h.isFinished()) {
            this.c = true;
        }
        if (this.c && this.b) {
            LocalBroadcastManager.getInstance(AppApplication.getInstance()).sendBroadcast(new Intent().setAction(WxNotifyRefrshReceiver.b).putExtra(CleanSwitch.CLEAN_DATA, WxNotifyRefrshReceiver.c));
        }
    }

    private void c() {
        ThreadTaskUtil.executeNormalTask("-CleanWxScanUtil-changeHomeNum-456-- ", new Runnable() {
            public void run() {
                while (true) {
                    if ((!d.isFinished() || !g.isFinished() || !f.isFinished() || !e.isFinished()) && !WxQqUtil.this.p) {
                        SystemClock.sleep(50);
                        if (!WxQqUtil.this.p) {
                            if (WxQqUtil.this.q != null) {
                                WxQqUtil.this.q.changeHomeNum();
                            }
                        } else {
                            return;
                        }
                    }
                    if (WxQqUtil.this.q != null) {
                        WxQqUtil.this.q.changeHomeNum();
                    }
                }

            }
        });
    }

    private void d() {
        ThreadTaskUtil.executeNormalTask("-CleanWxScanUtil-refleshAllWxData-481-- ", new Runnable() {
            public void run() {
                while (true) {
                    if ((!WxQqUtil.this.c || !WxQqUtil.this.b) && !WxQqUtil.this.p) {
                        SystemClock.sleep(1000);
                        n.setMergTemp(true);
                        l.setMergTemp(true);
                        m.setMergTemp(true);
                        k.setMergTemp(true);
                        j.setMergTemp(true);
                        i.setMergTemp(true);
                        h.setMergTemp(true);
                        g.setMergTemp(true);
                        f.setMergTemp(true);
                        e.setMergTemp(true);
                        d.setMergTemp(true);
                    }
                    a = false;
                }

            }
        });
    }

    private void e() {
        this.b = true;
        this.c = true;
        d.setFinished(true);
        g.setFinished(true);
        f.setFinished(true);
        e.setFinished(true);
        n.setFinished(true);
        l.setFinished(true);
        m.setFinished(true);
        k.setFinished(true);
        j.setFinished(true);
        i.setFinished(true);
        h.setFinished(true);
        a = false;
    }

    private void a(CleanWxEasyInfo cleanWxEasyInfo, CleanWxItemInfo cleanWxItemInfo) {
        cleanWxEasyInfo.getTempList().add(cleanWxItemInfo);
        if (cleanWxEasyInfo.isMergTemp()) {
            a(cleanWxEasyInfo);
            cleanWxEasyInfo.setMergTemp(false);
            LocalBroadcastManager.getInstance(AppApplication.getInstance()).sendBroadcast(new Intent().setAction(WxNotifyRefrshReceiver.b).putExtra(CleanSwitch.CLEAN_DATA, cleanWxEasyInfo.getTag()));
        }
    }

    /* access modifiers changed from: private */
    public void a(CleanWxEasyInfo cleanWxEasyInfo) {
        int i2 = 0;
        while (true) {
            int i3 = i2;
            if (i3 < cleanWxEasyInfo.getTempList().size()) {
                insertFileToList(cleanWxEasyInfo.getList(), (CleanWxItemInfo) cleanWxEasyInfo.getTempList().get(i3));
                i2 = i3 + 1;
            } else {
                cleanWxEasyInfo.getTempList().clear();
                return;
            }
        }
    }

    public static void insertFileToList(List<MultiItemEntity> list, CleanWxItemInfo cleanWxItemInfo) {
        try {
//            if (CleanWxClearNewActivity.a == cleanWxItemInfo.getDays()) {
//                cleanWxItemInfo.setStringDay("今天");
//            } else if (CleanWxClearNewActivity.a - cleanWxItemInfo.getDays() == 1) {
//                cleanWxItemInfo.setStringDay("昨天");
//            } else {
                cleanWxItemInfo.setStringDay(SimpleDateFormat.getDateInstance().format(new Date(cleanWxItemInfo.getFile().lastModified())));
//            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof CleanWxHeadInfo) {
                    CleanWxHeadInfo cleanWxHeadInfo = (CleanWxHeadInfo) list.get(i);
                    if (cleanWxHeadInfo.getDays() == cleanWxItemInfo.getDays()) {
                        int i2 = 0;
                        while (i2 < cleanWxHeadInfo.getSubItems().size()) {
                            if (cleanWxHeadInfo.getSubItems().get(i2) == null || ((CleanWxFourItemInfo) cleanWxHeadInfo.getSubItems().get(i2)).getFourItem().size() >= 4) {
                                i2++;
                            } else {
                                ((CleanWxFourItemInfo) cleanWxHeadInfo.getSubItems().get(i2)).getFourItem().add(cleanWxItemInfo);
                                cleanWxHeadInfo.setTotalNum(cleanWxHeadInfo.getTotalNum() + 1);
                                return;
                            }
                        }
                        CleanWxFourItemInfo cleanWxFourItemInfo = new CleanWxFourItemInfo();
                        if (cleanWxItemInfo.getFileType() == 8 || cleanWxItemInfo.getFileType() == 10) {
                            cleanWxFourItemInfo.setItemType(12);
                        }
                        cleanWxFourItemInfo.setDays(cleanWxItemInfo.getDays());
                        cleanWxFourItemInfo.getFourItem().add(cleanWxItemInfo);
                        cleanWxHeadInfo.setTotalNum(cleanWxHeadInfo.getTotalNum() + 1);
                        cleanWxHeadInfo.addSubItem(cleanWxFourItemInfo);
                        if (cleanWxHeadInfo.isExpanded()) {
                            list.add(cleanWxHeadInfo.getSubItems().size() + i, cleanWxFourItemInfo);
                            return;
                        }
                        return;
                    }
                }
            }
        } catch (Exception e) {
        }
        try {
            CleanWxHeadInfo cleanWxHeadInfo2 = new CleanWxHeadInfo();
            cleanWxHeadInfo2.setDays(cleanWxItemInfo.getDays());
            cleanWxHeadInfo2.setChecked(cleanWxItemInfo.isChecked());
            cleanWxHeadInfo2.setExpanded(true);
            cleanWxHeadInfo2.setStringDay(cleanWxItemInfo.getStringDay());
            CleanWxFourItemInfo cleanWxFourItemInfo2 = new CleanWxFourItemInfo();
            if (cleanWxItemInfo.getFileType() == 8 || cleanWxItemInfo.getFileType() == 10) {
                cleanWxFourItemInfo2.setItemType(12);
            }
            cleanWxFourItemInfo2.setDays(cleanWxItemInfo.getDays());
            cleanWxFourItemInfo2.getFourItem().add(cleanWxItemInfo);
            cleanWxHeadInfo2.setTotalNum(cleanWxHeadInfo2.getTotalNum() + 1);
            cleanWxFourItemInfo2.setDays(cleanWxItemInfo.getDays());
            cleanWxHeadInfo2.addSubItem(cleanWxFourItemInfo2);
            for (int i3 = 0; i3 < list.size(); i3++) {
                if (list.get(i3) instanceof CleanWxHeadInfo) {
                    if (cleanWxHeadInfo2.getDays() > ((CleanWxHeadInfo) list.get(i3)).getDays()) {
                        list.add(i3, cleanWxHeadInfo2);
                        if (cleanWxHeadInfo2.isExpanded()) {
                            list.add(i3 + 1, cleanWxFourItemInfo2);
                            return;
                        }
                        return;
                    }
                }
            }
            list.add(cleanWxHeadInfo2);
            if (cleanWxHeadInfo2.isExpanded()) {
                list.add(cleanWxFourItemInfo2);
            }
        } catch (Exception e2) {
        }
    }



    public void stopScan() {
        this.p = true;
        a = false;
    }
}