package com.xiaoniu.cleanking.utils.profile;

import android.content.Context;

import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by longer on 18/3/13.
 */

public class FileProfileService {

    private static FileProfileService instance;
    private Context context;
    private PrefAccessor mPrefAccessor;

    private FileProfileService(Context context) {
        mPrefAccessor = new PrefAccessor(context);
    }

    public static FileProfileService getInstance(Context context) {
        if (null == instance) {
            synchronized (FileProfileService.class) {
                instance = new FileProfileService(context);
            }
        }
        return instance;
    }

    public void updateCurrentFile(String key,ArrayList<FileTitleEntity> keyHistoryEntity) {
        mPrefAccessor.saveObject(key, keyHistoryEntity);
    }

    public List<FileTitleEntity> getCurrentFile(String key) {
        return (List<FileTitleEntity>) mPrefAccessor.getObject(key);
    }
}
