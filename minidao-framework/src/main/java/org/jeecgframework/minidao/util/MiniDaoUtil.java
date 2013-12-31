package org.jeecgframework.minidao.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;


/**
 * 
 * @Title:JdkLocalUtil
 * @description:JdkLocalUtil
 * @author 张代浩
 * @date Jul 5, 2013 2:58:29 PM
 * @version V1.0
 */
public class MiniDaoUtil {
	private static final Logger logger = Logger.getLogger(MiniDaoUtil.class);

	public static void main(String[] args) throws Exception {
		logger.debug(isWrapClass(Long.class));
		logger.debug(isWrapClass(Integer.class));
		logger.debug(isWrapClass(String.class));
	}

	/**
	 * 判断Class是否是基本包装类型
	 * 
	 * @param clz
	 * @return
	 */
	public static boolean isWrapClass(Class clz) {
		try {
			return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断方法是否是抽象方法
	 * 
	 * @param method
	 * @return
	 */
	public static boolean isAbstract(Method method) {
		int mod = method.getModifiers();
		return Modifier.isAbstract(mod);
	}

	/**
	 * 根据SQL_URL读取SQL文件内容
	 * @param sqlurl
	 * @return
	 */
	public static String getMethodSqlLogicJar(String sqlurl) {
		StringBuffer sb = new StringBuffer();
		// 返回读取指定资源的输入流
		InputStream is = MiniDaoUtil.class.getResourceAsStream(sqlurl);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String s = "";
		try {
			while ((s = br.readLine()) != null)
				sb.append(s + " ");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	
	/**
	 * 返回首字母变为小写的字符串
	 * @param name
	 * @return
	 */
	public static String getFirstSmall(String name){
		name = name.trim();
		if(name.length()>=2){
			return name.substring(0, 1).toLowerCase()+name.substring(1);
		}else{
			return name.toLowerCase();
		}
		
	}

	/**
	 * Map转为实体Bean
	 * @param bean
	 * @param properties
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
    public static void populate(Object bean, Map properties)
    		throws IllegalAccessException, InvocationTargetException {
        // Do nothing unless both arguments have been specified
        if ((bean == null) || (properties == null)) {
            return;
        }
        // Loop through the property name/value pairs to be set
        Iterator names = properties.keySet().iterator();
        while (names.hasNext()) {

            // Identify the property name and value(s) to be assigned
            String name = (String) names.next();
            if (name == null) {
                continue;
            }
            Object value = properties.get(name);
            String camelName = CamelCaseUtils.toCamelCase(name);

            // Perform the assignment for this property
            BeanUtils.setProperty(bean, camelName, value);
        }
    }
		
}
