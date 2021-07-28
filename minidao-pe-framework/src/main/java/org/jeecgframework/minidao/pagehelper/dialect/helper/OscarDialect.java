package org.jeecgframework.minidao.pagehelper.dialect.helper;

import org.jeecgframework.minidao.pagehelper.dialect.AbstractHelperDialect;
import org.jeecgframework.minidao.pojo.MiniDaoPage;

import java.text.MessageFormat;

/**
 * 神通数据库
 */
public class OscarDialect extends AbstractHelperDialect {

    @Override
    public String getPageSql(String sql, MiniDaoPage miniDaoPage) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 14);
        sqlBuilder.append(sql);
        String[] sqlParam = this.getPageParam(miniDaoPage);
        if (Integer.valueOf(sqlParam[0])  == 0) {
            sqlBuilder.append("\n LIMIT {1} ");
        } else {
            sqlBuilder.append("\n LIMIT {1} OFFSET {0} ");
        }
        String newSql = sqlBuilder.toString();
        newSql = super.format(newSql, super.getPageParam(miniDaoPage));
        return newSql;
    }
}
