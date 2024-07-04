package org.jeecgframework.minidao.sqlparser.impl;

import org.jeecgframework.minidao.pojo.MiniDaoPage;
import org.jeecgframework.minidao.sqlparser.AbstractSqlProcessor;
import org.jeecgframework.minidao.util.MiniDaoUtil;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
     * @param sql
     * @return
     */
    private static int getAfterSelectInsertPoint(String sql) {
        int selectIndex = sql.toLowerCase().indexOf("select");
        int selectDistinctIndex = sql.toLowerCase().indexOf("select distinct");
        return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
    }

}
