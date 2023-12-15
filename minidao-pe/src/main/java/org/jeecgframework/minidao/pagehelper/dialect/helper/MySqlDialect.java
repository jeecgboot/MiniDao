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
        String[] sqlParam = super.getPageParam(miniDaoPage);
        //update-begin--Author:wangshuai--Date:20211201--for:判断mysql是否包含limit,包含用select包裹起来，不包含直接拼接
        if(sql.toUpperCase().contains("LIMIT")){
            sqlBuilder.append("SELECT * FROM ( ");
            sqlBuilder.append(sql);
            sqlBuilder.append(" ) TMP_PAGE ");
            if (Integer.valueOf(sqlParam[0])  == 0) {
                sqlBuilder.append("\n LIMIT {1}");
            } else {
                sqlBuilder.append("\n LIMIT {0}, {1} ");
            }
        }else{
            sqlBuilder.append(sql);
            if (Integer.valueOf(sqlParam[0])  == 0) {
                sqlBuilder.append("\n LIMIT {1} ");
            } else {
                sqlBuilder.append("\n LIMIT {0}, {1} ");
            }
        }
        //update-end--Author:wangshuai--Date:20211201--for:判断mysql是否包含limit,包含用select包裹起来，不包含直接拼接
        String newSql = sqlBuilder.toString();
        newSql = super.format(newSql,sqlParam);
        return newSql;
    }

}
