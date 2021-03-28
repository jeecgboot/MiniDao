/**
 * 
 */
package org.jeecgframework.minidao.annotation;

import org.jeecgframework.minidao.annotation.type.IdType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author scott
 * 自定义主键生成策略（目前只支持自增，后续待扩展）
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IdAutoGenerator {
	
	/**
	 * 主键策略
	 * native：ID自增
	 * @return
	 */
	IdType type() default IdType.AUTO;

}
