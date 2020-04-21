package com.haohaohu.cachemanage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.LruCache;
import com.google.gson.Gson;
import com.haohaohu.cachemanage.observer.CacheObserver;
import com.haohaohu.cachemanage.util.Base64Util;
import com.haohaohu.cachemanage.util.LockUtil;
import com.haohaohu.cachemanage.util.Md5Utils;
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

    private CacheUtilConfig mConfig;
    private SoftReference<LruCache<String, String>> mLuCache =
            new SoftReference<>(new LruCache<String, String>(50));

    private static CacheUtil getInstance() {
        return CacheUtilHolder.mInstance;
    }

    /**
     * 初始化工具类，使用之前调用
     *
     * @param config 配置
     */
    public static void init(CacheUtilConfig config) {
        if (config == null) {
            throw new NullPointerException("u should Builder first");
        }
        getInstance().mConfig = config;
    }

    protected static CacheUtilConfig getConfig() {
        if (getInstance().mConfig == null) {
            throw new NullPointerException("u should Builder first");
        }
        return getInstance().mConfig;
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    private static Context getContext() {
        if (getConfig().getContext() != null) {
            return getConfig().getContext();
        }
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
    public static void put(String key, @NonNull String value) {
        put(key, value, getConfig().isEncrypt());
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param key 保存的key
     * @param value 保存的value
     * @param isEncrypt 是否加密
     */
    public static void put(String key, @NonNull String value, boolean isEncrypt) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }
        LockUtil.getInstance().writeLock().lock();
        if (getConfig().isKeyEncrypt()) {
            key = translateSecretKey(key);
        }
        if (getConfig().isMemoryCache()) {
            getLruCache().put(key, value);
        }
        getConfig().getACache().put(key, value, isEncrypt);
        CacheObserver.getInstance().notifyDataChange(key, value);
        LockUtil.getInstance().writeLock().unlock();
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param key 保存的key
     * @param value 保存的value
     * @param time 过期时间
     */
    public static void put(String key, @NonNull String value, int time) {
        put(key, value, time, getConfig().isEncrypt());
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param key 保存的key
     * @param value 保存的value
     * @param time 过期时间
     * @param isEncrypt 是否加密
     */
    public static void put(String key, @NonNull String value, int time, boolean isEncrypt) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }
        LockUtil.getInstance().writeLock().lock();
        if (getConfig().isKeyEncrypt()) {
            key = translateSecretKey(key);
        }
        if (getConfig().isMemoryCache()) {
            getLruCache().put(key, Utils.newStringWithDateInfo(time, value));
        }
        getConfig().getACache().put(key, value, time, isEncrypt);
        CacheObserver.getInstance().notifyDataChange(key, value);
        LockUtil.getInstance().writeLock().unlock();
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param key 保存的key
     * @param value 保存的value
     * @param <T> 对应的实体对象
     */
    public static <T> void put(String key, @NonNull T value) {
        put(key, value, getConfig().isEncrypt());
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param <T> 对应的实体对象
     * @param key 保存的key
     * @param value 保存的value
     * @param isEncrypt 是否加密
     */
    public static <T> void put(String key, @NonNull T value, boolean isEncrypt) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        Gson gson = new Gson();
        String date;
        if (value instanceof JSONObject) {
            date = value.toString();
        } else if (value instanceof JSONArray) {
            date = value.toString();
        } else {
            date = gson.toJson(value);
        }

        put(key, date, isEncrypt);
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param <T> 对应的实体对象
     * @param key 保存的key
     * @param value 保存的value
     * @param time 过期时间 秒
     */
    public static <T> void put(String key, @NonNull T value, int time) {
        put(key, value, time, getConfig().isEncrypt());
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param <T> 对应的实体对象
     * @param key 保存的key
     * @param value 保存的value
     * @param time 过期时间 秒
     * @param isEncrypt 是否加密
     */
    public static <T> void put(String key, @NonNull T value, int time, boolean isEncrypt) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        Gson gson = new Gson();
        String date;
        if (value instanceof JSONObject) {
            date = value.toString();
        } else if (value instanceof JSONArray) {
            date = value.toString();
        } else {
            date = gson.toJson(value);
        }

        put(key, date, time, isEncrypt);
    }

    /**
     * 根据key获取保存的value
     * 先从内存缓存提取，取不到再从文件缓存中获取
     *
     * @param key 要查找的key
     * @return 保存的value
     */
    @Nullable
    public static String get(String key) {
        return get(key, getConfig().isEncrypt());
    }

    /**
     * 根据key获取保存的value
     * 先从内存缓存提取，取不到再从文件缓存中获取
     *
     * @param key 要查找的key
     * @param isEncrypt 是否加密
     * @return 保存的value
     */
    @Nullable
    public static String get(String key, boolean isEncrypt) {
        if (TextUtils.isEmpty(key)) {
            return "";
        }
        try {
            LockUtil.getInstance().readLock().lock();
            if (getConfig().isKeyEncrypt()) {
                key = translateSecretKey(key);
            }
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

            value = getConfig().getACache().getAsString(key, isEncrypt);
            if (!TextUtils.isEmpty(value)) {
                if (getConfig().isMemoryCache()) {
                    getLruCache().put(key,
                            getConfig().getACache().getAsStringHasDate(key, isEncrypt));
                }
                return value;
            }
            return "";
        } catch (Exception e) {
            return "";
        } finally {
            LockUtil.getInstance().readLock().unlock();
        }
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
    @Nullable
    public static <T> T get(String key, Class<T> classOfT) {
        return get(key, classOfT, getConfig().isEncrypt());
    }

    /**
     * 根据key获取对象
     * 先从内存缓存提取，取不到再从文件缓存中获取
     *
     * @param <T> 对应的实体对象
     * @param key 查找的key
     * @param classOfT 对应的实体对象
     * @param isEncrypt 是否加密
     * @return 实体对象
     */
    @Nullable
    public static <T> T get(String key, Class<T> classOfT, boolean isEncrypt) {
        if (TextUtils.isEmpty(key) || classOfT == null) {
            return null;
        }
        try {
            LockUtil.getInstance().readLock().lock();
            if (getConfig().isKeyEncrypt()) {
                key = translateSecretKey(key);
            }
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
            value = getConfig().getACache().getAsString(key, isEncrypt);

            if (!TextUtils.isEmpty(value)) {
                if (getConfig().isMemoryCache()) {
                    getLruCache().put(key,
                            getConfig().getACache().getAsStringHasDate(key, isEncrypt));
                }
                return gson.fromJson(value, classOfT);
            }
            return classOfT.newInstance();
        } catch (Exception e) {
            return null;
        } finally {
            LockUtil.getInstance().readLock().unlock();
        }
    }

    /**
     * 根据key获取对象
     * 先从内存缓存提取，取不到再从文件缓存中获取
     *
     * @param <T> 对应的实体对象
     * @param key 查找的key
     * @param classOfT 对应的实体对象
     * @param t 错误情况下返回数据
     * @return 实体对象
     */
    public static <T> T get(String key, Class<T> classOfT, T t) {
        return get(key, classOfT, t, getConfig().isEncrypt());
    }

    /**
     * 根据key获取对象
     * 先从内存缓存提取，取不到再从文件缓存中获取
     *
     * @param <T> 对应的实体对象
     * @param key 查找的key
     * @param classOfT 对应的实体对象
     * @param t 错误情况下返回数据
     * @param isEncrypt 是否加密
     * @return 实体对象
     */
    public static <T> T get(String key, Class<T> classOfT, @NonNull T t, boolean isEncrypt) {
        if (TextUtils.isEmpty(key) || classOfT == null) {
            return t;
        }
        try {
            LockUtil.getInstance().readLock().lock();
            Gson gson = new Gson();
            String value;
            if (getConfig().isKeyEncrypt()) {
                key = translateSecretKey(key);
            }
            if (getConfig().isMemoryCache()) {
                value = getLruCache().get(key);
                if (!TextUtils.isEmpty(value)) {
                    if (!Utils.isDue(value)) {
                        return gson.fromJson(Utils.clearDateInfo(value), classOfT);
                    } else {
                        getLruCache().remove(key);
                        return t;
                    }
                }
            }
            value = getConfig().getACache().getAsString(key, isEncrypt);

            if (!TextUtils.isEmpty(value)) {
                if (getConfig().isMemoryCache()) {
                    getLruCache().put(key,
                            getConfig().getACache().getAsStringHasDate(key, isEncrypt));
                }
                return gson.fromJson(value, classOfT);
            }
            return t;
        } catch (Exception e) {
            return t;
        } finally {
            LockUtil.getInstance().readLock().unlock();
        }
    }

    /**
     * 根据key清理内存缓存和文件缓存
     *
     * @param key 要删除的key
     */
    public static void clear(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (getConfig().isKeyEncrypt()) {
            key = translateSecretKey(key);
        }
        LockUtil.getInstance().writeLock().lock();
        getLruCache().remove(key);
        getConfig().getACache().remove(key);
        LockUtil.getInstance().writeLock().unlock();
    }

    /**
     * 根据key清理内存缓存
     *
     * @param key 要删除的key
     */

    public static void clearMemory(@NonNull String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (getConfig().isKeyEncrypt()) {
            key = translateSecretKey(key);
        }
        getLruCache().remove(key);
    }

    /**
     * 清理所有内存缓存
     */
    public static void clearAllMemory() {
        getLruCache().evictAll();
    }

    /**
     * 清理所有缓存
     */
    public static void clearAll() {
        getLruCache().evictAll();
        getConfig().getACache().clear();
    }

    /**
     * 以.开头的文件默认不显示
     */
    public static String translateKey(@NonNull String key) {
        return "." + Base64Util.encode(key.getBytes());
    }

    /**
     * keyMd5编码
     */
    public static String translateSecretKey(@NonNull String key) {
        return Md5Utils.md5(key);
    }

    private static class CacheUtilHolder {
        private static CacheUtil mInstance = new CacheUtil();
    }

    /**
     * 时间计算工具类
     *
     * @author 杨福海（michael）
     * @version 1.0
     */
    private static class Utils {

        private static final char SEPARATOR = ' ';

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
                strInfo = strInfo.substring(strInfo.indexOf(SEPARATOR) + 1, strInfo.length());
            }
            return strInfo;
        }

        private static byte[] clearDateInfo(byte[] data) {
            if (hasDateInfo(data)) {
                return copyOfRange(data, indexOf(data, SEPARATOR) + 1, data.length);
            }
            return data;
        }

        private static boolean hasDateInfo(byte[] data) {
            return data != null
                    && data.length > 15
                    && data[13] == '-'
                    && indexOf(data, SEPARATOR) > 14;
        }

        private static String[] getDateInfoFromDate(byte[] data) {
            if (hasDateInfo(data)) {
                String saveDate = new String(copyOfRange(data, 0, 13));
                String deleteAfter = new String(copyOfRange(data, 14, indexOf(data, SEPARATOR)));
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
            if (newLength < 0) {
                throw new IllegalArgumentException(from + " > " + to);
            }
            byte[] copy = new byte[newLength];
            System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
            return copy;
        }

        private static String createDateInfo(int second) {
            StringBuilder currentTime = new StringBuilder(System.currentTimeMillis() + "");
            while (currentTime.length() < 13) {
                currentTime.insert(0, "0");
            }
            return currentTime + "-" + second + SEPARATOR;
        }

        /**
         * Bitmap → byte[]
         *
         * @param bm 图片bitmap
         */
        private static byte[] bitmap2Bytes(Bitmap bm) {
            if (bm == null) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }

        /**
         * byte[] → Bitmap
         */
        private static Bitmap bytes2Bimap(byte[] b) {
            if (b.length == 0) {
                return null;
            }
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }

        /**
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

        /**
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
