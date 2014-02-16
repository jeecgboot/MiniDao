package org.jeecgframework.minidao.factory;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.util.MiniDaoUtil;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * 
 * @Title:MiniDaoHandler
 * @description:Init MiniDao Bean
 * @author 张代浩
 * @mail zhangdaiscott@163.com
 * @category www.jeecg.org
 * @date 20130817
 * @version V1.0
 */
public class MiniDaoBeanFactory implements BeanFactoryPostProcessor {
	private static final Logger logger = Logger.getLogger(MiniDaoBeanFactory.class);
	/**MiniDao扫描路径*/
	private List<String> packagesToScan;
	

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
			throws BeansException {
		logger.debug("................MiniDaoBeanFactory................ContextRefreshed................begin...................");
		try {
			//循环传入的MiniDao配置
			for(String pack : packagesToScan){
				if(org.apache.commons.lang.StringUtils.isNotEmpty(pack)){
				   Set<Class<?>> classSet = PackagesToScanUtil.getClasses(pack);
				   for(Class<?> miniDaoClass : classSet){
					   if(miniDaoClass.isAnnotationPresent(MiniDao.class)){
						   //单独加载一个接口的代理类
						   ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
						   proxyFactoryBean.setBeanFactory(beanFactory);
						   proxyFactoryBean.setInterfaces(new Class[]{miniDaoClass});
						   proxyFactoryBean.setInterceptorNames(new String[]{"miniDaoHandler"});
						   String beanName = MiniDaoUtil.getFirstSmall(miniDaoClass.getSimpleName());
						   if(!beanFactory.containsBean(beanName)){
							   
							   //logger.info(".................MiniDaoBean.................init...................."+miniDaoClass.getName());
							   logger.info("MiniDao Interface [/"+miniDaoClass.getName()+"/] onto Spring Bean '"+beanName+"'");
							   beanFactory.registerSingleton(beanName,proxyFactoryBean.getObject());
						   }
					   }
				   }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("................MiniDaoBeanFactory................ContextRefreshed................end...................");
	}
	

	public List<String> getPackagesToScan() {
		return packagesToScan;
	}

	public void setPackagesToScan(List<String> packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

}
