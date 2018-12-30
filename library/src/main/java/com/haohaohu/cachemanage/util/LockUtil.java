package com.haohaohu.cachemanage.util;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 公平锁单例类
 *
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
