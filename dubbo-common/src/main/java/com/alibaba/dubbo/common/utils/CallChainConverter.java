package com.alibaba.dubbo.common.utils;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.HashMap;
import java.util.Map;


/**
 * CallChainConverter
 *
 * @author chao.cheng
 * @date 2017/4/25
 */

public class CallChainConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        String logPrefixStr = LogPrefixContext.get();

        StringBuilder builder = new StringBuilder();

        builder.append("GUID[");
        builder.append(CallChainContext.getContext().getTraceId());
        builder.append("] - LEVEL[");
        builder.append(CallChainContext.getContext().getCurrentId());
        builder.append("] ");

        if(StringUtils.isNotEmpty(logPrefixStr)) {
            builder.append(" BUSSPREFIX[");
            builder.append(logPrefixStr);
            builder.append("] ");
        }

        return builder.toString();

    }
}
