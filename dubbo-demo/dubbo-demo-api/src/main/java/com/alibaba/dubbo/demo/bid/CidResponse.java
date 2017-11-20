package com.alibaba.dubbo.demo.bid;

import java.io.Serializable;

/**
 * Created by chengchao on 2017/4/21.
 */
public class CidResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CidResponse{" +
                "id='" + id + '\'' +
                '}';
    }
}
