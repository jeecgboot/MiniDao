package org.jeecgframework.minidao.pagehelper.dialect.nosql;

import org.jeecgframework.minidao.pagehelper.dialect.helper.MySqlDialect;
import org.jeecgframework.minidao.pojo.MiniDaoPage;

import java.text.MessageFormat;

/**
 *  HBase【大数据】
 */
public class HBaseDialect extends MySqlDialect {

    @Override
    public String getPageSql(String sql, MiniDaoPage miniDaoPage) {
        StringBuilder sqlStr = new StringBuilder(sql.length() + 17);
        sqlStr.append(sql);
        String[] sqlParam = super.getPageParam(miniDaoPage);
        sqlStr.append(" limit {1} offset {0}");
        String newSql = sqlStr.toString();
        newSql = super.format(newSql,sqlParam);
        return newSql;
    }
}
