package org.jeecgframework.minidao.pagehelper.dialect.helper;

import org.jeecgframework.minidao.pagehelper.dialect.AbstractHelperDialect;
import org.jeecgframework.minidao.pagehelper.parser.SqlServerParser;
import org.jeecgframework.minidao.pojo.MiniDaoPage;

/**
 * SqlServer
 */
public class SqlServerDialect extends AbstractHelperDialect {
    protected static SqlServerParser pageSql = new SqlServerParser();

    @Override
    public String getPageSql(String sql, MiniDaoPage miniDaoPage) {
            int page = miniDaoPage.getPage();
            int rows = miniDaoPage.getRows();
            int beginNum = (page - 1) * rows;
            String cacheSql = sql;
            cacheSql = pageSql.convertToPageSql(cacheSql, null, null);
            cacheSql = cacheSql.replace(String.valueOf(Long.MIN_VALUE), String.valueOf(beginNum));
            cacheSql = cacheSql.replace(String.valueOf(Long.MAX_VALUE), String.valueOf(rows));
            return cacheSql;
    }
}
