package org.jeecgframework.minidao.pagehelper.dialect.nosql;

import org.jeecgframework.minidao.pagehelper.dialect.helper.MySqlDialect;
import org.jeecgframework.minidao.pojo.MiniDaoPage;

import java.text.MessageFormat;

/**
 *  CouchBase
 */
public class CouchBaseDialect extends MySqlDialect {

    @Override
    public String getPageSql(String sql, MiniDaoPage miniDaoPage) {
        StringBuilder sqlStr = new StringBuilder(sql.length() + 17);
        sqlStr.append(sql);
        String[] sqlParam = super.getPageParam(miniDaoPage);
        sqlStr.append(" OFFSET {0} LIMIT {1}");
        String newSql = sqlStr.toString();
        newSql = MessageFormat.format(newSql, sqlParam);
        return newSql;
    }
}
