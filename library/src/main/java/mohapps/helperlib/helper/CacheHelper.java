package mohapps.helperlib.helper;

import android.content.Context;

import com.haohaohu.cachemanage.CacheUtil;
import com.haohaohu.cachemanage.CacheUtilConfig;
import com.haohaohu.cachemanage.strategy.KeyStoreEncryptStrategy;


public class CacheHelper {
   
    public static CacheUtilConfig getCacheUtilConfig(Context context){
        return CacheUtilConfig.builder(context)
                .setIEncryptStrategy(new KeyStoreEncryptStrategy(context, "cacheUtil"))
                .allowMemoryCache(true)
                .allowEncrypt(false)
                .build();
    }

    public static void save(String key, String value, boolean isEncrypt) {
        CacheUtil.put(key, value, isEncrypt);
    }

    public static void save(String key, String value) {
        save(key, value, false);
    }

    public static String read(String key, boolean isEncrypt) {
        return CacheUtil.get(key, isEncrypt);
    }

    public static String read(String key) {
        return read(key, false);
    }
}
