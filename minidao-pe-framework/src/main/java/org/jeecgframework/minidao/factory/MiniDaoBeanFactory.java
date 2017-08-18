package org.jeecgframework.minidao.factory;

import java.lang.reflect.Proxy;

import org.jeecgframework.minidao.aop.MiniDaoHandler;
import org.springframework.beans.factory.FactoryBean;

/**
 * 持久层的的工厂
 * 
 * @author JueYue
 * @date 2014年12月7日 下午4:18:47
 * @param <T>
 */
public class MiniDaoBeanFactory<T> implements FactoryBean<T> {

	private Class<T> daoInterface;

	private MiniDaoHandler proxy;

	@Override
	public T getObject() throws Exception {
		return newInstance();
	}

	@Override
	public Class<?> getObjectType() {
		return daoInterface;
	}

	public MiniDaoHandler getProxy() {
		return proxy;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@SuppressWarnings("unchecked")
	private T newInstance() {
		return (T) Proxy.newProxyInstance(daoInterface.getClassLoader(), new Class[] { daoInterface }, proxy);
	}

	public void setProxy(MiniDaoHandler proxy) {
		this.proxy = proxy;
	}

	public void setDaoInterface(Class<T> daoInterface) {
		this.daoInterface = daoInterface;
	}

}
