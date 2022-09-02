package org.jeecgframework.minidao.pagehelper.dialect.helper;

import org.jeecgframework.minidao.pagehelper.dialect.AbstractHelperDialect;
import org.jeecgframework.minidao.pojo.MiniDaoPage;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Hsqldb
 */
public class HsqldbDialect extends AbstractHelperDialect {

    @Override
    public String getPageSql(String sql, MiniDaoPage miniDaoPage) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 20);
        //update-begin--Author:wangshuai--Date:20211201--for:判断Hsql是否包含limit或offset,包含用select包裹起来，不包含直接拼接
        if(sql.toUpperCase().contains("LIMIT") || sql.toUpperCase().contains("OFFSET")){
            String[] sqlParam = this.getPageParam(miniDaoPage);
            sqlBuilder.append("SELECT * FROM (");
            sqlBuilder.append(sql);
            sqlBuilder.append(") TMP_PAGE ");
            if (Integer.valueOf(sqlParam[1]) > 0) {
                sqlBuilder.append("\n LIMIT {1} ");
            }
            if (Integer.valueOf(sqlParam[0]) > 0) {
                sqlBuilder.append("\n OFFSET {0} ");
            }
        }else{
            sqlBuilder.append(sql);
            String[] sqlParam = this.getPageParam(miniDaoPage);
            if (Integer.valueOf(sqlParam[1]) > 0) {
                sqlBuilder.append("\n LIMIT {1} ");
            }
            if (Integer.valueOf(sqlParam[0]) > 0) {
                sqlBuilder.append("\n OFFSET {0} ");
            } 
        }
        //update-begin--Author:wangshuai--Date:20211201--for:判断mysql是否包含limit,包含用select包裹起来，不包含直接拼接
        String newSql = sqlBuilder.toString();
        newSql = super.format(newSql, super.getPageParam(miniDaoPage));
        return newSql;
    }
}
