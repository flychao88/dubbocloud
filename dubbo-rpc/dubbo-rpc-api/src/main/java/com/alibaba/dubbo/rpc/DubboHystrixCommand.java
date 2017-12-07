package com.alibaba.dubbo.rpc;
import com.alibaba.dubbo.common.URL;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;


/**
 * Date:2017/10/25
 * Name:chao.cheng
 **/
public class DubboHystrixCommand extends HystrixCommand<Result> {

    private static Logger logger  = LoggerFactory.getLogger(DubboHystrixCommand.class);
    private static final int DEFAULT_THREADPOOL_CORE_SIZE = 30;
    private Invoker<?> invoker;
    private Invocation invocation;




    public DubboHystrixCommand(Invoker<?> invoker, final Invocation invocation, final Map<String,String> hystrixMap){


        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(invoker.getInterface().getName()))
                .andCommandKey(HystrixCommandKey.Factory.asKey(String.format("%s_%d", invocation.getMethodName(),
                        invocation.getArguments() == null ? 0 : invocation.getArguments().length)))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        //10秒钟内至少19次请求失败，熔断器才发挥起作用
                        .withCircuitBreakerRequestVolumeThreshold(Integer.parseInt(hystrixMap.get("requestVolume")))
                        //熔断器中断请求30秒后会进入半打开状态,放部分流量过去重试
                        .withCircuitBreakerSleepWindowInMilliseconds(Integer.parseInt(hystrixMap.get("sleepMilliseconds")))
                        //错误率达到50开启熔断保护
                        .withCircuitBreakerErrorThresholdPercentage(Integer.parseInt(hystrixMap.get("errorPercentage")))
                        //使用dubbo的超时，禁用这里的超时
                        .withExecutionTimeoutEnabled(Boolean.valueOf(hystrixMap.get("executionTimeoutEnabled"))))
                //线程池为30
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(getThreadPoolCoreSize(invoker.getUrl()))));


        this.invoker=invoker;
        this.invocation=invocation;
    }

    /**
     * 获取线程池大小
     *
     * @param url
     * @return
     */
    private static int getThreadPoolCoreSize(URL url) {
        if (url != null) {
            int size = url.getParameter("ThreadPoolCoreSize", DEFAULT_THREADPOOL_CORE_SIZE);
            if (logger.isDebugEnabled()) {
                logger.debug("ThreadPoolCoreSize:" + size);
            }
            return size;
        }

        return DEFAULT_THREADPOOL_CORE_SIZE;

    }

    @Override
    protected Result run() throws Exception {
        return invoker.invoke(invocation);
    }






}
