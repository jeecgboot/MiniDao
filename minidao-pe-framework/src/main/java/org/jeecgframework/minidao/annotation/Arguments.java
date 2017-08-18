package org.jeecgframework.minidao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 规则： 1. [注释标签参数]必须和[方法参数]，保持顺序一致 2. [注释标签参数]的参数数目不能大于[方法参数]的参数数目 3.
 * 只有在[注释标签参数]标注的参数，才会传递到SQL模板里 4. 如果[方法参数]只有一个，如果用户不设置 [注释标签参数]，则默认参数名为miniDto
 * 
 * @description:MiniDao-Arguments标签(记录SQL模板参数名)
 * @author 张代浩
 * @mail zhangdaiscott@163.com
 * @category www.jeecg.org
 * @date 20130817
 * @version V1.0
 * 
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Arguments {

	String[] value() default {};
}
