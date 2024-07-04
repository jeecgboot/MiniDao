package org.jeecgframework.minidao.sqlparser;

import org.jeecgframework.minidao.pojo.MiniDaoPage;

import java.util.List;
import java.util.Map;

/**
 * SQL解析处理器（普通正则引擎 和 JSqlParser解析引擎）
 *
 * @author zhang
 */
public interface AbstractSqlProcessor {

    /**
     * 获取SqlServer分页SQL
     *
     * @param sql
     * @return
     */
    String getSqlServerPageSql(String sql, MiniDaoPage miniDaoPage);

    /**
     * 获取SQL查询记录数SQL
     *
     * @param sql
     * @return
     */
    String getCountSql(String sql);

    /**
     * 去除排序SQL片段
     *
     * @param sql
     * @return
     */
    String removeOrderBy(String sql);


    /**
     * 解析SQL查询字段
     *
     * @param parsedSql
     * @return
     */
    List<Map<String, Object>> parseSqlFields(String parsedSql);
}
