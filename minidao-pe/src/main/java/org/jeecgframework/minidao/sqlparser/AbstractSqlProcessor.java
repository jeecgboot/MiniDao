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


    /**
     * 在SQL的最外层增加或修改ORDER BY子句
     * @for TV360X-2551
     * @param sql 原始SQL
     * @param field 新的ORDER BY字段
     * @param isAsc 是否正序
     * @return
     * @author chenrui
     * @date 2024/9/27 17:25
     */
    String addOrderBy(String sql, String field, boolean isAsc);
}
