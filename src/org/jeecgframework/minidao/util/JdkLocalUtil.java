package org.jeecgframework.minidao.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import examples.entity.Employee;

/**
 * 
 * @Title:JdkLocalUtil
 * @description:JdkLocalUtil
 * @author 张代浩
 * @date Jul 5, 2013 2:58:29 PM
 * @version V1.0
 */
public class JdkLocalUtil{
	 public static void main(String[] args) throws Exception {
	        System.out.println(isWrapClass(Long.class));
	        System.out.println(isWrapClass(Integer.class));
	        System.out.println(isWrapClass(String.class)); 
	        System.out.println(isWrapClass(Employee.class));
	    } 

	    /**
	     * 判断Class是否是基本包装类型
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
	     * @param method
	     * @return
	     */
	    public static boolean isAbstract(Method method) {
	        int mod = method.getModifiers();
	        return Modifier.isAbstract(mod);
	    }
	} 
