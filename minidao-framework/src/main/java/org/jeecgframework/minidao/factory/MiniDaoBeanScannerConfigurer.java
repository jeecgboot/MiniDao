package org.jeecgframework.minidao.factory;

import java.lang.annotation.Annotation;

import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.aop.MiniDaoHandler;
import org.jeecgframework.minidao.hibernate.dao.impl.GenericBaseCommonDao;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

/**
 * 扫描配置文件
 * 
 * @author JueYue
 * @date 2014年11月15日 下午9:48:31
 */
public class MiniDaoBeanScannerConfigurer implements BeanDefinitionRegistryPostProcessor {
	/**
	 * ,; \t\n
	 */
	private String basePackage;
	/**
	 * 默认是IDao,推荐使用Repository
	 */
	private Class<? extends Annotation> annotation = MiniDao.class;
	/**
	 * Map key类型
	 */
	private String keyType = "origin";
	/**
	 * 是否格式化sql
	 */
	private boolean formatSql = false;
	/**
	 * 是否输出sql
	 */
	private boolean showSql = false;
	/**
	 * 数据库类型
	 */
	private String dbType;

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		/**
		 * 首先Hibernate对象
		 */
		registerGenericBaseCommonDao(registry);
		/**
		 * 注册代理类
		 */
		registerRequestProxyHandler(registry);

		MiniDaoClassPathMapperScanner scanner = new MiniDaoClassPathMapperScanner(registry, annotation);
		/**
		 * 加载Dao层接口
		 */
		scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	private void registerGenericBaseCommonDao(BeanDefinitionRegistry registry) {
		GenericBeanDefinition genericBaseCommonDaofinition = new GenericBeanDefinition();
		genericBaseCommonDaofinition.setBeanClass(GenericBaseCommonDao.class);
		registry.registerBeanDefinition("genericBaseCommonDao", genericBaseCommonDaofinition);

	}

	/**
	 * RequestProxyHandler 手工注册代理类,减去了用户配置XML的烦恼
	 * 
	 * @param registry
	 */
	private void registerRequestProxyHandler(BeanDefinitionRegistry registry) {
		GenericBeanDefinition jdbcDaoProxyDefinition = new GenericBeanDefinition();
		jdbcDaoProxyDefinition.setBeanClass(MiniDaoHandler.class);
		jdbcDaoProxyDefinition.getPropertyValues().add("formatSql", formatSql);
		jdbcDaoProxyDefinition.getPropertyValues().add("keyType", keyType);
		jdbcDaoProxyDefinition.getPropertyValues().add("showSql", showSql);
		jdbcDaoProxyDefinition.getPropertyValues().add("dbType", dbType);
		registry.registerBeanDefinition("miniDaoHandler", jdbcDaoProxyDefinition);
	}

	public void setAnnotation(Class<? extends Annotation> annotation) {
		this.annotation = annotation;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public void setFormatSql(boolean formatSql) {
		this.formatSql = formatSql;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

}
