package com.alibaba.dubbo.rpc.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.json.ParseException;
import com.alibaba.dubbo.common.utils.CallChainContext;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.dubbo.rpc.filter.http.HttpConnectionPoolManage;
import com.alibaba.dubbo.rpc.support.MockTestConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chengchao on 2017/4/20.
 */
@Activate(group = Constants.CONSUMER, order = -10000)
public class MockTestFilter implements Filter {
	private final static Logger log = LoggerFactory.getLogger(MockTestFilter.class);
//    private static final Logger log = Logger.getLogger(MockTestFilter.class.getName());

    private static HttpConnectionPoolManage connectionPoolManage = new HttpConnectionPoolManage();
    private static OkHttpClient okHttpClient = new OkHttpClient();
    
    private final static String FACADE_PATH = "facadePath";
    private final static String APP_CODE = "appCode";
    private final static String METHOD_NAME = "methodName";
    private final static String IP = "ip";

    public Result invoke(Invoker<?> invoker, Invocation inv) throws RpcException {
//        log.info("[print current methodName:" + inv.getMethodName() + " gloab transmit parameter:" + inv.getAttachments() + " interface name:" + inv.getInvoker().getInterface());


        if(RpcContext.getContext().isConsumerSide()) {
            //设置traceId和spanId的值
            globalDataTranster(inv);
        }

        //判断mock环境
        Result result = invokeMockEnv(invoker, inv);
        return result;
    }

    /**
     * 检查是否是Mock测试环境
     * 如果是测试环境则拦截mock，如果不是则放行走正式逻辑
     *
     * @param invoker
     * @param inv
     * @return
     */
    private Result invokeMockEnv(Invoker<?> invoker, Invocation inv) {
    	long start = System.currentTimeMillis();
    	
        String env = inv.getInvoker().getUrl().getParameter("env");
        String mockUrl = inv.getInvoker().getUrl().getParameter("mockurl");
        String facadeName = inv.getInvoker().getInterface().getName();
        String applicationName = invoker.getUrl().getParameter("application");
        String methodName = inv.getMethodName();

        Result result = null;

        if (StringUtils.isEmpty(env)) {
            return invoker.invoke(inv);

        } else if (StringUtils.isNotEmpty(env) && env.equalsIgnoreCase("test")
                && StringUtils.isNotEmpty(mockUrl)) {

            //获取mock数据
            String mockData = getMockSystemData(mockUrl, facadeName, applicationName, methodName);
//            log.info("MockTestFilte.invokeMockEnv - mockData: " + mockData + delayTime(start));

            //如果得到的mock数据为空，则放行，不进行Mock操作
            if (StringUtils.isEmpty(mockData)) {
                return invoker.invoke(inv);
            }
            
//            log.info("MockTestFilte.invokeMockEnv - getmockover - data: " + mockData + delayTime(start));

            //解析mock数据
            MockTestConfig mockTestConfig = parseMockData(mockData, MockTestConfig.class);

            //执行mock处理时间
            exeMockDelayTime(mockTestConfig);

            //模拟异常抛出
            exeMockExceptionThrow(mockTestConfig);


            if (null != mockTestConfig) {
                result = parseAndReflectResponse(mockTestConfig);
            } else {
                log.warn("解析mock json串到对象为空 json:" + mockData);
            }
        } else {
            log.warn("dubbo环境配置有问题，请检查application的env设置！env:" + env);
        }
        
//        log.info("MockTestFilte.invokeMockEnv - result - " + (result == null ? "result is null": result.getValue()) + delayTime(start));

        return result;
    }


    /**
     * 执行mock处理时间
     *
     * @param mockTestConfig
     */
    private void exeMockDelayTime(MockTestConfig mockTestConfig) {
        String delayTimeStr = mockTestConfig.getDelayTime();
        if (StringUtils.isNotEmpty(delayTimeStr)) {
            Integer delayTime = Integer.parseInt(delayTimeStr);
            if (delayTime > 0) {
                try {
                    Thread.currentThread().sleep(delayTime);
                } catch (InterruptedException e) {
                    log.info( "Mock系统执行打转时间出错! mock参数是:" + mockTestConfig, e);
                    
                }
            }
        }
    }

    /**
     * 模拟异常抛出
     */
    private void exeMockExceptionThrow(MockTestConfig mockTestConfig) {
        String exceptionPath = mockTestConfig.getExceptionClass();
        String exceptionMsg = mockTestConfig.getExceptionJson();

        if (StringUtils.isNotEmpty(exceptionPath)) {
            try {
                if (StringUtils.isEmpty(exceptionMsg)) {
                    exceptionMsg = "Mock模拟Exception抛出 exceptionPath:" + exceptionPath;
                }
                Class exceptionClass = Class.forName(exceptionPath);
                Exception exceptionObj = (Exception) exceptionClass.newInstance();
                throw exceptionObj;
            } catch (ClassNotFoundException cnfe) {
                log.error( "异常类全路径没有找到 exceptionPath:" + exceptionPath);

            } catch (Exception e) {
                RpcException rpcException = new RpcException(exceptionMsg, e);
                rpcException.setStackTrace(e.getStackTrace());
                throw rpcException;
            }
        }
    }

