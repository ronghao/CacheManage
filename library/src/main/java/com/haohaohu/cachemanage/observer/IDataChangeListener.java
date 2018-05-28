package com.haohaohu.cachemanage.observer;

/**
 * 数据变化监听接口
 *
 * @author haohao(ronghao3508@gmail.com) on 2017/12/16 下午 05:31
 * @version v1.0
 */
public interface IDataChangeListener {
    /**
     * 数据变化
     *
     * @param key 主键
     * @param value 值
     */
    void onDataChange(String key, String value);
}
