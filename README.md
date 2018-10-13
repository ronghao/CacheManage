
# 🔥[CacheManage](https://github.com/ronghao/CacheManage)  [![](https://jitpack.io/v/ronghao/CacheManage.svg)](https://jitpack.io/#ronghao/CacheManage) [![](https://travis-ci.org/ronghao/CacheManage.svg?branch=master)](https://travis-ci.org/ronghao/CacheManage) [![GitHub license](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/ronghao/CacheManage/master/LICENSE) [ ![Download](https://api.bintray.com/packages/haohao/maven/CacheManage/images/download.svg?version=1.0.3) ](https://bintray.com/haohao/maven/CacheManage/1.0.3/link)

> android缓存管理器，分为两级缓存：内存缓存和文件缓存；先取内存数据，没有再从文件缓存中获取

# 特点
+ 封装[ASimpleCache](https://github.com/yangfuhai/ASimpleCache)，继承其所有功能
+ 二级缓存
	+ 内存缓存（采用SoftReference和LruCache，防止内存溢出）
	+ 文件缓存（由ASimpleCache实现）
+ 默认使用SD卡缓存
    + getCacheDir()获取的缓存文件较容易被删除
    + 大于1M的缓存文件，google建议使用getExternalCacheDir()缓存存储
    + 默认存储位置为app数据缓存位置
+ 支持文件加密存储
    + 默认des3加密内容
    + 默认生成des3唯一密钥（建议使用默认生成的密钥）
        + 默认生成的默认密钥，每个客户端都是唯一的，互不相同
        + 默认密钥存储在KeyStore中，防止逆向工程获取密钥
+ 支持基本数据类型、String、JSONObject、JSONArray、实体对象（Test类）
	+ 不支持数据类型可转换成String存储
+ 缓存数据可设置过期时间，到期自动销毁
+ 允许内存缓存
+ key值加密
    + 对应的本地缓存文件也加密
+ 添加数据监控机制
+ 支持自定义加密算法


# 使用方法
#### 初始化配置（必须调用 activity或者application）
```java
    CacheUtilConfig cc = CacheUtilConfig.builder(getApplication())
                .setIEncryptStrategy(
                        new Des3EncryptStrategy(MainActivity.this, "WLIJkjdsfIlI789sd87dnu==",
                                "haohaoha"))//自定义des3加密
                .allowMemoryCache(true)//是否允许保存到内存
                .allowEncrypt(false)//是否允许加密
                .build();
        CacheUtil.init(cc);//初始化，必须调用
```

#### 保存数据
```java
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
CacheUtil.put("key11", 1);//jsonArray对象测试
CacheUtil.put("key12", 1, true);//加密jsonArray对象测试
CacheUtil.put("key13", "测试数据1", 5);//保存数据5秒
CacheUtil.put("key14", new Test(1, "2"), 5);//保存对象数据5秒
CacheUtil.put("key15", "测试数据1", 5, true);//加密保存数据5秒
CacheUtil.put("key16", new Test(1, "2"), 5, true);//加密保存对象数据5秒
CacheUtil.put(CacheUtil.translateKey("key17"), "123456", true);//key加密
CacheUtil.put("key18", "测试数据18", false);//false代表不加密存储
```
#### 获取数据
```java
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
//key19未存储的数据，返回默认值
Test key20Value = CacheUtil.get(CacheUtil.translateKey("null1"), Test.class,
        new Test(1, "默认值"), true);
//key20未存储的数据，返回默认值
Test key21Value = CacheUtil.get(CacheUtil.translateKey("null1"), Test.class, true);
//key21未存储的数据，且无默认构造方法,返回null
```
```java
CacheUtil.get("要查找的key")  
CacheUtil.get("要查找的key", 是否加密)
CacheUtil.get("要查找的key",对应的实体对象）
CacheUtil.get("要查找的key",对应的实体对象， 是否加密)
CacheUtil.get("要查找的key",对应的实体对象， 错误情况下返回默认数据)
CacheUtil.get("要查找的key",对应的实体对象， 错误情况下返回默认数据,是否加密)
```

#### 删除数据
```java
CacheUtil.clearMemory("key1");//指定key内存缓存删除
CacheUtil.clear("key8");//指定key内存缓存和文件缓存都删除
CacheUtil.clearAllMemory();//所有内存缓存删除
CacheUtil.clearAll();//所有内存缓存和文件缓存都删除
```

#### 数据监听
```java
CacheObserver.getInstance().addObserver("key1", new IDataChangeListener() {
            @Override
            public void onDataChange(String str) {
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
```

# 项目添加方法
在根 build.gradle中添加
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

在项目build.gradle中添加 ![](https://jitpack.io/v/ronghao/CacheManage.svg)
```
dependencies {
    compile 'com.github.ronghao:CacheManage:1.2.0'
}
```

# 特别注意
+ **文件缓存默认缓存到app缓存文件，在手机清理缓存空间时可被清理，请悉知：如不想被清理，请指定缓存位置存储**
+ 禁止传入空key
+ 如未存储数据时，get（key）会返回的字符串为空字符串
+ 如未存储数据（实体对象）时获取数据，
	+ 如果实现了默认构造函数，会返回一个新的对象实例；
	+ 如果未实现构造函数，会返回null


# TODO
+ 添加多线程控制

# 版本更新说明
+ v1.2.1
  + 添加clearAll()和clearAllMemory()方法
+ v1.2.0
	+ 添加自定义加密算法，只需实现IEncryptStrategy接口，参见[Des3EncryptStrategy](https://github.com/ronghao/CacheManage/blob/master/library/src/main/java/com/haohaohu/cachemanage/strategy/Des3EncryptStrategy.java)
+ v1.1.2
    + 添加自定义ACache
	    + 在[CacheUtilConfig](https://github.com/ronghao/CacheManage/blob/master/library/src/main/java/com/haohaohu/cachemanage/CacheUtilConfig.java)配置
+ v1.1.1
    + 添加数据监控机制
+ v1.1.0
	+ 添加注解
	+ 优化大量代码
+ v1.0.5
	+ 添加KeyStore生成与存储密钥
+ v1.0.4
	+ 添加配置项管理
	+ 添加根据设备id自动生成des3秘钥
	+ 默认des3加密


# 关于
+ 个人博客：[www.haohaohu.com](http://www.haohaohu.com/)
+ 如果你也喜欢这个库，Star一下吧，欢迎Fork

# License

    Copyright 2016 haohaohu

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
