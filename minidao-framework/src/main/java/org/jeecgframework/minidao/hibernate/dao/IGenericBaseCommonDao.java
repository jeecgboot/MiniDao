package org.jeecgframework.minidao.hibernate.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;


/**
 * 
 * 类描述：DAO层泛型基类接口
 * 
 * @author: jeecg
 * @date： 日期：2012-12-8 时间：下午05:37:33
 * @version 1.0
 */
public interface IGenericBaseCommonDao {
	public <T> void save(T entity);
	public <T> void saveOrUpdate(T entity);
	public <T> void delete(T entitie);
	public <T> T get(T entitie);
	public <T> List<T> loadAll(T entitie);
	public <T> T get(Class<T> entityClass, final Serializable id);
	public Session getSession();
	public <T> T findUniqueByProperty(Class<T> entityClass, String propertyName, Object value);
	/**
	 * 根据主键删除指定的实体
	 * 
	 * @param <T>
	 * @param pojo
	 */
	public <T> void deleteEntityById(Class entityName, Serializable id);
}
