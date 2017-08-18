package org.jeecgframework.minidao.util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
 
/**
 * <p>
 * <font color="red">依赖javassit</font>的工具类，获取方法的参数名
 * </p>
 * 
 * @author dixingxing
 * @date Apr 20, 2012
 */
public class Classes {
    private Classes() {}
 
    /**
     * 
     * <p>
     * 获取方法参数名称
     * </p>
     * 
     * @param cm
     * @return
     * @throws Exception 
     */
    protected static String[] getMethodParamNames(CtMethod cm){
        CtClass cc = cm.getDeclaringClass();
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = null;
        if(codeAttribute!=null){
            attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        }else{
        	return null;
        }
 
        String[] paramNames = null;
        try {
            paramNames = new String[cm.getParameterTypes().length];
        } catch (NotFoundException e) {
            //Exceptions.uncheck(e);
        }
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i] = attr.variableName(i + pos);
        }
        return paramNames;
    }
 
    /**
     * 获取方法参数名称，按给定的参数类型匹配方法
     * 
     * @param clazz
     * @param method
     * @param paramTypes
     * @return
     */
    public static String[] getMethodParamNames(Class<?> clazz, String method,
            Class<?>... paramTypes) {
 
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = null;
        CtMethod cm = null;
        try {
            cc = pool.get(clazz.getName());
 
            String[] paramTypeNames = new String[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++)
                paramTypeNames[i] = paramTypes[i].getName();
 
            cm = cc.getDeclaredMethod(method, pool.get(paramTypeNames));
        } catch (NotFoundException e) {
//            Exceptions.uncheck(e);
        }
        return getMethodParamNames(cm);
    }
 
    /**
     * 获取方法参数名称，匹配同名的某一个方法
     * 
     * @param clazz
     * @param method
     * @return
     * @throws NotFoundException
     *             如果类或者方法不存在
     * @throws MissingLVException
     *             如果最终编译的class文件不包含局部变量表信息
     */
    public static String[] getMethodParamNames(Class<?> clazz, String method) {
 
        ClassPool pool = ClassPool.getDefault();
        CtClass cc;
        CtMethod cm = null;
        try {
            cc = pool.get(clazz.getName());
            cm = cc.getDeclaredMethod(method);
        } catch (NotFoundException e) {
//            Exceptions.uncheck(e);
        }
        return getMethodParamNames(cm);
    }
 
    
    public static void main(String[] args) {
		try {
			String[] s = getMethodParamNames(Class.forName("org.jeecgframework.web.demo.service.test.TransactionTestServiceI"), "insertData");
			for(String c:s){
				System.out.println(c);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
 
}
