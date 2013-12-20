package org.jeecgframework.minidao.hibernate.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jeecgframework.minidao.hibernate.dao.IGenericBaseCommonDao;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * 
 * 类描述： DAO层泛型基类
 * 
 * @author: jeecg
 * @date： 日期：2012-12-7 时间：上午10:16:48
 * @param <T>
 * @param <PK>
 * @version 1.0
 */
@Component
@Transactional
public class GenericBaseCommonDao<T, PK extends Serializable> implements IGenericBaseCommonDao {
	/**
	 * 初始化Log4j的一个实例
	 */
	private static final Logger logger = Logger.getLogger(GenericBaseCommonDao.class);
	/**
	 * 注入一个sessionFactory属性,并注入到父类(HibernateDaoSupport)
	 * **/
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getSession() {
		// 事务必须是开启的(Required)，否则获取不到
		
		//update-begin--date:20130910 for:因为加了MiniDao接口自动扫描，导致开启事务失败，所以捕获获取session异常，如果从当前线程获取不到，就重新创建
		try{
			return sessionFactory.getCurrentSession();
		}catch(Exception e){
			return sessionFactory.openSession();
		}
		//update-end--date:20130910 for:因为加了MiniDao接口自动扫描，导致开启事务失败，所以捕获获取session异常，如果从当前线程获取不到，就重新创建
	}


	/**
	 * 根据传入的实体持久化对象
	 */
	public <T> void save(T entity) {
		try {
			getSession().save(entity);
			getSession().flush();
			if (logger.isDebugEnabled()) {
				logger.debug("保存实体成功," + entity.getClass().getName());
			}
		} catch (RuntimeException e) {
			logger.error("保存实体异常", e);
			throw e;
		}

	}
	
	/**
	 * 根据传入的实体添加或更新对象
	 * 
	 * @param <T>
	 * 
	 * @param entity
	 */

	public <T> void saveOrUpdate(T entity) {
		try {
			getSession().saveOrUpdate(entity);
			getSession().flush();
			if (logger.isDebugEnabled()) {
				logger.debug("添加或更新成功," + entity.getClass().getName());
			}
		} catch (RuntimeException e) {
			logger.error("添加或更新异常", e);
			throw e;
		}
	}

	/**
	 * 根据传入的实体删除对象
	 */
	public <T> void delete(T entity) {
		try {
			getSession().delete(entity);
			getSession().flush();
			if (logger.isDebugEnabled()) {
				logger.debug("删除成功," + entity.getClass().getName());
			}
		} catch (RuntimeException e) {
			logger.error("删除异常", e);
			throw e;
		}
	}


	/**
	 * 根据实体名字获取唯一记录
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public <T> T findUniqueByProperty(Class<T> entityClass, String propertyName, Object value) {
		Assert.hasText(propertyName);
		return (T) createCriteria(entityClass, Restrictions.eq(propertyName, value)).uniqueResult();
	}

	/**
	 * 创建Criteria对象，有排序功能。
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param orderBy
	 * @param isAsc
	 * @param criterions
	 * @return
	 */
	private <T> Criteria createCriteria(Class<T> entityClass, boolean isAsc, Criterion... criterions) {
		Criteria criteria = createCriteria(entityClass, criterions);
		if (isAsc) {
			criteria.addOrder(Order.asc("asc"));
		} else {
			criteria.addOrder(Order.desc("desc"));
		}
		return criteria;
	}

	/**
	 * 创建Criteria对象带属性比较
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param criterions
	 * @return
	 */
	private <T> Criteria createCriteria(Class<T> entityClass, Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return criteria;
	}
	/**
	 * 按属性查找对象列表.
	 */
	public <T> List<T> findByProperty(Class<T> entityClass, String propertyName, Object value) {
		Assert.hasText(propertyName);
		return (List<T>) createCriteria(entityClass, Restrictions.eq(propertyName, value)).list();
	}
	
	/**
	 * 根据Id获取对象。
	 */
	public <T> T get(Class<T> entityClass, final Serializable id) {

		return (T) getSession().get(entityClass, id);

	}

	public <T> T get(T entitie) {
		Criteria executableCriteria = getSession().createCriteria(entitie.getClass());
		executableCriteria.add(Example.create(entitie));
		if(executableCriteria.list().size() == 0){
			return null;
		}
		return (T) executableCriteria.list().get(0);
	}

	
	public <T> List<T> loadAll(T entitie) {
		Criteria executableCriteria = getSession().createCriteria(entitie.getClass());
		executableCriteria.add(Example.create(entitie));
		return executableCriteria.list();
	}
	
	/**
	 * 根据主键删除指定的实体
	 * 
	 * @param <T>
	 * @param pojo
	 */
	public <T> void deleteEntityById(Class entityName, Serializable id) {
		delete(get(entityName, id));
		getSession().flush();
	}
}
