package com.alibaba.dubbo.common.utils;

import ch.qos.logback.classic.PatternLayout;

/**
 * CallChainPatternLayout
 *
 * @author chao.cheng
 * @date 2017/4/25
 */

public class CallChainPatternLayout extends PatternLayout {
    static {
        defaultConverterMap.put("callContext", CallChainConverter.class.getName());
    }
}
