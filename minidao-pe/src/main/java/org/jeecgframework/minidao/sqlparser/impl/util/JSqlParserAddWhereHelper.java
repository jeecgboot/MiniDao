package org.jeecgframework.minidao.sqlparser.impl.util;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * addWhere 工具类（jsqlparser 4.6）
 * for [issues/8336]支持SqlServer数据使用sql排序，新方案。
 * @author chenrui
 * @date 2025/8/14 19:02
 */
public class JSqlParserAddWhereHelper {

    /**
     * 为 SQL 增加 where 条件（条件字符串）
     *
     * @param sql       原始SQL
     * @param condition 条件（不含where关键字）
     * @author chenrui
     * @date 2025/8/14 19:03
     */
    public static String addWhereCondition(String sql, String condition) {
        // 参数校验：空值直接返回
        if (sql == null || condition == null || condition.trim().isEmpty()) {
            return sql;
        }
        // 掩码占位符，避免解析时丢失 # 后内容
        Map<String, String> mbMap = new LinkedHashMap<>();
        String maskedCondition = SqlParserUtils.maskMyBatisPlaceholders(condition, mbMap);
        //---------------------------------------------------------------------------------------------
        // 如果包含mybatis变量，先将其替换为占位符，避免解析时出错
        Map<String, String> sqlMbMap = new LinkedHashMap<>();
        sql = SqlParserUtils.maskMyBatisPlaceholders(sql, sqlMbMap, "_SQL_");
        //---------------------------------------------------------------------------------------------
        try {
            // 解析 SQL 为抽象语法树（支持方括号转义）
            Statement statement = CCJSqlParserUtil.parse(sql, parser -> parser.withSquareBracketQuotation(true));
            if (statement instanceof Select) {
                // 获取查询体（旧版本 API）
                Select selectStatement = (Select) statement;
                SelectBody selectBody = selectStatement.getSelectBody();

                if (selectBody instanceof PlainSelect) {
                    // 普通 SELECT：解析新条件表达式并与原 WHERE 合并
                    PlainSelect plainSelect = (PlainSelect) selectBody;
                    Expression conditionExpression = CCJSqlParserUtil.parseCondExpression(maskedCondition);
                    Expression whereExpression = plainSelect.getWhere();
                    if (whereExpression != null) {
                        // 已有 WHERE：AND 合并
                        plainSelect.setWhere(new AndExpression(whereExpression, conditionExpression));
                    } else {
                        // 无 WHERE：直接设置
                        plainSelect.setWhere(conditionExpression);
                    }
                    sql =  SqlParserUtils.restoreMyBatisPlaceholders(selectBody.toString(), mbMap);
                } else if (selectBody instanceof SetOperationList) {
                    // 复合查询（UNION/INTERSECT 等）：外包一层并追加 WHERE
                    SetOperationList setOperationList = (SetOperationList) selectBody;
                    PlainSelect newOuterSelect = new PlainSelect();
                    SubSelect subSelect = new SubSelect();
                    // 子查询体与别名
                    subSelect.setSelectBody(setOperationList);
                    subSelect.setAlias(new Alias("tmp_query"));
                    // 外层 FROM 使用子查询，并 SELECT *
                    newOuterSelect.setFromItem(subSelect);
                    newOuterSelect.setSelectItems(Collections.singletonList(new AllColumns()));
                    // 外层 WHERE 设置新条件
                    Expression conditionExpression = CCJSqlParserUtil.parseCondExpression(maskedCondition);
                    newOuterSelect.setWhere(conditionExpression);
                    // 生成最终 SQL 字符串
                    Select newSelect = new Select();
                    newSelect.setSelectBody(newOuterSelect);
                    sql = SqlParserUtils.restoreMyBatisPlaceholders(newSelect.toString(), mbMap);
                }
                // 非复合场景：返回修改后的 SQL（还原占位符）
                sql = SqlParserUtils.restoreMyBatisPlaceholders(selectStatement.toString(), mbMap);
            }
            //---------------------------------------------------------------------------------------------
            // 如果包含mybatis变量，恢复占位符
            sql = SqlParserUtils.restoreMyBatisPlaceholders(sql, sqlMbMap);
            //---------------------------------------------------------------------------------------------
            // 非 SELECT：原样返回
            return sql;
        } catch (JSQLParserException e) {
            throw new RuntimeException("SQL 解析失败，无法添加 WHERE 条件", e);
        }
    }

