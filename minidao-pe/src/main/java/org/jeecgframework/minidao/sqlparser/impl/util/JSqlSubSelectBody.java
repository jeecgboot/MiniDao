package org.jeecgframework.minidao.sqlparser.impl.util;

import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.select.SelectBody;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;

/**
 * @author: scott
 * @date: 2024年07月03日 下午3:14
 */
public class JSqlSubSelectBody {
    private static final Log logger = LogFactory.getLog(JSqlSubSelectBody.class);


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
