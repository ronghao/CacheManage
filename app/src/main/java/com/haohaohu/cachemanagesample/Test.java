package com.haohaohu.cachemanagesample;

/**
 * @author haohao on 2017/6/23 11:39
 * @version v1.0
 */
public class Test {
    private int d;
    private String str;

    Test(int d, String str) {
        this.d = d;
        this.str = str;
    }

    @Override
    public String toString() {
        return "{d:" + d + "   str:" + str + "}";
    }
}
