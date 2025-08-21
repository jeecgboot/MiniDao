package org.jeecgframework.minidao.sqlparser;

import org.jeecgframework.minidao.pojo.MiniDaoPage;
import org.jeecgframework.minidao.sqlparser.impl.vo.QueryTable;
import org.jeecgframework.minidao.sqlparser.impl.vo.SelectSqlInfo;

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

    /**
     * 为SQL语句增加查询条件（直接使用条件语句）
     * for [issues/8336]支持SqlServer数据使用sql排序，新方案。
     * @param sql 原始SQL
     * @param condition 查询条件（不含where关键字）
     * @return 添加查询条件后的SQL
     */
    String addWhereCondition(String sql, String condition);

    /**
     * 为SQL语句增加查询条件（使用字段、值和操作符）
     * for [issues/8336]支持SqlServer数据使用sql排序，新方案。
     * @param sql 原始SQL
     * @param field 字段名
     * @param value 字段值
     * @param operator 比较操作符（如：=, >, <, !=, like等）
     * @return 添加查询条件后的SQL
     */
    String addWhereCondition(String sql, String field, Object value, String operator);

    /**
     * 解析 查询（select）sql的信息，
     * 此方法会展开所有子查询到一个map里，
     * key只存真实的表名，如果查询的没有真实的表名，则会被忽略。
     * value只存真实的字段名，如果查询的没有真实的字段名，则会被忽略。
     * <p>
     * 例如：SELECT a.*,d.age,(SELECT count(1) FROM sys_depart) AS count FROM (SELECT username AS foo, realname FROM sys_user) a, demo d
     * 解析后的结果为：{sys_user=[username, realname], demo=[age], sys_depart=[]}
     *
     * @param selectSql 待解析的SQL
     */
    Map<String, SelectSqlInfo> parseAllSelectTable(String selectSql) throws Exception;

    /**
     * 解析 查询（select）sql的信息，子查询嵌套
     *
     * @param selectSql 待解析的SQL
     */
    SelectSqlInfo parseSelectSqlInfo(String selectSql) throws Exception;

    /**
     * 根据 sql语句 获取表和字段信息
     *
     * @param sql sql语句
     */
    List<QueryTable> getQueryTableInfo(String sql);

    /**
     * 解析 SQL 查询，将 SELECT 字段映射到 (alias或列名) -> (完整字段表达式) 的 Map 中。
     * for [QQYUN-13476]online 报表SqlServer兼容改造完
     *
     * @param sql 要解析的 SQL 查询语句
     * @return 字段映射：key 为别名（若有）或列名（无表名前缀），value 为对应的完整表达式字符串
     */
    Map<String, String> parseSelectAliasMap(String sql);

}
