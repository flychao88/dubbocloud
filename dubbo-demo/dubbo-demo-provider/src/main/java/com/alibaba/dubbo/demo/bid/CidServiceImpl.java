package com.alibaba.dubbo.demo.bid;

import com.alibaba.dubbo.rpc.RpcContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengchao on 2017/4/21.
 */
public class CidServiceImpl implements CidService {
    public CidResponse cid(CidRequest request) {
        CidResponse response = new CidResponse();

        response.setId("cccccccccc");
        RpcContext.getContext().set("key","aa");




        return response;
    }
}
