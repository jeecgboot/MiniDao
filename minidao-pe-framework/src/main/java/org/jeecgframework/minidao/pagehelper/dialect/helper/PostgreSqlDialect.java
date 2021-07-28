package org.jeecgframework.minidao.pagehelper.dialect.helper;

import org.jeecgframework.minidao.pojo.MiniDaoPage;

import java.text.MessageFormat;

/**
 * PostgreSQL 方言.
 *
 */
public class PostgreSqlDialect extends MySqlDialect {

    /**
     * 构建 <a href="https://www.postgresql.org/docs/current/queries-limit.html">PostgreSQL</a>分页查询语句
     */
    @Override
    public String getPageSql(String sql, MiniDaoPage miniDaoPage) {
        StringBuilder sqlStr = new StringBuilder(sql.length() + 17);
        sqlStr.append(sql);
        String[] sqlParam = super.getPageParam(miniDaoPage);
        if (Integer.valueOf(sqlParam[0])  == 0) {
            sqlStr.append(" LIMIT {1}");
        } else {
            sqlStr.append(" OFFSET {0} LIMIT {1}");
        }
        String newSql = sqlStr.toString();
        newSql = super.format(newSql, super.getPageParam(miniDaoPage));
        return newSql;
    }

}
