package com.haohaohu.cachemanage.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import kotlin.text.Charsets;
import mohapps.modified.java.util.Base64;



/**
 * KeyStore 帮助类 用来缓存密钥，防止破解
 * {转自 https://gist.github.com/alphamu/cf44b2783fb2fd81cc53aca91276d481}
 *
 * @author haohao on 2017/8/24 10:37
 * @version v1.0
 */
public class KeyStoreHelper {
    private static final String TAG = "KeyStoreHelper";


    /**
     * Creates a public and private key and stores it using the Android Key
     * Store, so that only this application will be able to access the keys.
     */
    public static void createKeys(Context context, String alias) {
        if (!isSigningKey(alias)) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                createKeysM(context, alias, false);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                createKeysN(alias, false);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                createKeysJBMR2(context, alias);
            }
        }
    }
    //this is for API <=23 only
    private static void setLocale(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static void createKeysJBMR2(Context context, String alias)
    {
        try {
            Calendar start = new GregorianCalendar();
            Calendar end = new GregorianCalendar();
            end.add(Calendar.YEAR, 30);
            Locale initialLocale = context.getResources().getConfiguration().locale;
            setLocale(context, Locale.ENGLISH);
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(SecurityConstants.KEY_ALGORITHM_RSA,
                    SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            keyPairGenerator.initialize(new KeyPairGeneratorSpec.Builder(context)
                    // You'll use the alias later to retrieve the key. It's a key
                    // for the key!
                    .setAlias(alias)
                    .setSubject(new X500Principal("CN=" + alias))
                    .setSerialNumber(BigInteger.valueOf(Math.abs(alias.hashCode())))
                    // Date range of validity for the generated pair.
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build());
            keyPairGenerator.generateKeyPair();
            setLocale(context, initialLocale);
            getSecretKeyJBMR2(context, alias);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private static void createKeysM(Context context, String alias, boolean requireAuth) {
        try {
            //workaround to avoid crash on API 23 when app locale is Arabic, Persian,...
            //https://issuetracker.google.com/issues/37095309
            //https://stackoverflow.com/a/46602170/6305235
            Locale initialLocale = context.getResources().getConfiguration().locale;
            setLocale(context, Locale.ENGLISH);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                    KeyProperties.PURPOSE_ENCRYPT
                            | KeyProperties.PURPOSE_DECRYPT)
                    .setKeySize(256)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    // Only permit the private key to be used if the user authenticated
                    // within the last five minutes.
                    .setUserAuthenticationRequired(requireAuth)
                    .build());
            keyGenerator.generateKey();
            setLocale(context, initialLocale);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static void createKeysN(String alias, boolean requireAuth) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                    KeyProperties.PURPOSE_ENCRYPT
                            | KeyProperties.PURPOSE_DECRYPT)
                    .setKeySize(256)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    // Only permit the private key to be used if the user authenticated
                    // within the last five minutes.
                    .setUserAuthenticationRequired(requireAuth)
                    .build());
            keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JBMR2+ If Key with the default alias exists, returns true, else false.
     * on pre-JBMR2 returns true always.
     */
    private static boolean isSigningKey(String alias) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                KeyStore keyStore =
                        KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
                keyStore.load(null);
                return keyStore.containsAlias(alias);
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns the private key signature on JBMR2+ or else null.
     */
    public static String getSigningKey(String alias) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            KeyStore.PrivateKeyEntry keyEntry = getPrivateKeyEntry(alias);
            if (keyEntry == null) {
                return null;
            }
            Certificate cert = keyEntry.getCertificate();
            if (cert == null) {
                return null;
            }
            try {
                return Base64.getEncoder().encodeToString(cert.getEncoded());
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    private static KeyStore.PrivateKeyEntry getPrivateKeyEntry(String alias) {
        try {
            KeyStore ks =
                    KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(alias, null);

            if (entry == null) {
                return null;
            }

            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                return null;
            }
            return (KeyStore.PrivateKeyEntry) entry;
        } catch (Exception e) {
            return null;
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static SecretKey getSecretKeyJBMR2(Context context, String alias) {
        SharedPreferences pref = context.getSharedPreferences(SecurityConstants.ENCRYPTION, Context.MODE_PRIVATE);
        String aesKey = pref.getString(SecurityConstants.AES_KEY, "");
        if (aesKey.equals("")) {
            try {
                KeyGenerator keygen = KeyGenerator.getInstance(SecurityConstants.KEY_ALGORITHM_AES);
                keygen.init(128);
                SecretKey secretKey = keygen.generateKey();
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(SecurityConstants.AES_KEY, encryptKey(alias, new String(secretKey.getEncoded(), Charsets.ISO_8859_1)));
                editor.apply();
                return secretKey;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else {
            return new SecretKeySpec(decryptKey(alias, aesKey).getBytes(Charsets.ISO_8859_1), 0, 16, SecurityConstants.KEY_ALGORITHM_AES);
        }
        return null;
    }
    @TargetApi(Build.VERSION_CODES.M)
    private static SecretKey getSecretKeyM(String alias) {
        try {
            KeyStore ks =
                    KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(alias, null);

            if (entry == null) {
                return null;
            }

            if (!(entry instanceof KeyStore.SecretKeyEntry)) {
                return null;
            }
            return ((KeyStore.SecretKeyEntry) entry).getSecretKey();
        } catch (Exception e) {
            return null;
        }
    }


    private static String encryptKey(String alias, String aesKey) {
        try {
            KeyStore.PrivateKeyEntry keyEntry = getPrivateKeyEntry(alias);
            if (keyEntry == null) {
                return "";
            }
            PublicKey publicKey = keyEntry.getCertificate().getPublicKey();
            Cipher cipher = getCipherRSA();
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(aesKey.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static String decryptKey(String alias, String aesKeyCipher) {
        try {
            KeyStore.PrivateKeyEntry keyEntry = getPrivateKeyEntry(alias);
            if (keyEntry == null) {
                return "";
            }
            PrivateKey privateKey = keyEntry.getPrivateKey();
            Cipher cipher = getCipherRSA();
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(aesKeyCipher.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String encrypt(Context context, String alias, String plainText) {
        try {
            SecretKey secretKey = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)?getSecretKeyM(alias):getSecretKeyJBMR2(context, alias);
            if (secretKey == null) {
                return "";
            }
            Cipher cipher = getCipherAES();
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] plainTextBytes = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedTextBytes = cipher.doFinal(plainTextBytes);
            IvParameterSpec ivParams = cipher.getParameters().getParameterSpec(IvParameterSpec.class);
            byte[] iv = ivParams.getIV();
            byte[] cb = new byte[iv.length + encryptedTextBytes.length];
            for (int i = 0; i < cb.length; ++i) {
                cb[i] = i < iv.length ? iv[i] : encryptedTextBytes[i - iv.length];
            }
            return Base64.getEncoder().encodeToString(cb);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public static String decrypt(Context context, String alias, String savedText) {
        try {
            SecretKey secretKey = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)?getSecretKeyM(alias):getSecretKeyJBMR2(context, alias);
            if (secretKey == null) {
                return "";
            }
            Cipher cipher = getCipherAES();
            byte[] savedTextBytes = Base64.getDecoder().decode(savedText.getBytes(StandardCharsets.UTF_8));
            byte[] iv = new byte[16];
            byte[] encryptedTextBytes = new byte[savedTextBytes.length - iv.length];
            for (int i = 0; i < savedTextBytes.length; i++) {
                if (i < iv.length) {
                    iv[i] = savedTextBytes[i];
                } else {
                    encryptedTextBytes[i-iv.length] = savedTextBytes[i];
                }
            }
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
            return new String(cipher.doFinal(encryptedTextBytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Cipher getCipherRSA() throws NoSuchPaddingException, NoSuchAlgorithmException {
            return Cipher.getInstance(String.format("%s/%s/%s", SecurityConstants.KEY_ALGORITHM_RSA,
                    SecurityConstants.BLOCK_MODE_ECB, SecurityConstants.ENCRYPTION_PADDING_RSA_PKCS1));
    }
    private static Cipher getCipherAES() throws NoSuchPaddingException, NoSuchAlgorithmException {
            return Cipher.getInstance(String.format("%s/%s/%s", SecurityConstants.KEY_ALGORITHM_AES,
                    SecurityConstants.BLOCK_MODE_CBC, SecurityConstants.ENCRYPTION_PADDING_PKCS7));
    }

    public interface SecurityConstants {
        String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";

        public static final String KEY_ALGORITHM_RSA = "RSA";
        public static final String KEY_ALGORITHM_EC = "EC";
        public static final String KEY_ALGORITHM_AES = "AES";

        public static final String ENCRYPTION_PADDING_NONE = "NoPadding";
        public static final String ENCRYPTION_PADDING_PKCS7 = "PKCS7Padding";
        public static final String ENCRYPTION_PADDING_RSA_PKCS1 = "PKCS1Padding";
        public static final String ENCRYPTION_PADDING_RSA_OAEP = "OAEPPadding";

        public static final String BLOCK_MODE_ECB = "ECB";
        public static final String BLOCK_MODE_CBC = "CBC";
        public static final String BLOCK_MODE_CTR = "CTR";
        public static final String BLOCK_MODE_GCM = "GCM";

        public static final String ENCRYPTION = "ENCRIPTION";
        public static final String AES_KEY = "AES_KEY";

        String SIGNATURE_SHA256withRSA = "SHA256withRSA";
        String SIGNATURE_SHA512withRSA = "SHA512withRSA";
    }
}
