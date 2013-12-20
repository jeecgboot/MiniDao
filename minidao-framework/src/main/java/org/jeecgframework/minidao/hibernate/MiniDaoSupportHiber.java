package org.jeecgframework.minidao.hibernate;

import java.io.Serializable;
import java.util.List;

/**
 * 支持Hbiernate实体维护
 * MiniDao自动生成SQL
 * @param po
 */
public interface MiniDaoSupportHiber<T> {
    void saveByHiber(T  entity);
    <T> T getByIdHiber(Class<T> entityClass,final Serializable id);
    <T> T getByEntityHiber(T entity);
    void updateByHiber(T  entity);
    void deleteByHiber(T  entity);
    List<T> listByHiber(T  entity);
	/**
	 * 根据主键删除指定的实体
	 * @param <T>
	 * @param pojo
	 */
	public <T> void deleteByIdHiber(Class entityName, Serializable id);
}
