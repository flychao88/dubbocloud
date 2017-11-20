package com.alibaba.dubbo.config;

/**
 * Created by chengchao on 2017/4/21.
 */
public class MockSystemUrlConfig extends AbstractConfig {
    private static final long    serialVersionUID = 5508512956753757169L;

    // Mock系统URL
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
