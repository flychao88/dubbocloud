package com.alibaba.dubbo.rpc.filter;


import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

/**
 * @author <a href="juntao.zhang@bkjk.com">juntao.zhang</a>
 * @Description:
 * @Package com.alibaba.dubbo.rpc.filter
 * @date 2017/12/7 13:18
 * @see
 */
public class ValidateFilterTest {
    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static void main(String[] args) {
        Person person = new Person();
        //person.setAddr("zzz");
        person.setName("zhangsan");
        person.setAge(11);
        Set<ConstraintViolation<Person>> validate = validator.validate(person, new Class[]{});
        Optional<ConstraintViolation<Person>> any = validate.stream().findFirst();
        if (any.isPresent()){
            ConstraintViolation<Person> personConstraintViolation = any.get();
            throw new IllegalArgumentException(String.format("field [%s] %s",personConstraintViolation.getPropertyPath() , personConstraintViolation.getMessage()));
        }









    }

}

class Person{

    @NotEmpty
    private String name;

    @NotNull
    private Integer age;

    @NotNull
    private String addr;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}