    /**
     * traceId和spanId传递
     *
     * @param inv
     */
    private void globalDataTranster(Invocation inv) {
    	long start = System.currentTimeMillis();
//    	log.info("MockTestFilte.globalDataTranster - start");
        if (inv instanceof RpcInvocation) {

            Map<String, String> globalMap = CallChainContext.getContext().get();

            initCallChainDataMap(globalMap);

            String traceIdValue = globalMap.get(CallChainContext.TRACEID);
            String spanIdValue = globalMap.get(CallChainContext.SPANID);
            String currentIdValue = globalMap.get(CallChainContext.CURRENTID);

            StringBuffer strBuff = new StringBuffer();

            try {
                if (!spanIdValue.contains(CallChainContext.POINT)) {
                    spanIdValue = strBuff.append(spanIdValue).append(CallChainContext.POINT).append(CallChainContext.DEFAULT).toString();
                } else {
                    String[] spanIdArr = spanIdValue.split(CallChainContext.POINTSPLIT);
                    Integer spanIdInt = Integer.parseInt(spanIdArr[spanIdArr.length - CallChainContext.DEFAULT]) + CallChainContext.DEFAULT;
                    spanIdValue = strBuff.append(currentIdValue).append(CallChainContext.POINT).append(spanIdInt).toString();
                }

            } catch (Throwable e) {
                log.error( "字符串截取或者数据转型失败！traceId:" + traceIdValue + " spanId:" + spanIdValue, e);
            }

            globalMap.put(CallChainContext.TRACEID, traceIdValue);
            globalMap.put(CallChainContext.SPANID, spanIdValue);

            RpcInvocation rpcInvocation = (RpcInvocation) inv;
            rpcInvocation.setAttachment(CallChainContext.TRACEID, traceIdValue);
            rpcInvocation.setAttachment(CallChainContext.SPANID, spanIdValue);
            RpcContext.getContext().setAttachment(CallChainContext.TRACEID, traceIdValue);
            RpcContext.getContext().setAttachment(CallChainContext.SPANID, spanIdValue);
            
//            log.info("MockTestFilte.globalDataTranster - spanid： " + spanIdValue + " currentIdValue: " + currentIdValue + " traceIdValue: " + traceIdValue + delayTime(start));
        }
    }


    /**
     * 初始化全局map
     * 如果没有值,则是第一次使用,初始化tradeId和spanId
     * @param globalMap
     */
    public void initCallChainDataMap(Map<String, String> globalMap) {
        if (null == globalMap) {
            globalMap = new ConcurrentHashMap<String, String>();
            CallChainContext.getContext().add(globalMap);

            String traceIdValue = globalMap.get(CallChainContext.TRACEID);
            if (StringUtils.isEmpty(traceIdValue)) {
                traceIdValue = Long.toHexString(System.currentTimeMillis());
                globalMap.put(CallChainContext.TRACEID, traceIdValue);
            }

            String spanIdValue = globalMap.get(CallChainContext.SPANID);
            if (StringUtils.isEmpty(spanIdValue)) {
                spanIdValue = CallChainContext.DEFAULT_ID;
                globalMap.put(CallChainContext.SPANID, spanIdValue);
            }

            String currentIdValue = globalMap.get(CallChainContext.CURRENTID);
            if (StringUtils.isEmpty(currentIdValue)) {
                //currentIdValue = CallChainContext.DEFAULT_ID;
                globalMap.put(CallChainContext.CURRENTID, spanIdValue);

            }
        }
    }


    /**
     * 根据mock系统传进来的json进行解析，与MockTestConfig进行映射
     *
     * @param mockData
     * @return
     */
    public <T> T parseMockData(String mockData, Class<T> mockCls) throws RpcException {
        T object = null;
        try {
            object = JSON.parse(mockData, mockCls);
        } catch (ParseException e) {
            String errorMessage = "mockData json串解析出错! json:" + mockData;
            log.error( errorMessage);
            throw new RpcException(errorMessage, e.getCause());
        }
        return object;

    }

