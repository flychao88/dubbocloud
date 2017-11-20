package com.alibaba.dubbo.rpc.filter.http;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.SSLContext;

public class HttpConnectionPoolManage {

    private PoolingHttpClientConnectionManager cm;

    public HttpConnectionPoolManage() {
        init();
    }

    public void init(){
        LayeredConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (Throwable e){

        }

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();

        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        cm.setMaxTotal(500);
        cm.setDefaultMaxPerRoute(50);

    }

    public CloseableHttpClient getHttpClient(){
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setConnectionManagerShared(true)
                .build();

        return httpClient;
    }
}
