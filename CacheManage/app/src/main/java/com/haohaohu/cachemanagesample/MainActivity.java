package com.haohaohu.cachemanagesample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.haohaohu.cachemanage.CacheUtil;

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
//        CacheUtil.init(MainActivity.this);
        CacheUtil.init(MainActivity.this,"WLIJkjdsfIlI789sd87dnu==","haohaoha");

        mTextView = (TextView) findViewById(R.id.text2);

        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Test key5Test = CacheUtil.get("key5", Test.class);
                Test key6Test = CacheUtil.get("key6", Test.class, true);
                String strKey5 = key5Test == null ? "" : key5Test.toString();
                String strKey6 = key6Test == null ? "" : key6Test.toString();

                String value = new StringBuilder().append("不加密字符串测试:" + check("测试数据1", CacheUtil.get("key1")) + "\n")
                        .append("加密字符串测试:" + check("测试数据2", CacheUtil.get("key2", true)) + "\n")
                        .append("特殊字符串测试:" + check("~!@#$%^&*()_+{}[];':,.<>`", CacheUtil.get("key3")) + "\n")
                        .append("加密特殊字符串测试:" + check("~!@#$%^&*()_+{}[];':,.<>`", CacheUtil.get("key4", true)) + "\n")
                        .append("实体对象测试:" + check(new Test(1, "2").toString(), strKey5) + "\n")
                        .append("加密实体对象测试:" + check(new Test(1, "2").toString(), strKey6) + "\n")
                        .append("jsonObject对象测试:" + check(jsonObject.toString(), CacheUtil.get("key7")) + "\n")
                        .append("加密jsonObject对象测试:" + check(jsonObject.toString(), CacheUtil.get("key8", true)) + "\n")
                        .append("jsonArray对象测试:" + check(jsonArray.toString(), CacheUtil.get("key9")) + "\n")
                        .append("加密jsonArray对象测试:" + check(jsonArray.toString(), CacheUtil.get("key10", true)) + "\n")
                        .toString();
                mTextView.setText(value);
            }
        });

        findViewById(R.id.text1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheUtil.put("key1", "测试数据1");//默认不加密
                CacheUtil.put("key2", "测试数据2", true);//true代表加密存储
                CacheUtil.put("key3", "~!@#$%^&*()_+{}[];':,.<>`");//特殊字符串测试
                CacheUtil.put("key4", "~!@#$%^&*()_+{}[];':,.<>`", true);//加密特殊字符串测试
                CacheUtil.put("key5", new Test(1, "2"));//实体对象测试
                CacheUtil.put("key6", new Test(1, "2"), true);//加密实体对象测试
                CacheUtil.put("key7", jsonObject);//jsonObject对象测试
                CacheUtil.put("key8", jsonObject, true);//加密jsonObject对象测试
                CacheUtil.put("key9", jsonArray);//jsonArray对象测试
                CacheUtil.put("key10", jsonArray, true);//加密jsonArray对象测试
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
                Toast.makeText(MainActivity.this, "清理成功", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public String check(String str, String str1) {
        return str.equals(str1) ? "ok" : "fail";
    }
}
