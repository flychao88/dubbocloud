package com.alibaba.dubbo.config;

import com.alibaba.dubbo.common.utils.StringUtils;

/**
 * Date:2017/10/25
 * Name:chao.cheng
 **/
public class HystrixConfig extends AbstractConfig {
    private static final long serialVersionUID = 4267533505537413570L;

    /** 熔断器在整个统计时间内是否开启的阀值，默认20。
        也就是10秒钟内至少请求20次，熔断器才发挥起作用 **/
    private String requestvolume = "20";

    /** 熔断器默认工作时间,默认:5秒。
        熔断器中断请求5秒后会关闭重试,如果请求仍然失败,继续打开熔断器5秒,如此循环 **/
    private String sleepmilliseconds = "5000";

    /** 默认:50。当出错率超过50%后熔断器启动 **/
    private String errorpercentage = "50";

    /** 禁用超时,这里指dubbo **/
    private String  executiontimeoutenabled = "false";

    /** 是否启用熔断机制 **/
    private String thresholdswitch = "false";


    public String getRequestvolume() {
        return requestvolume;
    }

    public void setRequestvolume(String requestvolume) {
        if(StringUtils.isNotEmpty(requestvolume)) {
            this.requestvolume = requestvolume;
        }
    }

    public String getSleepmilliseconds() {
        return sleepmilliseconds;
    }

    public void setSleepmilliseconds(String sleepmilliseconds) {
        if(StringUtils.isNotEmpty(sleepmilliseconds)) {
            this.sleepmilliseconds = sleepmilliseconds;
        }
    }

    public String getErrorpercentage() {
        return errorpercentage;
    }

    public void setErrorpercentage(String errorpercentage) {
        if(StringUtils.isNotEmpty(errorpercentage)) {
            this.errorpercentage = errorpercentage;
        }
    }

    public String getExecutiontimeoutenabled() {
        return executiontimeoutenabled;
    }

    public void setExecutiontimeoutenabled(String executiontimeoutenabled) {
        if(StringUtils.isNotEmpty(executiontimeoutenabled)) {
            this.executiontimeoutenabled = executiontimeoutenabled;
        }
    }

    public String getThresholdswitch() {
        return thresholdswitch;
    }

    public void setThresholdswitch(String thresholdswitch) {
        if(StringUtils.isNotEmpty(thresholdswitch)) {
            this.thresholdswitch = thresholdswitch;
        }
    }
}
