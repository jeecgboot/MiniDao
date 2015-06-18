package org.jeecgframework.minidao.hibernate;

import java.io.Serializable;
import java.util.List;

/**
 * 支持Hbiernate实体维护 MiniDao自动生成SQL
 * 
 * @param po
 */
@SuppressWarnings("hiding")
public interface MiniDaoSupportHiber<T> {
	/**
	 * 删除对象
	 * 
	 * @param entity
	 */
	void deleteByHiber(T entity);

	/**
	 * 根据主键删除指定的实体
	 * 
	 * @param <T>
	 * @param pojo
	 */
	public <T> void deleteByIdHiber(Class<?> entityName, Serializable id);

	/**
	 * 获取的对象
	 * 
	 * @param entity
	 * @return
	 */
	<T> T getByEntityHiber(T entity);

	/**
	 * 获得对象
	 * 
	 * @param entityClass
	 * @param id
	 * @return
	 */
	<T> T getByIdHiber(Class<T> entityClass, final Serializable id);

	/**
	 * 查询对象列表
	 * 
	 * @param entity
	 * @return
	 */
	List<T> listByHiber(T entity);

	/**
	 * 保存实体
	 * 
	 * @param entity
	 */
	void saveByHiber(T entity);

	/**
	 * 更新对象
	 * 
	 * @param entity
	 */
	void updateByHiber(T entity);
}
