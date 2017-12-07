package com.alibaba.dubbo.rpc.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.*;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.util.Asserts;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author juntao.zhang
 * @version V1.0
 * @Description: TODO
 * @Package com.alibaba.dubbo.rpc.filter
 * @date 2017/12/6 22:08
 */
@Activate(group = Constants.CONSUMER, order = -20000)
public class ValidateFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(ValidateFilter.class);

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            Object[] arguments = invocation.getArguments();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            if (parameterAnnotations != null && parameterAnnotations.length != 0) {
                for (Annotation[] parameterAnnotation : parameterAnnotations) {
                    for ( int i = 0 ; i < parameterAnnotation.length ; i ++) {
                        Annotation annotation = parameterAnnotation[i];
                        Object argument = arguments[i];
                        if (annotation instanceof NotNull) {
                            Asserts.notNull(argument,((NotNull) annotation).message());
                        }
                        if (annotation instanceof NotEmpty) {
                            Asserts.notNull(argument,((NotEmpty) annotation).message());
                            if (argument instanceof String)
                            Asserts.notEmpty(String.valueOf(argument),((NotEmpty) annotation).message());
                        }
                        if (annotation instanceof Valid) {
                            Set<ConstraintViolation<Object>> validate = validator.validate(argument, new Class[]{});
                            Optional<ConstraintViolation<Object>> first = validate.stream().findFirst();
                            if (first.isPresent()){
                                ConstraintViolation<Object> objectConstraintViolation = first.get();
                                throw new IllegalArgumentException(String.format("field [%s] %s",objectConstraintViolation.getPropertyPath() , objectConstraintViolation.getMessage()));
                            }
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
