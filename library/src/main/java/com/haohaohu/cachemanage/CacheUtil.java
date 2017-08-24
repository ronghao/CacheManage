package com.haohaohu.cachemanage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.LruCache;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.lang.ref.SoftReference;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 数据缓存类，包括内存缓存和文件缓存
 * 先取内存数据，没有再从文件缓存中取
 *
 * @author haohao on 2017/6/9 14:56
 * @version v1.1
 */
public class CacheUtil {

    public static class CacheUtilHolder {
        private static CacheUtil mInstance = new CacheUtil();
    }

    public static CacheUtil getInstance() {
        return CacheUtilHolder.mInstance;
    }

    private CacheUtilConfig mConfig;

    private SoftReference<LruCache<String, String>> mLuCache =
            new SoftReference<>(new LruCache<String, String>(50));

    /**
     * 初始化工具类，使用之前调用
     *
     * @param config 配置
     */
    public static void init(CacheUtilConfig config) {
        if (config == null) throw new NullPointerException("u should Builder first");
        getInstance().mConfig = config;
        if (getInstance().mConfig.isDes3()) {
            Des3Util.init(getInstance().mConfig.getSecretKey(), getInstance().mConfig.getIv());
        }
    }

    private static CacheUtilConfig getConfig() {
        if (getInstance().mConfig == null) throw new NullPointerException("u should Builder first");
        return getInstance().mConfig;
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (getConfig().getContext() != null) return getConfig().getContext();
        throw new NullPointerException("u should init first");
    }

    /**
     * 获取LruCache,防止弱引用销毁
     */
    private static LruCache<String, String> getLruCache() {
        LruCache<String, String> lruCache = getInstance().mLuCache.get();
        if (lruCache == null) {
            getInstance().mLuCache = new SoftReference<>(new LruCache<String, String>(50));
            lruCache = getInstance().mLuCache.get();
        }
        return lruCache;
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param key 保存的key
     * @param value 保存的value
     */
    public static void put(String key, String value) {
        put(key, value, getConfig().isDes3());
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param key 保存的key
     * @param value 保存的value
     * @param isDes3 是否加密
     */
    public static void put(String key, String value, boolean isDes3) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) return;
        if (getConfig().isMemoryCache()) {
            getLruCache().put(key, value);
        }
        ACache.get(getContext()).put(key, value, isDes3);
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param key 保存的key
     * @param value 保存的value
     * @param time 过期时间
     */
    public static void put(String key, String value, int time) {
        put(key, value, time, getConfig().isDes3());
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param key 保存的key
     * @param value 保存的value
     * @param time 过期时间
     * @param isDes3 是否加密
     */
    public static void put(String key, String value, int time, boolean isDes3) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) return;
        if (getConfig().isMemoryCache()) {
            getLruCache().put(key, Utils.newStringWithDateInfo(time, value));
        }
        ACache.get(getContext()).put(key, value, time, isDes3);
    }

    /**
     * 根据key获取保存的value
     * 先从内存缓存提取，取不到再从文件缓存中获取
     *
     * @param key 要查找的key
     * @return 保存的value
     */
    public static String get(String key) {
        return get(key, getConfig().isDes3());
    }