    /**
     * 为 SQL 增加 where 条件（字段+值+操作符）
     *
     * @param sql      原始SQL
     * @param field    字段名
     * @param value    字段值
     * @param operator 操作符（=, <>, >, <, like, in 等）
     * @author chenrui
     * @date 2025/8/14 19:03
     */
    public static String addWhereCondition(String sql, String field, Object value, String operator) {
        // 参数校验：必要字段缺失直接返回
        if (sql == null || field == null || field.trim().isEmpty() || operator == null || operator.trim().isEmpty()) {
            return sql;
        }
        // 值中如包含 MyBatis 占位符，则使用哨兵字面量参与解析，稍后还原
        boolean hasMbValue = false;
        String mbToken = null;
        String mbValueRaw = null;
        if (value instanceof String) {
            String vs = (String) value;
            Matcher m = SqlParserUtils.MB_PLACEHOLDER.matcher(vs);
            if (m.find()) {
                hasMbValue = true;
                mbValueRaw = vs;
                mbToken = SqlParserUtils.MB_PREFIX + 0 + SqlParserUtils.MB_SUFFIX;
            }
        }
        try {
            // 解析 SQL 为抽象语法树（支持方括号转义）
            Statement statement = CCJSqlParserUtil.parse(sql, parser -> parser.withSquareBracketQuotation(true));
            if (statement instanceof Select) {
                // 获取查询体（旧版本 API）
                Select selectStatement = (Select) statement;
                SelectBody selectBody = selectStatement.getSelectBody();

                if (selectBody instanceof PlainSelect) {
                    // 普通 SELECT：构造列和值表达式与比较表达式
                    PlainSelect plainSelect = (PlainSelect) selectBody;
                    Column column = new Column(field);
                    Expression valueExpression = hasMbValue ? new StringValue("'" + mbToken + "'") : createValueExpression(value);
                    Expression conditionExpression = createComparisonExpression(column, valueExpression, operator);
                    // 合并 WHERE 条件
                    Expression whereExpression = plainSelect.getWhere();
                    if (whereExpression != null) {
                        plainSelect.setWhere(new AndExpression(whereExpression, conditionExpression));
                    } else {
                        plainSelect.setWhere(conditionExpression);
                    }
                } else if (selectBody instanceof SetOperationList) {
                    // 复合查询：外包一层并构造 WHERE 条件
                    SetOperationList setOperationList = (SetOperationList) selectBody;
                    PlainSelect newOuterSelect = new PlainSelect();
                    SubSelect subSelect = new SubSelect();
                    subSelect.setSelectBody(setOperationList);
                    subSelect.setAlias(new Alias("tmp_query"));
                    newOuterSelect.setFromItem(subSelect);
                    newOuterSelect.setSelectItems(Collections.singletonList(new AllColumns()));
                    // 构造列、值、比较表达式并设置到 WHERE
                    Column column = new Column(field);
                    Expression valueExpression = hasMbValue ? new StringValue("'" + mbToken + "'") : createValueExpression(value);
                    Expression conditionExpression = createComparisonExpression(column, valueExpression, operator);
                    newOuterSelect.setWhere(conditionExpression);
                    // 返回包装后的 SQL
                    Select newSelect = new Select();
                    newSelect.setSelectBody(newOuterSelect);
                    String out = newSelect.toString();
                    return hasMbValue ? out.replace("'" + mbToken + "'", mbValueRaw) : out;
                }
                // 非复合场景：返回修改后的 SQL（如有占位符值，做还原）
                String out = selectStatement.toString();
                return hasMbValue ? out.replace("'" + mbToken + "'", mbValueRaw) : out;
            }
            // 非 SELECT：原样返回
            return sql;
        } catch (JSQLParserException e) {
            throw new RuntimeException("SQL 解析失败，无法添加 WHERE 条件", e);
        }
    }

