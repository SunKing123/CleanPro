package com.xiaoniu.cleanking.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoniu.cleanking.ui.main.bean.PathData;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;

import java.util.HashMap;
import java.util.List;

/**
 * @author zhengzhihao
 * @date 2019/10/15 16
 * @mail：zhengzhihao@hellogeek.com
 */
public class FilePathUtil {


    public static HashMap<String, PathData> getPathMap(Context context) {
        HashMap<String, PathData> pathDataHashMap = new HashMap<>();
        String pathString = SPUtil.getString(context, "path_data", "");
        List<PathData> pathDataList = new Gson().fromJson(pathString, new TypeToken<List<PathData>>() {}.getType());
        if (pathDataList != null && pathDataList.size() > 0) {
            for (PathData path : pathDataList) {
                pathDataHashMap.put(path.getPackName(), path);
            }
        }
        return pathDataHashMap;
    }
}
