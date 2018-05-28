package com.haohaohu.cachemanage;

import android.content.Context;
import com.haohaohu.cachemanage.strategy.Des3EncryptStrategy;
import com.haohaohu.cachemanage.strategy.IEncryptStrategy;

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

        public CacheUtilConfig build() {
            if (this.iEncryptStrategy == null) {
                iEncryptStrategy = new Des3EncryptStrategy(context);
            }
            if (this.aCache == null) {
                this.aCache = ACache.get(context);
            }

            return new CacheUtilConfig(this);
        }
    }
}