    /**
     * 解析请求响应类
     * 根据从Mock系统中得到相关的响应类进行解析，并根据Mock系统中设置的值进行注入
     *
     * @return
     */
    private Result parseAndReflectResponse(MockTestConfig mockTestConfig) {
        RpcResult result = new RpcResult();
        try {
            String responsePath = mockTestConfig.getResponsePath();
            if (StringUtils.isEmpty(responsePath)) {
                throw new RpcException("服务接口类全路径名称(facadeClass)为null");
            }

            Class responseClass = Class.forName(responsePath);
            Object responseBean = responseClass.newInstance();
            String responseJson = mockTestConfig.getResponseJson();
            responseBean = parseMockData(responseJson, responseBean.getClass());

            if (responseBean == null) {
                log.error( "响应json串映射成javaBean,ResponseBean为空! facadeClass:" + responsePath +
                        "  responseJson:" + responseJson);
                return result;
            }

            result.setValue(responseBean);

        } catch (Exception e) {
            log.error( e.toString(), e);
        }
        return result;
    }

    /**
     * 通过Http的方式发送mock请求数据
     * 并得到请求结果
     *
     * @param mockUrl
     */
    private String getMockSystemData(String mockUrl, String facadeName, String applicationName, String methodName) {
    	
    	String result = null;
    	
    	//httpclient
    	result = httpRequest(mockUrl, facadeName, applicationName, methodName);
    	
    	//okhttp
//    	result = okhttpRequest(mockUrl, facadeName, applicationName, methodName);
    	
    	return result;

    }
    
    private String okhttpRequest(String mockUrl, String facadeName, String applicationName, String methodName) {

        String ip = NetUtils.getLocalAddress().getHostAddress();

        RequestBody body = new FormBody.Builder()
                .add(FACADE_PATH, facadeName)
                .add(APP_CODE, applicationName)
                .add(METHOD_NAME, methodName)
                .add(IP, ip)
                .build();

        Request request = new Request.Builder()
                .url(mockUrl)
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            String responseBody = response.body().string();
            log.info(responseBody);
            return responseBody;
        } catch (Throwable e) {
            log.error( "mock系统连续不通,请检查相关配置！mockUrl:" + mockUrl, e);
        }
        return null;
    }
    
    private String httpRequest(String mockUrl, String facadeName, String applicationName, String methodName){
    	String result = null;
    	try {
    		String ip = NetUtils.getLocalAddress().getHostAddress();
    		result = post(mockUrl, getList(facadeName, applicationName, methodName, ip));
		} catch (Throwable e) {
			log.error( "MockTestFilter.getMockSystemData-mock系统连续不通,请检查相关配置！mockUrl:" + mockUrl, e);
		}
    	
    	return result;
    }
    
    private String post(String url, List<NameValuePair> nvps) throws IOException{
		CloseableHttpClient httpclient = connectionPoolManage.getHttpClient();
		
		HttpPost httpPost = new HttpPost(url);
		
		if(nvps != null)
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		
		CloseableHttpResponse response = httpclient.execute(httpPost);
		
		String result = null;
		if(response.getStatusLine().getStatusCode() == 200){
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity);
		}
		
		httpclient.close();
		
		return result;
	}
    
    private List<NameValuePair> getList(String facadePath, String appCode
    		, String methodName
    		, String ip){
		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair(FACADE_PATH, facadePath));
		nvps.add(new BasicNameValuePair(APP_CODE, appCode));
		nvps.add(new BasicNameValuePair(METHOD_NAME, methodName));
		nvps.add(new BasicNameValuePair(IP, ip));
		return nvps;
	}
    
    private String delayTime(long start){
    	return "-" + (System.currentTimeMillis() - start) + "ms - ";
    }

    public static void main(String[] args) {
//      globalDataTranster(null);
//      System.out.println("spanid： "+  CallChainContext.getContext().getSpanId() + " currentIdValue: " +  CallChainContext.getContext().getCurrentId() + " traceIdValue: " +  CallChainContext.getContext().getTraceId());
//      globalDataTranster(null);
//      System.out.println("spanid： "+  CallChainContext.getContext().getSpanId() + " currentIdValue: " +  CallChainContext.getContext().getCurrentId() + " traceIdValue: " +  CallChainContext.getContext().getTraceId());
//      globalDataTranster(null);
//      System.out.println("spanid： "+  CallChainContext.getContext().getSpanId() + " currentIdValue: " +  CallChainContext.getContext().getCurrentId() + " traceIdValue: " +  CallChainContext.getContext().getTraceId());
    	
//    	String mockUrl = "http://10.63.11.130:8080/mock-admin-platform/mockManage/getMockConfig";
//    	String facadeName = "com.ulpay.dubbox.facade.AsynAccountConfigFacade";
//    	String applicationName = "ulpay-dubbox-core";
//    	String methodName = "getAcmtcasyccInfo";
//    	String result = getMockSystemData(mockUrl, facadeName, applicationName, methodName);
//    	System.err.println(result);
    	
    }
}
