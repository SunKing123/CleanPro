package com.comm.jksdk.utils;

import android.os.Build;
import android.util.ArrayMap;
import android.util.ArraySet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CollectionUtils {
    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    public static boolean isEmpty(Map m) {
        return m == null || m.isEmpty();
    }

    public static int size(byte[] array) {
        return array == null ? 0 : array.length;
    }

    public static int size(char[] array) {
        return array == null ? 0 : array.length;
    }

    public static int size(short[] array) {
        return array == null ? 0 : array.length;
    }

    public static int size(int[] array) {
        return array == null ? 0 : array.length;
    }

    public static int size(long[] array) {
        return array == null ? 0 : array.length;
    }

    public static int size(float[] array) {
        return array == null ? 0 : array.length;
    }

    public static int size(double[] array) {
        return array == null ? 0 : array.length;
    }

    public static int size(boolean[] array) {
        return array == null ? 0 : array.length;
    }

    public static int size(Object[] array) {
        return array == null ? 0 : array.length;
    }

    public static int size(Collection c) {
        return c == null ? 0 : c.size();
    }

    public static int size(Map m) {
        return m == null ? 0 : m.size();
    }

    public static <T> Set<T> createSet() {
        return createSet(0);
    }

    public static <T> Set<T> createSet(int capacity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new ArraySet<>(capacity);
        } else {
            return new HashSet<>(capacity);
        }
    }

    public static <K, V> Map<K, V> createMap() {
        return createMap(0);
    }

    public static <K, V> Map<K, V> createMap(int capacity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new ArrayMap<>(capacity);
        } else {
            return new HashMap<>(capacity);
        }
    }

    public interface MapTraversalCallBack<K, V> {
        boolean findObject(K key, V value);
    }

    public static <K, V> void traversalMap(Map<K, V> map, MapTraversalCallBack<K, V> callBack) {
        if (map != null && map.size() > 0) {
            Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<K, V> entry = it.next();
                boolean find = callBack.findObject(entry.getKey(), entry.getValue());

                if (find) {
                    break;
                }
            }
        }
    }

    public interface SetTraversalCallBack<V> {
        boolean findObject(V value);
    }

    public static <V> void traversalSet(Set<V> set, SetTraversalCallBack<V> callBack) {
        if (set != null && set.size() > 0) {
            Iterator<V> it = set.iterator();
            while (it.hasNext()) {
                V entry = it.next();
                boolean find = callBack.findObject(entry);

                if (find) {
                    break;
                }
            }
        }
    }

    /**
     * 查询对象在集合中的位置.
     *
     * @param l 数据集合
     * @param o 要查询位置的数据
     * @return 成功返回位置, 失败返回-1
     */
    public static int getPositionByList(List l, Object o) {
        for (int i = 0, size = size(l); i < size; i++) {
            if (l.get(i) == o) return i;
        }
        return -1;
    }

    /**
     * 比较两个集中的数据是否完全一样.
     * 如果集中的数据是对象,要比较的话,请重写对象的equals().
     *
     * @param c1 集1
     * @param c2 集2
     * @return true, 两个集中数据完全相同, 但可能数据位置不同.false, 两个集中数据不全相同.
     */
    public static boolean equalsContent(Collection c1, Collection c2) {
        if (c1 == c2) return true;
        int size1 = size(c1), size2 = size(c2);
        if (size1 != size2) return false;
        if (size1 == 0) return true;

        for (Object object : c1) if (!c2.contains(object)) return false;

        return true;
    }


    public static boolean isListNullOrEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
}
