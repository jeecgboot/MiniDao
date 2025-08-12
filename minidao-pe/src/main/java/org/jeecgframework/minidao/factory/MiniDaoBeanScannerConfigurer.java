package org.jeecgframework.minidao.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.aop.MiniDaoHandler;
import org.jeecgframework.minidao.aspect.EmptyInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;

/**
 * 扫描配置文件
 * 
 * @author JueYue
 * @date 2014年11月15日 下午9:48:31
 */
public class MiniDaoBeanScannerConfigurer implements BeanDefinitionRegistryPostProcessor {
	private static final Log logger = LogFactory.getLog(MiniDaoBeanScannerConfigurer.class);
	
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
	private ApplicationContext applicationContext;
	/**
	 * Minidao拦截器
	 */
	private EmptyInterceptor emptyInterceptor;

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		/**
		 * 注册代理类
		 */
		initRegisterRequestProxyHandler(registry);

		MiniDaoClassPathMapperScanner scanner = new MiniDaoClassPathMapperScanner(registry, annotation);
		/**
		 * 加载Dao层接口
		 */
		if(this.basePackage != null){
			scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
		}else{
			logger.error("MiniDao 扫描路径未配置，请设置 minidao.base-package 属性，否则 MiniDao 无法使用！");
		}
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	/**
	 * RequestProxyHandler 手工注册代理类,减去了用户配置XML的烦恼
	 * 
	 * @param registry
	 */
	private void initRegisterRequestProxyHandler(BeanDefinitionRegistry registry) {
		GenericBeanDefinition jdbcDaoProxyDefinition = new GenericBeanDefinition();
		jdbcDaoProxyDefinition.setBeanClass(MiniDaoHandler.class);
		jdbcDaoProxyDefinition.getPropertyValues().add("formatSql", formatSql);
		jdbcDaoProxyDefinition.getPropertyValues().add("keyType", keyType);
		jdbcDaoProxyDefinition.getPropertyValues().add("showSql", showSql);
		//update-begin---author:chenrui ---date:20241021  for：[TV360X-2759]springboot3使用分库数据源配置，启动提示Bean被提前实例化 #3001------------
		// 标识miniDaoHandler为框架基础设施角色
		jdbcDaoProxyDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		//update-end---author:chenrui ---date:20241021  for：[TV360X-2759]springboot3使用分库数据源配置，启动提示Bean被提前实例化 #3001------------
		//jdbcDaoProxyDefinition.getPropertyValues().add("dbType", dbType);
		if(emptyInterceptor!=null){
			jdbcDaoProxyDefinition.getPropertyValues().add("emptyInterceptor", emptyInterceptor);
		}

		//update-begin---author:scott----date:20210608------for:-其中jdbcTemplate、namedParameterJdbcTemplate注入是个null.注入失败.-------
		if(applicationContext!=null){
			jdbcDaoProxyDefinition.getPropertyValues().add("applicationContext", applicationContext);
		}
		//update-end---author:scott----date:20210608------for:其中jdbcTemplate、namedParameterJdbcTemplate注入是个null.注入失败.--------

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

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setEmptyInterceptor(EmptyInterceptor emptyInterceptor) {
		this.emptyInterceptor = emptyInterceptor;
	}
}
