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
        //update-begin--Author:wangshuai--Date:20211201--for:判断oscar是否包含limit或OFFSET,包含用select包裹起来，不包含直接拼接
        String[] sqlParam = this.getPageParam(miniDaoPage);
        if(sql.toUpperCase().contains("LIMIT") || sql.toUpperCase().contains("OFFSET")){
            sqlBuilder.append("SELECT * FROM (");
            sqlBuilder.append(sql);
            sqlBuilder.append(") TMP_PAGE ");
            if (Integer.valueOf(sqlParam[0])  == 0) {
                sqlBuilder.append("\n LIMIT {1} ");
            }else{
                sqlBuilder.append("\n LIMIT {1} OFFSET {0} ");
            }
        }else{
            sqlBuilder.append(sql);
            if (Integer.valueOf(sqlParam[0])  == 0) {
                sqlBuilder.append("\n LIMIT {1} ");
            } else {
                sqlBuilder.append("\n LIMIT {1} OFFSET {0} ");
            } 
        }
        String newSql = sqlBuilder.toString();
        newSql = super.format(newSql, super.getPageParam(miniDaoPage));
        return newSql;
    }
}
