package com.haohaohu.cachemanagesample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.haohaohu.cachemanage.CacheUtil;
import com.haohaohu.cachemanage.CacheUtilConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    private JSONArray jsonArray;
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jsonObject = new JSONObject();
        try {
            jsonObject.put("11", "11");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        init();
    }

    private void init() {
        CacheUtilConfig cc = CacheUtilConfig.builder(MainActivity.this)
                //.setDes3("WLIJkjdsfIlI789sd87dnu==")
                //.setIv("haohaoha")
                .build();
        CacheUtil.init(cc);

        mTextView = (TextView) findViewById(R.id.text2);

        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Test key5Test = CacheUtil.get("key5", Test.class);
                Test key6Test = CacheUtil.get("key6", Test.class, true);
                Test key14Test = CacheUtil.get("key14", Test.class);
                Test key16Test = CacheUtil.get("key16", Test.class, true);
                String strKey5 = key5Test == null ? "" : key5Test.toString();
                String strKey6 = key6Test == null ? "" : key6Test.toString();
                String strKey14 = key14Test == null ? "" : key14Test.toString();
                String strKey16 = key16Test == null ? "" : key16Test.toString();

                String value = new StringBuilder().append("不加密字符串测试:")
                        .append(check("测试数据1", CacheUtil.get("key1")))
                        .append("\n")
                        .append("加密字符串测试:")
                        .append(check("测试数据2", CacheUtil.get("key2", true)))
                        .append("\n")
                        .append("不加密字符串测试:")
                        .append(check("测试数据18", CacheUtil.get("key18", false)))
                        .append("\n")
                        .append("特殊字符串测试:")
                        .append(check("~!@#$%^&*()_+{}[];':,.<>`", CacheUtil.get("key3")))
                        .append("\n")
                        .append("加密特殊字符串测试:")
                        .append(check("~!@#$%^&*()_+{}[];':,.<>`", CacheUtil.get("key4", true)))
                        .append("\n")
                        .append("实体对象测试:")
                        .append(check(new Test(1, "2").toString(), strKey5))
                        .append("\n")
                        .append("加密实体对象测试:")
                        .append(check(new Test(1, "2").toString(), strKey6))
                        .append("\n")
                        .append("jsonObject对象测试:")
                        .append(check(jsonObject.toString(), CacheUtil.get("key7")))
                        .append("\n")
                        .append("加密jsonObject对象测试:")
                        .append(check(jsonObject.toString(), CacheUtil.get("key8", true)))
                        .append("\n")
                        .append("jsonArray对象测试:")
                        .append(check(jsonArray.toString(), CacheUtil.get("key9")))
                        .append("\n")
                        .append("加密jsonArray对象测试:")
                        .append(check(jsonArray.toString(), CacheUtil.get("key10", true)))
                        .append("\n")
                        .append("数字测试:")
                        .append(check(1 + "", CacheUtil.get("key11")))
                        .append("\n")
                        .append("加密数字测试:")
                        .append(check(1 + "", CacheUtil.get("key12", true)))
                        .append("\n")
                        .append("保存数据5秒测试:")
                        .append(check("测试数据1", CacheUtil.get("key13")))
                        .append("\n")
                        .append("保存对象数据5秒测试:")
                        .append(check(new Test(1, "2").toString(), strKey14))
                        .append("\n")
                        .append("加密保存数据5秒测试:")
                        .append(check("测试数据1", CacheUtil.get("key15", true)))
                        .append("\n")
                        .append("加密保存对象数据5秒测试:")
                        .append(check(new Test(1, "2").toString(), strKey16))
                        .append("\n")
                        .append("key加密保存数据测试:")
                        .append(check("123456",
                                CacheUtil.get(CacheUtil.translateKey("key17"), true)))
                        .append("\n")
                        .toString();
                mTextView.setText(value);
            }
        });

        findViewById(R.id.text1).setOnClickListener(new View.OnClickListener() {
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

        findViewById(R.id.text3).setOnClickListener(new View.OnClickListener() {
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
                Toast.makeText(MainActivity.this, "清理成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String check(String str, String str1) {
        return str.equals(str1) ? "ok" : "fail";
    }
}
