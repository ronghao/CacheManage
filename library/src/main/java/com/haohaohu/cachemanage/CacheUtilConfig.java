package com.haohaohu.cachemanage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * 配置项
 *
 * @author haohao on 2017/8/24 10:37
 * @version v1.0
 */
public class CacheUtilConfig {

    private Context context;
    private boolean isDes3;
    private boolean memoryCache;
    private String secretKey;
    private String iv;
    private ACache aCache;

    private CacheUtilConfig(Builder builder) {
        context = builder.context;
        isDes3 = builder.isDes3;
        memoryCache = builder.memoryCache;
        secretKey = builder.secretKey;
        iv = builder.iv;
        aCache = builder.aCache;
    }

    public static Builder builder(Context context) {
        return new Builder(context.getApplicationContext());
    }

    public String getIv() {
        return iv;
    }

    public Context getContext() {
        return context;
    }

    public boolean isDes3() {
        return isDes3;
    }

    public boolean isMemoryCache() {
        return memoryCache;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public ACache getACache() {
        return aCache;
    }

    public void setACache(ACache aCache) {
        this.aCache = aCache;
    }

    public static class Builder {
        private Context context;
        private boolean isDes3 = true;//默认不加密
        private boolean memoryCache = true;//默认保存到内存
        private String secretKey;//秘钥
        private String iv = "haohaoha";//移动位置
        private ACache aCache;//ACache示例

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setDes3(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder allowDes3(boolean isDes3) {
            this.isDes3 = isDes3;
            return this;
        }

        public Builder allowMemoryCache(boolean memoryCache) {
            this.memoryCache = memoryCache;
            return this;
        }

        public Builder setIv(String iv) {
            this.iv = iv;
            return this;
        }

        public Builder setACache(ACache aCache) {
            this.aCache = aCache;
            return this;
        }

        public CacheUtilConfig build() {
            if (this.isDes3) {
                if (TextUtils.isEmpty(this.secretKey)) {
                    createKeyStoreSecretKey();
                    this.secretKey = KeyStoreHelper.getSigningKey("SecretKey");
                    this.secretKey = get24Str(this.secretKey);
                    if (TextUtils.isEmpty(this.secretKey)) {
                        this.secretKey = createSecretKey();
                    }
                    if (this.aCache == null) {
                        this.aCache = ACache.get(context);
                    }
                }
            }
            return new CacheUtilConfig(this);
        }

        private String createSecretKey() {
            String secretKey;
            String str = getAndroidID();
            if (str.length() > 24) {
                secretKey = str.substring(0, 24);
            } else {
                secretKey = str + getStr(24 - str.length());
            }

            return secretKey;
        }

        private String get24Str(String str) {
            if (TextUtils.isEmpty(str)) return "";
            if (str.length() >= 24) {
                return str.substring(0, 24);
            }
            return str + getStr(24 - str.length());
        }

        private String getStr(int num) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < num; i++) {
                builder.append("a");
            }
            return builder.toString();
        }

        @SuppressLint("HardwareIds")
        public String getAndroidID() {
            return Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }

        public void createKeyStoreSecretKey() {
            try {
                KeyStoreHelper.createKeys(context, "SecretKey");
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
        }
    }
}
