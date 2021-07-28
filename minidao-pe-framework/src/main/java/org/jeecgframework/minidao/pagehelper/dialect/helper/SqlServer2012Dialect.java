package org.jeecgframework.minidao.pagehelper.dialect.helper;

import org.jeecgframework.minidao.pojo.MiniDaoPage;

import java.text.MessageFormat;

/**
 *  SqlServer2012
 */
public class SqlServer2012Dialect extends SqlServerDialect {

    @Override
    public String getPageSql(String sql, MiniDaoPage miniDaoPage) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 64);
        sqlBuilder.append(sql);
        sqlBuilder.append("\n OFFSET {0} ROWS FETCH NEXT {1} ROWS ONLY ");
        String newSql = sqlBuilder.toString();
        newSql = super.format(newSql, super.getPageParam(miniDaoPage));
        return newSql;
    }

}
