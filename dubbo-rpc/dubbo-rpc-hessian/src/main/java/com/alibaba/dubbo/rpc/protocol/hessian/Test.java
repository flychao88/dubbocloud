package com.alibaba.dubbo.rpc.protocol.hessian;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;

/**
 * Test
 *
 * @author chao.cheng
 * @date 2017/6/27
 */

public class Test {
/*
    public <T> Result aa() {
        final Invoker<T> tagert = proxyFactory.getInvoker(doRefer(type, url), type, url);
        try {
            Result result = tagert.invoke(invocation);
            Throwable e = result.getException();
            if (e != null) {
                for (Class<?> rpcException : rpcExceptions) {
                    if (rpcException.isAssignableFrom(e.getClass())) {

                    }
                }
            }
            return result;
        } catch (RpcException e) {

            throw e;
        } catch (Throwable e) {

        }
        return null;
    }

    public <T> T doRefer(URL url, Class<T> serviceType) {
        HessianProxyFactoryWrapper hessianProxyFactory = new HessianProxyFactoryWrapper();

        String client = url.getParameter(Constants.CLIENT_KEY, Constants.DEFAULT_HTTP_CLIENT);
        if ("httpclient".equals(client)) {
            hessianProxyFactory.setConnectionFactory(new HttpClientConnectionFactory());
        } else if (client != null && client.length() > 0 && ! Constants.DEFAULT_HTTP_CLIENT.equals(client)) {
            throw new IllegalStateException("Unsupported http protocol client=\"" + client + "\"!");
        }
        int timeout = url.getParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT);
        hessianProxyFactory.setConnectTimeout(timeout);
        hessianProxyFactory.setReadTimeout(timeout);
        return (T) hessianProxyFactory.create(serviceType, url.setProtocol("http").toJavaURL(), Thread.currentThread().getContextClassLoader());
    }
    */
}
