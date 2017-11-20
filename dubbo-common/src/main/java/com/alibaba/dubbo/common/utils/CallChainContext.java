package com.alibaba.dubbo.common.utils;

import java.util.Map;

/**
 * CallChainContext
 *
 * @author chao.cheng
 * @date 2017/4/23
 */

public class CallChainContext {
    private ThreadLocal<Map> threadlocal = new ThreadLocal<Map>();
    public final static String TRACEID = "traceId";
    public final static String SPANID = "spanId";
    public final static String CURRENTID = "currentId";
    public final static String POINT = ".";
    public final static String POINTSPLIT = "\\.";
    public final static int DEFAULT = 1;
    public final static String DEFAULT_ID = "1";

    public static CallChainContext callChainContext = new CallChainContext();

    public static CallChainContext getContext() {
        return callChainContext;
    }

    public void add(Map value) {
        threadlocal.set(value);
    }

    public Map get() {
        return threadlocal.get();
    }

    public String getTraceId() {

        Map<String, String> globalMap = threadlocal.get();
        if (null != globalMap) {
            String traceId = globalMap.get(TRACEID);
            return traceId == null ? "" : traceId;
        }
        return "";
    }

    public String getSpanId() {
        Map<String, String> globalMap = threadlocal.get();
        if (null != globalMap) {
            String spanId = globalMap.get(SPANID);
            return spanId == null ? "" : spanId;
        }
        return "";
    }

    public String getCurrentId() {
        Map<String, String> globalMap = threadlocal.get();
        if (null != globalMap) {
            String currentId = globalMap.get(CURRENTID);
            return currentId == null ? "" : currentId;
        }
        return "";
    }

    /**
     * 清空线程对象，线程结束前必须调用
     */
    public void clearContext() {
        threadlocal.remove();
    }
}


