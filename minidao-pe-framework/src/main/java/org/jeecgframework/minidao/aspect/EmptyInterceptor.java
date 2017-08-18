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
}
