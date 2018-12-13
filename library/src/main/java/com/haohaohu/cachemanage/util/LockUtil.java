package com.haohaohu.cachemanage.util;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * 公平锁单例类
 * @author haohao(ronghao3508 gmail.com) on 2018/12/12 14:33
 * @version v1.0
 */
public class LockUtil {
    public static ReentrantReadWriteLock getInstance() {
        return ReentrantLockHolder.lock;
    }


    private static class ReentrantLockHolder {
        static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    }
}
