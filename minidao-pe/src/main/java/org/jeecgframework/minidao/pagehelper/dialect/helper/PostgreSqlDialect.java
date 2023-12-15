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
        //update-begin--Author:wangshuai--Date:20211201--for:判断postgresql是否包含limit或OFFSET,包含用select包裹起来，不包含直接拼接
        String[] sqlParam = super.getPageParam(miniDaoPage);
        if(sql.toUpperCase().contains("LIMIT") || sql.toUpperCase().contains("OFFSET")){
            sqlStr.append("SELECT * FROM (");
            sqlStr.append(sql);
            sqlStr.append(") TMP_PAGE ");
            if (Integer.valueOf(sqlParam[0])  == 0) {
                sqlStr.append(" LIMIT {1} ");
            }else{
                sqlStr.append(" OFFSET {0} LIMIT {1}");
            }
        }else{
            sqlStr.append(sql);
            if (Integer.valueOf(sqlParam[0])  == 0) {
                sqlStr.append(" LIMIT {1}");
            } else {
                sqlStr.append(" OFFSET {0} LIMIT {1}");
            }
        }
        //update-end--Author:wangshuai--Date:20211201--for:判断postgresql是否包含limit或OFFSET,包含用select包裹起来，不包含直接拼接
        String newSql = sqlStr.toString();
        newSql = super.format(newSql, super.getPageParam(miniDaoPage));
        return newSql;
    }

}
