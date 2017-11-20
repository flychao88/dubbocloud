package com.alibaba.dubbo.common.utils;

/**
 * LogPrefixContext
 *
 * @author chao.cheng
 * @date 2017/5/25
 */

public class LogPrefixContext {

    public static ThreadLocal<String> threadLocal = new ThreadLocal<String>();

    public static void set(String value) {
        threadLocal.set(value);
    }

    public static String get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }

}
