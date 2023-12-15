package org.jeecgframework.minidao.annotation;

import java.lang.annotation.*;  
  
/** 
 * minidao参数注解
 * @author scott 
 * @date 2015-08-04 23:39 
 */  
@Target(ElementType.PARAMETER)  
@Retention(RetentionPolicy.RUNTIME)  
@Documented  
public @interface Param {  
    String value();  
}  