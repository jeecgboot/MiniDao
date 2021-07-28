package org.jeecgframework.minidao.pagehelper.dialect.helper;

import org.jeecgframework.minidao.pagehelper.dialect.AbstractHelperDialect;
import org.jeecgframework.minidao.pojo.MiniDaoPage;

import java.text.MessageFormat;

/**
 * Db2数据库
 */
public class Db2Dialect extends AbstractHelperDialect {

    @Override
    public String getPageSql(String sql, MiniDaoPage miniDaoPage) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 140);
        sqlBuilder.append("SELECT * FROM (SELECT TMP_PAGE.*,ROWNUMBER() OVER() AS PAGEHELPER_ROW_ID FROM ( \n");
        sqlBuilder.append(sql);
        sqlBuilder.append("\n ) AS TMP_PAGE) TMP_PAGE WHERE PAGEHELPER_ROW_ID BETWEEN {0} AND {2}");
        String newSql = sqlBuilder.toString();
        newSql = super.format(newSql, super.getPageParam(miniDaoPage));
        return newSql;
    }

}
