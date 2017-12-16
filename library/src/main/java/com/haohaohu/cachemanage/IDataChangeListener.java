package com.haohaohu.cachemanage;

/**
 * 数据变化监听
 *
 * @author haohao(ronghao3508@gmail.com) on 2017/12/16 下午 05:31
 * @version v1.0
 */
public interface IDataChangeListener {
    /**
     * 数据变化
     */
    void onDataChange(String str);
}
