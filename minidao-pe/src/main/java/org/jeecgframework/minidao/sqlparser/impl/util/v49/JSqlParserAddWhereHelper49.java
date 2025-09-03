package org.jeecgframework.minidao.sqlparser.impl.util.v49;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.jeecgframework.minidao.sqlparser.impl.util.SqlParserUtils;

import java.util.*;
import java.util.regex.Matcher;

/**
 * addWhere 工具类（jsqlparser 4.9）
 * for [issues/8336]支持SqlServer数据使用sql排序，新方案。
 * @author chenrui
 * @date 2025/8/14 19:02
 */
public class JSqlParserAddWhereHelper49 {

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
        // 掩码占位符，避免 JSqlParser 解析时丢失 # 之后内容
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
                // 顶层为 Select 语句
                Select selectStmt = (Select) statement;

                if (selectStmt instanceof PlainSelect) {
                    // 普通 SELECT：解析新条件并与原 WHERE 合并
                    PlainSelect plainSelect = (PlainSelect) selectStmt;
                    Expression conditionExpression = CCJSqlParserUtil.parseCondExpression(maskedCondition);
                    Expression whereExpression = plainSelect.getWhere();
                    if (whereExpression != null) {
                        // 已有 WHERE：AND 合并
                        plainSelect.setWhere(new AndExpression(whereExpression, conditionExpression));
                    } else {
                        // 无 WHERE：直接设置
                        plainSelect.setWhere(conditionExpression);
                    }
                    sql =  SqlParserUtils.restoreMyBatisPlaceholders(selectStmt.toString(), mbMap);
                } else if (selectStmt instanceof SetOperationList) {
                    // 复合查询（UNION）：外包一层 SELECT * 并追加 WHERE
                    SetOperationList setOperationList = (SetOperationList) selectStmt;
                    PlainSelect outer = new PlainSelect();
                    ParenthesedSelect sub = new ParenthesedSelect().withSelect(setOperationList);
                    sub.setAlias(new Alias("tmp_query"));
                    outer.setFromItem(sub);
                    outer.addSelectItems(new AllColumns());
                    Expression conditionExpression = CCJSqlParserUtil.parseCondExpression(maskedCondition);
                    outer.setWhere(conditionExpression);
                    // 返回前还原占位符
                    sql =  SqlParserUtils.restoreMyBatisPlaceholders(outer.toString(), mbMap);
                } else if (statement instanceof ParenthesedSelect) {
                    // 顶层是括号查询：内部 普通查询 则直接合并 WHERE，否则外包一层
                    ParenthesedSelect ps = (ParenthesedSelect) statement;
                    if (ps.getPlainSelect() != null) {
                        // 内部为普通查询
                        PlainSelect inner = ps.getPlainSelect();
                        Expression conditionExpression = CCJSqlParserUtil.parseCondExpression(maskedCondition);
                        Expression whereExpression = inner.getWhere();
                        if (whereExpression != null) {
                            inner.setWhere(new AndExpression(whereExpression, conditionExpression));
                        } else {
                            inner.setWhere(conditionExpression);
                        }
                        // 返回前还原占位符
                        sql =  SqlParserUtils.restoreMyBatisPlaceholders(ps.toString(), mbMap);
                    } else {
                        // 内部为复合查询：外包一层 SELECT * 并追加 WHERE
                        PlainSelect outer = new PlainSelect();
                        if (ps.getAlias() == null) {
                            ps.setAlias(new Alias("tmp_query"));
                        }
                        outer.setFromItem(ps);
                        outer.addSelectItems(new AllColumns());
                        Expression conditionExpression = CCJSqlParserUtil.parseCondExpression(maskedCondition);
                        outer.setWhere(conditionExpression);
                        // 返回前还原占位符
                        sql =  SqlParserUtils.restoreMyBatisPlaceholders(outer.toString(), mbMap);
                    }
                }else {
                    sql =  SqlParserUtils.restoreMyBatisPlaceholders(selectStmt.toString(), mbMap);
                }
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
     * @param operator 操作符
     * @author chenrui
     * @date 2025/8/14 19:03
     */
    public static String addWhereCondition(String sql, String field, Object value, String operator) {
        // 参数校验：必要字段缺失直接返回
        if (sql == null || field == null || field.trim().isEmpty() || operator == null || operator.trim().isEmpty()) {
            return sql;
        }
        // 值中如包含 MyBatis 占位符，则使用占位参与解析，稍后还原
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
                // 顶层为 Select 语句
                Select selectStmt = (Select) statement;

                if (selectStmt instanceof PlainSelect) {
                    // 普通 SELECT：构造列、值与比较表达式，并合并 WHERE
                    PlainSelect plainSelect = (PlainSelect) selectStmt;
                    Column column = new Column(field);
                    Expression valueExpression = hasMbValue ? new StringValue("'" + mbToken + "'") : createValueExpression(value);
                    Expression conditionExpression = createComparisonExpression(column, valueExpression, operator);
                    Expression whereExpression = plainSelect.getWhere();
                    if (whereExpression != null) {
                        plainSelect.setWhere(new AndExpression(whereExpression, conditionExpression));
                    } else {
                        plainSelect.setWhere(conditionExpression);
                    }
                    String out = plainSelect.toString();
                    return hasMbValue ? out.replace("'" + mbToken + "'", mbValueRaw) : out;
                } else if (selectStmt instanceof SetOperationList) {
                    // 复合查询：外包一层 SELECT * 并在外层追加 WHERE
                    SetOperationList setOperationList = (SetOperationList) selectStmt;
                    PlainSelect outer = new PlainSelect();
                    ParenthesedSelect sub = new ParenthesedSelect().withSelect(setOperationList);
                    sub.setAlias(new Alias("tmp_query"));
                    outer.setFromItem(sub);
                    outer.addSelectItems(new AllColumns());
                    Column column = new Column(field);
                    Expression valueExpression = hasMbValue ? new StringValue("'" + mbToken + "'") : createValueExpression(value);
                    Expression conditionExpression = createComparisonExpression(column, valueExpression, operator);
                    outer.setWhere(conditionExpression);
                    String out = outer.toString();
                    return hasMbValue ? out.replace("'" + mbToken + "'", mbValueRaw) : out;
                } else if (statement instanceof ParenthesedSelect) {
                    // 顶层是括号查询：内部 普通查询 则直接合并 WHERE，否则外包一层
                    ParenthesedSelect ps = (ParenthesedSelect) statement;
                    if (ps.getPlainSelect() != null) {
                        PlainSelect inner = ps.getPlainSelect();
                        Column column = new Column(field);
                        Expression valueExpression = hasMbValue ? new StringValue("'" + mbToken + "'") : createValueExpression(value);
                        Expression conditionExpression = createComparisonExpression(column, valueExpression, operator);
                        Expression whereExpression = inner.getWhere();
                        if (whereExpression != null) {
                            inner.setWhere(new AndExpression(whereExpression, conditionExpression));
                        } else {
                            inner.setWhere(conditionExpression);
                        }
                        String out = ps.toString();
                        return hasMbValue ? out.replace("'" + mbToken + "'", mbValueRaw) : out;
                    } else {
                        PlainSelect outer = new PlainSelect();
                        if (ps.getAlias() == null) {
                            ps.setAlias(new Alias("tmp_query"));
                        }
                        outer.setFromItem(ps);
                        outer.addSelectItems(new AllColumns());
                        Column column = new Column(field);
                        Expression valueExpression = hasMbValue ? new StringValue("'" + mbToken + "'") : createValueExpression(value);
                        Expression conditionExpression = createComparisonExpression(column, valueExpression, operator);
                        outer.setWhere(conditionExpression);
                        String out = outer.toString();
                        return hasMbValue ? out.replace("'" + mbToken + "'", mbValueRaw) : out;
                    }
                }
                // 返回修改后的 SQL
                return selectStmt.toString();
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
        if (value == null) {
            return new NullValue();
        } else if (value instanceof Number) {
            if (value instanceof Integer || value instanceof Long) {
                return new LongValue(((Number) value).longValue());
            } else {
                return new DoubleValue(value.toString());
            }
        } else if (value instanceof Boolean) {
            boolean boolValue = (Boolean) value;
            return new StringValue("'" + boolValue + "'");
        } else {
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
        operator = operator.trim().toLowerCase();
        switch (operator) {
            case "=":
                EqualsTo equalsTo = new EqualsTo();
                equalsTo.setLeftExpression(left);
                equalsTo.setRightExpression(right);
                return equalsTo;
            case ">":
                GreaterThan gt = new GreaterThan();
                gt.setLeftExpression(left);
                gt.setRightExpression(right);
                return gt;
            case ">=":
                GreaterThanEquals gte = new GreaterThanEquals();
                gte.setLeftExpression(left);
                gte.setRightExpression(right);
                return gte;
            case "<":
                MinorThan lt = new MinorThan();
                lt.setLeftExpression(left);
                lt.setRightExpression(right);
                return lt;
            case "<=":
                MinorThanEquals lte = new MinorThanEquals();
                lte.setLeftExpression(left);
                lte.setRightExpression(right);
                return lte;
            case "!=":
            case "<>":
                NotEqualsTo notEqualsTo = new NotEqualsTo();
                notEqualsTo.setLeftExpression(left);
                notEqualsTo.setRightExpression(right);
                return notEqualsTo;
            case "like":
                LikeExpression like = new LikeExpression();
                like.setLeftExpression(left);
                like.setRightExpression(right);
                return like;
            case "not like":
                LikeExpression notLike = new LikeExpression();
                notLike.setLeftExpression(left);
                notLike.setRightExpression(right);
                notLike.setNot(true);
                return notLike;
            case "in":
                if (right instanceof StringValue) {
                    String inValues = right.toString().replace("'", "");
                    String[] values = inValues.split(",");
                    List<Expression> expressions = new ArrayList<>();
                    for (String value : values) {
                        expressions.add(new StringValue("'" + value.trim() + "'"));
                    }
                    InExpression inExpression = new InExpression();
                    inExpression.setLeftExpression(left);
                    inExpression.setRightExpression(new ExpressionList<>(expressions));
                    return inExpression;
                }
                EqualsTo fallbackEquals = new EqualsTo();
                fallbackEquals.setLeftExpression(left);
                fallbackEquals.setRightExpression(right);
                return fallbackEquals;
            default:
                EqualsTo defaultEquals = new EqualsTo();
                defaultEquals.setLeftExpression(left);
                defaultEquals.setRightExpression(right);
                return defaultEquals;
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
        StringBuilder condition = new StringBuilder();
        condition.append(field).append(" ").append(operator).append(" ");
        if (value == null) {
            condition.append("NULL");
        } else if (value instanceof Number || value instanceof Boolean) {
            condition.append(value);
        } else {
            String strValue = value.toString().replace("'", "''");
            condition.append("'").append(strValue).append("'");
        }
        return condition.toString();
    }
}
