package org.jeecgframework.minidao.sqlparser.impl;

import org.jeecgframework.minidao.pojo.MiniDaoPage;
import org.jeecgframework.minidao.sqlparser.AbstractSqlProcessor;
import org.jeecgframework.minidao.sqlparser.impl.vo.QueryTable;
import org.jeecgframework.minidao.sqlparser.impl.vo.SelectSqlInfo;
import org.jeecgframework.minidao.util.MiniDaoUtil;
import org.jeecgframework.minidao.sqlparser.impl.util.SimpleAddWhereHelper;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 简单SQL解析器（采用正则）
 *
 * @author zhang
 */
public class SimpleSqlProcessor implements AbstractSqlProcessor {
    private static final String SQLSERVER_SQL = "select * from ( select row_number() over(order by tempColumn) tempRowNumber, * from (select top {1} tempColumn = 0, {0}) t ) tt where tempRowNumber > {2}"; // sqlserver

    @Override
    public String getSqlServerPageSql(String sql, MiniDaoPage miniDaoPage) {
        int page = miniDaoPage.getPage();
        int rows = miniDaoPage.getRows();
        String[] sqlParam = new String[3];
        // 去掉排序，兼容SqlServer
        sql = MiniDaoUtil.removeOrderBy(sql);

        int beginIndex = (page - 1) * rows;
        int endIndex = beginIndex + rows;
        sqlParam[2] = Integer.toString(beginIndex);
        sqlParam[1] = Integer.toString(endIndex);
        sqlParam[0] = sql.substring(getAfterSelectInsertPoint(sql));
        sql = MessageFormat.format(SQLSERVER_SQL, sqlParam);
        return sql;
    }

    @Override
    public String getCountSql(String sql) {
        // 去掉排序，兼容SqlServer
        sql = MiniDaoUtil.removeOrderBy(sql);
        return "select count(0) from (" + sql + ") tmp_count";
    }

    /**
     * 去除排序SQL片段
     *
     * @param sql
     * @return
     */
    @Override
    public String removeOrderBy(String sql) {
        if (sql == null) {
            return null;
        }
        sql = sql.replaceAll("(?i)\\s+ORDER\\s+BY\\s+[\\w\\s,.]+", "");
        return sql;
    }


    /**
     * 解析SQL查询字段
     *
     * @param parsedSql
     * @return
     */
    @Override
    public List<Map<String, Object>> parseSqlFields(String parsedSql) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<String> fields = new ArrayList<>();

        // 匹配SELECT关键字后的第一个左括号到第一个FROM关键字之间的内容
        Pattern pattern = Pattern.compile("SELECT\\s+(.*?)\\s+FROM", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(parsedSql);

        if (matcher.find()) {
            String selectFields = matcher.group(1);
            String[] fieldArray = selectFields.split(",");

            for (String field : fieldArray) {
                fields.add(field.trim());
            }
        }

        return list;
    }


    /**
     * 排序方向
     *
     * @for TV360X-2551
     */
    private static final List<String> ORDER_DIRECTION = Arrays.asList("ASC", "DESC");

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
    @Override
    public String addOrderBy(String sql, String field, boolean isAsc) {
        // 解析SQL以找到最外层的ORDER BY
        int orderByIndex = findOuterOrderBy(sql);
        field = field.trim().toLowerCase();  // 处理新字段

        String orderField = " " + field + " " + (isAsc ? "ASC" : "DESC") + " ";

        if (orderByIndex != -1) {
            // 找到最外层的ORDER BY
            String existingOrderByClause = sql.substring(orderByIndex + 8).trim().toLowerCase(); // 获取ORDER BY之后的字段

            // 检查新字段是否已经存在
            String replaceReg = "\\s*[,;\\s]\\s*";
            String[] existingOrderByFields = existingOrderByClause.split(replaceReg);
            for (String existingOrderByField : existingOrderByFields) {
                if (null != existingOrderByField
                        && !ORDER_DIRECTION.contains(existingOrderByField.trim())) {
                    if(existingOrderByField.equalsIgnoreCase(field)){
                        // 新字段已存在，直接返回原SQL
                        return sql;
                    }
                }
            }

            // 新字段插入现有ORDER BY之前
            return sql.substring(0, orderByIndex + 8) + " " + orderField + ", " + sql.substring(orderByIndex + 8);
        } else {
            // 如果没有ORDER BY，则在SQL的最后插入ORDER BY
            if(sql.trim().endsWith(";")){
                sql = sql.substring(0, sql.lastIndexOf(";"));
            }
            return sql.trim() + " ORDER BY " + orderField;
        }
    }


