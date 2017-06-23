package com.haohaohu.cachemanage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.LruCache;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.SoftReference;

/**
 * 数据缓存类，包括内存缓存和文件缓存
 * 先取内存数据，没有再从文件缓存中取
 *
 * @author haohao on 2017/6/9 14:56
 * @version v1.0
 */
public class CacheUtil {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    private static CacheUtil mInstance;
    private static boolean mIsDes3 = false;//默认不加密

    private static String secretKey = "WLIJkjdsfIlI789sd87dnu==";
    private static String iv = "haohaoha";

    private SoftReference<LruCache<String, String>> mLuCache =
            new SoftReference<>(new LruCache<String, String>(50));

    private CacheUtil() {
        if (context == null) {
            throw new UnsupportedOperationException("you can't init me,Crying");
        }
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        CacheUtil.context = context.getApplicationContext();
        Des3Util.init(secretKey, iv);
    }

    /**
     * 初始化工具类
     *
     * @param context   上下文
     * @param secretKey 密钥
     * @param iv        向量，8位字符
     */
    public static void init(Context context, String secretKey, String iv) {
        CacheUtil.context = context.getApplicationContext();
        Des3Util.init(secretKey, iv);
    }

    /**
     * 初始化工具类
     *
     * @param context   上下文
     * @param secretKey 密钥
     * @param iv        向量，8位字符
     * @param isDes3    默认是否加密
     */
    public static void init(Context context, String secretKey, String iv, boolean isDes3) {
        CacheUtil.context = context.getApplicationContext();
        CacheUtil.mIsDes3 = isDes3;
        Des3Util.init(secretKey, iv);
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }

    private static CacheUtil getInstance() {
        if (mInstance == null) {
            synchronized (CacheUtil.class) {
                if (mInstance == null) {
                    mInstance = new CacheUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取LruCache,防止弱引用销毁
     */
    private static LruCache<String, String> getLruCache() {
        LruCache<String, String> lruCache = getInstance().mLuCache.get();
        if (lruCache == null) {
            getInstance().mLuCache =
                    new SoftReference<>(new LruCache<String, String>(50));
            lruCache = getInstance().mLuCache.get();
        }
        return lruCache;
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param key   保存的key
     * @param value 保存的value
     */
    public static void put(String key, String value) {
        put(key, value, mIsDes3);
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param key    保存的key
     * @param value  保存的value
     * @param isDes3 是否加密
     */
    public static void put(String key, String value, boolean isDes3) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value))
            return;
        getLruCache().put(key, value);
        ACache.get(getContext()).put(key, value, isDes3);
    }


    /**
     * 根据key获取保存的value
     * 先从内存缓存提取，取不到再从文件缓存中获取
     *
     * @param key 要查找的key
     * @return 保存的value
     */
    public static String get(String key) {
        return get(key, mIsDes3);
    }

    /**
     * 根据key获取保存的value
     * 先从内存缓存提取，取不到再从文件缓存中获取
     *
     * @param key    要查找的key
     * @param isDes3 是否加密
     * @return 保存的value
     */
    public static String get(String key, boolean isDes3) {
        if (TextUtils.isEmpty(key))
            return "";
        String value = getLruCache().get(key);
        if (!TextUtils.isEmpty(value)) {
            return value;
        }
        value = ACache.get(getContext()).getAsString(key, isDes3);
        if (!TextUtils.isEmpty(value)) {
            getLruCache().put(key, value);
            return value;
        }
        return "";
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param key   保存的key
     * @param value 保存的value
     * @param <T>   对应的实体对象
     */
    public static <T> void put(String key, T value) {
        put(key, value, mIsDes3);
    }

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param <T>    对应的实体对象
     * @param key    保存的key
     * @param value  保存的value
     * @param isDes3 是否加密
     */
    public static <T> void put(String key, T value, boolean isDes3) {
        if (TextUtils.isEmpty(key) || value == null)
            return;
        Gson gson = new Gson();
        String date;
        if (value instanceof JSONObject) {
            date = value.toString();
        } else if (value instanceof JSONArray) {
            date = value.toString();
        } else {
            date = gson.toJson(value);
        }
        getLruCache().put(key, date);
        ACache.get(getContext()).put(key, date, isDes3);
    }


    /**
     * 根据key获取对象
     * 先从内存缓存提取，取不到再从文件缓存中获取
     *
     * @param key      查找的key
     * @param classOfT 对应的实体对象
     * @param <T>      对应的实体对象
     * @return 实体对象
     */
    public static <T> T get(String key, Class<T> classOfT) {
        return get(key, classOfT, mIsDes3);
    }

    /**
     * 根据key获取对象
     * 先从内存缓存提取，取不到再从文件缓存中获取
     *
     * @param <T>      对应的实体对象
     * @param key      查找的key
     * @param classOfT 对应的实体对象
     * @param isDes3   是否加密
     * @return 实体对象
     */
    public static <T> T get(String key, Class<T> classOfT, boolean isDes3) {
        if (TextUtils.isEmpty(key) || classOfT == null)
            return null;
        Gson gson = new Gson();
        String value = getLruCache().get(key);
        if (!TextUtils.isEmpty(value)) {
            return gson.fromJson(value, classOfT);
        }
        value = ACache.get(getContext()).getAsString(key, isDes3);
        if (!TextUtils.isEmpty(value)) {
            getLruCache().put(key, value);
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
}
