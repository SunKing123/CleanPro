package com.xiaoniu.cleanking.utils.profile;

import java.io.IOException;
import java.io.Serializable;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * save application params for the user,
 * <p>
 * 2015-10-20
 *
 * @version 1.0
 */


public class PrefAccessor {
    private static final String PREFERENCE_NAME = "files_c_c";

    private SharedPreferences mSharedPreferences;


    public PrefAccessor(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void saveString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).commit();
    }

    public void saveBoolean(String key, boolean boo) {
        mSharedPreferences.edit().putBoolean(key, boo).commit();
    }

    public void saveInt(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).commit();
    }

    public void saveFloat(String key, float value) {
        mSharedPreferences.edit().putFloat(key, value).commit();
    }

    public void saveLong(String key, long value) {
        mSharedPreferences.edit().putLong(key, value).commit();
    }

    public boolean saveObject(String key, Serializable dataObj) {
        Editor editor = mSharedPreferences.edit();
        try {
            editor.putString(key, ObjectSerializer.serialize(dataObj));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public String getString(String key, String... defValue) {
        if (defValue.length > 0)
            return mSharedPreferences.getString(key, defValue[0]);
        else
            return mSharedPreferences.getString(key, "");
    }

    public boolean getBoolean(String key, Boolean... defValue) {
        if (defValue.length > 0)
            return mSharedPreferences.getBoolean(key, defValue[0]);
        else
            return mSharedPreferences.getBoolean(key, true);
    }

    public int getInt(String key, Integer... defValue) {
        if (defValue.length > 0)
            return mSharedPreferences.getInt(key, defValue[0]);
        else
            return mSharedPreferences.getInt(key, 0);
    }

    public float getFloat(String key, Float... defValue) {
        if (defValue.length > 0)
            return mSharedPreferences.getFloat(key, defValue[0]);
        else
            return mSharedPreferences.getFloat(key, 0);
    }

    public long getLong(String key, Long... defValue) {
        if (defValue.length > 0)
            return mSharedPreferences.getLong(key, defValue[0]);
        else
            return mSharedPreferences.getLong(key, 0);
    }

    public Object getObject(String objKey) {
        Object dataObj = null;
        try {
            dataObj = ObjectSerializer.deserialize(mSharedPreferences.getString(objKey, null));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dataObj;
    }

}
