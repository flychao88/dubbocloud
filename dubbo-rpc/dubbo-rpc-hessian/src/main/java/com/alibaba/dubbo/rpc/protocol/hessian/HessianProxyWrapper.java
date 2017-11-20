package com.alibaba.dubbo.rpc.protocol.hessian;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.utils.CallChainContext;
import com.caucho.hessian.client.HessianConnection;
import com.caucho.hessian.client.HessianProxy;
import com.caucho.hessian.client.HessianProxyFactory;

/**
 * author: chao.cheng
 */
public class HessianProxyWrapper extends HessianProxy {


    private static final long serialVersionUID = 353338409377437466L;

    private static final Logger log = LoggerFactory.getLogger(HessianProxyWrapper.class
            .getName());

    public HessianProxyWrapper(URL url, HessianProxyFactory factory, Class<?> type) {
        super(url, factory, type);
    }

    protected void addRequestHeaders(HessianConnection conn) {
        super.addRequestHeaders(conn);
        
        StringBuffer sb = new StringBuffer();
        sb.append("[service url] - ")
            .append(getURL().getHost())
            .append(" - [appName] -")
            .append(getURL().getPath());

        log.info(sb.toString());
        
        conn.addHeader(CallChainContext.TRACEID, CallChainContext.getContext().getTraceId());
        conn.addHeader(CallChainContext.SPANID, CallChainContext.getContext().getSpanId());
    }


}