    /**
     * 创建值表达式
     *
     * @param value 入参值
     * @author chenrui
     * @date 2025/8/14 19:03
     */
    private static Expression createValueExpression(Object value) {
        // 空值
        if (value == null) {
            return new NullValue();
        } else if (value instanceof Number) {
            // 数值类型：区分整数与小数
            if (value instanceof Integer || value instanceof Long) {
                return new LongValue(((Number) value).longValue());
            } else {
                return new DoubleValue(value.toString());
            }
        } else if (value instanceof Boolean) {
            // 布尔类型：转成字符串字面量
            boolean boolValue = (Boolean) value;
            return new StringValue("'" + boolValue + "'");
        } else {
            // 其他字符串：转义单引号
            String strValue = value.toString().replace("'", "''");
            return new StringValue("'" + strValue + "'");
        }
    }

    /**
     * 创建比较表达式
     *
     * @param left     左表达式
     * @param right    右表达式
     * @param operator 操作符
     * @author chenrui
     * @date 2025/8/14 19:03
     */
    private static Expression createComparisonExpression(Expression left, Expression right, String operator) {
        // 统一操作符大小写
        operator = operator.trim().toLowerCase();
        switch (operator) {
            case "=":
                return new EqualsTo(left, right);
            case ">":
                // 大于
                GreaterThan gt = new GreaterThan();
                gt.setLeftExpression(left);
                gt.setRightExpression(right);
                return gt;
            case ">=":
                // 大于等于
                GreaterThanEquals gte = new GreaterThanEquals();
                gte.setLeftExpression(left);
                gte.setRightExpression(right);
                return gte;
            case "<":
                // 小于
                MinorThan lt = new MinorThan();
                lt.setLeftExpression(left);
                lt.setRightExpression(right);
                return lt;
            case "<=":
                // 小于等于
                MinorThanEquals lte = new MinorThanEquals();
                lte.setLeftExpression(left);
                lte.setRightExpression(right);
                return lte;
            case "!=":
            case "<>":
                // 不等于
                return new NotEqualsTo(left, right);
            case "like":
                // 模糊匹配
                LikeExpression like = new LikeExpression();
                like.setLeftExpression(left);
                like.setRightExpression(right);
                return like;
            case "not like":
                // 取反模糊匹配
                LikeExpression notLike = new LikeExpression();
                notLike.setLeftExpression(left);
                notLike.setRightExpression(right);
                notLike.setNot(true);
                return notLike;
            case "in":
                // IN 列表（字符串形式）
                if (right instanceof StringValue) {
                    String inValues = right.toString().replace("'", "");
                    String[] values = inValues.split(",");
                    ExpressionList expressionList = new ExpressionList();
                    List<Expression> expressions = new ArrayList<>();
                    for (String value : values) {
                        expressions.add(new StringValue("'" + value.trim() + "'"));
                    }
                    expressionList.setExpressions(expressions);
                    InExpression inExpression = new InExpression();
                    inExpression.setLeftExpression(left);
                    inExpression.setRightItemsList(expressionList);
                    return inExpression;
                }
                // 其他情况退化为等号
                return new EqualsTo(left, right);
            default:
                // 默认：等于
                return new EqualsTo(left, right);
        }
    }

    /**
     * 构造字符串条件
     *
     * @param field    字段名
     * @param value    值
     * @param operator 操作符
     * @author chenrui
     * @date 2025/8/14 19:03
     */
    private static String buildCondition(String field, Object value, String operator) {
        // 拼接基础结构：field operator value
        StringBuilder condition = new StringBuilder();
        condition.append(field).append(" ").append(operator).append(" ");

        if (value == null) {
            // NULL 值
            condition.append("NULL");
        } else if (value instanceof Number || value instanceof Boolean) {
            // 数字/布尔直接拼接
            condition.append(value);
        } else {
            // 字符串值需要转义
            String strValue = value.toString().replace("'", "''");
            condition.append("'").append(strValue).append("'");
        }
        return condition.toString();
    }
}
