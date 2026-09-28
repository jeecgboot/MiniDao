package org.jeecgframework.minidao.sqlparser.impl.util.v49;

import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.WithItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author: scott
 * @date: 2024年07月03日 下午3:14
 */
public class JSqlSubSelectBody49 {
    private static final Log logger = LogFactory.getLog(JSqlSubSelectBody49.class);


    /**
     * 获取子的SelectBody
     * <p>
     * 兼容 jsqlparser 4.9/5.0 与 5.1+ 两个版本系列：
     * - 4.9/5.0：WithItem 继承 ParenthesedSelect，getSelect() 返回内部的 Select（实际可能再包一层 ParenthesedSelect，需要解开）
     * - 5.1+：WithItem 不再继承 ParenthesedSelect，getSelect() 签名变化（返回 ParenthesedSelect），
     *   编译期为 Select 的方法已不存在，需通过 getParenthesedStatement() 反射获取内部 Select
     *
     * @param withItem
     * @return
     */
    public static Select getItemSelectBody(WithItem withItem) {
        if (withItem == null) {
            return null;
        }
        try {
            //update-begin---author:scott---date:2026-09-23---for:【issues/4798】CTE表达式在分页查询模式下报错 java.lang.NoSuchMethodError----
            // jsqlparser 5.1+ 的 WithItem 不继承 ParenthesedSelect，改从 getParenthesedStatement() 里取内部的 Select
            if (!(withItem instanceof ParenthesedSelect)) {
                Object parenthesedStatement = withItem.getClass().getMethod("getParenthesedStatement").invoke(withItem);
                if (parenthesedStatement instanceof ParenthesedSelect) {
                    return ((ParenthesedSelect) parenthesedStatement).getSelect();
                }
                logger.warn("无法识别的WithItem结构: " + (parenthesedStatement == null ? "null" : parenthesedStatement.getClass().getName()));
                return null;
            }
            //update-end---author:scott---date:2026-09-23---for:【issues/4798】CTE表达式在分页查询模式下报错 java.lang.NoSuchMethodError----
            // jsqlparser 4.9/5.0：getSelect() 返回的 Select 可能再包一层 ParenthesedSelect，解开取出真正的 selectBody
            Select select = withItem.getSelect();
            while (select instanceof ParenthesedSelect) {
                select = ((ParenthesedSelect) select).getSelect();
            }
            return select;
        } catch (Exception e) {
            logger.error("获取WithItem内部的Select失败", e);
            return null;
        }
    }

}
