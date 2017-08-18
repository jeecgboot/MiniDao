package org.jeecgframework.minidao.datasource;

/**
 * 类名：DataSourceContextHolder.java 功能：获得和设置上下文环境的类，主要负责改变上下文数据源的名称
 * DataSourceContextHolder
 * 
 * @description:DataSourceContextHolder
 * @author 张代浩
 * @mail zhangdaiscott@163.com
 * @category www.jeecg.org
 * @date 20130817
 * @version V1.0
 */
public class DataSourceContextHolder {

	private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<DataSourceType>();

	public static void clearDataSourceType() {
		contextHolder.remove();
	}

	public static DataSourceType getDataSourceType() {
		return (DataSourceType) contextHolder.get();
	}

	public static void setDataSourceType(DataSourceType dataSourceType) {
		contextHolder.set(dataSourceType);
	}

}
