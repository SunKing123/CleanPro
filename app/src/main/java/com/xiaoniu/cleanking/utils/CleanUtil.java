package com.xiaoniu.cleanking.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.bean.OtherJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.SecondJunkInfo;
import com.xiaoniu.common.utils.ContextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by mazhuang on 16/1/14.
 */
public class CleanUtil {
    public static String formatShortFileSize(Context context, long number) {
        if (context == null) {
            return "";
        }

        float result = number;
        int suffix = R.string.byte_short;
        if (result >= 1024) {
            suffix = R.string.kilo_byte_short;
            result = result / 1024;
        }
        if (result >= 1024) {
            suffix = R.string.mega_byte_short;
            result = result / 1024;
        }
        if (result >= 1024) {
            suffix = R.string.giga_byte_short;
            result = result / 1024;
        }
        if (result >= 1024) {
            suffix = R.string.tera_byte_short;
            result = result / 1024;
        }
        if (result >= 1024) {
            suffix = R.string.peta_byte_short;
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format("%.2f", result);
        } else if (result < 10) {
            value = String.format("%.2f", result);
        } else if (result < 100) {
            value = String.format("%.1f", result);
        } else {
            value = String.format("%.0f", result);
        }
        return context.getResources().
                getString(R.string.clean_file_size_suffix,
                        value, context.getString(suffix));
    }

    public static CountEntity formatShortFileSize(long number) {

        CountEntity countEntity = new CountEntity();

        float result = number;
        int suffix = R.string.byte_short;
        if (result >= 1024) {
            suffix = R.string.kilo_byte_short;
            result = result / 1024;
        }
        if (result >= 1024) {
            suffix = R.string.mega_byte_short;
            result = result / 1024;
        }
        if (result >= 1024) {
            suffix = R.string.giga_byte_short;
            result = result / 1024;
        }
        if (result >= 1024) {
            suffix = R.string.tera_byte_short;
            result = result / 1024;
        }
        if (result >= 1024) {
            suffix = R.string.peta_byte_short;
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format(Locale.ENGLISH,"%.2f", result);
        } else if (result < 10) {
            value = String.format(Locale.ENGLISH,"%.2f", result);
        } else if (result < 100) {
            value = String.format(Locale.ENGLISH,"%.1f", result);
        } else {
            value = String.format(Locale.ENGLISH,"%.0f", result);
        }
        countEntity.setTotalLong(number);
        countEntity.setTotalSize(value);
        if (null != AppApplication.getInstance()) {
            try {
                //修复友盟bug 有时候getString()找不到
                countEntity.setUnit(AppApplication.getInstance().getString(suffix));
                countEntity.setResultSize(value + AppApplication.getInstance().getString(suffix));
            }catch (Exception e){

            }

        }
        countEntity.setNumber(number);
        return countEntity;
    }

    public static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            for (String name : children) {
                boolean suc = deleteFile(new File(file, name));
                if (!suc) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    public static void killAppProcesses(String packageName, int pid) {
        if (packageName == null || packageName.isEmpty()) {
            return;
        }

        ActivityManager am = (ActivityManager) AppApplication.getInstance()
                .getSystemService(Context.ACTIVITY_SERVICE);
        try {
            if (!FileUtils.isSystemApK(packageName)) {
                am.killBackgroundProcesses(packageName);
            }
        } catch (Exception e) {
            if (pid != 0) {
                try {
                    Process.killProcess(pid);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        }

    }

    //释放指定目录文件垃圾
    public static long freeJunkInfos(ArrayList<FirstJunkInfo> junks) {
        long total = 0;
        if (junks != null) {
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= junks.size()) {
                    break;
                }
                if (junks.get(i2) != null && (junks.get(i2).isAllchecked()|| (junks.get(i2).isIsthreeLevel()&&junks.get(i2).isUncarefulIsChecked())||(junks.get(i2).isIsthreeLevel()&&junks.get(i2).isCarefulIsChecked()) || (junks.get(i2).isSomeShecked()&&(junks.get(i2).isUncarefulIsChecked() || junks.get(i2).isCarefulIsChecked())))) {
                    total += junks.get(i2).getTotalSize();
                    if (junks.get(i2).getSubGarbages() != null) {
                        for (SecondJunkInfo secondlevelGarbageInfo : junks.get(i2).getSubGarbages()) {
                            if(!secondlevelGarbageInfo.isChecked() && !junks.get(i2).isCarefulIsChecked())//需要手动删除的情况,未选中删除，默认不删除
                                continue;
                            if (secondlevelGarbageInfo != null ) {
                                try {
                                    if (secondlevelGarbageInfo.getFilecatalog() != null) {
                                        File delFile = new File(secondlevelGarbageInfo.getFilecatalog());
                                        if (delFile != null && delFile.exists()) {
                                            com.xiaoniu.common.utils.FileUtils.deleteFileAndFolder(delFile);
                                        }
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    try {
                        if (junks.get(i2).getGarbageCatalog() != null) {
                            File delFile = new File(junks.get(i2).getGarbageCatalog());
                            if (delFile != null && delFile.exists()) {
                                com.xiaoniu.common.utils.FileUtils.deleteFileAndFolder(new File(junks.get(i2).getGarbageCatalog()));
                            }
                        }

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                i = i2 + 1;
            }
        }

        return total;
    }


    //释放指定目录文件垃圾
    public static long freeOtherJunkInfos(ArrayList<OtherJunkInfo> junks) {
        long total = 0;
        if (junks != null) {
            for (OtherJunkInfo otherJunkInfo : junks) {
                total += otherJunkInfo.getGarbageSize();
                if (otherJunkInfo != null) {
                    try {
                        if (otherJunkInfo.getFilecatalog() != null) {
                            File delFile = new File(otherJunkInfo.getFilecatalog());
                            if (delFile != null && delFile.exists()) {
                                com.xiaoniu.common.utils.FileUtils.deleteFileAndFolder(delFile);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return total;
    }

    //计算当前一级总数
    public static long getTotalSize(HashMap<Integer, JunkGroup> mJunkGroups) {
        long size = 0L;
        if (CollectionUtils.isEmpty(mJunkGroups)) {
            return size;
        }
        try {
            for (JunkGroup group : mJunkGroups.values()) {
                if (group.mChildren.size() > 0) {
                    for (FirstJunkInfo firstJunkInfo : group.mChildren) {
                        if (firstJunkInfo.isAllchecked()) {
                            size += firstJunkInfo.getTotalSize();
                        }
                    }
                } else if (group.mName.equals(ContextUtils.getContext().getString(R.string.other_clean)) && group.isChecked) {//其他垃圾类目单独处理
                    size += group.mSize;
                } else if (group.mName.equals(ContextUtils.getContext().getString(R.string.process_clean)) && group.isChecked) {
                    size += group.mSize;
                }

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return size;
    }
}