    /**
     * 根据key获取保存的value
     * 先从内存缓存提取，取不到再从文件缓存中获取
     *
     * @param key 要查找的key
     * @param isDes3 是否加密
     * @return 保存的value
     */
    public static String get(String key, boolean isDes3) {
        if (TextUtils.isEmpty(key)) return "";
        String value;
        if (getConfig().isMemoryCache()) {
            value = getLruCache().get(key);
            if (!TextUtils.isEmpty(value)) {
                if (!Utils.isDue(value)) {
                    return Utils.clearDateInfo(value);
                } else {
                    getLruCache().remove(key);
                    return "";
                }
            }
        }

        value = ACache.get(getContext()).getAsString(key, isDes3);
        if (!TextUtils.isEmpty(value)) {
            if (getConfig().isMemoryCache()) {
                getLruCache().put(key, ACache.get(getContext()).getAsStringHasDate(key, isDes3));
            }
            return value;
        }
        return "";
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param key 保存的key
     * @param value 保存的value
     * @param <T> 对应的实体对象
     */
    public static <T> void put(String key, T value) {
        put(key, value, getConfig().isDes3());
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param <T> 对应的实体对象
     * @param key 保存的key
     * @param value 保存的value
     * @param isDes3 是否加密
     */
    public static <T> void put(String key, T value, boolean isDes3) {
        if (TextUtils.isEmpty(key) || value == null) return;
        Gson gson = new Gson();
        String date;
        if (value instanceof JSONObject) {
            date = value.toString();
        } else if (value instanceof JSONArray) {
            date = value.toString();
        } else {
            date = gson.toJson(value);
        }
        if (getConfig().isMemoryCache()) {
            getLruCache().put(key, date);
        }
        ACache.get(getContext()).put(key, date, isDes3);
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param <T> 对应的实体对象
     * @param key 保存的key
     * @param value 保存的value
     * @param time 过期时间 秒
     */
    public static <T> void put(String key, T value, int time) {
        put(key, value, time, getConfig().isDes3());
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param <T> 对应的实体对象
     * @param key 保存的key
     * @param value 保存的value
     * @param time 过期时间 秒
     * @param isDes3 是否加密
     */
    public static <T> void put(String key, T value, int time, boolean isDes3) {
        if (TextUtils.isEmpty(key) || value == null) return;
        Gson gson = new Gson();
        String date;
        if (value instanceof JSONObject) {
            date = value.toString();
        } else if (value instanceof JSONArray) {
            date = value.toString();
        } else {
            date = gson.toJson(value);
        }
        if (getConfig().isMemoryCache()) {
            getLruCache().put(key, Utils.newStringWithDateInfo(time, date));
        }
        ACache.get(getContext()).put(key, date, time, isDes3);
    }

    /**
     * 根据key获取对象
     * 先从内存缓存提取，取不到再从文件缓存中获取
     *
     * @param key 查找的key
     * @param classOfT 对应的实体对象
     * @param <T> 对应的实体对象
     * @return 实体对象
     */
    public static <T> T get(String key, Class<T> classOfT) {
        return get(key, classOfT, getConfig().isDes3());
    }

    /**
     * 根据key获取对象
     * 先从内存缓存提取，取不到再从文件缓存中获取
     *
     * @param <T> 对应的实体对象
     * @param key 查找的key
     * @param classOfT 对应的实体对象
     * @param isDes3 是否加密
     * @return 实体对象
     */
    public static <T> T get(String key, Class<T> classOfT, boolean isDes3) {
        if (TextUtils.isEmpty(key) || classOfT == null) return null;
        Gson gson = new Gson();
        String value;
        if (getConfig().isMemoryCache()) {
            value = getLruCache().get(key);
            if (!TextUtils.isEmpty(value)) {
                if (!Utils.isDue(value)) {
                    return gson.fromJson(Utils.clearDateInfo(value), classOfT);
                } else {
                    getLruCache().remove(key);
                    try {
                        return classOfT.newInstance();
                    } catch (InstantiationException e) {
                        return null;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        value = ACache.get(getContext()).getAsString(key, isDes3);

        if (!TextUtils.isEmpty(value)) {
            if (getConfig().isMemoryCache()) {
                getLruCache().put(key, ACache.get(getContext()).getAsStringHasDate(key, isDes3));
            }
            return gson.fromJson(value, classOfT);
        }
        try {
            return classOfT.newInstance();
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据key清理内存缓存和文件缓存
     *
     * @param key 要删除的key
     */
    public static void clear(String key) {
        if (TextUtils.isEmpty(key)) return;
        getLruCache().remove(key);
        ACache.get(getContext()).remove(key);
    }

    /**
     * 以.开头的文件默认不显示
     */
    public static String translateKey(String key) {
        return "." + Base64Util.encode(key.getBytes());
    }

    /**
     * 时间计算工具类
     *
     * @author 杨福海（michael）
     * @version 1.0
     */
    private static class Utils {

        private static final char mSeparator = ' ';

        /**
         * 判断缓存的String数据是否到期
         *
         * @param str 保存的str
         * @return true：到期了 false：还没有到期
         */
        private static boolean isDue(String str) {
            return isDue(str.getBytes());
        }

        /**
         * 判断缓存的byte数据是否到期
         *
         * @param data 保存的data
         * @return true：到期了 false：还没有到期
         */
        private static boolean isDue(byte[] data) {
            String[] strs = getDateInfoFromDate(data);
            if (strs != null && strs.length == 2) {
                String saveTimeStr = strs[0];
                while (saveTimeStr.startsWith("0")) {
                    saveTimeStr = saveTimeStr.substring(1, saveTimeStr.length());
                }
                long saveTime = Long.valueOf(saveTimeStr);
                long deleteAfter = Long.valueOf(strs[1]);
                if (System.currentTimeMillis() > saveTime + deleteAfter * 1000) {
                    return true;
                }
            }
            return false;
        }

        private static String newStringWithDateInfo(int second, String strInfo) {
            return createDateInfo(second) + strInfo;
        }

        private static byte[] newByteArrayWithDateInfo(int second, byte[] data2) {
            byte[] data1 = createDateInfo(second).getBytes();
            byte[] retdata = new byte[data1.length + data2.length];
            System.arraycopy(data1, 0, retdata, 0, data1.length);
            System.arraycopy(data2, 0, retdata, data1.length, data2.length);
            return retdata;
        }

        private static String clearDateInfo(String strInfo) {
            if (strInfo != null && hasDateInfo(strInfo.getBytes())) {
                strInfo = strInfo.substring(strInfo.indexOf(mSeparator) + 1, strInfo.length());
            }
            return strInfo;
        }

        private static byte[] clearDateInfo(byte[] data) {
            if (hasDateInfo(data)) {
                return copyOfRange(data, indexOf(data, mSeparator) + 1, data.length);
            }
            return data;
        }

        private static boolean hasDateInfo(byte[] data) {
            return data != null
                    && data.length > 15
                    && data[13] == '-'
                    && indexOf(data, mSeparator) > 14;
        }

        private static String[] getDateInfoFromDate(byte[] data) {
            if (hasDateInfo(data)) {
                String saveDate = new String(copyOfRange(data, 0, 13));
                String deleteAfter = new String(copyOfRange(data, 14, indexOf(data, mSeparator)));
                return new String[] { saveDate, deleteAfter };
            }
            return null;
        }

        private static int indexOf(byte[] data, char c) {
            for (int i = 0; i < data.length; i++) {
                if (data[i] == c) {
                    return i;
                }
            }
            return -1;
        }

        private static byte[] copyOfRange(byte[] original, int from, int to) {
            int newLength = to - from;
            if (newLength < 0) throw new IllegalArgumentException(from + " > " + to);
            byte[] copy = new byte[newLength];
            System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
            return copy;
        }

        private static String createDateInfo(int second) {
            String currentTime = System.currentTimeMillis() + "";
            while (currentTime.length() < 13) {
                currentTime = "0" + currentTime;
            }
            return currentTime + "-" + second + mSeparator;
        }

        /*
         * Bitmap → byte[]
         */
        private static byte[] Bitmap2Bytes(Bitmap bm) {
            if (bm == null) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }

        /*
         * byte[] → Bitmap
         */
        private static Bitmap Bytes2Bimap(byte[] b) {
            if (b.length == 0) {
                return null;
            }
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }

        /*
         * Drawable → Bitmap
         */
        private static Bitmap drawable2Bitmap(Drawable drawable) {
            if (drawable == null) {
                return null;
            }
            // 取 drawable 的长宽
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            // 取 drawable 的颜色格式
            Bitmap.Config config =
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565;
            // 建立对应 bitmap
            Bitmap bitmap = Bitmap.createBitmap(w, h, config);
            // 建立对应 bitmap 的画布
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            // 把 drawable 内容画到画布中
            drawable.draw(canvas);
            return bitmap;
        }

        /*
         * Bitmap → Drawable
         */
        @SuppressWarnings("deprecation")
        private static Drawable bitmap2Drawable(Bitmap bm) {
            if (bm == null) {
                return null;
            }
            return new BitmapDrawable(bm);
        }
    }
}
