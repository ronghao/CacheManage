package com.haohaohu.cachemanage.util;

import android.text.TextUtils;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Des3工具类
 *
 * @author haohao on 2017/6/22 18:19
 * @version v1.0
 */
public class Des3Util {
    public static final String DESEDE_CBC_PKCS5_PADDING = "desede/CBC/PKCS5Padding";
    private static String encoding = "utf-8";

    /**
     * 加密
     *
     * @param plainText 要加密文字
     * @param iv 偏移量
     * @param secretKey 密钥
     * @return 加密文字
     * @throws Exception
     */
    public static String encode(String plainText, String secretKey, String iv) throws Exception {
        if (TextUtils.isEmpty(secretKey) || TextUtils.isEmpty(iv)) {
            throw new NullPointerException("u should init first");
        }
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        SecretKey deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(DESEDE_CBC_PKCS5_PADDING);
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(1, deskey, ips);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
        return Base64Util.encode(encryptData);
    }

    /***
     * 解密
     * @param encryptText 要解密文字
     * @param secretKey  密钥
     * @param iv 偏移量
     * @return 解密文字
     * @throws Exception
     */
    public static String decode(String encryptText, String secretKey, String iv) throws Exception {
        if (TextUtils.isEmpty(secretKey) || TextUtils.isEmpty(iv)) {
            throw new NullPointerException("u should init first");
        }
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        SecretKey deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(DESEDE_CBC_PKCS5_PADDING);
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(2, deskey, ips);
        byte[] decryptData = cipher.doFinal(Base64Util.decode(encryptText));
        return new String(decryptData, encoding);
    }
}
