# CacheManage
> android缓存管理器，分为内存缓存和文件缓存两种<br>
> 先取内存数据，没有再从文件缓存中取

# 特点
+ 封装[ASimpleCache](https://github.com/yangfuhai/ASimpleCache)，继承其所有功能
+ 默认使用SD卡缓存
	+ getCacheDir()获取的缓存文件较容易被删除
	+ 大于1M的缓存文件，google建议使用getExternalCacheDir()缓存存储
+ 支持文件加密存储
	+ 支持String、byte、JSONObject、JSONArray
+ 双缓存：内存缓存和文件缓存
	+ 先取内存数据，没有再从文件缓存中取

# 使用方法
	
	CacheUtil.init(MainActivity.this);//初始化，必须调用，不需要加密功能
	CacheUtil.init(MainActivity.this,"WLIJkjdsfIlI789sd87dnu==","haohaoha");//初始化，必须调用，需要加密功能

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

# 项目添加方法
在根 build.gradle中添加

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
在项目build.gradle中添加

	dependencies {
	        compile 'com.github.ronghao:CacheManage:-SNAPSHOT'
	}

# 特别注意
+ 当未存储数据时，获取内部类对象可能会返回null，一般的对象（如果实现了默认构造函数）会返回一个新的对象实例

# 未完成任务
+ 添加文件名称base64

# 关于
+ 个人博客：[www.haohaohu.com](www.haohaohu.com)
+ 如果你也喜欢这个库，Star一下吧，欢迎Fork