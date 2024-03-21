package org.jeecgframework.minidao.util;

import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.WithItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Description: 反射属性
 * @author: lsq
 * @date: 2021年07月26日 19:49
 */
public class ReflectUtil {
    private static final Log logger = LogFactory.getLog(ReflectUtil.class);
    
    /**
     * 设置bean 属性值
     *
     * @param map
     * @param bean
     * @throws Exception
     */
    public static void setIdFieldValue(Map<Object, Object> map, Object bean) throws Exception {
        Class<?> cls = bean.getClass();
        Method methods[] = cls.getDeclaredMethods();
        Field fields[] = cls.getDeclaredFields();

        for (Field field : fields) {
            String fldtype = field.getType().getSimpleName();
            String fldSetName = field.getName();
            String setMethod = pareSetName(fldSetName);
            if (!checkMethod(methods, setMethod)) {
                continue;
            }
            if (!map.containsKey(fldSetName)) {
                continue;
            }

            Object value = map.get(fldSetName);
            Method method = cls.getMethod(setMethod, field.getType());
            if (null != value) {
                if ("String".equals(fldtype)) {

                    //如果ID自定义值，则不自动生成
                    field.setAccessible(true);
                    Object idValue = field.get(bean);
                    if(idValue!=null && !idValue.toString().trim().equals("")){
                        break;
                    }

                    method.invoke(bean, String.valueOf(value));
                } else if ("Double".equals(fldtype)) {
                    method.invoke(bean, (Double) value);
                } else if ("int".equals(fldtype)) {
                    int val = Integer.valueOf((String) value);
                    method.invoke(bean, val);
                } else {
                    method.invoke(bean, value);
                }
            }

        }
    }
    
    /**
     * 设置bean 属性值
     *
     * @param map
     * @param bean
     * @throws Exception
     */
    public static void setFieldValue(Map<Object, Object> map, Object bean) throws Exception {
        Class<?> cls = bean.getClass();
        Method methods[] = cls.getDeclaredMethods();
        Field fields[] = cls.getDeclaredFields();

        for (Field field : fields) {
            String fldtype = field.getType().getSimpleName();
            String fldSetName = field.getName();
            String setMethod = pareSetName(fldSetName);
            if (!checkMethod(methods, setMethod)) {
                continue;
            }
            if (!map.containsKey(fldSetName)) {
                continue;
            }
            Object value = map.get(fldSetName);
            Method method = cls.getMethod(setMethod, field.getType());
            if (null != value) {
                if ("String".equals(fldtype)) {
                    method.invoke(bean, String.valueOf(value));
                } else if ("Double".equals(fldtype)) {
                    method.invoke(bean, (Double) value);
                } else if ("int".equals(fldtype)) {
                    int val = Integer.valueOf((String) value);
                    method.invoke(bean, val);
                } else {
                    method.invoke(bean, value);
                }
            }

        }
    }

    /**
     * 拼接某属性set 方法
     *
     * @param fldname
     * @return
     */
    public static String pareSetName(String fldname) {
        if (null == fldname || "".equals(fldname)) {
            return null;
        }
        String pro = "set" + fldname.substring(0, 1).toUpperCase() + fldname.substring(1);
        return pro;
    }

    /**
     * 判断该方法是否存在
     *
     * @param methods
     * @param met
     * @return
     */
    public static boolean checkMethod(Method methods[], String met) {
        if (null != methods) {
            for (Method method : methods) {
                if (met.equals(method.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取子的SelectBody
     *
     * @param withItem
     * @return
     */
    public static SelectBody getItemSelectBody(WithItem withItem) {
        long startTime = System.nanoTime();
        Class<?> clazz = withItem.getClass();
        try {
            Method method = clazz.getMethod("getSelectBody");
            SelectBody result = (SelectBody) method.invoke(withItem);
            logger.debug("jsqlparser 4.0 写法： " + method);
            return result;
        } catch (Exception e) {
        }

        try {
            Method method = clazz.getMethod("getSubSelect");
            Object subresult = method.invoke(withItem);
            Method submethod = subresult.getClass().getMethod("getSelectBody");
            SelectBody result = (SelectBody) submethod.invoke(subresult);
            logger.debug("jsqlparser 4.4 写法： " + method + "." + submethod);
            return result;
        } catch (Exception e) {
        }

        logger.debug("Method execution time: " + (System.nanoTime() - startTime));
        return null;
    }
}
