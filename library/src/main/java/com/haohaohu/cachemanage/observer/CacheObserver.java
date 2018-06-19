package com.haohaohu.cachemanage.observer;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 数据监控器
 * 1、当数据变化时候，回调所有监听器
 *
 * @author haohao(ronghao3508@gmail.com) on 2017/12/16 下午 05:30
 * @version v1.0
 */
public class CacheObserver {

    private HashMap<String, List<IDataChangeListener>> mListenerMap = new HashMap<>();

    public static CacheObserver getInstance() {
        return Holder.observer;
    }

    public static void addListener(String key, IDataChangeListener listener) {
        CacheObserver.getInstance().addObserver(key, listener);
    }

    public synchronized void addObserver(String key, IDataChangeListener listener) {
        if (TextUtils.isEmpty(key) || listener == null) {
            return;
        }
        List<IDataChangeListener> list = mListenerMap.get(key);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(listener);
        mListenerMap.put(key, list);
    }

    public synchronized void addObserver(String key, List<IDataChangeListener> listeners) {
        if (TextUtils.isEmpty(key) || listeners == null) {
            return;
        }
        List<IDataChangeListener> list = mListenerMap.get(key);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.addAll(listeners);
        mListenerMap.put(key, list);
    }

    public synchronized void removeObserver(String key, IDataChangeListener listener) {
        if (TextUtils.isEmpty(key) || listener == null) {
            return;
        }
        List<IDataChangeListener> list = mListenerMap.get(key);
        if (list == null) {
            return;
        }
        list.remove(listener);
        mListenerMap.put(key, list);
    }

    public synchronized void removeObservers(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mListenerMap.remove(key);
    }

    public void notifyDataChange(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        List<IDataChangeListener> list = mListenerMap.get(key);
        if (list == null) {
            return;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            list.get(i).onDataChange(key, value);
        }
    }

    private static class Holder {
        private static CacheObserver observer = new CacheObserver();
    }
}
