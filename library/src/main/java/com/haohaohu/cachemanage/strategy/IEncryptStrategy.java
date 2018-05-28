package com.haohaohu.cachemanage.strategy;

/**
 * 加解密策略
 *
 * @author haohao(ronghao3508@gmail.com) on 2018/5/28 16:14
 * @version v1.0
 */
public interface IEncryptStrategy {

    String encrypt(String str);

    String decode(String str);
}
