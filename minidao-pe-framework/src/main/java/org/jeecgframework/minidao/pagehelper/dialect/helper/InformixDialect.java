package org.jeecgframework.minidao.pagehelper.dialect.helper;

import org.jeecgframework.minidao.pagehelper.dialect.AbstractHelperDialect;
import org.jeecgframework.minidao.pojo.MiniDaoPage;

import java.text.MessageFormat;

/**
 * Informix
 */
public class InformixDialect extends AbstractHelperDialect {

    @Override
    public String getPageSql(String sql, MiniDaoPage miniDaoPage) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 40);
        sqlBuilder.append("SELECT ");
        String[] sqlParam = super.getPageParam(miniDaoPage);
        if (Integer.valueOf(sqlParam[0]) > 0) {
            sqlBuilder.append(" SKIP {0} ");
        }
        if (Integer.valueOf(sqlParam[1]) > 0) {
            sqlBuilder.append(" FIRST {1} ");
        }
        sqlBuilder.append(" * FROM ( \n");
        sqlBuilder.append(sql);
        sqlBuilder.append("\n ) TEMP_T ");
        String newSql = sqlBuilder.toString();
        newSql = super.format(newSql, sqlParam);
        return newSql;
    }

}
