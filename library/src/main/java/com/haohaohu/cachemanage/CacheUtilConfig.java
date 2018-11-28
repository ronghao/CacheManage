package com.haohaohu.cachemanage;

import android.content.Context;
import android.os.Build;

import com.haohaohu.cachemanage.strategy.Des3EncryptStrategy;
import com.haohaohu.cachemanage.strategy.IEncryptStrategy;

import java.io.File;

/**
 * 配置项
 *
 * @author haohao on 2017/8/24 10:37
 * @version v1.0
 */
public class CacheUtilConfig {

    private Context context;
    private boolean isEncrypt;
    private boolean memoryCache;
    private ACache aCache;

    private IEncryptStrategy mIEncryptStrategy;

    private CacheUtilConfig(Builder builder) {
        context = builder.context;
        isEncrypt = builder.isEncrypt;
        memoryCache = builder.memoryCache;
        aCache = builder.aCache;
        mIEncryptStrategy = builder.iEncryptStrategy;
    }

    public static Builder builder(Context context) {
        return new Builder(context.getApplicationContext());
    }

    public Context getContext() {
        return context;
    }

    public boolean isEncrypt() {
        return isEncrypt;
    }

    public boolean isMemoryCache() {
        return memoryCache;
    }

    public ACache getACache() {
        return aCache;
    }

    public void setACache(ACache aCache) {
        this.aCache = aCache;
    }

    public IEncryptStrategy getIEncryptStrategy() {
        return mIEncryptStrategy;
    }

    public void setIEncryptStrategy(IEncryptStrategy mIEncryptStrategy) {
        this.mIEncryptStrategy = mIEncryptStrategy;
    }

    public static class Builder {
        private Context context;
        private boolean isEncrypt = true;//默认加密
        private boolean memoryCache = true;//默认保存到内存
        private File file;
        private boolean isPreventPowerDelete = false;//防止被删除

        private ACache aCache;//ACache示例
        public IEncryptStrategy iEncryptStrategy;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder allowMemoryCache(boolean memoryCache) {
            this.memoryCache = memoryCache;
            return this;
        }

        public Builder setACache(ACache aCache) {
            this.aCache = aCache;
            return this;
        }

        public Builder setIEncryptStrategy(IEncryptStrategy iEncryptStrategy) {
            this.iEncryptStrategy = iEncryptStrategy;
            return this;
        }

        public Builder allowEncrypt(boolean isEncrypt) {
            this.isEncrypt = isEncrypt;
            return this;
        }

        public Builder preventPowerDelete(boolean isPreventPowerDelete) {
            this.isPreventPowerDelete = isPreventPowerDelete;
            return this;
        }

        public CacheUtilConfig build() {
            if (this.iEncryptStrategy == null) {
                iEncryptStrategy = new Des3EncryptStrategy(context);
            }
            if (this.aCache == null) {
                if (isPreventPowerDelete) {
                    File file = context.getDatabasePath("cachetest");
                    if (file == null || !file.exists()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            context.openOrCreateDatabase("cachetest",
                                    Context.MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
                        }
                        file = context.getDatabasePath("cachetest");
                    }
                    File cacheFile = new File(file.getParent(), "cachemanage/");
                    if (cacheFile.exists() && cacheFile.isFile()) {
                        cacheFile.delete();
                    }
                    if (!cacheFile.exists()) {
                        cacheFile.mkdirs();
                    }

                    this.aCache = ACache.get(cacheFile);
                } else {
                    File file = new File(ACache.getDiskCacheDir(context));
                    File cacheFile = new File(file.getParent(), "cachemanage/");
                    this.aCache = ACache.get(cacheFile);
                }
            }

            return new CacheUtilConfig(this);
        }
    }
}
