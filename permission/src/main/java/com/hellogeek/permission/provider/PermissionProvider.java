package com.hellogeek.permission.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;


;

import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.util.AppUtils;

import java.util.Iterator;

public class PermissionProvider extends ContentProvider {

    public static final String AUTHORITY = ".PreferenceProvider";
    public static Uri AUTHORITY_URI;
    public static final String PARAM_KEY = "key";

    public static final String PARAM_VALUE = "value";

    private final String DB_NAME = "global.sp";
    private SharedPreferences mStore;

    public static Cursor query(Context context, String... keys) {
        return context.getContentResolver().query(getURI(context), keys, null, null, null);
    }


    private static Uri getURI(Context context) {
        if (AUTHORITY_URI == null) {
//            String packageName = PermissionIntegrate.getPermission().getAppPackName();
            String packageName = PermissionIntegrate.getPermission().getAppPackName();
            if (TextUtils.isEmpty(packageName)) {
                packageName = AppUtils.getPackageName(context);
            }
            AUTHORITY_URI = Uri.parse(String.format("content://%s%s", packageName, AUTHORITY));
        }
        return AUTHORITY_URI;
    }

    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    public static String getString(Context context, String key, String defValue) {
        Cursor cursor = query(context, key);
        String ret = defValue;
        if (cursor.moveToNext()) {
            ret = cursor.getString(0);
            if (TextUtils.isEmpty(ret)) {
                ret = defValue;
            }
        }
        cursor.close();
        return ret;
    }

    public static int getInt(Context context, String key, int defValue) {
        Cursor cursor = query(context, key);
        int ret = defValue;
        if (cursor.moveToNext()) {
            try {
                ret = cursor.getInt(0);
            } catch (Exception e) {

            }
        }
        cursor.close();
        return ret;
    }

    public static boolean getBoolen(Context context, String key, boolean defValue) {
        Cursor cursor = query(context, key);
        int ret = defValue ? 1 : 0;
        if (null!= cursor && cursor.moveToNext()) {
            try {
                ret = cursor.getInt(0);
            } catch (Exception e) {

            }
        }
        if(null!= cursor){
            cursor.close();
        }
        boolean isSave = (ret == 1);
        return isSave;
    }

    public static Uri save(Context context, ContentValues values) {
        return context.getContentResolver().insert(getURI(context), values);
    }

    public static Uri save(Context context, String key, String value) {
        ContentValues values = new ContentValues(1);
        values.put(key, value);
        return save(context, values);
    }

    public static Uri save(Context context, String key, boolean value) {
        ContentValues values = new ContentValues(1);
        values.put(key, value ? 1 : 0);
        return save(context, values);
    }

    public static Uri remove(Context context, String key) {
        return save(context, key, null);
    }

    @Override
    public boolean onCreate() {
        mStore = getContext().getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int size = projection == null ? 0 : projection.length;
        if (size > 0) {
            String[] values = new String[size];
            for (int i = 0; i < size; i++) {
                values[i] = getValue(projection[i], null);
            }
            return createCursor(projection, values);
        }
        String key = uri.getQueryParameter(PARAM_KEY);
        String value = null;
        if (!TextUtils.isEmpty(key)) {
            value = getValue(key, null);
        }
        return createSingleCursor(key, value);
    }

    protected Cursor createSingleCursor(String key, String value) {
        MatrixCursor cursor = new MatrixCursor(new String[]{key}, 1);
        if (!TextUtils.isEmpty(value)) {
            cursor.addRow(new Object[]{value});
        }
        return cursor;
    }

    protected Cursor createCursor(String[] keys, String[] values) {
        MatrixCursor cursor = new MatrixCursor(keys, 1);
        cursor.addRow(values);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (values != null && values.size() > 0) {
            save(values);
        } else {
            String key = uri.getQueryParameter(PARAM_KEY);
            String value = uri.getQueryParameter(PARAM_VALUE);
            if (!TextUtils.isEmpty(key)) {
                save(key, value);
            }
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String key = selection == null ? selection : uri.getQueryParameter(PARAM_KEY);
        if (!TextUtils.isEmpty(key)) {
            remove(key);
            return 1;
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (values != null && values.size() > 0) {
            save(values);
            return values.size();
        }
        String key = uri.getQueryParameter(PARAM_KEY);
        String value = uri.getQueryParameter(PARAM_VALUE);
        if (!TextUtils.isEmpty(key)) {
            save(key, value);
            return 1;
        }
        return 0;
    }

    protected String getValue(String key, String defValue) {
        return mStore.getString(key, defValue);
    }

    protected void save(ContentValues values) {
        String key;
        String value;
        Iterator<String> iterator = values.keySet().iterator();
        SharedPreferences.Editor editor = mStore.edit();
        while (iterator.hasNext()) {
            key = iterator.next();
            value = values.getAsString(key);
            if (!TextUtils.isEmpty(key)) {
                if (value != null) {
                    editor.putString(key, value);
                } else {
                    editor.remove(key);
                }
            }
        }
        editor.commit();
    }

    protected void save(String key, String value) {
        SharedPreferences.Editor editor = mStore.edit();
        if (value != null) {
            editor.putString(key, value);
        } else {
            editor.remove(key);
        }
        editor.commit();
    }

    protected void remove(String key) {
        SharedPreferences.Editor editor = mStore.edit();
        editor.remove(key);
        editor.commit();
    }

}
