/**
 * 简单字符串方式为 SQL 增加 where 条件（与 SimpleSqlProcessor 同步逻辑）
 * @author chenrui
 * @date 2025/8/14 19:02
 */
package org.jeecgframework.minidao.sqlparser.impl.util;


/**
 * 简单字符串方式为 SQL 增加 where 条件（与 SimpleSqlProcessor 同步逻辑）
 * for [issues/8336]支持SqlServer数据使用sql排序，新方案。
 * @author chenrui
 * @date 2025/8/14 19:02
 */
public class SimpleAddWhereHelper {

    /**
     * 为 SQL 增加 where 条件（条件字符串）
     * @param sql 原始SQL
     * @param condition 条件（不含where关键字）
     * @author chenrui
     * @date 2025/8/14 19:03
     */
    public static String addWhereCondition(String sql, String condition) {
        // 1) 判空与去除多余空白
        if (sql == null || condition == null || condition.trim().isEmpty()) {
            return sql;
        }
        sql = sql.trim();
        condition = condition.trim();

        // 2) 移除末尾分号，避免拼接后语法错误
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1).trim();
        }

        // 3) 在最外层查找是否已存在 WHERE 位置（忽略括号内）
        boolean hasWhere = false;
        int wherePos = -1;
        int nestedLevel = 0;
        String sqlLower = sql.toLowerCase();
        for (int i = 0; i < sqlLower.length() - 5; i++) {
            char c = sqlLower.charAt(i);
            if (c == '(') {
                nestedLevel++;
            } else if (c == ')') {
                nestedLevel--;
            } else if (nestedLevel == 0 && i + 5 < sqlLower.length()
                    && sqlLower.substring(i, i + 5).equals("where")
                    && (i == 0 || Character.isWhitespace(sqlLower.charAt(i - 1)))) {
                if (i + 5 == sqlLower.length() || Character.isWhitespace(sqlLower.charAt(i + 5))) {
                    hasWhere = true;
                    wherePos = i;
                    break;
                }
            }
        }

        // 4) 根据是否已有 WHERE，选择合并方式
        if (hasWhere) {
            // 4.1) 已有 WHERE：在 WHERE 条件尾部插入 AND 条件
            int endPos = findWhereConditionEnd(sql, wherePos + 5);
            return sql.substring(0, endPos) + " AND " + condition + sql.substring(endPos);
        } else {
            // 4.2) 无 WHERE：在合适位置（FROM 段之后或子句前）插入 WHERE 条件
            int insertPos = findWhereInsertPosition(sql);
            StringBuilder result = new StringBuilder();
            result.append(sql.substring(0, insertPos).trim());
            result.append(" WHERE ");
            result.append(condition);
            String remaining = sql.substring(insertPos).trim();
            if (!remaining.isEmpty()) {
                result.append(" ").append(remaining);
            }
            return result.toString();
        }
    }

    /**
     * 为 SQL 增加 where 条件（字段+值+操作符）
     * @param sql 原始SQL
     * @param field 字段名
     * @param value 字段值
     * @param operator 操作符
     * @author chenrui
     * @date 2025/8/14 19:03
     */
    public static String addWhereCondition(String sql, String field, Object value, String operator) {
        // 1) 判空：必要参数缺失直接返回
        if (sql == null || field == null || field.trim().isEmpty() || operator == null || operator.trim().isEmpty()) {
            return sql;
        }
        // 2) 先构造条件字符串
        String condition = buildCondition(field, value, operator);
        // 3) 复用字符串条件入口
        return addWhereCondition(sql, condition);
    }

    /**
     * 构造条件字符串
     * @param field 字段名
     * @param value 字段值
     * @param operator 操作符
     * @author chenrui
     * @date 2025/8/14 19:03
     */
    private static String buildCondition(String field, Object value, String operator) {
        // 1) 拼接基本结构：field operator value
        StringBuilder condition = new StringBuilder();
        condition.append(field).append(" ").append(operator).append(" ");
        // 2) 根据值类型拼接字面量
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

    // ================== 内部扫描工具 ==================

    /**
     * 定位 WHERE 条件结束位置
     * @param sql SQL 文本
     * @param startPos 起始扫描位置（紧随 WHERE 关键字）
     * @author chenrui
     * @date 2025/8/14 19:03
     */
    private static int findWhereConditionEnd(String sql, int startPos) {
        // 从 WHERE 后开始，寻找 GROUP BY/HAVING/ORDER BY/LIMIT 等子句边界
        int nestedLevel = 0;
        String sqlLower = sql.toLowerCase();
        for (int i = startPos; i < sqlLower.length(); i++) {
            char c = sqlLower.charAt(i);
            if (c == '(') {
                nestedLevel++;
            } else if (c == ')') {
                nestedLevel--;
            } else if (nestedLevel == 0) {
                for (String keyword : new String[]{"group by", "having", "order by", "limit"}) {
                    if (i + keyword.length() <= sqlLower.length()
                            && sqlLower.regionMatches(i, keyword, 0, keyword.length())
                            && (i == 0 || Character.isWhitespace(sqlLower.charAt(i - 1)))
                            && (i + keyword.length() == sqlLower.length()
                            || Character.isWhitespace(sqlLower.charAt(i + keyword.length())))) {
                        return i;
                    }
                }
            }
        }
        return sql.length();
    }

    /**
     * 计算 WHERE 插入位置
     * @param sql SQL 文本
     * @author chenrui
     * @date 2025/8/14 19:03
     */
    private static int findWhereInsertPosition(String sql) {
        // 1) 先定位 FROM 关键字结束位置（忽略括号内）
        int nestedLevel = 0;
        String sqlLower = sql.toLowerCase();
        int fromPos = -1;
        for (int i = 0; i < sqlLower.length() - 4; i++) {
            char c = sqlLower.charAt(i);
            if (c == '(') {
                nestedLevel++;
            } else if (c == ')') {
                nestedLevel--;
            } else if (nestedLevel == 0 && i + 4 < sqlLower.length()
                    && sqlLower.regionMatches(i, "from", 0, 4)
                    && (i == 0 || Character.isWhitespace(sqlLower.charAt(i - 1)))) {
                if (i + 4 == sqlLower.length() || Character.isWhitespace(sqlLower.charAt(i + 4))) {
                    fromPos = i + 4;
                    break;
                }
            }
        }
        // 2) 未找到 FROM：直接返回末尾
        if (fromPos == -1) {
            return sql.length();
        }
        // 3) 找到 FROM 后，扫描到 FROM 段结束（遇到 where/group/having/order/limit）
        int endPos = findFromClauseEnd(sql, fromPos);
        // 4) 在 FROM 段后继续寻找 group/having/order/limit 的出现位置作为插入点
        int clausePos = Integer.MAX_VALUE;
        nestedLevel = 0;
        for (int i = endPos; i < sqlLower.length(); i++) {
            char c = sqlLower.charAt(i);
            if (c == '(') {
                nestedLevel++;
            } else if (c == ')') {
                nestedLevel--;
            } else if (nestedLevel == 0) {
                for (String keyword : new String[]{"group by", "having", "order by", "limit"}) {
                    if (i + keyword.length() <= sqlLower.length()
                            && sqlLower.regionMatches(i, keyword, 0, keyword.length())
                            && (i == 0 || Character.isWhitespace(sqlLower.charAt(i - 1)))
                            && (i + keyword.length() == sqlLower.length()
                            || Character.isWhitespace(sqlLower.charAt(i + keyword.length())))) {
                        clausePos = i;
                        break;
                    }
                }
                if (clausePos != Integer.MAX_VALUE) {
                    break;
                }
            }
        }
        return clausePos != Integer.MAX_VALUE ? clausePos : sql.length();
    }

    /**
     * 定位 FROM 子句结束位置
     * @param sql SQL 文本
     * @param startPos 起始扫描位置（紧随 FROM 关键字）
     * @author chenrui
     * @date 2025/8/14 19:03
     */
    private static int findFromClauseEnd(String sql, int startPos) {
        // 从 FROM 后开始，直到遇到 where/group/having/order/limit（忽略括号内）
        int nestedLevel = 0;
        String sqlLower = sql.toLowerCase();
        for (int i = startPos; i < sqlLower.length(); i++) {
            char c = sqlLower.charAt(i);
            if (c == '(') {
                nestedLevel++;
            } else if (c == ')') {
                nestedLevel--;
            } else if (nestedLevel == 0) {
                for (String keyword : new String[]{"where", "group by", "having", "order by", "limit"}) {
                    if (i + keyword.length() <= sqlLower.length()
                            && sqlLower.regionMatches(i, keyword, 0, keyword.length())
                            && (i == 0 || Character.isWhitespace(sqlLower.charAt(i - 1)))
                            && (i + keyword.length() == sqlLower.length()
                            || Character.isWhitespace(sqlLower.charAt(i + keyword.length())))) {
                        return i;
                    }
                }
            }
        }
        return sql.length();
    }
}
