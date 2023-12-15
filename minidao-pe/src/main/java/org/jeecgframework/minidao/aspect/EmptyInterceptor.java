package org.jeecgframework.minidao.aspect;

import java.lang.reflect.Field;

public interface EmptyInterceptor {
	/**
	 * 插入拦截
	 * @param fields
	 * @return
	 */
	public boolean onInsert(Field[] fields,Object obj);
	/**
	 * 修改拦截
	 * @param fields
	 * @return
	 */
	public boolean onUpdate(Field[] fields,Object obj);

	/**
	 * 查询数据注入查询条件字段
	 * 
	 * 规则说明
	 * 1、只支持方法名字以select、query、get、page开头的dao方法
	 * 2、dao方法的第一个参数必须是需要注入条件的实体
	 * 3、不想注入saas隔离字段，给方法加 @IgnoreSaas
	 * @param fields
	 * @return
	 */
	public boolean onSelect(Field[] fields,Object obj);

}
