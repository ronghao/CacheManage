package com.haohaohu.cachemanage.strategy;

import android.content.Context;

import com.haohaohu.cachemanage.util.KeyStoreHelper;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * KeyStore加密策略
 *
 * @author haohao(ronghao3508@gmail.com) on 2018/5/28 16:18
 * @version v1.0
 */
public class KeyStoreEncryptStrategy implements IEncryptStrategy {

    private Context mContext;
    private String alias;

    public KeyStoreEncryptStrategy(Context context) {
        this(context, context.getPackageName());
    }

    public KeyStoreEncryptStrategy(Context context, String alias) {
        this.mContext = context;
        this.alias = context.getPackageName() + alias;
        createKeyStoreSecretKey(this.alias);
    }

    @Override
    public String encrypt(String str) {
        try {
            return KeyStoreHelper.encrypt(alias, str);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String decode(String str) {
        try {
            return KeyStoreHelper.decrypt(alias, str);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void createKeyStoreSecretKey(String alias) {
        try {
            KeyStoreHelper.createKeys(mContext, alias);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }
}
