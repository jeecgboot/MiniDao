//package org.jeecgframework.minidao.sqlparser.impl;
//
//import net.sf.jsqlparser.JSQLParserException;
//import net.sf.jsqlparser.expression.*;
//import net.sf.jsqlparser.parser.CCJSqlParserUtil;
//import net.sf.jsqlparser.parser.SimpleNode;
//import net.sf.jsqlparser.schema.Column;
//import net.sf.jsqlparser.schema.Table;
//import net.sf.jsqlparser.statement.Statement;
//import net.sf.jsqlparser.statement.select.*;
//import org.jeecgframework.minidao.pojo.MiniDaoPage;
//import org.jeecgframework.minidao.sqlparser.AbstractSqlProcessor;
//import org.jeecgframework.minidao.sqlparser.impl.util.*;
//import org.jeecgframework.minidao.sqlparser.impl.vo.QueryTable;
//import org.jeecgframework.minidao.sqlparser.impl.vo.SelectSqlInfo;
//
//import java.util.*;
//
///**
// * jsqlparser解析SQL
// *
// * @author zhang
// */
//public class JsqlparserSqlProcessor implements AbstractSqlProcessor {
//    protected static JSqlCountSqlParser jsqlCountSqlParser = new JSqlCountSqlParser();
//    protected static JSqlServerPagesHelper jsqlServerPagesHelper = new JSqlServerPagesHelper();
//    protected static JSqlRemoveSqlOrderBy jsqlRemoveSqlOrderBy = new JSqlRemoveSqlOrderBy();
//    
//    @Override
//    public String getSqlServerPageSql(String sql, MiniDaoPage miniDaoPage) {
//        int page = miniDaoPage.getPage();
//        int rows = miniDaoPage.getRows();
//        int beginNum = (page - 1) * rows;
//        String pageSql = jsqlServerPagesHelper.convertToPageSql(sql);
//        pageSql = pageSql.replace(String.valueOf(Long.MIN_VALUE), String.valueOf(beginNum));
//        pageSql = pageSql.replace(String.valueOf(Long.MAX_VALUE), String.valueOf(rows));
//        return pageSql;
//    }
//
//    @Override
//    public String getCountSql(String sql) {
//        return jsqlCountSqlParser.getSmartCountSql(sql);
//    }
//
//    /**
//     * 去除排序SQL片段
//     *
//     * @param sql
//     * @return
//     */
//    @Override
//    public String removeOrderBy(String sql) {
//        if (sql == null) {
//            return null;
//        }
//        try {
//            sql = jsqlRemoveSqlOrderBy.removeOrderBy(sql);
//        } catch (JSQLParserException e) {
//            throw new RuntimeException(e);
//        }
//        return sql;
//    }
//
//
//    /**
//     * 解析SQL查询字段
//     *
//     * @param parsedSql
//     * @return
//     */
//    @Override
//    public List<Map<String, Object>> parseSqlFields(String parsedSql) {
//        List<Map<String, Object>> list = new ArrayList<>();
//        Select select = null;
//        try {
//            //update-begin---author:wangshuai ---date:20220215  for：[issues/I4STNJ]SQL Server表名关键字查询失败
//            select = (Select) CCJSqlParserUtil.parse(parsedSql, parser -> parser.withSquareBracketQuotation(true));
//            //update-end---author:wangshuai ---date:20220215  for：[issues/I4STNJ]SQL Server表名关键字查询失败
//        } catch (JSQLParserException jsqlParserException) {
//            jsqlParserException.printStackTrace();
//        }
//        PlainSelect plain;
//        // 处理union的情况
//        SelectBody selectBody = select.getSelectBody();
//        if (selectBody instanceof SetOperationList) {
//            SetOperationList selectBodyList = (SetOperationList) selectBody;
//            List<SelectBody> selects = selectBodyList.getSelects();
//            for (int i = 0; i < selects.size(); i++) {
//                plain = (PlainSelect) selects.get(i);
//                // 获取字段名的集合
//                List<String> tableAndColumns = getTableAndColumns(plain);
//                // 将list循环放到map中(key和value均是字段名)
//                getMapFiled(list, tableAndColumns);
//            }
//        }
//        if (selectBody instanceof PlainSelect) {
//            plain = (PlainSelect) selectBody;
//            List<String> tableAndColumns = getTableAndColumns(plain);
//            getMapFiled(list, tableAndColumns);
//        }
//        return list;
//    }
//
//    /**
//     * 在SQL的最外层增加或修改ORDER BY子句
//     * @for TV360X-2551
//     * @param sql 原始SQL
//     * @param field 新的ORDER BY字段
//     * @param isAsc 是否正序
//     * @return
//     * @author chenrui
//     * @date 2024/9/27 17:25
//     */
//    @Override
//    public String addOrderBy(String sql, String field, boolean isAsc) {
//        Statement statement = null;
//        //---------------------------------------------------------------------------------------------
//        // 如果包含mybatis变量，先将其替换为占位符，避免解析时出错
//        Map<String, String> mbMap = new LinkedHashMap<>();
//        sql = SqlParserUtils.maskMyBatisPlaceholders(sql, mbMap);
//        //---------------------------------------------------------------------------------------------
//        try {
//            statement = CCJSqlParserUtil.parse(sql);
//        } catch (JSQLParserException e) {
//            throw new RuntimeException(e);
//        }
//
//        if(statement instanceof Select){
//            Select selectStatement = (Select) statement;
//            SelectBody selectBody = selectStatement.getSelectBody();
//            if (selectBody instanceof PlainSelect) {
//                PlainSelect plainSelect = (PlainSelect) selectBody;
//                List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
//                OrderByElement orderByElement = new OrderByElement();
//                orderByElement.setExpression(new Column(field)); // 你想要添加的字段
//                orderByElement.setAsc(isAsc); // 按正序排序
//                if (orderByElements != null && !orderByElements.isEmpty()) {
//                    // 如果已经有ORDER BY，则添加到现有的ORDER BY中
//                    long existsCount = orderByElements.stream().filter(orderByEl -> {
//                        Expression expression = orderByEl.getExpression();
//                        if (expression instanceof Column) {
//                            Column column = (Column) expression;
//                            return column.getColumnName().equalsIgnoreCase(field);
//                        } else {
//                            return false;
//                        }
//                    }).count();
//                    if (existsCount == 0) {
//                        orderByElements.add(orderByElement);
//                    }
//                } else {
//                    // 如果没有ORDER BY，则创建一个新的ORDER BY子句
//                    plainSelect.setOrderByElements(Collections.singletonList(orderByElement));
//                }
//                sql = plainSelect.toString();
//            }
//        }
//        //---------------------------------------------------------------------------------------------
//        // 如果包含mybatis变量，恢复占位符
//        sql = SqlParserUtils.restoreMyBatisPlaceholders(sql, mbMap);
//        //---------------------------------------------------------------------------------------------
//        return sql;
//    }
//
//    private static void getMapFiled(List<Map<String, Object>> list, List<String> tableAndColumns) {
//        Map<String, Object> map = new HashMap(5);
//        for (String str : tableAndColumns) {
//            //排除*不展示
//            if (!"*".equals(str)) {
//                map.put(str, str);
//            }
//        }
//        list.add(map);
//    }
//
//    private static List<String> getTableAndColumns(PlainSelect plain) {
//        // 获取select后面的语句
//        List<SelectItem> selectItems = plain.getSelectItems();
//        List<String> items = new ArrayList<>();
//        if (selectItems != null) {
//            for (SelectItem selectItem : selectItems) {
//                if (selectItem instanceof SelectExpressionItem) {
//                    SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;
//                    String columnName = "";
//                    Alias alias = selectExpressionItem.getAlias();
//                    Expression expression = selectExpressionItem.getExpression();
//                    if (expression instanceof CaseExpression) {
//                        // case表达式
//                        columnName = alias.getName();
//                    } else if (expression instanceof LongValue || expression instanceof StringValue
//                            || expression instanceof DateValue || expression instanceof DoubleValue) {
//                        // 值表达式
//                        columnName = Objects.nonNull(alias.getName()) ? alias.getName()
//                                : expression.getASTNode().jjtGetValue().toString();
//                    } else if (expression instanceof TimeKeyExpression) {
//                        // 日期
//                        columnName = alias.getName();
//                    } else {
//                        if (alias != null) {
//                            columnName = alias.getName();
//                        } else {
//                            SimpleNode node = expression.getASTNode();
//                            Object value = node.jjtGetValue();
//                            if (value instanceof Column) {
//                                columnName = ((Column) value).getColumnName();
//                            } else if (value instanceof Function) {
//                                columnName = value.toString();
//                            } else {
//                                // 增加对select 'aaa' from table; 的支持
//                                columnName = String.valueOf(value);
//                                columnName = columnName.replace("'", "");
//                                columnName = columnName.replace("\"", "");
//                                columnName = columnName.replace("`", "");
//                            }
//                        }
//                    }
//
//                    columnName = columnName.replace("'", "");
//                    columnName = columnName.replace("\"", "");
//                    columnName = columnName.replace("`", "");
//
//                    items.add(columnName);
//                } else if (selectItem instanceof AllTableColumns) {
//                    AllTableColumns allTableColumns = (AllTableColumns) selectItem;
//                    items.add(allTableColumns.toString());
//                } else {
//                    items.add(selectItem.toString());
//                }
//            }
//        }
//        return items;
//    }
//
//    @Override
//    public Map<String, SelectSqlInfo> parseAllSelectTable(String selectSql) throws JSQLParserException {
//        return JSqlParserSelectInfoUtil.parseAllSelectTable(selectSql);
//    }
//
//    @Override
//    public SelectSqlInfo parseSelectSqlInfo(String selectSql) throws JSQLParserException {
//        return JSqlParserSelectInfoUtil.parseSelectSqlInfo(selectSql);
//    }
//
//    @Override
//    public List<QueryTable> getQueryTableInfo(String sql) {
//        return JSqlTableInfoHelper.getQueryTableInfo(sql);
//    }
//
//}
