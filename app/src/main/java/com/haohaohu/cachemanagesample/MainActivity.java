package com.haohaohu.cachemanagesample;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.haohaohu.cachemanage.ACache;
import com.haohaohu.cachemanage.CacheUtil;
import com.haohaohu.cachemanage.CacheUtilConfig;
import com.haohaohu.cachemanage.observer.CacheObserver;
import com.haohaohu.cachemanage.observer.IDataChangeListener;
import com.haohaohu.cachemanage.strategy.Des3EncryptStrategy;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import javax.crypto.KeyGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 测试Activity
 *
 * @author ME
 */
public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private Integer index = 0;
    private CountDownLatch countDownLatch = new CountDownLatch(10);
    private ReentrantLock lock = new ReentrantLock(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        init();
    }

    private void initData() {
        jsonObject = new JSONObject();
        try {
            jsonObject.put("11", "11");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonArray = new JSONArray();
        jsonArray.put(jsonObject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_default:
                initCacheConfigDefault();
                clearMemory();
            case R.id.action_config1:
                initCacheConfig1();
                clearMemory();
                break;
            case R.id.action_config2:
                initCacheConfig2();
                clearMemory();
                break;
            case R.id.action_config3:
                initCacheConfig3();
                clearMemory();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        mTextView = (TextView) findViewById(R.id.main_text3);
        initCacheConfig1();
        initEvent();
        initObserver();
    }

    private void initObserver() {
        CacheObserver.getInstance().addObserver("key1", new IDataChangeListener() {
            @Override
            public void onDataChange(String key, String str) {
                Toast.makeText(MainActivity.this, key + "：" + str, Toast.LENGTH_SHORT).show();
            }
        });

        CacheObserver.addListener("key2", new IDataChangeListener() {
            @Override
            public void onDataChange(String key, String str) {
                Toast.makeText(MainActivity.this, key + "：" + str, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initEvent() {
        findViewById(R.id.main_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String key1Value = CacheUtil.get("key1");
                String key2Value = CacheUtil.get("key2", true);
                String key3Value = CacheUtil.get("key3", false);
                String key4Value = CacheUtil.get("key4", true);
                Test key5Test = CacheUtil.get("key5", Test.class);//可能为null
                String key5Value = key5Test == null ? "" : key5Test.toString();
                Test key6Test = CacheUtil.get("key6", Test.class, true);
                String key6Value = key6Test == null ? "" : key6Test.toString();
                String key7Value = CacheUtil.get("key7");
                String key8Value = CacheUtil.get("key8", true);
                String key9Value = CacheUtil.get("key9");
                String key10Value = CacheUtil.get("key10", true);
                String key11Value = CacheUtil.get("key11");
                String key12Value = CacheUtil.get("key12", true);
                String key13Value = CacheUtil.get("key13");
                Test key14Test = CacheUtil.get("key14", Test.class);
                String key14Value = key14Test == null ? "" : key14Test.toString();
                String key15Value = CacheUtil.get("key15", true);
                Test key16Test = CacheUtil.get("key16", Test.class, true);
                String key16Value = key16Test == null ? "" : key16Test.toString();
                String key17Value = CacheUtil.get(CacheUtil.translateKey("key17"), true);
                String key18Value = CacheUtil.get("key18", false);
                Boolean key19Value =
                        CacheUtil.get(CacheUtil.translateKey("null"), Boolean.class, false, true);
                //key19未存储的数据，返回默认值
                Test key20Value = CacheUtil.get(CacheUtil.translateKey("null1"), Test.class,
                        new Test(1, "默认值"), true);
                //key20未存储的数据，返回默认值
                Test key21Value = CacheUtil.get(CacheUtil.translateKey("null1"), Test.class, true);
                //key21未存储的数据，且无默认构造方法,返回null
                int key22Value = CacheUtil.get("key22", int.class, 100,true);
                //key21已存储的数据，返回默认值

                String value = "测试:\n"
                        + "字符串(默认方式):"
                        + check("测试数据1", key1Value)
                        + "\n"
                        + "字符串(加密):"
                        + check("测试数据2", key2Value)
                        + "\n"
                        + "字符串(不加密):"
                        + check("测试数据18", key18Value)
                        + "\n"
                        + "特殊字符串(不加密):"
                        + check("~!@#$%^&*()_+{}[];':,.<>`", key3Value)
                        + "\n"
                        + "特殊字符串(加密):"
                        + check("~!@#$%^&*()_+{}[];':,.<>`", key4Value)
                        + "\n"
                        + "实体对象[Test类](不加密):"
                        + check(new Test(1, "2").toString(), key5Value)
                        + "\n"
                        + "实体对象[Test类](加密):"
                        + check(new Test(1, "2").toString(), key6Value)
                        + "\n"
                        + "jsonObject对象(不加密):"
                        + check(jsonObject.toString(), key7Value)
                        + "\n"
                        + "jsonObject对象(加密):"
                        + check(jsonObject.toString(), key8Value)
                        + "\n"
                        + "jsonArray对象(不加密):"
                        + check(jsonArray.toString(), key9Value)
                        + "\n"
                        + "jsonArray对象(加密):"
                        + check(jsonArray.toString(), key10Value)
                        + "\n"
                        + "数字(不加密):"
                        + check(1 + "", key11Value)
                        + "\n"
                        + "数字(加密):"
                        + check(1 + "", key12Value)
                        + "\n"
                        + "字符串(5秒):"
                        + check("测试数据1", key13Value)
                        + "\n"
                        + "实体对象[Test类](5秒):"
                        + check(new Test(1, "2").toString(), key14Value)
                        + "\n"
                        + "字符串(5秒)(加密):"
                        + check("测试数据1", key15Value)
                        + "\n"
                        + "实体对象(5秒)(加密):"
                        + check(new Test(1, "2").toString(), key16Value)
                        + "\n"
                        + "对key加密:"
                        + check("123456", key17Value)
                        + "\n"
                        + "未保存数据(Boolean类型):    默认值"
                        + key19Value
                        + "\n"
                        + "未保存实体对象[Test类](有默认返回对象):"
                        + (key20Value != null ? key20Value.toString() : null)
                        + "\n"
                        + "未保存实体对象[Test类](无默认返回对象):"
                        + (key21Value != null ? key21Value.toString() : "null")
                        + "\n"
                        + "保存实体对象[Test类](有默认返回对象):"
                        + (String.valueOf(key22Value))
                        + "\n";
                mTextView.setText(value);
            }
        });

        findViewById(R.id.main_text1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheUtil.put("key1", "测试数据1");//默认加密状态
                CacheUtil.put("key2", "测试数据2", true);//true代表加密存储
                CacheUtil.put("key18", "测试数据18", false);//false代表不加密存储
                CacheUtil.put("key3", "~!@#$%^&*()_+{}[];':,.<>`");//特殊字符串测试
                CacheUtil.put("key4", "~!@#$%^&*()_+{}[];':,.<>`", true);//加密特殊字符串测试
                CacheUtil.put("key5", new Test(1, "2"));//实体对象测试
                CacheUtil.put("key6", new Test(1, "2"), true);//加密实体对象测试
                CacheUtil.put("key7", jsonObject);//jsonObject对象测试
                CacheUtil.put("key8", jsonObject, true);//加密jsonObject对象测试
                CacheUtil.put("key9", jsonArray);//jsonArray对象测试
                CacheUtil.put("key10", jsonArray, true);//加密jsonArray对象测试
                CacheUtil.put("key11", 1);//jsonArray对象测试
                CacheUtil.put("key12", 1, true);//加密jsonArray对象测试
                CacheUtil.put("key13", "测试数据1", 5);//保存数据5秒
                CacheUtil.put("key14", new Test(1, "2"), 5);//保存对象数据5秒
                CacheUtil.put("key15", "测试数据1", 5, true);//加密保存数据5秒
                CacheUtil.put("key16", new Test(1, "2"), 5, true);//加密保存对象数据5秒
                CacheUtil.put(CacheUtil.translateKey("key17"), "123456", true);//key加密
                CacheUtil.put("key22", 1);//测试默认值返回
                Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.main_text2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 0;
                countDownLatch = new CountDownLatch(10);
                Toast.makeText(MainActivity.this, "压力测试，看Log", Toast.LENGTH_SHORT).show();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doTask();
                    }
                });
                thread.start();
            }
        });

        findViewById(R.id.main_text4).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearMemory();
                        Toast.makeText(MainActivity.this, "清理内存成功", Toast.LENGTH_SHORT).show();
                    }
                });

        findViewById(R.id.main_text5).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CacheUtil.clearAllMemory();
                        Toast.makeText(MainActivity.this, "清理所有内存缓存成功", Toast.LENGTH_SHORT).show();
                    }
                });

        findViewById(R.id.main_text6).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CacheUtil.clearAll();
                        Toast.makeText(MainActivity.this, "清理缓存成功", Toast.LENGTH_SHORT).show();
                    }
                });

        findViewById(R.id.main_text7).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CacheUtil.clear("key1");
                        CacheUtil.clear("key2");
                        CacheUtil.clear("key18");
                        CacheUtil.clear("key3");
                        CacheUtil.clear("key4");
                        Toast.makeText(MainActivity.this, "清理缓存成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void doTask() {
        long startTime = System.currentTimeMillis();   //获取开始时间
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (MainActivity.this) {
                        index++;
                    }
                    CacheUtil.put("key0", "" + (index));//默认加密状态
                    countDownLatch.countDown();
                }
            });
            thread.start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String str1 = CacheUtil.get("key0");
        Log.e("time", str1);
        long endTime = System.currentTimeMillis(); //获取结束时间
        Log.e("time", "程序运行时间： " + (endTime - startTime) + "ms");
    }

    private void initCacheConfigDefault() {
        CacheUtilConfig cc =
                CacheUtilConfig.builder(getApplication()).allowMemoryCache(true)//是否允许保存到内存
                        .allowEncrypt(false)//是否允许加密
                        .build();
        CacheUtil.init(cc);//初始化，必须调用
    }

    private void initCacheConfig1() {
        CacheUtilConfig cc =
                CacheUtilConfig.builder(getApplication()).allowMemoryCache(true)//是否允许保存到内存
                        .allowEncrypt(false)//是否允许加密
                        .allowKeyEncrypt(true)//是否允许Key加密
                        //.preventPowerDelete(true)//强力防止删除，将缓存数据存储在app数据库目录下的cachemanage文件夹下
                        //                .setACache(ACache.get(file1))//自定义ACache，file1为缓存自定义存储文件夹,设置该项，preventPowerDelete失效
                        .setAlias("")//默认KeyStore加密算法私钥，建议设置.自定义加密算法，该功能失效
                        .setIEncryptStrategy(new Des3EncryptStrategy(MainActivity.this,
                                "WLIJkjdsfIlI789sd87dnu==", "haohaoha"))//自定义des3加密
                        .build();
        CacheUtil.init(cc);//初始化，必须调用
    }

    private void initCacheConfig2() {
        CacheUtilConfig cc = CacheUtilConfig.builder(MainActivity.this)
                .setIEncryptStrategy(
                        new Des3EncryptStrategy(MainActivity.this, "WLIJkjdsfIlI789sd87dnu==",
                                "haohaoha"))
                .allowMemoryCache(true)//是否允许保存到内存
                .allowEncrypt(true)//是否允许加密
                .build();
        CacheUtil.init(cc);//初始化，必须调用
        KeyGenerator xd;
        try {
            xd = KeyGenerator.getInstance("DES");
            String str = xd.generateKey().toString();
            Log.e("str", str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCacheConfig3() {
        File file = new File(ACache.getDiskCacheDir(MainActivity.this));
        File file1 = new File(file.getParent(), "cachemanage");
        CacheUtilConfig cc =
                CacheUtilConfig.builder(getApplication()).allowMemoryCache(true)//是否允许保存到内存
                        .allowEncrypt(false)//是否允许加密
                        .setACache(ACache.get(file1))//自定义ACache
                        .build();
        CacheUtil.init(cc);//初始化，必须调用
    }

    private void initCacheConfig4() {
        File file1 = new File(Environment.getExternalStorageDirectory(), "cache");
        CacheUtilConfig cc =
                CacheUtilConfig.builder(getApplication()).allowMemoryCache(true)//是否允许保存到内存
                        .allowEncrypt(false)//是否允许加密
                        //                        .preventPowerDelete(true)//强力防止删除，将缓存数据存储在app数据库目录下的cachemanage文件夹下
                        .setACache(ACache.get(file1))//自定义ACache
                        .build();
        CacheUtil.init(cc);//初始化，必须调用
    }

    public void clearMemory() {
        CacheUtil.clearMemory("key1");
        CacheUtil.clearMemory("key2");
        CacheUtil.clearMemory("key18");
        CacheUtil.clearMemory("key3");
        CacheUtil.clearMemory("key4");
        CacheUtil.clearMemory("key5");
        CacheUtil.clearMemory("key6");
        CacheUtil.clearMemory("key7");
        CacheUtil.clearMemory("key8");
        CacheUtil.clearMemory("key9");
        CacheUtil.clearMemory("key10");
        CacheUtil.clearMemory("key11");
        CacheUtil.clearMemory("key12");
        CacheUtil.clearMemory("key13");
        CacheUtil.clearMemory("key14");
        CacheUtil.clearMemory("key15");
        CacheUtil.clearMemory("key16");
        CacheUtil.clearMemory(CacheUtil.translateKey("key17"));
    }

    public String check(String str, String str1) {
        return str.equals(str1) ? "ok" : "fail";
    }
}
