package com.alibaba.dubbo.rpc.filter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.dubbo.common.utils.CallChainContext;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcInvocation;


/**
 * @author zhulei007@ulic.com.cn
 * @author chao.cheng
 * @ClassName: AAALogFilter
 * @Description: 日志过滤器
 * @date 2017年3月7日 下午9:54:25
 */
@WebFilter(filterName = "logFilter", urlPatterns = "/*")
public class LogFilter implements Filter {

    private static final Logger log = Logger.getLogger(LogFilter.class.getName());

    /**
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException 设定文件
     * @Title: doFilter
     * @Description: 执行过滤的核心方法
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 将请求转换成HttpServletRequest请求
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        try {
            archieveId(request);
        } catch (Throwable e) {
            log.log(Level.SEVERE, "traceId或spanId解析出错!", e);
        }

        try {
            chain.doFilter(request, response);
        } catch (IOException e) {
            //还原线程名称
            throw e;
        } catch (ServletException e) {
            //还原线程名称
            throw e;
        } finally {
            CallChainContext.getContext().clearContext();
        }

    }

    private void archieveId(ServletRequest request) throws Throwable {
        @SuppressWarnings("unchecked")
        Map<String, String> globalMap = CallChainContext.getContext().get();
        String currentIdValue = "";
        String traceIdValue = "";
        String spanIdValue = "";

        if (null == globalMap) {
            globalMap = new ConcurrentHashMap<String, String>();
            CallChainContext.getContext().add(globalMap);
        }

        traceIdValue = globalMap.get(CallChainContext.TRACEID);
        if (StringUtils.isEmpty(traceIdValue)) {
            traceIdValue = Long.toHexString(System.currentTimeMillis());
        }

        spanIdValue = globalMap.get(CallChainContext.SPANID);
        if (StringUtils.isEmpty(spanIdValue)) {
            spanIdValue = CallChainContext.DEFAULT_ID;
        }


        globalMap.put(CallChainContext.TRACEID, traceIdValue);
        globalMap.put(CallChainContext.SPANID, spanIdValue);
        currentIdValue = globalMap.get(CallChainContext.CURRENTID);
        if (StringUtils.isEmpty(currentIdValue)) {
            //currentIdValue = CallChainContext.DEFAULT_ID;
            globalMap.put(CallChainContext.CURRENTID, spanIdValue);

        }

        RpcContext.getContext().setAttachment(CallChainContext.TRACEID, traceIdValue);
        RpcContext.getContext().setAttachment(CallChainContext.SPANID, spanIdValue);

    }

    public void init(FilterConfig config) {
    }

    public void destroy() {
    }
}