package com.alibaba.dubbo.rpc.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.*;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
/**
 * @author juntao.zhang
 * @version V1.0
 * @Description: TODO
 * @Package com.alibaba.dubbo.rpc.filter
 * @date 2017/12/6 22:08
 */
@Activate(group = Constants.CONSUMER, order = -10000)
public class ValidateFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(ValidateFilter.class);

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());



            //1、method 判断有无valid注解

            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            if (parameterAnnotations != null && parameterAnnotations.length != 0) {
                for (Annotation[] parameterAnnotation : parameterAnnotations) {
                    for (Annotation annotation : parameterAnnotation) {
                        if (annotation instanceof Valid) {
                            //Set<ConstraintViolation<Car>> constraintViolations = validator.validate(car);
                        }
                    }
                }


            }


            //2、


            //3、检验对象参数中属性
            Object[] arguments1 = invocation.getArguments();
            if (arguments1 != null){
                for (int i = 0 ; i < arguments1.length ; i ++){
                    Field[] declaredFields = arguments1.getClass().getDeclaredFields();
                    if (declaredFields != null){
                        for (Field field : declaredFields){
                            /*Annotation annotation = field.getAnnotation();*/






                        }
                    }
                }
            }
        } catch (NoSuchMethodException | IllegalArgumentException  e) {
            log.error("检验参数validate异常", e);
            return new RpcResult(e);
        }
        return invoker.invoke(invocation);
    }

}
