package org.jeecgframework.minidao.pagehelper.dialect.helper;

import org.jeecgframework.minidao.sqlparser.AbstractSqlProcessor;
import org.jeecgframework.minidao.pagehelper.dialect.AbstractHelperDialect;
import org.jeecgframework.minidao.pojo.MiniDaoPage;
import org.jeecgframework.minidao.sqlparser.impl.JsqlparserSqlProcessor;
import org.jeecgframework.minidao.sqlparser.impl.SimpleSqlProcessor;
import org.jeecgframework.minidao.util.MiniDaoUtil;

/**
 * SqlServer
 */
public class SqlServerDialect extends AbstractHelperDialect {

    //update-begin---author:scott ---date:2024-07-04  for：SQL解析引擎改造支持普通和jsqlparser切换----
    protected static AbstractSqlProcessor abstractSqlProcessor;

    static {
        if (MiniDaoUtil.isJSqlParserAvailable()) {
            abstractSqlProcessor = new JsqlparserSqlProcessor();
        } else {
            abstractSqlProcessor = new SimpleSqlProcessor();
        }
    }
    //update-end---author:scott ---date:2024-07-04  for：SQL解析引擎改造支持普通和jsqlparser切换----

    @Override
    public String getPageSql(String sql, MiniDaoPage miniDaoPage) {
        String pageSql = null;
        try {
            pageSql = abstractSqlProcessor.getSqlServerPageSql(sql, miniDaoPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageSql;
    }

}
