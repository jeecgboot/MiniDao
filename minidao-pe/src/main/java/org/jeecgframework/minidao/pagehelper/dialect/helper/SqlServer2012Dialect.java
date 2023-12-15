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
        //update-begin--Author:wangshuai--Date:20211201--for:判断sqlserver是否包含offset或next,包含用select包裹起来，不包含直接拼接
        if(sql.toUpperCase().contains("OFFSET") && sql.toUpperCase().contains("NEXT")){
            sqlBuilder.append("SELECT * FROM (");
            sqlBuilder.append(sql);
            sqlBuilder.append(") TMP_PAGE ");
            sqlBuilder.append("\n OFFSET {0} ROWS FETCH NEXT {1} ROWS ONLY ");
        }else{
            sqlBuilder.append(sql);
            sqlBuilder.append("\n OFFSET {0} ROWS FETCH NEXT {1} ROWS ONLY ");  
        }
        //update-end--Author:wangshuai--Date:20211201--for:判断sqlserver是否包含offset或next,包含用select包裹起来，不包含直接拼接
        String newSql = sqlBuilder.toString();
        newSql = super.format(newSql, super.getPageParam(miniDaoPage));
        return newSql;
    }

}
