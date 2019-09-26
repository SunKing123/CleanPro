package com.xiaoniu.cleanking.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.SecondJunkInfo;

import java.io.File;
import java.util.ArrayList;

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
            value = String.format("%.2f", result);
        } else if (result < 10) {
            value = String.format("%.2f", result);
        } else if (result < 100) {
            value = String.format("%.1f", result);
        } else {
            value = String.format("%.0f", result);
        }
        countEntity.setTotalSize(value);
        countEntity.setUnit(AppApplication.getInstance().getString(suffix));
        countEntity.setResultSize(value + AppApplication.getInstance().getString(suffix));
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

    public static long freeJunkInfos(ArrayList<FirstJunkInfo> junks) {
        long total = 0;
        if (junks != null) {
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= junks.size()) {
                    break;
                }
                if (junks.get(i2) != null && junks.get(i2).isAllchecked()) {
                    total += junks.get(i2).getTotalSize();
                    if (junks.get(i2).getSubGarbages() != null) {
                        for (SecondJunkInfo secondlevelGarbageInfo : junks.get(i2).getSubGarbages()) {
                            if (secondlevelGarbageInfo != null) {
                                try {
                                    com.xiaoniu.common.utils.FileUtils.deleteFileAndFolder(new File(secondlevelGarbageInfo.getFilecatalog()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    try {
                        com.xiaoniu.common.utils.FileUtils.deleteFileAndFolder(new File(junks.get(i2).getGarbageCatalog()));
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                i = i2 + 1;
            }
        }

        return total;
    }


}
