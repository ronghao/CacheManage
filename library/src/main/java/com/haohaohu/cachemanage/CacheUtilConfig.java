package com.haohaohu.cachemanage;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.haohaohu.cachemanage.strategy.IEncryptStrategy;
import com.haohaohu.cachemanage.strategy.KeyStoreEncryptStrategy;

import java.io.File;

/**
 * 配置项
 *
 * @author haohao on 2017/8/24 10:37
 * @version v1.0
 */
public class CacheUtilConfig {

    private Context context;
    private boolean isKeyEncrypt;
    private boolean isEncrypt;
    private boolean memoryCache;
    private ACache aCache;

    private IEncryptStrategy mIEncryptStrategy;

    private CacheUtilConfig(Builder builder) {
        context = builder.context;
        isEncrypt = builder.isEncrypt;
        isKeyEncrypt = builder.isKeyEncrypt;
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

    public boolean isKeyEncrypt() {
        return isKeyEncrypt;
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
        public IEncryptStrategy iEncryptStrategy;
        private Context context;
        private boolean isEncrypt = true;//默认加密
        private boolean isKeyEncrypt = true;//key默认加密
        private boolean memoryCache = true;//默认保存到内存
        private boolean isPreventPowerDelete = false;//防止被删除
        private String alias;//私钥
        private ACache aCache;//ACache示例

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

        public Builder allowKeyEncrypt(boolean isKeyEncrypt) {
            this.isKeyEncrypt = isKeyEncrypt;
            return this;
        }

        public Builder preventPowerDelete(boolean isPreventPowerDelete) {
            this.isPreventPowerDelete = isPreventPowerDelete;
            return this;
        }

        public Builder setAlias(String alise) {
            this.alias = alise;
            return this;
        }

        public CacheUtilConfig build() {
            if (this.iEncryptStrategy == null) {
                if (TextUtils.isEmpty(alias)) {
                    iEncryptStrategy = new KeyStoreEncryptStrategy(context);
                } else {
                    iEncryptStrategy = new KeyStoreEncryptStrategy(context, alias);
                }
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
                    //删除临时创建的数据库文件
                    if (file.exists()) {
                        file.delete();
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
