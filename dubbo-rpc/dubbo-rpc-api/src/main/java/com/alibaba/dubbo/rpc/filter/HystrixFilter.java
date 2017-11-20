package com.alibaba.dubbo.rpc.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Date:2017/10/25
 * Name:chao.cheng
 **/
@Activate(group = Constants.CONSUMER, order = -10000)
public class HystrixFilter implements Filter {
    private final static Logger log = LoggerFactory.getLogger(HystrixFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        String requestVolume = invocation.getInvoker().getUrl().getParameter("requestvolume");
        String sleepMilliseconds = invocation.getInvoker().getUrl().getParameter("sleepmilliseconds");
        String errorPercentage = invocation.getInvoker().getUrl().getParameter("errorpercentage");
        String executionTimeoutEnabled = invocation.getInvoker().getUrl().getParameter("executiontimeoutenabled");
        String thresholdSwitch = invocation.getInvoker().getUrl().getParameter("thresholdswitch");

        Map<String, String> hystrixMap = new HashMap<String, String>();
        hystrixMap.put("requestVolume", requestVolume);
        hystrixMap.put("sleepMilliseconds", sleepMilliseconds);
        hystrixMap.put("errorPercentage", errorPercentage);
        hystrixMap.put("executionTimeoutEnabled", executionTimeoutEnabled);

        log.info("[hystrixMap参数是]"+hystrixMap);

        if (StringUtils.isNotEmpty(thresholdSwitch)) {
            if (thresholdSwitch.equalsIgnoreCase("true")) {
                DubboHystrixCommand command = new DubboHystrixCommand(invoker, invocation, hystrixMap);
                return command.execute();
            } else {
                return invoker.invoke(invocation);
            }
        }


        return invoker.invoke(invocation);
    }

}
