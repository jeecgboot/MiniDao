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

        /**
         * 在SQL Server中，使用OFFSET关键字来实现分页查询时，通常需要搭配ORDER BY子句来指定排序规则，因为OFFSET必须和ORDER BY一起使用。
         * 如果不需要排序，可以使用ORDER BY (SELECT NULL)来实现类似无排序的效果。
         */
        sqlBuilder.append("SELECT * FROM (");
        sqlBuilder.append(sql);
        sqlBuilder.append(") TMP_PAGE ");
        sqlBuilder.append(" ORDER BY (SELECT NULL) ");
        sqlBuilder.append("\n OFFSET {0} ROWS FETCH NEXT {1} ROWS ONLY ");
        String newSql = sqlBuilder.toString();
        newSql = super.format(newSql, super.getPageParam(miniDaoPage));
        return newSql;
    }

}