    /**
     * 查找最外层的ORDER BY的索引
     *
     * @param sql 要解析的SQL字符串
     * @return 最外层ORDER BY的索引，如果没有则返回-1
     */
    private static int findOuterOrderBy(String sql) {
        int nestedLevel = 0;  // 记录括号的嵌套层次
        int orderByIndex = -1;
        String lowerSql = sql.toLowerCase();  // 转换为小写以便不区分大小写

        // 遍历SQL字符串，处理括号嵌套情况
        for (int i = 0; i < lowerSql.length(); i++) {
            char c = lowerSql.charAt(i);
            if (c == '(') {
                nestedLevel++;  // 进入嵌套
            } else if (c == ')') {
                nestedLevel--;  // 退出嵌套
            } else if (nestedLevel == 0 && lowerSql.startsWith("order by", i)) {
                // 只有在最外层时匹配到ORDER BY
                orderByIndex = i;
                break;
            }
        }
        return orderByIndex;
    }


    /**
     * @param sql
     * @return
     */
    private static int getAfterSelectInsertPoint(String sql) {
        int selectIndex = sql.toLowerCase().indexOf("select");
        int selectDistinctIndex = sql.toLowerCase().indexOf("select distinct");
        return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
    }

    /**
     * 注：使用正则方式解析具有缺陷，并不能保证能解析出所有表名，并且 SelectSqlInfo 中只有表名信息，没有其他字段的信息
     * @param selectSql 待解析的SQL
     * @return
     */
    @Override
    public Map<String, SelectSqlInfo> parseAllSelectTable(String selectSql) {
        Map<String, SelectSqlInfo> tableMap = new HashMap<>();
        String regex = "(?i)\\b(from|join)\\s+([\\w.]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(selectSql);

        while (matcher.find()) {
            String tableName = matcher.group(2);
            SelectSqlInfo selectSqlInfo = new SelectSqlInfo(selectSql);
            selectSqlInfo.setFromTableName(tableName);
            tableMap.put(tableName, selectSqlInfo);
        }
        return tableMap;
    }

    /**
     * 注：使用正则方式解析具有缺陷， SelectSqlInfo 中只有表名信息，没有其他字段的信息
     * @param selectSql 待解析的SQL
     * @return
     */
    @Override
    public SelectSqlInfo parseSelectSqlInfo(String selectSql) {
        System.err.println("此方法未实现！！！");
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<QueryTable> getQueryTableInfo(String sql) {
        System.err.println("此方法未实现！！！");
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * 为SQL语句增加查询条件（直接使用条件语句）
     * for [issues/8336]支持SqlServer数据使用sql排序，新方案。
     * @param sql       原始SQL
     * @param condition 查询条件（不含where关键字）
     * @return 添加查询条件后的SQL
     */
    @Override
    public String addWhereCondition(String sql, String condition) {
        return SimpleAddWhereHelper.addWhereCondition(sql, condition);
    }

    /**
     * 为SQL语句增加查询条件（使用字段、值和操作符）
     * for [issues/8336]支持SqlServer数据使用sql排序，新方案。
     * @param sql      原始SQL
     * @param field    字段名
     * @param value    字段值
     * @param operator 比较操作符（如：=, >, <, !=, like等）
     * @return 添加查询条件后的SQL
     */
    @Override
    public String addWhereCondition(String sql, String field, Object value, String operator) {
        return SimpleAddWhereHelper.addWhereCondition(sql, field, value, operator);
    }

}
