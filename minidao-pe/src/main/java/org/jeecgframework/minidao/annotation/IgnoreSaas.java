package org.jeecgframework.minidao.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @description:MiniDao
 * @author 张代浩
 * @mail jeecgos@163.com
 * @category www.jeecg.com
 * @date 20130817
 * @version V1.0
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface IgnoreSaas {

	String value() default "";
}
