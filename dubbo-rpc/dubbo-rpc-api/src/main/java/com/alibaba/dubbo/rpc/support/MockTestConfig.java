package com.alibaba.dubbo.rpc.support;

import java.io.Serializable;

/**
 * MockTestConfig
 *
 * @author chao.cheng
 * @date 2017/5/8
 */

public class MockTestConfig implements Serializable {
    private static final long  serialVersionUID = 5508512956753757169L;

    //挡板名称
    private String mockName;
    //服务接口类全路径名称
    private String facadePath;
    //服务接口方法名称
    private String methodName;
    //挡板请求返回json串
    private String responseJson;
    //mock执行时间
    private String delayTime;
    //响应对象路径
    private String responsePath;
    //异常类对象路径
    private String exceptionClass;
    //异常描述信息
    private String exceptionJson;


    public String getMockName() {
        return mockName;
    }

    public void setMockName(String mockName) {
        this.mockName = mockName;
    }

    public String getFacadePath() {
        return facadePath;
    }

    public void setFacadePath(String facadePath) {
        this.facadePath = facadePath;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getResponseJson() {
        return responseJson;
    }

    public void setResponseJson(String responseJson) {
        this.responseJson = responseJson;
    }

    public String getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(String delayTime) {
        this.delayTime = delayTime;
    }

    public String getResponsePath() {
        return responsePath;
    }

    public void setResponsePath(String responsePath) {
        this.responsePath = responsePath;
    }


    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getExceptionJson() {
        return exceptionJson;
    }

    public void setExceptionJson(String exceptionJson) {
        this.exceptionJson = exceptionJson;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MockTestConfig{");
        try {
            sb.append("mockName='").append(mockName).append('\'');
            sb.append(", facadePath='").append(facadePath).append('\'');
            sb.append(", methodName='").append(methodName).append('\'');
            sb.append(", responseJson='").append(responseJson).append('\'');
            sb.append(", delayTime='").append(delayTime).append('\'');
            sb.append(", responsePath='").append(responsePath).append('\'');
            sb.append(", exceptionClass='").append(exceptionClass).append('\'');
            sb.append(", exceptionJson='").append(exceptionJson).append('\'');
            sb.append('}');
        } catch (Exception e) {

        }
        return sb.toString();
    }
}
