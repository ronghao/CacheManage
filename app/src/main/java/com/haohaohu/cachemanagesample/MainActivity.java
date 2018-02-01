package com.haohaohu.cachemanagesample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.haohaohu.cachemanage.CacheObserver;
import com.haohaohu.cachemanage.CacheUtil;
import com.haohaohu.cachemanage.CacheUtilConfig;
import com.haohaohu.cachemanage.IDataChangeListener;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
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
            case R.id.action_ok:
                initCacheConfig();
                clearMemory();
                break;
            case R.id.action_des_2:
                initCacheConfig1();
                clearMemory();
                break;
            case R.id.action_default:
                initCacheConfig2();
                clearMemory();
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
        initCacheConfig2();
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
    }

    private void initEvent() {
        findViewById(R.id.main_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String key1Value = CacheUtil.get("key1");
                String key2Value = CacheUtil.get("key2", true);
                String key3Value = CacheUtil.get("key3");
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
                Test key20Value = CacheUtil.get(CacheUtil.translateKey("null1"), Test.class,
                        new Test(1, "默认值"), true);

                StringBuilder builder = new StringBuilder();
                String value = builder.append("不加密字符串测试:")
                        .append(check("测试数据1", key1Value))
                        .append("\n")
                        .append("加密字符串测试:")
                        .append(check("测试数据2", key2Value))
                        .append("\n")
                        .append("不加密字符串测试:")
                        .append(check("测试数据18", key18Value))
                        .append("\n")
                        .append("特殊字符串测试:")
                        .append(check("~!@#$%^&*()_+{}[];':,.<>`", key3Value))
                        .append("\n")
                        .append("加密特殊字符串测试:")
                        .append(check("~!@#$%^&*()_+{}[];':,.<>`", key4Value))
                        .append("\n")
                        .append("实体对象测试:")
                        .append(check(new Test(1, "2").toString(), key5Value))
                        .append("\n")
                        .append("加密实体对象测试:")
                        .append(check(new Test(1, "2").toString(), key6Value))
                        .append("\n")
                        .append("jsonObject对象测试:")
                        .append(check(jsonObject.toString(), key7Value))
                        .append("\n")
                        .append("加密jsonObject对象测试:")
                        .append(check(jsonObject.toString(), key8Value))
                        .append("\n")
                        .append("jsonArray对象测试:")
                        .append(check(jsonArray.toString(), key9Value))
                        .append("\n")
                        .append("加密jsonArray对象测试:")
                        .append(check(jsonArray.toString(), key10Value))
                        .append("\n")
                        .append("数字测试:")
                        .append(check(1 + "", key11Value))
                        .append("\n")
                        .append("加密数字测试:")
                        .append(check(1 + "", key12Value))
                        .append("\n")
                        .append("保存数据5秒测试:")
                        .append(check("测试数据1", key13Value))
                        .append("\n")
                        .append("保存对象数据5秒测试:")
                        .append(check(new Test(1, "2").toString(), key14Value))
                        .append("\n")
                        .append("加密保存数据5秒测试:")
                        .append(check("测试数据1", key15Value))
                        .append("\n")
                        .append("加密保存对象数据5秒测试:")
                        .append(check(new Test(1, "2").toString(), key16Value))
                        .append("\n")
                        .append("key加密保存数据测试:")
                        .append(check("123456", key17Value))
                        .append("\n")
                        .append("未保存数据测试(Boolean类型):")
                        .append(key19Value)
                        .append("\n")
                        .append("未保存数据测试(Test类):")
                        .append(key20Value.toString())
                        .append("\n")
                        .toString();
                mTextView.setText(value);
            }
        });

        findViewById(R.id.main_text1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheUtil.put("key1", "测试数据1");//默认加密状态
                CacheUtil.put("key2", "测试数据2", true);//true代表加密存储
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
                CacheUtil.put("key18", "测试数据18", false);//false代表不加密存储
                Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.main_text2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheUtil.clear("key1");
                CacheUtil.clear("key2");
                CacheUtil.clear("key3");
                CacheUtil.clear("key4");
                CacheUtil.clear("key5");
                CacheUtil.clear("key6");
                CacheUtil.clear("key7");
                CacheUtil.clear("key8");
                CacheUtil.clear("key9");
                CacheUtil.clear("key10");
                CacheUtil.clear("key11");
                CacheUtil.clear("key12");
                CacheUtil.clear("key13");
                CacheUtil.clear("key14");
                CacheUtil.clear("key15");
                CacheUtil.clear("key16");
                CacheUtil.clear(CacheUtil.translateKey("key17"));
                CacheUtil.clear("key18");
                Toast.makeText(MainActivity.this, "清理成功", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.main_text4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheUtil.clearMemory("key1");
                CacheUtil.clearMemory("key2");
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
                CacheUtil.clearMemory("key18");
                Toast.makeText(MainActivity.this, "清理内存成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initCacheConfig() {
        CacheUtilConfig cc = CacheUtilConfig.builder(getApplication())
                .setDes3("WLIJkjdsfIlI789sd87dnu==")//自定义des3加密
                .setIv("haohaoha")//自定义des3偏移量
                .allowMemoryCache(true)//是否允许保存到内存
                .allowDes3(true)//是否允许des3加密
                .build();
        CacheUtil.init(cc);//初始化，必须调用
    }

    private void initCacheConfig1() {
        CacheUtilConfig cc = CacheUtilConfig.builder(MainActivity.this)
                .setDes3("WMIJkjdsfIlI789sd87dn14R")//自定义des3加密
                .setIv("haohaoha")//自定义des3偏移量
                .allowMemoryCache(true)//是否允许保存到内存
                .allowDes3(true)//是否允许des3加密
                .build();
        CacheUtil.init(cc);//初始化，必须调用
        KeyGenerator xd = null;
        try {
            xd = KeyGenerator.getInstance("DES");
            String str = xd.generateKey().toString();
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

    private void initCacheConfig2() {
        CacheUtilConfig cc =
                CacheUtilConfig.builder(getApplication()).allowMemoryCache(true)//是否允许保存到内存
                        .allowDes3(true)//是否允许des3加密
                        .build();
        CacheUtil.init(cc);//初始化，必须调用
    }

    public void clearMemory() {
        CacheUtil.clearMemory("key1");
        CacheUtil.clearMemory("key2");
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
        CacheUtil.clearMemory("key18");
    }

    public String check(String str, String str1) {
        return str.equals(str1) ? "ok" : "fail";
    }
}
