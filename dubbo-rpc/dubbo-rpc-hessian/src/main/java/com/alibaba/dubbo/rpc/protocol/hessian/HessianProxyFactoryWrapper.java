package com.alibaba.dubbo.rpc.protocol.hessian;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.logging.Logger;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.HessianRemoteObject;

public class HessianProxyFactoryWrapper extends HessianProxyFactory{
	
	protected static Logger log = Logger.getLogger(HessianProxyFactoryWrapper.class
			.getName());
	
	public HessianProxyFactoryWrapper(){
		super();
	}
	
	public Object create(Class<?> api, URL url, ClassLoader loader) {
		if (api == null)
			throw new NullPointerException(
					"api must not be null for HessianProxyFactory.create()");
		InvocationHandler handler = null;

		handler = new HessianProxyWrapper(url, this, api);

		return Proxy.newProxyInstance(loader, new Class[] { api,
				HessianRemoteObject.class }, handler);
	}
	
}
