package org.jeecgframework.minidao.pagehelper.dialect.helper;

import org.jeecgframework.minidao.pagehelper.dialect.AbstractHelperDialect;
import org.jeecgframework.minidao.pojo.MiniDaoPage;

import java.text.MessageFormat;

/**
 * MySql
 */
public class MySqlDialect extends AbstractHelperDialect {

    @Override
    public String getPageSql(String sql, MiniDaoPage miniDaoPage) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 14);
        sqlBuilder.append(sql);
        String[] sqlParam = super.getPageParam(miniDaoPage);
        if (Integer.valueOf(sqlParam[0])  == 0) {
            sqlBuilder.append("\n LIMIT {1} ");
        } else {
            sqlBuilder.append("\n LIMIT {0}, {1} ");
        }
        String newSql = sqlBuilder.toString();
        newSql = super.format(newSql,sqlParam);
        return newSql;
    }

}
